package com.kidsdynamic.swing.domain.datasource;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.event.EventApi;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.SubHostRequests;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.domain.DeviceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RemoteDataSource 服务的event数据 <br/>
 * date:   2017/12/8 11:58 <br/>
 */

public class RemoteDataSource {
    private Context applicationContext;

    private static RemoteDataSource INSTANCE = null;

    private final MutableLiveData<List<EventWithTodo>> mEventList;
    private final MutableLiveData<SubHostRequests> mSubHostRequests;
    private final MutableLiveData<Boolean> mIsLoadingSubHostRequests;

    private final HostApi hostApi;

    {
        mEventList = new MutableLiveData<>();
        mSubHostRequests = new MutableLiveData<>();
        mIsLoadingSubHostRequests = new MutableLiveData<>();
    }

    public static RemoteDataSource getInstance(Application application){
        if (INSTANCE == null) {
            synchronized (RemoteDataSource.class){
                if (INSTANCE == null) {
                    INSTANCE = new RemoteDataSource(application);
                }
            }
        }

        return INSTANCE;
    }

    private RemoteDataSource(Application application){
        this.applicationContext = application.getApplicationContext();

        hostApi =  ApiGen.getInstance(applicationContext).
                generateApi(HostApi.class,true);
    }

    public LiveData<Boolean> isLoadingSubHostList() {
        return mIsLoadingSubHostRequests;
    }

    public LiveData<SubHostRequests> getSuhHostList(String status) {
        mIsLoadingSubHostRequests.setValue(true);
        hostApi.subHostList(status).enqueue(new Callback<SubHostRequests>() {
            @Override
            public void onResponse(Call<SubHostRequests> call, Response<SubHostRequests> response) {
                int code = response.code();
                if(code == 200){
                    mSubHostRequests.setValue(response.body());
                    DeviceManager.updateSubHostRequestsInCache(response.body());
                }

                mIsLoadingSubHostRequests.setValue(false);
            }

            @Override
            public void onFailure(Call<SubHostRequests> call, Throwable t) {
                t.printStackTrace();
                mIsLoadingSubHostRequests.setValue(false);
            }
        });


        return mSubHostRequests;
    }

    public LiveData<List<EventWithTodo>> getEventList(){
        final EventApi eventApi = ApiGen.getInstance(applicationContext).
                generateApi(EventApi.class, true);

        eventApi.retrieveAllEventsWithTodo().enqueue(new Callback<List<EventWithTodo>>() {
            @Override
            public void onResponse(Call<List<EventWithTodo>> call, Response<List<EventWithTodo>> response) {

                if (response.code() == 200) {//获取成功
                    LogUtil2.getUtils().d("onResponse: " + response.body());

//                    new EventManager().saveEventForLogin(getContext(), response.body());

                    mEventList.setValue(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<EventWithTodo>> call, Throwable t) {
                Log.d("syncData", "retrieveAllEventsWithTodo error, ");
            }
        });

        return mEventList;
    }



}
