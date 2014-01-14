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
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import oak.R;

/**
 * User: Michael Lake Date: 11/21/11 Time: 5:36 PM
 */
public class BeastTextView extends TextViewWithFont {

    private static final String TAG = BeastTextView.class.getSimpleName();

    private int[] mGradientColors;
    private float[] mGradientPositions;
    private float mGradientAngle = 0.0f;
    private LinearGradient mGradient;

    public BeastTextView(Context context) {
        this(context, null);
    }

    public BeastTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeastTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BeastTextView);
            if (typedArray != null) {
                try {
                    int gradientColorsResId = typedArray.getResourceId(R.styleable.BeastTextView_oakColors, -1);
                    if (gradientColorsResId != -1) {
                        String[] colors = context.getResources().getStringArray(gradientColorsResId);
                        mGradientColors = new int[colors.length];
                        for (int i = 0; i < colors.length; i++) {
                            mGradientColors[i] = Color.parseColor(colors[i]);
                        }
                    }

                    int gradientPositionsResId = typedArray.getResourceId(R.styleable.BeastTextView_oakPositions, -1);
                    if (gradientPositionsResId != -1) {
                        String[] gps = context.getResources().getStringArray(gradientPositionsResId);
                        mGradientPositions = new float[gps.length];
                        for (int i = 0; i < gps.length; i++) {
                            mGradientPositions[i] = Float.parseFloat(gps[i]);
                        }
                    }

                    mGradientAngle = typedArray.getFloat(R.styleable.BeastTextView_oakAngle, 1f);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        if (mGradient == null && mGradientColors != null && mGradientPositions != null) {
            mGradient = getGradient(getMeasuredWidth(), getMeasuredHeight(), mGradientAngle, mGradientColors,
                    mGradientPositions);
            getPaint().setShader(mGradient);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mGradient = null;
    }

    public static void setGradient(TextView tv, float angle, int[] colors, float[] positions) {
        tv.measure(tv.getLayoutParams().width, tv.getLayoutParams().height);
        LinearGradient gradient = getGradient(tv.getMeasuredWidth(), tv.getMeasuredHeight(), angle, colors, positions);
        tv.getPaint().setShader(gradient);
    }

    static LinearGradient getGradient(int measuredWidth, int measuredHeight, float angle, int[] colors,
                                      float[] positions) {
        // calculate a vector for this angle
        double rad = Math.toRadians(angle);
        double oa = Math.tan(rad);
        double x;
        double y;
        if (oa == Double.POSITIVE_INFINITY) {
            y = 1;
            x = 0;
        } else if (oa == Double.NEGATIVE_INFINITY) {
            y = -1;
            x = 0;
        } else {
            y = oa;
            if (rad > Math.PI) {
                x = -1;
            } else {
                x = 1;
            }
        }

        // using the vector, calculate the start and end points from the center of the box
        int mx = measuredWidth;
        int my = measuredHeight;
        int cx = mx / 2;
        int cy = my / 2;

        double n;
        if (x == 0) {
            n = (double) cy / y;
        } else if (y == 0) {
            n = (double) cx / x;
        } else {
            n = (double) cy / y;
            double n2 = (double) cx / x;
            if (Math.abs(n2) < Math.abs(n)) {
                n = n2;
            }
        }

        int sx = (int) (cx - n * x);
        int sy = (int) (cy - n * y);
        int ex = (int) (cx + n * x);
        int ey = (int) (cy + n * y);

        return new LinearGradient(sx, sy, ex, ey, colors, positions, Shader.TileMode.CLAMP);
    }
}
