package com.kidsdynamic.data.repository.disk;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.dao.KidsDao;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

/**
 * date:   2017/10/26 15:24 <br/>
 */

public class KidsDataStore {
    private DbUtil dbUtil;

    public KidsDataStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    public void clearAllData(){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        kidsDao.deleteAll();
    }

    public void save(@NonNull List<DB_Kids> kidsList){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
//        kidsDao.insertOrReplaceInTx();
        kidsDao.insertOrReplaceInTx(kidsList);
    }

    public DB_Kids getKidsInfo(long kidsId){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> kidsList = kidsDao.queryBuilder().where(KidsDao.Properties.KidsId.eq(kidsId)).list();
        if(!ObjectUtils.isListEmpty(kidsList)){
            return kidsList.get(0);
        }

        return null;
    }

    public void delKidsInfo(long kidsId){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> kidsList = kidsDao.queryBuilder().where(KidsDao.Properties.KidsId.eq(kidsId)).list();
        if(!ObjectUtils.isListEmpty(kidsList)){
            kidsDao.deleteInTx(kidsList);
        }
    }

    public List<DB_Kids> getKidsInfoByParentId(long parentId){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> kidsList = kidsDao.queryBuilder().where(KidsDao.Properties.ParentId.eq(parentId)).list();
        if(!ObjectUtils.isListEmpty(kidsList)){
            return kidsList;
        }

        return null;
    }

    public List<DB_Kids> getKidsInfoByShared(int shareType){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> kidsList = kidsDao.queryBuilder().
                where(KidsDao.Properties.ShareType.eq(shareType)).list();
        if(!ObjectUtils.isListEmpty(kidsList)){
            return kidsList;
        }

        return null;
    }

    public void clearKidsInfoForShareType(int ShareType){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> db_kids = kidsDao.queryBuilder().
                where(KidsDao.Properties.ShareType.eq(ShareType)).list();

        if(!ObjectUtils.isListEmpty(db_kids)){
            kidsDao.deleteInTx(db_kids);
        }
    }


    public void update(@NonNull DB_Kids db_kids){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();

        kidsDao.update(db_kids);
    }

    public void updateFirmwareVersion(String macId, String fireWareVersion){
        if(TextUtils.isEmpty(macId)
                || TextUtils.isEmpty(fireWareVersion)){
            return;
        }

        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> list = kidsDao.queryBuilder().where(KidsDao.Properties.MacId.eq(macId)).list();

        if(!ObjectUtils.isListEmpty(list)){
            for (DB_Kids dbKids : list) {
                dbKids.setFirmwareVersion(fireWareVersion);
            }

            kidsDao.updateInTx(list);
        }
    }
}
