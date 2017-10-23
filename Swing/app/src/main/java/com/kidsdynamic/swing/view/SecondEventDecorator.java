package com.kidsdynamic.swing.view;

import android.support.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;

/**
 * date:   2017/10/23 19:55 <br/>
 */

public class SecondEventDecorator extends EventDecorator {
    private String first_color = "#4040FF";
    private int first_offSize = 5;

    public SecondEventDecorator(@NonNull Collection<CalendarDay> dates) {
        super(dates);

        this.color = first_color;
        this.offSize = first_offSize;
    }
}
