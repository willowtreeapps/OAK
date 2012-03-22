package ${package};

import android.graphics.PixelFormat;
import android.view.Window;

import roboguice.activity.RoboFragmentActivity;

public abstract class AbstractActivity extends RoboFragmentActivity {

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        // Eliminates color banding
        window.setFormat(PixelFormat.RGBA_8888);

    }
}