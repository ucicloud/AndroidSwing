package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.CalendarManager;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.view.calendar.ViewCalendarCellMonth;
import com.kidsdynamic.swing.view.calendar.ViewCalendarMonth;
import com.kidsdynamic.swing.view.calendar.ViewCalendarSelector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * CalendarMonthFragment
 */

public class CalendarMonthFragment extends CalendarBaseFragment {
    private View mViewMain;

    @BindView(R.id.calendar_month_selector)
    protected ViewCalendarSelector mViewSelector;
    @BindView(R.id.calendar_month_calendar)
    protected ViewCalendarMonth mViewCalendar;

    @BindView(R.id.dashboard_month_sync)
    protected Button mSyncButton;

    private long mDefaultDate = System.currentTimeMillis();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_month, container, false);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

        ButterKnife.bind(this,mViewMain);

        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewCalendar.setOnSelectListener(mCalendarListener);

        mSyncButton.setOnClickListener(mOnSynBtnClickedListener);
//        showSyncDialog();

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


    /*@Override
    public void onToolbarAction1() {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_DATE, mViewCalendar.getDate());

        mActivityMain.selectFragment(FragmentCalendarMain.class.getName(), bundle);
    }

    @Override
    public void onToolbarAction2() {
        WatchEvent event = new WatchEvent(mViewCalendar.getDate());
        event.mUserId = mActivityMain.mOperator.getUser().mId;

        mActivityMain.mEventStack.push(event);
        mActivityMain.selectFragment(FragmentCalendarEvent.class.getName(), null);
    }*/

    @Override
    public void onResume() {
        super.onResume();

        /*if (getArguments() != null)
            mDefaultDate = getArguments().getLong(BUNDLE_KEY_DATE);*/
        mViewSelector.setDate(mDefaultDate);
        mViewCalendar.setDate(mDefaultDate);

        loadEventList(mViewCalendar.getDateBegin(), mViewCalendar.getDateEnd());
    }

    private void loadEventList(long start, long end) {
        mViewCalendar.delAllEvent();

        List<WatchEvent> list = CalendarManager.getEventList(start, end);

        for (WatchEvent event : list) {
/*
            Calendar cale = Calendar.getInstance();
            cale.setTimeInMillis(event.mStartDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);
            String s = sdf.format(cale.getTimeInMillis());
            cale.setTimeInMillis(event.mEndDate);
            String e = sdf.format(cale.getTimeInMillis());
            Log.d("xxx", "Event:" + event.mName + "," + s + "--" + e );
*/
            mViewCalendar.addEvent(event);
        }
    }

    private ViewCalendarSelector.OnSelectListener mSelectorListener = new ViewCalendarSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, long offset, long date) {
            mViewCalendar.setDate(date);
            loadEventList(mViewCalendar.getDateBegin(), mViewCalendar.getDateEnd());
        }
    };

    private ViewCalendarMonth.OnSelectListener mCalendarListener = new ViewCalendarMonth.OnSelectListener() {
        @Override
        public void onSelect(ViewCalendarMonth calendar, ViewCalendarCellMonth cell) {
           /* Bundle bundle = new Bundle();
            bundle.putLong(BUNDLE_KEY_DATE, cell.getDate());

            mActivityMain.selectFragment(FragmentCalendarDaily.class.getName(), bundle);*/
        }
    };

    private Button.OnClickListener mOnSynBtnClickedListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
//            mActivityMain.selectFragment(FragmentDashboardMain.class.getName(), null);
//            mActivityMain.selectFragment(FragmentDashboardProgress.class.getName(), null);
        }
    };

}
