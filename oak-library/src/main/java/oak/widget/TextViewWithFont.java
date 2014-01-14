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

package oak.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import oak.R;
import oak.util.OakUtils;

/**
 * User: Michael Lake Date: 11/21/11 Time: 5:36 PM
 */
public class TextViewWithFont extends TextView {


    public TextViewWithFont(Context context) {
        this(context, null);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public TextViewWithFont(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        if (isInEditMode()) return;

        String fontName = null;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithFont);
            if (typedArray != null) {
                try {
                    fontName = typedArray.getString(R.styleable.TextViewWithFont_oakFont);
                    if (fontName != null) {
                        setTypeface(OakUtils.getStaticTypeFace(context, fontName));
                    }
                } catch (IllegalArgumentException e) {
                    try {
                        int fontNameRes = typedArray.getResourceId(R.styleable.TextViewWithFont_oakFont, -1);
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
