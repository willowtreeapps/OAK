package oak.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * User: ericrichardson Date: 5/6/13 Time: 2:10 PM
 */
public class FontTypefaceSpan extends MetricAffectingSpan {

    private Typeface mTypeface;

    public FontTypefaceSpan(Context context, String typefaceName) {
        mTypeface = OakUtils.getStaticTypeFace(context, typefaceName);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}
