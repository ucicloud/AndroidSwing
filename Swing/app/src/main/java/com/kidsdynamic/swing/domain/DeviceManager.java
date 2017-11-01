package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.text.TextUtils;

import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.data.repository.disk.KidsDataStore;
import com.kidsdynamic.swing.SwingApplication;

/**
 * Created by Administrator on 2017/10/27.
 */

public class DeviceManager {

    private final static String key_focus_kids = "focus_kids";
    public static DB_Kids getFocusWatchInfo(Context context){

        int focusKidsId = getFocusKidsId();
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());
        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);


        return kidsDataStore.getKidsInfo(focusKidsId);
    }

    public static String getMacID(String macAddress) {
        String[] separated = macAddress.split(":");
        String macId = "";
        for (String s : separated)
            macId += s;

        //macId = "1A2B3C4D5F01";
        return macId;
    }

    public static String getMacAddress(String macId) {
        if (macId.length() < 12)
            return "00:00:00:00:00:00";

        return String.format("%c%c:%c%c:%c%c:%c%c:%c%c:%c%c",
                macId.charAt(0), macId.charAt(1), macId.charAt(2), macId.charAt(3),
                macId.charAt(4), macId.charAt(5), macId.charAt(6), macId.charAt(7),
                macId.charAt(8), macId.charAt(9), macId.charAt(10), macId.charAt(11)
        );
    }

    public static boolean saveBindWatchBattery(String watchMacAddress, int battery){
        if(TextUtils.isEmpty(watchMacAddress) || battery <= 0){
            return false;
        }

        String macID = getMacID(watchMacAddress);

        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
        return preferencesUtil.setPreferenceIntValue(macID, battery);
    }

    public static int getCacheWatchBattery(String watchMacId){
        if(TextUtils.isEmpty(watchMacId)){
            return -1;
        }
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
        return preferencesUtil.gPrefIntValue(watchMacId);

    }

    public static boolean updateFocusKids(int kids){
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
        return preferencesUtil.setPreferenceIntValue(key_focus_kids, kids);
    }

    public static int getFocusKidsId(){
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
        return preferencesUtil.gPrefIntValue(key_focus_kids);
    }
}
