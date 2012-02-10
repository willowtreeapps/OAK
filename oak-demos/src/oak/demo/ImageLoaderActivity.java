/*
 * Copyright (c) 2011. WillowTree Apps, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.demo;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import oak.OAK;
import oak.OAKImageLoader;
import oak.transformation.ImageBorder;
import oak.transformation.ImageScale;

public class ImageLoaderActivity extends Activity {

    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/%s?s=512";

    private List<PhotoItem> photoItemList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_loader_demo);
        Log.d(OAK.LOGTAG, "HEAP SIZE: " + Long.toString(Debug.getNativeHeapSize()));
        this.photoItemList = new ArrayList<PhotoItem>();

        OAKImageLoader.initialize(this, OAKImageLoader.PREFER_SD);

        Resources res = getResources();
        String[] names = res.getStringArray(R.array.names);
        String[] emails = res.getStringArray(R.array.emails);
        photoItemList = new ArrayList<PhotoItem>();
        for (int i = 0; i < names.length; i++) {
            PhotoItem photoItem = new PhotoItem();
            photoItem.setTitle(names[i]);
            photoItem.setUrl(String.format(GRAVATAR_URL, md5Hex(emails[i] + "@willowtreeapps.com")));
            Log.d(OAK.LOGTAG, "Gravatar URL: " + photoItem.getURL());
            photoItemList.add(photoItem);
        }
        ListAdapter adapter = new PhotoItemListAdapter(photoItemList);

        ListView listView = (ListView) findViewById(R.id.image_loader_lv);
        listView.setAdapter(adapter);
    }


    private class PhotoItemListAdapter extends BaseAdapter {

        private List<PhotoItem> photoItemList;

        public PhotoItemListAdapter(List<PhotoItem> photoItemList) {
            this.photoItemList = photoItemList;
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
                res = getLayoutInflater()
                        .inflate(R.layout.photo_list_item, parent, false);
            }

            final ImageView imageView = (ImageView) res.findViewById(R.id.item_image);

            OAKImageLoader.start(
                    photoItem.getURL(),
                    imageView,
                    getResources().getDrawable(R.drawable.icon),
                    null,
                    new ImageScale(100, 50),
                    new ImageBorder(3, 0xffcccccc)
            );

            TextView tv = (TextView) res.findViewById(R.id.item_text);
            tv.setText(photoItem.getTitle());

            return res;
        }

    }

    public void clearCacheClicked(View view) {
        OAKImageLoader.clearCache();
        Toast.makeText(this, "Cache cleared."
                + " Exit this activity and return to reload the images", 4000).show();
    }

    public static String hex(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i]
                    & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String md5Hex(String message) {
        try {
            MessageDigest md =
                    MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}
