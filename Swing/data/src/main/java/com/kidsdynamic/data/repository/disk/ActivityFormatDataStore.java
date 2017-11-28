package com.kidsdynamic.data.repository.disk;

import android.util.Log;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_FormatActivity;
import com.kidsdynamic.data.dao.FormatActivityDao;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

/**
 * ActivityFormatDataStore
 */

public class ActivityFormatDataStore {
    public final  static String Activity_type_indoor = "INDOOR";
    public final  static String Activity_type_outdoor = "OUTDOOR";
    private DbUtil dbUtil;

    public ActivityFormatDataStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    public DbUtil getDbUtil() {
        return dbUtil;
    }

    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }


    public void saveAll(List<DB_FormatActivity> db_cloudActivities){
        FormatActivityDao formatActivityDao = dbUtil.getDaoSession().getFormatActivityDao();
        formatActivityDao.insertInTx(db_cloudActivities);
    }

    public void deleteByKidId(long kidsId){
        FormatActivityDao formatActivityDao = dbUtil.getDaoSession().getFormatActivityDao();
        List<DB_FormatActivity> list = formatActivityDao.queryBuilder().
                where(FormatActivityDao.Properties.KidId.eq(kidsId)).
                list();

        Log.w("DB", "del " + list);

        if(!ObjectUtils.isListEmpty(list)){
            formatActivityDao.deleteInTx(list);
        }
    }

    public List<DB_FormatActivity> getByKidId(long kidsId){
        FormatActivityDao formatActivityDao = dbUtil.getDaoSession().getFormatActivityDao();
        return formatActivityDao.queryBuilder().
                where(FormatActivityDao.Properties.KidId.eq(kidsId)).
                list();
    }

}
