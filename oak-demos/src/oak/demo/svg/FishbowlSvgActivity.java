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
public class FishbowlSvgActivity extends OakDemoActivity {

    private Handler mHandler = new Handler();

    private Button resetAnimationsButton;
    private AnimatedSvgView fullLogo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fishbowl_svg);

        resetAnimationsButton = (Button) findViewById(R.id.reset_animations);
        resetAnimationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullLogo.reset();
                fullLogo.start();
            }
        });
        fullLogo = (AnimatedSvgView) findViewById(R.id.full_logo);
        fullLogo.setGlyphStrings(FishbowlLogoPaths.FISHBOWL_GLYPHS);
        fullLogo.setFillPaints(
                new int[]{255, 255, 255, 255, 255, 0, 255, 255},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{178, 178, 178, 178, 178, 178, 178, 178},
                new int[]{238, 238, 238, 238, 238, 238, 238, 238});
        int traceColor = Color.argb(255, 0, 0, 0);
        fullLogo.setTraceColors(
                new int[]{traceColor, traceColor, traceColor, traceColor, traceColor, traceColor, traceColor,
                        traceColor});
        int residueColor = Color.argb(50, 0, 0, 0);
        fullLogo.setTraceResidueColors(
                new int[]{residueColor, residueColor, residueColor, residueColor, residueColor, residueColor,
                        residueColor, residueColor});
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
