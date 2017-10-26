package com.kidsdynamic.data.repository.disk;

import android.support.annotation.NonNull;

import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.dao.KidsDao;
import com.kidsdynamic.data.dao.UserDao;
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
}
