package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.util.Log;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_FormatActivity;
import com.kidsdynamic.data.dao.DB_RawActivity;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.activity.ActivityApi;
import com.kidsdynamic.data.net.activity.model.RetrieveDataRep;
import com.kidsdynamic.data.net.activity.model.RetrieveHourlyDataRep;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.repository.disk.ActivityCloudDataStore;
import com.kidsdynamic.data.repository.disk.ActivityFormatDataStore;
import com.kidsdynamic.data.repository.disk.RawActivityDataStore;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * kid activity manager
 * Created by only_app on 2017/11/13.
 */

public class KidActivityManager {

    private int mTimezoneOffset;
    private long mSearchEnd;
    private long mSearchStart;
    private List<WatchActivity> mActivities;
    private long kidId;

    public void getActivityDataFromCloud(Context context, final long kidId, final IFinishListener finishListener) {

        Calendar cal = Calendar.getInstance();
        mTimezoneOffset = cal.getTimeZone().getOffset(cal.getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        mSearchEnd = cal.getTimeInMillis() + mTimezoneOffset;
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.SECOND, 1);
        mSearchStart = cal.getTimeInMillis() + mTimezoneOffset;
        mActivities = new ArrayList<>();


        //每天一个activity对象
        for (long activityTimeStamp = mSearchStart; activityTimeStamp < mSearchEnd; activityTimeStamp += 86400000) {
            mActivities.add(new WatchActivity(kidId, activityTimeStamp));
        }


        ActivityApi activityApi = ApiGen.getInstance(
                context.getApplicationContext()).
                generateApi(ActivityApi.class, true);

        //从服务端获取kids的activity数据，获取成功后保持到本地，然后把上传的原始数据中标记done的数据记录删除
        activityApi.retrieveDataByTime(
                (long) (mSearchStart / 1000),
                (long) (mSearchEnd / 1000),
                kidId).enqueue(new BaseRetrofitCallback<RetrieveDataRep>() {
            @Override
            public void onResponse(Call<RetrieveDataRep> call, Response<RetrieveDataRep> response) {

                //如果获取数据成功，则开始更新本地数据
                if (response.code() == 200
                        && response.body() != null) {
                    handleRetrieveActivityData(kidId, response);
                }

                if (finishListener != null) {
                    finishListener.onFinish(response.code());
                }

                super.onResponse(call, response);

            }

            @Override
            public void onFailure(Call<RetrieveDataRep> call, Throwable t) {
                super.onFailure(call, t);

                if (finishListener != null) {
                    finishListener.onFailed("net error", -1);
                }
            }
        });
    }

    //把服务端数据经过简单转化后，更新到本地
    private void handleRetrieveActivityData(long kidId, Response<RetrieveDataRep> response) {
        RetrieveDataRep retrieveDataRep = response.body();

        if (ObjectUtils.isListEmpty(retrieveDataRep.getActivities())) {
            return;
        }

        for (RetrieveDataRep.ActivitiesEntity activity : retrieveDataRep.getActivities()) {
            long timestamp = BeanConvertor.getLocalTimeStamp(activity.getReceivedDate());

            if (timestamp < mSearchStart || timestamp > mSearchEnd) {
                Log.d("swing", "Retrieve activity wrong time! " + activity.getReceivedDate());
                continue;
            }


            for (WatchActivity act : mActivities) {
                long actEnd = act.mIndoor.mTimestamp + 86400000;
                if (timestamp >= act.mIndoor.mTimestamp && timestamp <= actEnd) {
                    if (activity.type.equals(ActivityCloudDataStore.Activity_type_indoor)) {
                        act.mIndoor.mId = activity.getId();
                        act.mIndoor.mMacId = activity.getMacId();
                        act.mIndoor.mSteps = activity.getSteps();
                        act.mIndoor.mDistance = activity.getDistance();
                    } else if (activity.type.equals(ActivityCloudDataStore.Activity_type_outdoor)) {
                        act.mOutdoor.mId = activity.getId();
                        act.mOutdoor.mMacId = activity.getMacId();
                        act.mOutdoor.mSteps = activity.getSteps();
                        act.mOutdoor.mDistance = activity.getDistance();

                    }
                    break;
                }
            }
        }

        Collections.reverse(mActivities);

        for (WatchActivity act : mActivities) {
            act.mIndoor.mTimestamp -= mTimezoneOffset;
            act.mOutdoor.mTimestamp -= mTimezoneOffset;
        }

        updateActivityList(kidId, mActivities);
    }

    private void updateActivityList(long kidId, List<WatchActivity> list) {
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
//        ActivityCloudDataStore activityCloudDataStore = new ActivityCloudDataStore(dbUtil);
        ActivityFormatDataStore activityFormatDataStore = new ActivityFormatDataStore(dbUtil);


        //按照kids删除
        activityFormatDataStore.deleteByKidId(kidId);

        List<DB_FormatActivity> dbFormatActivitys = BeanConvertor.getDBFormatActivity(list);

        if (!ObjectUtils.isListEmpty(dbFormatActivitys)) {
            activityFormatDataStore.saveAll(dbFormatActivitys);
        }

        RawActivityDataStore rawActivityDataStore = new RawActivityDataStore(dbUtil);
        rawActivityDataStore.deleteByStatus(RawActivityDataStore.status_done);

    }

    public interface IFinishListener {
        void onFinish(Object arg);

        void onFailed(String Command, int statusCode);
    }

    public List<WatchActivity> loadActivityWithLocal(Context context) {
        KidsEntityBean kid = DeviceManager.getFocusKidsInfo(context);
        List<WatchActivity> rtn = new ArrayList<>();
        // 结束时间为今天的23时59分59秒
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long endTimestamp = cal.getTimeInMillis();
        // 起始时间为一年前今天的23时59分59秒，再加一秒
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.SECOND, 1);
        long startTimestamp = cal.getTimeInMillis();

        // 一天（24小时）的毫秒数
        // 毫秒*秒*分*时
        int millsInDay = 1000 * 60 * 60 * 24;

        while (startTimestamp < endTimestamp) {
            rtn.add(new WatchActivity(kid == null ? 0 : kid.getKidsId(), startTimestamp));
            startTimestamp += millsInDay;
        }

        Collections.reverse(rtn);
        if (kid == null)
            return rtn;

        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        ActivityFormatDataStore activityFormatDataStore = new ActivityFormatDataStore(dbUtil);
        List<DB_FormatActivity> formatActivities = activityFormatDataStore.getByKidId(kidId);
        List<WatchActivity> exportList;
        if (!ObjectUtils.isListEmpty(formatActivities)) {
            exportList = BeanConvertor.getWatchActivity(formatActivities);
            for (WatchActivity exportActivity : exportList) {
                for (WatchActivity preloadActivity : rtn) {
                    long actEnd = preloadActivity.mIndoor.mTimestamp + 86400000;
                    if (exportActivity.mIndoor.mTimestamp >= preloadActivity.mIndoor.mTimestamp && exportActivity.mIndoor.mTimestamp < actEnd) {
                        preloadActivity.mIndoor.mSteps += exportActivity.mIndoor.mSteps;
                        preloadActivity.mOutdoor.mSteps += exportActivity.mOutdoor.mSteps;
                        break;
                    }
                }
            }
        }

        RawActivityDataStore rawActivityDataStore = new RawActivityDataStore(dbUtil);
        List<DB_RawActivity> uploadList = rawActivityDataStore.getByMacId(kid.getMacId());
        for (DB_RawActivity raw : uploadList) {
            for (WatchActivity preloadActivity : rtn) {
                long actEnd = preloadActivity.mIndoor.mTimestamp + 86400000;
                long rawTime = raw.getTime();
                rawTime *= 1000;

                if (rawTime >= preloadActivity.mIndoor.mTimestamp && rawTime < actEnd) {
                    String[] arg = raw.getIndoorActivity().split(",");
                    int indoor = Integer.valueOf(arg[2]);
                    arg = raw.getOutdoorActivity().split(",");
                    int outdoor = Integer.valueOf(arg[2]);

                    preloadActivity.mIndoor.mSteps += indoor;
                    preloadActivity.mOutdoor.mSteps += outdoor;

                    break;
                }
            }
        }

        return rtn;
    }

    public WatchActivity getActivityOfDay(Context context) {
        List<WatchActivity> list = loadActivityWithLocal(context);

        return list.get(0);
    }

    public List<WatchActivity> getActivityOfWeek(Context context) {
        List<WatchActivity> rtn = new ArrayList<>();
        List<WatchActivity> list = loadActivityWithLocal(context);

        for (int idx = 0; idx < 7; idx++)
            rtn.add(list.get(idx));

        Collections.reverse(rtn);

        return rtn;
    }

    public List<WatchActivity> getActivityOfMonth(Context context) {
        List<WatchActivity> rtn = new ArrayList<>();
        List<WatchActivity> list = loadActivityWithLocal(context);

        for (int idx = 0; idx < 30; idx++)
            rtn.add(list.get(idx));
        Collections.reverse(rtn);

        return rtn;
    }

    public List<WatchActivity> getActivityOfYear(Context context) {
        List<WatchActivity> rtn = new ArrayList<>();
        long startTimestamp;
        long endTimestamp;

        KidsEntityBean kid = DeviceManager.getFocusKidsInfo(context);
        List<WatchActivity> list = null;
        if (kid == null)
            list = new ArrayList<>();
        else {
            DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
            ActivityFormatDataStore activityFormatDataStore = new ActivityFormatDataStore(dbUtil);
            List<DB_FormatActivity> formatActivities = activityFormatDataStore.getByKidId(kidId);
            if (!ObjectUtils.isListEmpty(formatActivities)) {
                list = BeanConvertor.getWatchActivity(formatActivities);
            }
        }

        if (null == list) {
            list = new ArrayList<>();
        }

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

        for (int idx = 0; idx < 12; idx++) {
            WatchActivity watchActivity = new WatchActivity(0, startTimestamp);

            for (WatchActivity src : list)
                watchActivity.addInTimeRange(src, startTimestamp, endTimestamp);
            rtn.add(watchActivity);

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
        return rtn;
    }

    public void getHourlyDataFromCloud(Context context, final long kidId, final ICompleteListener completeListener) {
        Calendar cal = Calendar.getInstance();
        mTimezoneOffset = cal.getTimeZone().getOffset(cal.getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        mSearchEnd = cal.getTimeInMillis() + mTimezoneOffset;
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.add(Calendar.SECOND, 1);
        mSearchStart = cal.getTimeInMillis() + mTimezoneOffset;

        ActivityApi activityApi = ApiGen.getInstance(
                context.getApplicationContext()).
                generateApi(ActivityApi.class, true);

        activityApi.retrieveHourlyDataByTime(
                (long) (mSearchStart / 1000),
                (long) (mSearchEnd / 1000),
                kidId).enqueue(new BaseRetrofitCallback<RetrieveHourlyDataRep>() {
            @Override
            public void onResponse(Call<RetrieveHourlyDataRep> call, Response<RetrieveHourlyDataRep> response) {
                RetrieveHourlyDataRep retrieveHourlyDataRep = response.body();
                //如果获取数据成功，则开始更新本地数据
                if (response.code() == 200
                        && retrieveHourlyDataRep != null) {
                    if (completeListener != null) {
                        completeListener.onFinish(retrieveHourlyDataRep.getActivities(), response.code());
                    }
                } else {
                    if (completeListener != null) {
                        completeListener.onFinish(null, response.code());
                    }
                }

                super.onResponse(call, response);

            }

            @Override
            public void onFailure(Call<RetrieveHourlyDataRep> call, Throwable t) {
                super.onFailure(call, t);

                if (completeListener != null) {
                    completeListener.onFailed("net error", -1);
                }
            }
        });
    }

    public interface ICompleteListener {
        void onFinish(Object arg, int statusCode);

        void onFailed(String Command, int statusCode);
    }
}
