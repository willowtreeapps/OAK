package oak.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import oak.R;

/**
 * Created by ericrichardson on 1/9/14.
 */
public class AspectRatioLayout extends FrameLayout {
    private float mAspectRatio = 1;

    public AspectRatioLayout(Context context) {
        super(context);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout);
            if (typedArray != null) {
                mAspectRatio = typedArray.getFloat(R.styleable.AspectRatioLayout_aspectRatio, 1f);
                typedArray.recycle();
            }

        }
    }

    public void setAspectRatio(float ratio) {
        mAspectRatio = ratio;
        requestLayout();
    }

    public float getAspectRatio() {
        return mAspectRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        if (height == 0 && heightMode == View.MeasureSpec.UNSPECIFIED) {
            height = Integer.MAX_VALUE;
            //heightMode = MeasureSpec.EXACTLY;
        }

        if (width == 0 && widthMode == View.MeasureSpec.UNSPECIFIED) {
            width = Integer.MAX_VALUE;
            //widthMode = MeasureSpec.EXACTLY;
        }

        float requestedRatio = width / (float) height;

        if (requestedRatio > mAspectRatio) {
            super.onMeasure(
                    View.MeasureSpec.makeMeasureSpec((int) (height * mAspectRatio), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec((int) (width / mAspectRatio), View.MeasureSpec.EXACTLY));
        }
    }
}
