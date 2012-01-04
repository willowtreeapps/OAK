/*
 * Copyright (c) 2011. WillowTree Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak;

import android.content.Context;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;

public class ResizedTextView extends TextViewWithFont {

    private float minTextSize;
    private int maxLines;
    private float maxBoundsWidth;
    public static final String ELLIPSE = "...";
    private String theText;
    private float lineSpacingMultiplier = 2.0f;
    private Layout textLayout;
    private Layout textLayout2;
    private int counted;
    private boolean notChecked;


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
                11);

        setEllipsize(null);
        setMaxLines(maxLines);
        theText = getText().toString();
        counted = 0;
        notChecked = true;
    }

    @Override
    public void setMaxLines(int maximumLines) {
        super.setMaxLines(maximumLines);
        maxLines = maximumLines;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //Get it's current Width, set size to 1, and get that width

        this.maxBoundsWidth = this.getMeasuredWidth();
        this.setMeasuredDimension((int) this.maxBoundsWidth, this.getMeasuredHeight());
        if (counted == 0) {
            textLayout = createWorkingLayout(theText);
            textLayout2 = textLayout;
            setTextSize(findTextSize(1, 1));
            counted++;
        }
        ellipsizeText();
    }


    /**
     * recursive function to find the correct number of lines and the text size.
     * @param numLines the number of lines the text will use
     * @param aTextSize a text size that may be the one needed
     * @return the correct text size to fit
     */
    public float findTextSize(int numLines, float aTextSize) {
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int newWidth = getMeasuredWidth();
        int textSize = (int) aTextSize;

        if (textSize <= 1) {
            for (; newWidth / numLines <= maxBoundsWidth; textSize++) {
                this.setTextSize(textSize);
                this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                newWidth = this.getMeasuredWidth();
            }

        }
        setTextSize(textSize);
        textLayout = createWorkingLayout(theText);
        findAndSet(textLayout);

        if (textSize > minTextSize) {
            if (numLines == 1 && ((textLayout2.getLineCount() > 1) || (wouldEllipse(numLines)))) {
                return findTextSize(numLines, textSize - 2);
            }
            else if (numLines == 1 && notChecked && ((textLayout2.getLineCount() > 1) || (wouldEllipse(numLines)))) {
                return findTextSize(numLines, textSize - 2);
            }
            else if (numLines <= maxLines && (wouldEllipse(numLines) || textLayout2.getLineCount() > numLines)) {
                return findTextSize(numLines, textSize - 2);
            }
            else {
                return (float) textSize;
            }
        }

        if (textSize < minTextSize) {
            if (numLines + 1 > maxLines ) {
                return minTextSize;
            }
            else if (numLines < maxLines)
            {
                setLines(numLines + 1);
                return findTextSize(numLines + 1, 1);
            }
            else if (textLayout.getLineCount() < numLines) {
                setLines(numLines + 1);
                return findTextSize(numLines + 1, minTextSize);
            }
            else {
                return minTextSize;
            }
        }
        return findTextSize(numLines, minTextSize - 2);
    }

    private boolean wouldEllipse(int numLines) {
        return textLayout.getLineCount() > numLines;
    }

    /**
     * determines if the the text goes beyond the maximum number of lines allowed and
     * cuts it off and adds an ellipse if it does.
     */
    private void ellipsizeText() {
            theText = getText().toString();
            String ellipsedText = theText;
            textLayout = createWorkingLayout(theText);
            int lastChar = textLayout.getLineEnd(maxLines);
            if (textLayout.getLineCount() > maxLines) {
                ellipsedText = ellipsedText.substring(0, textLayout.getLineEnd(maxLines) - 1);
                while (createWorkingLayout(ellipsedText + ELLIPSE).getLineCount() > maxLines) {
                    lastChar--;
                    ellipsedText = ellipsedText.substring(0, lastChar);
                }
                ellipsedText = ellipsedText + ELLIPSE;
            }
            if (!ellipsedText.equals(theText)) {
                try {
                    setText(ellipsedText);
                }
                finally {
                    //do nothing
                }
            }
        }

    private Layout createWorkingLayout(String workingText) {
        return new StaticLayout(
                workingText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(), Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, 0.0f, false);
    }
    
    private void findAndSet(Layout lay) {
        if (notChecked) {
            int startLine = 0;
            int endLine = 0;
            CharSequence longestLine = lay.getText().subSequence(startLine, endLine);
            for (int i = 0; i < lay.getLineCount() - 1; i++)
            {
                startLine = lay.getLineStart(i);
                endLine = lay.getLineEnd(i);
                CharSequence nLongestLine = lay.getText().subSequence(startLine, endLine);
                if (nLongestLine.length() > longestLine.length()) {
                    longestLine = nLongestLine;
                    textLayout2 = createWorkingLayout(longestLine.toString());
                    notChecked = false;
                }
            }
        }
    }


}


