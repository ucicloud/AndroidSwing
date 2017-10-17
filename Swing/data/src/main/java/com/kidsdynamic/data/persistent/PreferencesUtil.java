package com.kidsdynamic.data.persistent;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PreferencesUtil {

    private SharedPreferences sp;
    private Editor sharedEditor;

    private static PreferencesUtil preferencesUtil;

    public static PreferencesUtil getInstance(Context context){
        if (context == null) {
            return null;
        }

        if(preferencesUtil == null){
            synchronized (PreferencesUtil.class){
                if(preferencesUtil == null){
                    preferencesUtil = new PreferencesUtil(context.getApplicationContext());
                }
            }
        }

        return preferencesUtil;
    }


    private PreferencesUtil(@NonNull Context aContext) {
        sp = PreferenceManager.getDefaultSharedPreferences(aContext);
        sharedEditor = sp.edit();
    }

    /**
     * Set a preference string value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceStringValue(String key, String value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putString(key, value);
            return editor.commit();
        } else {
            sharedEditor.putString(key, value);
            return sharedEditor.commit();
        }
    }

    /**
     * Set a preference boolean value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceBooleanValue(String key, boolean value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        } else {
            sharedEditor.putBoolean(key, value);
            return sharedEditor.commit();
        }
    }

    /**
     * Set a preference float value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceFloatValue(String key, float value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putFloat(key, value);
            return editor.commit();
        } else {
            sharedEditor.putFloat(key, value);
            return sharedEditor.commit();
        }
    }

    /**
     * Set a preference int value
     *
     * @param key   the preference key to set
     * @param value the value for this key
     */
    public boolean setPreferenceIntValue(String key, int value) {
        if (sharedEditor == null) {
            Editor editor = sp.edit();
            editor.putInt(key, value);
            return editor.commit();
        } else {
            sharedEditor.putInt(key, value);
            return sharedEditor.commit();
        }
    }

    //Private static getters
    // For string
    @Nullable
    public String gPrefStringValue(String key) {
        return sp.getString(key, "");
    }

    // For boolean
    public Boolean gPrefBooleanValue(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    // For int
    public int gPrefIntValue(String key) {
        return sp.getInt(key, 0);
    }

    // For float
    public float gPrefFloatValue(String key) {
        return sp.getFloat(key, 0);
    }
}
