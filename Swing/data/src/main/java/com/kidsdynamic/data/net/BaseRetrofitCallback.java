package com.kidsdynamic.data.net;

import com.kidsdynamic.data.utils.LogUtil2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * date:   2017/10/26 11:57 <br/>
 */

public  class BaseRetrofitCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        LogUtil2.getUtils().d("base callback onResponse code: " + response.code());
        // TODO: 2017/10/26 如果登录token无效，则提示用户重新登录或者主动跳转到登录界面
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        LogUtil2.getUtils().d("base callback onFailure");
        t.printStackTrace();
    }
}
