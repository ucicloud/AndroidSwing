package com.kidsdynamic.data.persistent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.kidsdynamic.data.dao.DaoMaster;
import com.kidsdynamic.data.dao.DaoSession;


/**
 * <p>Summary:数据库操作工具</p>
 */
public class DbUtil {

    private final String Swing_DBNAME = "swingNew";

    private DaoMaster.DevOpenHelper DBOpenHelper;
    private DaoMaster daoMaster;

    private Context context;
    public void setContext(Context context) {
        this.context = context;
    }

    /*private DbUtil(Context context) {
        this.context = context;
    }*/

    private DbUtil() {}


//    private static DbUtil dbUtil;
    public static final DbUtil getInstance(@NonNull Context context){
        return DBUtilHolder.getInstance(context.getApplicationContext());
    }

    private static class DBUtilHolder{
        private static final DbUtil dbUtils = new DbUtil();
        private static final DbUtil getInstance(Context context){
            if(dbUtils.context == null){
                dbUtils.setContext(context);
            }

            return dbUtils;
        }
    }

    private DaoMaster.DevOpenHelper getDBOpenHelper() {
        if (DBOpenHelper == null) {
            DBOpenHelper = new DaoMaster.DevOpenHelper(context, Swing_DBNAME, null);
        }
        return DBOpenHelper;
    }

    public DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            SQLiteDatabase db = getDBOpenHelper().getWritableDatabase();
            daoMaster = new DaoMaster(db);
        }
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        return getDaoMaster().newSession();
    }
}
