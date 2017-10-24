package com.kidsdynamic.swing.presenter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.style.LineBackgroundSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.view.calendar.MultiEventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/4/15.
 */

public class TestFragment_2 extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View myAnswerView;
    View view_tobeProfessor;
    View view_myAsk;

    TextView tv_userName;
    TextView tv_myAskCount;
    TextView tv_myWealthNum;

    @BindView(R.id.calendarView)
    MaterialCalendarView materialCalendarView;

    @BindView(R.id.btn_change_style)
    Button btnChangeCalendarStyle;

    @BindView(R.id.weekView)
    WeekView weekView;

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//         super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_mine, null);
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_test_2, null);
//        layoutView.findViewById(R.id.)

        ButterKnife.bind(this,layoutView);

        initView();

        initDayEvent();
        return layoutView;
    }

    private void initDayEvent() {

        weekView.setXScrollingSpeed(0);
        weekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, 3);
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.MONTH, newMonth - 1);
                startTime.set(Calendar.YEAR, newYear);
                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, 1);
                endTime.set(Calendar.MONTH, newMonth - 1);
                WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
                event.setColor(getResources().getColor(R.color.color_blue));
                events.add(event);

                return events;
            }
        });

        setupDateTimeInterpreter(false);
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "0 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    public void changeFragmentTest(){

    }


    @OnClick(R.id.btn_change_style)
    public void testChangeStyle(){
        materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS)
                .setFirstDayOfWeek(Calendar.MONDAY).commit();
    }

    private void initView(){
        materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS)
                .setFirstDayOfWeek(Calendar.MONDAY).commit();

        for(int year = 2010; year <= 2020; year++){
            for(int month = 1; month <= 12; month ++){
                materialCalendarView.addDecorator(new MultiEventDecorator(year,month));
            }
        }



        /*CalendarDay calendarDay = CalendarDay.today();
        Log.w("TestCalendar","initView day is " + calendarDay.toString());

        List<CalendarDay> calendarDayList = new ArrayList<>();
        calendarDayList.add(calendarDay);

        CalendarDay calendarDay1 = CalendarDay.from(2017,9,22);
        calendarDayList.add(calendarDay1);

        CalendarDay calendarDay2 = CalendarDay.from(2017,9,24);
        calendarDayList.add(calendarDay2);


        List<CalendarDay> calendarDayList2 = new ArrayList<>();
        calendarDayList.add(calendarDay);

        CalendarDay calendarDay3 = CalendarDay.from(2017,9,22);
        calendarDayList2.add(calendarDay3);

        CalendarDay calendarDay4 = CalendarDay.from(2017,9,25);
        calendarDayList2.add(calendarDay4);


        materialCalendarView.addDecorator(new FirstEventDecorator(calendarDayList));
        materialCalendarView.addDecorator(new SecondEventDecorator(calendarDayList2));*/
    }


    public class EventDecorator implements DayViewDecorator{
        private String color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(String color, Collection<CalendarDay> dates){
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            Log.w("TestCalendar","day is " + day.toString());
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new AnnulusSpan(5,Color.parseColor(color)));

//            view.addSpan(new DotSpan(5,Color.parseColor("#4040FF")));
//            view.addSpan(new AnnulusSpan(5,color));
//            view.addSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")));
        }
    }

    public class AnnulusSpan implements LineBackgroundSpan{
        float radius;
        int color;

        public AnnulusSpan(float radius, int color){
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline,
                                   int bottom, CharSequence charSequence, int i5, int i6, int i7) {

            int oldColor = paint.getColor();
            if(color != 0){
                paint.setColor(color);
            }


            Log.w("TestCalendar","(left+right) " + (left+right));

            canvas.drawCircle((left+right)/2-20, bottom+radius,radius, paint);
            paint.setColor(oldColor);

           /* Paint paint1 = new Paint();
            paint1.setAntiAlias(true);
            paint1.setStyle(Paint.Style.STROKE);
//            int ringWidth = dip2px()
            paint1.setColor(color);
            paint1.setStrokeWidth(radius);

            canvas.drawCircle((right - left)/2,(bottom-top)/2+4, 8, paint1);*/
        }
    }

}
