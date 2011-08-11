package com.willowtree.android.shared;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Bug fix for Android bug #6066:
 * http://code.google.com/p/android/issues/detail?id=6066
 * @author cceckman
 *
 */
public class FlushedInputStream extends BufferedInputStream {
	public FlushedInputStream(InputStream inputStream) {
        super(inputStream);
    }

    public long skip(long n) throws IOException {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped < n) {
            long bytesSkipped = in.skip(n - totalBytesSkipped);
            if (bytesSkipped == 0L) {
                  int aByte = read();
                  if (aByte < 0) {
                      break;  // we reached EOF
                  } else {
                      bytesSkipped = 1; // we read one byte
                  }
           }
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}
