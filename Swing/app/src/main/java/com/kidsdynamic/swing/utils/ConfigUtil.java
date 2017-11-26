package com.kidsdynamic.swing.utils;

import android.content.Context;
import android.text.TextUtils;

import com.kidsdynamic.data.net.Config;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.domain.LoginManager;

/**
 * 保存，读取配置信息类
 *
 */
public class ConfigUtil {
	public static final String label_user_name = "userName";
	public static final String label_user_id = "userId";
	public static final String label_focus_kids_id = "focusKidId";
	public static final String label_notify_getreqward = "isNotifyReward";
	public static final String value_notify_getreqward_nomore = "nomore";
	public static final String login_state = "login";
	public static final String calendar_first_time = "calendar";
	public static final String event_list_first_time = "event_list";
	public static final String calendar_month_first_time = "calendar_month_list";
	public static final String calendar_main_first_time = "calendar_main_month_list";
	public static final String todo_detail_first_time = "todo_detail";


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

	public static void logoutState(){
		LoginManager loginManager = new LoginManager();
		loginManager.clearToken();
		loginManager.clearCacheLoginData(SwingApplication.getAppContext());

		/*PreferencesUtil.getInstance(SwingApplication.getAppContext()).
				setPreferenceBooleanValue(login_state, false);*/

		clearIntroFlag();

	}

	private static void clearIntroFlag(){
		PreferencesUtil.getInstance(SwingApplication.getAppContext()).
				setPreferenceBooleanValue(ConfigUtil.event_list_first_time,true);

		PreferencesUtil.getInstance(SwingApplication.getAppContext()).
				setPreferenceBooleanValue(ConfigUtil.calendar_first_time,true);

		PreferencesUtil.getInstance(SwingApplication.getAppContext()).
				setPreferenceBooleanValue(ConfigUtil.calendar_month_first_time,true);

		PreferencesUtil.getInstance(SwingApplication.getAppContext()).
				setPreferenceBooleanValue(ConfigUtil.calendar_main_first_time,true);

		PreferencesUtil.getInstance(SwingApplication.getAppContext()).
				setPreferenceBooleanValue(ConfigUtil.todo_detail_first_time,true);
	}

}
