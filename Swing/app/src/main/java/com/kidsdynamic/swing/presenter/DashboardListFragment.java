package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
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
import com.kidsdynamic.data.net.activity.model.RetrieveMonthlyActivityRep;
import com.kidsdynamic.data.repository.disk.ActivityCloudDataStore;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.KidActivityManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.utils.SwingFontsCache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
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

    public static final String DOOR_TYPE = "door_type";
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
    private DataAdapter dataAdapter;

    public static DashboardListFragment newInstance(int doorType, int listType, int emotion) {
        Bundle args = new Bundle();
        args.putInt(DOOR_TYPE, doorType);
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

        Bundle args = getArguments();

        mEmotion = args.getInt(EMOTION_INT, EMOTION_LOW);
        setEmotion(mEmotion);

        int door = args.getInt(DOOR_TYPE, INDOOR);
        if (INDOOR == door) {
            mRadioButtonIndoor.setChecked(true);
        } else {
            mRadioButtonOutdoor.setChecked(true);
        }
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        long kidId = 0;
        KidsEntityBean kid = DeviceManager.getFocusKidsInfo(getContext());
        if (null != kid) {
            kidId = kid.getKidsId();
        }
        int listType = args.getInt(LIST_TYPE, LIST_TODAY);
        if (LIST_TODAY == listType) {
            tv_title.setText(R.string.dashboard_chart_today);
            Calendar cld = Calendar.getInstance();
            int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            long end = cld.getTimeInMillis() + timezoneOffset;

            cld.add(Calendar.DAY_OF_MONTH, -1);
            cld.add(Calendar.SECOND, 1);
            long start = cld.getTimeInMillis() + timezoneOffset;

            new KidActivityManager().retrieveHourlyDataByTime(getContext(), kidId, start, end,
                    new IRetrieveCompleteListener(listType, kidId, start, end, timezoneOffset));
        } else if (LIST_WEEK == listType) {
            tv_title.setText(R.string.dashboard_chart_this_week);
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
                    new IRetrieveCompleteListener(listType, kidId, start, end, timezoneOffset));
        } else if (LIST_MONTH == listType) {
            tv_title.setText(R.string.dashboard_chart_this_month);
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
                    new IRetrieveCompleteListener(listType, kidId, start, end, timezoneOffset));
        } else {
            tv_title.setText(R.string.dashboard_chart_this_year);
            Calendar cld = Calendar.getInstance();
            int timezoneOffset = cld.getTimeZone().getOffset(cld.getTimeInMillis());

            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            long end = cld.getTimeInMillis() + timezoneOffset;

            cld.add(Calendar.MONTH, -11);
            cld.set(Calendar.DATE, 1);
            cld.set(Calendar.HOUR_OF_DAY, 0);
            cld.set(Calendar.MINUTE, 0);
            cld.set(Calendar.SECOND, 0);
            long start = cld.getTimeInMillis() + timezoneOffset;

            new KidActivityManager().retrieveMonthlyActivity(getContext(), kidId, start, end,
                    new IRetrieveCompleteListener(listType, kidId, start, end, timezoneOffset));
        }
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
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

                mViewRoot.setBackgroundResource(R.drawable.emotion_bg_purple);
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_green_main);
                mBorderButtonBg = R.drawable.border_button_bg_green;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_green_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.emotion_bg_green);
                break;

            default:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_orange_main);
                mBorderButtonBg = R.drawable.border_button_bg_orange;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(),
                        R.color.text_orange_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.emotion_bg_orange);
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

        private int listType;
        private long kidId;
        private long start, end, timezoneOffset;

        IRetrieveCompleteListener(int listType, long kidId, long start, long end, long timezoneOffset) {
            this.listType = listType;
            this.kidId = kidId;
            this.start = start;
            this.end = end;
            this.timezoneOffset = timezoneOffset;
        }

        @Override
        public void onComplete(Object arg, int statusCode) {
            if (200 == statusCode && null != arg) {
                if (LIST_TODAY == listType && arg instanceof RetrieveDataRep) {
                    new HourlyTask(DashboardListFragment.this)
                            .execute(listType, arg, start, end, timezoneOffset, kidId);
                } else if ((LIST_WEEK == listType || LIST_MONTH == listType) && arg instanceof RetrieveDataRep) {
                    new WeeklyAndMonthlyTask(DashboardListFragment.this)
                            .execute(listType, arg, start, end, timezoneOffset, kidId);
                } else if (LIST_YEAR == listType && arg instanceof RetrieveMonthlyActivityRep) {
                    new YearlyTask(DashboardListFragment.this)
                            .execute(listType, arg, start, end, timezoneOffset, kidId);
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

    private static class HourlyTask extends AsyncTask<Object, Integer, List<WatchActivity>> {

        private DashboardListFragment theFragment;

        HourlyTask(DashboardListFragment instance) {
            WeakReference<DashboardListFragment> wr = new WeakReference<>(instance);
            theFragment = wr.get();
        }

        @Override
        protected List<WatchActivity> doInBackground(Object... params) {
            int listType = (int) params[0];
            RetrieveDataRep rep = (RetrieveDataRep) params[1];
            List<WatchActivity> watchActivities = new ArrayList<>();
            List<RetrieveDataRep.ActivitiesEntity> activitiesEntities = rep.getActivities();

            long start = (Long) params[2];
            long end = (Long) params[3];
            long timezoneOffset = (Long) params[4];
            long millisInHour = 1000 * 60 * 60;
            long timestamp = start;
            while (timestamp < end) {
                watchActivities.add(new WatchActivity((Long) params[5], timestamp));
                timestamp += millisInHour;
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
                theFragment.handleHourlyData(watchActivities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleHourlyData(List<WatchActivity> watchActivities) {
        Bundle args = getArguments();
        int door = null != args ? args.getInt(DOOR_TYPE, INDOOR) : INDOOR;
        setDataAdapter(watchActivities, LIST_TODAY, door, mEmotionColor);
    }

    private static class WeeklyAndMonthlyTask extends AsyncTask<Object, Integer, List<WatchActivity>> {

        private int listType;
        private DashboardListFragment theFragment;

        WeeklyAndMonthlyTask(DashboardListFragment instance) {
            WeakReference<DashboardListFragment> wr = new WeakReference<>(instance);
            theFragment = wr.get();
        }

        @Override
        protected List<WatchActivity> doInBackground(Object... params) {
            listType = (int) params[0];
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
                theFragment.handleWeeklyAndMonthlyData(this.listType, watchActivities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleWeeklyAndMonthlyData(int listType, List<WatchActivity> watchActivities) {
        Bundle args = getArguments();
        int door = null != args ? args.getInt(DOOR_TYPE, INDOOR) : INDOOR;
        setDataAdapter(watchActivities, listType, door, mEmotionColor);
    }

    private static class YearlyTask extends AsyncTask<Object, Integer, List<WatchActivity>> {

        private DashboardListFragment theFragment;

        YearlyTask(DashboardListFragment instance) {
            WeakReference<DashboardListFragment> wr = new WeakReference<>(instance);
            theFragment = wr.get();
        }

        @Override
        protected List<WatchActivity> doInBackground(Object... params) {
            int listType = (int) params[0];
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
        Bundle args = getArguments();
        int door = null != args ? args.getInt(DOOR_TYPE, INDOOR) : INDOOR;
        setDataAdapter(watchActivities, LIST_YEAR, door, mEmotionColor);
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
