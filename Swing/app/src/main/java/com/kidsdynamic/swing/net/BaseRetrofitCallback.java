package com.kidsdynamic.swing.net;

import android.content.Intent;

import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.presenter.SignupActivity;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.yy.base.ActivityController;
import com.yy.base.utils.ToastCommon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * date:   2017/10/26 11:57 <br/>
 */

public abstract class BaseRetrofitCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        LogUtil2.getUtils().d("base callback onResponse code: " + response.code());

        // 如果登录token无效，则提示用户重新登录或者主动跳转到登录界面
        if(403 == response.code()){//token 无效，提示重新登录
            ToastCommon.makeText(SwingApplication.getAppContext(), R.string.token_invalid);

            try {
                ActivityController.getInstance().exit();

                ConfigUtil.logoutState();

                Intent intent = new Intent(SwingApplication.getAppContext(),
                        SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SwingApplication.getAppContext().startActivity(
                        intent);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(400 == response.code()){
            ToastCommon.makeText(SwingApplication.getAppContext(),
                    R.string.error_api_kid_who_registered_mac_id_400);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        LogUtil2.getUtils().d("base callback onFailure");
        t.printStackTrace();

        ToastCommon.makeText(SwingApplication.getAppContext(),R.string.cloud_api_call_net_error);
    }
}
