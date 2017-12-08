package com.kidsdynamic.swing.domain.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * EventViewModel
 * date:   2017/12/8 11:52 <br/>
 */

public class EventViewModel extends AndroidViewModel {

    public EventViewModel(@NonNull Application application) {
        super(application);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory{
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application){
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return ((T) new EventViewModel(mApplication));
        }
    }
}
