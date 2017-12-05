package com.kidsdynamic.swing.presenter;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.BetterViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kidsdynamic.data.net.activity.model.RetrieveDataRep;
import com.kidsdynamic.data.repository.disk.ActivityCloudDataStore;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.KidActivityManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewChartBarVertical;
import com.kidsdynamic.swing.view.ViewChartToday;
import com.kidsdynamic.swing.view.ViewDotIndicator;
import com.kidsdynamic.swing.view.ViewTextSelector;
import com.yy.base.utils.ToastCommon;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
    public static final String WATCH_ACTIVITY = "watch_activity";

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
    private long kidId;
    private boolean isFirstLoad = true;
    private List<WatchActivity> watchActivities;

    public static DashboardChartSingleFragment newInstance(int doorType, int chartType, WatchActivity wa) {
        Bundle args = new Bundle();
        args.putInt(DOOR_TYPE, doorType);
        args.putInt(CHART_TYPE, chartType);
        args.putSerializable(WATCH_ACTIVITY, wa);
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
        isFirstLoad = true;
        view_left_action.setImageResource(R.drawable.icon_left);
        tv_title.setText(R.string.dashboard_chart_activity);
//        view_right_action.setImageResource(R.drawable.icon_uv_blue_light2_);
//        int right = (int) getResources().getDimension(R.dimen.base_12);
//        view_right_action.setPadding(0, 0, right, 0);

        Bundle args = getArguments();
        int door = null != args ? args.getInt(DOOR_TYPE, INDOOR) : INDOOR;
        mRadioButtonIndoor.setChecked(INDOOR == door);
        mRadioButtonOutdoor.setChecked(OUTDOOR == door);
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        WatchActivity wa = null != args ? (WatchActivity) args.getSerializable(WATCH_ACTIVITY) : null;
        int emotion = getEmotionWithTodayActivity(wa);

        mViewSelector.setOnSelectListener(mSelectorListener);

        mViewSelector.clear();
        mViewSelector.add(Arrays.asList(
                getString(R.string.dashboard_chart_today),
                getString(R.string.dashboard_chart_this_week),
                getString(R.string.dashboard_chart_this_month),
                getString(R.string.dashboard_chart_this_year)));

        mViewIndicator.setDotCount(mViewSelector.getCount());
        mViewIndicator.setDotPosition(0);

        initViewPager();

        setEmotion(emotion);

        int chartType = null != args ? args.getInt(CHART_TYPE, CHART_TODAY) : CHART_TODAY;
        showChart(chartType);
        mCurrentChart = chartType;

        loadData();
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
//        mActivityMain.popFragment();
        getFragmentManager().popBackStack();
    }

    private void loadData() {
//        showLoadingDialog(R.string.signup_login_wait);
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

        KidsEntityBean kid = DeviceManager.getFocusKidsInfo(getContext());
        if (null != kid) {
            kidId = kid.getKidsId();
        }

        new KidActivityManager().retrieveDataByTime(getContext(), kidId, start, end,
                new IRetrieveCompleteListener(start, end, timezoneOffset));
    }

    private class IRetrieveCompleteListener implements KidActivityManager.ICompleteListener {

        private long start, end, timezoneOffset;

        IRetrieveCompleteListener(long start, long end, long timezoneOffset) {
            this.start = start;
            this.end = end;
            this.timezoneOffset = timezoneOffset;
        }

        @Override
        public void onComplete(Object arg, int statusCode) {
            if (200 == statusCode && null != arg) {
                new DataTask(DashboardChartSingleFragment.this)
                        .execute(arg, start, end, timezoneOffset);
            } else {
//                finishLoadingDialog();
                ToastCommon.makeText(getContext(), R.string.dashboard_enqueue_fail_common);
            }
        }

        @Override
        public void onFailed(String Command, int statusCode) {
//            finishLoadingDialog();
            ToastCommon.showToast(getContext(), Command);
        }
    }

    private static class DataTask extends AsyncTask<Object, Integer, List<WatchActivity>> {

        private DashboardChartSingleFragment theFragment;

        DataTask(DashboardChartSingleFragment instance) {
            WeakReference<DashboardChartSingleFragment> wr = new WeakReference<>(instance);
            theFragment = wr.get();
        }

        @Override
        protected List<WatchActivity> doInBackground(Object... params) {
            RetrieveDataRep rep = (RetrieveDataRep) params[0];
            List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();
            if (null == activitiesEntities || activitiesEntities.isEmpty()) {
                return null;
            }
            List<WatchActivity> watchActivities = new ArrayList<>();
            long start = (Long) params[1];
            long end = (Long) params[2];
            long timezoneOffset = (Long) params[3];
            long millisInDay = 1000 * 60 * 60 * 24;
            long timestamp = start;
            while (timestamp < end) {
                watchActivities.add(new WatchActivity(theFragment.kidId, timestamp));
                timestamp += millisInDay;
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

            Collections.reverse(watchActivities);

//            for (WatchActivity act : theFragment.watchActivities) {
//                act.mIndoor.mTimestamp -= timezoneOffset;
//                act.mOutdoor.mTimestamp -= timezoneOffset;
//            }

            return watchActivities;
        }

        @Override
        protected void onPostExecute(List<WatchActivity> watchActivities) {
            super.onPostExecute(watchActivities);
            theFragment.handlePostExecute(watchActivities);
//            theFragment.finishLoadingDialog();
        }
    }

    private void handlePostExecute(List<WatchActivity> watchActivities) {
        this.watchActivities = watchActivities;
        int emotion = getEmotionWithTodayActivity(null != watchActivities ? watchActivities.get(0) : null);
        setEmotion(emotion);
        showChart(mCurrentChart);
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
            setFragment(DashboardListFragment.newInstance(getDoor(), mCurrentChart, mEmotion), true);
        }
    };

    private ViewChartBarVertical.onBarClickListener onBarClickListener = new ViewChartBarVertical.onBarClickListener() {
        @Override
        public void onBarClick(int index, float x, float y) {
            setFragment(DashboardListFragment.newInstance(getDoor(), mCurrentChart, mEmotion), true);
        }
    };

    private View.OnClickListener onChartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setFragment(DashboardListFragment.newInstance(getDoor(), mCurrentChart, mEmotion), true);
        }
    };

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

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster01);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_below));
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_green_main);
                mBorderButtonBg = R.drawable.border_button_bg_green;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_green_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster02);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_almost));
                break;

            default:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_orange_main);
                mBorderButtonBg = R.drawable.border_button_bg_orange;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_orange_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster03);
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
        setToday();
    }

    private void setToday() {
        WatchActivity.Act act = null;
        if (isFirstLoad) {
            Bundle args = getArguments();
            WatchActivity wa = null != args ? (WatchActivity) args.getSerializable(WATCH_ACTIVITY) : null;
            if (null != wa) {
                act = getDoor() == INDOOR ? wa.mIndoor : wa.mOutdoor;
            }
            isFirstLoad = false;
        } else {
            act = getStepToday(getDoor());
        }
        mViewChartToday.setValue(act);
        mViewChartToday.setGoal(WatchActivity.STEP_GOAL);
        mViewChartToday.invalidate();
    }

    private void showWeek() {
        if (CHART_WEEK != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_WEEK, true);
        }
        setWeek();
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
        setMonth();
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
        setYear();
    }

    private void setYear() {
        mViewChartYear.setValue(getStepYear(getDoor()));
        mViewChartYear.setGoal(WatchActivity.STEP_GOAL);
        mViewChartYear.invalidate();
    }

    private WatchActivity.Act getStepToday(int door) {
        Bundle args = getArguments();
        WatchActivity wa = null != args ? (WatchActivity) args.getSerializable(WATCH_ACTIVITY) : null;
        if (null == wa && null != watchActivities && !watchActivities.isEmpty()) {
            wa = watchActivities.get(0);
        }
        return null != wa ? (door == INDOOR ? wa.mIndoor : wa.mOutdoor) : null;
    }

    private List<WatchActivity.Act> getStepWeek(int door) {
        if (null == watchActivities || watchActivities.isEmpty()) {
            return null;
        }
        List<WatchActivity> thisWeek = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            thisWeek.add(watchActivities.get(i));
        }
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisWeek)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

        Collections.reverse(rtn);

        return rtn;
    }

    private List<WatchActivity.Act> getStepMonth(int door) {
        if (null == watchActivities || watchActivities.isEmpty()) {
            return null;
        }
        List<WatchActivity> thisMonth = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            thisMonth.add(watchActivities.get(i));
        }
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisMonth)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

//        for (WatchActivity.Act act : rtn) {
//            act.mSteps = (int) (Math.random() * mViewChartMonth.mAxisVMax);
//        }

        Collections.reverse(rtn);

        return rtn;
    }

    private List<WatchActivity.Act> getStepYear(int door) {
        if (null == watchActivities || watchActivities.isEmpty()) {
            return null;
        }
        List<WatchActivity> thisYear = new ArrayList<>();
        Calendar cld = Calendar.getInstance();

        cld.add(Calendar.MONTH, -11);
        cld.set(Calendar.DATE, 1);
        cld.set(Calendar.HOUR_OF_DAY, 0);
        cld.set(Calendar.MINUTE, 0);
        cld.set(Calendar.SECOND, 0);
        long startTimestamp = cld.getTimeInMillis();

        for (int i = 0; i < 12; i++) {
            WatchActivity watchActivity = new WatchActivity(0, startTimestamp);

            // 下一个起始时间加一个月后，再减去一秒，作为本月的结束时间
            cld.setTimeInMillis(startTimestamp);
            cld.add(Calendar.MONTH, 1);
            cld.set(Calendar.DATE, 1);
            cld.set(Calendar.HOUR_OF_DAY, 0);
            cld.set(Calendar.MINUTE, 0);
            cld.set(Calendar.SECOND, 0);
            cld.add(Calendar.SECOND, -1);
            long endTimestamp = cld.getTimeInMillis();

            int days = 0;
            for (WatchActivity src : watchActivities) {
                boolean isInTimeRange = watchActivity.addInTimeRange(src, startTimestamp, endTimestamp);
                if (isInTimeRange) {
                    days += 1;
                }
            }
            if (days > 0) {
                watchActivity.mOutdoor.mSteps = watchActivity.mOutdoor.mSteps / days;
                watchActivity.mIndoor.mSteps = watchActivity.mIndoor.mSteps / days;
            }
            thisYear.add(watchActivity);

            // 本次起始时间加一个月，作为下一个起始时间
            cld.setTimeInMillis(startTimestamp);
            cld.add(Calendar.MONTH, 1);
            cld.set(Calendar.DATE, 1);
            cld.set(Calendar.HOUR_OF_DAY, 0);
            cld.set(Calendar.MINUTE, 0);
            cld.set(Calendar.SECOND, 0);
            startTimestamp = cld.getTimeInMillis();
        }

        List<WatchActivity.Act> rtn = new ArrayList<>();
        for (WatchActivity activity : thisYear)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

//        Collections.reverse(rtn);

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

        String chartTitle = getString(R.string.dashboard_chart_steps);

        View chartWeek = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartWeek = (ViewChartBarVertical) chartWeek.findViewById(R.id.dashboard_chart_vertical);
        mViewChartWeek.setTitle(chartTitle);
//        mViewChartWeek.setOnBarClickListener(onBarClickListener);
        mViewChartWeek.setOnClickListener(onChartClickListener);
        list.add(chartWeek);

        View chartMonth = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartMonth = (ViewChartBarVertical) chartMonth.findViewById(R.id.dashboard_chart_vertical);
        mViewChartMonth.setTitle(chartTitle);
//        mViewChartMonth.setOnBarClickListener(onBarClickListener);
        mViewChartMonth.setOnClickListener(onChartClickListener);
        list.add(chartMonth);

        View chartYear = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartYear = (ViewChartBarVertical) chartYear.findViewById(R.id.dashboard_chart_vertical);
        mViewChartYear.setTitle(chartTitle);
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
