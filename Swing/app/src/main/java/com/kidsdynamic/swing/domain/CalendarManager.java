package com.kidsdynamic.swing.domain;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>author: wzg@xdja.com <br/>
 * date:   2017/10/24 11:06 <br/>
 */

public class CalendarManager {

    /**
     * 获取指定日期中前四个event的颜色列表
     * @param calendarDay
     * @return
     */
    public static List<String> getDayFirst4EventColors(CalendarDay calendarDay){
        // TODO: 2017/10/24

        List<String> colors = new ArrayList<>(4);
        if(calendarDay.getDay() == 21){
            colors.add("#F05D25");
        }

        if(calendarDay.getDay() == 22){
            colors.add("#F05D25");
            colors.add("#FF4081");
        }

        if(calendarDay.getDay() == 23){
            colors.add("#F05D25");
            colors.add("#FF4081");
            colors.add("#20FF20");
        }

        if(calendarDay.getDay() == 24){
            colors.add("#F05D25");
            colors.add("#FF4081");
            colors.add("#20FF20");
            colors.add("#8B4513");
        }


        return colors;
    }
}
