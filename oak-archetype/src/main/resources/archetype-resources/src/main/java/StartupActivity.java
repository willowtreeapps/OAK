package ${package};

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import roboguice.inject.InjectView;

public class StartupActivity extends RoboSherlockFragmentActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
    @InjectView(R.id.pager)ViewPager pager;
    ActionBar bar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainApp.TAG, "onCreate");
        setContentView(R.layout.startup_tabs);

        bar = getSupportActionBar();
        bar.addTab(bar.newTab().setText("Tab A").setTabListener(this).setTag(TabA.class.getName()));
        bar.addTab(bar.newTab().setText("Tab B").setTabListener(this).setTag(TabB.class.getName()));
        bar.addTab(bar.newTab().setText("Tab C").setTabListener(this).setTag(TabC.class.getName()));
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        bar.setSelectedNavigationItem(0);
        pager.setAdapter(new OakAdapter(getSupportFragmentManager()));
        pager.setOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    @Override
    public void onPageSelected(int i) {
        bar.setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    private class OakAdapter extends FragmentPagerAdapter{

        public OakAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return Fragment.instantiate(StartupActivity.this, bar.getTabAt(i).getTag().toString(), null);
        }

        @Override
        public int getCount() {
            return bar.getTabCount();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = this.getWindow();

        // Eliminates color banding
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

}

