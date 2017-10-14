package com.kidsdynamic.data.net.user;


import com.kidsdynamic.data.BuildConfig;
import com.kidsdynamic.data.net.user.model.LoginEntity;
import com.kidsdynamic.data.net.user.model.LoginSuccessRep;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//用户相关接口
public interface UserApi {

    String BASE_URL = BuildConfig.API_BASE_URL;


    @POST("v1/user/login")
    Call<LoginSuccessRep> login(@Body LoginEntity loginEntity);

}