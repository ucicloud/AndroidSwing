package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.EventManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.view.calendar.ViewCalendar;
import com.kidsdynamic.swing.view.calendar.ViewCalendarCellWeek;
import com.kidsdynamic.swing.view.calendar.ViewCalendarDaily;
import com.kidsdynamic.swing.view.calendar.ViewCalendarSelector;
import com.kidsdynamic.swing.view.calendar.ViewCalendarWeek;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * CalendarDailyFragment
 */

public class CalendarDailyFragment extends CalendarBaseFragment {
    private MainFrameActivity mainFrameActivity;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarWeek mViewCalendar;
    private ViewCalendarDaily mViewSchedule;

    private long mDefaultDate = -1;
    private long currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainFrameActivity = (MainFrameActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_daily, container, false);

        ButterKnife.bind(this,mViewMain);

        InputMethodManager imm = (InputMethodManager) mainFrameActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

        mViewSelector = (ViewCalendarSelector) mViewMain.findViewById(R.id.calendar_main_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = (ViewCalendarWeek) mViewMain.findViewById(R.id.calendar_daily_calendar);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        mViewSchedule = (ViewCalendarDaily) mViewMain.findViewById(R.id.calendar_daily_schedule);
        mViewSchedule.setOnSelectListener(mScheduleListener);

        initTitleBar();

        return mViewMain;
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_left);

        view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);

    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
//        mActivityMain.popFragment();
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.main_toolbar_action2)
    public void onToolbarAction2() {
//        WatchEvent event = new WatchEvent(mViewCalendar.getDate());

        WatchEvent event = EventManager.getWatchEventForAdd(mViewCalendar.getDate());

        event.mUserId = LoginManager.getCurrentLoginUserId(getContext());

        mainFrameActivity.mEventStack.push(event);
        selectFragment(CalendarAddEventFragment.class.getName(), null,true);
    }

    @Override
    public void onResume() {
        super.onResume();

        currentUserId = LoginManager.getCurrentLoginUserId(getContext());

        // 帶入的Arguments, 表示日曆需設置的日期
        if(mDefaultDate == -1){
            if (getArguments() != null){
                mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
            }else {
                mDefaultDate = System.currentTimeMillis();
            }
        }

        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);
        mViewSchedule.setDate(mDefaultDate);

        loadEventList(mDefaultDate);

        loadWeekEventList();
    }

    //load week event
    private void loadWeekEventList() {
        List<WatchEvent> list = EventManager.getEventList(currentUserId,
                mViewCalendar.getDateBegin(), mViewCalendar.getDateEnd());

        //add 2017年11月21日11:42:53
        //week calendar add event
        mViewCalendar.delAllEvent();
        for (WatchEvent watchEvent : list) {
            mViewCalendar.addEvent(watchEvent);
        }
    }

    private void loadEventList(long date) {
        mViewSchedule.delAllEvent();

        long start = ViewCalendar.stripTime(date);
        long end = start + 86400000 - 1;
        // 依起迄時間載入事件列表
        List<WatchEvent> list = EventManager.getEventList(currentUserId, start, end);;

        // 載入當日的所有事件
        for (WatchEvent event : list){
            mViewSchedule.addEvent(event);
        }
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {

            date = getFormatSelectData(date);

            mDefaultDate = date;//更新为选择日期

            mViewCalendar.setDate(date);
            mViewSchedule.setDate(date);
            loadEventList(date);

            loadWeekEventList();
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            long date = getFormatSelectData(cell.getDate());

            mDefaultDate = date;//更新为选择日期

            mViewSelector.setDate(date);
            mViewSchedule.setDate(date);
            loadEventList(date);
        }
    };

    private long getFormatSelectData(long selectData){
        Calendar calc = Calendar.getInstance();
        calc.setTimeInMillis(selectData);

        calc.set(Calendar.HOUR_OF_DAY, 6);
        calc.set(Calendar.MINUTE, 0);
        calc.set(Calendar.SECOND, 0);
        calc.set(Calendar.MILLISECOND, 0);

        return calc.getTimeInMillis();

    }

    private ViewCalendarDaily.OnSelectListener mScheduleListener = new ViewCalendarDaily.OnSelectListener() {
        @Override
        public void OnSelect(View view, WatchEvent event) {
            mainFrameActivity.mEventStack.push(event);

            //modify 2017年11月21日21:55:36 only
            //无论有没有todo，都先进入详情界面
            selectFragment(CalendarTodoFragment.class.getName(), null,true);
            /*if (event.mTodoList.size() > 0)
                selectFragment(CalendarTodoFragment.class.getName(), null,true);
            else
               selectFragment(CalendarAddEventFragment.class.getName(), null,true);*/
        }
    };
}
