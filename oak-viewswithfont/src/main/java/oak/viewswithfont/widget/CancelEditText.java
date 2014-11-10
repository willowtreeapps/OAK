/*
 * Copyright (c) 2012. WillowTree Apps, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import oak.viewswithfont.R;
import oak.viewswithfont.util.OakFontUtils;


/**
 * User: mlake Date: 12/8/11 Time: 11:01 AM
 * Allows you to set a custom resource that will
 * clear/cancel an EditText.
 */
public class CancelEditText extends EditText {
    private Drawable mDrawable;
    private Drawable[] mCompoundDrawables;


    public CancelEditText(Context context) {
        this(context, null);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public CancelEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public CancelEditText(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        int cancelDrawableId = 0;
        String fontName;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CancelEditText);
            if (typedArray != null) {
                cancelDrawableId = typedArray.getResourceId(R.styleable.CancelEditText_oakCancelDrawable, -1);
                if (cancelDrawableId != -1) {
                    mDrawable = getResources().getDrawable(cancelDrawableId);

                    addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            showOrHideCancel();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    showOrHideCancel();
                }
                try {
                    fontName = typedArray.getString(R.styleable.CancelEditText_oakFont);

                    if (fontName != null) {
                        setTypeface(OakFontUtils.getStaticTypeFace(context, fontName));
                    }
                } catch (IllegalArgumentException e) {
                    try {
                        int fontNameRes = typedArray.getResourceId(R.styleable.CancelEditText_oakFont, -1);
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

        if (isInEditMode()) return;
    }

    /**
     * We expose this method as public because calling setError(null) on Gingerbread devices will hide
     * the cancel (and other) drawables. You can call showOrHideCancel() after you call setError(null)
     * to reset the drawables.
     */
    public void showOrHideCancel() {
        setCancelVisible(getText().length() > 0);
    }

    private void setCancelVisible(boolean visible) {
        if (mCompoundDrawables == null) {
            mCompoundDrawables = getCompoundDrawables();
        }
        if (visible) {
            setCompoundDrawablesWithIntrinsicBounds(
                    mCompoundDrawables[0],
                    mCompoundDrawables[1],
                    mDrawable,
                    mCompoundDrawables[3]);

        } else {
            setCompoundDrawablesWithIntrinsicBounds(
                    mCompoundDrawables[0],
                    mCompoundDrawables[1],
                    mCompoundDrawables[2],
                    mCompoundDrawables[3]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mDrawable != null && event.getX() > getWidth() - getPaddingRight() - mDrawable
                .getIntrinsicWidth()) {
            setText("");
            setCancelVisible(false);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected boolean getDefaultEditable() {
        return true;
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return ArrowKeyMovementMethod.getInstance();
    }

    @Override
    public Editable getText() {
        return (Editable) super.getText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }

    /**
     * Convenience for {@link android.text.Selection#setSelection(android.text.Spannable, int, int)}.
     */
    public void setSelection(int start, int stop) {
        Selection.setSelection(getText(), start, stop);
    }

    /**
     * Convenience for {@link android.text.Selection#setSelection(android.text.Spannable, int)}.
     */
    public void setSelection(int index) {
        Selection.setSelection(getText(), index);
    }

    /**
     * Convenience for {@link android.text.Selection#selectAll}.
     */
    public void selectAll() {
        Selection.selectAll(getText());
    }

    /**
     * Convenience for {@link android.text.Selection#extendSelection}.
     */
    public void extendSelection(int index) {
        Selection.extendSelection(getText(), index);
    }

    @Override
    public void setEllipsize(TextUtils.TruncateAt ellipsis) {
        if (ellipsis == TextUtils.TruncateAt.MARQUEE) {
            throw new IllegalArgumentException("EditText cannot use the ellipsize mode "
                    + "TextUtils.TruncateAt.MARQUEE");
        }
        super.setEllipsize(ellipsis);
    }
}
