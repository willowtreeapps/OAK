package oak.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Gallery-esque ViewPager that supports zoom gestures.
 */
public class SwankyGallery extends FrameLayout {
    private boolean hasBeenZooming;
    private Context mContext;
    private SwankyViewPager mViewPager;
    private SwankyAdapter mAdapter;
    private static final BitmapFactory.Options LO_RES_OPTIONS = new BitmapFactory.Options(),
            HI_RES_OPTIONS = new BitmapFactory.Options();
    private float xPosPrev;
    private float mMaxZoom = 2.5f;
    private OnGalleryPageSelectedListener mPageSelectedListener = null;

    // unused
    static { LO_RES_OPTIONS.inSampleSize = 2; HI_RES_OPTIONS.inSampleSize = 1; }

    public SwankyGallery(Context context) {
        super(context);
        init(context, null);
    }

    public SwankyGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwankyGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mViewPager = new SwankyViewPager(context, attrs);
        mViewPager.setSaveEnabled(false);
        addView(mViewPager);
        if (attrs != null) {
            // do stuff with attrs
        }
    }

    public void setAdapter(SwankyAdapter adapter) {
        mViewPager.setAdapter(mAdapter = adapter);
    }

    /**
     * Sets maximum alpha when buttons fade in
     *
     * @param item set which item to go to
     * @param smoothScroll boolean on whether to smooth scroll or not
     */
    public void setCurrentItem(int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
    }

    /**
     * Set the max zoom level
     *
     * @param maxZoom
     */
    public void setMaxZoom(float maxZoom) {
        mMaxZoom = maxZoom;
    }

    public void setOffscreenPageLimit(int limit) {
        mViewPager.setOffscreenPageLimit(limit);
    }

    public void setOnGalleryPageSelectedListener(OnGalleryPageSelectedListener listener) {
        mPageSelectedListener = listener;
    }

    /**
     * Unused
     * @param sampleSize
     */
    public void setLowSampleSize(int sampleSize) {
        if (sampleSize < HI_RES_OPTIONS.inSampleSize) {
            throw new IllegalArgumentException("Low-res sample size may not be less than high-res");
        } else {
            LO_RES_OPTIONS.inSampleSize = sampleSize;
        }
    }

    /**
     * Unused
     * @param sampleSize
     */
    public void setHighSampleSize(int sampleSize) {
        if (sampleSize > LO_RES_OPTIONS.inSampleSize) {
            throw new IllegalArgumentException("High-res sample size may not be greater than low-res");
        } else {
            HI_RES_OPTIONS.inSampleSize = sampleSize;
        }
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        SwankyImageView currentView = mViewPager.getCurrentView();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                hasBeenZooming = false;
                mViewPager.onInterceptTouchEvent(event); // required to initiate ViewPager behavior
                mViewPager.onTouchEvent(event);
                currentView.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                currentView.onTouchEvent(event);
                hasBeenZooming = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                currentView.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() >= 2) {
                    currentView.onTouchEvent(event);
                } else if (currentView.getCurrentScale() == 1 && !hasBeenZooming) {
                    mViewPager.onTouchEvent(event);
                } else {
                    mViewPager.getCurrentView().onTouchEvent(event);
                    if (!hasBeenZooming) {
                        if ((xPosPrev < event.getX() && !mViewPager.getCurrentView()
                                .canScrollLeft()) ||
                                (xPosPrev > event.getX() && !mViewPager.getCurrentView()
                                        .canScrollRight())) {
                            mViewPager.onTouchEvent(event);
                        }
                    }
                }
                break;
            default:
                if (!hasBeenZooming) {
                    mViewPager.onTouchEvent(event);
                }
                mViewPager.getCurrentView().onTouchEvent(event);
        }
        xPosPrev = event.getX();
        return true;
    }

    public static class SwankyAdapter extends PagerAdapter {

        private Context mContext;
        private int[] mIds;

        public SwankyAdapter(Context context, int[] imageIds) {
            mContext = context;
            mIds = imageIds;
        }

        public SwankyImageView instantiateItem(ViewGroup container, int position) {
            SwankyImageView img = new SwankyImageView(mContext);
            img.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), mIds[position],
                    HI_RES_OPTIONS));
            container.addView(img);
            img.setTag(position);
            return img;
        }

        public int getCount() {
            return mIds.length;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class SwankyViewPager extends ViewPager {

        private int currentItem = 0;

        private final SimpleOnPageChangeListener pageChangeListener = new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                View prev = findViewWithTag(currentItem);
                if (prev != null) {
                    if (((SwankyImageView) prev).getCurrentScale() > 1) {
                        ((SwankyImageView) prev).resetScale();
                    }
                }
                currentItem = i;
                if (mPageSelectedListener != null) {
                    mPageSelectedListener.onPageSelected(i);
                }
            }
        };

        public SwankyViewPager(Context context) {
            super(context);
            setOnPageChangeListener(pageChangeListener);
        }

        public SwankyViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            setOnPageChangeListener(pageChangeListener);
        }

        @Override
        protected void onPageScrolled(int position, float offset, int offsetPixels) {
            super.onPageScrolled(position, offset, offsetPixels);
            Log.d("SWANK", "page scrolled: " + offsetPixels);
        }

        public final SwankyImageView getCurrentView() {
            return (SwankyImageView) findViewWithTag(getCurrentItem());
        }
    }

    public static interface OnGalleryPageSelectedListener {
        public void onPageSelected(int index);
    }

}
