package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DashboardListFragment
 * <p>
 * Created by Stefan on 2017/11/20.
 */

public class DashboardListFragment extends DashboardBaseFragment {

    private static final String LIST_TYPE = "list_type";
    private static final String EMOTION_INT = "emotion_int";

    @BindView(R.id.dashboard_list_root)
    View mViewRoot;
    @BindView(R.id.dashboard_list_view)
    ListView mListView;
    @BindView(R.id.dashboard_list_radio)
    RadioGroup mRadioGroup;
    @BindView(R.id.dashboard_list_indoor)
    RadioButton mRadioButtonIndoor;
    @BindView(R.id.dashboard_list_outdoor)
    RadioButton mRadioButtonOutdoor;

    private int mEmotion;
    private int mEmotionColor;

    private long kidId;
    private int listType;
    private DataAdapter dataAdapter;

    public static DashboardListFragment newInstance(int listType, int emotion) {
        Bundle args = new Bundle();
        args.putInt(LIST_TYPE, listType);
        args.putInt(EMOTION_INT, emotion);
        DashboardListFragment fragment = new DashboardListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view_left_action.setImageResource(R.drawable.icon_left);
        mRadioButtonIndoor.setChecked(true);
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        Bundle args = getArguments();

        mEmotion = args.getInt(EMOTION_INT, EMOTION_LOW);
        setEmotion(mEmotion);

        KidsEntityBean kid = DeviceManager.getFocusKidsInfo(getContext());
        if (null != kid) {
            kidId = kid.getKidsId();
        }
        listType = args.getInt(LIST_TYPE, LIST_TODAY);
        if (LIST_TODAY == listType) {
            tv_title.setText(R.string.dashboard_chart_today);
            showLoadingDialog(R.string.signup_login_wait);
            Calendar cld = Calendar.getInstance();
            int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            long end = cld.getTimeInMillis() + timezoneOffset;

            cld.add(Calendar.DAY_OF_MONTH, -1);
            cld.add(Calendar.SECOND, 1);
            long start = cld.getTimeInMillis() + timezoneOffset;

            new KidActivityManager().retrieveHourlyDataByTime(getContext(), start, end, kidId,
                    new IRetrieveCompleteListener(start, end, timezoneOffset));
        } else if (LIST_WEEK == listType) {
            tv_title.setText(R.string.dashboard_chart_this_week);
            showLoadingDialog(R.string.signup_login_wait);
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

            new KidActivityManager().retrieveDataByTime(getContext(), start, end, kidId,
                    new IRetrieveCompleteListener(start, end, timezoneOffset));
        } else if (LIST_MONTH == listType) {
            tv_title.setText(R.string.dashboard_chart_this_month);
            showLoadingDialog(R.string.signup_login_wait);
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

            new KidActivityManager().retrieveDataByTime(getContext(), start, end, kidId,
                    new IRetrieveCompleteListener(start, end, timezoneOffset));
        } else {
            tv_title.setText(R.string.dashboard_chart_this_year);
            showLoadingDialog(R.string.signup_login_wait);
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

            new KidActivityManager().retrieveDataByTime(getContext(), start, end, kidId,
                    new IRetrieveCompleteListener(start, end, timezoneOffset));
        }
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
//        mActivityMain.popFragment();
        getFragmentManager().popBackStack();
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener =
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (null == dataAdapter || dataAdapter.isEmpty()) {
                        return;
                    }
                    dataAdapter.setDoor(R.id.dashboard_list_indoor == checkedId ? INDOOR : OUTDOOR);
                    dataAdapter.notifyDataSetChanged();
                }
            };

    private void setEmotion(int emotion) {
        int mBorderButtonBg;
        ColorStateList mBorderButtonTextColorStateList;
        switch (emotion) {
            case EMOTION_LOW:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_blue_main);
                mBorderButtonBg = R.drawable.border_button_bg_blue;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_blue_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster01);
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_green_main);
                mBorderButtonBg = R.drawable.border_button_bg_green;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_green_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster02);
                break;

            default:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_orange_main);
                mBorderButtonBg = R.drawable.border_button_bg_orange;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_orange_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster03);
                break;
        }

        mRadioButtonIndoor.setBackgroundResource(mBorderButtonBg);
        mRadioButtonIndoor.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mRadioButtonIndoor.setTextColor(mBorderButtonTextColorStateList);
        mRadioButtonOutdoor.setBackgroundResource(mBorderButtonBg);
        mRadioButtonIndoor.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mRadioButtonOutdoor.setTextColor(mBorderButtonTextColorStateList);

        mEmotion = emotion;
    }

    private void setDataAdapter(List<?> list, int type, int door, int emotionColor) {
        dataAdapter = new DataAdapter<>(getContext(), list, type, door, emotionColor);
        mListView.setAdapter(dataAdapter);
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
            if (200 == statusCode && null != arg && arg instanceof RetrieveDataRep) {
                if (LIST_TODAY == listType) {
                    handleHourlyData(arg, start, end, timezoneOffset);
                } else if (LIST_WEEK == listType || LIST_MONTH == listType) {
                    handleWeeklyAndMonthlyData(arg, start, end, timezoneOffset);
                } else {
                    handleYearlyData(arg, start, end, timezoneOffset);
                }
                finishLoadingDialog();
            } else {
                finishLoadingDialog();
                ToastCommon.makeText(getContext(), R.string.dashboard_enqueue_fail_common);
            }
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            finishLoadingDialog();
            ToastCommon.showToast(getContext(), Command);
        }
    }

    private void handleHourlyData(Object arg, long start, long end, long timezoneOffset) {
        RetrieveDataRep rep = (RetrieveDataRep) arg;
        List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();
        if (null == activitiesEntities || activitiesEntities.isEmpty()) {
            setDataAdapter(null, LIST_TODAY, OUTDOOR, mEmotionColor);
            return;
        }
        List<WatchActivity> watchActivities = new ArrayList<>();
        long millisInHour = 1000 * 60 * 60;
        long timestamp = start;
        while (timestamp < end) {
            watchActivities.add(new WatchActivity(kidId, timestamp));
            timestamp += millisInHour;
        }
        for (WatchActivity act : watchActivities) {
            for (RetrieveDataRep.ActivitiesEntity entity : activitiesEntities) {
                long receiveDate = BeanConvertor.getLocalTimeStamp(entity.getReceivedDate());
                long actEnd = act.mIndoor.mTimestamp + millisInHour;
                if (receiveDate >= act.mIndoor.mTimestamp && receiveDate < actEnd) {
                    if (entity.type.equals(ActivityCloudDataStore.Activity_type_indoor)) {
                        act.mIndoor.mId = entity.getId();
                        act.mIndoor.mMacId = entity.getMacId();
                        act.mIndoor.mSteps = entity.getSteps();
                        act.mIndoor.mDistance = entity.getDistance();
                    } else if (entity.type.equals(ActivityCloudDataStore.Activity_type_outdoor)) {
                        act.mOutdoor.mId = entity.getId();
                        act.mOutdoor.mMacId = entity.getMacId();
                        act.mOutdoor.mSteps = entity.getSteps();
                        act.mOutdoor.mDistance = entity.getDistance();
                    }
                    break;
                }
            }
        }

        Collections.reverse(watchActivities);

        for (WatchActivity act : watchActivities) {
            act.mIndoor.mTimestamp -= timezoneOffset;
            act.mOutdoor.mTimestamp -= timezoneOffset;
        }

        setDataAdapter(watchActivities, LIST_TODAY, OUTDOOR, mEmotionColor);
    }

    private void handleWeeklyAndMonthlyData(Object arg, long start, long end, long timezoneOffset) {
        RetrieveDataRep rep = (RetrieveDataRep) arg;
        List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();
        if (null == activitiesEntities || activitiesEntities.isEmpty()) {
            setDataAdapter(null, listType, OUTDOOR, mEmotionColor);
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

        setDataAdapter(watchActivities, listType, OUTDOOR, mEmotionColor);
    }

    private void handleYearlyData(Object arg, long start, long end, long timezoneOffset) {
        RetrieveDataRep rep = (RetrieveDataRep) arg;
        List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();
        if (null == activitiesEntities || activitiesEntities.isEmpty()) {
            setDataAdapter(null, listType, OUTDOOR, mEmotionColor);
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

        List<WatchActivity> thisYear = new ArrayList<>();
        long startTimestamp;
        long endTimestamp;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.SECOND, -1);
        cal.add(Calendar.MONTH, 2);
        // 结束时间为一年前下个月最后一天 23时59分59秒
        endTimestamp = cal.getTimeInMillis();

        cal.add(Calendar.SECOND, 1);
        cal.add(Calendar.MONTH, -1);
        // 起始时间为一年前下个月的第一天 0时0分0秒
        startTimestamp = cal.getTimeInMillis();

        for (int i = 0; i < 12; i++) {
            WatchActivity watchActivity = new WatchActivity(0, startTimestamp);

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
            cal.setTimeInMillis(startTimestamp);
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTimestamp = cal.getTimeInMillis();

            // 下一个起始时间加一个月后，再减去一秒，作为本月的结束时间
            cal.setTimeInMillis(startTimestamp);
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.SECOND, -1);
            endTimestamp = cal.getTimeInMillis();
        }

        Collections.reverse(watchActivities);

        for (WatchActivity act : watchActivities) {
            act.mIndoor.mTimestamp -= timezoneOffset;
            act.mOutdoor.mTimestamp -= timezoneOffset;
        }

        setDataAdapter(thisYear, listType, OUTDOOR, mEmotionColor);
    }

    private class DataAdapter<E> extends BaseAdapter {

        private Context context;
        private List<E> items = new ArrayList<>();
        private int type;
        private int door;
        private int emotionColor;
        private int millisInHour = 1000 * 60 * 60;

        DataAdapter(Context context, List<E> items, int type, int door, int emotionColor) {
            this.context = context;
            if (null != items) {
                this.items.addAll(items);
            }
            this.type = type;
            this.door = door;
            this.emotionColor = emotionColor;
        }

        void setDoor(int door) {
            this.door = door;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (LIST_TODAY == type) {
                TodayHolder holder;
                if (null == convertView) {
                    convertView = LayoutInflater.from(context)
                            .inflate(R.layout.item_activity_today, parent, false);
                    holder = new TodayHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (TodayHolder) convertView.getTag();
                }
                holder.tvTimeStart.setTextColor(emotionColor);
                holder.tvTimeEnd.setTextColor(emotionColor);
                holder.tvStepsValue.setTextColor(emotionColor);
                holder.tvStepsUnit.setTextColor(emotionColor);

                Object obj = getItem(position);
                if (obj instanceof WatchActivity) {
                    WatchActivity wa = (WatchActivity) obj;
                    String strStart = "", strEnd = "";
                    long start = INDOOR == door ? wa.mIndoor.mTimestamp : wa.mOutdoor.mTimestamp;
                    if (start > 0) {
                        strStart = BeanConvertor.getLocalTimeString(start, "KK:mm a");
                        long end = start + millisInHour - 1000;
                        strEnd = BeanConvertor.getLocalTimeString(end, "KK:mm a");
                    }
                    holder.tvTimeStart.setText(strStart);
                    holder.tvTimeEnd.setText(strEnd);
                    long steps = door == INDOOR ? wa.mIndoor.mSteps : wa.mOutdoor.mSteps;
                    holder.tvStepsValue.setText(BeanConvertor.getStepString(steps));
                }
            } else {
                CommonHolder holder;
                if (null == convertView) {
                    convertView = LayoutInflater.from(context)
                            .inflate(R.layout.item_activity_common, parent, false);
                    holder = new CommonHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (CommonHolder) convertView.getTag();
                }
                holder.tvTimeMain.setTextColor(emotionColor);
                holder.tvStepsValue.setTextColor(emotionColor);
                holder.tvStepsUnit.setTextColor(emotionColor);
                Object obj = getItem(position);
                if (obj instanceof WatchActivity) {
                    WatchActivity wa = (WatchActivity) obj;
                    long timestamp = INDOOR == door ? wa.mIndoor.mTimestamp : wa.mOutdoor.mTimestamp;
                    String strTimeMain = "", strTimeSub = "";
                    if (timestamp > 0) {
                        if (LIST_WEEK == type || LIST_MONTH == type) {
                            strTimeMain = BeanConvertor.getLocalTimeString(timestamp, "MMMM dd,yyyy");
                            strTimeSub = BeanConvertor.getLocalTimeString(timestamp, "EEEE");
                        } else {
                            strTimeMain = BeanConvertor.getLocalTimeString(timestamp, "MMMM");
                        }
                    }
                    holder.tvTimeMain.setText(strTimeMain);
                    holder.tvTimeSub.setText(strTimeSub);
                    long steps = INDOOR == door ? wa.mIndoor.mSteps : wa.mOutdoor.mSteps;
                    holder.tvStepsValue.setText(BeanConvertor.getStepString(steps));
                }
            }
            return convertView;
        }
    }

    static class TodayHolder extends BaseHolder {
        @BindView(R.id.tvTimeStart)
        TextView tvTimeStart;
        @BindView(R.id.tvTimeEnd)
        TextView tvTimeEnd;

        TodayHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

    static class CommonHolder extends BaseHolder {
        @BindView(R.id.tvTimeMain)
        TextView tvTimeMain;
        @BindView(R.id.tvTimeSub)
        TextView tvTimeSub;

        CommonHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

    static class BaseHolder {
        @BindView(R.id.tvStepsValue)
        TextView tvStepsValue;
        @BindView(R.id.tvStepsUnit)
        TextView tvStepsUnit;
    }

}
