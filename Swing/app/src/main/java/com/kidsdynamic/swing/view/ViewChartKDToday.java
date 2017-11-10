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
import android.util.AttributeSet;

import java.util.Locale;

/**
 * Created by 03543 on 2017/2/20.
 */

public class ViewChartKDToday extends ViewChart {

    private int mDesiredWidth = 160;
    private int mDesiredHeight = 100;

    private float mTotal;
    private float mGoal;
    //    private WatchActivity.Act mValue;
    private int mSteps;

    private Paint mPaint;
    private Rect mRect;

    public ViewChartKDToday(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChartKDToday(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChartKDToday(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//        mValue = new WatchActivity.Act();
        mPaint = new Paint();
        mRect = new Rect();
    }

//    public void setValue(WatchActivity.Act value) {
//        mValue = new WatchActivity.Act(value);
//    }

    public void setSteps(int steps) {
        mSteps = steps;
    }

    public float getGoal() {
        return mGoal;
    }

    public void setGoal(float goal) {
        mGoal = goal;
    }

    public float getTotal() {
        return mTotal;
    }

    public void setTotal(float total) {
        mTotal = total;
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

        mRect = makeAxisXRect();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mAxisHEnabled) {
            drawAxisH(canvas, mRect);
        }

//        drawValue(canvas, mRect, mValue.mSteps);
        drawValue(canvas, mRect, mSteps);
//        drawTotal(canvas, mRect);
        drawGoal(canvas, mRect);
    }

    private void drawAxisH(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mAxisColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(rect, mPaint);
    }

    private void drawValue(Canvas canvas, Rect rect, float value) {
        Rect barRect = new Rect(rect);

        if (value > mAxisHMax)
            value = mAxisHMax;
        if (value < mAxisHMin)
            value = 0;

        barRect.right = barRect.left + (int) (value * barRect.width() / mAxisHMax);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(barRect, mPaint);

        int textX, textY;
        Rect textRect = new Rect();
        String text = String.format(Locale.US, "Step %d", (int) value);

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

    private void drawTotal(Canvas canvas, Rect rect) {
        int posX = rect.left + (int) (mTotal * rect.width() / mAxisHMax);
        int posY = rect.bottom;
        int size = mChartTextSize + 1;

        if (posX > rect.right)
            posX = rect.right;

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        path.moveTo(posX, posY);
        path.lineTo(posX - (size / 2), posY + size);
        path.lineTo(posX + (size / 2), posY + size);
        path.close();

        canvas.drawPath(path, mPaint);

        int textX, textY;
        Rect textRect = new Rect();
        String text = String.format(Locale.US, "Total %d", (int) mTotal);

        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mPaint.setTextSize(mChartTextSize + 1);
        mPaint.getTextBounds(text, 0, text.length(), textRect);
        mPaint.setColor(mChartColor);

        int textPadding = (size / 2) + 5;
        if ((posX - textRect.width() - textPadding * 2) > 0) {
            textX = posX - textPadding - textRect.width();
        } else {
            textX = posX + textPadding;
        }
        textY = posY + textRect.height() + (size - textRect.height()) / 2;

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

    private Rect makeAxisXRect() {
        Rect rect = new Rect();

        int height = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) * 2 / 5;

        rect.left = getPaddingStart();
        rect.right = getMeasuredWidth() - getPaddingEnd();
        rect.top = (getMeasuredHeight() - height) / 2;
        rect.bottom = rect.top + height;

        return rect;
    }
}
