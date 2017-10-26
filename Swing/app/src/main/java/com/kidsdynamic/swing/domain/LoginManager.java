package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidsdynamic.data.net.Config;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.data.repository.disk.KidsDataStore;
import com.kidsdynamic.data.repository.disk.UserDataStore;
import com.kidsdynamic.swing.SwingApplication;

/**
 * date:   2017/10/17 13:38 <br/>
 */

public class LoginManager {
    // TODO: 2017/10/17 登录相关的业务流程处理类

    public boolean cacheToken(@NonNull String token){
        return PreferencesUtil.getInstance(SwingApplication.getAppContext()).
                setPreferenceStringValue(Config.KEY_TOKEN_LABEL, token);
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
