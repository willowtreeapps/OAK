package oak;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Woody
 * Date: 2/2/12
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResizedTextView extends TextViewWithFont {
    
    private int numMaxLines; //Maximum number of lines, default is 2 if not set in XML
    private int numMinTextSize; //Minimum text size, default is 11 if not set in XML
    private int numLines; // Hold the current number of lines
    private int numTextSize; //Hold the current text size
    
    private final Canvas resizeCanvas = new Canvas();
    
    public ResizedTextView(Context context) {
        this(context, null);
    }

    public ResizedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResizedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.numMaxLines = attrs.getAttributeIntValue(
                "http://schemas.android.com/apk/res/android",
                "maxLines",
                2);


        this.numMinTextSize = attrs.getAttributeIntValue(
                OAK.XMLNS,
                "minTextSize",
                11);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        this.numTextSize = (int)size;
    }

    @Override 
    protected  void onLayout(boolean  changed, int left, int top, int right, int bottom) {
        if(changed) {
            //Get the max height and width our text view can occupy.
            int maxWidth = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int maxHeight = (bottom - top) - getCompoundPaddingTop() - getCompoundPaddingBottom();
            
            //Get the current text, and a canvas to draw onto
            CharSequence text = this.getText();
            TextPaint textPaint = this.getPaint();
            
            //Starting at the minimum text size, keep increasing until we can no longer keep the height, or we hit the max number of lines
            int textHeight = getTextHeight(text, textPaint, maxWidth, this.numMinTextSize);

            while(textHeight < maxHeight && this.numLines <= this.numMaxLines) {
                //Log.d("OAK", "Trying text size: " + (this.numTextSize + 1));
                textHeight = getTextHeight(text, textPaint, maxWidth, this.numTextSize + 1);
                //Log.d("OAK", "Numlines: " + this.numLines);
                //Log.d("OAK", "Height is: " + textHeight + " Of " + maxHeight);
            }

            //numTextSize now contains the maximum text size, or the largest size without going over the maxLines
            //So test if we should Elipsize (AKA we hit maxLines)

            if(this.numLines > this.numMaxLines) {
                //We hit the max number of lines, so cut off some of the text
                textPaint.setTextSize(this.numTextSize);
                StaticLayout tempLayout = createWorkingLayout(text, textPaint ,maxWidth);
                tempLayout.draw(resizeCanvas);
                //EndOfLine is the position of the last element of the 2nd to last line
                int EndOfLine = tempLayout.getLineEnd(this.numLines - 1);
                //If it still doesn't fit take away a character, and try again
                while(createWorkingLayout(text.subSequence(0, EndOfLine - 4) + "...", textPaint, maxWidth).getLineCount() > this.numMaxLines) {
                    EndOfLine--;
                }
                //Log.d("OAK", "Text was: \"" + text + "\" now is: " + text.subSequence(0, EndOfLine - 4) + "...");
                setText(text.subSequence(0, EndOfLine - 4) + "...");
            }

            //Set the text size
            textPaint.setTextSize(this.numTextSize);
            setLineSpacing(0.0f, 1.0f);
        }
        super.onLayout(changed, left, right, top, bottom);    
    }
    
    private int getTextHeight(CharSequence text, TextPaint paint, int width, int textSize) {
        //Draw the Text behind the scenes to get the height
        paint.setTextSize(textSize);
        this.numTextSize = textSize;
        StaticLayout tempLayout = createWorkingLayout(text, paint, width);
        tempLayout.draw(resizeCanvas);
        this.numLines = tempLayout.getLineCount();
        return tempLayout.getHeight();
    }
    
    private StaticLayout createWorkingLayout(CharSequence text, TextPaint paint, int width) {
        return new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }
}
