package com.kidsdynamic.swing.domain;

import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.swing.SwingApplication;

/**
 * ConfigManager
 */

public class ConfigManager {
    private final static String KEY_LANGUAGE = "KEY_LANGUAGE";

    public final static String Avatar_Update_Action = "Avatar_UI_action";
    public final static String Tag_Key = "tag_key";
    public final static String Tag_KidsId_Key = "tag_kids_Id";
    public final static String Tag_Avatar_File_Uri_Key = "tag_avatar_path";

    public final static int Tag_Update_Type_Kids_Avatar = 2601;
    public final static int Tag_Update_Type_User_Avatar = 2602;

    public static String getCurrentSelectedLanguage(){
        return PreferencesUtil.getInstance(SwingApplication.getAppContext()).
                gPrefStringValue(KEY_LANGUAGE);
    }
}
