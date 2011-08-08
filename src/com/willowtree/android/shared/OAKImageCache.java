package com.willowtree.android.shared;

import java.io.File;
import java.util.HashMap;
import java.util.Date;


import android.util.Log;

import com.github.droidfu.cachefu.ImageCache;


public class OAKImageCache extends ImageCache {
	public static final String LOG_TAG = OAKImageCache.class.getSimpleName();
	
	private HashMap<String, Long> fileAges;
	private int cacheLimit = 4194304; // 4 MB
	volatile private int cacheAllocated = 0; // also in bytes

	public OAKImageCache(int initialCapacity, long expirationInMinutes,
			int maxConcurrentThreads) {
		
		super(initialCapacity, expirationInMinutes, maxConcurrentThreads);
		fileAges = new HashMap<String, Long>();
		
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

}
