package com.kidsdynamic.swing;

import android.app.Application;
import android.content.Context;

/**
 * <br>author: only_app <br/>
 * date:   2017/10/14 13:59 <br/>
 */

public class SwingApplication extends Application {
    private static Context applicationContext;
    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = this;
    }

    public static Context getAppContext(){
        return applicationContext;
    }
}
