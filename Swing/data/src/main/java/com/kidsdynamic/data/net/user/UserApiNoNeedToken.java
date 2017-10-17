package com.kidsdynamic.data.net.user;


import com.kidsdynamic.data.BuildConfig;
import com.kidsdynamic.data.net.user.model.LoginEntity;
import com.kidsdynamic.data.net.user.model.LoginSuccessRep;
import com.kidsdynamic.data.net.user.model.RegisterEntity;
import com.kidsdynamic.data.net.user.model.RegisterFailResponse;
import com.kidsdynamic.data.net.user.model.UpdateProfileEntity;
import com.kidsdynamic.data.net.user.model.UpdateProfileSuccess;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

//用户相关接口
public interface UserApiNoNeedToken {

    String BASE_URL = BuildConfig.API_BASE_URL;

//    200 Success Login
//    400 Bad Request. Login failed
    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("/v1/user/login")
    Call<LoginSuccessRep> login(@Body LoginEntity loginEntity);


    //200 The token is valid
    //403 Forbidden. The token is not valid
    //•The API doesn't return any JSON
    @GET("/v1/user/isTokenValid")
    Call<RegisterFailResponse> checkTokenValid(@Query("email") String email, @Query("token") String token);

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("/v1/user/register")
    Call<RegisterFailResponse> registerUser(@Body RegisterEntity registerEntity);

    //200 The email is able to register ;
    //409 Conflict. The email is already registered
    //curl -X GET "http://localhost:8111/v1/user/isEmailAvailableToRegister?email=jack083001@gmail.com"
    @GET("/v1/user/isEmailAvailableToRegister")
    Call<Object> checkEmailAvailableToRegister(@Query("email") String email);


   /*
    @HeaderMap Map<String, String> headers
   @Headers({
          "Content-Type: application/json; charset=UTF-8",
            "Content-Type: application/json; charset=UTF-8"
    })*/

    @Headers("Content-Type: application/json; charset=UTF-8")
    @PUT("/v1/user/updateProfile")
    Call<UpdateProfileSuccess> updateProfile (@Body UpdateProfileEntity updateProfileEntity);



}