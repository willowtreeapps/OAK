package oak.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ViewPager that helps while using mapviews, nav drawers, etc.
 *
 *
 * Created by david.hodge on 3/24/14.
 */
public class EdgeyViewPager extends ViewPager {

    private int edgeSize = 16;

    private int mDefaultEdgeSize;
    private int mEdgeSize;
    float mStartDragX;
    OnSwipeOutListener mListener;

    public EdgeyViewPager(Context context) {
        super(context);
        init();
    }

    public EdgeyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public int getEdgeSize(){
        return edgeSize;
    }

    /**
     * Allows you to set a custom size to the swipe edge.
     * The default value is 16.
     *
     * @param edgeSize
     */
    public void setEdgeSize(int edgeSize){
        this.edgeSize = edgeSize;
    }

    public void EdgeyViewPager(OnSwipeOutListener listener) {
        mListener = listener;
    }

    void init() {
        final float density = getContext().getResources().getDisplayMetrics().density;
        mDefaultEdgeSize = (int) (edgeSize * density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int measuredWidth = getMeasuredWidth();
        final int maxGutterSize = measuredWidth / 10;
        mEdgeSize = Math.min(maxGutterSize, mDefaultEdgeSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == getAdapter().getCount() - 1) {
            final int action = ev.getAction();
            float x = ev.getX();
            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mStartDragX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (x < mStartDragX) {
                        mListener.onSwipeOutAtEnd();
                    } else {
                        mStartDragX = 0;
                    }
                    break;
            }
        } else if (ev.getAction() != MotionEvent.ACTION_UP && ev.getX() < mEdgeSize) {
            return false;
        } else {
            mStartDragX = 0;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        public void onSwipeOutAtEnd();
    }

}
