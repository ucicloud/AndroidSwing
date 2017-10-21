package com.kidsdynamic.data.net.kids;

import com.kidsdynamic.data.net.kids.model.KidsAddRequest;
import com.kidsdynamic.data.net.kids.model.KidsAddSuccessRep;
import com.kidsdynamic.data.net.user.model.UserInfo;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * date:   2017/10/21 15:27 <br/>
 */

public interface KidsApi {

//    200 Data added successfully
//    400 Bad request. Missing some parameters
//    409 Conflict. The mac ID is already registered
//    500 Internal error. Please send me the error. I will fix it
    @POST("/v1/kids/add")
    Call<KidsAddSuccessRep> addKid(@Body KidsAddRequest kidsAddRequest );
}
