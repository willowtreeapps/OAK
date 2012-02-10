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

package oak;

import oak.external.com.github.droidfu.imageloader.ImageLoaderHandler;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.widget.ImageView;


public class OAKImageLoaderHandler extends ImageLoaderHandler {
	
	
	protected String printedUrl;
	
	public OAKImageLoaderHandler(ImageView imageView, String imageUrl) {
		super(imageView, imageUrl);
	}
	
	public OAKImageLoaderHandler(ImageView imageView, String imageUrl,
			Drawable errorDrawable) {
		super(imageView, imageUrl, errorDrawable);
	}
	
	public void setPrintedUrl(String printedUrl) {
		this.printedUrl = printedUrl;
	}
	
	public String getPrintedUrl() {
		return printedUrl;
	}
	
	/**
     * Override this method if you need custom handler logic. Note that this method can actually be
     * called directly for performance reasons, in which case the message will be null
     * 
     * @param bitmap
     *            the bitmap returned from the image loader
     * @param msg
     *            the handler message; can be null
     * @return true if the view was updated with the new image, false if it was discarded
     */
	@Override
    public boolean handleImageLoaded(Bitmap bitmap, Message msg) {
        // If this handler is used for loading images in a ListAdapter,
        // the thread will set the image only if it's the right position,
        // otherwise it won't do anything.
        String forUrl = (String) getImageView().getTag();
        if (printedUrl.equals(forUrl)) {
            Bitmap image = bitmap != null || getErrorDrawable() == null ? bitmap
                    : ((BitmapDrawable) getErrorDrawable()).getBitmap();
            if (image != null) {
                getImageView().setImageBitmap(image);
            }

            if(OAKImageLoader.spinLoading){
            	getImageView().clearAnimation();//clear the loading 
            }
            return true;
        }

        return false;
    }

}
