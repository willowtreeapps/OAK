package oak;

import oak.external.com.github.droidfu.cachefu.ImageCache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug.MemoryInfo;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public class OAKImageCache extends ImageCache {
	public static final String LOG_TAG = OAKImageCache.class.getSimpleName();
	
	private HashMap<String, Long> fileAges;
	private int cacheLimit = 8388608; // 8 MB
	volatile private int cacheAllocated = 0; // also in bytes
	private Context context;
	private int[]  pid;
	private ActivityManager am;
	private boolean safeMode;
	private int bytesPerPixel = 4; // RGBA8888

	public OAKImageCache(int initialCapacity, long expirationInMinutes,
			int maxConcurrentThreads) {
		
		super(initialCapacity, expirationInMinutes, maxConcurrentThreads);
		fileAges = new HashMap<String, Long>();
		//pid = new int[] {Process.myPid()};
		//am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	}
	
	@Override
	public synchronized Bitmap getBitmap(Object elementKey) {
        byte[] imageData = super.get(elementKey);
        if (imageData == null) {
            return null;
        }
        if (safeMode) {
        	int memPerProcess = am.getMemoryClass() * 1024 * 1024;
        	MemoryInfo mi = am.getProcessMemoryInfo(pid)[0];
        	int usedMem = mi.getTotalPrivateDirty() + mi.getTotalSharedDirty();
        	Log.d("OAKImageCache", "MemPerProcess: " + memPerProcess);
        	Log.d("OAKImageCache", "usedMem: " + usedMem);
        	Log.d("OAKImageCache", "imgSize: " + getImageArea(imageData) * bytesPerPixel);
        	if(usedMem + getImageArea(imageData) * bytesPerPixel > memPerProcess) {
        		return null;
        	}
        }
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }
	
	@Override
	public synchronized byte[] put(String imageUrl, byte[] data) {
		fileAges.put(imageUrl, (new Date().getTime()));
		while(cacheAllocated + data.length > cacheLimit) {
			deleteOldestImage();
		}
		byte[] result = super.put(imageUrl, data);
		return result;
	}
	
	public synchronized void putToDisk(String imageUrl, byte[] data) {
		fileAges.put(imageUrl,  (new Date().getTime()));
		while(cacheAllocated + data.length > cacheLimit) {
			deleteOldestImage();
		}
		super.cacheToDisk(imageUrl, data);
	}
	
	public int getCacheAllocated() {
		return cacheAllocated;
	}
	
	public int getCacheLimit() {
		return cacheLimit;
	}
	
	/**
	 * Set how large the cache may grow before old images are discarded.
	 * @param newLimit New cache size in bytes.
	 */
	public void setCacheLimit(int newLimit) {
		cacheLimit = newLimit;
	}

	/**
	 * Populates the cache with information about images already on disk. Should be run
	 * after enabling disk caching -- if not, previously existing images on disk will not be
	 * considered toward allocation limits and old image removal.
	 */
	public void updateContents() {
		String cacheDir = getDiskCacheDirectory();
		if (cacheDir != null) {
			File cachedFiles[] = new File(cacheDir).listFiles();
	        if(cachedFiles == null) {
	        	cacheAllocated = 0;
	        } else {
	        	for(File f : cachedFiles) {
	        		cacheAllocated += f.length();
	        		fileAges.put(f.getName(), f.lastModified());
	        	}
	        }
	        Log.i(LOG_TAG, "Allocated cache at startup: " + cacheAllocated + " bytes.");
		}
	}
	
	private void deleteOldestImage() {
		long oldestAge = 0;
		String oldestUrl = null;
		for(String url : fileAges.keySet()) {
			Long currAge = fileAges.get(url);
			if(currAge > oldestAge) {
				oldestAge = currAge;
				oldestUrl = url;
			}
		}
		if(oldestUrl != null) {
			fileAges.remove(oldestUrl); // remove from ages list
			cacheAllocated -= (new File(getFileNameForKey(oldestUrl))).length();
			this.remove(oldestUrl);  // ... and disk
		}
	}
	
	@Override
	protected void cacheToDisk(String key, byte[] value) {
		super.cacheToDisk(key, value);
	}
	
	protected int getImageArea(byte[] data) {
		if(data[0] == -1 && data[1] == -40) { //JPG
			try {
				return getJPEGArea(data);
			} catch(Exception e) {
				return -1;
			}
		}
		if(data[0] == -119 && data[1] == 80 && data[2] == 78 && data[3] == 71) { //PNG
			int idx = 0;
			// find IHDR
			for(int i = 0; i < data.length; i++) {
				if(data[i] == (byte)'I' && data[i+1] == (byte)'H' && data[i+2] == (byte)'D' && data[i+3] == (byte)'R') {
					idx = i;
					break;
				}
			}
			idx += 4;
			int width = (data[idx] << 24) + (data[idx+1] << 16) + (data[idx+2] << 8) + data[idx+3];
			int height = (data[idx+4] << 24) + (data[idx+5] << 16) + (data[idx+6] << 8) + data[idx+7];
			return width * height;
		}
		if(data[0] == 71 && data[1] == 73 && data[2] == 70 && data[3] == 56) { // GIF
			// Java doesn't have unsigned ints. Life is hard, sometimes.
			int wIdx = 6; 
			int hIdx = 8;
			int width = (((int)data[wIdx+1] & 0xFF) << 8) + ((int)data[wIdx] & 0xFF);
			int height = (((int)data[hIdx+1] & 0xFF) << 8) + data[hIdx];
			return width * height;
		}
		if(data[0] == 66 && data[1] == 77) { //BMP
			return data.length;
		}
		return -1;
	}
	
	private int getJPEGArea(byte[] data) throws IOException {
        InputStream bis = new ByteArrayInputStream(data);
        // check for SOI marker
        if (bis.read() != 255 || bis.read() != 216)
                throw new RuntimeException("SOI (Start Of Image) marker 0xff 0xd8 missing");

        while (bis.read() == 255) {
                int marker = bis.read();
                int len = bis.read() << 8 | bis.read();
                if (marker == 192) {
                        bis.skip(1);
                        int height = bis.read() << 8 | bis.read();
                        int width = bis.read() << 8 | bis.read();
                        bis.close();
                        return width * height;
                }
                bis.skip(len - 2);
        }
        bis.close();
        return -1;
}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public boolean isSafeMode() {
		return safeMode;
	}

	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}

}
