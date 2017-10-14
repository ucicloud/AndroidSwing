package com.kidsdynamic.data.repository.disk;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.dao.DaoSession;
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
    public String getUserName(long id){
        DaoSession daoSession = dbUtil.getDaoMaster().newSession();

        QueryBuilder<DB_User> queryBuilder = QueryBuilder.internalCreate(daoSession.getUserDao());
        Query<DB_User> query = queryBuilder.where(UserDao.Properties.Id.eq(id)).build();

        List<DB_User> list = query.list();
        if(ObjectUtils.isListEmpty(list)){
            return "";
        }else {
            return list.get(0).getFirstName();
        }
    }
}
