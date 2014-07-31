package oak.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import oak.R;


/**
 * Created by sean.kenkeremath on 7/29/14.
 */
public class ResizableHeaderScrollView extends FrameLayout {

    private final static int SPACE_ID = 1001;
    private final static int SCROLLVIEW_ID = 1002;

    private final static int NOT_SPECIFIED = -1;

    private SyncedScrollView scrollView;

    private View headerFrame;
    private View header;

    private boolean headerCentered;
    private boolean headerCollapsed;
    private int minHeaderHeight;
    private int maxHeaderHeight;
    private int headerFlipScrollThreshold; //how much scrolling until header switches
    private HeaderChangeListener headerListener;


    public ResizableHeaderScrollView(Context context, int minHeaderHeight, int maxHeaderHeight,
                                     int headerResId, int scrollViewLayoutId,
                                     int headerFlipScrollThreshold, boolean headerCentered){
        super(context);
        this.headerCentered = headerCentered;
        init(minHeaderHeight, maxHeaderHeight, headerResId, scrollViewLayoutId,
                headerFlipScrollThreshold, NOT_SPECIFIED);

    }

    public ResizableHeaderScrollView(Context context, AttributeSet attr){
        super(context, attr);
        parseAttributes(attr);
    }

    public ResizableHeaderScrollView(Context context, AttributeSet attr, int defInStyles){
        super(context, attr, defInStyles);
        parseAttributes(attr);
    }

    private void parseAttributes(AttributeSet attr){
        if (attr!=null){
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attr,R.styleable.ResizableHeaderScrollView,0,0);
            if (a!=null){
                headerCentered = a.getBoolean(R.styleable.ResizableHeaderScrollView_headerCentered, false);
                minHeaderHeight = a.getDimensionPixelSize(R.styleable.ResizableHeaderScrollView_minHeaderHeight,NOT_SPECIFIED);
                maxHeaderHeight = a.getDimensionPixelSize(R.styleable.ResizableHeaderScrollView_maxHeaderHeight,NOT_SPECIFIED);
                headerFlipScrollThreshold = a.getDimensionPixelSize(R.styleable.ResizableHeaderScrollView_headerChangeThreshold,NOT_SPECIFIED);
                int headerBackground = a.getResourceId(R.styleable.ResizableHeaderScrollView_resizableHeaderBackground, NOT_SPECIFIED);
                int scrollViewRes = a.getResourceId(R.styleable.ResizableHeaderScrollView_contentLayout, NOT_SPECIFIED);
                int headerRes = a.getResourceId(R.styleable.ResizableHeaderScrollView_headerLayout, NOT_SPECIFIED);
                init(minHeaderHeight,maxHeaderHeight,headerRes,scrollViewRes,headerFlipScrollThreshold, headerBackground);
                a.recycle();
                return;
            }
        }

        init(NOT_SPECIFIED,NOT_SPECIFIED,NOT_SPECIFIED,NOT_SPECIFIED,NOT_SPECIFIED,NOT_SPECIFIED);
    }

    private int getContentScrolled(){
        return scrollView.getScrollY();
    }

    public int getHeaderDelta(){
        return maxHeaderHeight - minHeaderHeight;
    }

    private void init(int minHeaderHeight, int maxHeaderHeight, int headerRes,
                      int scrollViewLayoutId, int headerFlipThreshold, int headerBackground){

        if (minHeaderHeight >maxHeaderHeight){
            minHeaderHeight = maxHeaderHeight;
        } else if (maxHeaderHeight < minHeaderHeight){
            minHeaderHeight = maxHeaderHeight;
        }
        this.minHeaderHeight = minHeaderHeight;
        this.maxHeaderHeight = maxHeaderHeight;
        this.headerFlipScrollThreshold = headerFlipThreshold;

        /**
         * Header must be created first because the dimensions for spacing under the ScrollView will
         * be based on the measured height of the header if minHeaderHeight is NOT_SPECIFIED
         */
        createHeader(headerRes,headerBackground);
        createScrollView(scrollViewLayoutId);
        if (headerBackground!=NOT_SPECIFIED){
            setHeaderBackground(headerBackground);
        }

        scrollView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {

                if (!headerChangeEnabled()){
                    return;
                }

                /**
                 * Check how much the ScrollView has been scrolled and changed header if necessary
                 */
                if (getContentScrolled() > headerFlipScrollThreshold) {

                    if (headerListener!=null && !headerCollapsed){
                        headerListener.collapse(header);
                        headerCollapsed = true;
                    }
                } else if (getContentScrolled() < headerFlipScrollThreshold) {
                    if (headerListener!=null && headerCollapsed) {
                        headerListener.expand(header);
                        headerCollapsed = false;
                    }
                }

                adjustChildren();

            }
        });

        requestLayout();
    }

    public void setHeaderChangeListener(HeaderChangeListener listener){
        this.headerListener= listener;
    }

    private void createScrollView(int scrollViewLayoutId){

        /**
         * This method creates a RelativeLayout containing a SyncedScrollView and a blank View.
         * The blank view is necessary so the scrollview will cover the correct amount of space when
         * the header has shrunken to its minimum size.  The provided scrollViewLayoutId will be
         * inflated into the SyncedScrollView.
         */
        RelativeLayout layout = new RelativeLayout(getContext());
        FrameLayout.LayoutParams relLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(relLayoutParams);
        this.addView(layout);

        View space = new View(getContext());
        //noinspection ResourceType
        space.setId(SPACE_ID);
        RelativeLayout.LayoutParams spaceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                minHeaderHeight != NOT_SPECIFIED ? minHeaderHeight : 0);
        spaceParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        space.setLayoutParams(spaceParams);
        layout.addView(space);

        scrollView = new SyncedScrollView(getContext(), this);
        //noinspection ResourceType
        scrollView.setId(SCROLLVIEW_ID);
        RelativeLayout.LayoutParams scrollViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        scrollViewParams.addRule(RelativeLayout.ABOVE,space.getId());
        scrollView.setLayoutParams(scrollViewParams);
        LayoutInflater.from(getContext()).inflate(scrollViewLayoutId, scrollView);
        layout.addView(scrollView);
    }

    private void createHeader(int headerResId, int headerBackgroundId){

        /**
         * This frame will be the parent layout of the entire header.  This frame will move with
         * scrolling while its children will move the opposite direction to give the appearance
         * that the header is shrinking.
         */
        LinearLayout frame = new LinearLayout(getContext());
        if (headerBackgroundId != NOT_SPECIFIED){
            frame.setBackgroundResource(headerBackgroundId);
        }

        /**
         * The provided header resource is inflated into this container.  The container will be
         * a child of the frame created above and will move opposite to the frame to remain in place.
         * If headerCentered is true, the container will move -1 for every 2 its parent moves during
         * scrolling so it will stay centered.
         */
        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams container_params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        container.setLayoutParams(container_params);
        LayoutInflater.from(getContext()).inflate(headerResId, container);

        frame.addView(container);

        headerFrame = frame;
        header = container;

        this.addView(headerFrame);

        /**
         * The height of the header layout is measured and will be used as default values if
         * maxHeaderHeight or minHeaderHeight are NOT_SPECIFIED.
         */
        headerFrame.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        int measuredHeight = headerFrame.getMeasuredHeight();

        if (minHeaderHeight == NOT_SPECIFIED || minHeaderHeight < measuredHeight){
            minHeaderHeight = measuredHeight;
        }
        if (maxHeaderHeight == NOT_SPECIFIED || maxHeaderHeight < measuredHeight){
            maxHeaderHeight = measuredHeight;
        }
        if (minHeaderHeight >maxHeaderHeight){
            minHeaderHeight = maxHeaderHeight;
        } else if (maxHeaderHeight < minHeaderHeight){
            minHeaderHeight = maxHeaderHeight;
        }

        /**
         * The height for the header is set after the final maxHeaderHeight is determined.
         */
        FrameLayout.LayoutParams frame_params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                maxHeaderHeight);
        frame.setLayoutParams(frame_params);

        if (this.headerFlipScrollThreshold == NOT_SPECIFIED) {
            this.headerFlipScrollThreshold = getHeaderDelta() > 0 ? getHeaderDelta() / 2 : NOT_SPECIFIED;
        }
        this.headerCollapsed = false;
    }

    public void setHeaderBackground(int headerBackgroundRes){
        headerFrame.setBackgroundResource(headerBackgroundRes);
    }

    private boolean headerChangeEnabled(){
        if (getHeaderDelta() <= 0){
            return false;
        }
        return true;
    }

    // offset header frame, header, and scrollview based on scroll amount
    private void adjustChildren(){

        /**
         * This method offsets the frame, header, and scrollview based on the current amount of
         * scroll.  Since there are gaps between OnScroll callbacks, the current position of each
         * view is retrieved and the delta between where it should be and where it is is used to
         * position them.
         */

        if (getContentScrolled() < getHeaderDelta()) {
            int headerOffset = 0 - getContentScrolled();
            int headerCurrent = headerFrame.getTop();

            headerFrame.offsetTopAndBottom(headerOffset - headerCurrent);
            //cancel offset of frame
            header.offsetTopAndBottom(-(headerOffset - headerCurrent));

            if (headerCentered) {
                //offset an additional amount to keep the layout centered within the frame
                int layoutOffset = getContentScrolled() / 2;
                int layoutCurrent = header.getTop();
                header.offsetTopAndBottom(layoutOffset - layoutCurrent);
            }

            //offset scrollview based on height of header
            int scrollViewOffset = maxHeaderHeight - getContentScrolled();
            int scrollViewCurrent = scrollView.getTop();

            scrollView.offsetTopAndBottom(scrollViewOffset - scrollViewCurrent);

        } else {

            int headerOffset = 0 - getHeaderDelta();
            int headerCurrent = headerFrame.getTop();

            headerFrame.offsetTopAndBottom(headerOffset - headerCurrent);
            //cancel offset of frame
            header.offsetTopAndBottom(-(headerOffset - headerCurrent));

            if (headerCentered) {
                //offset an additional amount to keep the layout centered within the frame
                int layoutOffset = getHeaderDelta() / 2;
                int layoutCurrent = header.getTop();
                header.offsetTopAndBottom(layoutOffset - layoutCurrent);
            }

            //offset scrollview based on height of header
            int scrollViewOffset = maxHeaderHeight - getHeaderDelta();
            int scrollViewCurrent = scrollView.getTop();

            scrollView.offsetTopAndBottom(scrollViewOffset - scrollViewCurrent);

        }
    }

    public ScrollView getScrollView(){
        return scrollView;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        adjustChildren();
    }


    private class SyncedScrollView extends ScrollView {

        private boolean scrolling;
        private float scrollAmount;
        private ResizableHeaderScrollView parent;
        public OnScrollListener listener;


        public SyncedScrollView(Context context, ResizableHeaderScrollView parent) {
            super(context);
            this.parent = parent;
        }

        public SyncedScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SyncedScrollView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e){

            if (scrolling){
                e.offsetLocation(0,scrollAmount);
            }

            switch (e.getAction()){
                case MotionEvent.ACTION_MOVE:
                    scrolling = true;
                    break;
                case MotionEvent.ACTION_DOWN:
                    scrolling = true;
                    break;
                case MotionEvent.ACTION_UP:
                    scrolling = false;
                    scrollAmount = 0f;
                    break;

                case MotionEvent.ACTION_CANCEL:
                    scrolling = false;
                    scrollAmount = 0f;
                    break;

            }

            return super.onTouchEvent(e);
        }

        public void setOnScrollListener(OnScrollListener listener){
            this.listener = listener;
        }

        @Override
        protected void onScrollChanged(int x, int y, int oldx, int oldy){
            if (listener!=null){
                listener.onScrollChanged(x,y,oldx,oldy);
            }
            if (scrolling  && getScrollY() < parent.getHeaderDelta()){
                scrollAmount += (oldy-y);
            }
            super.onScrollChanged(x,y,oldx,oldy);
        }

    }

    public interface OnScrollListener{
        void onScrollChanged(int x, int y, int oldx, int oldy);
    }

    public interface HeaderChangeListener{
        void collapse(View header);
        void expand(View header);
    }

}
