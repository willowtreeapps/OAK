package oak.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import oak.widget.TextViewWithFont;

/**
 * User: derek Date: 8/7/12 Time: 10:06 AM
 */
public class OakUtils {

    public static void changeFonts(ViewGroup root, Activity a, String typeface) {
        Typeface tf = TextViewWithFont.getStaticTypeFace(a, typeface);

        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(tf);
            } else if (v instanceof ViewGroup) {
                changeFonts((ViewGroup) v, a, typeface);
            }
        }
    }
}
