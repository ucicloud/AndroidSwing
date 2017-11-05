package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewShape;
import com.kidsdynamic.swing.view.ViewTodo;
import com.yy.base.utils.ColorUtils;

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

    @BindView(R.id.calendar_event_todo_container)
    protected LinearLayout mViewTodoContainer;

    @BindView(R.id.calendar_event_todo_option)
    protected LinearLayout mViewTodoOption;

    @BindView(R.id.calendar_event_advance)
    protected Button mViewAdvance;

    @BindView(R.id.calendar_event_save)
    protected Button mViewSave;

    @BindView(R.id.calendar_event_description_line)
    protected View mViewDescriptionLine;

    @BindView(R.id.calendar_event_todo_line)
    protected View mViewTodoLine;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_calendar_add_event, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: 2017/11/5
        mEvent = new WatchEvent(mDefaultDate);
        mEvent.mUserId = 123;
    }

    private void initView() {
        mViewAdvance.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mViewSave.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mViewDescription.setTypeface(SwingFontsCache.getNormalType(getContext()));

        //init event color list
        for (int color : WatchEvent.ColorList){
            addColor(color);
        }
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/
    }

    @OnClick(R.id.calendar_event_alarm_name_layout)
   protected void onClickSelectEvent(){
//       mActivityMain.mEventStack.push(mEvent);

       Bundle bundle = new Bundle();
//       bundle.putInt("kidId", mEvent.mKids.get(0));
       bundle.putInt("kidId", 123);
       selectFragment(CalendarAlarmListFragment.class.getName(), bundle,true);
   }

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
