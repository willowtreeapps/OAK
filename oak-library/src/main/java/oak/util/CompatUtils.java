package oak.util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by erichardson on 8/5/14.
 */
@SuppressWarnings("deprecation")
public class CompatUtils {
    public static void setBackground(View v, Drawable background){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            v.setBackground(background);
        }else{
            v.setBackgroundDrawable(background);
        }
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }else{
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }
}
