package com.kidsdynamic.swing;

import android.app.Application;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.kidsdynamic.data.net.ApiGen;

/**
 * <br>author: only_app <br/>
 * date:   2017/10/14 13:59 <br/>
 */

public class SwingApplication extends Application {
    private static Context applicationContext;

    public static LocalBroadcastManager localBroadcastManager;
    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = this;

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        //配置服务器地址：该地址根据当前编译类型
        ApiGen.BASE_URL = BuildConfig.API_BASE_URL;
    }

    public static Context getAppContext(){
        return applicationContext;
    }
}
