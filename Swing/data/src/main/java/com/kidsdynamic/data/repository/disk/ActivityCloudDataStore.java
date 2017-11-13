package com.kidsdynamic.data.repository.disk;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.CloudActivityDao;
import com.kidsdynamic.data.dao.DB_CloudActivity;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

/**
 */

public class ActivityCloudDataStore {
    public final  static String Activity_type_indoor = "INDOOR";
    public final  static String Activity_type_outdoor = "OUTDOOR";
    private DbUtil dbUtil;

    public ActivityCloudDataStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    public DbUtil getDbUtil() {
        return dbUtil;
    }

    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }


    public void saveAll(List<DB_CloudActivity> db_cloudActivities){
        CloudActivityDao cloudActivityDao = dbUtil.getDaoSession().getCloudActivityDao();
        cloudActivityDao.insertInTx(db_cloudActivities);
    }

    public void deleteByKidId(long kidsId){
        CloudActivityDao cloudActivityDao = dbUtil.getDaoSession().getCloudActivityDao();
        List<DB_CloudActivity> list = cloudActivityDao.queryBuilder().
                where(CloudActivityDao.Properties.KidId.eq(kidsId)).
                list();

        if(!ObjectUtils.isListEmpty(list)){
            cloudActivityDao.deleteInTx(list);
        }
    }

}
