package com.kidsdynamic.swing.presenter;

import android.content.res.ColorStateList;
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

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.KidActivityManager;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewChartBarVertical;
import com.kidsdynamic.swing.view.ViewChartToday;
import com.kidsdynamic.swing.view.ViewDotIndicator;
import com.kidsdynamic.swing.view.ViewTextSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        view_right_action.setImageResource(R.drawable.icon_uv_blue_light2_);
        int right = (int) getResources().getDimension(R.dimen.base_12);
        view_right_action.setPadding(0, 0, right, 0);

        Bundle args = getArguments();
        int door = args.getInt(DOOR_TYPE, INDOOR);
        mRadioButtonIndoor.setChecked(INDOOR == door);
        mRadioButtonOutdoor.setChecked(OUTDOOR == door);
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        long step = getStepToday(INDOOR).mSteps + getStepToday(OUTDOOR).mSteps;

        int emotion = EMOTION_LOW;
        if (step > WatchActivity.STEP_ALMOST)
            emotion = EMOTION_ALMOST;
        if (step > WatchActivity.STEP_EXCELLENT)
            emotion = EMOTION_EXCELLENT;

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

        Bundle args = getArguments();
        int chartType = args.getInt(CHART_TYPE, CHART_TODAY);
        setChart(chartType);
        mCurrentChart = chartType;
    }

    private ViewTextSelector.OnSelectListener mSelectorListener = new ViewTextSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, int position) {
            mViewIndicator.setDotPosition(position);
            setChart(position);
        }
    };

    private ViewChartToday.OnAxisRectClickListener onAxisRectClickListener = new ViewChartToday.OnAxisRectClickListener() {
        @Override
        public void onAxisRectClick(float x, float y) {
            setFragment(DashboardListFragment.newInstance(mCurrentChart, mEmotion), true);
        }
    };

    private ViewChartBarVertical.onBarClickListener onBarClickListener = new ViewChartBarVertical.onBarClickListener() {
        @Override
        public void onBarClick(int index, float x, float y) {
            setFragment(DashboardListFragment.newInstance(mCurrentChart, mEmotion), true);
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
            setChart(position);
            mCurrentChart = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            setChart(mCurrentChart);
        }
    };

    private void setEmotion(int emotion) {
        int mEmotionColor;
        int mBorderButtonBg;
        ColorStateList mBorderButtonTextColorStateList;
        switch (emotion) {
            case EMOTION_LOW:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_blue_main);
                mBorderButtonBg = R.drawable.border_button_bg_blue;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(), R.color.text_blue_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster01);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_below));
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_green_main);
                mBorderButtonBg = R.drawable.border_button_bg_green;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(), R.color.text_green_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster02);
                mViewMessage.setText(getResources().getString(R.string.dashboard_chart_message_almost));
                break;

            default:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_orange_main);
                mBorderButtonBg = R.drawable.border_button_bg_orange;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(), R.color.text_orange_white_change_selector);

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

    private void setChart(int chart) {
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
        mViewChartToday.setValue(getStepToday(getDoor()));
        mViewChartToday.setTotal(getStepToday(INDOOR).mSteps + getStepToday(OUTDOOR).mSteps);
        mViewChartToday.setGoal(WatchActivity.STEP_GOAL);
        mViewChartToday.invalidate();
    }

    private void showWeek() {
        if (CHART_WEEK != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_WEEK, true);
        }
        mViewChartWeek.setValue(getStepWeek(getDoor()));
        mViewChartWeek.setGoal(WatchActivity.STEP_GOAL);
        mViewChartWeek.invalidate();
    }

    private void showMonth() {
        if (CHART_MONTH != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_MONTH, true);
        }
        mViewChartMonth.setValue(getStepMonth(getDoor()));
        mViewChartMonth.setGoal(WatchActivity.STEP_GOAL);
        mViewChartMonth.invalidate();
    }

    private void showYear() {
        if (CHART_YEAR != mCurrentChart) {
            mViewPager.setCurrentItem(CHART_YEAR, true);
        }
        mViewChartYear.setValue(getStepYear(getDoor()));
        mViewChartYear.setGoal(WatchActivity.STEP_GOAL);
        mViewChartYear.invalidate();
    }

    private WatchActivity.Act getStepToday(int door) {
        WatchActivity act = new KidActivityManager().getActivityOfDay(getContext());
        return door == INDOOR ? act.mIndoor : act.mOutdoor;
    }

    private List<WatchActivity.Act> getStepWeek(int door) {
        List<WatchActivity> thisWeek = new KidActivityManager().getActivityOfWeek(getContext());
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisWeek)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

        return rtn;
    }

    private List<WatchActivity.Act> getStepMonth(int door) {
        List<WatchActivity> thisWeek = new KidActivityManager().getActivityOfMonth(getContext());
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisWeek)
            rtn.add(new WatchActivity.Act(door == INDOOR ? activity.mIndoor : activity.mOutdoor));

//        for (WatchActivity.Act act : rtn) {
//            act.mSteps = (int) (Math.random() * mViewChartMonth.mAxisVMax);
//        }

        return rtn;
    }

    private List<WatchActivity.Act> getStepYear(int door) {
        List<WatchActivity> thisWeek = new KidActivityManager().getActivityOfYear(getContext());
        List<WatchActivity.Act> rtn = new ArrayList<>();

        for (WatchActivity activity : thisWeek)
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

        String chartTitle = getString(R.string.dashboard_chart_steps);

        View chartWeek = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartWeek = (ViewChartBarVertical) chartWeek.findViewById(R.id.dashboard_chart_vertical);
        mViewChartWeek.setTitle(chartTitle);
        mViewChartWeek.setOnBarClickListener(onBarClickListener);
        list.add(chartWeek);

        View chartMonth = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartMonth = (ViewChartBarVertical) chartMonth.findViewById(R.id.dashboard_chart_vertical);
        mViewChartMonth.setTitle(chartTitle);
        mViewChartMonth.setOnBarClickListener(onBarClickListener);
        list.add(chartMonth);

        View chartYear = inflater.inflate(R.layout.layout_chart_vertical_single, mViewPager, false);
        mViewChartYear = (ViewChartBarVertical) chartYear.findViewById(R.id.dashboard_chart_vertical);
        mViewChartYear.setTitle(chartTitle);
        mViewChartYear.setOnBarClickListener(onBarClickListener);
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
