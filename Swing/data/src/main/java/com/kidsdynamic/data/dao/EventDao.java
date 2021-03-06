package com.kidsdynamic.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.kidsdynamic.data.dao.DB_Event;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "t_event".
*/
public class EventDao extends AbstractDao<DB_Event, Long> {

    public static final String TABLENAME = "t_event";

    /**
     * Properties of entity DB_Event.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property EventId = new Property(0, long.class, "eventId", true, "event_id");
        public final static Property UserId = new Property(1, Long.class, "userId", false, "user_id");
        public final static Property KidIds = new Property(2, String.class, "kidIds", false, "kid_ids");
        public final static Property Name = new Property(3, String.class, "name", false, "event_name");
        public final static Property StartDate = new Property(4, Long.class, "startDate", false, "start_date");
        public final static Property EndDate = new Property(5, Long.class, "endDate", false, "end_date");
        public final static Property Color = new Property(6, String.class, "color", false, "color");
        public final static Property Description = new Property(7, String.class, "description", false, "description");
        public final static Property Alert = new Property(8, Integer.class, "alert", false, "alert");
        public final static Property Repeat = new Property(9, String.class, "repeat", false, "repeat");
        public final static Property TimezoneOffset = new Property(10, Integer.class, "timezoneOffset", false, "timezoneOffset");
        public final static Property DateCreated = new Property(11, Long.class, "dateCreated", false, "date_created");
        public final static Property LastUpdate = new Property(12, Long.class, "lastUpdate", false, "last_update");
        public final static Property Status = new Property(13, String.class, "status", false, "status");
    };

    private DaoSession daoSession;


    public EventDao(DaoConfig config) {
        super(config);
    }
    
    public EventDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_event\" (" + //
                "\"event_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: eventId
                "\"user_id\" INTEGER," + // 1: userId
                "\"kid_ids\" TEXT," + // 2: kidIds
                "\"event_name\" TEXT," + // 3: name
                "\"start_date\" INTEGER," + // 4: startDate
                "\"end_date\" INTEGER," + // 5: endDate
                "\"color\" TEXT," + // 6: color
                "\"description\" TEXT," + // 7: description
                "\"alert\" INTEGER," + // 8: alert
                "\"repeat\" TEXT," + // 9: repeat
                "\"timezoneOffset\" INTEGER," + // 10: timezoneOffset
                "\"date_created\" INTEGER," + // 11: dateCreated
                "\"last_update\" INTEGER," + // 12: lastUpdate
                "\"status\" TEXT);"); // 13: status
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_event\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DB_Event entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getEventId());
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(2, userId);
        }
 
        String kidIds = entity.getKidIds();
        if (kidIds != null) {
            stmt.bindString(3, kidIds);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        Long startDate = entity.getStartDate();
        if (startDate != null) {
            stmt.bindLong(5, startDate);
        }
 
        Long endDate = entity.getEndDate();
        if (endDate != null) {
            stmt.bindLong(6, endDate);
        }
 
        String color = entity.getColor();
        if (color != null) {
            stmt.bindString(7, color);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(8, description);
        }
 
        Integer alert = entity.getAlert();
        if (alert != null) {
            stmt.bindLong(9, alert);
        }
 
        String repeat = entity.getRepeat();
        if (repeat != null) {
            stmt.bindString(10, repeat);
        }
 
        Integer timezoneOffset = entity.getTimezoneOffset();
        if (timezoneOffset != null) {
            stmt.bindLong(11, timezoneOffset);
        }
 
        Long dateCreated = entity.getDateCreated();
        if (dateCreated != null) {
            stmt.bindLong(12, dateCreated);
        }
 
        Long lastUpdate = entity.getLastUpdate();
        if (lastUpdate != null) {
            stmt.bindLong(13, lastUpdate);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(14, status);
        }
    }

    @Override
    protected void attachEntity(DB_Event entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DB_Event readEntity(Cursor cursor, int offset) {
        DB_Event entity = new DB_Event( //
            cursor.getLong(offset + 0), // eventId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // kidIds
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // startDate
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // endDate
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // color
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // description
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // alert
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // repeat
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // timezoneOffset
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11), // dateCreated
            cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12), // lastUpdate
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // status
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DB_Event entity, int offset) {
        entity.setEventId(cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setKidIds(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStartDate(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setEndDate(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setColor(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDescription(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAlert(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setRepeat(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setTimezoneOffset(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setDateCreated(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
        entity.setLastUpdate(cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12));
        entity.setStatus(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DB_Event entity, long rowId) {
        entity.setEventId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DB_Event entity) {
        if(entity != null) {
            return entity.getEventId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
