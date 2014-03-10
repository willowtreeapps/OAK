package oak.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import oak.R;

/**
 * User: evantatarka Date: 9/17/13 Time: 9:24 AM
 */
public class AspectRatioLayout extends FrameLayout {
    private long mAspectRatioWidth = 1;
    private long mAspectRatioHeight = 1;

    public AspectRatioLayout(Context context) {
        super(context);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout);

            parseAspectRatio(a.getString(R.styleable.AspectRatioLayout_oakAspectRatio));

            a.recycle();
        }
    }

    public void setAspectRatio(int width, int height) {
        mAspectRatioWidth = width;
        mAspectRatioHeight = height;
        requestLayout();
    }

    public void setAspectRatio(String ratio) {
        parseAspectRatio(ratio);
        requestLayout();
    }

    public long getAspectRatioWidth() {
        return mAspectRatioWidth;
    }

    public long getAspectRatioHeight() {
        return mAspectRatioHeight;
    }

    public float getAspectRatio() {
        return mAspectRatioWidth / (float) mAspectRatioHeight;
    }

    private void parseAspectRatio(String ratio) {
        if (ratio == null) {
            mAspectRatioWidth = 1;
            mAspectRatioHeight = 1;
            return;
        }

        String[] parts = ratio.split("[x:]");
        if (parts.length == 2) {
            mAspectRatioWidth = Integer.parseInt(parts[0]);
            mAspectRatioHeight = Integer.parseInt(parts[1]);
        } else {
            throw new IllegalArgumentException("Invalid ratio: " + ratio);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();

        int width = MeasureSpec.getSize(widthMeasureSpec) - widthPadding;
        int height = MeasureSpec.getSize(heightMeasureSpec) - heightPadding;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (height <= 0 && width <= 0 && heightMode == MeasureSpec.UNSPECIFIED && widthMode == MeasureSpec.UNSPECIFIED) {
            width = 0;
            height = 0;
        } else if (height <= 0 && heightMode == MeasureSpec.UNSPECIFIED) {
            height = (int) (width * mAspectRatioHeight / mAspectRatioWidth);
        } else if (width <= 0 && widthMode == MeasureSpec.UNSPECIFIED) {
            width = (int) (height * mAspectRatioWidth / mAspectRatioHeight);
        } else if (width * mAspectRatioHeight > mAspectRatioWidth * height) {
            width = (int) (height * mAspectRatioWidth / mAspectRatioHeight);
        } else {
            height = (int) (width * mAspectRatioHeight / mAspectRatioWidth);
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width + widthPadding, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height + heightPadding, MeasureSpec.EXACTLY));
    }
}

