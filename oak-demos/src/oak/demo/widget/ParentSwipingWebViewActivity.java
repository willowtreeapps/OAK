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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.widget.ParentSwipingWebView;
import roboguice.inject.InjectView;

public class ParentSwipingWebViewActivity extends OakDemoActivity {

    @InjectView(R.id.view_pager) ViewPager mPager;

    TestFragmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_swiping_view_pager_demo);

        String demoText
                = "Using ParentSwipingWebView, you can dispatch touch events to the parent View if you are on an edge of the WebView.  This is especially helpful for WebViews placed inside ViewPagers or other scrollable elements like a ListView, or for applications using the Sliding Menu/Side Navigation UI pattern.";

        String[] fragmentTitles = new String[4];
        fragmentTitles[0] = demoText;
        fragmentTitles[1] = demoText;
        fragmentTitles[2] = demoText;
        fragmentTitles[3] = demoText;

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

            fragment.mContent = content;

            return fragment;
        }

        private String mContent = "???";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
                mContent = savedInstanceState.getString(KEY_CONTENT);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.test_fragment_web_view, null);

            WebView wv = (ParentSwipingWebView) view.findViewById(R.id.web_view);
            wv.loadUrl("http://willowtreeapps.github.com/OAK/");
            TextView tv = (TextView) view.findViewById(R.id.text_view);
            tv.setText(mContent);

            return view;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(KEY_CONTENT, mContent);
        }
    }
}
