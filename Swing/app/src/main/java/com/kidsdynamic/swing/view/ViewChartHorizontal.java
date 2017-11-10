package com.kidsdynamic.swing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 03543 on 2017/2/20.
 */

public class ViewChartHorizontal extends ViewChart {

    private int mDesiredWidth = 160 * 3;
    private int mDesiredHeight = 100 * 3;

    private float mGoal;
    private List<HorizontalBar> mBars = new ArrayList<>();

    private Paint mPaint;
    private Rect mRect;
    /**
     * the gap height between two bars
     */
    private int barGapHeight = 60;

    public ViewChartHorizontal(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChartHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChartHorizontal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mRect = new Rect();
    }

    public void setHorizontalBars(List<HorizontalBar> bars) {
        if (!mBars.isEmpty()) {
            mBars.clear();
        }
        mBars.addAll(bars);
    }

    public float getGoal() {
        return mGoal;
    }

    public void setGoal(float goal) {
        mGoal = goal;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = mDesiredWidth + getPaddingStart() + getPaddingEnd();
        int desiredHeight = mDesiredHeight + getPaddingTop() + getPaddingBottom();

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);

        mRect = makeChartRect();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (null == mBars || mBars.isEmpty()) {
            return;
        }
        for (int i = 0; i < mBars.size(); i++) {
            HorizontalBar bar = mBars.get(i);
            drawValue(canvas, mRect, bar, i);
        }
        drawGoal(canvas, mRect);
    }

    private void drawAxisH(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mAxisColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(rect, mPaint);
    }

    private void drawValue(Canvas canvas, Rect rect, HorizontalBar bar, int i) {
        Rect axisXRect = makeAxisRect(rect, i);
        if (mAxisHEnabled) {
            drawAxisH(canvas, axisXRect);
        }

        float value = bar.value;
        if (value > mAxisHMax)
            value = mAxisHMax;
        if (value < mAxisHMin)
            value = 0;
        Rect barRect = new Rect(axisXRect);
        barRect.right = barRect.left + (int) (value * barRect.width() / mAxisHMax);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(barRect, mPaint);

        int textX, textY;
        Rect textRect = new Rect();
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(bar.title)) {
            sb.append(bar.title);
            sb.append(" ");
        }
        sb.append(String.format(Locale.US, "%d", (int) value));
        if (!TextUtils.isEmpty(bar.unit)) {
            sb.append(" ");
            sb.append(bar.unit);
        }
        String text = sb.toString();
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mPaint.setTextSize(mChartTextSize + 1);
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        int textPadding = 20;
        if ((textRect.width() + (2 * textPadding)) > barRect.width()) {
            mPaint.setColor(mChartColor);
            textX = barRect.right + textPadding;
        } else {
            mPaint.setColor(Color.WHITE);
            textX = barRect.right - textPadding - textRect.width();
        }
        textY = barRect.bottom - ((barRect.height() - textRect.height()) / 2);

        canvas.drawText(text, textX, textY, mPaint);
    }

    private void drawGoal(Canvas canvas, Rect rect) {
        int posX = rect.left + (int) (mGoal * rect.width() / mAxisHMax);
        int posY = rect.top;
        int size = mChartTextSize + 1;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(8f);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        mPaint.setPathEffect(effects);

        Path path = new Path();
        float lineEndY = posY + rect.height() + 60;
        path.moveTo(posX, posY - 60);
        path.lineTo(posX, lineEndY);

        canvas.drawPath(path, mPaint);

        float textX, textY;
        Rect textRect = new Rect();
        String text = "Goal";

        mPaint.reset();
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mPaint.setTextSize(mChartTextSize + 1);
        mPaint.getTextBounds(text, 0, text.length(), textRect);
        mPaint.setColor(Color.RED);

        int textPadding = (size / 2) + 30;
        if ((posX + textRect.width() + textPadding * 2) < getMeasuredWidth()) {
            textX = posX - textPadding;
        } else {
            textX = posX - textPadding - textRect.width();
        }
        textY = lineEndY + textPadding;

        canvas.drawText(text, textX, textY, mPaint);

    }

    private Rect makeChartRect() {
        Rect rect = new Rect();

        int height = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) * 2 / 5;

        rect.left = getPaddingStart();
        rect.right = getMeasuredWidth() - getPaddingEnd();
        rect.top = (getMeasuredHeight() - height) / 2;
        rect.bottom = rect.top + height;

        return rect;
    }

    private Rect makeAxisRect(Rect rect, int i) {
        int barHeight = (int) (rect.height() / 3.0f);
        Rect barRect = new Rect();
        barRect.left = rect.left;
        barRect.right = rect.right;
        barRect.top = i * barHeight + i * barGapHeight;
        barRect.bottom = barRect.top + barHeight;
        return barRect;
    }

    public static class HorizontalBar {
        public String title;
        public float value;
        public String unit;
    }

}
