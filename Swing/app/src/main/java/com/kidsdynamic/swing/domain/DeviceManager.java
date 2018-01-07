package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_EventKids;
import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.firmware.FirmwareApi;
import com.kidsdynamic.data.net.firmware.model.CurrentFirmwareVersion;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRequests;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.user.model.KidInfo;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.data.repository.disk.EventKidsStore;
import com.kidsdynamic.data.repository.disk.KidsDataStore;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.presenter.MainFrameActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kidsdynamic.swing.domain.BeanConvertor.getUTCTimeStamp;

/**
 * Created by Administrator on 2017/10/27.
 */

public class DeviceManager {
    public final static String BUNDLE_KEY_AVATAR = "AVATAR";
    public final static String BUNDLE_KEY_AVATAR_FILE = "AVATAR_FILE";
    public final static String BUNDLE_KEY_KID_NAME = "KID_NAME";
    public final static String BUNDLE_KEY_KID_ID = "KID_ID";
    public final static String BUNDLE_KEY_USER_ID = "USER_ID";
    public final static String BUNDLE_KEY_ASSIGN_KID_ID = "ASSIGN_KID_ID";

    private final static String key_focus_kids = "focus_kids";
    private final static String key_SubHostRequests = "SubHostRequests";

    //分享类型：0 无；1 自己分享给别人；2 别人分享给自己
    public final static int kidsType_null = 0;
    public final static int kidsType_my_kids_shared = 1;
    public final static int kidsType_other_kids = 2;


    public static DB_Kids getFocusWatchInfo(Context context){

        long focusKidsId = getFocusKidsId();

        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());
        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        if(focusKidsId > 0){
            //如果focusId 有缓存，但是数据库无数据，则删除缓存文件中保持；这种情况有可能是因为
            //focusId为共享设备，但其他用户删除了共享
            DB_Kids kidsInfo = kidsDataStore.getKidsInfo(focusKidsId);

            if(kidsInfo == null){
                //删除focus kids id
                DeviceManager.updateFocusKids(-1);

                return getKidsIfNoFocusCache(context, kidsDataStore);
            }

            return kidsInfo;
        }else {
            DB_Kids kidsInfoByParentId = getKidsIfNoFocusCache(context, kidsDataStore);
            if (kidsInfoByParentId != null) return kidsInfoByParentId;
        }

        return null;
    }

    private static void updateKidsIfNoFocusCache(){
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        getKidsIfNoFocusCache(SwingApplication.getAppContext(), kidsDataStore);
    }

    @Nullable
    private static DB_Kids getKidsIfNoFocusCache(Context context, KidsDataStore kidsDataStore) {
        long userId = LoginManager.getCurrentLoginUserId(context);
        List<DB_Kids> kidsInfoByParentId = kidsDataStore.getKidsInfoByParentId(userId);
        if(!ObjectUtils.isListEmpty(kidsInfoByParentId)){

            //如果当前没有缓存focuskids，那边把查询到第一个设置为当前focus
            updateFocusKids(kidsInfoByParentId.get(0).getKidsId());

            return kidsInfoByParentId.get(0);
        }

        //如果当前用户没有自己的设备，则查询下共享设备
        List<DB_Kids> allKidsByShared_dbKids = getAllKidsByShared_DBKids(context);
        if(!ObjectUtils.isListEmpty(allKidsByShared_dbKids)){

            //如果当前没有缓存focuskids，那边把查询到第一个设置为当前focus
            updateFocusKids(allKidsByShared_dbKids.get(0).getKidsId());

            return allKidsByShared_dbKids.get(0);
        }

        return null;
    }

    public static KidsEntityBean getFocusKidsInfo(Context context){
        DB_Kids focusWatchInfo = getFocusWatchInfo(context);

        return null != focusWatchInfo ? BeanConvertor.convert(focusWatchInfo) : null;
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

    public static void checkAndUpdateFocus(long kidsId4Del){

        long focusKidsId = getFocusKidsId();
        if (focusKidsId > 0 && kidsId4Del > 0 &&
                focusKidsId == kidsId4Del) {

            try {
                //如果删除的kids为当前focus kids
                updateFocusKids(-1);
                //如果当前依然有kids，则更新当前cache
                updateKidsIfNoFocusCache();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //在数据库中新增一个kids
    public boolean saveKidsData(@NonNull Context context, KidsWithParent kidsWithParent){
        //首先清除；然后保存
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);

        //开始保存
        List<DB_Kids> db_kidsList = new ArrayList<>(1);
        db_kidsList.add(BeanConvertor.getDBKidsInfo(kidsWithParent));
        kidsDataStore.save(db_kidsList);

        return true;
    }

    //在本地数据库中，保存
    public static boolean saveKidsData4Shared(List<RequestAddSubHostEntity> requestTo){

        //获取到所有状态为 accepted的数据
        if(ObjectUtils.isListEmpty(requestTo)){
            return true;
        }

        List<DB_Kids> dbKidsList_shared = new ArrayList<>();

        for (RequestAddSubHostEntity requestToEntity : requestTo) {
            if(requestToEntity.getStatus().equals(WatchContact.User.STATUS_ACCEPTED)){
                List<KidInfo> kids = requestToEntity.getKids();

                List<DB_Kids> dbKidsForShared = getDBKidsForShared(requestToEntity.getId(),
                        requestToEntity.getRequestToUser().getId(),
                        kids);

                if(!ObjectUtils.isListEmpty(dbKidsForShared)){
                    dbKidsList_shared.addAll(dbKidsForShared);
                }
            }

        }

        //如果有别人分享给自己的watch，则先清除本地的分享给自己的watch，然后保持此次的数据
        if(!ObjectUtils.isListEmpty(dbKidsList_shared)){
            //先清除本地缓存的
            DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
            KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
            kidsDataStore.clearKidsInfoForShareType(kidsType_other_kids);

            kidsDataStore.save(dbKidsList_shared);
        }

        return true;

    }

    public static void delKidsInDB(long kidsId){
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        kidsDataStore.delKidsInfo(kidsId);
    }

    private static List<DB_Kids> getDBKidsForShared(long suhHostId, long parentId, List<KidInfo> kidInfos){
        List<DB_Kids> db_kidsList = new ArrayList<>(kidInfos.size());

        for (KidInfo kidInfo : kidInfos) {

            DB_Kids db_kids = new DB_Kids();

            db_kids.setKidsId(kidInfo.getId());
            db_kids.setName(kidInfo.getName());
            db_kids.setLastUpdate(System.currentTimeMillis());
            db_kids.setDateCreated(getUTCTimeStamp(kidInfo.getDateCreated()));
            db_kids.setMacId(kidInfo.getMacId());
            db_kids.setProfile(kidInfo.getProfile());
            db_kids.setParentId(parentId);
            db_kids.setFirmwareVersion("");

            db_kids.setBattery(-1);
            db_kids.setSubHostId((long)suhHostId);
            db_kids.setShareType(kidsType_other_kids);//别人分享给自己的watch

            db_kidsList.add(db_kids);
        }

        return db_kidsList;

    }

    public static boolean isContain(List<KidsEntityBean> kidsEntityBeans, KidsWithParent kidsWithParent){
        boolean isContain = false;

        if(!ObjectUtils.isListEmpty(kidsEntityBeans) && kidsWithParent != null){
            for (KidsEntityBean kidsEntityBean : kidsEntityBeans) {
                if(kidsEntityBean.getKidsId() == kidsWithParent.getId()){
                    isContain = true;
                    break;
                }
            }
        }

        return isContain;
    }

    public static List<KidsEntityBean>  getAllKidsAndShared(Context context){

        List<KidsEntityBean> allKidsByUserId =
                getAllKidsByUserId(context,  LoginManager.getCurrentLoginUserId(context));

        //其他用户共享的kids
        List<KidsEntityBean> allKidsByShared = getAllKidsByShared(context);
        if(!ObjectUtils.isListEmpty(allKidsByShared)){
            allKidsByUserId.addAll(allKidsByShared);
        }

        return allKidsByUserId;
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

    //获取他人分享给自己的watch list
    public static List<DB_Kids>  getAllKidsByShared_DBKids(Context context){
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        List<DB_Kids> dbKids = kidsDataStore.getKidsInfoByShared(kidsType_other_kids);

        if(!ObjectUtils.isListEmpty(dbKids)){
            return dbKids;
        }


        return null;
    }

    //获取他人分享给自己的watch list
    public static List<KidsEntityBean>  getAllKidsByShared(Context context){
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());

        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        List<DB_Kids> dbKids = kidsDataStore.getKidsInfoByShared(kidsType_other_kids);

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


    public static KidsEntityBean  getKidsInfo(Context context, long kidId){
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

    public static List<WatchContact.Kid> getKidsByIdInCache(List<KidsEntityBean> kidsEntityBeans, List<Long> kidsIdList){
        List<WatchContact.Kid> kidsForUI = new ArrayList<>(5);
        if(!ObjectUtils.isListEmpty(kidsEntityBeans)){
            for (KidsEntityBean kidsEntityBean :
                    kidsEntityBeans) {
             if(kidsIdList.contains(kidsEntityBean.getKidsId())){

                 WatchContact.Kid kidsForUIRaw = BeanConvertor.getKidsForUI(kidsEntityBean);
                 kidsForUIRaw.mBound = true;
                 kidsForUIRaw.mLabel = kidsForUIRaw.mName;

                 kidsForUI.add(kidsForUIRaw);
             }
            }
        }

        return kidsForUI;
    }


    //界面显示需要的kids信息list
    public static List<WatchContact.Kid> getKidsForUI(Context context, long parentId){
        List<KidsEntityBean> kidsByUserId = getAllKidsByUserId(context, parentId);

        List<WatchContact.Kid> kidsForUI = new ArrayList<>();
        if(!ObjectUtils.isListEmpty(kidsByUserId)){
            for (KidsEntityBean kidsEntityBean :
                    kidsByUserId) {
                WatchContact.Kid kidsForUIRaw = BeanConvertor.getKidsForUI(kidsEntityBean);
                kidsForUIRaw.mBound = true;
                kidsForUIRaw.mLabel = kidsForUIRaw.mName;

                kidsForUI.add(kidsForUIRaw);
            }
        }

        return kidsForUI;

    }
    //界面显示需要的kids信息list
    public static List<WatchContact.Kid> getKidsForUI_sharedKids(Context context){
        List<KidsEntityBean> kidsByUserId = getAllKidsByShared(context);

        List<WatchContact.Kid> kidsForUI = new ArrayList<>();
        if(!ObjectUtils.isListEmpty(kidsByUserId)){
            for (KidsEntityBean kidsEntityBean :
                    kidsByUserId) {
                WatchContact.Kid kidsForUIRaw = BeanConvertor.getKidsForUI(kidsEntityBean);
                kidsForUIRaw.mBound = true;
                kidsForUIRaw.mLabel = kidsForUIRaw.mName;

                kidsForUI.add(kidsForUIRaw);
            }
        }

        return kidsForUI;

    }

    //因为kids的info会在两张表中保存（DB_Kids，DB_eventKids）,需要都更新
    public static void updateKidsProfile2DB(@NonNull KidsWithParent kidsWithParent){
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);

        DB_Kids dbKids = kidsDataStore.getKidsInfo(kidsWithParent.getId());

        long updateTime = System.currentTimeMillis();

        if(dbKids != null){
            kidsDataStore.update(
                    BeanConvertor.updateDBKids(dbKids, kidsWithParent,updateTime));
        }


        //db_eventKids表更新
        EventKidsStore eventKidsStore = new EventKidsStore(dbUtil);
        List<DB_EventKids> dbEventKids = eventKidsStore.getDBEventKids(kidsWithParent.getId());

        if(!ObjectUtils.isListEmpty(dbEventKids)){
            eventKidsStore.updateList(
                    BeanConvertor.updateEventKidsList(dbEventKids, kidsWithParent,updateTime));
        }

    }

    public static void updateKidsFirmwareVersion(String macId, String fireWareVersion){

        if(TextUtils.isEmpty(macId)
                || TextUtils.isEmpty(fireWareVersion)){
            return;
        }

        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        KidsDataStore kidsDataStore = new KidsDataStore(dbUtil);
        kidsDataStore.updateFirmwareVersion(macId,fireWareVersion);

        //更新eventKids表
        EventKidsStore eventKidsStore = new EventKidsStore(dbUtil);
        eventKidsStore.updateFirmwareVersion(macId,fireWareVersion);
    }

    public static void updateEventKidsFirmwareVersion(String macId, String fireWareVersion){
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());

        EventKidsStore eventKidsStore = new EventKidsStore(dbUtil);
        eventKidsStore.updateFirmwareVersion(macId,fireWareVersion);
    }

    public void uploadFirmwareVersion(final String macId, final String version) {
        FirmwareApi firmwareApi = ApiGen.getInstance(SwingApplication.getAppContext()).
                generateApi(FirmwareApi.class, true);

        CurrentFirmwareVersion currentFirmwareVersion = new CurrentFirmwareVersion();
        currentFirmwareVersion.setFirmwareVersion(version);
        currentFirmwareVersion.setMacId(macId);

        firmwareApi.sendFirmwareVersion(currentFirmwareVersion).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Log.w("uploadFirmwareVersion","sendFirmwareVersion ok");
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.w("uploadFirmwareVersion","sendFirmwareVersion fail");
            }
        });
    }

    public static void clearSubHostRequestsInCache(){
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
        preferencesUtil.setPreferenceStringValue(key_SubHostRequests,"");
    }

    public static void updateSubHostRequestsInCache(SubHostRequests subHostRequests){
        if(subHostRequests != null){
            Gson gson = new Gson();
            String jsonStr = gson.toJson(subHostRequests);

            PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());
            preferencesUtil.setPreferenceStringValue(key_SubHostRequests,jsonStr);
        }

    }

    public static SubHostRequests getSubHostRequestsInCache(){
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(SwingApplication.getAppContext());

        String subHostListStr = preferencesUtil.gPrefStringValue(key_SubHostRequests);
        if(TextUtils.isEmpty(subHostListStr)){
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(subHostListStr, SubHostRequests.class);
    }

    public static RequestAddSubHostEntity getSharedKidsSubHostEntity(SubHostRequests mSubHostRequests, long kidsId){
        if(mSubHostRequests != null){
            List<RequestAddSubHostEntity> requestTo = mSubHostRequests.getRequestTo();

            if(!ObjectUtils.isListEmpty(requestTo)){
                for (RequestAddSubHostEntity requestToEntity : requestTo) {
                    if(requestToEntity.getStatus().equals(WatchContact.User.STATUS_ACCEPTED)
                            && !ObjectUtils.isListEmpty(requestToEntity.getKids())){
                        if(isContainKidsId(requestToEntity.getKids(),kidsId)){
                            return requestToEntity;
                        }
                    }
                }
            }
        }

        return null;
    }

    private static boolean isContainKidsId(List<KidInfo> kidInfoList, long kidsId){
        for (KidInfo kidInfo :
                kidInfoList) {
            if(kidInfo.getId() == kidsId){
                return true;
            }
        }

        return false;
    }

    public  static void sendBroadcastUpdateAvatar() {
        Intent intent = new Intent(MainFrameActivity.UI_Update_Action);
        intent.putExtra(MainFrameActivity.Tag_Key,MainFrameActivity.Tag_Avatar_update);
        SwingApplication.localBroadcastManager.sendBroadcast(intent);
    }
}
