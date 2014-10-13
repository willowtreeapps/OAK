package oak.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Allows you to create a dialog with a loading animation
 * using your own resources.
 */
public class CustomProgressDialog extends Dialog {

    private ImageView mProgressImageView;
    private final int mAnimationDuration = 0;

    public CustomProgressDialog(Context context, int[] progressDrawables) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressImageView = new ImageView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgressImageView.setLayoutParams(lp);
        mProgressImageView.setImageResource(progressDrawables[0]);
        setContentView(mProgressImageView);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        new SceneAnimation(mProgressImageView, progressDrawables, mAnimationDuration);
    }
}
