package com.kidsdynamic.data.net.avatar;

import com.kidsdynamic.data.BuildConfig;
import com.kidsdynamic.data.net.user.model.UpdateKidAvatarRepEntity;
import com.kidsdynamic.data.net.user.model.UserInfo;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * date:   2017/10/18 19:51 <br/>
 */

public interface AvatarApi {
    String BASE_PHOTO_URL = BuildConfig.PHOTO_BASE_URL;
    String BASE_URL = BuildConfig.API_BASE_URL;

    static String param_upload = "upload";
    static String param_kidId = "kidId";

//https://juejin.im/entry/5879aa9cb123db005de3a2fa

//    200    File upload successfully
//    400    Bad request. Missing some parameters
//    500    Internal error. Please send me the error. I will fix it
    @Multipart
    @POST("v1/user/avatar/upload")
    Call<UserInfo> uploadUserAvatar(@Part MultipartBody.Part filePart);

//    200    File upload successfully
//    400    Bad request. Missing some parameters
//    500    Internal error. Please send me the error. I will fix it
    @Multipart
    @POST("v1/user/avatar/uploadKid")
    Call<UpdateKidAvatarRepEntity> uploadKidAvatar(
            @PartMap() Map<String, RequestBody> pramPart,
            @Part MultipartBody.Part filePart);


    //获取头像接口如下
    //https://childrenlab.s3.amazonaws.com/userProfile/{userProfile}
}
