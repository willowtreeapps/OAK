package oak.demo.verticalpager;


import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;

import oak.demo.R;

/**
 * Created by ericrichardson on 3/4/14.
 */
public class HorizonTransform implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way 'off-screen' to the bottom.
            view.findViewById(R.id.color).setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            //Fade Out
            view.findViewById(R.id.color).setAlpha(1 + position);

            // Animate down over the "horizon"
            view.setTranslationY(pageWidth * -(position + position));

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            view.findViewById(R.id.color).setAlpha(1 - position);

            // Counteract the default slide transition.
            view.setTranslationY(pageWidth * -position);

            // Scale the page up basd on position
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 + Math.abs(position * 2));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // This page is way 'off-screen' to the bottom.
            view.findViewById(R.id.color).setAlpha(0);
        }
    }
}
