package com.kidsdynamic.swing.domain;

import android.util.SparseArray;

import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * date:   2017/10/24 11:06 <br/>
 */

public class CalendarManager {
    public static SparseArray<String> MonthLabelMap = new SparseArray<>(12);
    static {
        MonthLabelMap.put(0,"Jan");
        MonthLabelMap.put(1,"Feb");
        MonthLabelMap.put(2,"Mar");
        MonthLabelMap.put(3,"Apr");
        MonthLabelMap.put(4,"May");
        MonthLabelMap.put(5,"Jun");
        MonthLabelMap.put(6,"Jul");
        MonthLabelMap.put(7,"Aug");
        MonthLabelMap.put(8,"Sep");
        MonthLabelMap.put(9,"Oct");
        MonthLabelMap.put(10,"Nov");
        MonthLabelMap.put(11,"Dec");
    }

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

    public static List<WatchEvent> getEventList(long start, long end){

        List<WatchEvent> list = new ArrayList<>();
            list.add(new WatchEvent(0, 452, "Name",
                    2017, 9, 28, 8, 30, 2017, 9, 28, 9, 10, WatchEvent.ColorList[0],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);
            list.get(list.size() - 1).mTodoList = Arrays.asList(
                    new WatchTodo(1, 452, 0, "1 Todo todo todo todo", WatchTodo.STATUS_DONE),
                    new WatchTodo(2, 452, 0, "2 Todo todo todo todo", WatchTodo.STATUS_PENDING)
            );

            list.add(new WatchEvent(0, 452, "Name",
                    2017, 9, 27, 10, 0, 2017, 9, 27, 10, 50, WatchEvent.ColorList[1],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

            list.add(new WatchEvent(0, 452, "Name",
                    2017, 9, 27, 8, 30, 2017, 9, 27, 11, 30, WatchEvent.ColorList[2],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

            //26 号
            list.add(new WatchEvent(0, 452, "Name",
                    2017, 9, 26, 10, 30, 2017, 9, 26, 11, 10, WatchEvent.ColorList[3],
                    "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
            list.get(list.size() - 1).mKids = Arrays.asList(8);

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 26, 10, 0, 2017, 9, 26, 10, 50, WatchEvent.ColorList[1],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList(8);

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 26, 8, 30, 2017, 9, 26, 11, 30, WatchEvent.ColorList[2],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList(8);

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 26, 9, 30, 2017, 9, 26, 11, 30, WatchEvent.ColorList[2],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList(8);


            return list;
    }

}
