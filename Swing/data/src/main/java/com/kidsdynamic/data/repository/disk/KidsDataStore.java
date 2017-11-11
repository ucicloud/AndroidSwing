package com.kidsdynamic.data.repository.disk;

import android.support.annotation.NonNull;

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
        kidsDao.insertInTx(kidsList);
    }

    public DB_Kids getKidsInfo(long kidsId){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> kidsList = kidsDao.queryBuilder().where(KidsDao.Properties.KidsId.eq(kidsId)).list();
        if(!ObjectUtils.isListEmpty(kidsList)){
            return kidsList.get(0);
        }

        return null;
    }

    public List<DB_Kids> getKidsInfoByParentId(long parentId){
        KidsDao kidsDao = dbUtil.getDaoSession().getKidsDao();
        List<DB_Kids> kidsList = kidsDao.queryBuilder().where(KidsDao.Properties.ParentId.eq(parentId)).list();
        if(!ObjectUtils.isListEmpty(kidsList)){
            return kidsList;
        }

        return null;
    }


}
