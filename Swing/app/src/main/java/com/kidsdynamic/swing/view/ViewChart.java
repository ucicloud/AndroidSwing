package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.kidsdynamic.swing.R;

/**
 * Created by 03543 on 2017/2/19.
 */

// 圖表物件的基礎類別, 主要用來處理來自XML的屬性
public abstract class ViewChart extends View {

    public int mChartColor = Color.BLACK;
    public int mChartWidth = 10;
    public int mChartTextColor = Color.BLACK;
    public int mChartTextSize = 30;
    public int mChartTextStyle = Typeface.NORMAL;
    public int mNodeColor = Color.GRAY;
    public int mNodeSize = 15;
    public int mAxisColor = Color.BLACK;
    public int mAxisWidth = 6;
    public int mAxisTextSize = 30;
    public int mAxisTextColor = Color.BLACK;
    public int mAxisTextStyle = Typeface.NORMAL;
    public boolean mAxisHEnabled = false;
    public boolean mAxisVEnabled = false;

    public float mAxisHMax = 100.f;
    public float mAxisHMin = 0.f;
    public float mAxisVMax = 100.f;
    public float mAxisVMin = 0.f;

    public ViewChart(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewChart);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewChart_chartColor) {
                    mChartColor = typedArray.getColor(attr, mChartColor);
                } else if (attr == R.styleable.ViewChart_chartWidth) {
                    mChartWidth = typedArray.getDimensionPixelSize(attr, mChartWidth);
                } else if (attr == R.styleable.ViewChart_chartTextColor) {
                    mChartTextColor = typedArray.getColor(attr, mChartTextColor);
                } else if (attr == R.styleable.ViewChart_chartTextSize) {
                    mChartTextSize = typedArray.getDimensionPixelOffset(attr, mChartTextSize);
                } else if (attr == R.styleable.ViewChart_chartTextStyle) {
                    mChartTextStyle = typedArray.getInteger(attr, mChartTextStyle);
                } else if (attr == R.styleable.ViewChart_nodeColor) {
                    mNodeColor = typedArray.getColor(attr, mNodeColor);
                } else if (attr == R.styleable.ViewChart_nodeSize) {
                    mNodeSize = typedArray.getDimensionPixelSize(attr, mNodeSize);
                } else if (attr == R.styleable.ViewChart_axisColor) {
                    mAxisColor = typedArray.getColor(attr, mAxisColor);
                } else if (attr == R.styleable.ViewChart_axisWidth) {
                    mAxisWidth = typedArray.getDimensionPixelSize(attr, mAxisWidth);
                } else if (attr == R.styleable.ViewChart_axisTextColor) {
                    mAxisTextColor = typedArray.getColor(attr, mAxisTextColor);
                } else if (attr == R.styleable.ViewChart_axisTextSize) {
                    mAxisTextSize = typedArray.getDimensionPixelOffset(attr, mAxisTextSize);
                } else if (attr == R.styleable.ViewChart_axisTextStyle) {
                    mAxisTextStyle = typedArray.getInteger(attr, mAxisTextStyle);
                } else if (attr == R.styleable.ViewChart_axisHEnabled) {
                    mAxisHEnabled = typedArray.getBoolean(attr, mAxisHEnabled);
                } else if (attr == R.styleable.ViewChart_axisHMax) {
                    mAxisHMax = typedArray.getFloat(attr, mAxisHMax);
                } else if (attr == R.styleable.ViewChart_axisHMin) {
                    mAxisHMin = typedArray.getFloat(attr, mAxisHMin);
                } else if (attr == R.styleable.ViewChart_axisVEnabled) {
                    mAxisVEnabled = typedArray.getBoolean(attr, mAxisVEnabled);
                } else if (attr == R.styleable.ViewChart_axisVMax) {
                    mAxisVMax = typedArray.getFloat(attr, mAxisVMax);
                } else if (attr == R.styleable.ViewChart_axisVMin) {
                    mAxisVMin = typedArray.getFloat(attr, mAxisVMin);
                }
            }
            typedArray.recycle();
        }
    }
}
