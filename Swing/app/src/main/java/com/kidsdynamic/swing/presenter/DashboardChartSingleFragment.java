package com.kidsdynamic.swing.presenter;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.BetterViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kidsdynamic.data.net.activity.model.RetrieveDataRep;
import com.kidsdynamic.data.net.activity.model.RetrieveMonthlyActivityRep;
import com.kidsdynamic.data.repository.disk.ActivityCloudDataStore;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.KidActivityManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.utils.DataUtil;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewChartBarVertical;
import com.kidsdynamic.swing.view.ViewChartToday;
import com.kidsdynamic.swing.view.ViewDotIndicator;
import com.kidsdynamic.swing.view.ViewTextSelector;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DashboardChartSingleFragment
 * <p>
 * Created by Stefan on 2017/11/9.
 */

public class DashboardChartSingleFragment extends DashboardBaseFragment {

    public static final String DOOR_TYPE = "door_type";
    public static final String CHART_TYPE = "chart_type";

    @BindView(R.id.dashboard_chart_root)
    View mViewRoot;
    @BindView(R.id.dashboard_chart_indicator)
    ViewDotIndicator mViewIndicator;
    @BindView(R.id.dashboard_chart_selector)
    ViewTextSelector mViewSelector;
    @BindView(R.id.dashboard_chart_pager)
    BetterViewPager mViewPager;
    @BindView(R.id.dashboard_chart_radio)
    RadioGroup mRadioGroup;
    @BindView(R.id.dashboard_chart_indoor)
    RadioButton mRadioButtonIndoor;
    @BindView(R.id.dashboard_chart_outdoor)
    RadioButton mRadioButtonOutdoor;

    private TextView mViewMessage;
    private ViewChartToday mViewChartToday;
    private ViewChartBarVertical mViewChartWeek;
    private ViewChartBarVertical mViewChartMonth;
    private ViewChartBarVertical mViewChartYear;

    private int mEmotion;
    private int mCurrentChart;
    private WatchActivity todayWatchActivity;
    private List<WatchActivity> weeklyWatchActivities;
    private List<WatchActivity> monthlyWatchActivities;
    private List<WatchActivity> yearlyWatchActivities;

    public static DashboardChartSingleFragment newInstance(int doorType, int chartType) {
        Bundle args = new Bundle();
        args.putInt(DOOR_TYPE, doorType);
        args.putInt(CHART_TYPE, chartType);
        DashboardChartSingleFragment fragment = new DashboardChartSingleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_chart_single, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view_left_action.setImageResource(R.drawable.icon_left);
        tv_title.setText(R.string.dashboard_chart_activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        Integer door = DataUtil.getInstance().getDoorTypeInSingleChart();
        if (null == door) {
            door = null != args ? args.getInt(DOOR_TYPE, INDOOR) : INDOOR;
        }
        mRadioButtonIndoor.setChecked(INDOOR == door);
        mRadioButtonOutdoor.setChecked(OUTDOOR == door);
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        Integer emotion = DataUtil.getInstance().getEmotionInSingleChart();
        if (null == emotion) {
            WatchActivity wa = DataUtil.getInstance().getTodayWatchActivityInSingleChart();
            emotion = getEmotionWithTodayActivity(wa);
        }

        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewSelector.clear();
        mViewSelector.add(Arrays.asList(
                getString(R.string.dashboard_chart_today),
                getString(R.string.dashboard_chart_this_week),
                getString(R.string.dashboard_chart_this_month),
                getString(R.string.dashboard_chart_this_year)));

        mViewIndicator.setDotCount(mViewSelector.getCount());
        Integer chartType = DataUtil.getInstance().getChartTypeInSingleChart();
        if (null == chartType) {
            mViewIndicator.setDotPosition(0);
            mViewSelector.select(0);
        } else {
            mViewIndicator.setDotPosition(chartType);
            mViewSelector.select(chartType);
        }

        initViewPager();

        setEmotion(emotion);

        if (null == chartType) {
            chartType = null != args ? args.getInt(CHART_TYPE, CHART_TODAY) : CHART_TODAY;
            showChart(chartType);
            mCurrentChart = chartType;
        } else {
            showChart(chartType);
            mCurrentChart = chartType;
        }
        View root = getView();
        if (null == root) {
            return;
        }
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    clearSavedData();
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        clearSavedData();
        getFragmentManager().popBackStack();
    }

    private void loadData(int chartType) {
        long kidId = 0;
        KidsEntityBean kid = DeviceManager.getFocusKidsInfo(getContext());
        if (null != kid) {
            kidId = kid.getKidsId();
        }
        if (CHART_TODAY == chartType) {
            Calendar cld = Calendar.getInstance();
            int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            long end = cld.getTimeInMillis() + timezoneOffset;

            cld.add(Calendar.DAY_OF_MONTH, -1);
            cld.add(Calendar.SECOND, 1);
            long start = cld.getTimeInMillis() + timezoneOffset;

            new KidActivityManager().retrieveDataByTime(getContext(), kidId, start, end,
                    new IRetrieveCompleteListener(chartType, kidId, start, end, timezoneOffset));
        } else if (CHART_WEEK == chartType) {
            Calendar cld = Calendar.getInstance();
            int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            long end = cld.getTimeInMillis() + timezoneOffset;

            cld.add(Calendar.DAY_OF_YEAR, -6);
            cld.set(Calendar.HOUR_OF_DAY, 0);
            cld.set(Calendar.MINUTE, 0);
            cld.set(Calendar.SECOND, 0);
            long start = cld.getTimeInMillis() + timezoneOffset;

            new KidActivityManager().retrieveDataByTime(getContext(), kidId, start, end,
                    new IRetrieveCompleteListener(chartType, kidId, start, end, timezoneOffset));
        } else if (CHART_MONTH == chartType) {
            Calendar cld = Calendar.getInstance();
            int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            long end = cld.getTimeInMillis() + timezoneOffset;

            cld.add(Calendar.DAY_OF_MONTH, -29);
            cld.set(Calendar.HOUR_OF_DAY, 0);
            cld.set(Calendar.MINUTE, 0);
            cld.set(Calendar.SECOND, 0);
            long start = cld.getTimeInMillis() + timezoneOffset;

            new KidActivityManager().retrieveDataByTime(getContext(), kidId, start, end,
                    new IRetrieveCompleteListener(chartType, kidId, start, end, timezoneOffset));
        } else {
            Calendar cld = Calendar.getInstance();
            int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            long end = cld.getTimeInMillis() + timezoneOffset;

            cld.add(Calendar.MONTH, -11);
            cld.set(Calendar.HOUR_OF_DAY, 0);
            cld.set(Calendar.MINUTE, 0);
            cld.set(Calendar.SECOND, 0);
            long start = cld.getTimeInMillis() + timezoneOffset;

            new KidActivityManager().retrieveMonthlyActivity(getContext(), kidId, start, end,
                    new IRetrieveCompleteListener(chartType, kidId, start, end, timezoneOffset));
        }
    }

    private class IRetrieveCompleteListener implements KidActivityManager.ICompleteListener {

        private long kidId;
        private int chartType;
        private long start, end, timezoneOffset;

        IRetrieveCompleteListener(int chartType, long kidId, long start, long end, long timezoneOffset) {
            this.kidId = kidId;
            this.chartType = chartType;
            this.start = start;
            this.end = end;
            this.timezoneOffset = timezoneOffset;
        }

        @Override
        public void onComplete(Object arg, int statusCode) {
            if (200 == statusCode && null != arg) {
                if (CHART_TODAY == chartType && arg instanceof RetrieveDataRep) {
                    new TodayTask(DashboardChartSingleFragment.this)
                            .execute(chartType, arg, start, end, timezoneOffset, kidId);
                } else if ((CHART_WEEK == chartType || CHART_MONTH == chartType) && arg instanceof RetrieveDataRep) {
                    new WeeklyAndMonthlyTask(DashboardChartSingleFragment.this)
                            .execute(chartType, arg, start, end, timezoneOffset, kidId);
                } else if (CHART_YEAR == chartType && arg instanceof RetrieveMonthlyActivityRep) {
                    new YearlyTask(DashboardChartSingleFragment.this)
                            .execute(chartType, arg, start, end, timezoneOffset, kidId);
                }
            } else {
//                ToastCommon.makeText(SwingApplication.getAppContext(), R.string.dashboard_enqueue_fail_common);
            }
        }

        @Override
        public void onFailed(String Command, int statusCode) {
//            ToastCommon.showToast(SwingApplication.getAppContext(), Command);
        }
    }

    private static class TodayTask extends AsyncTask<Object, Integer, WatchActivity> {

        private DashboardChartSingleFragment theFragment;

        TodayTask(DashboardChartSingleFragment instance) {
            WeakReference<DashboardChartSingleFragment> wr = new WeakReference<>(instance);
            theFragment = wr.get();
        }

        @Override
        protected WatchActivity doInBackground(Object... params) {
            int chartType = (int) params[0];
            RetrieveDataRep rep = (RetrieveDataRep) params[1];
            WatchActivity act = new WatchActivity((Long) params[5], 0);
            List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();

            if (null == activitiesEntities || activitiesEntities.isEmpty()) {
                return act;
            }

            long start = (Long) params[2];
            long end = (Long) params[3];
            long timezoneOffset = (Long) params[4];
            for (RetrieveDataRep.ActivitiesEntity entity : activitiesEntities) {
                long receiveDate = BeanConvertor.getLocalTimeStamp(entity.getReceivedDate());
                if (receiveDate >= start && receiveDate < end) {
                    if (entity.type.equals(ActivityCloudDataStore.Activity_type_indoor)) {
                        act.mIndoor.mId = entity.getId();
                        act.mIndoor.mMacId = entity.getMacId();
                        act.mIndoor.mSteps += entity.getSteps();
                        act.mIndoor.mDistance += entity.getDistance();
                    } else if (entity.type.equals(ActivityCloudDataStore.Activity_type_outdoor)) {
                        act.mOutdoor.mId = entity.getId();
                        act.mOutdoor.mMacId = entity.getMacId();
                        act.mOutdoor.mSteps += entity.getSteps();
                        act.mOutdoor.mDistance += entity.getDistance();
                    }
                }
            }

            return act;
        }

        @Override
        protected void onPostExecute(WatchActivity activity) {
            super.onPostExecute(activity);
            try {
                theFragment.handleTodayData(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleTodayData(WatchActivity activity) {
        todayWatchActivity = activity;
        setToday();
    }

    private static class WeeklyAndMonthlyTask extends AsyncTask<Object, Integer, List<WatchActivity>> {

        private int chartType;
        private DashboardChartSingleFragment theFragment;

        WeeklyAndMonthlyTask(DashboardChartSingleFragment instance) {
            WeakReference<DashboardChartSingleFragment> wr = new WeakReference<>(instance);
            theFragment = wr.get();
        }

        @Override
        protected List<WatchActivity> doInBackground(Object... params) {
            this.chartType = (int) params[0];
            RetrieveDataRep rep = (RetrieveDataRep) params[1];
            List<WatchActivity> watchActivities = new ArrayList<>();
            List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();

            long start = (Long) params[2];
            long end = (Long) params[3];
            long timezoneOffset = (Long) params[4];
            long millisInDay = 1000 * 60 * 60 * 24;
            long timestamp = start;
            while (timestamp < end) {
                watchActivities.add(new WatchActivity((Long) params[5], timestamp));
                timestamp += millisInDay;
            }
            if (null == activitiesEntities || activitiesEntities.isEmpty()) {
                for (WatchActivity act : watchActivities) {
                    act.mIndoor.mTimestamp -= timezoneOffset;
                    act.mOutdoor.mTimestamp -= timezoneOffset;
                }
                return watchActivities;
            }
            for (WatchActivity act : watchActivities) {
                for (RetrieveDataRep.ActivitiesEntity entity : activitiesEntities) {
                    long receiveDate = BeanConvertor.getLocalTimeStamp(entity.getReceivedDate());
                    long actEnd = act.mIndoor.mTimestamp + millisInDay;
                    if (receiveDate >= act.mIndoor.mTimestamp && receiveDate < actEnd) {
                        if (entity.type.equals(ActivityCloudDataStore.Activity_type_indoor)) {
                            act.mIndoor.mId = entity.getId();
                            act.mIndoor.mMacId = entity.getMacId();
                            act.mIndoor.mSteps += entity.getSteps();
                            act.mIndoor.mDistance += entity.getDistance();
                        } else if (entity.type.equals(ActivityCloudDataStore.Activity_type_outdoor)) {
                            act.mOutdoor.mId = entity.getId();
                            act.mOutdoor.mMacId = entity.getMacId();
                            act.mOutdoor.mSteps += entity.getSteps();
                            act.mOutdoor.mDistance += entity.getDistance();
                        }
                    }
                }
            }

//            Collections.reverse(watchActivities);

            for (WatchActivity act : watchActivities) {
                act.mIndoor.mTimestamp -= timezoneOffset;
                act.mOutdoor.mTimestamp -= timezoneOffset;
            }

            return watchActivities;
        }

        @Override
        protected void onPostExecute(List<WatchActivity> watchActivities) {
            super.onPostExecute(watchActivities);
            try {
                theFragment.handleWeeklyAndMonthlyData(this.chartType, watchActivities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleWeeklyAndMonthlyData(int chartType, List<WatchActivity> watchActivities) {
        if (CHART_WEEK == chartType) {
            weeklyWatchActivities = watchActivities;
            setWeek();
        } else if (CHART_MONTH == chartType) {
            monthlyWatchActivities = watchActivities;
            setMonth();
        }
    }

    private static class YearlyTask extends AsyncTask<Object, Integer, List<WatchActivity>> {

        private DashboardChartSingleFragment theFragment;

        YearlyTask(DashboardChartSingleFragment instance) {
            WeakReference<DashboardChartSingleFragment> wr = new WeakReference<>(instance);
            theFragment = wr.get();
        }

        @Override
        protected List<WatchActivity> doInBackground(Object... params) {
            int chartType = (int) params[0];
            RetrieveMonthlyActivityRep rep = (RetrieveMonthlyActivityRep) params[1];
            List<RetrieveMonthlyActivityRep.ActivitiesEntity> activitiesEntities = rep.getActivities();
            long start = (Long) params[2];
            long end = (Long) params[3];
            long timezoneOffset = (Long) params[4];

            List<WatchActivity> watchActivities = new ArrayList<>();
            Calendar cld = Calendar.getInstance();
            cld.setTimeInMillis(start);
            for (int i = 0; i < 12; i++) {
                long timestamp = cld.getTimeInMillis();
                WatchActivity watchActivity = new WatchActivity((Long) params[5], timestamp);
                watchActivities.add(watchActivity);
                cld.add(Calendar.MONTH, 1);
            }

            if (null == activitiesEntities || activitiesEntities.isEmpty()) {
                for (WatchActivity act : watchActivities) {
                    act.mIndoor.mTimestamp -= timezoneOffset;
                    act.mOutdoor.mTimestamp -= timezoneOffset;
                }
                return watchActivities;
            }
            for (WatchActivity act : watchActivities) {
                long timestamp = act.mIndoor.mTimestamp;
                cld.setTimeInMillis(timestamp);
                int month = cld.get(Calendar.MONTH) + 1;
                for (RetrieveMonthlyActivityRep.ActivitiesEntity entity : activitiesEntities) {
                    if (month == entity.getMonth()) {
                        if (entity.type.equals(ActivityCloudDataStore.Activity_type_indoor)) {
                            act.mIndoor.mMacId = entity.getMacId();
                            act.mIndoor.mSteps += entity.getSteps();
                            act.mIndoor.mDistance += entity.getDistance();
                        } else if (entity.type.equals(ActivityCloudDataStore.Activity_type_outdoor)) {
                            act.mOutdoor.mMacId = entity.getMacId();
                            act.mOutdoor.mSteps += entity.getSteps();
                            act.mOutdoor.mDistance += entity.getDistance();
                        }
                    }
                }
            }

//            Collections.reverse(watchActivities);

            for (WatchActivity act : watchActivities) {
                act.mIndoor.mTimestamp -= timezoneOffset;
                act.mOutdoor.mTimestamp -= timezoneOffset;
            }

            return watchActivities;
        }

        @Override
        protected void onPostExecute(List<WatchActivity> watchActivities) {
            super.onPostExecute(watchActivities);
            try {
                theFragment.handleYearlyData(watchActivities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleYearlyData(List<WatchActivity> watchActivities) {
        yearlyWatchActivities = watchActivities;
        setYear();
    }

    private ViewTextSelector.OnSelectListener mSelectorListener = new ViewTextSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, int position) {
            mViewIndicator.setDotPosition(position);
            showChart(position);
        }
    };

    private ViewChartToday.OnAxisRectClickListener onAxisRectClickListener = new ViewChartToday.OnAxisRectClickListener() {
        @Override
        public void onAxisRectClick(float x, float y) {
            setSavedData();
            setFragment(DashboardListFragment.newInstance(getDoor(), mCurrentChart, mEmotion), true);
        }
    };

    private ViewChartBarVertical.onBarClickListener onBarClickListener = new ViewChartBarVertical.onBarClickListener() {
        @Override
        public void onBarClick(int index, float x, float y) {
            setSavedData();
            setFragment(DashboardListFragment.newInstance(getDoor(), mCurrentChart, mEmotion), true);
        }
    };

    private View.OnClickListener onChartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setSavedData();
            setFragment(DashboardListFragment.newInstance(getDoor(), mCurrentChart, mEmotion), true);
        }
    };

    private void setSavedData() {
        DataUtil.getInstance().setChartTypeInSingleChart(mCurrentChart);
        DataUtil.getInstance().setDoorTypeInSingleChart(getDoor());
        DataUtil.getInstance().setEmotionInSingleChart(mEmotion);
        DataUtil.getInstance().setTodayWatchActivityInSingleChart(todayWatchActivity);
    }

    private void clearSavedData() {
        DataUtil.getInstance().setChartTypeInSingleChart(null);
        DataUtil.getInstance().setDoorTypeInSingleChart(null);
        DataUtil.getInstance().setEmotionInSingleChart(null);
        DataUtil.getInstance().setTodayWatchActivityInSingleChart(null);
    }

    private BetterViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mViewIndicator.setDotPosition(position);
            mViewSelector.select(position);
            showChart(position);
            mCurrentChart = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            showChart(mCurrentChart);
        }
    };

    private int getEmotionWithTodayActivity(WatchActivity wa) {
        long steps = 0;
        if (null != wa) {
            steps = wa.mIndoor.mSteps + wa.mOutdoor.mSteps;
        }

        int emotion = EMOTION_LOW;
        if (steps > WatchActivity.STEP_ALMOST)
            emotion = EMOTION_ALMOST;
        if (steps > WatchActivity.STEP_EXCELLENT)
            emotion = EMOTION_EXCELLENT;

        return emotion;
    }

    private void setEmotion(int emotion) {
        int mEmotionColor;
        int mBorderButtonBg;
        ColorStateList mBorderButtonTextColorStateList;
        switch (emotion) {
            case EMOTION_LOW:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_blue_main);
                mBorderButtonBg = R.drawable.border_button_bg_blue;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_blue_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.emotion_bg_purple);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_below));
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_green_main);
                mBorderButtonBg = R.drawable.border_button_bg_green;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_green_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.emotion_bg_green);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_almost));
                break;

            default:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_orange_main);
                mBorderButtonBg = R.drawable.border_button_bg_orange;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_orange_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.emotion_bg_orange);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_excellent));
                break;
        }

        mViewIndicator.setDotColorOn(mEmotionColor);
        mViewSelector.setTextColor(mEmotionColor);
        mViewSelector.setSelectorColor(mEmotionColor);
        mViewMessage.setTextColor(mEmotionColor);

        mViewChartToday.mChartColor = mEmotionColor;
        mViewChartWeek.mChartColor = mEmotionColor;
        mViewChartMonth.mChartColor = mEmotionColor;
        mViewChartYear.mChartColor = mEmotionColor;

        mRadioButtonIndoor.setBackgroundResource(mBorderButtonBg);
        mRadioButtonIndoor.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mRadioButtonIndoor.setTextColor(mBorderButtonTextColorStateList);
        mRadioButtonOutdoor.setBackgroundResource(mBorderButtonBg);
        mRadioButtonIndoor.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mRadioButtonOutdoor.setTextColor(mBorderButtonTextColorStateList);

        mEmotion = emotion;
    }

    private int getDoor() {
        return mRadioButtonIndoor.isChecked() ? INDOOR : OUTDOOR;
    }

    private void showChart(int chart) {
        if (chart == CHART_TODAY)
            showToday();
        else if (chart == CHART_WEEK)
            showWeek();
        else if (chart == CHART_MONTH)
            showMonth();
        else if (chart == CHART_YEAR)
            showYear();
    }

    private void showToday() {
        if (CHART_TODAY != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_TODAY, true);
        }
        if (null == todayWatchActivity) {
            loadData(CHART_TODAY);
        } else {
            setToday();
        }
    }

    private void setToday() {
        mViewChartToday.setValue(getStepToday(getDoor()));
        mViewChartToday.setGoal(WatchActivity.STEP_GOAL);
        mViewChartToday.invalidate();
    }

    private void showWeek() {
        if (CHART_WEEK != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_WEEK, true);
        }
        if (null == weeklyWatchActivities) {
            loadData(CHART_WEEK);
        } else {
            setWeek();
        }
    }

    private void setWeek() {
        mViewChartWeek.setValue(getStepWeek(getDoor()));
        mViewChartWeek.setGoal(WatchActivity.STEP_GOAL);
        mViewChartWeek.invalidate();
    }

    private void showMonth() {
        if (CHART_MONTH != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_MONTH, true);
        }
        if (null == monthlyWatchActivities) {
            loadData(CHART_MONTH);
        } else {
            setMonth();
        }
    }

    private void setMonth() {
        mViewChartMonth.setValue(getStepMonth(getDoor()));
        mViewChartMonth.setGoal(WatchActivity.STEP_GOAL);
        mViewChartMonth.invalidate();
    }

    private void showYear() {
        if (CHART_YEAR != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_YEAR, true);
        }
        if (null == yearlyWatchActivities) {
            loadData(CHART_YEAR);
        } else {
            setYear();
        }
    }

    private void setYear() {
        mViewChartYear.setValue(getStepYear(getDoor()));
        mViewChartYear.setGoal(WatchActivity.STEP_GOAL);
        mViewChartYear.invalidate();
    }

    private WatchActivity.Act getStepToday(int door) {
        return null != todayWatchActivity ?
                (door == INDOOR ? todayWatchActivity.mIndoor : todayWatchActivity.mOutdoor) : null;
    }

    private List<WatchActivity.Act> getStepWeek(int door) {
        if (null == weeklyWatchActivities || weeklyWatchActivities.isEmpty()) {
            return null;
        }
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : weeklyWatchActivities)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

        return rtn;
    }

    private List<WatchActivity.Act> getStepMonth(int door) {
        if (null == monthlyWatchActivities || monthlyWatchActivities.isEmpty()) {
            return null;
        }

        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : monthlyWatchActivities)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

//        for (WatchActivity.Act act : rtn) {
//            act.mSteps = (int) (Math.random() * mViewChartMonth.mAxisVMax);
//        }

        return rtn;
    }

    private List<WatchActivity.Act> getStepYear(int door) {
        if (null == yearlyWatchActivities || yearlyWatchActivities.isEmpty()) {
            return null;
        }

        List<WatchActivity.Act> rtn = new ArrayList<>();
        for (WatchActivity activity : yearlyWatchActivities)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

        return rtn;
    }

    private void initViewPager() {
        List<View> list = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View chartToday = inflater.inflate(R.layout.layout_chart_today_single, mViewPager, false);
        mViewMessage = (TextView) chartToday.findViewById(R.id.dashboard_chart_message);
        mViewChartToday = (ViewChartToday) chartToday.findViewById(R.id.dashboard_chart_today);
        mViewChartToday.setOnAxisRectClickListener(onAxisRectClickListener);
        list.add(chartToday);

        View chartWeek = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartWeek = (ViewChartBarVertical) chartWeek.findViewById(R.id.dashboard_chart_vertical);
//        mViewChartWeek.setOnBarClickListener(onBarClickListener);
        mViewChartWeek.setOnClickListener(onChartClickListener);
        list.add(chartWeek);

        View chartMonth = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartMonth = (ViewChartBarVertical) chartMonth.findViewById(R.id.dashboard_chart_vertical);
//        mViewChartMonth.setOnBarClickListener(onBarClickListener);
        mViewChartMonth.setOnClickListener(onChartClickListener);
        list.add(chartMonth);

        View chartYear = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartYear = (ViewChartBarVertical) chartYear.findViewById(R.id.dashboard_chart_vertical);
//        mViewChartYear.setOnBarClickListener(onBarClickListener);
        mViewChartYear.setOnClickListener(onChartClickListener);
        list.add(chartYear);

        ChartPagerAdapter adapter = new ChartPagerAdapter(list);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        mViewChartToday.getParent().requestDisallowInterceptTouchEvent(true);
        mViewChartWeek.getParent().requestDisallowInterceptTouchEvent(true);
        mViewChartMonth.getParent().requestDisallowInterceptTouchEvent(true);
        mViewChartYear.getParent().requestDisallowInterceptTouchEvent(true);
    }

    private class ChartPagerAdapter extends PagerAdapter {

        private List<View> views;

        ChartPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {//初始化一个页卡
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {//销毁一个页卡
            container.removeView(views.get(position));
        }
    }

}
