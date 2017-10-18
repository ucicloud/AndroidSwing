package com.kidsdynamic.data.net.avatar;

import com.kidsdynamic.data.net.user.model.UserInfo;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * date:   2017/10/18 19:51 <br/>
 */

public interface AvatarApi {
//https://juejin.im/entry/5879aa9cb123db005de3a2fa

    @Multipart
    @POST("/v1/user/avatar/upload")
    Call<UserInfo> uploadUserAvatar(@Part MultipartBody.Part file);
}
