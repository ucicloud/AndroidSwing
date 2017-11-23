package com.kidsdynamic.swing.presenter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.event.EventApi;
import com.kidsdynamic.data.net.event.model.EventAddEntity;
import com.kidsdynamic.data.net.event.model.EventEditRep;
import com.kidsdynamic.data.net.event.model.EventInfo;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.CalendarManager;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.EventManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewShape;
import com.kidsdynamic.swing.view.ViewTodo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.yy.base.utils.ColorUtils;
import com.yy.base.utils.LogUtil;
import com.yy.base.utils.ToastCommon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * CalendarAddEventFragment
 * Created by Administrator on 2017/11/1.
 */

public class CalendarAddEventFragment extends CalendarBaseFragment {
    @BindView(R.id.calendar_event_alarm_name_layout)
    protected RelativeLayout mViewAlarmLine;

    @BindView(R.id.calendar_event_alarm_name_2)
    protected TextView mEventAlarmName;

    @BindView(R.id.calendar_event_start_time_layout)
    protected RelativeLayout mViewStartLine;

    @BindView(R.id.calendar_event_start)
    protected TextView mViewStart;

    @BindView(R.id.calendar_event_end_layout)
    protected RelativeLayout mViewEndLine;

    @BindView(R.id.calendar_event_end)
    protected TextView mViewEnd;

    @BindView(R.id.calendar_event_repeat)
    protected TextView mViewRepeat;

    @BindView(R.id.calendar_event_assign_name)
    protected TextView mViewAssignName;

    //to-do
    @BindView(R.id.calendar_event_todo_line)
    protected View mViewTodoLine;
    @BindView(R.id.calendar_event_todo_add)
    protected View mViewTodoAdd;
    @BindView(R.id.calendar_event_todo_option)
    protected LinearLayout mViewTodoOption;
    @BindView(R.id.calendar_event_todo_container)
    protected LinearLayout mViewTodoContainer;


    @BindView(R.id.calendar_event_advance)
    protected Button mViewAdvance;

    @BindView(R.id.calendar_event_save)
    protected Button mViewSave;

    @BindView(R.id.calendar_event_description_line)
    protected View mViewDescriptionLine;


    //description
    @BindView(R.id.calendar_event_description)
    protected EditText mViewDescription;

    @BindView(R.id.calendar_event_color_container)
    protected LinearLayout mViewColorContainer;

    @BindView(R.id.calendar_event_color_option)
    protected LinearLayout mViewColorOption;

    @BindView(R.id.calendar_event_color)
    protected ViewShape mViewColor;

    private WatchEvent mEvent;
    private long mDefaultDate = System.currentTimeMillis();
    private boolean isStarDate = false;
    private Calendar mCalendarDate;
    private List<KidsEntityBean> allKidsByUserId;
    private long currentUserId = -1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_calendar_add_event, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();

//        initValue();

        return mView;
    }

    private boolean initValue(long userId) {

        long focusKidsIdId = DeviceManager.getFocusKidsId();

        if(userId <= 0){
            ToastCommon.makeText(getContext(),R.string.login_data_invalid);
            getFragmentManager().popBackStack();

            return false;
        }

        //获取kids list
        allKidsByUserId = DeviceManager.getAllKidsByUserId(getContext(), userId);

        //test
        /*KidsEntityBean kidsEntityBean = new KidsEntityBean();
        kidsEntityBean.setKidsId(123);
        kidsEntityBean.setName("Alex Smith");
        kidsEntityBean.setParentId(123);
        allKidsByUserId.add(kidsEntityBean);*/

        if(ObjectUtils.isListEmpty(allKidsByUserId)){
            ToastCommon.makeText(getContext(),R.string.login_data_invalid);
            getFragmentManager().popBackStack();

            return false;
        }

        if (mEvent.mKids.size() == 0) { // assign is illegal
            if (focusKidsIdId <= 0)
                mEvent.insertKid(allKidsByUserId.get(0).getKidsId(), 0);
            else
                mEvent.insertKid(focusKidsIdId, 0);
        }


        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        //如果stack不为空，则表示stack保存当前编辑的event；
        //如果为空，则表示需要新增一个新的event
        /*if (!mainFrameActivity.mCalendarBundleStack.isEmpty()) {
            //如果bundle不为空，则认为有业务数据
            updateEventByFragmentBundle(mainFrameActivity.mCalendarBundleStack.pop());
        }*/

        // 由帶入的参数表示事件预设定的日期,显示在UI之上
        if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);

        //缓存当前登录者id
        currentUserId = LoginManager.getCurrentLoginUserId(getContext());

        // 若Stack不為空, 表示Stack中保存當前需進行編輯的事件
        // 若為空, 表示需要新增一個新的事件
        if (mainFrameActivity.mEventStack.isEmpty()) {
            mEvent = new WatchEvent(mDefaultDate);
            mEvent.mUserId = currentUserId;
            //如果初始化获取当前登录者id和focusKidsId为空，则退出该界面
           if(!initValue(mEvent.mUserId)){
               return;
           }
        } else {
            mEvent = mainFrameActivity.mEventStack.pop();
            if(!initValue(mEvent.mUserId)){
                return;
            }
        }

        loadWatchEvent();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainFrameActivity.mEventStack.push(mEvent);
    }

    private void loadWatchEvent() {
        loadDate();
        loadColor();
        loadRepeat();
        loadAssign();
        loadDescription();
        loadTodo();
        loadAlarm();

        //高级选项中，不用判断repeat
        viewAdvance(/*mEvent.mRepeat.length() != 0 ||*/
                mEvent.mDescription.length() != 0 || mEvent.mTodoList.size() != 0);
//        viewEnable(mActivityMain.mOperator.getUser().mId == mEvent.mUserId);
        ViewDelete(currentUserId == mEvent.mUserId && mEvent.mId != 0);
    }


    private void loadColor() {
        mViewColor.setColor(WatchEvent.stringToColor(mEvent.mColor));
    }

    private void loadRepeat() {
        String repeat = mEvent.mRepeat.toUpperCase();

        switch (repeat) {
            case WatchEvent.REPEAT_MONTHLY:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_monthly));
                break;

            case WatchEvent.REPEAT_WEEKLY:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_weekly));
                break;

            case WatchEvent.REPEAT_DAILY:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_daily));
                break;

            case WatchEvent.REPEAT_NEVER:
                mViewRepeat.setText(getResources().getString(R.string.event_repeat_never));
                break;
        }
    }

    private void loadAssign() {

        // 2017/11/11
        /*ArrayList<WatchContact.Kid> list = new ArrayList<>();

        list.addAll(mActivityMain.mOperator.getDeviceList());
        list.addAll(mActivityMain.mOperator.getSharedList());

        if (list.size() == 0)
            return;

        if (mEvent.mKids.size() == 0) { // assign is illegal
            WatchContact.Kid focusKid = mActivityMain.mOperator.getFocusKid();
            if (focusKid == null)
                mEvent.insertKid(list.get(0).mId, 0);
            else
                mEvent.insertKid(focusKid.mId, 0);
        }*/


        setAssign(mEvent.mKids.get(0));

    }

    private void setAssign(long kidsId) {
        if (kidsId <= 0) {
            mViewAssignName.setText("");
        } else {
            KidsEntityBean kidsByIdInCache = DeviceManager.getKidsByIdInCache(allKidsByUserId, kidsId);
            if(kidsByIdInCache != null){

                String name = kidsByIdInCache.getName();
                if (mEvent.mKids.size() > 1)
                    name += "...";
                mViewAssignName.setText(name);
            }
        }
    }

    private void loadDescription() {
        mViewDescription.setText(mEvent.mDescription);
    }

    private void loadTodo() {
        mViewTodoContainer.removeAllViews();

        for (WatchTodo todo : mEvent.mTodoList)
            addTodoView(todo);
    }

    private void updateEventByFragmentBundle(Bundle fragmentBundle) {
        if(fragmentBundle == null){
            return;
        }

        String dataType = fragmentBundle.getString(CalendarManager.KEY_DATA_TYPE);
        if(CalendarManager.VALUE_DATA_TYPE_REPEAT.equals(dataType)){
            //event repeat
            mEvent.mRepeat = fragmentBundle.getString(CalendarManager.KEY_DATA_TYPE_REPEAT_VALUE);
            mViewRepeat.setText(fragmentBundle.getString(CalendarManager.KEY_DATA_TYPE_REPEAT_STR));
        }else if(CalendarManager.VALUE_DATA_TYPE_EVENT.equals(dataType)){
            //选择的event 事件 -- which alarm selected
            mEvent.mAlert = fragmentBundle.getInt(CalendarManager.KEY_SELECT_EVENT);

            loadAlarm();
        }


    }

    private boolean loadAlarm() {
        WatchEvent.Alarm[] alertList;
        KidsEntityBean kid = new DeviceManager().getKidsInfo(getContext(), mEvent.mKids.get(0));
        if (kid != null && kid.getFirmwareVersion() != null && kid.getFirmwareVersion().contains("KDV01")) {
            alertList = WatchEvent.AlarmList_new;
        } else {
            alertList = WatchEvent.AlarmList;

        }

        for (WatchEvent.Alarm target : alertList) {
            if (target.mId == mEvent.mAlert) {
                mEvent.mAlert = target.mId;
                mEvent.mName = getResources().getString(target.mName);   // multi-dependence issue, cause from KD.
                mEventAlarmName.setText(mEvent.mName);
                return true;
            }
        }

        return false;
    }

    private void initView() {
        mViewAdvance.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mViewSave.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mViewDescription.setTypeface(SwingFontsCache.getNormalType(getContext()));
        mViewDescription.addTextChangedListener(mDescriptionWatcher);

        //init event color list
        for (int color : WatchEvent.ColorList){
            addColor(color);
        }
    }

    private TextWatcher mDescriptionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEvent.mDescription = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_left);

        view_right_action.setImageResource(R.drawable.icon_delete);
        view_right_action.setVisibility(View.INVISIBLE);
       /* view_right_action.setTag(R.drawable.icon_add);*/
    }

    @OnClick(R.id.main_toolbar_action1)
    protected void onTopLeftBtnClick(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @OnClick(R.id.main_toolbar_action2)
    protected void onTopRightBtnClick(){
        //del event
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

                }else if(code == 401){//无权限删除
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


    @OnClick(R.id.calendar_event_alarm_name_layout)
   protected void onClickSelectEvent(){
//       mainFrameActivity.mEventStack.push(mEvent);

       Bundle bundle = new Bundle();
       bundle.putLong("kidId", mEvent.mKids.get(0));
       selectFragment(CalendarAlarmListFragment.class.getName(), bundle,true);
   }

   @OnClick(R.id.calendar_event_start_time_layout)
   protected void onClickStartTimeLine(){
       isStarDate = true;
       openDatePicker();
   }

   @OnClick(R.id.calendar_event_end_layout)
   protected void onClickEndTimeLine(){
       isStarDate = false;
       openDatePicker();
   }

    public void openDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                mDateSetListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        if(mEvent.mStartDate != 0 && !isStarDate) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mEvent.mStartDate);
            dpd.setMinDate(cal);

            //设置end时间必须和start在同一天
            dpd.setMaxDate(cal);
        } else {
            dpd.setMinDate(now);
        }

        dpd.setAccentColor(ContextCompat.getColor(getContext(), R.color.color_orange_deep));
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            mCalendarDate = Calendar.getInstance();
            mCalendarDate.set(year, monthOfYear, dayOfMonth);
            openTimePicker();
        }
    };

    public void openTimePicker() {
        Calendar now = Calendar.getInstance();

        int minutes = now.get(Calendar.MINUTE);
        if(!isStarDate) {
            //如果是选择event ent time，则把当前时间设置为默认值
            now.setTimeInMillis(mEvent.mEndDate);
//            minutes = 0;
        }

        TimePickerDialog dpd = TimePickerDialog.newInstance(
                mTimeSetListener,
                now.get(Calendar.HOUR),
                minutes,
                false
        );

        //modify 2017年11月22日11:42:57 only
        //开始时间可以为当前时刻，结束时间至少是1分钟后
        if(!isStarDate){
            //如果时选择结束时间
//            now.add(Calendar.MINUTE, 5);
            now.add(Calendar.MINUTE, 1);

        }

        if(mCalendarDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                mCalendarDate.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                mCalendarDate.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) {
            dpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);
        }
        dpd.setAccentColor(ContextCompat.getColor(getContext(), R.color.color_orange_deep));
        dpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
            mCalendarDate.set(mCalendarDate.get(Calendar.YEAR), mCalendarDate.get(Calendar.MONTH), mCalendarDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);

            if(isStarDate) {
                mEvent.mStartDate = mCalendarDate.getTimeInMillis();

                //event的开始和结束时间最少间隔1分钟
//                mCalendarDate.add(Calendar.MINUTE, 10);
                mCalendarDate.add(Calendar.MINUTE, 1);
                mEvent.mEndDate = mCalendarDate.getTimeInMillis();
            } else {

                //event的start，end必须在一天内
                mEvent.mEndDate = mCalendarDate.getTimeInMillis();
            }

            loadDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.US);
            Log.d("Time-Picker -", sdf.format(mCalendarDate.getTime()));
        }
    };
    private void loadDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);

        mViewStart.setText(simpleDateFormat.format(mEvent.mStartDate));
        mViewEnd.setText(simpleDateFormat.format(mEvent.mEndDate));
    }

    //repeat
    @OnClick(R.id.calendar_event_repeat_line)
    protected void onClickEventRepeat(){
//        mainFrameActivity.mEventStack.push(mEvent);
        selectFragment(EventRepeatOptionFragment.class.getName(),null,true);
    }

    //todo 2017年11月20日22:44:38 weizg 暂不可响应
    //assign to
//    @OnClick(R.id.calendar_event_assign_line)
    protected void onClickAssignLine(){

        /*Bundle args = new Bundle();
        args.putLong(DeviceManager.BUNDLE_KEY_USER_ID,mEvent.mUserId);
        args.putLong(DeviceManager.BUNDLE_KEY_KID_ID,123);*/

//        mainFrameActivity.mEventStack.push(mEvent);
        selectFragment(EventAssignToFragment.class.getName(),null,true);
    }

    //event color layout
    private View addColor(int color) {
        ViewShape view = new ViewShape(getContext());

        int size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        view.setColor(color);
        view.setDesiredSize(size);
        view.setShape(ViewShape.SHAPE_CIRCLE);

        int padding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        view.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        layoutParams.gravity = Gravity.CENTER;

        view.setOnClickListener(mColorOptionListener);
        mViewColorContainer.addView(view, layoutParams);

        return view;
    }

    private View.OnClickListener mColorOptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewShape shape = (ViewShape) view;
            mViewColor.setColor(shape.getColor());

            mEvent.mColor = ColorUtils.colorToString(mViewColor.getColor());
        }
    };

    //del 2017年11月20日23:01:49 weizg 不响应折叠效果
//    @OnClick(R.id.calendar_event_color_line)
    protected void onViewLineClick(){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewColorOption.getLayoutParams();
        if (params.height == 0)
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        else
            params.height = 0;

        mViewColorOption.setLayoutParams(params);
    }

    @OnClick(R.id.calendar_event_todo_add)
    protected void onToDoListAddClick(){
        WatchTodo todo = new WatchTodo();
        mEvent.mTodoList.add(todo);
        addTodoView(todo);
    }

    // Advance模式.
    @OnClick(R.id.calendar_event_advance)
    protected void onAdvanceBtnClick(){
        viewAdvance(true);
    }

    private void viewAdvance(boolean visible) {
        if (visible) {
            mViewSave.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            mViewSave.requestLayout();

            mViewDescriptionLine.setVisibility(View.VISIBLE);
            mViewTodoLine.setVisibility(View.VISIBLE);
        } else {
            mViewSave.getLayoutParams().width = 0;  // by weight
            mViewSave.requestLayout();

            mViewDescriptionLine.setVisibility(View.INVISIBLE);
            mViewTodoLine.setVisibility(View.INVISIBLE);
        }

        viewTodoList(mViewTodoContainer.getChildCount() != 0);
    }

    // 啟動或關閉UI中, 刪除事件的按鈕, 若事件為新增的事件或是事件擁有者並非用戶本人,
    // 則不顯示刪除鈕, 若事件擁有人為用戶本人時.
    //title bar right btn; 如果是新增event或拥有者非用户本人，则不显示；否则显示；
    private void ViewDelete(boolean enable) {

        int visibility = enable ? View.VISIBLE : View.INVISIBLE;
        view_right_action.setVisibility(visibility);
    }

    private View addTodoView(WatchTodo todo) {
        ViewTodo viewTodo = new ViewTodo(getContext());
        viewTodo.setTag(todo);
        viewTodo.load(todo);
        viewTodo.setEditMode();
        viewTodo.setOnEditListener(mTodoEditListener);

        int height = getResources().getDisplayMetrics().heightPixels / 15;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        mViewTodoContainer.addView(viewTodo, layoutParams);

        viewTodoList(true);

        return viewTodo;
    }

    private void delTodoView(WatchTodo todo) {
        ViewTodo viewTodo = (ViewTodo) mViewTodoContainer.findViewWithTag(todo);
        mViewTodoContainer.removeView(viewTodo);

        viewTodoList(mViewTodoContainer.getChildCount() != 0);
    }

    private ViewTodo.OnEditListener mTodoEditListener = new ViewTodo.OnEditListener() {
        @Override
        public void onDelete(ViewTodo viewTodo, View view) {
            WatchTodo todo = (WatchTodo) viewTodo.getTag();
            delTodoView(todo);
            mEvent.mTodoList.remove(todo);
        }

        @Override
        public void onCheck(ViewTodo viewTodo, View view, boolean checked) {
            WatchTodo todo = (WatchTodo) viewTodo.getTag();
            viewTodo.save(todo);
        }

        @Override
        public void onText(ViewTodo viewTodo, View view, String text) {
            WatchTodo todo = (WatchTodo) viewTodo.getTag();
            viewTodo.save(todo);
        }
    };

    // 开启或关闭TodoList的编辑
    private void viewTodoList(boolean visible) {
        if (visible) {
            mViewTodoOption.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;

            int count = mViewTodoContainer.getChildCount();
            for (int idx = 0; idx < count; idx++) {
                ViewTodo viewTodo = (ViewTodo) mViewTodoContainer.getChildAt(idx);
                viewTodo.setSeparatorVisibility(idx == (count - 1) ? View.INVISIBLE : View.VISIBLE);
            }
        } else {
            mViewTodoOption.getLayoutParams().height = 0;
        }

        mViewTodoOption.requestLayout();
    }

    @OnClick(R.id.calendar_event_save)
    protected void onSaveClick(){
        if (!loadAlarm()) {
            Toast.makeText(getActivity(), R.string.calendar_event_please_select_event, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEvent.mStartDate >= mEvent.mEndDate) {
            Toast.makeText(getActivity(), R.string.event_time_select_error,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog(R.string.activity_main_wait);
        //save event

        final EventApi eventApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi(EventApi.class,true);

        EventAddEntity eventAddBean = BeanConvertor.getEventAddBean(mEvent);

        if (mEvent.mId <= 0) {
            addNewEvent(eventApi, eventAddBean);
        }else {
            LogUtil.getUtils().d("event update");
            updateEvent(eventApi);
        }

    }

    private void addNewEvent(EventApi eventApi, EventAddEntity eventAddBean) {
        eventApi.eventAdd(eventAddBean).enqueue(new BaseRetrofitCallback<EventEditRep>(){
            @Override
            public void onResponse(Call<EventEditRep> call, Response<EventEditRep> response) {

                //如果保存成功，则把event保存到本地
                if(response.code() == 200){
                    EventManager.saveEventForAdd(getContext(), response.body());

                    getFragmentManager().popBackStack();
                    showSyncDialog();
                }

                finishLoadingDialog();

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<EventEditRep> call, Throwable t) {
                super.onFailure(call, t);

                finishLoadingDialog();
            }
        });
    }

    private void updateEvent(EventApi eventApi) {
        EventInfo eventInfo4Update = BeanConvertor.getEventInfo4Update(mEvent);

        eventApi.eventUpdate(eventInfo4Update).enqueue(new BaseRetrofitCallback<EventEditRep>(){
            @Override
            public void onResponse(Call<EventEditRep> call, Response<EventEditRep> response) {

                //如果保存成功，则把event保存到本地
                if(response.code() == 200){
                    EventManager.updateEvent(getContext(), response.body());

                    getFragmentManager().popBackStack();
                    showSyncDialog();
                }else if(response.code() == 401){
                    ToastCommon.makeText(getContext(),R.string.error_api_event_update_403);
                }else {
                    String msg = getResources().getString(R.string.save_todo_done_statue_err, response.code());
                    ToastCommon.showToast(getContext(),msg);
                }

                finishLoadingDialog();

                super.onResponse(call, response);

            }

            @Override
            public void onFailure(Call<EventEditRep> call, Throwable t) {
                super.onFailure(call, t);

                finishLoadingDialog();
            }
        });
    }

    private void showSyncDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainFrameActivity,
                R.style.AppDialogStyle);
        builder.setTitle("");
        builder.setMessage(R.string.dialog_sync_content);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();

        // display dialog
        dialog.show();

        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setBackgroundColor(getResources().getColor(R.color.color_orange_main));
        nbutton.setTextColor(Color.WHITE);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) nbutton.getLayoutParams();
        params.rightMargin = 50;
        nbutton.setLayoutParams(params);

        TextView msgText = (TextView) dialog.findViewById(android.R.id.message);
        msgText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

    }

}
