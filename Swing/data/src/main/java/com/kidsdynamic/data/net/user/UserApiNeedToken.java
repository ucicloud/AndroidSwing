package com.kidsdynamic.data.net.user;

import com.kidsdynamic.data.net.user.model.UserProfileRep;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 *
 * need token head api
 * Created by Administrator on 2017/10/17.
 */


public interface UserApiNeedToken {

//    200	Retrieve successfully
//    400	Bad request. The token is invalid
//    500	Internal error. Please send me the error. I will fix it
    @GET("/v1/user/retrieveUserProfile")
    Call<UserProfileRep> retrieveUserProfile();

}
