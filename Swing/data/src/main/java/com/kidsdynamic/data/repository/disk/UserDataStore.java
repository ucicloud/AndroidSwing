package com.kidsdynamic.data.repository.disk;

import android.support.annotation.NonNull;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.dao.UserDao;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * date:   2017/10/14 14:21 <br/>
 */

public class UserDataStore {
    private DbUtil dbUtil;

    public UserDataStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    //方法测试
    public String getUserName(int userId){

        /*DaoSession daoSession = dbUtil.getDaoMaster().newSession();

        QueryBuilder<DB_User> queryBuilder = QueryBuilder.internalCreate(daoSession.getUserDao());
        Query<DB_User> query = queryBuilder.where(UserDao.Properties.UserId.eq(userId)).build();

        List<DB_User> list = query.list();
        if(ObjectUtils.isListEmpty(list)){
            return "";
        }else {
            return list.get(0).getFirstName();
        }*/

        UserDao userDao = dbUtil.getDaoSession().getUserDao();
        QueryBuilder<DB_User> builder = userDao.queryBuilder();
        Query<DB_User> build = builder.where(UserDao.Properties.UserId.eq(userId)).build();

        List<DB_User> list = build.list();
        if(ObjectUtils.isListEmpty(list)){
            return "";
        }else {
            return list.get(0).getFirstName();
        }
    }

    public void clearAllData(){
        UserDao userDao = dbUtil.getDaoSession().getUserDao();
        userDao.deleteAll();
    }

    public void save(@NonNull DB_User db_user){
        UserDao userDao = dbUtil.getDaoSession().getUserDao();
        userDao.insert(db_user);
    }

    public DB_User getById(long userId){
        UserDao userDao = dbUtil.getDaoSession().getUserDao();
        List<DB_User> list = userDao.queryBuilder().
                where(UserDao.Properties.UserId.eq(userId)).
                build().list();

        if(!ObjectUtils.isListEmpty(list)){
            return list.get(0);
        }

        return null;
    }
}
