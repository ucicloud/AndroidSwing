package com.kidsdynamic.swing.presenter;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kidsdynamic.data.net.event.model.EventUtils;
import com.kidsdynamic.data.net.event.model.TodoEntity;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.domain.EventManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.datasource.RemoteDataSource;
import com.kidsdynamic.swing.domain.viewmodel.EventViewModel;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.kidsdynamic.swing.view.calendar.ViewCalendar;
import com.kidsdynamic.swing.view.calendar.ViewCalendarCellWeek;
import com.kidsdynamic.swing.view.calendar.ViewCalendarSelector;
import com.kidsdynamic.swing.view.calendar.ViewCalendarWeek;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * calendar main
 */

public class CalendarMainFragment extends CalendarBaseFragment {

    private View mViewMain;


    @BindView(R.id.calendar_main_selector)
    protected ViewCalendarSelector mViewSelector;
    @BindView(R.id.calendar_week)
    protected ViewCalendarWeek mViewCalendarWeek;

    @BindView(R.id.calendar_main_alert_circle)
    protected ViewCircle mViewAlert;

    @BindView(R.id.calendar_main_alert_time)
    protected TextView mViewAlertTime;

    @BindView(R.id.calendar_main_alert_time_a)
    protected TextView mViewAlertTime_a;//时间的am，pm


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

    @BindView(R.id.listview_event_todo)
    protected ListView listView_todo;

    @BindView(R.id.nearby_event_layout)
    protected View layout_nearby_event;

    @BindView(R.id.layout_event_oper)
    protected View layout_event_oper;


    private long mDefaultDate = System.currentTimeMillis();

    private List<WatchEvent> mEventList;
    private List<WatchEvent> mEventListInToday;
    private WatchEvent mNearbyEven;
    private long currentUserId;

    private EventViewModel eventViewModel = null;
    private boolean isLoadEvent = false;

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

        initTitleBar();
        initCalendar();

        checkIsShowIntro();

        return mViewMain;
    }

    private void checkIsShowIntro(){
        Boolean calendarMonthFirstTime = PreferencesUtil.getInstance(getContext()).
                gPrefBooleanValue(ConfigUtil.calendar_main_first_time, true);

        if(calendarMonthFirstTime){
            if (getActivity() instanceof MainFrameActivity) {
                ((MainFrameActivity) getActivity()).showCalendarMainIntroductionUI();
            }
        }

    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_calendar);

        view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /*getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_BACK){
                    if(layout_event_detail.getVisibility() == View.VISIBLE){
                        layout_event_detail.setVisibility(View.GONE);
                        layout_event_oper.setVisibility(View.VISIBLE);

                        //切换右上角图标
                        view_right_action.setImageResource(R.drawable.icon_add);
                        view_right_action.setTag(R.drawable.icon_add);
                        *//*Intent intent = new Intent(CalendarContainerFragment.UI_Update_Action);
                        intent.putExtra(CalendarContainerFragment.UI_Update_Action_Type,
                                CalendarContainerFragment.UI_Action_Change_Event_Add);
                        SwingApplication.localBroadcastManager.sendBroadcast(intent);*//*

                        return true;
                    }
                }
                return false;
            }
        });*/
    }

    private void initCalendar(){
        //周日期选择
        mViewSelector.setOnSelectListener(mSelectorListener);

        //每天日期选择
        mViewCalendarWeek.setOnSelectListener(mCalendarListener);

        //today's schedule
        mViewToday.setOnClickListener(mTodayListener);
        //sync now
        mSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment(DashboardProgressFragment.class.getName(), null,true);
            }
        });

        ViewUtils.setBtnTypeFace(getContext(),mViewToday,
                mSyncButton);

    }


    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        //show calendar month model
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, mViewCalendarWeek.getDate());

        selectFragment(CalendarMonthFragment.class.getName(), bundle,true);
    }

    @OnClick(R.id.main_toolbar_action2)
    public void onToolbarAction2() {

        //新增event后，显示新提示layout标志
        mainFrameActivity.mCalendarSignStack.push(signal_show_sync_layout_new);

//        WatchEvent event = new WatchEvent(mViewCalendarWeek.getDate());
        WatchEvent event = EventManager.getWatchEventForAdd(mViewCalendarWeek.getDate());

        event.mUserId = currentUserId;
        mainFrameActivity.mEventStack.push(event);

        selectFragment(CalendarAddEventFragment.class.getName(),null,true);
    }

//    @OnClick(R.id.nearby_event_layout)
//    @OnClick(R.id.calendar_main_alert_event)
    protected void onNearbyEventClick(){
        if(mNearbyEven != null){
            /*layout_event_detail.setVisibility(View.VISIBLE);
            layout_event_oper.setVisibility(View.GONE);

        view_right_action.setImageResource(R.drawable.icon_pen);
        view_right_action.setTag(R.drawable.icon_pen);


        TodoListAdapter todoListAdapter = new TodoListAdapter(getContext(), getTodoEntity());
        listView_todo.setAdapter(todoListAdapter);*/
        }
    }

    public void onTodoItemClick(){

    }

    private  List<TodoEntity> getTodoEntity(){
        List<TodoEntity> todoEntityList = new ArrayList<>(4);

        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setId(123);
        todoEntity.setText("System.arraycopy(info.");
        todoEntity.setStatus(EventUtils.TODO_STATUS_PENDING);
        todoEntityList.add(todoEntity);

        TodoEntity todoEntity1 = new TodoEntity();
        todoEntity1.setId(234);
        todoEntity1.setText("getTodoEntity System.arraycopy(info.");
        todoEntity1.setStatus(EventUtils.TODO_STATUS_DONE);
        todoEntityList.add(todoEntity1);

        TodoEntity todoEntity2 = new TodoEntity();
        todoEntity2.setId(234);
        todoEntity2.setText("TodoEntity System.arraycopy(info.");
        todoEntity2.setStatus(EventUtils.TODO_STATUS_PENDING);
        todoEntityList.add(todoEntity2);

        TodoEntity todoEntity3 = new TodoEntity();
        todoEntity3.setId(2342);
        todoEntity3.setText("TodoEntity System.arraycopy(info.");
        todoEntity3.setStatus(EventUtils.TODO_STATUS_PENDING);
        todoEntityList.add(todoEntity3);

        TodoEntity todoEntity4 = new TodoEntity();
        todoEntity4.setId(2343);
        todoEntity4.setText("TodoEntity System.arraycopy(info.");
        todoEntity4.setStatus(EventUtils.TODO_STATUS_PENDING);
        todoEntityList.add(todoEntity4);

        return todoEntityList;
    }

    @Override
    public void onResume() {
        super.onResume();

       /* if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);*/
        mViewSelector.setDate(mDefaultDate);
        mViewCalendarWeek.setDate(mDefaultDate);

        refreshEventUI();

        //异步更新event--from cloud
        subscribeUI();
    }

    private void refreshEventUI() {
        long start = ViewCalendar.stripTime(mDefaultDate);
        long end = start + 86400000 - 1;

        //从cache中读取当前userid
        currentUserId = LoginManager.getCurrentLoginUserId(getContext());
//        mEventList = EventManager.getEventList(currentUserId, start, end);
        mEventList = EventManager.getEventList(currentUserId,
                mViewCalendarWeek.getDateBegin(), mViewCalendarWeek.getDateEnd());

        mEventListInToday = EventManager.getEventList(currentUserId,start, end);

        updateAlert();

        loadEvents();
    }

    private void subscribeUI() {
        Log.w("Calendar","subscribeUI");

        if (!isAdded()) {
            return;
        }

        SwingApplication appInstance = (SwingApplication) SwingApplication.getAppContext();
        EventViewModel.Factory factory = new EventViewModel.Factory(appInstance,
                RemoteDataSource.getInstance(appInstance));
        eventViewModel = ViewModelProviders.of(this, factory).get(EventViewModel.class);

        eventViewModel.getWatchEventLiveData().observe(this, new Observer<List<WatchEvent>>() {
            @Override
            public void onChanged(@Nullable List<WatchEvent> watchEvents) {
                refreshEventUI();
            }
        });

        eventViewModel.getLoadState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean == null){
                    return;
                }

                isLoadEvent = aBoolean;
            }
        });

        if(!isLoadEvent){
            eventViewModel.refreshEvent();
        }

    }

    // 更新中央coming soon的事件, 載入原則為今日即將發生的事件
    private void updateAlert() {
        Calendar cale = Calendar.getInstance();

        //只显示今日即将发生的event
        WatchEvent event = WatchEvent.earliestInDay(cale.getTimeInMillis(), mEventListInToday);

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
        String timeAmPM = "";
        String messageString;

        if (event == null) {
            timeString = "";
            messageString = getResources().getString(R.string.calendar_main_no_incoming_event);

        } else {
            Calendar cale = Calendar.getInstance();
            cale.setTimeInMillis(event.mStartDate);
//            timeString = String.format(Locale.getDefault(), "%02d:%02d", cale.get(Calendar.HOUR_OF_DAY), cale.get(Calendar.MINUTE));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm", Locale.US);
            String timeTmp = simpleDateFormat.format(event.mStartDate);
            timeString = simpleDateFormat2.format(event.mStartDate);
            messageString = event.mName;

            if(timeTmp.toLowerCase().contains("am")){
                timeAmPM = "am";
            }else {
                timeAmPM = "pm";
            }
        }

        mViewAlertTime_a.setText(timeAmPM);
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

            //为了把显示的弧形变宽，如果event的起止时间在一个小时内，则扩大到一个小时
            if(endHour == startHour){
                endHour += 1;
                endMin = 0;
            }

            int endActive = (int) (endHour * 5 + Math.floor(endMin / 12));

            mViewAlert.setStrokeBeginEnd(startActive, endActive);
        }
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mViewCalendarWeek.setDate(date);

            mEventList = EventManager.getEventList(currentUserId,
                    mViewCalendarWeek.getDateBegin(), mViewCalendarWeek.getDateEnd());

            loadEvents();
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            //2017/10/29 跳转转到每天event界面
            Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, cell.getDate());

            selectFragment(CalendarDailyFragment.class.getName(), bundle,true);
        }
    };

    private View.OnClickListener mAlertListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WatchEvent event = (WatchEvent) view.getTag();
            if (event == null)
                return;

            //点击显示当日时间线界面
            showDailyEventFragment();
           /* mainFrameActivity.mEventStack.push(event);
            selectFragment(CalendarAddEventFragment.class.getName(), null,true);*/
        }
    };

    private void showDailyEventFragment() {
        Calendar cale = Calendar.getInstance();
        long date = ViewCalendar.stripTime(cale.getTimeInMillis());

        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, date);

        selectFragment(CalendarDailyFragment.class.getName(), bundle,true);
    }

    private View.OnClickListener mTodayListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDailyEventFragment();
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
