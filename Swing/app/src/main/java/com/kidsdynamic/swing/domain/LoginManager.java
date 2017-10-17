package com.kidsdynamic.swing.domain;

import android.support.annotation.NonNull;

import com.kidsdynamic.data.net.Config;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.swing.SwingApplication;

/**
 * date:   2017/10/17 13:38 <br/>
 */

public class LoginManager {
    // TODO: 2017/10/17 登录相关的业务流程处理类

    public boolean cacheToken(@NonNull String token){
        // TODO: 2017/10/17
        return PreferencesUtil.getInstance(SwingApplication.getAppContext()).
                setPreferenceStringValue(Config.KEY_TOKEN_LABEL, token);
    }
}
