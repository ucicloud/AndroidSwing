package com.kidsdynamic.swing.view.calendar;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.Locale;

/**
 */

public class ViewCalendarCellWeekName extends ViewCalendarCell {

    public ViewCalendarCellWeekName(Context context) {
        super(context);
        init(context, null);
    }

    public ViewCalendarCellWeekName(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewCalendarCellWeekName(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    static final String dayName[] = new String[]{"?", "S", "M", "T", "W", "T", "F", "S"};
    static final String dayName_ru[] = new String[]{"?", "В", "П", "В", "С", "Ч", "П", "С"};
    static final String dayName_es[] = new String[]{"?", "D", "L", "M", "X", "J", "V", "S"};

    @Override
    public void setDate(long date) {
        super.setDate(date);

        Calendar calc = ViewCalendar.getInstance();
        calc.setTimeInMillis(mDate);

        int weekDay = calc.get(Calendar.DAY_OF_WEEK);
        String lang = Locale.getDefault().getLanguage();

        if (lang.equals("ru"))
            setText(dayName_ru[weekDay]);
        else if (lang.equals("es"))
            setText(dayName_es[weekDay]);
        else
            setText(dayName[weekDay]);
    }
}
