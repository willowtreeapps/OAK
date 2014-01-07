package oak.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * User: derek Date: 8/7/12 Time: 10:06 AM
 */
public class OakUtils {
    private static final String TAG = OakUtils.class.getSimpleName();

    private static HashMap<String, Typeface> mFontMap;

    /**
     * @param context      Context for fetching Typeface
     * @param fontFileName Typeface to fetch. Must match a typeface name in /assets/fonts
     * @return Typeface
     */
    public static Typeface getStaticTypeFace(Context context, String fontFileName) {
        if (mFontMap == null) {
            initializeFontMap(context);
        }
        Typeface typeface = mFontMap.get(fontFileName);
        if (typeface == null) {
            throw new IllegalArgumentException(
                    "Font name must match file name in assets/fonts/ directory: " + fontFileName);
        }
        return typeface;
    }

    private static void initializeFontMap(Context context) {
        mFontMap = new HashMap<String, Typeface>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] fontFileNames = assetManager.list("fonts");
            for (String fontFileName : fontFileNames) {
                Log.d(TAG, "Found font in assets: " + fontFileName);
                Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/" + fontFileName);
                mFontMap.put(fontFileName, typeface);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Given a View, this method sets the Typeface on every TextView contained within that View
     *
     * @param root     View to check
     * @param ctx      Context for fetching Typeface
     * @param typeface Typeface to set. Must match a typeface name in /assets/fonts
     */
    public static void changeFonts(View root, Context ctx, String typeface) {
        Typeface tf = getStaticTypeFace(ctx, typeface);
        if (root instanceof TextView) {
            ((TextView) root).setTypeface(tf);
        } else if (root instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                changeFonts(((ViewGroup) root).getChildAt(i), ctx, typeface);
            }
        }
    }

    /**
     * Method that determines whether a specified package is installed on the device
     *
     * @param ctx         Context for package manager
     * @param packageName Target package name
     * @return boolean of whether packageName is installed
     */
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

    /**
     * Method to determine whether string is a valid email address
     */
    public static boolean isValidEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private static final Pattern PHONE_NUMBER = Pattern.compile("/(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]‌​)\\s*)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)([2-9]1[02-9]‌​|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})/");

    /**
     * Method to determine whether string is a valid phone number
     */
    public static boolean isValidPhone(String phone) {
        return PHONE_NUMBER.matcher(phone).matches();
    }
}
