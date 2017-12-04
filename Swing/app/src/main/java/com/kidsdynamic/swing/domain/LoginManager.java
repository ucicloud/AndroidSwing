package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.Config;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.data.repository.disk.KidsDataStore;
import com.kidsdynamic.data.repository.disk.UserDataStore;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.presenter.SignupActivity;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.yy.base.ActivityController;

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

    public static long getCurrentLoginUserId(Context context){
        return PreferencesUtil.getInstance(context.getApplicationContext()).
                gPrefLongValue(ConfigUtil.label_user_id);
    }

    public static DB_User getCurrentLoginUserInfo(){
        long userId = getCurrentLoginUserId(SwingApplication.getAppContext());
        if(userId > 0){
            DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
            UserDataStore userDataStore = new UserDataStore(dbUtil);
            return userDataStore.getById(userId);
        }
        return null;
    }

    public static String getUserName(@NonNull DB_User currentLoginUserInfo){
        String firstName = currentLoginUserInfo.getFirstName();
        String lastName = currentLoginUserInfo.getLastName();
        if(!TextUtils.isEmpty(firstName)
                && !TextUtils.isEmpty(lastName)){
            return firstName + " " + lastName;
        }

        if(!TextUtils.isEmpty(firstName)){
            return firstName;
        }

        if(!TextUtils.isEmpty(lastName)){
            return lastName;
        }

        return "";
    }

    public void clearCacheLoginData(Context context){
        PreferencesUtil.getInstance(SwingApplication.getAppContext()).
                setPreferenceBooleanValue(ConfigUtil.login_state, false);

        //删除登录者
        PreferencesUtil.getInstance(context.getApplicationContext()).
                setPreferenceLongValue(ConfigUtil.label_user_id,-1);
        //删除focus kids id
        DeviceManager.updateFocusKids(-1);


    }


    public static void clearAcvShowLogin(){
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
    }
}
