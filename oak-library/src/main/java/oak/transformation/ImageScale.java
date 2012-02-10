package oak.transformation;

import android.graphics.Bitmap;

import oak.ImageTransformation;

/**
 * User: mlake Date: 12/9/11 Time: 5:54 PM
 */
public class ImageScale implements ImageTransformation {

    private int mMaxWidth;
    private int mMaxHeight;

    private String mFingerPrint;


    /**
     * Scale the image proportionally to the bounds of the given max width and height.
     * This works great when you're trying to use really large images for thumbnails. Rather than
     * caching the full image, this transformation will be cached instead, making load times much
     * faster.
     *
     * @param maxWidth
     * @param maxHeight
     */

    public ImageScale(int maxWidth, int maxHeight) {
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mFingerPrint = ImageScale.class.getSimpleName() + maxWidth + "-" + maxHeight;
    }

    @Override
    public Bitmap transform(Bitmap image) {

        int resultWidth = 0;
        int resultHeight = 0;

        if (image.getWidth() >= image.getHeight() && image.getWidth() > mMaxWidth) {
            //we're wider than we are taller
            resultWidth = mMaxWidth;
            float ratio = (float) mMaxWidth / (float) image.getWidth();
            resultHeight = Math.round(ratio * image.getHeight());
        } else if (image.getHeight() > image.getWidth() && image.getHeight() > mMaxHeight) {
            //we're wider than we are taller
            resultHeight = mMaxHeight;
            float ratio = (float) mMaxHeight / (float) image.getHeight();
            resultWidth = Math.round(ratio * image.getWidth());
        }

        return Bitmap.createScaledBitmap(image, resultWidth, resultHeight, true);
    }

    @Override
    public String fingerprint() {
        return mFingerPrint;
    }
}
