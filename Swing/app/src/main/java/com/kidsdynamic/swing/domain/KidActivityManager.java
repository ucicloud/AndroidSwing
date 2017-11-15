package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.util.Log;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_FormatActivity;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.activity.ActivityApi;
import com.kidsdynamic.data.net.activity.model.RetrieveDataRep;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.repository.disk.ActivityCloudDataStore;
import com.kidsdynamic.data.repository.disk.ActivityFormatDataStore;
import com.kidsdynamic.data.repository.disk.RawActivityDataStore;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.model.WatchActivity;
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

    public void getActivityDataFromCloud(Context context, final long kidId, final IFinishListener finishListener){

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
                super.onResponse(call, response);

                //如果获取数据成功，则开始更新本地数据
                if(response.code() == 200
                        && response.body() != null){
                    handleRetrieveActivityData(kidId, response);
                }

                if(finishListener != null){
                    finishListener.onFinish(response.code());
                }

            }

            @Override
            public void onFailure(Call<RetrieveDataRep> call, Throwable t) {
                super.onFailure(call, t);

                if(finishListener != null){
                    finishListener.onFailed("net error",-1);
                }
            }
        });
    }

    //把服务端数据经过简单转化后，更新到本地
    private void handleRetrieveActivityData(long kidId, Response<RetrieveDataRep> response) {
        RetrieveDataRep retrieveDataRep = response.body();

        if(ObjectUtils.isListEmpty(retrieveDataRep.getActivities())){
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

        if(!ObjectUtils.isListEmpty(dbFormatActivitys)){
            activityFormatDataStore.saveAll(dbFormatActivitys);
        }

        RawActivityDataStore rawActivityDataStore = new RawActivityDataStore(dbUtil);
        rawActivityDataStore.deleteByStatus(RawActivityDataStore.status_done);

    }

    public interface IFinishListener {
        void onFinish(Object arg);

        void onFailed(String Command, int statusCode);
    }
}
