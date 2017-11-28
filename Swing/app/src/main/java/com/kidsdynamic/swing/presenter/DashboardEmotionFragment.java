package com.kidsdynamic.swing.presenter;

import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.KidActivityManager;
import com.kidsdynamic.swing.model.WatchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DashboardEmotionFragment
 * <p>
 * Created by Stefan on 2017/11/7.
 */

public class DashboardEmotionFragment extends DashboardBaseFragment {

    @BindView(R.id.dashboard_emotion_root)
    View root;
    @BindView(R.id.tv_top_banner)
    TextView tv_top_banner;
    @BindView(R.id.tv_message)
    TextView tv_message;
    @BindView(R.id.iv_monster)
    ImageView iv_monster;
    @BindView(R.id.tv_today_summary)
    TextView tv_today_summary;
    @BindView(R.id.tv_indoor_steps_desc)
    TextView tv_indoor_steps_desc;
    @BindView(R.id.tv_indoor_steps_value)
    TextView tv_indoor_steps_value;
    @BindView(R.id.tv_indoor_steps_unit)
    TextView tv_indoor_steps_unit;
    @BindView(R.id.tv_indoor_time)
    TextView tv_indoor_time;
    @BindView(R.id.tv_outdoor_steps_desc)
    TextView tv_outdoor_steps_desc;
    @BindView(R.id.tv_outdoor_steps_value)
    TextView tv_outdoor_steps_value;
    @BindView(R.id.tv_outdoor_steps_unit)
    TextView tv_outdoor_steps_unit;
    @BindView(R.id.tv_outdoor_time)
    TextView tv_outdoor_time;
    @BindView(R.id.tv_uv_desc)
    TextView tv_uv_desc;
    @BindView(R.id.tv_uv_hour)
    TextView tv_uv_hour;
    @BindView(R.id.tv_uv_hours)
    TextView tv_uv_hours;
    @BindView(R.id.tv_uv_minute)
    TextView tv_uv_minute;
    @BindView(R.id.tv_uv_minutes)
    TextView tv_uv_minutes;
    @BindView(R.id.tv_uv_time)
    TextView tv_uv_time;
    @BindView(R.id.ib_activity)
    ImageButton ib_activity;
    @BindView(R.id.tv_activity)
    TextView tv_activity;
    @BindView(R.id.ib_uv_detection)
    ImageButton ib_uv_detection;
    @BindView(R.id.tv_uv_detection)
    TextView tv_uv_detection;

    private Animatable animLoading;

    public static DashboardEmotionFragment newInstance() {
        Bundle args = new Bundle();
        DashboardEmotionFragment fragment = new DashboardEmotionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_emotion, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_dashboard);
        view_left_action.setImageResource(R.drawable.progress_loading);
        animLoading = (Animatable) view_left_action.getDrawable();
        animLoading.start();

        WatchActivity act = new KidActivityManager().getActivityOfDay(getContext());
        long step = act.mIndoor.mSteps + act.mOutdoor.mSteps;
        int emotion;
        if (step < WatchActivity.STEP_ALMOST)
            emotion = EMOTION_LOW;
        else if (step < WatchActivity.STEP_EXCELLENT)
            emotion = EMOTION_ALMOST;
        else
            emotion = EMOTION_EXCELLENT;
        setViews(emotion);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != animLoading && animLoading.isRunning()) {
            animLoading.stop();
        }
    }

    @OnClick(R.id.rl_indoor_steps)
    public void clickIndoorSteps() {
        setFragment(DashboardChartSingleFragment.newInstance(INDOOR, CHART_TODAY), true);
    }

    @OnClick(R.id.rl_outdoor_steps)
    public void clickOutdoorSteps() {
        setFragment(DashboardChartSingleFragment.newInstance(OUTDOOR, CHART_TODAY), true);
    }

    @OnClick(R.id.rl_uv_explosure)
    public void clickUVExplosure() {

    }

    @OnClick({R.id.ib_activity, R.id.tv_activity})
    public void clickActivity() {
        setFragment(DashboardChartTripleFragment.newInstance(OUTDOOR), true);
    }

    @OnClick({R.id.ib_uv_detection, R.id.tv_uv_detection})
    public void clickUVDetection() {

    }

    private void setViews(int emotion) {
        Resources res = getResources();
        int rootId = R.drawable.emotion_fragment_bg_blue;
        int color = res.getColor(R.color.color_blue_main);
        int messageId = R.string.dashboard_emotion_below;
        int monsterId = R.drawable.monster_purple;
        int activityBgId = R.drawable.circle_bg_blue;
        int uvDetectionBgId = R.drawable.circle_bg_blue;
        if (EMOTION_LOW == emotion) {
            rootId = R.drawable.emotion_fragment_bg_blue;
            color = res.getColor(R.color.color_blue_main);
            messageId = R.string.dashboard_emotion_below;
            monsterId = R.drawable.monster_purple;
            activityBgId = R.drawable.circle_bg_blue;
            uvDetectionBgId = R.drawable.circle_bg_blue;
        } else if (EMOTION_ALMOST == emotion) {
            rootId = R.drawable.emotion_fragment_bg_green;
            color = res.getColor(R.color.color_green_main);
            messageId = R.string.dashboard_emotion_almost;
            monsterId = R.drawable.monster_green;
            activityBgId = R.drawable.circle_bg_green;
            uvDetectionBgId = R.drawable.circle_bg_green;
        } else if (EMOTION_EXCELLENT == emotion) {
            rootId = R.drawable.emotion_fragment_bg_orange;
            color = res.getColor(R.color.color_orange_main);
            messageId = R.string.dashboard_emotion_excellent;
            monsterId = R.drawable.monster_yellow;
            activityBgId = R.drawable.circle_bg_orange;
            uvDetectionBgId = R.drawable.circle_bg_orange;
        }
        root.setBackgroundResource(rootId);
        tv_top_banner.setTextColor(color);
        tv_message.setText(messageId);
        tv_message.setTextColor(color);
        iv_monster.setImageResource(monsterId);
        tv_today_summary.setTextColor(color);
        tv_indoor_steps_desc.setTextColor(color);
        tv_indoor_steps_value.setTextColor(color);
        tv_indoor_steps_unit.setTextColor(color);
        tv_outdoor_steps_desc.setTextColor(color);
        tv_outdoor_steps_value.setTextColor(color);
        tv_outdoor_steps_unit.setTextColor(color);
        tv_uv_desc.setTextColor(color);
        tv_uv_hour.setTextColor(color);
        tv_uv_hours.setTextColor(color);
        tv_uv_minute.setTextColor(color);
        tv_uv_minutes.setTextColor(color);
        tv_activity.setTextColor(color);
        tv_uv_detection.setTextColor(color);
        ib_activity.setBackgroundResource(activityBgId);
        ib_uv_detection.setBackgroundResource(uvDetectionBgId);
    }

}
