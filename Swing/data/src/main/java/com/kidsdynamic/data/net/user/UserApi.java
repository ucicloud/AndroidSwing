package com.kidsdynamic.data.net.user;


import com.kidsdynamic.data.BuildConfig;
import com.kidsdynamic.data.net.user.model.LoginEntity;
import com.kidsdynamic.data.net.user.model.LoginSuccessRep;
import com.kidsdynamic.data.net.user.model.RegisterEntity;
import com.kidsdynamic.data.net.user.model.RegisterFailResponse;
import com.kidsdynamic.data.net.user.model.UpdateProfileEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

//用户相关接口
public interface UserApi {

    String BASE_URL = BuildConfig.API_BASE_URL;


    @POST("v1/user/login")
    Call<LoginSuccessRep> login(@Body LoginEntity loginEntity);


    //200 The token is valid
    //403 Forbidden. The token is not valid
    //•The API doesn't return any JSON
    @GET("/v1/user/isTokenValid")
    Call<RegisterFailResponse> checkTokenValid(@Query("email") String email, @Query("token") String token);

    @POST("/v1/user/register")
    Call<RegisterFailResponse> registerUser(@Body RegisterEntity registerEntity);

    //200 The email is able to register ;
    //409 Conflict. The email is already registered
    //curl -X GET "http://localhost:8111/v1/user/isEmailAvailableToRegister?email=jack083001@gmail.com"
    @GET("/v1/user/isEmailAvailableToRegister")
    Call checkEmailAvailableToRegister(@Query("email") String email);


    @PUT("/v1/user/updateProfile")
    Call updateProfile (@Body UpdateProfileEntity updateProfileEntity);



}