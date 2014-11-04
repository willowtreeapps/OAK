package oak.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * User: derek Date: 7/12/12 Time: 2:57 PM
 * <p/>
 * Viewpager that is helpful when having to handle multiple views with touch or swipe events.
 * For example, handling a viewpager in a viewpager.
 */
public class ParentSwipingViewPager extends ViewPager {

    private float mCurrX = 0.0f;
    private float mCurrY = 0.0f;
    private float mStartX, mStartY;
    private int mTouchSlop;

    public ParentSwipingViewPager(Context context) {
        super(context);
        init();
    }

    public ParentSwipingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = (viewConfiguration.getScaledTouchSlop());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        super.onInterceptTouchEvent(event);
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mCurrX = event.getX();
            mCurrY = event.getY();
            mStartX = event.getX();
            mStartY = event.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (Math.abs(event.getY() - mStartY) > Math.abs(event.getX() - mStartX)) {
            // User scrolled vertically
            getParent().requestDisallowInterceptTouchEvent(false);
        } else if (action == MotionEvent.ACTION_MOVE) {
            // Shouldn't need to do anything
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            getParent().requestDisallowInterceptTouchEvent(false);
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            //mCurrX = event.getX();
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (getCurrentItem() == 0 && ((event.getX() - mCurrX) > mTouchSlop)) {
                // User moved finger to the right and is on the leftmost ViewGroup
                getParent().requestDisallowInterceptTouchEvent(false);
            } else if (getCurrentItem() == (getAdapter().getCount() - 1) && ((mCurrX - event.getX()) > mTouchSlop)) {
                // User moved finger to the left and is on the rightmost ViewGroup
                getParent().requestDisallowInterceptTouchEvent(false);
            } else if (Math.abs(event.getY() - mCurrY) > Math.abs(event.getX() - mCurrX)) {
                // User scrolled vertically
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.onTouchEvent(event);
    }
}