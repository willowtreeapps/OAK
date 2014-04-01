package oak.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.FrameLayout;

import oak.R;

/**
 * Created by robcook on 3/31/14.
 *
 * An activity that hosts a fragment determined by class name.  Any
 * extras will be passed to the fragment as arguments.
 *
 * If you need to support older android devices (Pre-Honeycomb),
 * modify to extend SherlockFragmentActivity and replace getActionBar
 * with getSupportActionBar.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SupportFragmentHostActivity extends FragmentActivity {

    private static final String ACTION_BAR_TITLE_KEY = "FragmentHostActivity_ActionBarTitleKey";
    private static final String FRAGMENT_CLASS_NAME_KEY = "FragmentHostActivity_FragmentClassNameKey";

    private FrameLayout frameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_host);
        frameLayout = (FrameLayout)findViewById(R.id.oak_content);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(ACTION_BAR_TITLE_KEY)) {
            setActionBarTitle(extras.getString(ACTION_BAR_TITLE_KEY));
        }
        if (extras != null && extras.containsKey(FRAGMENT_CLASS_NAME_KEY)) {
            String fragmentTag = extras.getString(FRAGMENT_CLASS_NAME_KEY);

            if (getSupportFragmentManager().findFragmentByTag(fragmentTag) == null) {
                Fragment fragment = Fragment.instantiate(this, fragmentTag, null);
                Bundle bundle = new Bundle();
                bundle.putAll(extras);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.replace(R.id.oak_content, fragment, fragmentTag);
                fragmentTransaction.commit();
            }
        }
    }

    /**
     * The wrapper FrameLayout hosting the fragments.  Use this
     * to set properties on the wrapper layout.
     * @return
     */
    public FrameLayout getFrameLayout() {
        return frameLayout;
    }

    public void setActionBarTitle(int stringResId) {
        setActionBarTitle(getString(stringResId));
    }

    public void setActionBarTitle(String title) {
        getActionBar().setTitle(title);
    }

    public static Intent getIntent(Context context, String actionBarTitle,
                                   Class clazz, Bundle extras) {
        Intent intent = new Intent(context, SupportFragmentHostActivity.class);
        intent.putExtra(ACTION_BAR_TITLE_KEY, actionBarTitle);
        intent.putExtra(FRAGMENT_CLASS_NAME_KEY, clazz.getName());
        if (extras != null) {
            intent.putExtras(extras);
        }
        return intent;
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
