package oak.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;

public class BeastWebView extends WebView {

    private float mCurrX = 0.0f;
    private float mCurrY = 0.0f;
    private int mTouchSlop;
    private float oldX;
    private boolean overScrollLeft = false;
    private boolean overScrollRight = false;
    private boolean isScrolling = false;

    public BeastWebView(Context context) {
        super(context);
        init();
    }


    public BeastWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = (viewConfiguration.getScaledTouchSlop());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mCurrX = event.getX();
            mCurrY = event.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
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
        // width of the vertical scrollbar
        int scrollBarWidth = getVerticalScrollbarWidth();

        // width of the view depending of you set in the layout
        int viewWidth = computeHorizontalScrollExtent();

        // width of the webpage depending of the zoom
        int innerWidth = computeHorizontalScrollRange();

        // position of the left side of the horizontal scrollbar
        int scrollBarLeftPos = computeHorizontalScrollOffset();

        // position of the right side of the horizontal scrollbar, the width of scroll is the width of view minus the width of vertical scrollbar
        int scrollBarRightPos = scrollBarLeftPos + viewWidth - scrollBarWidth;

        // if left pos of scroll bar is 0 left over scrolling is true
        if (scrollBarLeftPos == 0) {
            overScrollLeft = true;
        } else {
            overScrollLeft = false;
        }

        // if right pos of scroll bar is superior to webpage width: right over scrolling is true
        if (scrollBarRightPos >= innerWidth) {
            overScrollRight = true;
        } else {
            overScrollRight = false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            // if scrollbar is the most left or right
            if (overScrollLeft || overScrollRight) {
                isScrolling = false;
            } else {
                isScrolling = true;
            }
            oldX = event.getX();
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!isScrolling) {
                if (event.getX() > oldX && overScrollLeft) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                if (event.getX() < oldX && overScrollRight) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (!isScrolling) {
                if (event.getX() > oldX && overScrollLeft) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                if (event.getX() < oldX && overScrollRight) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
