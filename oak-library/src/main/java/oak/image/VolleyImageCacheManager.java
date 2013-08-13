package oak.image;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by ericrichardson on 8/13/13.
 */
public class VolleyImageCacheManager {

    /**
     * Volley recommends in-memory L1 cache but both a disk and memory cache are provided.
     * Volley includes a L2 disk cache out of the box but you can technically use a disk cache as an L1 cache provided
     * you can live with potential i/o blocking.
     */
    public enum CacheType {
        DISK, MEMORY, DUAL
    }

    private static VolleyImageCacheManager mInstance;

    /**
     * Volley image loader
     */
    private ImageLoader mImageLoader;

    /**
     * Image cache implementation
     */
    private ImageLoader.ImageCache mImageCache;

    private ImageLoader.ImageCache mSecondCache;

    /**
     * @return instance of the cache manager
     */
    public static VolleyImageCacheManager getInstance() {
        if (mInstance == null)
            mInstance = new VolleyImageCacheManager();

        return mInstance;
    }

    /**
     * Initializer for the manager. Must be called prior to use.
     *
     * @param context        application context
     * @param uniqueName     name for the cache location
     * @param cacheSize      max size for the cache
     * @param compressFormat file type compression format.
     * @param quality
     */
    public void init(Context context, String uniqueName, int cacheSize, Bitmap.CompressFormat compressFormat, int quality, CacheType type) {
        switch (type) {
            case DISK:
                mImageCache = new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
                break;
            case MEMORY:
                mImageCache = new BitmapLruImageCache(cacheSize);
                break;
            case DUAL:
                mImageCache = new BitmapLruImageCache(cacheSize);
                mSecondCache = new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
            default:
                mImageCache = new BitmapLruImageCache(cacheSize);
                break;
        }

        mImageLoader = new ImageLoader(RequestManager.getRequestQueue(), mImageCache);
    }

    public Bitmap getBitmap(String url) {
        try {
            Bitmap bm = mImageCache.getBitmap(createKey(url));
            if (bm == null && mSecondCache != null) {
                bm = mSecondCache.getBitmap(createKey(url));
            }
            return bm;
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }

    public void putBitmap(String url, Bitmap bitmap) {
        try {
            mImageCache.putBitmap(createKey(url), bitmap);
            if (mSecondCache != null) {
                mSecondCache.putBitmap(createKey(url), bitmap);
            }
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }


    /**
     * Executes and image load
     *
     * @param url      location of image
     * @param listener Listener for completion
     */
    public void getImage(String url, ImageLoader.ImageListener listener) {
        mImageLoader.get(url, listener);
    }

    /**
     * @return instance of the image loader
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Creates a unique cache key based on a url value
     *
     * @param url url to be used in key creation
     * @return cache key value
     */
    private String createKey(String url) {
        return String.valueOf(url.hashCode());
    }


}


