package com.kidsdynamic.swing;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.content.LocalBroadcastManager;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.swing.domain.LoginManager;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;
import com.yy.base.ActivityController;
import com.yy.base.handleException.CrashHandler;

/**
 * <br>author: only_app <br/>
 * date:   2017/10/14 13:59 <br/>
 */

public class SwingApplication extends Application {
    private static Context applicationContext;

    public static final String Action_Application_showLogin = "show_login";
    public static LocalBroadcastManager localBroadcastManager;

    private ApplicationReceiver applicationReceiver;
    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = this;

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        //配置服务器地址：该地址根据当前编译类型
        ApiGen.BASE_URL = BuildConfig.API_BASE_URL;
        ApiGen.BASE_PHOTO_URL = BuildConfig.PHOTO_BASE_URL;

        //非debug，则开启全局捕获异常
        if (!BuildConfig.DEBUG) {
            //全局异常捕获
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.setEnable(true);
            crashHandler.init(getApplicationContext());
        }
        else {
            //Debug时打印蓝牙
            ViseLog.getLogConfig().configAllowLog(true);//配置日志信息
            ViseLog.plant(new LogcatTree());//添加Logcat打印信息
        }


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Action_Application_showLogin);

        applicationReceiver = new ApplicationReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                applicationReceiver,
                intentFilter);
    }

    public static Context getAppContext(){
        return applicationContext;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ActivityController.getInstance().exit();

    }

    public class ApplicationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent == null){
                return;
            }

            //如果是显示登录界面的action
            if (Action_Application_showLogin.equals(intent.getAction())) {
                LoginManager.clearAcvShowLogin();
            }
        }
    }
}
