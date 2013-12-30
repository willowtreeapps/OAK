package oak.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import oak.OAK;

/**
 * User: tylerromeo Date: 10/16/13 Time: 1:05 PM
 *
 * LinearLayout that has a height that is dependant on its width
 * the ratio is determined by the provided width and height values
 *
 */
public class RatioLinearLayout extends LinearLayout {

    private int width;
    private int height;
    private static final int DEFAULT_WIDTH = 1;
    private static final int DEFAULT_HEIGHT = 1;

    public RatioLinearLayout(Context context){
        super(context);
        initialize(null);
    }

    public RatioLinearLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        initialize(attrs);
    }

    @TargetApi(11)
    public RatioLinearLayout(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initialize(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int calculatedHeight = parentWidth / width * height;
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY));
    }

    private void initialize(AttributeSet attrs) {
        if (attrs == null) {
            // default to 1x1 square
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
        } else {
            width = attrs.getAttributeIntValue(OAK.XMLNS, "ratioWidth", DEFAULT_WIDTH);
            height = attrs.getAttributeIntValue(OAK.XMLNS, "ratioHeight", DEFAULT_HEIGHT);
        }
    }
}
