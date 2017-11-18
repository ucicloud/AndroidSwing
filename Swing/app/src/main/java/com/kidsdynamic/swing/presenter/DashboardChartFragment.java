package com.kidsdynamic.swing.presenter;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewChartBarVertical;
import com.kidsdynamic.swing.view.ViewChartKDToday;
import com.kidsdynamic.swing.view.ViewDotIndicator;
import com.kidsdynamic.swing.view.ViewTextSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * DashboardChartFragment
 * <p>
 * Created by Stefan on 2017/11/9.
 */

public class DashboardChartFragment extends DashboardBaseFragment {

    private static final int EMOTION_LOW = 0;
    private static final int EMOTION_ALMOST = 1;
    private static final int EMOTION_EXCELLENT = 2;

    private static final int INDOOR = 0;
    private static final int OUTDOOR = 1;

    private static final int CHART_TODAY = 0;
    private static final int CHART_WEEK = 1;
    private static final int CHART_MONTH = 2;
    private static final int CHART_YEAR = 3;

    @BindView(R.id.dashboard_chart_root)
    View mViewRoot;
    @BindView(R.id.dashboard_chart_indicator)
    ViewDotIndicator mViewIndicator;
    @BindView(R.id.dashboard_chart_selector)
    ViewTextSelector mViewSelector;
    @BindView(R.id.dashboard_chart_message)
    TextView mViewMessage;
    @BindView(R.id.dashboard_chart_today)
    ViewChartKDToday mViewChartToday;
    //    @BindView(R.id.dashboard_chart_horizontal)
//    ViewChartHorizontal mViewChartHorizontal;
    @BindView(R.id.dashboard_chart_vertical)
    ViewChartBarVertical mViewChartVertical;
    @BindView(R.id.dashboard_chart_radio)
    RadioGroup mRadioGroup;
    @BindView(R.id.dashboard_chart_indoor)
    RadioButton mRadioButtonIndoor;
    @BindView(R.id.dashboard_chart_outdoor)
    RadioButton mRadioButtonOutdoor;

    private int mEmotion;
    private int mEmotionColor;

    public static DashboardChartFragment newInstance() {
        Bundle args = new Bundle();
        DashboardChartFragment fragment = new DashboardChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_chart, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_title.setText(R.string.dashboard_chart_activity);
        mRadioButtonIndoor.setChecked(true);
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        mViewChartVertical.setTitle(getResources().getString(R.string.dashboard_chart_steps));
    }

    @Override
    public void onResume() {
        super.onResume();
//        int step = getStepToday(INDOOR).mSteps + getStepToday(OUTDOOR).mSteps;
        int step = new Random().nextInt(15000);

        // 依KD要求, 12000為達標, 我自己認為6000算是接近吧.
        int emotion = EMOTION_LOW;
        if (step > 6000)
            emotion = EMOTION_ALMOST;
        if (step > 12000)
            emotion = EMOTION_EXCELLENT;

        setEmotion(emotion);

        mViewSelector.setOnSelectListener(mSelectorListener);

        //////////////////////////////////////////////////////////////////
//        List<ViewChartHorizontal.HorizontalBar> bars = new ArrayList<>();
//        Random random = new Random();
//        for (int i = 0; i < 1; i++) {
//            ViewChartHorizontal.HorizontalBar bar = new ViewChartHorizontal.HorizontalBar();
//            bar.title = String.format(Locale.getDefault(), "Title%d", i);
//            bar.value = random.nextFloat() * 12000;
//            bar.unit = "";
//            bars.add(bar);
//        }
//        mViewChartHorizontal.setHorizontalBars(bars);
//        mViewChartHorizontal.setGoal(12000);
//        mViewChartHorizontal.mChartColor = mEmotionColor;
//        mViewChartHorizontal.postInvalidate();
        ////////////////////////////////////////////////////////////////////////

        mViewSelector.clear();
        mViewSelector.add(Arrays.asList(
                getResources().getString(R.string.dashboard_chart_today),
                getResources().getString(R.string.dashboard_chart_this_week),
                getResources().getString(R.string.dashboard_chart_this_month),
                getResources().getString(R.string.dashboard_chart_this_year)));

        mViewIndicator.setDotCount(mViewSelector.getCount());
        mViewIndicator.setDotPosition(0);

        Random random = new Random();
        int remainder = random.nextInt() % 4;
        if (remainder == 0) {
            showToday();
        } else if (remainder == 1) {
            showWeek();
        } else if (remainder == 2) {
            showMonth();
        } else {
            showYear();
        }
    }

    private ViewTextSelector.OnSelectListener mSelectorListener = new ViewTextSelector.OnSelectListener() {
        @Override
        public void OnSelect(View view, int position) {
            mViewIndicator.setDotPosition(position);
//            setChart(position);
        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            setChart(getChart());
        }
    };

    private void setEmotion(int emotion) {
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
        mViewChartVertical.mChartColor = mEmotionColor;

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

    private void showToday() {
        mViewMessage.setVisibility(View.VISIBLE);
        mViewChartToday.setVisibility(View.VISIBLE);
        mViewChartVertical.setVisibility(View.GONE);

        mViewChartToday.setValue(getStepToday(getDoor()));
//        mViewChartToday.setTotal(getStepToday(INDOOR).mSteps + getStepToday(OUTDOOR).mSteps);
        mViewChartToday.setTotal(3562f);
        mViewChartToday.setGoal(12000);
        mViewChartToday.invalidate();
    }

    private void showWeek() {
        mViewMessage.setVisibility(View.GONE);
        mViewChartToday.setVisibility(View.GONE);
        mViewChartVertical.setVisibility(View.VISIBLE);

        mViewChartVertical.setValue(getStepWeek(getDoor()));
        mViewChartVertical.setGoal(12000);
        mViewChartVertical.invalidate();
    }

    private void showMonth() {
        mViewMessage.setVisibility(View.GONE);
        mViewChartToday.setVisibility(View.GONE);
        mViewChartVertical.setVisibility(View.VISIBLE);

        mViewChartVertical.setValue(getStepMonth(getDoor()));
        mViewChartVertical.setGoal(12000);
        mViewChartVertical.invalidate();
    }

    private void showYear() {
        mViewMessage.setVisibility(View.GONE);
        mViewChartToday.setVisibility(View.GONE);
        mViewChartVertical.setVisibility(View.VISIBLE);

        mViewChartVertical.setValue(getStepYear(getDoor()));
        mViewChartVertical.setGoal(12000);
        mViewChartVertical.invalidate();
    }

    private WatchActivity.Act getStepToday(int door) {
        Random random = new Random();
        WatchActivity.Act act = new WatchActivity.Act();
        act.mSteps = random.nextInt(12000);
        act.mTimestamp = System.currentTimeMillis();
        act.mDistance = random.nextInt(8000);
        return act;
    }

    private List<WatchActivity.Act> getStepWeek(int door) {
        List<WatchActivity.Act> rtn = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            WatchActivity.Act act = new WatchActivity.Act();
            act.mSteps = random.nextInt(12000);
            act.mTimestamp = System.currentTimeMillis();
            act.mDistance = random.nextInt(8000);
            rtn.add(act);
        }
        return rtn;
    }

    private List<WatchActivity.Act> getStepMonth(int door) {
        List<WatchActivity.Act> rtn = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            WatchActivity.Act act = new WatchActivity.Act();
            act.mSteps = random.nextInt(12000);
            act.mTimestamp = System.currentTimeMillis();
            act.mDistance = random.nextInt(8000);
            rtn.add(act);
        }
        return rtn;
    }

    private List<WatchActivity.Act> getStepYear(int door) {
        List<WatchActivity.Act> rtn = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            WatchActivity.Act act = new WatchActivity.Act();
            act.mSteps = random.nextInt(12000);
            act.mTimestamp = System.currentTimeMillis();
            act.mDistance = random.nextInt(8000);
            rtn.add(act);
        }
        return rtn;
    }

}
