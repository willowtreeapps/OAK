package oak.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;

public class ParentSwipingWebView extends WebView {

    private float mCurrX = 0.0f;
    private float mCurrY = 0.0f;
    private int mTouchSlop;
    private boolean overScrollTop = false;
    private boolean overScrollLeft = false;
    private boolean overScrollRight = false;
    private boolean overScrollBottom = false;
    private boolean isScrolling = false;

    public ParentSwipingWebView(Context context) {
        super(context);
        init();
    }


    public ParentSwipingWebView(Context context, AttributeSet attrs) {
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

        // width of the view
        int viewWidth = computeHorizontalScrollExtent();

        // width of the webpage depending on the zoom
        int innerWidth = computeHorizontalScrollRange();

        // position of the left side of the horizontal scrollbar
        int scrollBarLeftPos = computeHorizontalScrollOffset();

        // position of the right side of the horizontal scrollbar, the width of scroll is the width of view minus the width of vertical scrollbar
        int scrollBarRightPos = scrollBarLeftPos + viewWidth + scrollBarWidth;

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

        // height of the horizontal scrollbar
        int scrollBarHeight = getHorizontalScrollbarHeight();

        // height of the view
        int viewHeight = computeVerticalScrollExtent();

        // height of the webpage depending on the zoom
        int innerHeight = computeVerticalScrollRange();

        // position of the top side of the vertical scrollbar
        int scrollBarTopPos = computeVerticalScrollOffset();

        // position of the bottom of the vertical scrollbar, the height of scroll is the height of the view
        int scrollBarBottomPos = scrollBarTopPos + viewHeight + scrollBarHeight;

        // if top pos of scroll bar is 0 top over scrollling is true
        if (scrollBarTopPos == 0) {
            overScrollTop = true;
        } else {
            overScrollTop = false;
        }

        // if bottom pos of scroll bar is superior to webpage height: bottom over scrolling is true
        if (scrollBarBottomPos >= innerHeight) {
            overScrollBottom = true;
        } else {
            overScrollBottom = false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            if (!isScrolling) {
                if (((event.getX() - mCurrX) > mTouchSlop) && overScrollLeft) {
                    // User moved finger to the right
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (((mCurrX - event.getX()) > mTouchSlop) && overScrollRight) {
                    // User moved finger to the left
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (((event.getY() - mCurrY) > mTouchSlop) && overScrollTop) {
                    // User moved finger to the bottom
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (((mCurrY - event.getY()) > mTouchSlop) && overScrollBottom) {
                    // User moved finger to the top
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
