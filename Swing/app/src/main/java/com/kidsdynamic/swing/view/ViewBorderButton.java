package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.kidsdynamic.swing.R;

/**
 * Created by 03543 on 2017/2/19.
 */

// 用於FragmentDashboardChart下方的indoor, outdoor等兩個按鈕, 具底色於外框.
// 當press時, 互換底色與外框
public class ViewBorderButton extends AppCompatButton {
    private int mFloorColor = Color.GRAY;
    private int mBorderColor = Color.WHITE;
    private int mBorderWidth = 20;

    public ViewBorderButton(Context context) {
        super(context);
        init(context, null);
    }

    public ViewBorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewBorderButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewBorderButton);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewBorderButton_floorColor) {
                    mFloorColor = typedArray.getColor(attr, mFloorColor);
                } else if (attr == R.styleable.ViewBorderButton_borderColor) {
                    mBorderColor = typedArray.getColor(attr, mBorderColor);
                } else if (attr == R.styleable.ViewBorderButton_borderWidth) {
                    mBorderWidth = typedArray.getDimensionPixelSize(attr, mBorderWidth);
                }
            }
            typedArray.recycle();
        }

        makeAppearance(mFloorColor, mBorderColor, mBorderWidth);
    }

    public void makeAppearance(int floorColor, int borderColor, int borderWidth) {
        StateListDrawable background = new StateListDrawable();

        background.addState(new int[]{android.R.attr.state_pressed}, new BackgroundDrawable(borderColor, floorColor, borderWidth));
        background.addState(new int[]{android.R.attr.state_selected}, new BackgroundDrawable(borderColor, floorColor, borderWidth));
        background.addState(new int[]{}, new BackgroundDrawable(floorColor, borderColor, borderWidth));

        setBackground(background);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed, android.R.attr.state_selected},
                new int[]{android.R.attr.state_pressed, -android.R.attr.state_selected},
                new int[]{android.R.attr.state_selected},
                new int[]{}
        };

        int[] colors = new int[]{
                Color.GRAY,
                Color.GRAY,
                floorColor,
                borderColor,
        };

        setTextColor(new ColorStateList(states, colors));
    }

    public void setAppearance(int floorColor, int borderColor, int borderWidth) {
        mFloorColor = floorColor;
        mBorderColor = borderColor;
        mBorderWidth = borderWidth;

        makeAppearance(mFloorColor, mBorderColor, mBorderWidth);
    }

    public void setFloorColor(int color) {
        mFloorColor = color;
        makeAppearance(mFloorColor, mBorderColor, mBorderWidth);
    }

    public int getFloorColor() {
        return mFloorColor;
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
        makeAppearance(mFloorColor, mBorderColor, mBorderWidth);
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    private class BackgroundDrawable extends Drawable {
        private Paint mPaint;

        int mFloorColor = Color.GRAY;
        int mBorderColor = Color.WHITE;

        public BackgroundDrawable() {
            int strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    5, getResources().getDisplayMetrics());

            init(mFloorColor, mBorderColor, strokeWidth);
        }

        public BackgroundDrawable(int floorColor, int borderColor) {
            int strokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    5, getResources().getDisplayMetrics());

            init(floorColor, borderColor, strokeWidth);
        }

        public BackgroundDrawable(int floorColor, int borderColor, int strokeWidth) {
            init(floorColor, borderColor, strokeWidth);
        }

        private void init(int floorColor, int borderColor, int strokeWidth) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setStyle(Paint.Style.FILL);

            mFloorColor = floorColor;
            mBorderColor = borderColor;
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bound = getBounds();
            RectF rect = new RectF(0, 0, bound.width(), bound.height());

            int round = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    10, getResources().getDisplayMetrics());

            if (mBorderWidth > 0) {
                mPaint.setColor(mBorderColor);
                canvas.drawRoundRect(rect, round, round, mPaint);
            }

            rect.set(rect.left + mBorderWidth, rect.top + mBorderWidth,
                    rect.right - mBorderWidth, rect.bottom - mBorderWidth);
            mPaint.setColor(mFloorColor);
            canvas.drawRoundRect(rect, round, round, mPaint);
        }

        @Override
        protected boolean onLevelChange(int level) {
            invalidateSelf();
            return true;
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        public void setFloorColor(int color) {
            mFloorColor = color;
        }

        public int getFloorColor() {
            return mFloorColor;
        }

        public void setBorderColor(int color) {
            mBorderColor = color;
        }

        public int getBorderColor() {
            return mBorderColor;
        }

        public void setStrokeWidth(float strokeWidth) {
            mPaint.setStrokeWidth(strokeWidth);
        }

        public float getStrokeWidth() {
            return mPaint.getStrokeWidth();
        }
    }
}
