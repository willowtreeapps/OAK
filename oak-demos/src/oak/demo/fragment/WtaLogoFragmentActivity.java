package oak.demo.fragment;

import android.os.Bundle;
import android.os.Handler;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.fragment.AnimatedWtaLogoFragment;

/**
 * User: derek Date: 2/13/14 Time: 10:28 AM
 */
public class WtaLogoFragmentActivity extends OakDemoActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wta_logo_fragment);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimatedWtaLogoFragment logoFragment = (AnimatedWtaLogoFragment)
                        getFragmentManager().findFragmentById(R.id.animated_logo_fragment);
                logoFragment.start();
            }
        }, 1000);
    }
}
