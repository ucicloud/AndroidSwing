package com.kidsdynamic.data.repository.cloud;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.event.EventApi;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.utils.LogUtil2;

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

    {
        mEventList = new MutableLiveData<>();
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
