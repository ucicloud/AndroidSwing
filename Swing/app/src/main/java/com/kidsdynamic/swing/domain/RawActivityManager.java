package com.kidsdynamic.swing.domain;

import com.kidsdynamic.data.dao.DB_RawActivity;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.activity.ActivityApi;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.repository.disk.RawActivityDataStore;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.ble.ActivityModel;
import com.yy.base.utils.NetUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * RawActivityManager watch 原始数据管理类
 * Created by only_app on 2017/11/14.
 */

public class RawActivityManager {
    private static boolean isBackUploadRunning = false;

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static ExecutorService executorServiceInBack = Executors.newSingleThreadExecutor();

    public static void saveRawActivity(ActivityModel activityModel){
        if(activityModel == null){
            return;
        }

        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        RawActivityDataStore rawActivityDataStore = new RawActivityDataStore(dbUtil);

        rawActivityDataStore.saveRawActivityData(BeanConvertor.getDBRawActivity(activityModel));
    }

    public void uploadRawActivity(String macId, IFinishListener iFinishListener){

        //首先获取全部rawActivity
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        RawActivityDataStore rawActivityDataStore = new RawActivityDataStore(dbUtil);

        List<DB_RawActivity> rawActivities = rawActivityDataStore.getAllData(macId);

        //然后启动线程逐条上传，上传完成后，回调界面
        executorService.execute(
                new UpdateActivityRunnable(rawActivities,
                        rawActivityDataStore,
                        iFinishListener));

    }

    public void uploadRawActivityInBack(){

        if(isBackUploadRunning){
           return;
        }

        isBackUploadRunning = true;

        //首先获取全部rawActivity
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        RawActivityDataStore rawActivityDataStore = new RawActivityDataStore(dbUtil);

        List<DB_RawActivity> rawActivities = rawActivityDataStore.getAllData();

        //然后启动线程逐条上传，上传完成后，回调界面
        executorServiceInBack.execute(
                new UpdateActivityRunnable(rawActivities,
                        rawActivityDataStore,
                        backUploadFinishListener));

    }

    private  IFinishListener backUploadFinishListener = new IFinishListener() {
        @Override
        public void onFinish(Object arg) {
            isBackUploadRunning = false;
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            isBackUploadRunning = false;
        }
    };

    private class UpdateActivityRunnable implements Runnable{
        List<DB_RawActivity> rawActivities;
        RawActivityDataStore rawActivityDataStore;
        private IFinishListener iFinishListener;
        ActivityApi activityApi;

        public UpdateActivityRunnable(List<DB_RawActivity> rawActivities,
                                      RawActivityDataStore rawActivityDataStore,
                                      IFinishListener iFinishListener){
            this.rawActivities = rawActivities;
            this.rawActivityDataStore = rawActivityDataStore;
            this.iFinishListener = iFinishListener;

            activityApi = ApiGen.getInstance(
                    SwingApplication.getAppContext()).
                    generateApi(ActivityApi.class, true);
        }

        @Override
        public void run() {
            int successNum = 0;

            for (DB_RawActivity dbRawActivity : rawActivities) {
                if(dbRawActivity == null){
                    continue;
                }

                try {

                    //如果网络不可用，则进行下次
                    if(!NetUtils.isNetworkConnected(SwingApplication.getAppContext())){
                        continue;
                    }

                    //阻塞方式上传数据
                    Response<Object> execute = activityApi.uploadRawData(
                            BeanConvertor.getRawActivityDataEntity(dbRawActivity)).execute();

                    int code = execute.code();
                    if(code == 200 || code == 409){//上传成功或者服务端返回该数据已存在
                        //上传服务端成功后，开始删除本地数据
                        rawActivityDataStore.deleteById(dbRawActivity.getId());
                        successNum ++;
                    }
                }catch (IOException ioe){
                    //add 2017年12月8日14:42:14 only
                    //如果发生io异常，则停止本次数据上传
                    ioe.printStackTrace();
                    break;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (iFinishListener != null) {

                //有数据未上传成功
                if(successNum < rawActivities.size()){
                    iFinishListener.onFailed("upload_activity_exist_fail",-1);
                }else {
                    iFinishListener.onFinish(0);
                }
            }

        }
    }

    public static void delAllRawActivity(){
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        RawActivityDataStore rawActivityDataStore = new RawActivityDataStore(dbUtil);

        rawActivityDataStore.dealAll();
    }

    public interface IFinishListener {
        void onFinish(Object arg);

        void onFailed(String Command, int statusCode);
    }
}
