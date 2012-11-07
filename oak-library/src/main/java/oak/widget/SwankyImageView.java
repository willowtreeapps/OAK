package oak.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

import oak.util.ScaleGestureDetector;

/**
 * An ImageView that supports pinch-to-zoom, double-tap-to-zoom, and swipe panning.
 *
 * Usage: - Set image source via android:src, setImageBitmap(), or setImageResource() - Supports
 * scale type of CENTER_CROP (default) and CENTER_INSIDE - Default maximum zoom scale is 2.5x
 *
 * @author Nate Vogt Based loosely on TouchImageView by Michael Ortiz
 */
public class SwankyImageView extends ImageView {

    private Matrix matrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0, DRAG = 1, ZOOM = 2;
    private static final float MIN_SCALE = 1f;
    private float maxScale = 2.5f;
    private static final float FRICTION_K = .01f; // lose this fraction of velocity per millisecond
    private static final float VEL_THRESHOLD = .05f; // pixels per millisecond
    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    private int mode = NONE;
    private long timeOfLastMoveEvent;

    // Remember some things for zooming
    private PointF last = new PointF(), start = new PointF();
    private float saveScale = 1f;
    private float[] m;

    private float redundantXSpace, redundantYSpace;

    private boolean isCenterInside;

    private float viewWidth, viewHeight;
    private float right, bottom, origWidth, origHeight, bitmapWidth, bitmapHeight;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mTapSwipeDetector;
    private OnClickListener clickListener;

    private static final Interpolator ZOOM_INTERPOLATOR = new Interpolator() {
        public float getInterpolation(float input) {
            return input * 2 - input * input; // parabolic curve, similar to DECELERATE
        }
    };

    private AsyncTask<Void, Void, Void> mFlingTask;

    private long lastFrameCompleted;

    private static final int MSG_REDRAW = 1, MSG_STOP_ANIM = 2;

    private final Handler FLING_ANIM_HANDLER = new Handler() {

        private long lastUpdated;
        private final int FRAME_RATE = 25; // ms per frame

        public void handleMessage(Message msg) {
            if (lastUpdated < lastFrameCompleted) {
                lastUpdated = lastFrameCompleted;
                setImageMatrix(matrix);
                invalidate();
            } else if (msg.what == MSG_REDRAW) {
                sendEmptyMessage(MSG_REDRAW);
                return;
            }
            if (msg.what == MSG_REDRAW) {
                sendEmptyMessageDelayed(MSG_REDRAW, FRAME_RATE);
            } else if (msg.what == MSG_STOP_ANIM) {
                removeMessages(MSG_REDRAW);
                removeMessages(MSG_STOP_ANIM);
            }
        }
    };

    public SwankyImageView(Context context) {
        super(context);
        initialize(null);
    }

    public SwankyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public SwankyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mTapSwipeDetector = new GestureDetector(getContext(), new TapSwipeListener());
        if (attrs != null) {
            // default to centerCrop, but check to see if centerInside is requested
            int scaleType = attrs.getAttributeIntValue(ANDROID_SCHEMA, "scaleType", 6);
            if (scaleType == 7) {
                isCenterInside = true;
            }
        }
        setScaleType(ScaleType.MATRIX);
        matrix.setTranslate(1f, 1f);
        m = new float[9];
        Drawable d = getDrawable();
        setImageMatrix(matrix);
        if (d != null)
        // avoid overwriting src if it was already set in xml
        {
            setImageBitmap(((BitmapDrawable) d).getBitmap());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mTapSwipeDetector.onTouchEvent(event);
        matrix.getValues(m);
        float x = m[Matrix.MTRANS_X];
        float y = m[Matrix.MTRANS_Y];
        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mFlingTask != null) {
                    mFlingTask.cancel(true);
                }
                last.set(event.getX(), event.getY());
                start.set(last);
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
//			log("x = "+x+", y = "+y+", bottom = "+bottom+", right = "+right);
                timeOfLastMoveEvent = System.currentTimeMillis();
                if (mode == DRAG) {
                    float deltaX = curr.x - last.x;
                    float deltaY = curr.y - last.y;
                    float scaleWidth = Math.round(origWidth * saveScale);
                    float scaleHeight = Math.round(origHeight * saveScale);
                    if (scaleWidth < viewWidth) {
                        deltaX = 0;
                        if (y + deltaY > 0) {
                            deltaY = -y;
                        } else if (y + deltaY < -bottom) {
                            deltaY = -(y + bottom);
                        }
                    } else if (scaleHeight < viewHeight) {
                        deltaY = 0;
                        if (x + deltaX > 0) {
                            deltaX = -x;
                        } else if (x + deltaX < -right) {
                            deltaX = -(x + right);
                        }
                    } else {
                        if (x + deltaX > 0) {
                            deltaX = -x;
                        } else if (x + deltaX < -right) {
                            deltaX = -(x + right);
                        }

                        if (y + deltaY > 0) {
                            deltaY = -y;
                        } else if (y + deltaY < -bottom) {
                            deltaY = -(y + bottom);
                        }
                    }
                    matrix.postTranslate(deltaX, deltaY);
                    last.set(curr.x, curr.y);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    int pointerRemaining = event.getAction() == MotionEvent.ACTION_POINTER_2_UP ? 0
                            : 1;
                    last.set(event.getX(pointerRemaining), event.getY(pointerRemaining));
                    mode = DRAG;
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
        }
        setImageMatrix(matrix);
        invalidate();
        return true; // indicate event was handled
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmapWidth = bm.getWidth();
        bitmapHeight = bm.getHeight();
    }

    @Override
    public void setImageResource(int resId) {
        setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    /**
     * Fetches the image's current zoom scale, between 1 and max.
     *
     * @return current scale
     */
    public float getCurrentScale() {
        return saveScale;
    }

    /**
     * @param scale new max scale value
     * @throws IllegalArgumentException if scale is less than or equal to 1
     */
    public void setMaxScale(float scale) throws IllegalArgumentException {
        if (scale <= 1) {
            throw new IllegalArgumentException("Max scale must be greater than 1.");
        } else {
            resetScale();
            maxScale = scale;
        }
    }

    public void resetScale() {
        last = new PointF();
        start = new PointF();
        fitToScreen();
    }

    private class TapSwipeListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            ZoomAnimation anim;
            if (saveScale > MIN_SCALE) {
                anim = new ZoomAnimation(saveScale, MIN_SCALE, e.getX(), e.getY());
            } else {
                anim = new ZoomAnimation(MIN_SCALE, maxScale, e.getX(), e.getY());
            }
            startAnimation(anim);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onClick();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mode = NONE;
            mFlingTask = new FlingTask(timeOfLastMoveEvent, velocityX / 1000, velocityY / 1000)
                    .execute();
            return true;
        }
    }

    private class FlingTask extends AsyncTask<Void, Void, Void> {

        private long curTimestamp;
        private float velX, velY; // pixels per millisecond

        public FlingTask(long prevTimestamp, float velX, float velY) {
            lastFrameCompleted = timeOfLastMoveEvent;
            this.velX = velX;
            this.velY = velY;
        }

        @Override
        protected Void doInBackground(Void... params) {
            float x, y, deltaX, deltaY, scaleWidth, scaleHeight;
            FLING_ANIM_HANDLER.sendEmptyMessage(MSG_REDRAW);
            while (!isCancelled()) {
                curTimestamp = System.currentTimeMillis();
                int deltaT = (int) (curTimestamp - lastFrameCompleted);
                matrix.getValues(m);
                x = m[Matrix.MTRANS_X];
                y = m[Matrix.MTRANS_Y];
                deltaX = velX * deltaT;
                deltaY = velY * deltaT;
                scaleWidth = Math.round(origWidth * saveScale);
                scaleHeight = Math.round(origHeight * saveScale);

                if (scaleWidth <= viewWidth) {
                    deltaX = 0;
                    velX = 0;
                } else if (x + deltaX >= 0) {
                    deltaX = -x;
                    velX = 0;
                } else if (x + deltaX <= -right) {
                    deltaX = -x - right;
                    velX = 0;
                }

                if (scaleHeight <= viewHeight) {
                    deltaY = 0;
                    velY = 0;
                } else if (y + deltaY >= 0) {
                    deltaY = -y;
                    velY = 0;
                } else if (y + deltaY <= -bottom) {
                    deltaY = -y - bottom;
                    velY = 0;
                }

                if (velX != 0) {
                    velX *= 1 - FRICTION_K * deltaT;
                }
                if (velY != 0) {
                    velY *= 1 - FRICTION_K * deltaT;
                }
                if (Math.abs(velX) < VEL_THRESHOLD && Math.abs(velY) < VEL_THRESHOLD) {
                    velX = velY = 0;
                }

                matrix.postTranslate(deltaX, deltaY);
                lastFrameCompleted = curTimestamp;
                if (velX == 0 && velY == 0) {
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            FLING_ANIM_HANDLER.sendEmptyMessage(MSG_STOP_ANIM);
            mFlingTask = null;
        }

        @Override
        protected void onCancelled() {
            FLING_ANIM_HANDLER.sendEmptyMessage(MSG_STOP_ANIM);
            mFlingTask = null;
        }
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = (float) Math.min(
                    Math.max(.95f, detector.getScaleFactor()), 1.05);
            float origScale = saveScale;
            saveScale *= mScaleFactor;
            if (saveScale > maxScale) {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            } else if (saveScale < MIN_SCALE) {
                saveScale = MIN_SCALE;
                mScaleFactor = MIN_SCALE / origScale;
            }
            right = viewWidth * saveScale - viewWidth
                    - (2 * redundantXSpace * saveScale);
            bottom = viewHeight * saveScale - viewHeight
                    - (2 * redundantYSpace * saveScale);
            boolean viewWiderThanImg = origWidth * saveScale <= viewWidth;
            boolean viewTallerThanImg = origHeight * saveScale <= viewHeight;
            if (viewWiderThanImg || viewTallerThanImg) {
                if (viewWiderThanImg && !viewTallerThanImg) {
                    matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
                            detector.getFocusY());
                } else if (!viewWiderThanImg && viewTallerThanImg) {
                    matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(),
                            viewHeight / 2);
                } else {
                    matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);
                }
                if (mScaleFactor < 1) {
                    matrix.getValues(m);
                    float x = m[Matrix.MTRANS_X];
                    float y = m[Matrix.MTRANS_Y];
                    if (Math.round(origWidth * saveScale) < viewWidth) {
                        if (y < -bottom) {
                            matrix.postTranslate(0, -(y + bottom));
                        } else if (y > 0) {
                            matrix.postTranslate(0, -y);
                        }
                    } else {
                        if (x < -right) {
                            matrix.postTranslate(-(x + right), 0);
                        } else if (x > 0) {
                            matrix.postTranslate(-x, 0);
                        }
                    }
                    // why was this here again?  removing for now.
//                  if (!isCenterInside) {
//                      matrix.postTranslate(-x - right / 2, bottom / 2 - y);
//                  }
                }
            } else {
                matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(),
                        detector.getFocusY());
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                if (mScaleFactor < 1) {
                    if (x < -right) {
                        matrix.postTranslate(-(x + right), 0);
                    } else if (x > 0) {
                        matrix.postTranslate(-x, 0);
                    }
                    if (y < -bottom) {
                        matrix.postTranslate(0, -(y + bottom));
                    } else if (y > 0) {
                        matrix.postTranslate(0, -y);
                    }
                }
            }
            return true;

        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        clickListener = l;
    }

    private void onClick() {
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        fitToScreen();
    }

    private void fitToScreen() {
        float scale;
        if (isCenterInside) {
            scale = Math.min(viewWidth / bitmapWidth, viewHeight / bitmapHeight);
        } else // centerCrop
        {
            scale = Math.max(viewWidth / bitmapWidth, viewHeight / bitmapHeight);
        }
        matrix.setScale(scale, scale);
        saveScale = 1f;

        redundantYSpace = (viewHeight - scale * bitmapHeight) / 2f;
        redundantXSpace = (viewWidth - scale * bitmapWidth) / 2f;

        matrix.postTranslate(redundantXSpace, redundantYSpace);

        origWidth = viewWidth - 2 * redundantXSpace;
        origHeight = viewHeight - 2 * redundantYSpace;
        right = viewWidth * saveScale - viewWidth - (2 * redundantXSpace * saveScale);
        bottom = viewHeight * saveScale - viewHeight - (2 * redundantYSpace * saveScale);
        setImageMatrix(matrix);
    }

    private class ZoomAnimation extends Animation {

        private float startScale, endScale, centerX, centerY;

        public ZoomAnimation(float startScale, float endScale, float centerX, float centerY) {
            this.startScale = startScale;
            this.endScale = endScale;
            if (this.startScale > this.endScale) {
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                if (x >= 0) {
                    this.centerX = viewWidth / 2f;
                } else {
                    this.centerX = viewWidth * ((-x) / right);
                }
                if (y >= 0) {
                    this.centerY = viewHeight / 2f;
                } else {
                    this.centerY = viewHeight * ((-y) / bottom);
                }
            } else {
                this.centerX = centerX;
                this.centerY = centerY;
            }
            setDuration(250);
            setInterpolator(ZOOM_INTERPOLATOR);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float origScale = saveScale;
            saveScale = startScale + (endScale - startScale) * interpolatedTime;
            float scaleFactor = saveScale / origScale;
            right = viewWidth * saveScale - viewWidth
                    - (2 * redundantXSpace * saveScale);
            bottom = viewHeight * saveScale - viewHeight
                    - (2 * redundantYSpace * saveScale);
            boolean viewWiderThanImg = origWidth * saveScale <= viewWidth;
            boolean viewTallerThanImg = origHeight * saveScale <= viewHeight;
            if (viewWiderThanImg || viewTallerThanImg) {
                if (viewWiderThanImg && !viewTallerThanImg) {
                    matrix.postScale(scaleFactor, scaleFactor, viewWidth / 2, centerY);
                } else if (!viewWiderThanImg && viewTallerThanImg) {
                    matrix.postScale(scaleFactor, scaleFactor, centerX, viewHeight / 2);
                } else {
                    matrix.postScale(scaleFactor, scaleFactor, viewWidth / 2, viewHeight / 2);
                }
                if (scaleFactor < 1) {
                    matrix.getValues(m);
                    float x = m[Matrix.MTRANS_X];
                    float y = m[Matrix.MTRANS_Y];
                    if (Math.round(origWidth * saveScale) < viewWidth) {
                        if (y < -bottom) {
                            matrix.postTranslate(0, -(y + bottom));
                        } else if (y > 0) {
                            matrix.postTranslate(0, -y);
                        }
                    } else {
                        if (x < -right) {
                            matrix.postTranslate(-(x + right), 0);
                        } else if (x > 0) {
                            matrix.postTranslate(-x, 0);
                        }
                    }
                    // why was this here again?  removing for now.
//                  if (!isCenterInside) {
//                      matrix.postTranslate(-x - right / 2, bottom / 2 - y);
//                  }
                }
            } else {
                matrix.postScale(scaleFactor, scaleFactor, centerX, centerY);
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                if (scaleFactor < 1) {
                    if (x < -right) {
                        matrix.postTranslate(-(x + right), 0);
                    } else if (x > 0) {
                        matrix.postTranslate(-x, 0);
                    }
                    if (y < -bottom) {
                        matrix.postTranslate(0, -(y + bottom));
                    } else if (y > 0) {
                        matrix.postTranslate(0, -y);
                    }
                }
            }
            setImageMatrix(matrix);
            invalidate();
        }
    }

    public boolean canScrollLeft() {
        matrix.getValues(m);
        if (m[Matrix.MTRANS_X] < 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canScrollRight() {
        matrix.getValues(m);
        if (-m[Matrix.MTRANS_X] < right) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == GONE || visibility == INVISIBLE) {
            if (mFlingTask != null) {
                mFlingTask.cancel(true);
                mFlingTask = null;
            }
            FLING_ANIM_HANDLER.sendEmptyMessage(MSG_STOP_ANIM);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mFlingTask != null) {
            mFlingTask.cancel(true);
            mFlingTask = null;
        }
        FLING_ANIM_HANDLER.sendEmptyMessage(MSG_STOP_ANIM);
    }
}