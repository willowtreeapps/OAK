package oak.aspectratioview.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import oak.aspectratioview.R;


/**
 * User: derek Date: 3/17/14 Time: 11:17 AM
 */
public class RatioSizingUtils {

    public static class RatioSizingInfo {

        public long aspectRatioWidth = 1;
        public long aspectRatioHeight = 1;
    }

    public static class RatioMeasureInfo {

        public int width;
        public int height;
    }

    public static RatioSizingInfo getRatioSizingInfoFromAttrs(Context context, AttributeSet attrs)
            throws IllegalArgumentException {
        RatioSizingInfo rsi = new RatioSizingInfo();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout);

            String ratioString = a.getString(R.styleable.AspectRatioLayout_oakAspectRatio);
            try {
                rsi = parseRatioSizingInfo(ratioString);
            } catch (IllegalArgumentException e) {
                throw e;
            }

            a.recycle();
        }

        return rsi;
    }

    public static RatioSizingInfo parseRatioSizingInfo(String ratioString) throws IllegalArgumentException {
        RatioSizingInfo rsi = new RatioSizingInfo();

        if (TextUtils.isEmpty(ratioString)) {
            rsi.aspectRatioWidth = 1;
            rsi.aspectRatioHeight = 1;
            return rsi;
        }

        String[] parts = ratioString.split("[x:]");
        if (parts.length == 2) {
            rsi.aspectRatioWidth = Integer.parseInt(parts[0]);
            rsi.aspectRatioHeight = Integer.parseInt(parts[1]);
        } else {
            throw new IllegalArgumentException("Invalid ratio: " + ratioString);
        }

        return rsi;
    }

    public static RatioMeasureInfo getMeasureInfo(int widthMeasureSpec, int heightMeasureSpec,
                                                  RatioSizingInfo ratioSizingInfo, int widthPadding, int heightPadding) {
        RatioMeasureInfo rmi = new RatioMeasureInfo();

        rmi.width = View.MeasureSpec.getSize(widthMeasureSpec) - widthPadding;
        rmi.height = View.MeasureSpec.getSize(heightMeasureSpec) - heightPadding;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        if (rmi.height <= 0 && rmi.width <= 0 && heightMode == View.MeasureSpec.UNSPECIFIED
                && widthMode == View.MeasureSpec.UNSPECIFIED) {
            rmi.width = 0;
            rmi.height = 0;
        } else if (rmi.height <= 0 && heightMode == View.MeasureSpec.UNSPECIFIED) {
            rmi.height = (int) (rmi.width * ratioSizingInfo.aspectRatioHeight / ratioSizingInfo.aspectRatioWidth);
        } else if (rmi.width <= 0 && widthMode == View.MeasureSpec.UNSPECIFIED) {
            rmi.width = (int) (rmi.height * ratioSizingInfo.aspectRatioWidth / ratioSizingInfo.aspectRatioHeight);
        } else if (rmi.width * ratioSizingInfo.aspectRatioHeight > ratioSizingInfo.aspectRatioWidth * rmi.height) {
            rmi.width = (int) (rmi.height * ratioSizingInfo.aspectRatioWidth / ratioSizingInfo.aspectRatioHeight);
        } else {
            rmi.height = (int) (rmi.width * ratioSizingInfo.aspectRatioHeight / ratioSizingInfo.aspectRatioWidth);
        }

        return rmi;
    }
}
