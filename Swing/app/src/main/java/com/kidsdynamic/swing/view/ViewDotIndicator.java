package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kidsdynamic.swing.R;

/**
 * ViewDotIndicator
 * <p>
 * Created by 03543 on 2017/2/19.
 */

// 圓點進度指示物件
public class ViewDotIndicator extends View {

    private int mDesiredSize = 50;

    private int mDotColorOn = Color.GRAY;
    private int mDotColorOff = Color.WHITE;
    private int mDotCount = 3;
    private int mDotPosition = 0;
    private int mDotSize = 12;

    private Paint mPaint;
    private Rect mRect;

    public ViewDotIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public ViewDotIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewDotIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewDotIndicator);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewDotIndicator_dotColorOn) {
                    mDotColorOn = typedArray.getColor(attr, mDotColorOn);
                } else if (attr == R.styleable.ViewDotIndicator_dotColorOff) {
                    mDotColorOff = typedArray.getColor(attr, mDotColorOff);
                } else if (attr == R.styleable.ViewDotIndicator_dotCount) {
                    mDotCount = typedArray.getInteger(attr, mDotCount);
                } else if (attr == R.styleable.ViewDotIndicator_dotPosition) {
                    mDotPosition = typedArray.getInteger(attr, mDotPosition);
                } else if (attr == R.styleable.ViewDotIndicator_dotSize) {
                    mDotSize = typedArray.getDimensionPixelSize(attr, mDotSize);
                }
            }
            typedArray.recycle();
        }

        mPaint = new Paint();
        mRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size = mDesiredSize + Math.max(getPaddingTop() + getPaddingBottom(), getPaddingStart() + getPaddingEnd());

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(size, widthSize);
        } else {
            width = size;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(size, heightSize);
        } else {
            height = size;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);

        mRect.top = ((getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) - mDotSize) / 2;
        mRect.bottom = mRect.top + mDotSize;
        mRect.left = getPaddingStart();
        mRect.right = (getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / mDotCount;

        int radius = Math.round(mDotSize / 2);
        for (int idx = 0; idx < mDotCount; idx++) {
            mPaint.setColor(idx == mDotPosition ? mDotColorOn : mDotColorOff);

            canvas.drawCircle(mRect.centerX(), mRect.centerY(), radius, mPaint);
            mRect.offset(mRect.width(), 0);
        }
    }

    public void offset(int offset) {
        int newPos = mDotPosition + offset;

        if (newPos >= mDotCount)
            newPos = newPos % mDotCount;
        if (newPos < 0)
            newPos = (newPos % mDotCount) + mDotCount;

        setDotPosition(newPos);
    }

    public void setDotColorOn(int color) {
        mDotColorOn = color;
        invalidate();
    }

    public int getDotColorOn() {
        return mDotColorOn;
    }

    public void setDotColorOff(int color) {
        mDotColorOff = color;
        invalidate();
    }

    public int getDotColorOff() {
        return mDotColorOff;
    }

    public void setDotCount(int count) {
        mDotCount = count;
        invalidate();
    }

    public int getDotCount() {
        return mDotCount;
    }

    public void setDotPosition(int pos) {
        mDotPosition = pos;
        invalidate();
    }

    public int getDotPosition() {
        return mDotPosition;
    }

    public void setDotSize(int size) {
        mDotSize = size;
        invalidate();
    }

    public int getDotSize() {
        return mDotSize;
    }

}
