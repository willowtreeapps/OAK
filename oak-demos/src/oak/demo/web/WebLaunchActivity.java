package oak.demo.web;

import android.os.Bundle;
import android.view.View;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.web.WebViewActivity;

/**
 * Created by ericrichardson on 3/19/14.
 */
public class WebLaunchActivity extends OakDemoActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_launch);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchWebActivity();
            }
        });
    }

    private void launchWebActivity() {
        WebViewActivity.startWebActivity(this, "http://willowtreeapps.com", R.layout.fragment_webview);
    }

}