package com.kidsdynamic.swing.presenter;

import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.data.net.activity.model.RetrieveDataRep;
import com.kidsdynamic.data.repository.disk.ActivityCloudDataStore;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.KidActivityManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.utils.DataUtil;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

    //    private Animatable animLoading;
    private WatchActivity watchActivity;

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
//        view_left_action.setImageResource(R.drawable.progress_loading);
//        animLoading = (Animatable) view_left_action.getDrawable();
//        animLoading.start();

        WatchActivity wa = DataUtil.getInstance().getWatchActivityInEmotionFragment();
        if (null != wa) {
            setWatchActivity(wa);
            return;
        }

        KidsEntityBean kid = DeviceManager.getFocusKidsInfo(getContext());
        if (null == kid) {
            return;
        }
//        showLoadingDialog(R.string.signup_login_wait);
        Calendar cld = Calendar.getInstance();
        int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

        cld.set(Calendar.HOUR_OF_DAY, 23);
        cld.set(Calendar.MINUTE, 59);
        cld.set(Calendar.SECOND, 59);
        long end = cld.getTimeInMillis() + timezoneOffset;

        cld.add(Calendar.DAY_OF_MONTH, -1);
        cld.add(Calendar.SECOND, 1);
        long start = cld.getTimeInMillis() + timezoneOffset;

        new KidActivityManager().retrieveDataByTime(getContext(), kid.getKidsId(), start, end,
                new IRetrieveCompleteListener(start, end, timezoneOffset, kid.getKidsId()));
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    DataUtil.getInstance().setWatchActivityInEmotionFragment(null);
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (null != animLoading && animLoading.isRunning()) {
//            animLoading.stop();
//        }
    }

    @OnClick(R.id.rl_indoor_steps)
    public void clickIndoorSteps() {
        DataUtil.getInstance().setWatchActivityInEmotionFragment(watchActivity);
        setFragment(DashboardChartSingleFragment.newInstance(INDOOR, CHART_TODAY, watchActivity), true);
    }

    @OnClick(R.id.rl_outdoor_steps)
    public void clickOutdoorSteps() {
        DataUtil.getInstance().setWatchActivityInEmotionFragment(watchActivity);
        setFragment(DashboardChartSingleFragment.newInstance(OUTDOOR, CHART_TODAY, watchActivity), true);
    }

    @OnClick(R.id.rl_uv_exposure)
    public void clickUVExposure() {

    }

    @OnClick({R.id.ib_activity, R.id.tv_activity})
    public void clickActivity() {
//        setFragment(DashboardChartTripleFragment.newInstance(OUTDOOR), true);
        DataUtil.getInstance().setWatchActivityInEmotionFragment(watchActivity);
        setFragment(DashboardChartSingleFragment.newInstance(OUTDOOR, CHART_TODAY, watchActivity), true);
    }

    @OnClick({R.id.ib_uv_detection, R.id.tv_uv_detection})
    public void clickUVDetection() {

    }

    private void setWatchActivity(WatchActivity wa) {
        long steps = wa.mIndoor.mSteps + wa.mOutdoor.mSteps;
        setData(wa);
        setEmotion(steps);
        watchActivity = wa;
    }

    private void setData(WatchActivity wa) {
        tv_indoor_steps_value.setText(BeanConvertor.getStepString(wa.mIndoor.mSteps));
        tv_outdoor_steps_value.setText(BeanConvertor.getStepString(wa.mOutdoor.mSteps));
//        String today = getString(R.string.dashboard_chart_today);
//        long indoorTimestamp = wa.mIndoor.mTimestamp;
//        if (indoorTimestamp > 0) {
//            StringBuilder sb = new StringBuilder(today);
//            sb.append(",");
//            sb.append(BeanConvertor.getLocalTimeString(indoorTimestamp, "K:mm a"));
//            tv_indoor_time.setText(sb);
//        }
//        long outdoorTimestamp = wa.mOutdoor.mTimestamp;
//        if (outdoorTimestamp > 0) {
//            StringBuilder sb = new StringBuilder(today);
//            sb.append(",");
//            sb.append(BeanConvertor.getLocalTimeString(outdoorTimestamp, "K:mm a"));
//            tv_outdoor_time.setText(sb);
//        }
    }

    private void setEmotion(long steps) {
        int emotion;
        if (steps < WatchActivity.STEP_ALMOST)
            emotion = EMOTION_LOW;
        else if (steps < WatchActivity.STEP_EXCELLENT)
            emotion = EMOTION_ALMOST;
        else
            emotion = EMOTION_EXCELLENT;
        setViews(emotion);
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

    private class IRetrieveCompleteListener implements KidActivityManager.ICompleteListener {

        private long start, end, timezoneOffset, kidId;

        IRetrieveCompleteListener(long start, long end, long timezoneOffset, long kidId) {
            this.start = start;
            this.end = end;
            this.timezoneOffset = timezoneOffset;
            this.kidId = kidId;
        }

        @Override
        public void onComplete(Object arg, int statusCode) {
            if (200 == statusCode && null != arg && arg instanceof RetrieveDataRep) {
                try {
                    handleRetrieveData(arg, start, end, timezoneOffset, kidId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//                finishLoadingDialog();
                ToastCommon.makeText(getContext(), R.string.dashboard_enqueue_fail_common);
            }
//            finishLoadingDialog();
        }

        @Override
        public void onFailed(String Command, int statusCode) {
//            finishLoadingDialog();
            ToastCommon.showToast(getContext(), Command);
        }
    }

    private void handleRetrieveData(Object arg, long start, long end, long timezoneOffset, long kidId) {
        RetrieveDataRep rep = (RetrieveDataRep) arg;
        List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();
        if (null == activitiesEntities || activitiesEntities.isEmpty()) {
            setEmotion(0);
            return;
        }
        List<WatchActivity> watchActivities = new ArrayList<>();
        long millisInDay = 1000 * 60 * 60 * 24;
        long timestamp = start;
        while (timestamp < end) {
            watchActivities.add(new WatchActivity(kidId, timestamp));
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

        for (WatchActivity act : watchActivities) {
            act.mIndoor.mTimestamp -= timezoneOffset;
            act.mOutdoor.mTimestamp -= timezoneOffset;
        }

        setWatchActivity(watchActivities.get(0));
    }

}
