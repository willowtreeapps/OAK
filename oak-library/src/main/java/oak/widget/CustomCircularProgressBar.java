package oak.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import oak.R;

/**
 * User: derek Date: 5/25/12 Time: 1:06 PM
 */
public class CustomCircularProgressBar extends ProgressBar {

    Context c;
    public RotateAnimation rotate;

    public CustomCircularProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public CustomCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context c, AttributeSet attrs) {
        this.c = c;
        this.setIndeterminate(true);
        rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(1750);
        rotate.setRepeatCount(Animation.INFINITE);

        if (attrs != null) {
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.CustomCircularProgressBar);
            int resourceId;
            if (typedArray != null) {
                resourceId = typedArray.getResourceId(R.styleable.CustomCircularProgressBar_progressBarDrawable, -1);
                typedArray.recycle();
                if (resourceId != -1) {
                    Drawable d = c.getResources()
                            .getDrawable(resourceId);
                    this.setIndeterminateDrawable(d);
                }
            }

        }
        this.setAnimation(rotate);

        rotate.start();
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            this.clearAnimation();
        } else if (visibility == View.VISIBLE) {
            this.setAnimation(rotate);
            rotate.start();
        }
        super.setVisibility(visibility);
    }
}
