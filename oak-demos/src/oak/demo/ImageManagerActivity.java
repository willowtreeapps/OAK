/*
 * Copyright (c) 2011. WillowTree Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.demo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oak.OAKImageLoader;
import oak.OAKImageLoaderHandler;

public class ImageManagerActivity extends ListActivity {

    private List<PhotoItem> photoItemList;

    private Handler droidFuHandler;

    private Map<String, Bitmap> thumbnailCache = new HashMap<String, Bitmap>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MEMINFO", "HEAP SIZE: " + Long.toString(Debug.getNativeHeapSize()));
        this.photoItemList = new ArrayList<PhotoItem>();
        initializeData();
        OAKImageLoader.initialize(this, OAKImageLoader.PREFER_SD);
        OAKImageLoader.clearCache();
        droidFuHandler = new Handler();

        ListAdapter adapter = new PhotoItemListAdapter(photoItemList, this);
        getListView().setAdapter(adapter);
    }

    private class PhotoItemListAdapter extends BaseAdapter {

        private List<PhotoItem> photoItemList;

        private Context context;

        public PhotoItemListAdapter(List<PhotoItem> photoItemList, Context context) {
            this.photoItemList = photoItemList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return photoItemList.size();
        }

        @Override
        public PhotoItem getItem(int position) {
            return photoItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View res = convertView;

            final PhotoItem photoItem = photoItemList.get(position);

            if (res == null) {
                res = (LinearLayout) getLayoutInflater().inflate(R.layout.photo_list_item, parent, false);
            }

            final ImageView imageView = (ImageView) res.findViewById(R.id.item_image);

            if (thumbnailCache.containsKey(photoItem.getURL()) && thumbnailCache.get(photoItem.getURL()) != null) {
                imageView.setImageBitmap(thumbnailCache.get(photoItem.getURL()));
            } else {
                droidFuHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        OAKImageLoader.start(photoItem.getURL(),
                                new OAKImageLoaderHandler(imageView, photoItem.getURL()) {

                                    public Bitmap transform(Bitmap image) {
                                        // Scales the image down to 100x100, draws a
                                        // green dot in the middle
                                        // to make it apparent transform is occurring
                                        image = Bitmap.createScaledBitmap(image, 100, 100, true);
                                        Canvas canvas = new Canvas();
                                        Paint paint = new Paint();
                                        paint.setColor(0xFF00FF00);
                                        paint.setStrokeWidth(0.1f);
                                        canvas.setBitmap(image);
                                        canvas.drawCircle(image.getWidth() / 2,
                                                image.getHeight() / 2, 5.0f, paint);
                                        return image;
                                    }

                                    public String fingerprint() {
                                        return "scaledot";
                                    }
                                });
                    }
                });
            }

            TextView tv = (TextView) res.findViewById(R.id.item_text);
            tv.setText(photoItem.getTitle());

            return res;
        } // End getView

    } // End PhotoItemListAdapter

    private void initializeData() {
        // http://mobile.virginia.edu/parsers/writewip.php
        String resp = getResponse();
        ArrayList<String> attributeList = new ArrayList<String>();
        attributeList.add("title");
        attributeList.add("link");
        attributeList.add("description");
        attributeList.add("pubdate");
        setData("item", attributeList, resp);
    }

    /**
     * Temporary ad-hoc XML parser Scans the HTML String to find attributes requested
     *
     * @param mainTag      The main tag in HTML. Usually the 'item' tag.
     * @param attributes A list of requested attributes within the main tag.
     */
    public void setData(String mainTag, ArrayList<String> attributes, String HTML) {
        String startMainTag = "<" + mainTag + ">";
        String stopMainTag = "</" + mainTag + ">";
        Handler mHandler = new Handler();
        int mainCursor = 0;

        // Loop while it continues to find the main tag from the current cursor.
        while (HTML.indexOf(startMainTag, mainCursor) != -1) {
            final Map<String, String> dataMap = new HashMap<String, String>();

            int startMainIndex = HTML.indexOf(startMainTag, mainCursor);
            int stopMainIndex = HTML.indexOf(stopMainTag, mainCursor);

            String HTMLScope = HTML.substring(startMainIndex + startMainTag.length(), stopMainIndex);

            for (String attribute : attributes) {
                String startAttributeTag = "<" + attribute + ">";
                String stopAttributeTag = "</" + attribute + ">";

                // Ensure attribute is well ordered
                if (HTMLScope.indexOf(startAttributeTag) < HTMLScope.indexOf(stopAttributeTag)) {
                    int startAttributeIndex = HTMLScope.indexOf(startAttributeTag);
                    int stopAttributeIndex = HTMLScope.indexOf(stopAttributeTag);

                    String attributeValue = HTMLScope.substring(startAttributeIndex + startAttributeTag.length(),
                            stopAttributeIndex);
                    attributeValue = attributeValue.replace("&amp;", "&").replace("&apos;", "\'");
                    dataMap.put(attribute, attributeValue);
                }
            }

            PhotoItem pi = new PhotoItem();
            pi.setUrl(dataMap.get("link"));
            pi.setTitle(dataMap.get("title"));
            pi.setDescription(dataMap.get("description"));
            this.photoItemList.add(pi);

            mainCursor = stopMainIndex + 1;
        } // end while

    }

    public static String getResponse() {
        String response = "";
        try {
            HttpClient client = new DefaultHttpClient();
            String getURL = "http://mobile.virginia.edu/parsers/writewip.php";
            HttpGet get = new HttpGet(getURL);
            HttpResponse responseGet = client.execute(get);
            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                response = EntityUtils.toString(resEntityGet);
            } else {
                Log.d("UVA WEB", "ERR: resEntityGet is null");
            }
        } catch (Exception e) {
            Log.d("UVA WEB", "ERR: could not connect " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}
