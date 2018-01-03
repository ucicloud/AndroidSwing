package com.bobomee.android.htttp.okhttp;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.http.HTTP;

/**
 * <br>author: wzg@xdja.com <br/>
 * date:   2018/1/3 15:02 <br/>
 */

public class OkHttpConfig {
    private static HttpLoggingInterceptor.Level httpLogLevel =
            HttpLoggingInterceptor.Level.BODY;

    public static void setHttpLogDebug(){
        httpLogLevel =HttpLoggingInterceptor.Level.BODY;
    }

    public static void setHttpLogRelease(){
        httpLogLevel =HttpLoggingInterceptor.Level.HEADERS;
    }

    public static HttpLoggingInterceptor.Level getHttpLogLevel(){
        return httpLogLevel;
    }
}
