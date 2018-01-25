package com.kidsdynamic.data.net.firmware;

import com.kidsdynamic.data.net.firmware.model.CurrentFirmwareVersion;
import com.kidsdynamic.data.net.firmware.model.FirmwareVersionEntity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

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
    @GET("v1/fw/currentVersion")
    Call<FirmwareVersionEntity> currentVersion(@Query("macId") String macId, @Query("fwVersion") String firmwareVersion);


    //==========================
    //接口有疑问
    //=========================
    //    200	Update successfully
    //    400	Bad request. The token is invalid or mac id is not present
    //    500	Internal error. Please send me the error. I will fix it
    //    Send device firmware version to backend
    //    Send the firmware version every time when user sync
    @PUT("v1/fw/firmwareVersion")
    Call<Object> sendFirmwareVersion(@Body CurrentFirmwareVersion currentFirmwareVersion);

    /**
     * 下载固件更新文件
     *
     * @param url 文件地址
     * @return Call<ResponseBody>
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithUrl(@Url String url);
}
