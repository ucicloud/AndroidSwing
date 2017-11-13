package com.kidsdynamic.data.repository.disk;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_RawActivity;
import com.kidsdynamic.data.dao.RawActivityDao;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

/**
 * <br>RawActivityDataStore <br/>
 * date:   only_app<br/>
 */

public class RawActivityDataStore {
    public final static String status_pending = "PENDING";
    public final static String status_done = "DONE";

    private DbUtil dbUtil;

    public RawActivityDataStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    public DbUtil getDbUtil() {
        return dbUtil;
    }

    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }


    public void deleteByStatus(String status){
        RawActivityDao rawActivityDao = dbUtil.getDaoSession().getRawActivityDao();
        List<DB_RawActivity> list = rawActivityDao.queryBuilder().
                where(RawActivityDao.Properties.Status.eq(status)).
                list();

        if(!ObjectUtils.isListEmpty(list)){
            rawActivityDao.deleteInTx(list);
        }
    }

}
