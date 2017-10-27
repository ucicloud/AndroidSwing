package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.kidsdynamic.swing.R;


/**
 * Created by 03543 on 2016/12/31.
 */

// 依父物件動態計算大小之空間物件
public class ViewSponge extends View {
    private float mShrinkWidth = 1.0f;
    private float mShrinkHeight = 1.0f;

    public ViewSponge(Context context) {
        super(context);
        init(context, null);
    }

    public ViewSponge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public ViewSponge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewSponge);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewSponge_shrinkWidth) {
                    mShrinkWidth = typedArray.getFloat(attr, mShrinkWidth);
                } else if (attr == R.styleable.ViewSponge_shrinkHeight) {
                    mShrinkHeight = typedArray.getFloat(attr, mShrinkHeight);
                }
            }

            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int parentWidth = ((View) getParent()).getMeasuredWidth();
        int parentHeight = ((View) getParent()).getMeasuredHeight();

        int desiredWidth = (int) (parentWidth * mShrinkWidth);
        int desiredHeight = (int) (parentHeight * mShrinkHeight);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

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
    }
}
