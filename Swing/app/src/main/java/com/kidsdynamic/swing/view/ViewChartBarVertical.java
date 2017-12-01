package com.kidsdynamic.swing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.utils.SwingFontsCache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * ViewChartBarVertical
 * <p>
 * Created by 03543 on 2017/2/22.
 */

public class ViewChartBarVertical extends ViewChart {

    private int mDesiredWidth = 160;
    private int mDesiredHeight = 100;

    private Paint mPaint;
    private Rect mRect;
    private Rect mRectH;
    private Rect mRectV;

    private float mGoal;
    private List<WatchActivity.Act> mValue;
    private String mTitle = "Steps";

    private List<Rect> barRectList;
    private MotionEvent mCurrentEvent;
    private onBarClickListener onBarClickListener;

    private int padding = 18;

    public ViewChartBarVertical(Context context) {
        super(context);
        init(context, null);
    }

    public ViewChartBarVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewChartBarVertical(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mValue = new ArrayList<>();

        mPaint = new Paint();
        mRect = new Rect();
        mRectV = new Rect();
        mRectH = new Rect();

        barRectList = new ArrayList<>();
    }

    public void setValue(List<WatchActivity.Act> list) {
        mValue.clear();
        if (null != list) {
            mValue.addAll(list);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public float getGoal() {
        return mGoal;
    }

    public void setGoal(float goal) {
        mGoal = goal;
    }

    public void setOnBarClickListener(ViewChartBarVertical.onBarClickListener listener) {
        this.onBarClickListener = listener;
    }

    public interface onBarClickListener {
        void onBarClick(int index, float x, float y);
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

        mRect.set(getPaddingStart() + padding, getPaddingTop(), getMeasuredWidth() - getPaddingEnd() - padding, getMeasuredHeight());
    }

    @Override
    public void onDraw(Canvas canvas) {
        int colCount = mValue.size();
        int gapCount = colCount - 1;

        int sum = (colCount * 162) + (gapCount * 100); // 1.62:1.00 Golder ratio.

        int colWidth = mRect.width() * 162 / sum;
        int gapWidth = mRect.width() * 100 / sum;

        mRectV.set(mRect.left, mRect.top, mRect.right, mRect.top + mRect.height() * 7 / 8);
        mRectH.set(mRectV.left, mRectV.top, mRectV.left + colWidth, mRectV.bottom);

        drawTitle(canvas, mRectV, mTitle);
        drawGoal(canvas, mRectH, mRectV);

        for (int idx = 0; idx < mValue.size(); idx++) {
            drawValue(canvas, mRectH, idx, mValue.size() <= 7);
            mRectH.offset(colWidth + gapWidth, 0);
        }

        mRectV.top = mRectV.bottom;
        mRectV.bottom = mRectV.top + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mAxisWidth, getResources().getDisplayMetrics());
        drawAxisH(canvas, mRectV);

        mRectV.top = mRectV.bottom;
        mRectV.bottom = (mRect.bottom + mRectV.top) / 2;
        mRectH.set(mRectV.left, mRectV.top, mRectV.left + colWidth, mRectV.bottom);
        for (int idx = 0; idx < mValue.size(); idx++) {
            if (mValue.size() == 12) {
                if (idx == 0 || idx % 4 == 0 || idx == mValue.size() - 1) {
                    drawMonth(canvas, mRectH, idx);
                }
            } else if (mValue.size() == 7) {
                if (idx == 0 || idx == mValue.size() - 1) {
                    drawDate(canvas, mRectH, idx);
                }
            } else {
                if (idx == 0 || idx == mValue.size() - 1) {
                    drawDate(canvas, mRectH, idx);
                }
            }
            mRectH.offset(colWidth + gapWidth, 0);
        }

        mRectV.top = mRectV.bottom;
        mRectV.bottom = mRect.bottom;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        if (null == barRectList || barRectList.isEmpty()) {
            return false;
        }
        if (null == onBarClickListener || null == mCurrentEvent) {
            return false;
        }
        for (int i = 0; i < barRectList.size(); i++) {
            Rect rect = barRectList.get(i);
            if (rect.contains((int) mCurrentEvent.getX(), (int) mCurrentEvent.getY())) {
                onBarClickListener.onBarClick(i, mCurrentEvent.getX(), mCurrentEvent.getY());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                mCurrentEvent = event;
                performClick();
                break;
        }
        return true;
    }

    private void drawValue(Canvas canvas, Rect rect, int index, boolean shouldDrawValueText) {
        Rect barRect = new Rect(rect);

        int value = 0;
        if (index < mValue.size())
            value = (int) mValue.get(index).mSteps;

        int bound = value;
        if (bound > mAxisVMax)
            bound = Math.round(mAxisVMax);
        if (bound < mAxisVMin)
            bound = 0;

        barRect.top = barRect.bottom - (int) (bound * barRect.height() / mAxisVMax);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(barRect, mPaint);

        barRectList.add(index, barRect);

        if (!shouldDrawValueText) {
            return;
        }
        if (mChartTextColor == Color.TRANSPARENT)
            return;

        int textX, textY;
        Rect textRect = new Rect();
        String text = String.format(Locale.getDefault(), "%,d", (int) value);

        mPaint.setTypeface(SwingFontsCache.getNormalType(getContext()));
        mPaint.setTextSize(mChartTextSize + 1);
        mPaint.getTextBounds(text, 0, text.length(), textRect);
        mPaint.setColor(mChartTextColor);

        int textPadding = 5;
        textX = barRect.left + (barRect.width() - textRect.width()) / 2;
        if ((barRect.height() - (textPadding * 2)) > textRect.height())
            textY = barRect.top + textRect.height() + textPadding;
        else
            textY = barRect.top - textPadding;

        canvas.drawText(text, textX, textY, mPaint);
    }

    private void drawAxisH(Canvas canvas, Rect rect) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mChartColor);
        mPaint.setStyle(Paint.Style.FILL);

        rect.left = rect.left - 36;
        rect.right = rect.right + 36;
        canvas.drawRect(rect, mPaint);
    }

    private void drawMonth(Canvas canvas, Rect rect, int index) {
//        if ((index % 2) == 0)
//            return;

        Calendar cale = Calendar.getInstance();
        long date = cale.getTimeInMillis();

        if (index < mValue.size())
            date = mValue.get(index).mTimestamp;

        cale.setTimeInMillis(date);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.getDefault());
        String text = month_date.format(date);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(SwingFontsCache.getNormalType(getContext()));
        mPaint.setTextSize(mAxisTextSize + 1);
        mPaint.setColor(mChartColor);

        Rect textRect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        int textX = rect.left + rect.width() / 2;
        int textY = rect.top + mAxisTextSize + 5; // padding 5

        canvas.drawText(text, textX, textY, mPaint);
    }

    private void drawDate(Canvas canvas, Rect rect, int index) {
        Calendar cale = Calendar.getInstance();
        long date = cale.getTimeInMillis();

        if (index < mValue.size())
            date = mValue.get(index).mTimestamp;

        cale.setTimeInMillis(date);

        String text = String.format(Locale.getDefault(), "%d/%d", cale.get(Calendar.MONTH) + 1, cale.get(Calendar.DAY_OF_MONTH));

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(SwingFontsCache.getNormalType(getContext()));
        mPaint.setTextSize(mAxisTextSize + 1);
        mPaint.setColor(mChartColor);

        Rect textRect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        int textX = rect.left + rect.width() / 2;
        int textY = rect.top + mAxisTextSize + 5; // padding 5

        canvas.drawText(text, textX, textY, mPaint);
    }

    private void drawTitle(Canvas canvas, Rect rect, String title) {
        mPaint.reset();
        mPaint.setTextSize(mChartTitleTextSize + 1);
        mPaint.setColor(mChartColor);
        mPaint.setTypeface(SwingFontsCache.getBoldType(getContext()));

        Rect textRect = new Rect();
        mPaint.getTextBounds(title, 0, title.length(), textRect);

        int x = rect.left + (rect.width() - textRect.width()) / 2;
        canvas.drawText(title, x, mRect.height() / 8, mPaint);
    }

    private void drawGoal(Canvas canvas, Rect rectH, Rect rectV) {
        if (mGoal == 0.0f) {
            return;
        }
        int bound = (int) mGoal;
        if (bound > mAxisVMax)
            bound = Math.round(mAxisVMax);
        if (bound < mAxisVMin)
            bound = 0;

        int posX = rectV.left - 36;
        int posY = rectH.bottom - (int) (bound * rectH.height() / mAxisVMax);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4f);
        mPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        mPaint.setPathEffect(effects);

        Rect textRect = new Rect();
        String text = String.valueOf(bound);
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        Path dashLine = new Path();
        int lineEndX = rectV.right - textRect.width() - 36 - 20;
        dashLine.moveTo(posX, posY);
        dashLine.lineTo(lineEndX, posY);

        canvas.drawPath(dashLine, mPaint);

        Path polygon = new Path();
        polygon.moveTo(lineEndX, posY);
        polygon.lineTo(lineEndX + 20, posY - textRect.height() * 2);
        polygon.lineTo(rectV.right + 36, posY - textRect.height() * 2);
        polygon.lineTo(rectV.right + 36, posY + textRect.height() * 3 / 2);
        polygon.lineTo(lineEndX + 20, posY + textRect.height() * 3 / 2);
        polygon.lineTo(lineEndX, posY);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(polygon, mPaint);

        mPaint.reset();
        mPaint.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mPaint.setTextSize(mChartTextSize + 1);
        mPaint.setColor(Color.WHITE);
        int textX = lineEndX + 32;
        int textY = posY + textRect.height() / 2;

        canvas.drawText(text, textX, textY, mPaint);
    }

}
