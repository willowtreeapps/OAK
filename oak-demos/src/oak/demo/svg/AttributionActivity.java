package oak.demo.svg;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import oak.demo.OakDemoActivity;
import oak.demo.R;

/**
 * User: derek Date: 2/13/14 Time: 3:34 PM
 */
public class AttributionActivity extends OakDemoActivity {

    private Handler mHandler = new Handler();

    private Button resetAnimationsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribution);

        resetAnimationsButton = (Button) findViewById(R.id.reset_animations);
        resetAnimationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimatedWtaLogoFragment logoFragment = (AnimatedWtaLogoFragment)
                        getSupportFragmentManager().findFragmentById(R.id.animated_logo_fragment);
                logoFragment.reset();
                logoFragment.start();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimatedWtaLogoFragment logoFragment = (AnimatedWtaLogoFragment)
                        getSupportFragmentManager().findFragmentById(R.id.animated_logo_fragment);
                logoFragment.start();
                //fullLogo.start();
            }
        }, 1000);
    }
}
