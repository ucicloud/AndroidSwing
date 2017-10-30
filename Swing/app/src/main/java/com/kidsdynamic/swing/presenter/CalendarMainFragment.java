package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.domain.CalendarManager;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.view.ViewCircle;
import com.kidsdynamic.swing.view.calendar.ViewCalendar;
import com.kidsdynamic.swing.view.calendar.ViewCalendarCellWeek;
import com.kidsdynamic.swing.view.calendar.ViewCalendarSelector;
import com.kidsdynamic.swing.view.calendar.ViewCalendarWeek;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * calendar main
 */

public class CalendarMainFragment extends BaseFragment {

    private View mViewMain;

    @BindView(R.id.calendar_main_selector)
    protected ViewCalendarSelector mViewSelector;
    @BindView(R.id.calendar_week)
    protected ViewCalendarWeek mViewCalendarWeek;

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

    @BindView(R.id.layout_earliest_detail)
    protected View layout_event_detail;

    @BindView(R.id.nearby_event_layout)
    protected View layout_nearby_event;

    @BindView(R.id.layout_event_oper)
    protected View layout_event_oper;


    private long mDefaultDate = System.currentTimeMillis();

    private List<WatchEvent> mEventList;
    private WatchEvent mNearbyEven;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_main, container, false);

        ButterKnife.bind(this,mViewMain);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

//        initTitleBar();
        initCalendar();

        return mViewMain;
    }

   /* private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        view_left_action.setImageResource(R.drawable.icon_calendar);
        view_right_action.setImageResource(R.drawable.icon_add);
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_BACK){
                    if(layout_event_detail.getVisibility() == View.VISIBLE){
                        layout_event_detail.setVisibility(View.GONE);
                        layout_event_oper.setVisibility(View.VISIBLE);

                        //切换右上角图标
                        Intent intent = new Intent(CalendarContainerFragment.UI_Update_Action);
                        intent.putExtra(CalendarContainerFragment.UI_Update_Action_Type,
                                CalendarContainerFragment.UI_Action_Change_Event_Add);
                        SwingApplication.localBroadcastManager.sendBroadcast(intent);

                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initCalendar(){
        //周日期选择
        mViewSelector.setOnSelectListener(mSelectorListener);

        //每天日期选择
        mViewCalendarWeek.setOnSelectListener(mCalendarListener);

    }


//    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        //show calendar month model
        /*Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, mViewCalendar.getDate());

        mActivityMain.selectFragment(FragmentCalendarMonth.class.getName(), bundle);*/
    }

//    @OnClick(R.id.main_toolbar_action2)
    public void onToolbarAction2() {
        //todo add new event
        /*WatchEvent event = new WatchEvent(mViewCalendar.getDate());
        event.mUserId = mActivityMain.mOperator.getUser().mId;

        mActivityMain.mEventStack.push(event);
        mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);*/
    }

//    @OnClick(R.id.nearby_event_layout)
    @OnClick(R.id.calendar_main_alert_event)
    protected void onNearbyEventClick(){
        // TODO: 2017/10/30 测试，不论有没有event都可以点击
//        if(mNearbyEven != null){
            layout_event_detail.setVisibility(View.VISIBLE);
            layout_event_oper.setVisibility(View.GONE);

        Intent intent = new Intent(CalendarContainerFragment.UI_Update_Action);
        intent.putExtra(CalendarContainerFragment.UI_Update_Action_Type,
                CalendarContainerFragment.UI_Action_Change_Event_detail);
        SwingApplication.localBroadcastManager.sendBroadcast(intent);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

       /* if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);*/
        mViewSelector.setDate(mDefaultDate);
        mViewCalendarWeek.setDate(mDefaultDate);

        long start = ViewCalendar.stripTime(mDefaultDate);
        long end = start + 86400000 - 1;
        mEventList = CalendarManager.getEventList(start, end);

        updateAlert();

        loadEvents();
    }

    // 更新中央coming soon的事件, 載入原則為今日即將發生的事件
    private void updateAlert() {
        Calendar cale = Calendar.getInstance();

        WatchEvent event = WatchEvent.earliestInDay(cale.getTimeInMillis(), mEventList);

        setAlertMessage(event);
        setAlertClock(event);

        if (event != null) {
            mViewAlert.setTag(event);
            mViewAlert.setOnClickListener(mAlertListener);
        } else {
            mViewAlert.setOnClickListener(null);
        }
    }

    private void loadEvents(){
        mViewCalendarWeek.delAllEvent();

        for (WatchEvent watchEvent : mEventList) {
            mViewCalendarWeek.addEvent(watchEvent);
        }
    }

    private void setAlertMessage(WatchEvent event) {
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
            mViewCalendarWeek.setDate(date);
            loadEvents();
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            // TODO: 2017/10/29 调整转到每天event界面
            /*Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, cell.getDate());

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);*/
        }
    };

    private View.OnClickListener mAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*WatchEvent event = (WatchEvent) view.getTag();
            if (event == null)
                return;

            mActivityMain.mEventStack.push(event);
            mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);*/
        }
    };

    private View.OnClickListener mTodayListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*Calendar cale = Calendar.getInstance();
            long date = ViewCalendar.stripTime(cale.getTimeInMillis());

            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, date);

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);*/
        }
    };

    private View.OnClickListener mMonthlyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*Calendar cale = Calendar.getInstance();
            long date = ViewCalendar.stripTime(cale.getTimeInMillis());

            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, date);

            mActivityMain.selectFragment(FragmentCalendarMonth.class.getName(), bundle);*/
        }
    };
}
