package com.kidsdynamic.swing.view;


import android.support.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;

/**
 * date:   2017/10/23 19:53 <br/>
 */

public class FirstEventDecorator extends EventDecorator {
    private String first_color = "#FF4081";
    private int first_offSize = 20;

    public FirstEventDecorator(@NonNull Collection<CalendarDay> dates) {
        super(dates);

        this.color = first_color;
        this.offSize = first_offSize;
    }
}
