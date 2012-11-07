package oak.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import oak.OAK;

public class MaskedImageView extends ImageView {

    private BitmapDrawable maskDrawable;
    private NinePatchDrawable maskDrawableNine;
    private BitmapDrawable overlayDrawable;
    private NinePatchDrawable overlayDrawableNine;
    private int mFillColor;
    private int shadowColor;

    public MaskedImageView(Context context) {
        super(context);
    }

    public MaskedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources res = getResources();
        
        int fillColorResourceId = attrs.getAttributeResourceValue(OAK.XMLNS, "fillColor", -1);
        int glareColorResourceId = attrs.getAttributeResourceValue(OAK.XMLNS, "glareColor", -1);
        int maskResourceId = attrs.getAttributeResourceValue(OAK.XMLNS, "mask", -1);
        int overlayResourceId = attrs.getAttributeResourceValue(OAK.XMLNS, "overlay", -1);
        
        if (fillColorResourceId != -1){            
            setFillColor(res.getColor(fillColorResourceId));
        }
        if (glareColorResourceId != -1){
            setGlareColor(res.getColor(glareColorResourceId));
        }
        if (maskResourceId != -1){
            Drawable d = res.getDrawable(maskResourceId);
            if(d != null && d instanceof BitmapDrawable){
                setMaskDrawable((BitmapDrawable)d);
            }else if (d != null && d instanceof NinePatchDrawable){
                setMaskDrawable((NinePatchDrawable)d);
            }
        }
        if (overlayResourceId != -1){
            Drawable d = res.getDrawable(overlayResourceId);
            if(d != null && d instanceof BitmapDrawable){
                setOverlayDrawable((BitmapDrawable)d);
            }else if (d != null && d instanceof NinePatchDrawable){
                setOverlayDrawable((NinePatchDrawable)d);
            }
        }
    }

    public void setFillColor(int color) {
        mFillColor = color;
    }

    private void setGlareColor(int color){
        shadowColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        BitmapDrawable bmDrawable = (BitmapDrawable) getDrawable();
        Rect rect = new Rect();
        getDrawingRect(rect);
        Bitmap product = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);

        if (bmDrawable != null && bmDrawable.getBitmap() != null) {
            product.setDensity(bmDrawable.getBitmap().getDensity());
            Canvas c = new Canvas(product);
            c.setDensity(canvas.getDensity());
            c.drawColor(mFillColor);
            super.onDraw(c);
            if (maskDrawable != null) {
                Paint p = new Paint();
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
                c.drawBitmap(maskDrawable.getBitmap(), null, rect, p);
            } else if (maskDrawableNine != null) {
                maskDrawableNine.setBounds(rect);
                maskDrawableNine.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
                maskDrawableNine.draw(c);
            }
            if(shadowColor!=0){
                //draw glare
                Paint glarePaint = new Paint();
                glarePaint.setColor(shadowColor);
                glarePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                Path glarePath = new Path();
                glarePath.moveTo(rect.centerX()-rect.width()/2, rect.centerY()-rect.height()/2);
                glarePath.lineTo(rect.centerX()-rect.width()/2, rect.centerY()+rect.height()/2-rect.height()/5);
                glarePath.lineTo(rect.centerX()+rect.width()/2, rect.centerY()-rect.height()/2+rect.height()/5);
                glarePath.lineTo(rect.centerX()+rect.width()/2, rect.centerY()-rect.height()/2);
                c.drawPath(glarePath, glarePaint);
            }
            if (overlayDrawable != null) {
                Paint p = new Paint();
                c.drawBitmap(overlayDrawable.getBitmap(), null, rect, p);
            } else if (overlayDrawableNine != null) {
                overlayDrawableNine.setBounds(rect);
                overlayDrawableNine.draw(c);
            }
            canvas.drawBitmap(product, null, rect, new Paint());
        }
        product.recycle();
    }

    public void setMaskDrawable(BitmapDrawable bmDrawable) {
        this.maskDrawable = bmDrawable;
    }

    public void setMaskDrawable(NinePatchDrawable bmDrawable) {
        this.maskDrawableNine = bmDrawable;
    }

    public void setOverlayDrawable(BitmapDrawable bmDrawable) {
        this.overlayDrawable = bmDrawable;
    }

    public void setOverlayDrawable(NinePatchDrawable npDrawable) {
        this.overlayDrawableNine = npDrawable;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    @Override
    public void requestLayout() {
        // do nothing -- the layout changes so this optimizes things a bit
    }
}