package oak.demo.fragmenthostactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import oak.activity.SupportFragmentHostActivity;
import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.demo.verticalpager.ColorFragment;
import roboguice.inject.InjectView;

/**
 * Created by robcook on 3/31/14.
 */
public class SupportFragmentHostDemoActivity extends OakDemoActivity {

    @InjectView(R.id.frag_host_btn_1)
    Button btnOne;

    @InjectView(R.id.frag_host_btn_2)
    Button btnTwo;

    @InjectView(R.id.frag_host_bundle_text)
    EditText textToPass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host_demo);

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fragHostIntent = SupportFragmentHostActivity.getIntent(
                        SupportFragmentHostDemoActivity.this, "Colors",
                        ColorFragment.class, null);

                startActivity(fragHostIntent);
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = ShowTextSupportFragment.getArguments(textToPass.getText().toString());
                Intent fragHostIntent = SupportFragmentHostActivity.getIntent(
                        SupportFragmentHostDemoActivity.this, "Passed Text",
                        ShowTextSupportFragment.class, args);
                startActivity(fragHostIntent);
            }
        });
    }
}
