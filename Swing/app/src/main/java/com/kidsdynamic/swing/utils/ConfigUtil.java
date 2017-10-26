package com.kidsdynamic.swing.utils;

import android.content.Context;
import android.text.TextUtils;

import com.kidsdynamic.data.net.Config;
import com.kidsdynamic.data.persistent.PreferencesUtil;

/**
 * 保存，读取配置信息类
 *
 */
public class ConfigUtil {
	private static final String lable_user_name = "userName";
	private static final String label_notify_getreqward = "isNotifyReward";
	private static final String value_notify_getreqward_nomore = "nomore";
	private static final String login_state = "login";


	/**
	 * 是否登录成功；
	 * @param context
	 * @return true 登录成功；
	 */
	public static boolean isLoginState(Context context){
		//get token
		String token = PreferencesUtil.getInstance(context.getApplicationContext())
				.gPrefStringValue(Config.KEY_TOKEN_LABEL);

		boolean loginFlag = PreferencesUtil.getInstance(context).gPrefBooleanValue(login_state, false);

		return !TextUtils.isEmpty(token) && loginFlag;
	}


}
