package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.CalendarManager;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewShape;
import com.kidsdynamic.swing.view.ViewTodo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.yy.base.utils.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_calendar_add_event, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();

        initValue();

        return mView;
    }

    private void initValue() {
        mEvent = new WatchEvent(mDefaultDate);
        mEvent.mUserId = 123;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: 2017/11/5
        //如果stack不为空，则表示stack保存当前编辑的event；
        //如果为空，则表示需要新增一个新的event
        if (!mainFrameActivity.mCalendarBundleStack.isEmpty()) {
            //如果bundle不为空，则认为有业务数据
            updateEventByFragmentBundle(mainFrameActivity.mCalendarBundleStack.pop());
        }
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
        WatchEvent.Alarm[] alertList = WatchEvent.AlarmList_new;
        /*if(mActivityMain.language.equals(Locale.JAPAN.getLanguage())) {
            alertList = WatchEvent.AlarmList_ja;
        } else {
            alertList = WatchEvent.AlarmList;
        }*/


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

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/
    }

    @OnClick(R.id.main_toolbar_action1)
    protected void onTopLeftBtnClick(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @OnClick(R.id.calendar_event_alarm_name_layout)
   protected void onClickSelectEvent(){
//       mainFrameActivity.mEventStack.push(mEvent);

       Bundle bundle = new Bundle();
//       bundle.putInt("kidId", mEvent.mKids.get(0));
       bundle.putInt("kidId", 123);
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
            minutes = 0;
        }

        TimePickerDialog dpd = TimePickerDialog.newInstance(
                mTimeSetListener,
                now.get(Calendar.HOUR),
                minutes,
                false
        );

        now.add(Calendar.MINUTE, 5);

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
                mCalendarDate.add(Calendar.MINUTE, 10);
                mEvent.mEndDate = mCalendarDate.getTimeInMillis();
            } else {
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

    @OnClick(R.id.calendar_event_color_line)
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

}
