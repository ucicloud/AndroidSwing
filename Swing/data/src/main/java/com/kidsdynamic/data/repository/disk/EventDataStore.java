package com.kidsdynamic.data.repository.disk;

import com.kidsdynamic.data.dao.DB_Event;
import com.kidsdynamic.data.dao.EventDao;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

import de.greenrobot.dao.DbUtils;

/**
 * <br>author: wzg@xdja.com <br/>
 * date:   2017/10/26 17:02 <br/>
 */

public class EventDataStore {
    private DbUtil dbUtil;

    public EventDataStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    public DbUtil getDbUtil() {
        return dbUtil;
    }

    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void dealAll(){
        EventDao eventDao = dbUtil.getDaoSession().getEventDao();
        eventDao.deleteAll();
    }

    public boolean save(DB_Event db_event){
        EventDao eventDao = dbUtil.getDaoSession().getEventDao();
        long index = eventDao.insert(db_event);

        return (index != -1);
    }
    public void saveAll(List<DB_Event> db_eventList){
        EventDao eventDao = dbUtil.getDaoSession().getEventDao();
        eventDao.insertInTx(db_eventList);
    }
}
