package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.kidsdynamic.swing.R;

/**
 */

// 几何原型
public class ViewShape extends View {
    public final static int SHAPE_CIRCLE = 0;
    public final static int SHAPE_RECTANGLE = 1;
    public final static int SHAPE_TRIANGLE_TOP = 2;
    public final static int SHAPE_TRIANGLE_BOTTOM = 3;
    public final static int SHAPE_TRIANGLE_LEFT = 4;
    public final static int SHAPE_TRIANGLE_RIGHT = 5;

    private int mColor = Color.GRAY;
    private int mShape = SHAPE_CIRCLE;
    private int mDesiredSize = 50;

    protected Paint mPaint;
    protected Rect mRect;
    protected Path mPath;

    public ViewShape(Context context) {
        super(context);
        init(context, null);
    }

    public ViewShape(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewShape(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewShape);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewShape_color) {
                    mColor = typedArray.getColor(attr, mColor);
                } else if (attr == R.styleable.ViewShape_shape) {
                    mShape = typedArray.getInteger(attr, mShape);
                } else if (attr == R.styleable.ViewShape_desiredSize) {
                    mDesiredSize = typedArray.getDimensionPixelSize(attr, mDesiredSize);
                }
            }
            typedArray.recycle();
        }

        mPaint = new Paint();
        mRect = new Rect();
        mPath = new Path();
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

        updateRect(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);

        if (mShape == SHAPE_CIRCLE)
            paintCircle(canvas);
        else if (mShape == SHAPE_RECTANGLE)
            paintRectangle(canvas);
        else if (mShape == SHAPE_TRIANGLE_TOP)
            paintTriangleTop(canvas);
        else if (mShape == SHAPE_TRIANGLE_BOTTOM)
            paintTriangleBottom(canvas);
        else if (mShape == SHAPE_TRIANGLE_LEFT)
            paintTriangleLeft(canvas);
        else if (mShape == SHAPE_TRIANGLE_RIGHT)
            paintTriangleRight(canvas);
    }

    private void paintCircle(Canvas canvas) {
        canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height()) / 2, mPaint);
    }

    private void paintRectangle(Canvas canvas) {
        canvas.drawRect(mRect, mPaint);
    }

    private void paintTriangleTop(Canvas canvas) {
        mPath.reset();

        mPath.moveTo(mRect.left, mRect.bottom);
        mPath.lineTo(mRect.right, mRect.bottom);
        mPath.lineTo(mRect.centerX(), mRect.top);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    private void paintTriangleBottom(Canvas canvas) {
        mPath.reset();

        mPath.moveTo(mRect.left, mRect.top);
        mPath.lineTo(mRect.right, mRect.top);
        mPath.lineTo(mRect.centerX(), mRect.bottom);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    private void paintTriangleLeft(Canvas canvas) {
        mPath.reset();

        mPath.moveTo(mRect.left, mRect.top);
        mPath.lineTo(mRect.left, mRect.bottom);
        mPath.lineTo(mRect.right, mRect.centerY());
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    private void paintTriangleRight(Canvas canvas) {
        mPath.reset();

        mPath.moveTo(mRect.right, mRect.top);
        mPath.lineTo(mRect.right, mRect.bottom);
        mPath.lineTo(mRect.left, mRect.centerY());
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    private void updateRect(int width, int height) {

        int centerX = ((width - getPaddingStart() - getPaddingEnd()) / 2) + getPaddingStart();
        int centerY = ((height - getPaddingTop() - getPaddingBottom()) / 2) + getPaddingTop();
        int radius = mDesiredSize / 2;
//        int radius = (Math.min(width - getPaddingStart() - getPaddingEnd(), height - getPaddingTop() - getPaddingBottom())) / 2;

        mRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    public void setColor(int color) {
        mColor = color;
        invalidate();
    }

    public int getColor() {
        return mColor;
    }

    public void setShape(int shape) {
        mShape = shape;
        invalidate();
    }

    public int getShape() {
        return mShape;
    }

    public void setDesiredSize(int pixel) {
        mDesiredSize = pixel;
        invalidate();
    }

    public int getDesiredSize() {
        return mDesiredSize;
    }
}
