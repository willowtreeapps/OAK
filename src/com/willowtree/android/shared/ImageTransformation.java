package com.willowtree.android.shared;

import android.graphics.Bitmap;

public interface ImageTransformation {
	Bitmap transform(Bitmap image);
	String fingerprint();
}
