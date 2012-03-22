package ${package};

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

public class StartupActivity extends AbstractActivity {

    @InjectView(R.id.startup_text) TextView mStartupText;
    @InjectResource(R.color.custom_blue) int mCustomBlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainApp.TAG, "onCreate");
        setContentView(R.layout.startup);
        mStartupText.setTextColor(mCustomBlue);
    }

}

