package com.kidsdynamic.swing.view.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.Calendar;

/**
 */

public class ViewCalendarCellWeek extends ViewCalendarCell {

    public ViewCalendarCellWeek(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarCellWeek(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarCellWeek(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    public void setDate(long milli) {
        super.setDate(milli);

        ViewCalendar calendar = getViewCalendar();
        if (calendar != null) {
            if (calendar.isSameDay(mDate) && calendar.getFocusColor() != Color.TRANSPARENT)
                setTextColor(calendar.getFocusColor());
            else if (ViewCalendar.isToday(mDate))
                setTextColor(calendar.getTodayColor());
            else
                setTextColor(calendar.getTextColor());
        }

        Calendar date = ViewCalendar.getInstance();
        date.setTimeInMillis(mDate);

        int day = date.get(Calendar.DAY_OF_MONTH);
        setText(String.valueOf(day));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ViewCalendar calendar = getViewCalendar();
        if (calendar != null && calendar.getFocusBackgroundColor() != Color.TRANSPARENT && calendar.isSameDay(mDate))
            drawFocus(canvas, calendar.getFocusBackgroundColor());

        super.onDraw(canvas);
    }

    private void drawFocus(Canvas canvas, int color) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float size = getTextSize() * 1.64f; // golden ratio.

        size = Math.min(size, width);
        size = Math.min(size, height);

        RectF rect = new RectF((width - size) / 2, (height - size) / 2, (width + size) / 2, (height + size) / 2);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(rect.centerX(), rect.centerY(), size / 2, paint);
    }
}
