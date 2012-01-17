package oak.transformation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import oak.ImageTransformation;

/**
 * User: mlake Date: 12/9/11 Time: 5:09 PM
 */
public class ImageBorder implements ImageTransformation {


    private int mColor;
    private int mBorder;
    private String mFingerPrint;

    /**
     * Add a border to an image with the width and color specified.
     * (This is handy when you need to scale images AND draw a border around them.)
     *
     *
     * @param borderPixels
     * @param color
     */

    public ImageBorder(int borderPixels, int color) {
        mBorder = borderPixels;
        mColor = color;
        
        mFingerPrint = ImageBorder.class.getSimpleName()
                + borderPixels + color;
                
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap mutableBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + (mBorder * 2),
                bitmap.getHeight() + (mBorder * 2),
                bitmap.getConfig());

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(mColor);
        canvas.drawPaint(paint);
        canvas.drawBitmap(bitmap, mBorder, mBorder, paint);
        return mutableBitmap;
    }

    @Override
    public String fingerprint() {
        return mFingerPrint;
    }
}
