package oak.demo;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * User: derek Date: 11/7/12 Time: 11:23 AM
 */
public class OakDemoActivity extends RoboSherlockFragmentActivity {

    protected RelativeLayout fullLayout;
    protected FrameLayout actContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void setContentView(int layoutResId) {
        fullLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.oak_activity, null); // Your base layout here
        actContent = (FrameLayout) fullLayout.findViewById(R.id.content);
        getLayoutInflater().inflate(layoutResId, actContent, true);
        TextView attr = (TextView) fullLayout.findViewById(R.id.attribution);
        attr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.willowtreeapps.com"));
                startActivity(i);
            }
        });
        super.setContentView(fullLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
