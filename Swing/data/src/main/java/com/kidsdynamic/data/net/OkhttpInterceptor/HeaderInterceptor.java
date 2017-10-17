package com.kidsdynamic.data.net.OkhttpInterceptor;

import com.kidsdynamic.data.persistent.PreferencesUtil;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 所有接口请求header添加类
 * date:   2017/10/17 15:06 <br/>
 */

public class HeaderInterceptor implements Interceptor {
    Map<String, String> headerMap;

    public HeaderInterceptor(Map<String, String> headerMap){
        this.headerMap = headerMap;
    }

    //从配置文件中读取token，然后设置到http header中
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        if(headerMap != null && !headerMap.isEmpty()){
            for (Map.Entry<String, String> entry : headerMap.entrySet()){
                builder.addHeader(entry.getKey(),entry.getValue());
            }
        }

        return chain.proceed(builder.build());
    }
}
