/*
 * Copyright (c) 2011. WillowTree Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import oak.BaseSectionAdapter;


/**
 * A ListView that maintains a header pinned at the top of the list. The
 * pinned header can be pushed up and dissolved as needed.
 */
public class SectionListView extends ListView {
    public static final String TAG = SectionListView.class.getSimpleName();

    private View mHeaderView;
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;
    private int mHeaderViewHeight;

    private BaseSectionAdapter mAdapter;

    public void setPinnedHeaderView(View view) {
        mHeaderView = view;

        // Disable vertical fading when the pinned header is present
        // TODO change ListView to allow separate measures for top and bottom fading edge;
        // in this particular case we would like to disable the top, but not the bottom edge.
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    public void configureHeaderView(int position) {
        if (mHeaderView == null) {
            return;
        }

        int state = mAdapter.getPinnedHeaderState(position);
        switch (state) {
            case BaseSectionAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case BaseSectionAdapter.PINNED_HEADER_VISIBLE: {
                mAdapter.configurePinnedHeader(mHeaderView, position, 255);
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case BaseSectionAdapter.PINNED_HEADER_PUSHED_UP: {
                View firstView = getChildAt(0);
                if (firstView != null) {
                    int bottom = firstView.getBottom();
                    int headerHeight = mHeaderView.getHeight();
                    int y;
                    int alpha;
                    if (bottom < headerHeight) {
                        y = (bottom - headerHeight);
                        alpha = 255 * (headerHeight + y) / headerHeight;
                    } else {
                        y = 0;
                        alpha = 255;
                    }
                    mAdapter.configurePinnedHeader(mHeaderView, position, alpha);
                    if (mHeaderView.getTop() != y) {
                        mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                    }
                    mHeaderViewVisible = true;
                }
                break;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }


    public SectionListView(Context context) {
        super(context);
    }

    public SectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SectionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof BaseSectionAdapter)) {
            throw new IllegalArgumentException(SectionListView.class.getSimpleName() + " must use adapter of type " + BaseSectionAdapter.class.getSimpleName());
        }

        // previous adapter
        if (this.mAdapter != null) {
            this.setOnScrollListener(null);
        }

        this.mAdapter = (BaseSectionAdapter) adapter;
        this.setOnScrollListener((BaseSectionAdapter) adapter);

        View dummy = new View(getContext());
        super.addFooterView(dummy);
        super.setAdapter(adapter);
        super.removeFooterView(dummy);
    }

    @Override
    public BaseSectionAdapter getAdapter() {
        return mAdapter;
    }

}
