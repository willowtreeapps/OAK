package ${package};

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;

public class StartupActivity extends RoboSherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainApp.TAG, "onCreate");
        setContentView(R.layout.startup_tabs);

        ActionBar bar = getSupportActionBar();

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        ActionBar.Tab firstTab = bar.newTab().setText("Tab A")
                .setTabListener(new TabListener<TabA>(this, "Tab A", TabA.class));
        bar.addTab(firstTab);
        bar.addTab(bar.newTab().setText("Tab B")
                .setTabListener(new TabListener<TabB>(this, "Tab B", TabB.class)));
        bar.addTab(bar.newTab().setText("Tab C")
                .setTabListener(new TabListener<TabC>(this, "Tab C", TabC.class)));
        bar.selectTab(firstTab);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = this.getWindow();

        // Eliminates color banding
        window.setFormat(PixelFormat.RGBA_8888);
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {

        private final SherlockFragmentActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            T preInitializedFragment = (T) mActivity.getSupportFragmentManager().findFragmentByTag(mTag);

            // Check if the fragment is already initialized
            if (mFragment == null && preInitializedFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else if (mFragment != null) {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            } else if (preInitializedFragment != null) {
                ft.attach(preInitializedFragment);
                mFragment = preInitializedFragment;
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }
}

