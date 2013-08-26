package oak.image;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by ericrichardson on 8/26/13.
 */
public class FadeInNetworkImageView extends NetworkImageView {

    private static final int FADE_IN_TIME_MS = 250;

    public FadeInNetworkImageView(Context context) {
        super(context);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                    new ColorDrawable(android.R.color.transparent),
                    new BitmapDrawable(getContext().getResources(), bm)
            });
            setImageDrawable(td);
            td.startTransition(FADE_IN_TIME_MS);
        } else {
            this.setAlpha(0.0f);
            setImageDrawable(new BitmapDrawable(getContext().getResources(), bm));
            ObjectAnimator.ofFloat(this, "alpha", 1f);
        }
    }
}
