package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.data.repository.disk.KidsDataStore;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.model.KidsEntityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 */

public class DeviceManager {
    public final static String BUNDLE_KEY_AVATAR = "AVATAR";
    public final static String BUNDLE_KEY_KID_NAME = "KID_NAME";
    public final static String BUNDLE_KEY_KID_ID = "KID_ID";
    public final static String BUNDLE_KEY_USER_ID = "USER_ID";

    private final static String key_focus_kids = "focus_kids";
    public static DB_Kids getFocusWatchInfo(Context context){

        long focusKidsId = getFocusKidsId();

        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());
        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        if(focusKidsId > 0){
            return kidsDataStore.getKidsInfo(focusKidsId);
        }else {
            long userId = LoginManager.getCurrentLoginUserId(context);
            List<DB_Kids> kidsInfoByParentId = kidsDataStore.getKidsInfoByParentId(userId);
            if(!ObjectUtils.isListEmpty(kidsInfoByParentId)){
                return kidsInfoByParentId.get(0);
            }
        }

        return null;
    }

    public static KidsEntityBean getFocusKidsInfo(Context context){
        DB_Kids focusWatchInfo = getFocusWatchInfo(context);

        return BeanConvertor.convert(focusWatchInfo);
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
        if(TextUtils.isEmpty(macId)){
            return "00:00:00:00:00:00";
        }

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

    public static boolean updateFocusKids(long kids){
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
        return preferencesUtil.setPreferenceLongValue(key_focus_kids, kids);
    }

    public static long getFocusKidsId(){
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
        return preferencesUtil.gPrefLongValue(key_focus_kids);
    }

    public boolean saveKidsData(@NonNull Context context, KidsWithParent kidsWithParent){
        //首先清除；然后保存
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        kidsDataStore.clearAllData();

        //开始保存
        List<DB_Kids> db_kidsList = new ArrayList<>(1);
        db_kidsList.add(BeanConvertor.getDBKidsInfo(kidsWithParent));
        kidsDataStore.save(db_kidsList);

        return true;
    }

    public static List<KidsEntityBean>  getAllKidsByUserId(Context context, long parentId){
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        List<DB_Kids> dbKids = kidsDataStore.getKidsInfoByParentId(parentId);

        List<KidsEntityBean> kidsEntityBeanList = new ArrayList<>();
        if(ObjectUtils.isListEmpty(dbKids)){
            return kidsEntityBeanList;
        }else {
            for (DB_Kids dbKidBean :dbKids) {
                kidsEntityBeanList.add(BeanConvertor.convert(dbKidBean));
            }
        }

        return kidsEntityBeanList;
    }

    public KidsEntityBean  getKidsInfo(Context context, long kidId){
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        DB_Kids dbKids = kidsDataStore.getKidsInfo(kidId);

        if(dbKids != null){
            return BeanConvertor.convert(dbKids);
        }

        return null;
    }

    public static KidsEntityBean getKidsByIdInCache(List<KidsEntityBean> kidsEntityBeans, long kidsId){
        if(!ObjectUtils.isListEmpty(kidsEntityBeans)){
            for (KidsEntityBean kidsEntityBean :
                    kidsEntityBeans) {
             if(kidsId == kidsEntityBean.getKidsId()){
                 return kidsEntityBean;
             }
            }
        }

        return null;
    }
}
