package com.kidsdynamic.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 1): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 3;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        UserDao.createTable(db, ifNotExists);
        KidsDao.createTable(db, ifNotExists);
        CloudActivityDao.createTable(db, ifNotExists);
        EventDao.createTable(db, ifNotExists);
        TodoDao.createTable(db, ifNotExists);
        EventKidsDao.createTable(db, ifNotExists);
        RawActivityDao.createTable(db, ifNotExists);
        FormatActivityDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        UserDao.dropTable(db, ifExists);
        KidsDao.dropTable(db, ifExists);
        CloudActivityDao.dropTable(db, ifExists);
        EventDao.dropTable(db, ifExists);
        TodoDao.dropTable(db, ifExists);
        EventKidsDao.dropTable(db, ifExists);
        RawActivityDao.dropTable(db, ifExists);
        FormatActivityDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");

            //modify 2017年12月4日20:35:08 only
            if(oldVersion == 1 || oldVersion == 2){
                //如果老数据库为1，因开发阶段，故采用drop表的方式升级，
                //同时为了和缓存数据的一致性，升级后需要重新登陆
                dropAllTables(db, true);
                onCreate(db);

                //关闭当前界面，显示登陆界面
            }
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UserDao.class);
        registerDaoClass(KidsDao.class);
        registerDaoClass(CloudActivityDao.class);
        registerDaoClass(EventDao.class);
        registerDaoClass(TodoDao.class);
        registerDaoClass(EventKidsDao.class);
        registerDaoClass(RawActivityDao.class);
        registerDaoClass(FormatActivityDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
