package com.kidsdynamic.swing.domain;

import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.swing.SwingApplication;

/**
 * ConfigManager
 */

public class ConfigManager {
    private final static String KEY_LANGUAGE = "KEY_LANGUAGE";

    public static String getCurrentSelectedLanguage(){
        return PreferencesUtil.getInstance(SwingApplication.getAppContext()).
                gPrefStringValue(KEY_LANGUAGE);
    }
}
