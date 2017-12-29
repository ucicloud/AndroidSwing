package com.kidsdynamic.swing.domain.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.kidsdynamic.swing.domain.datasource.RemoteDataSource;
import com.kidsdynamic.swing.model.WatchEvent;

import java.util.List;

/**
 * EventViewModel
 * date:   2017/12/8 11:52 <br/>
 */

public class EventViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mEventStatus = new MutableLiveData<>();
    private final LiveData<List<WatchEvent>> mWatchEventLiveData;

    private RemoteDataSource remoteDataSource = null;

    public EventViewModel(@NonNull Application application, RemoteDataSource remoteDataSource1) {
        super(application);
        remoteDataSource = remoteDataSource1;

        mWatchEventLiveData = Transformations.switchMap(mEventStatus, new Function<String, LiveData<List<WatchEvent>>>() {
            @Override
            public LiveData<List<WatchEvent>> apply(String input) {
                return remoteDataSource.getEventList();
            }
        });

    }

    public LiveData<List<WatchEvent>> getWatchEventLiveData() {
        return mWatchEventLiveData;
    }

    public void refreshEvent() {
        mEventStatus.setValue("");
    }

    public LiveData<Boolean> getLoadState() {
        return remoteDataSource.isLoadingEvent();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory{
        @NonNull
        private final Application mApplication;
        private final RemoteDataSource remoteDataSource;

        public Factory(@NonNull Application application,RemoteDataSource remoteDataSource){
            mApplication = application;
            this.remoteDataSource = remoteDataSource;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return ((T) new EventViewModel(mApplication,remoteDataSource));
        }
    }
}
