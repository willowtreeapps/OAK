package com.willowtreeapps.android.shared;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class ResizedTextView extends TextView {

    private float minTextSize;

    private int maxLines;

    private float defaultTextWidth;
    //private boolean tooLarge = false;

    public ResizedTextView(Context context) {
        this(context, null);
    }

    public ResizedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResizedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        maxLines = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/android",
                "maxLines",
                2);

        minTextSize = attrs.getAttributeIntValue(
                OAK.XMLNS,
                "minTextSize",
                10);
    }

    /*
         * Set TextSize to 1, and expand until the TextView becomes its previous size.
         */

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //Get it's current Width, set size to 1, and get that width
        this.defaultTextWidth = this.getMeasuredWidth();

        this.setTextSize(findTextSize(1)); //We want the last size that didn't break the loop
        this.setMeasuredDimension((int) this.defaultTextWidth, this.getMeasuredHeight());
//		if(tooLarge) {this.setEllipsize(getEllipsize());}
    }

    /*
         * Recursive function, that returns the optimal text size, and sets the correct number of lines
         * param: int lines = The number of lines to start out with. For now, its always 1, but a MinimumLines feature could be added later
         *
         * This is not as efficient as it could be, but it seems to work fine.
         */

    private int findTextSize(int lines) {
        Log.d("OAK", "Finding TextSize with lines: " + lines);
        float textSize = 1;
        this.setTextSize(textSize);
        this.setLines(lines);
        this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int newWidth = this.getMeasuredWidth();
        for (; newWidth / lines < this.defaultTextWidth; textSize++) {
            this.setTextSize(textSize);
            this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            newWidth = this.getMeasuredWidth();
        }
        Log.d("OAK", "Best fitting size is: " + (textSize - 4));
        if (textSize - 2 < minTextSize) {
            if (lines + 1 > maxLines) {
                return (int) (minTextSize);
            } else {
                return findTextSize(lines + 1);
            }
        } else {
            return (int) (textSize - 4);
        }
    }
}
