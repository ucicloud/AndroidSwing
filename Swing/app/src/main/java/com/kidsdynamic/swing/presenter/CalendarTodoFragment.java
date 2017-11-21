package com.kidsdynamic.swing.presenter;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.event.EventApi;
import com.kidsdynamic.data.net.event.model.TodoDoneEntity;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.EventManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.view.ViewTodo;
import com.kidsdynamic.swing.view.calendar.ViewCalendarCellWeek;
import com.kidsdynamic.swing.view.calendar.ViewCalendarSelector;
import com.kidsdynamic.swing.view.calendar.ViewCalendarWeek;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * CalendarTodoFragment.
 */

public class CalendarTodoFragment extends CalendarBaseFragment {
    private MainFrameActivity mActivityMain;
    private View mViewMain;

    private ViewCalendarSelector mViewSelector;
    private ViewCalendarWeek mViewCalendar;
    private TextView mViewTitle;
    private TextView mViewEventName;
    private TextView mViewDescription;
    private View mViewContainerLine;
    private LinearLayout mViewContainer;
    private View mViewButtonLine;
    private View mViewSave;
    private View mViewDelete;

    private Dialog mProcessDialog = null;

    private long mDefaultDate = System.currentTimeMillis();
    private WatchEvent mEvent;
    private long currentEventId;
    private long currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (MainFrameActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_todo, container, false);

        ButterKnife.bind(this,mViewMain);

        mViewSelector = mViewMain.findViewById(R.id.calendar_main_selector);
        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar = mViewMain.findViewById(R.id.calendar_todo_calendar);
        mViewCalendar.setOnSelectListener(mCalendarListener);

        mViewTitle = mViewMain.findViewById(R.id.calendar_todo_title);
        mViewEventName = mViewMain.findViewById(R.id.calendar_todo_name);
        mViewDescription = mViewMain.findViewById(R.id.calendar_todo_description);

        mViewContainerLine = mViewMain.findViewById(R.id.calendar_todo_container_line);
        mViewContainer = mViewMain.findViewById(R.id.calendar_todo_container);

        mViewButtonLine = mViewMain.findViewById(R.id.calendar_todo_button_line);
        mViewSave = mViewMain.findViewById(R.id.calendar_todo_save);
        mViewSave.setOnClickListener(mSaveListener);
        mViewDelete = mViewMain.findViewById(R.id.calendar_todo_del);
        mViewDelete.setOnClickListener(mDeleteListener);

        initTitleBar();

        return mViewMain;
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_left);

        view_right_action.setImageResource(R.drawable.icon_pen);
        view_right_action.setTag(R.drawable.icon_pen);

    }

   /* public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_calendar), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, R.mipmap.icon_edit);
    }*/

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.main_toolbar_action2)
    public void onToolbarAction2() {
        mActivityMain.mEventStack.push(mEvent);
        selectFragment(CalendarAddEventFragment.class.getName(), null,true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);
        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);

        // 載入需要編輯的事件
        if (mActivityMain.mEventStack.isEmpty()){
            if(currentEventId > 0){
                mEvent = EventManager.getEventById(currentEventId);
            }

            if(mEvent == null){
                //如果查询不到，根据当前界面的业务，可以认为当前event已经被del
//                mEvent = new WatchEvent();

                getFragmentManager().popBackStack();
                return;
            }
        }
        else{
            mEvent = mActivityMain.mEventStack.pop();
            currentEventId = mEvent.mId;
        }

        //del 2017年11月12日13:05:01 to-do的展示UI背景颜色固定
        /*mViewTitle.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));
        mViewDescription.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));
        mViewContainerLine.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));
        mViewButtonLine.setBackgroundColor(WatchEvent.stringToColor(mEvent.mColor));*/

        Calendar cale = Calendar.getInstance();
        cale.setTimeInMillis(mEvent.mStartDate);
        int startHour = cale.get(Calendar.HOUR_OF_DAY);
        int startMinute = cale.get(Calendar.MINUTE);
        cale.setTimeInMillis(mEvent.mEndDate);
        int endHour = cale.get(Calendar.HOUR_OF_DAY);
        int endMinute = cale.get(Calendar.MINUTE);

        String title = String.format(Locale.US, "%02d:%02d-%02d:%02d",
                startHour, startMinute, endHour, endMinute);

        mViewTitle.setText(title);
        mViewEventName.setText(mEvent.mName);
        mViewDescription.setText(mEvent.mDescription);

        for (WatchTodo todo : mEvent.mTodoList){
            addTodo(todo);
        }

        loadWeekEventList();
    }

    //load week event
    private void loadWeekEventList() {
        currentUserId = LoginManager.getCurrentLoginUserId(getContext());
        List<WatchEvent> list = EventManager.getEventList(currentUserId,
                mViewCalendar.getDateBegin(), mViewCalendar.getDateEnd());

        //add 2017年11月21日11:42:53
        //week calendar add event
        mViewCalendar.delAllEvent();
        for (WatchEvent watchEvent : list) {
            mViewCalendar.addEvent(watchEvent);
        }
    }

    @Override
    public void onPause() {
        if (mProcessDialog != null)
            mProcessDialog.dismiss();

        super.onPause();
    }

    private void addTodo(WatchTodo todo) {
        ViewTodo viewTodo = new ViewTodo(mActivityMain);
        viewTodo.setTag(todo);
        viewTodo.load(todo);
        if (todo.mStatus.equals(WatchTodo.STATUS_DONE))
            viewTodo.setLockMode();
        else
            viewTodo.setCheckMode();
        viewTodo.setOnEditListener(null);

        int height = getResources().getDisplayMetrics().heightPixels / 15;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        mViewContainer.addView(viewTodo, layoutParams);
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mViewCalendar.setDate(date);

            loadWeekEventList();
        }
    };

    private ViewCalendarWeek.OnSelectListener mCalendarListener = new ViewCalendarWeek.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarWeek calendar, ViewCalendarCellWeek cell) {
            mViewSelector.setDate(cell.getDate());
        }
    };

    private Queue<WatchTodo> doneWatchTodoQueue = new ArrayDeque<>();
    private View.OnClickListener mSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int count = mViewContainer.getChildCount();

            if(count <= 0){
                return;
            }

            doneWatchTodoQueue.clear();

            for (int idx = 0; idx < count; idx++) {
                ViewTodo viewTodo = (ViewTodo) mViewContainer.getChildAt(idx);
                WatchTodo todo = (WatchTodo) viewTodo.getTag();

                viewTodo.save(todo);
                todo.mLastUpdated = System.currentTimeMillis();
            }


            //save done status to cloud
            saveTodoItemDone();


           /* mProcessDialog = ProgressDialog.show(mActivityMain,
                    getResources().getString(R.string.calendar_todo_processing),
                    getResources().getString(R.string.calendar_todo_wait), true);
            mActivityMain.mOperator.todoDone(mTodoDoneListener, mEvent.mTodoList);*/
            //mActivityMain.mOperator.setEvent(null, mEvent);


        }
    };

    private void saveTodoItemDone() {
        showLoadingDialog(R.string.activity_main_wait);

        //首先过滤出标记为已完成的item
        for (WatchTodo watchTodo:mEvent.mTodoList) {
            if (watchTodo.mStatus.equals(WatchTodo.STATUS_DONE)) {
                doneWatchTodoQueue.add(watchTodo);
            }
        }

        //save event
        final EventApi eventApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi(EventApi.class,true);

        if(!doneWatchTodoQueue.isEmpty()){
            sendSingleTodoStatueToCloud(eventApi,doneWatchTodoQueue.peek());
        }
    }

    private void sendSingleTodoStatueToCloud(final EventApi eventApi, final WatchTodo watchTodo){
        TodoDoneEntity todoDoneEntity = new TodoDoneEntity();
        todoDoneEntity.setEventId(watchTodo.mEventId);
        todoDoneEntity.setTodoId(watchTodo.mId);

        eventApi.todoItemDone(todoDoneEntity).enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                super.onResponse(call, response);
                int code = response.code();
                if(code == 200){//向服务器保存状态成功后
                    //首先保存状态到数据库
                    EventManager.updateTodoItemStatus(watchTodo);

                    //然后删除缓存中todo item，如果依然有done to-do，则继续调用该接口
                    doneWatchTodoQueue.poll();
                    if(doneWatchTodoQueue.isEmpty()){
                        //如果全部保存了，则提示保存完成
                        finishLoadingDialog();
                        getFragmentManager().popBackStack();
                    }else {
                        sendSingleTodoStatueToCloud(eventApi,doneWatchTodoQueue.peek());
                    }
                }else{
                    finishLoadingDialog();
                    ToastCommon.showToast(getContext(),
                            getString(R.string.save_todo_done_statue_err,code));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                super.onFailure(call, t);

                finishLoadingDialog();

                ToastCommon.makeText(getContext(),R.string.cloud_api_call_net_error);

            }
        });
    }



    private View.OnClickListener mDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            showLoadingDialog(R.string.activity_main_wait);

            final EventApi eventApi = ApiGen.getInstance(getContext().getApplicationContext()).
                    generateApi(EventApi.class,true);

            eventApi.eventDelete(mEvent.mId).enqueue(new BaseRetrofitCallback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
//                    super.onResponse(call, response);

                    int code = response.code();
                    if(code == 200){//删除成功
                        //删除本地数据库中相关记录
                        EventManager.delEventById(mEvent.mId);
                        getFragmentManager().popBackStack();

                    }else if(code == 403){//无权限删除
                        ToastCommon.makeText(getContext(),
                                R.string.error_api_event_delete_403);
                    }else if(code == 400){
                        ToastCommon.makeText(getContext(),
                                R.string.error_api_event_delete_400);
                    }else {
                        ToastCommon.makeText(getContext(),
                                R.string.error_api_unknown);
                    }
                    finishLoadingDialog();
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    super.onFailure(call, t);

                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(),R.string.cloud_api_call_net_error);
                }
            });
        }
    };

}
