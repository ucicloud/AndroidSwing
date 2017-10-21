package com.kidsdynamic.data.net.firmware;

import com.kidsdynamic.data.net.firmware.model.CurrentFirmwareVersion;
import com.kidsdynamic.data.net.firmware.model.FirmwareVersionEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2017/10/21.
 */

public interface FirmwareApi {

   /* Retrieve current Firmware version
    The API only return latest firmware version
    the Mac ID is on the path
    The file is on Amazon S3*/

//   200	Retrieve successfully
//   400	Bad request. The token is invalid or mac id is not present
//   500	Internal error. Please send me the error. I will fix it
    @GET("v1/fw/currentVersion/{macId}")
    Call<FirmwareVersionEntity> currentVersion(@Path("macId") String macId);


    //==========================
    //接口有疑问
    //=========================
//    200	Update successfully
//400	Bad request. The token is invalid or mac id is not present
//500	Internal error. Please send me the error. I will fix it
//    Send device firmware version to backend
//    Send the firmware version every time when user sync
    @PUT("v1/fw/firmwareVersion")
    Call<Object> sendFirmwareVersion(@Body CurrentFirmwareVersion currentFirmwareVersion);
}
