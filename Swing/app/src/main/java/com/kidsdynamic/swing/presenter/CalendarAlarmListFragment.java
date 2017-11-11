package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.CalendarManager;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.view.AvenirTextView;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * CalendarAlarmListFragment
 */

public class CalendarAlarmListFragment extends CalendarBaseFragment {
    private View mViewMain;
    private LinearLayout mViewContainer;

    private int mLineHeight = 200;
    private int mLineMarginStart = 10;
    private int mLineMarginEnd = 10;

    private WatchEvent mEvent;
    private long kidId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLineHeight = getResources().getDisplayMetrics().heightPixels / 16;
        mLineMarginStart = getResources().getDisplayMetrics().widthPixels / 16;
        mLineMarginEnd = getResources().getDisplayMetrics().widthPixels / 16;

        kidId = getArguments().getLong("kidId");
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_left);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_calendar_alarm, container, false);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);
        
        mViewContainer = (LinearLayout) mViewMain.findViewById(R.id.calendar_alert_container);

        ButterKnife.bind(this,mViewMain);
        initTitleBar();

        //todo
//        WatchContact.Kid kid = mActivityMain.mOperator.getKid(kidId);
        KidsEntityBean kid = new DeviceManager().getKidsInfo(getContext(), kidId);
        if(kid != null && kid.getFirmwareVersion() != null && kid.getFirmwareVersion().contains("KDV01")) {
            addSeparator();
            addTitle(getResources().getString(R.string.calendar_alarm_agenda));
            addAlarm(WatchEvent.AlarmList_new[0], mLineListener);
            addTitle(getResources().getString(R.string.calendar_alarm_clock));
            addAlarm(WatchEvent.AlarmList_new[1], mLineListener);
            addTitle(getResources().getString(R.string.calendar_alarm_morning));
            addAlarm(WatchEvent.AlarmList_new[2], mLineListener);
            addAlarm(WatchEvent.AlarmList_new[3], mLineListener);
            addAlarm(WatchEvent.AlarmList_new[4], mLineListener);
            addAlarm(WatchEvent.AlarmList_new[5], mLineListener);
            addAlarm(WatchEvent.AlarmList_new[6], mLineListener);
            addTitle(getResources().getString(R.string.calendar_alarm_bed));
            addAlarm(WatchEvent.AlarmList_new[7], mLineListener);
            addAlarm(WatchEvent.AlarmList_new[8], mLineListener);
            addAlarm(WatchEvent.AlarmList_new[9], mLineListener);
            addTitle(getResources().getString(R.string.calendar_alarm_activities));
            for (int idx = 10; idx < WatchEvent.AlarmList_new.length; idx++)
                addAlarm(WatchEvent.AlarmList_new[idx], mLineListener);
        } else {
            addSeparator();
            addTitle(getResources().getString(R.string.calendar_alarm_agenda));
            addAlarm(WatchEvent.AlarmList[0], mLineListener);

            addTitle(getResources().getString(R.string.calendar_alarm_clock));
            addAlarm(WatchEvent.AlarmList[1], mLineListener);

            //晨间
            addTitle(getResources().getString(R.string.calendar_alarm_morning));
            addAlarm(WatchEvent.AlarmList[2], mLineListener);
            addAlarm(WatchEvent.AlarmList[3], mLineListener);
            addAlarm(WatchEvent.AlarmList[4], mLineListener);
            addAlarm(WatchEvent.AlarmList[5], mLineListener);
            addAlarm(WatchEvent.AlarmList[6], mLineListener);
            //就寝
            addTitle(getResources().getString(R.string.calendar_alarm_bed));
            addAlarm(WatchEvent.AlarmList[7], mLineListener);
            addAlarm(WatchEvent.AlarmList[8], mLineListener);
            addAlarm(WatchEvent.AlarmList[9], mLineListener);
            //活动
            addTitle(getResources().getString(R.string.calendar_alarm_activities));
            for (int idx = 10; idx < WatchEvent.AlarmList.length; idx++)
                addAlarm(WatchEvent.AlarmList[idx], mLineListener);
        }

        return mViewMain;
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO: 2017/11/5
        // 若由Stack非空, 則可取出處於編輯狀態下的Event
        if (!mainFrameActivity.mEventStack.isEmpty())
            mEvent = mainFrameActivity.mEventStack.pop();
        else
            mEvent = new WatchEvent();
    }

   /* @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_calendar), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, ActivityMain.RESOURCE_HIDE);
    }*/

   @OnClick(R.id.main_toolbar_title)
    public void onToolbarAction1() {
       mainFrameActivity.mEventStack.push(mEvent);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void addAlarm(WatchEvent.Alarm alarm, View.OnClickListener listener) {
        addAlarm(alarm.mId, getResources().getString(alarm.mName), alarm.mResource, listener);
    }

    // 在UI新增一個Alarm
    private void addAlarm(int id, String title, int resource, View.OnClickListener listener) {
        RelativeLayout.LayoutParams layoutParams;

        RelativeLayout viewLine = new RelativeLayout(getContext());
        viewLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mLineHeight));

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParams.setMarginStart(mLineMarginStart);

        AvenirTextView viewLabel = new AvenirTextView(getContext());
        viewLabel.setGravity(Gravity.CENTER);
        viewLabel.setText(title);
        viewLabel.setLayoutParams(layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.setMarginStart(mLineMarginEnd);

        ImageView viewIcon = new ImageView(getContext());
        viewIcon.setAdjustViewBounds(true);
        if (resource != 0)
            viewIcon.setImageResource(resource);
        viewIcon.setLayoutParams(layoutParams);

        viewLine.setTag(id);
        viewLine.addView(viewLabel);
        viewLine.addView(viewIcon);

        if (listener != null)
            viewLine.setOnClickListener(listener);

        mViewContainer.addView(viewLine);
        addSeparator();
    }

    // 在UI新增一個routine
    private void addRoutine(String label) {
        RelativeLayout.LayoutParams layoutParams;

        RelativeLayout viewLine = new RelativeLayout(getContext());
        viewLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mLineHeight));

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParams.setMarginStart(mLineMarginStart);

        TextView viewLabel = new TextView(getContext());
        viewLabel.setGravity(Gravity.CENTER);
        viewLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.color_orange_main));
        viewLabel.setTextSize(COMPLEX_UNIT_SP, 20);
        viewLabel.setTypeface(null, Typeface.BOLD);
        viewLabel.setText(label);
        viewLabel.setLayoutParams(layoutParams);

        viewLine.addView(viewLabel);

        mViewContainer.addView(viewLine);
        addSeparator();
    }

    // 新增UI上的標題列
    private void addTitle(String label) {
        RelativeLayout.LayoutParams layoutParams;

        RelativeLayout viewLine = new RelativeLayout(getContext());
        viewLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mLineHeight));

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams.setMarginStart(mLineMarginStart);

        AvenirTextView viewLabel = new AvenirTextView(getContext());
        viewLabel.setGravity(Gravity.CENTER);
        viewLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.color_orange_main));
        viewLabel.setTextSize(COMPLEX_UNIT_SP, 20);
        viewLabel.setTypeface(null, Typeface.BOLD);
        viewLabel.setText(label);
        viewLabel.setLayoutParams(layoutParams);

        viewLine.addView(viewLabel);

        mViewContainer.addView(viewLine);
        addSeparator();
    }

    // 新增UI上的分隔線
    private void addSeparator() {
        View viewSeparator = new View(getContext());
        viewSeparator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_gray_light));
        viewSeparator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) getResources().getDisplayMetrics().density));

        mViewContainer.addView(viewSeparator);
    }

    private View.OnClickListener mLineListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mEvent.mAlert = (int) view.getTag();
            Log.d("On CLicked", String.valueOf(mEvent.mAlert));
            mainFrameActivity.mEventStack.push(mEvent);

            Bundle bundle = new Bundle();
            bundle.putString(CalendarManager.KEY_DATA_TYPE,CalendarManager.VALUE_DATA_TYPE_EVENT);
            bundle.putInt(CalendarManager.KEY_SELECT_EVENT,(int) view.getTag());

            mainFrameActivity.mCalendarBundleStack.push(bundle);

            getActivity().getSupportFragmentManager().popBackStack();
        }
    };
}
