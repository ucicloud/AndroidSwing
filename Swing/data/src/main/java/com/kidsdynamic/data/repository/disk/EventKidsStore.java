package com.kidsdynamic.data.repository.disk;

import android.support.annotation.NonNull;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_EventKids;
import com.kidsdynamic.data.dao.EventKidsDao;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

/**
 * date:   2017年12月4日21:59:27 <br/>
 */

public class EventKidsStore {
    private DbUtil dbUtil;

    public EventKidsStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    public void clearAllData(){
        EventKidsDao eventKidsDao = dbUtil.getDaoSession().getEventKidsDao();
        eventKidsDao.deleteAll();
    }

    public void save(@NonNull List<DB_EventKids> eventKidsList){
        EventKidsDao eventKidsDao = dbUtil.getDaoSession().getEventKidsDao();
        eventKidsDao.insertInTx(eventKidsList);
    }

    public List<DB_EventKids> getDBEventKids(long kidsId){
        EventKidsDao eventKidsDao = dbUtil.getDaoSession().getEventKidsDao();

        List<DB_EventKids> dbEventKids = eventKidsDao.queryBuilder().
                where(EventKidsDao.Properties.KidsId.eq(kidsId)).list();

        if(!ObjectUtils.isListEmpty(dbEventKids)){
            return dbEventKids;
        }

        return null;
    }


    public void update(@NonNull DB_EventKids eventKids){
        EventKidsDao eventKidsDao = dbUtil.getDaoSession().getEventKidsDao();

        eventKidsDao.update(eventKids);
    }

    public void updateList(@NonNull List<DB_EventKids> eventKids){
        if(ObjectUtils.isListEmpty(eventKids)){
            return;
        }

        EventKidsDao eventKidsDao = dbUtil.getDaoSession().getEventKidsDao();

        eventKidsDao.updateInTx(eventKids);
    }
}
