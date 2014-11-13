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
import android.widget.CheckBox;

import oak.util.OakUtils;
import oak.viewswithfont.R;


/**
 * Allows you to set a checkbox with custom
 * font via XML
 * <p/>
 * Start by adding xmlns:oak="http://oak/oak/schema" to the base of your view.
 * You can then specify a particular font with
 * oak:font="Your-font.ttf"
 * <p/>
 * Make sure the font you want to use is in a proper assets/fonts/ folder.
 */
public class CheckBoxWithFont extends CheckBox {

    private static final String TAG = CheckBoxWithFont.class.getSimpleName();

    public CheckBoxWithFont(Context context) {
        super(context);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public CheckBoxWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        setFont(context, attrs);
    }

    public CheckBoxWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        setFont(context, attrs);
    }

    private void setFont(Context context, AttributeSet attrs) {
        if (isInEditMode()) return;

        String fontName = null;

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxWithFont);
            if (typedArray != null) {
                try {
                    fontName = typedArray.getString(R.styleable.CheckBoxWithFont_oakFont);
                    if (fontName != null) {
                        setTypeface(OakUtils.getStaticTypeFace(context, fontName));
                    }
                } catch (IllegalArgumentException e) {
                    try {
                        int fontNameRes = typedArray.getResourceId(R.styleable.CheckBoxWithFont_oakFont, -1);
                        if (fontNameRes != -1) {
                            fontName = context.getString(fontNameRes);
                            if (fontName != null) {
                                setTypeface(OakUtils.getStaticTypeFace(context, fontName));
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
