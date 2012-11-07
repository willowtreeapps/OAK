package oak.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import oak.widget.TextViewWithFont;

/**
 * User: derek Date: 8/7/12 Time: 10:06 AM
 */
public class OakUtils {

    public static void changeFonts(View root, Context ctx, String typeface) {
        Typeface tf = TextViewWithFont.getStaticTypeFace(ctx, typeface);

        if (root instanceof TextView) {
            ((TextView) root).setTypeface(tf);
        } else if (root instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                changeFonts(((ViewGroup) root).getChildAt(i), ctx, typeface);
            }
        }
    }

    public static boolean isPackageInstalled(Context ctx, String packageName) {
        try {
            ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
