package oak;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * User: mlake Date: 12/8/11 Time: 11:01 AM
 */
public class CancelEditText extends TextViewWithFont {

    private Drawable mDrawable;

    public CancelEditText(Context context) {
        this(context, null);
    }

    public CancelEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public CancelEditText(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);

        int cancelDrawableId = 0;
        if (attrs != null) {
            cancelDrawableId = attrs.getAttributeResourceValue(OAK.XMLNS, "cancelDrawable", 0);
        }

        if (cancelDrawableId != 0) {
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

    }

    private void showOrHideCancel() {
        setCancelVisible(getText().length() > 0);
    }

    private void setCancelVisible(boolean visible) {
        if (visible) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);

        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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
     * Convenience for {@link android.text.Selection#setSelection(android.text.Spannable, int,
     * int)}.
     */
    public void setSelection(int start, int stop) {
        Selection.setSelection(getText(), start, stop);
    }

    /**
     * Convenience for {@link Selection#setSelection(android.text.Spannable, int)}.
     */
    public void setSelection(int index) {
        Selection.setSelection(getText(), index);
    }

    /**
     * Convenience for {@link Selection#selectAll}.
     */
    public void selectAll() {
        Selection.selectAll(getText());
    }

    /**
     * Convenience for {@link Selection#extendSelection}.
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
