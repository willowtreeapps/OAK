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

package oak.viewswithfont.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

import oak.viewswithfont.R;
import oak.viewswithfont.util.OakFontUtils;


/**
 * User: Michael Lake Date: 11/21/11 Time: 5:36 PM
 * <p/>
 * Allows you to set a custom font to a button's text via XML.
 * <p/>
 * Start by adding xmlns:oak="http://oak/oak/schema" to the base of your view.
 * You can then specify a particular font with
 * oak:font="Your-font.ttf"
 * <p/>
 * Make sure the font you want to use is in a proper assets/fonts/ folder.
 */
public class ButtonWithFont extends Button {

    private static final String TAG = ButtonWithFont.class.getSimpleName();

    public ButtonWithFont(Context context) {
        super(context);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public ButtonWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        setFont(context, attrs);
    }

    public ButtonWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        setFont(context, attrs);
    }

    private void setFont(Context context, AttributeSet attrs) {
        if (isInEditMode()) return;

        String fontName = null;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonWithFont);
            if (typedArray != null) {
                try {
                    fontName = typedArray.getString(R.styleable.ButtonWithFont_oakFont);
                    if (fontName != null) {
                        setTypeface(OakFontUtils.getStaticTypeFace(context, fontName));
                    }
                } catch (IllegalArgumentException e) {
                    try {
                        int fontNameRes = typedArray.getResourceId(R.styleable.ButtonWithFont_oakFont, -1);
                        if (fontNameRes != -1) {
                            fontName = context.getString(fontNameRes);
                            if (fontName != null) {
                                setTypeface(OakFontUtils.getStaticTypeFace(context, fontName));
                            }
                        }
                    } catch (IllegalArgumentException f) {
                        f.printStackTrace();
                    }
                }
                typedArray.recycle();
            }
        }
    }
}
