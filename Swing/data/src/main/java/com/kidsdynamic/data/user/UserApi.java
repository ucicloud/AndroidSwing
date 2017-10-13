package com.kidsdynamic.data.user;


import com.kidsdynamic.data.user.model.LoginEntity;
import com.kidsdynamic.data.user.model.LoginSuccessRep;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    String BASE_URL = "http://dev.childrenlab.com/";


    @POST("v1/user/login")
    Call<LoginSuccessRep> login(@Body LoginEntity loginEntity);

}