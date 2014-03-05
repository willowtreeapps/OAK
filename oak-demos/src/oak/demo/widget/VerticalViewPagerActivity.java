package oak.demo.widget;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Random;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.demo.verticalpager.ColorFragment;
import oak.demo.verticalpager.HorizonTransform;
import oak.widget.VerticalViewPager;

/**
 * Created by ericrichardson on 3/5/14.
 */
public class VerticalViewPagerActivity extends OakDemoActivity {
    VerticalViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vertical_viewpager_demo);
        pager = (VerticalViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ExampleAdapter(getSupportFragmentManager()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            pager.setPageTransformer(true, new HorizonTransform());
        }
    }

    private class ExampleAdapter extends FragmentPagerAdapter {

        public ExampleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ColorFragment.newInstance(ColorFragment.colors[new Random().nextInt(ColorFragment.colors.length - 1)]);
        }


        @Override
        public int getCount() {
            return 10;
        }
    }
}