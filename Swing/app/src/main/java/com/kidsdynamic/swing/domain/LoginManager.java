package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kidsdynamic.data.net.Config;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.data.repository.disk.KidsDataStore;
import com.kidsdynamic.data.repository.disk.UserDataStore;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.utils.ConfigUtil;

/**
 * date:   2017/10/17 13:38 <br/>
 */

public class LoginManager {
    // 2017/10/17 登录相关的业务流程处理类

    //保存登录状态，当前用户-id，token
    public boolean cacheLoginOK(Context context, @NonNull UserProfileRep.UserEntity user){
        //保存登录状态
        PreferencesUtil.getInstance(context).
                setPreferenceBooleanValue(ConfigUtil.login_state, true);

        //保存登录者ID
        PreferencesUtil.getInstance(context).
                setPreferenceLongValue(ConfigUtil.label_user_id, user.getId());


        return true;
    }

    public boolean cacheToken(@NonNull String token){
        return PreferencesUtil.getInstance(SwingApplication.getAppContext()).
                setPreferenceStringValue(Config.KEY_TOKEN_LABEL, token);
    }

    public boolean clearToken() {
        return PreferencesUtil.getInstance(SwingApplication.getAppContext()).
                setPreferenceStringValue(Config.KEY_TOKEN_LABEL, "");
    }

    public boolean saveLoginData(@NonNull Context context, UserProfileRep userProfileRep){
        //首先清除；然后保存
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());
        UserDataStore userDataStore = new UserDataStore(dbUtil);
        userDataStore.clearAllData();

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        kidsDataStore.clearAllData();

        //开始保存
        userDataStore.save(BeanConvertor.getDBUser(userProfileRep));
        kidsDataStore.save(BeanConvertor.getDBKidsList(userProfileRep));

        return true;
    }


}
