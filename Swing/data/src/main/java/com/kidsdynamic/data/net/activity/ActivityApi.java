package com.kidsdynamic.data.net.activity;

import com.kidsdynamic.data.net.activity.model.RawActivityDataEntity;
import com.kidsdynamic.data.net.activity.model.RetrieveDataRep;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/10/21.
 */

public interface ActivityApi {

//    Activity data
//    Upload with user's timezone offset (e.g. New York Timezone offset is -300)
//    When the data duplicate which is when server response 409 status, you can ignore it and process next data
//    200	Update successfully
//    400	Bad request. Missing some parameters, or the type is wrong
//    409	Conflict. The data is already exist
//    500	Internal error. Please send me the error. I will fix it
    @POST("v1/activity/uploadRawData")
    Call<Object> uploadRawData(@Body RawActivityDataEntity rawActivityDataEntity);


//    kidId	Yes	Integer	20
//    period	Yes	String	DAILY, WEEKLY MONTHLY, YEARLY

//    200	Receiving Data successfully
//    400	Bad request. Missing some parameters, or the type is wrong
//    500	Internal error. Please send me the error. I will fix it
    @GET("v1/activity/retrieveData")
    Call<RetrieveDataRep> retrieveData(@Query("kidId") String kidId, @Query("period") String period);


//    start	Yes	Long Timestamp	1491599032
//    end	    Yes	Long Timestamp	1498089090
//    kidId	Yes	Integer	1

//    200	Receiving Data successfully
//    400	Bad request. Missing some parameters, or the type is wrong
//    500	Internal error. Please send me the error. I will fix it
    @GET("v1/activity/retrieveDataByTime")
    Call<RetrieveDataRep> retrieveDataByTime (@Query("start") long start, @Query("end") long end,
                                              @Query("kidId")String kidId);


}
