/*
 * Copyright (c) 2011. WillowTree Apps, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.demo.widget;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.widget.BeastViewPager;
import roboguice.inject.InjectView;

public class BeastViewPagerActivity extends OakDemoActivity {

    @InjectView(R.id.view_pager) ViewPager mPager;

    TestFragmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beast_view_pager_demo);

        String[] fragmentTitles = new String[]{"THIS", "IS", "A", "TEST"};

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager(), fragmentTitles);
        mPager.setAdapter(mAdapter);
    }

    class TestFragmentAdapter extends FragmentPagerAdapter {

        private String[] mTitles;

        public TestFragmentAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.newInstance(mTitles[position]);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    public static class TestFragment extends RoboSherlockFragment {

        private static final String KEY_CONTENT = "TestFragment:Content";

        public static TestFragment newInstance(String content) {
            TestFragment fragment = new TestFragment();

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                builder.append(content).append(" ");
            }
            builder.deleteCharAt(builder.length() - 1);
            fragment.mContent = builder.toString();

            return fragment;
        }

        private String mContent = "???";
        private String[] mViewPagerContent = new String[]{"Using BeastViewPager, you can dispatch touch events",
                "to the parent View if you are on the leftmost or rightmost view.",
                "This is especially helpful for ViewPagers inside ViewPagers,",
                "or for applications using the Sliding Menu/Side Navigation UI pattern."};

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
                mContent = savedInstanceState.getString(KEY_CONTENT);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.test_fragment, null);

            BeastViewPager bvp = (BeastViewPager) view.findViewById(R.id.view_pager);
            TestFragmentPagerAdapter tfpa = new TestFragmentPagerAdapter(mViewPagerContent);
            bvp.setAdapter(tfpa);
            TextView tv = (TextView) view.findViewById(R.id.text_view);
            tv.setText(mContent + " " + mContent + " " + mContent + " " + mContent + " " + mContent + " " + mContent);

            return view;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(KEY_CONTENT, mContent);
        }

        private class TestFragmentPagerAdapter extends PagerAdapter {

            private String[] testContent;

            public TestFragmentPagerAdapter(String[] content) {
                testContent = content;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TextView text = new TextView(getActivity());
                text.setGravity(Gravity.CENTER);
                text.setText(testContent[position]);
                text.setTextSize(12 * getResources().getDisplayMetrics().density);
                text.setPadding(20, 20, 20, 20);

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setBackgroundColor(Color.GRAY);
                layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.setGravity(Gravity.CENTER);
                layout.addView(text);
                container.addView(layout);

                return layout;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object view) {
                container.removeView((View) view);
            }

            @Override
            public int getCount() {
                return testContent.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view.equals(o);
            }
        }
    }
}
