package oak.viewswithfont.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ericrichardson on 11/10/14.
 */
public class OakFontUtils {
    private static HashMap<String, Typeface> mFontMap;

    private OakFontUtils() {
    }

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
                Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/" + fontFileName);
                mFontMap.put(fontFileName, typeface);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a formatted String to be used in places like an ActionBar
     *
     * @param context  Context for fetching Typeface
     * @param text     text to be formatted
     * @param typeface typeface to convert the text into.
     * @return formatted String.
     */
    public static SpannableString getTypefaceFormattedText(Context context, String text, String typeface) {
        //TODO Make it not crash on terrible LG devices
        SpannableString s = new SpannableString(text);
        s.setSpan(new FontTypefaceSpan(context, typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
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
}
