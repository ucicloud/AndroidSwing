package com.kidsdynamic.data.net.user;

import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.data.net.user.model.UserProfileRep;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 *
 * need token head api
 * Created by Administrator on 2017/10/17.
 */


public interface UserApiNeedToken {

//    200	Retrieve successfully
//    400	Bad request. The token is invalid
//    500	Internal error. Please send me the error. I will fix it
    @GET("v1/user/retrieveUserProfile")
    Call<UserProfileRep> retrieveUserProfile();

//    200 Update the registration id successfully
//    400 Bad request. The token is invalid
//    500 Internal error. Please send me the error. I will fix it
    @POST("v1/user/updateLanguage")
    Call<Object> updateLanguage(@Body String language);

//    200 send successfully
//    400 Bad request. The token is invalid
//    500 Internal error. Please send me the error. I will fix it
    @POST("v1/user/sendResetPasswordEmail")
    Call<Object> sendResetPasswordEmail(@Body Map<String, String> emailMap);

//    200 Get user successfully
//    400 Bad request. The email parameter is missing
//    404 Not found. Can't find user by the email
//    500 Internal error. Please send me the error. I will fix it
    @GET("v1/user/findByEmail")
    Call<UserInfo> findByEmail(@Query("email") String email);

}
