package com.willowtree.android.shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.github.droidfu.cachefu.ImageCache;
import com.github.droidfu.imageloader.ImageLoader;

import javax.net.ssl.SSLException;


public class OAKImageLoader extends ImageLoader implements Runnable {

	private static final String LOG_TAG = OAKImageLoader.class.getSimpleName();

	private String imageUrl;
	private String printedUrl; // imageUrl with transformation fingerprints prepended
	private OAKImageLoaderHandler handler;
	private ImageTransformation[] transformations;
	private static OAKImageCache imageCache;

	public static final int NO_DISK_CACHING = 0;
	public static final int INTERNAL_CACHING = 1;
	public static final int SD_CACHING = 2;
	public static final int PREFER_INTERNAL = 3;
	public static final int PREFER_SD = 4;

	private static Drawable defaultLoading = null;
	private static Drawable defaultError = null;
	public static boolean spinLoading = false;

	/**
	 * This method must be called before any other method is invoked on this class.
	 *
	 * @param context
	 *            the current context
	 * @param cacheType
	 * 			  What kind of disk caching, if any, should be used.<br>
	 *			NO_DISK_CACHING: Use memory only.<br>
	 *			INTERNAL_CACHING: Use device's internal memory.<br>
	 *			SD_CACHING: Attempt to use an SD card for caching but use only memory if one isn't present.<br>
	 *			PREFER_INTERNAL: Try to use internal memory, then fall back on SD, then fall back on memory only.<br>
	 *			PREFER_SD: Try to use SD, then fall back on internal, then fall back on memory only.<br>
	 */
	public static synchronized void initialize(Context context, int cacheType) {
		if (executor == null) {
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
		}
		if (imageCache == null) {
			imageCache = new OAKImageCache(25, expirationInMinutes, DEFAULT_POOL_SIZE);
			switch(cacheType) {
			case NO_DISK_CACHING:
				break;
			case INTERNAL_CACHING:
				imageCache.enableDiskCache(context, ImageCache.DISK_CACHE_INTERNAL);
				break;
			case SD_CACHING:
				imageCache.enableDiskCache(context, ImageCache.DISK_CACHE_SDCARD);
				break;
			case PREFER_INTERNAL:
				if(!imageCache.enableDiskCache(context, ImageCache.DISK_CACHE_INTERNAL))
					imageCache.enableDiskCache(context, ImageCache.DISK_CACHE_SDCARD);
				break;
			case PREFER_SD:
				if(!imageCache.enableDiskCache(context, ImageCache.DISK_CACHE_SDCARD))
				imageCache.enableDiskCache(context, ImageCache.DISK_CACHE_INTERNAL);
				break;
			default:
				break;
			}
			imageCache.updateContents();
			Log.d("OAKImageLoader", "Caching to " + imageCache.getDiskCacheDirectory());
		}

	}

	private OAKImageLoader(String imageUrl, String printedUrl, OAKImageLoaderHandler handler, ImageTransformation ... transformations) {
		super(imageUrl, handler);
		this.imageUrl = imageUrl;
		this.handler = handler;
		this.printedUrl = printedUrl;
		this.transformations = transformations;
	}


	public static void start(String imageUrl, OAKImageLoaderHandler handler) {
		start(imageUrl, handler.getImageView(), handler, null, null, new ImageTransformation[]{});
	}

	public static void start(String imageUrl, OAKImageLoaderHandler handler, ImageTransformation ... transformations) {
		start(imageUrl, handler.getImageView(), handler, null, null, transformations);
	}

	public static void start(String imageUrl, ImageView imageView, Drawable dummyDrawable,
			Drawable errorDrawable) {
		start(imageUrl, imageView, new OAKImageLoaderHandler(imageView, imageUrl), dummyDrawable,
				errorDrawable, new ImageTransformation[]{});
	}

	public static void start(String imageUrl, ImageView imageView, Drawable dummyDrawable,
			Drawable errorDrawable, ImageTransformation ... transformations) {
		start(imageUrl, imageView, new OAKImageLoaderHandler(imageView, imageUrl), dummyDrawable,
				errorDrawable, transformations);
	}

	public static void start(String imageUrl, ImageView imageView) {
		start(imageUrl, imageView, new OAKImageLoaderHandler(imageView, imageUrl), null, null, new ImageTransformation[]{});
	}

	public static void start(String imageUrl, ImageView imageView, ImageTransformation ... transformations) {
		start(imageUrl, imageView, new OAKImageLoaderHandler(imageView, imageUrl), null, null, transformations);
	}

	public static void start(String imageUrl, OAKImageLoaderHandler handler, Drawable dummyDrawable,
			Drawable errorDrawable, ImageTransformation ... transformations) {
		start(imageUrl, handler.getImageView(), handler, dummyDrawable, errorDrawable, transformations);
	}

	public static void start(String imageUrl, OAKImageLoaderHandler handler, Drawable dummyDrawable,
			Drawable errorDrawable) {
		start(imageUrl, handler.getImageView(), handler, dummyDrawable, errorDrawable,
				new ImageTransformation[]{});
	}

	protected static void start(String imageUrl, ImageView imageView, OAKImageLoaderHandler handler,
			Drawable dummyDrawable, Drawable errorDrawable, ImageTransformation ... transformations) {

		// No handler => No callback, do a single image pre-cache.
		// preCache() *should* be called directly but this is a
		// squirrelly way to get to it.
		if (handler == null) {
			preCache(new String[] {imageUrl}, transformations);
			return;
		}

		dummyDrawable = (dummyDrawable == null)?defaultLoading:dummyDrawable;
		errorDrawable = (errorDrawable == null)?defaultError:errorDrawable;

		String printedUrl = getPrintedUrl(imageUrl, transformations);

		if (imageView != null) {
			if (imageUrl == null) {
				// In a ListView views are reused, so we must be sure to remove the tag that could
				// have been set to the ImageView to prevent that the wrong image is set.
				imageView.setTag(null);
				setLoading(imageView, dummyDrawable);
				return;
			}
			String oldImageUrl = (String) imageView.getTag();
			if (printedUrl.equals(oldImageUrl)) {
				// nothing to do
				return;
			} else {
				// Set the dummy image while waiting for the actual image to be downloaded.
				setLoading(imageView, dummyDrawable);
				imageView.setTag(printedUrl);
			}
		}

		if (imageCache.containsKeyInMemory(printedUrl)) {
			// do not go through message passing, handle directly instead
			handler.setPrintedUrl(printedUrl);
			handler.handleImageLoaded(imageCache.getBitmap(printedUrl), null);
		} else {
			executor.execute(new OAKImageLoader(imageUrl, printedUrl, handler, transformations));
		}
	}

	public static String getPrintedUrl(String originalUrl, ImageTransformation ... transformations) {
		if(transformations.length == 0)
			return originalUrl;
		String printedUrl = originalUrl;
		for(ImageTransformation trans : transformations) {
			printedUrl = trans.fingerprint() + printedUrl;
		}
		return printedUrl;
	}

	/**
	 * Downloads images in urls array and applies transformations to each. Transformed images
	 * are then written to disk cache for fast retrieval later with ImageLoader.start().
	 * @param urls
	 * @param transformations
	 */
	public static void preCache(String[] urls, ImageTransformation ... transformations) {
		for(String url : urls) {
			String printedUrl = getPrintedUrl(url, transformations);
			executor.execute(new OAKImageLoader(url, printedUrl, null, transformations));
		}
	}

	@Override
	public void run() {
		// TODO: if we had a way to check for in-memory hits, we could improve performance by
		// fetching an image from the in-memory cache on the main thread
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		// If the handler is null, there's no callback which means we're doing
		// a pre-cache. We should just download the image, write it to disk,
		// and *not* inflate a bitmap.
		if (handler == null) {
			downloadImage(true);
			return;
		}

		Bitmap bitmap = imageCache.getBitmap(this.printedUrl);

		if(bitmap == null) {
			bitmap = downloadImage(false);
		}

		// TODO: gracefully handle this case.
		notifyImageLoaded(this.printedUrl, bitmap);
	}

	@Override
	public void notifyImageLoaded(String url, Bitmap bitmap) {
		handler.setPrintedUrl(this.printedUrl);
		Message message = new Message();
		message.what = HANDLER_MESSAGE_ID;
		Bundle data = new Bundle();
		data.putString(IMAGE_URL_EXTRA, url);
		Bitmap image = bitmap;
		data.putParcelable(BITMAP_EXTRA, image);
		message.setData(data);

		handler.sendMessage(message);
	}

	// TODO: we could probably improve performance by re-using connections instead of closing them
	// after each and every download
	/**
	 *
	 * @param toDiskOnly
	 * @return the generated bitmap if toDiskOnly == true, otherwise null
	 */
	protected Bitmap downloadImage(boolean toDiskOnly) {
		int timesTried = 1;
		Bitmap image = null;

		while (timesTried <= numRetries) {
			try {

				byte[] imageData = retrieveImageData();

				if (imageData != null) {
					if (transformations.length == 0) {
						if(toDiskOnly) {
							imageCache.putToDisk(imageUrl, imageData);
						} else {
							imageCache.put(imageUrl, imageData);
						}
					} else {
						// TODO: something more efficient?
								Bitmap bm = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
								for(ImageTransformation trans : transformations) {
									bm = trans.transform(bm);
								}
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								bm.compress(CompressFormat.JPEG, 75, bos);
								bm.recycle();
								imageData = bos.toByteArray();
								if(toDiskOnly) {
									imageCache.putToDisk(printedUrl, imageData);
								} else {
									imageCache.put(printedUrl, imageData);
								}
					}
				} else {
					break;
				}

				if(!toDiskOnly) {
					image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
				}
				break;

			} catch (Throwable e) {
				Log.w(LOG_TAG, "download for " + imageUrl + " failed (attempt " + timesTried + ")");
				e.printStackTrace();
				SystemClock.sleep(DEFAULT_RETRY_HANDLER_SLEEP_TIME);
				timesTried++;
			}
		}

		return image;
	}



	private HttpClient getHttpClient() {
        //return new DefaultHttpClient();

        DefaultHttpClient client = new DefaultHttpClient();
    SSLSocketFactory sslSocketFactory = (SSLSocketFactory) client
            .getConnectionManager().getSchemeRegistry().getScheme("https")
            .getSocketFactory();
    final X509HostnameVerifier delegate = sslSocketFactory.getHostnameVerifier();
    if(!(delegate instanceof MyVerifier)) {
        sslSocketFactory.setHostnameVerifier(new MyVerifier(delegate));
    }
    return client;
	}

	/**
	 * Uses a BufferedHttpEntity to write to a byte array,
	 * to ensure that the complete data is loaded.
	 * New version 8/11/11 by cceckman to try to fix 2.3 issues.
	 */
	protected byte[] retrieveImageData() throws IOException {

		HttpGet req = new HttpGet(imageUrl);



		HttpResponse resp = (HttpResponse)getHttpClient().execute(req);

		BufferedHttpEntity bufResponse = new BufferedHttpEntity(resp.getEntity());//buffer the response before it comes back...

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bufResponse.writeTo(baos);
		return baos.toByteArray();

	}

	public static void clearCache() {
		imageCache.clear();
	}

	public static Drawable getDefaultLoading() {
		return defaultLoading;
	}

	/**
	 * Sets the default "dummy" drawable- to use while loading.
	 * @param defaultLoading
	 */
	public static void setDefaultLoading(Drawable defaultLoading) {
		OAKImageLoader.defaultLoading = defaultLoading;
	}

	public static Drawable getDefaultError() {
		return defaultError;
	}

	/**
	 * Sets the default error drawable- to use when there was an error in loading the image.
	 * @param defaultError the Drawable of the default error image.
	 */
	public static void setDefaultError(Drawable defaultError) {
		OAKImageLoader.defaultError = defaultError;
	}

	/**
	 * Gets a "spinning" animation to use with a loading dialog.
	 * Rotates at 1HZ and does not stop.
	 */
	public static void setSpinning(View v){
		RotateAnimation a = new RotateAnimation(0f, 360f, Animation.ABSOLUTE, v.getWidth()/2, Animation.ABSOLUTE, v.getHeight()/2);
		a.setInterpolator(new LinearInterpolator());
		a.setRepeatCount(Animation.INFINITE);
		a.setDuration(1);
		a.setStartTime(AnimationUtils.currentAnimationTimeMillis());

		v.setAnimation(a);
	}

	/**
	 * Sets an image in the loading state.
	 * @param v
	 * @param loading
	 */
	public static void setLoading(ImageView v, Drawable loading){
		if(loading != null) {
			v.setImageDrawable(loading);
			if(spinLoading){
				setSpinning(v);
			}
		}
		v.setVisibility(View.VISIBLE);
	}


    class MyVerifier extends AbstractVerifier {

        private final X509HostnameVerifier delegate;

        public MyVerifier(final X509HostnameVerifier delegate) {
            this.delegate = delegate;
        }


        public void verify(String host, String[] cns, String[] subjectAlts)
                throws SSLException {
            // code
//            boolean ok = false;
//            try {
//                delegate.verify(host, cns, subjectAlts);
//            } catch (SSLException e) {
//                for (String cn : cns) {
//                    if (cn.startsWith("*.")) {
//                        try {
//                            delegate.verify(host, new String[]{
//                                    cn.substring(2)}, subjectAlts);
//                            ok = true;
//                        } catch (Exception e1) {
//                        }
//                    }
//                }
//                if (!ok) throw e;
//            }
        }
    }

}
