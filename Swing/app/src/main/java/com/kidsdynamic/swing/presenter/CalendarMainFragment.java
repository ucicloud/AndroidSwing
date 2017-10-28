package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.CalendarManager;
import com.kidsdynamic.swing.view.ViewCircle;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * calendar main
 */

public class CalendarMainFragment extends BaseFragment {

    private View mViewMain;

    @BindView(R.id.main_toolbar_title)
    protected TextView tv_title;
    @BindView(R.id.main_toolbar_action1)
    protected ImageView view_left_action;
    @BindView(R.id.main_toolbar_action2)
    protected ImageView view_right_action;

    @BindView(R.id.calendarView)
    protected MaterialCalendarView calendarView;

    @BindView(R.id.calendar_main_alert_circle)
    protected ViewCircle mViewAlert;

    @BindView(R.id.calendar_main_alert_time)
    protected TextView mViewAlertTime;

    @BindView(R.id.calendar_main_alert_event)
    protected TextView mViewAlertEvent;

    @BindView(R.id.calendar_main_today)
    protected Button mViewToday;
    @BindView(R.id.calendar_main_monthly)
    protected Button mViewMonthly;
    @BindView(R.id.dashboard_main_sync)
    protected Button mSyncButton;

    private long mDefaultDate = System.currentTimeMillis();

//    private List<WatchEvent> mEventList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_main, container, false);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

        initTitleBar();
        initCalendar();

        return mViewMain;
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        view_left_action.setImageResource(R.drawable.icon_calendar);
        view_right_action.setImageResource(R.drawable.icon_add);
    }

    private void initCalendar(){
        calendarView.setWeekDayLabels(new String[]{"M","T","S","T","F","S","S"});
        calendarView.setHeaderTextAppearance(R.style.calendar_header_textappearance);
        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                StringBuilder stringBuilder = new StringBuilder();
                int years = day.getYear();
                int monthIndex = day.getMonth();
                int date = day.getDay() + 1;

                return stringBuilder.append(CalendarManager.MonthLabelMap.get(monthIndex))
                        .append(" ").append(date).append(",").append(years);
            }
        });
    }


    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        //show calendar month model
        /*Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, mViewCalendar.getDate());

        mActivityMain.selectFragment(FragmentCalendarMonth.class.getName(), bundle);*/
    }

    @OnClick(R.id.main_toolbar_action2)
    public void onToolbarAction2() {
        //todo add new event
        /*WatchEvent event = new WatchEvent(mViewCalendar.getDate());
        event.mUserId = mActivityMain.mOperator.getUser().mId;

        mActivityMain.mEventStack.push(event);
        mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);*/
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);

        long start = ViewCalendar.stripTime(mDefaultDate);
        long end = start + 86400000 - 1;
        mEventList = mActivityMain.mOperator.getEventList(start, end);*/

        updateAlert();
    }

    // 更新中央coming soon的事件, 載入原則為今日即將發生的事件
    private void updateAlert() {
        Calendar cale = Calendar.getInstance();

        /*WatchEvent event = WatchEvent.earliestInDay(cale.getTimeInMillis(), mEventList);

        setAlertMessage(event);
        setAlertClock(event);

        if (event != null) {
            mViewAlert.setTag(event);
            mViewAlert.setOnClickListener(mAlertListener);
        } else {
            mViewAlert.setOnClickListener(null);
        }*/
    }

/*    private void setAlertMessage(WatchEvent event) {
        String timeString;
        String messageString;

        if (event == null) {
            timeString = "";
            messageString = getResources().getString(R.string.calendar_main_no_incoming_event);

        } else {
            Calendar cale = Calendar.getInstance();
            cale.setTimeInMillis(event.mStartDate);
            timeString = String.format(Locale.getDefault(), "%02d:%02d", cale.get(Calendar.HOUR_OF_DAY), cale.get(Calendar.MINUTE));
            messageString = event.mName;
        }

        mViewAlertTime.setText(timeString);
        mViewAlertEvent.setText(messageString);
    }

    private void setAlertClock(WatchEvent event) {

        if (event == null) {
            mViewAlert.setActive(false);
        } else if ((event.mEndDate - event.mStartDate) >= 42480000) { // 11 Hours and 48 minute. if diff is bigger then it, active whole clock
            mViewAlert.setActive(true);
        } else {
            Calendar startCale = Calendar.getInstance();
            startCale.setTimeInMillis(event.mStartDate);

            int startHour = startCale.get(Calendar.HOUR_OF_DAY);
            int startMin = startCale.get(Calendar.MINUTE);

            startHour = startHour >= 12 ? startHour - 12 : startHour;
            int startActive = (int) (startHour * 5 + Math.floor(startMin / 12));

            Calendar endCale = Calendar.getInstance();
            endCale.setTimeInMillis(event.mEndDate);

            int endHour = endCale.get(Calendar.HOUR_OF_DAY);
            int endMin = endCale.get(Calendar.MINUTE);

            endHour = endHour >= 12 ? endHour - 12 : endHour;
            int endActive = (int) (endHour * 5 + Math.floor(endMin / 12));

            mViewAlert.setStrokeBeginEnd(startActive, endActive);
        }
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mViewCalendar.setDate(date);
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, cell.getDate());

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);
        }
    };

    private View.OnClickListener mAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchEvent event = (WatchEvent) view.getTag();
            if (event == null)
                return;

            mActivityMain.mEventStack.push(event);
            mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
        }
    };

    private View.OnClickListener mTodayListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar cale = Calendar.getInstance();
            long date = ViewCalendar.stripTime(cale.getTimeInMillis());

            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, date);

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);
        }
    };

    private View.OnClickListener mMonthlyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Calendar cale = Calendar.getInstance();
            long date = ViewCalendar.stripTime(cale.getTimeInMillis());

            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, date);

            mActivityMain.selectFragment(FragmentCalendarMonth.class.getName(), bundle);
        }
    };*/
}
