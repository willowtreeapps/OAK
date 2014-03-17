package oak.demo.svg;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.svg.AnimatedSvgView;

/**
 * User: derek Date: 2/13/14 Time: 3:34 PM
 */
public class FullLogoAttributionActivity extends OakDemoActivity {

    private Handler mHandler = new Handler();

    private Button resetAnimationsButton;
    private AnimatedSvgView fullLogo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_logo_attribution);

        resetAnimationsButton = (Button) findViewById(R.id.reset_animations);
        resetAnimationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullLogo.reset();
                fullLogo.start();
            }
        });
        fullLogo = (AnimatedSvgView) findViewById(R.id.full_logo);
        fullLogo.setGlyphStrings(WtaLogoPaths.FULL_LOGO_GLYPHS);
        fullLogo.setFillPaints(
                new int[]{255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255},
                new int[]{136, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65},
                new int[]{194, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65},
                new int[]{200, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65, 65});
        int tc = Color.argb(255, 0, 0, 0);
        int[] traceColors = new int[18];
        int rc = Color.argb(50, 0, 0, 0);
        int[] residueColors = new int[18];
        for (int i = 0; i < traceColors.length; i++) {
            traceColors[i] = tc;
            residueColors[i] = rc;
        }
        fullLogo.setTraceColors(traceColors);
        fullLogo.setTraceResidueColors(residueColors);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fullLogo.start();
            }
        }, 1000);
    }
}
