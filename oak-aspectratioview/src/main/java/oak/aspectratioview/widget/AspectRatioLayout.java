package oak.aspectratioview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import oak.aspectratioview.util.RatioSizingUtils;


/**
 * User: evantatarka Date: 9/17/13 Time: 9:24 AM
 */
public class AspectRatioLayout extends FrameLayout {

    private RatioSizingUtils.RatioSizingInfo mRatioSizingInfo = new RatioSizingUtils.RatioSizingInfo();

    public AspectRatioLayout(Context context) {
        super(context);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mRatioSizingInfo = RatioSizingUtils.getRatioSizingInfoFromAttrs(context, attrs);
    }

    public void setAspectRatio(int width, int height) {
        mRatioSizingInfo.aspectRatioWidth = width;
        mRatioSizingInfo.aspectRatioHeight = height;
        requestLayout();
    }

    public void setAspectRatio(String ratioString) {
        mRatioSizingInfo = RatioSizingUtils.parseRatioSizingInfo(ratioString);
        requestLayout();
    }

    public long getAspectRatioWidth() {
        return mRatioSizingInfo.aspectRatioWidth;
    }

    public long getAspectRatioHeight() {
        return mRatioSizingInfo.aspectRatioHeight;
    }

    public float getAspectRatio() {
        return mRatioSizingInfo.aspectRatioWidth / (float) mRatioSizingInfo.aspectRatioHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();

        RatioSizingUtils.RatioMeasureInfo rmi = RatioSizingUtils
                .getMeasureInfo(widthMeasureSpec, heightMeasureSpec, mRatioSizingInfo, widthPadding, heightPadding);

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(rmi.width + widthPadding, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(rmi.height + heightPadding, MeasureSpec.EXACTLY));
    }
}

