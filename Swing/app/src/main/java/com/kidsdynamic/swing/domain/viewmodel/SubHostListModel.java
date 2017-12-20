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

import com.kidsdynamic.data.net.host.model.SubHostRequests;
import com.kidsdynamic.swing.domain.datasource.RemoteDataSource;

/**
 * SubHostListModel.java
 * <p>
 * Created by only on 17/12/10.
 */

public class SubHostListModel extends AndroidViewModel {

    private final MutableLiveData<String> mSubHostStatus = new MutableLiveData<>();

    private final LiveData<SubHostRequests> subHostRequestsLiveData;

    private RemoteDataSource remoteDataSource = null;

    private SubHostListModel(Application application, RemoteDataSource remoteDataSource1) {
        super(application);
        this.remoteDataSource = remoteDataSource1;

        subHostRequestsLiveData = Transformations.switchMap(mSubHostStatus,
                new Function<String, LiveData<SubHostRequests>>() {
            @Override
            public LiveData<SubHostRequests> apply(String input) {
                return remoteDataSource.getSuhHostList(input);
            }
        });
    }

    public LiveData<SubHostRequests> getSubHostRequestsLiveData() {
        return subHostRequestsLiveData;
    }

    public void refreshSubHostData(String status) {
        mSubHostStatus.setValue(status);
    }


    public LiveData<Boolean> getLoadState() {
        return remoteDataSource.isLoadingSubHostList();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final RemoteDataSource remoteDataSource;

        public Factory(@NonNull Application application, RemoteDataSource remoteDataSource) {
            mApplication = application;
            this.remoteDataSource = remoteDataSource;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SubHostListModel(mApplication, remoteDataSource);
        }
    }
}
