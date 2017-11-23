package com.kidsdynamic.data.net.host;

import com.kidsdynamic.data.net.host.model.AcceptSubHostRequest;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRemovedKidRequest;
import com.kidsdynamic.data.net.host.model.SubHostRequests;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/10/21.
 */

public interface HostApi {
//200	Added successfully
//400	Bad request. Missing some parameters, or the type is wrong
//409	Conflict. The request is already exists
//500	Internal error. Please send me the error. I will fix it
    //Send sub host request to the host account
    @POST("v1/subHost/add")
    Call<RequestAddSubHostEntity> subHostAdd(@Body int hostId);


//    200	Accept successfully
//    400	Bad request. Missing some parameters, or the type is wrong. Or the logged in user doesn't have permission
//    500	Internal error. Please send me the error. I will fix it
//Accept the sub host request by allowing sub host to view the kids
//When adding a kid under a subhost, use this API as well
    @PUT("v1/subHost/accept")
    Call<RequestAddSubHostEntity> subHostAccept(@Body AcceptSubHostRequest acceptSubHostRequest);

//200	Accept successfully
//400	Bad request. Missing some parameters, or the type is wrong
//500	Internal error. Please send me the error. I will fix it
    @PUT("v1/subHost/deny")
    Call<Object> subHostDeny(@Body String subHostId);

//200	Receive list successfully
//500	Internal error. Please send me the error. I will fix it
//    If no status parameter, the API returns ALL of sub host belong to the user
//    It returns request from and request to Subhost list
//    status: PENDING, ACCEPTED, DENIED
    @GET("v1/subHost/list")
    Call<SubHostRequests> subHostList(@Body String status);

//200	Accept successfully
//400	Bad request. Missing some parameters, or the type is wrong
//401	Unauthorized. The user doesn't have permission
//500	Internal error. Please send me the error. I will fix it
    //It will delete a kid under the SubHost
    @PUT("v1/subHost/removeKid")
    Call<RequestAddSubHostEntity> subHostRemoveKid(@Body SubHostRemovedKidRequest subHostRemovedKidRequest);


//200	Delete successfully
//400	Bad request. Missing some parameters, or the type is wrong
//401	Unauthorized. The user doesn't have permission
//500	Internal error. Please send me the error. I will fix it
    @DELETE("v1/subHost/delete")
    Call<RequestAddSubHostEntity> subHostDelete(@Query("subHostId") int subHostId);

}
