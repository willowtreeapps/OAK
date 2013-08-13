package oak.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.regex.Pattern;

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

    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    public static boolean isValidEmail(String email){
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private static final Pattern PHONE_NUMBER = Pattern.compile("/(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]‌​)\\s*)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)([2-9]1[02-9]‌​|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})/");
    public static boolean isValidPhone(String phone) {
        return PHONE_NUMBER.matcher(phone).matches();
    }
}
