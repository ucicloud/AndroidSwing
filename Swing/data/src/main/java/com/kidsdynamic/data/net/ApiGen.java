package com.kidsdynamic.data.net;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bobomee.android.htttp.okhttp.okHttp;
import com.bobomee.android.htttp.retrofit2.Retrofit2Client;
import com.kidsdynamic.data.BuildConfig;
import com.kidsdynamic.data.net.OkhttpInterceptor.HeaderInterceptor;
import com.kidsdynamic.data.net.avatar.AvatarApi;
import com.kidsdynamic.data.persistent.PreferencesUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * date:   2017/10/17 15:12 <br/>
 */

public class ApiGen {
    public static String BASE_URL = BuildConfig.API_BASE_URL;
//    public static String AVATAR_BASE_URL = BASE_URL;

    public static String BASE_PHOTO_URL = BuildConfig.PHOTO_BASE_URL;

    private Context context;
    private static ApiGen apiGen;

    public static ApiGen getInstance(@NonNull Context context){

        if(apiGen == null){
            synchronized (ApiGen.class){
                if(apiGen == null){
                    apiGen = new ApiGen(context);
                }
            }
        }

        return apiGen;
    }

    private ApiGen(Context context){
        this.context = context.getApplicationContext();
    }

    public <T> T generateApi(Class<T> service, boolean isNeedToken){
        return Retrofit2Client.INSTANCE.getRetrofitBuilder()
                .client(getOkHttpClient(isNeedToken))
                .baseUrl(BASE_URL).build()
                .create(service);
    }

    //头像相关接口
    public <T> T generateApi4Avatar(Class<T> service){
        return Retrofit2Client.INSTANCE.getRetrofitBuilder()
                .client(getOkHttpClient(true))
                .baseUrl(BASE_URL).build()
                .create(service);
    }

    public <T> T generateApi4DownloadFile(Class<T> service) {
        return Retrofit2Client.INSTANCE.getRetrofitBuilder()
                .client(getOkHttpClientForDownload(true))
                .baseUrl(BASE_URL).build()
                .create(service);
    }

    private OkHttpClient getOkHttpClientForDownload(boolean isNeedToken) {
        OkHttpClient okHttpClient = okHttp.INSTANCE.getOkHttpClient();

        if (isNeedToken) {
            PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(context);
            String authToken = preferencesUtil.gPrefStringValue(Config.KEY_TOKEN_LABEL);
            Map<String, String> headMap = new HashMap<>(1);
            headMap.put(Config.HEADER_LABEL, authToken);

            //新增header添加拦截器
            return okHttpClient
                    .newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new HeaderInterceptor(headMap))
                    .build();
        }

        return okHttpClient;
    }

    private OkHttpClient getOkHttpClient(boolean isNeedToken){
        OkHttpClient okHttpClient = okHttp.INSTANCE.getOkHttpClient();

        if(isNeedToken){
            PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(context);
            String authToken = preferencesUtil.gPrefStringValue(Config.KEY_TOKEN_LABEL);
            Map<String, String> headMap = new HashMap<>(1);
            headMap.put(Config.HEADER_LABEL,authToken);

            //新增header添加拦截器
            return okHttpClient.newBuilder().addInterceptor(new HeaderInterceptor(headMap)).build();
        }

        return okHttpClient;
    }
}
