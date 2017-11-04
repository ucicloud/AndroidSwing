package com.kidsdynamic.data.net.kids;

import com.kidsdynamic.data.net.kids.model.BatteryStatus;
import com.kidsdynamic.data.net.kids.model.KidsAddRequest;
import com.kidsdynamic.data.net.kids.model.KidsInfoUpdateEntity;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.kids.model.WhoRegisterMacIDResp;
import com.kidsdynamic.data.net.user.model.KidInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * date:   2017/10/21 15:27 <br/>
 */

public interface KidsApi {

//    200 Data added successfully
//    400 Bad request. Missing some parameters
//    409 Conflict. The mac ID is already registered
//    500 Internal error. Please send me the error. I will fix it
    @POST("v1/kids/add")
    Call<KidsWithParent> addKid(@Body KidsAddRequest kidsAddRequest );

//200	Update successfully
//400	Bad request. Missing some parameters
//500	Internal error. Please send me the error. I will fix it
    @PUT("v1/kids/update")
    Call<KidsWithParent> kidsUpdate(@Body KidsInfoUpdateEntity kidsInfoUpdateEntity);

//    200	Delete successfully
//    400	Bad request. Missing some parameters
//    500	Internal error. Please send me the error. I will fix i
    @DELETE("v1/kids/delete")
    Call<Object> kidsDelete(@Query("kidId") int kidId);

    //Retrieve kids belong to the signed in user
    @GET("v1/kids/list")
    Call<List<KidInfo>> kidsList();

    //Retrieve kid and user information by MAC ID

//200	Retrieve successfully
//404	The user not found which means the MAC ID is not registered by anyone
//400	Bad request. Missing some parameters
//500	Internal error. Please send me the error. I will fix it
    @GET("v1/kids/whoRegisteredMacID")
    Call<WhoRegisterMacIDResp> whoRegisteredMacID(@Query("macId") String macId);

//200	Data added successfully
//400	Bad request. Missing some parameters
//500	Internal error. Please send me the error. I will fix it
    //Upload device battery life
    @POST("v1/kids/batteryStatus")
    Call<Object> batteryStatus (@Body BatteryStatus batteryStatus);


//200	Data added successfully
//400	Bad request. Missing some parameters
//500	Internal error. Please send me the error. I will fix it
    //Fix kid Mac ID reverse issue
    @PUT("v1/kids/updateKidRevertMacID")
    Call<Object> updateKidRevertMacID (@Query("macId") String macId, @Query("kidId") int kidId);


}
