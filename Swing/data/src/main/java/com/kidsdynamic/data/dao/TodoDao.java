package com.kidsdynamic.data.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.kidsdynamic.data.dao.DB_Todo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "t_todo".
*/
public class TodoDao extends AbstractDao<DB_Todo, Long> {

    public static final String TABLENAME = "t_todo";

    /**
     * Properties of entity DB_Todo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property TodoId = new Property(0, long.class, "todoId", true, "todo_id");
        public final static Property Text = new Property(1, String.class, "text", false, "text");
        public final static Property Status = new Property(2, String.class, "status", false, "status");
        public final static Property DateCreated = new Property(3, String.class, "dateCreated", false, "date_created");
        public final static Property LastUpdated = new Property(4, String.class, "lastUpdated", false, "last_updated");
        public final static Property EventId = new Property(5, Long.class, "eventId", false, "event_id");
    };

    private DaoSession daoSession;

    private Query<DB_Todo> dB_Event_TodoListQuery;

    public TodoDao(DaoConfig config) {
        super(config);
    }
    
    public TodoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"t_todo\" (" + //
                "\"todo_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: todoId
                "\"text\" TEXT," + // 1: text
                "\"status\" TEXT," + // 2: status
                "\"date_created\" TEXT," + // 3: dateCreated
                "\"last_updated\" TEXT," + // 4: lastUpdated
                "\"event_id\" INTEGER);"); // 5: eventId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"t_todo\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DB_Todo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getTodoId());
 
        String text = entity.getText();
        if (text != null) {
            stmt.bindString(2, text);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(3, status);
        }
 
        String dateCreated = entity.getDateCreated();
        if (dateCreated != null) {
            stmt.bindString(4, dateCreated);
        }
 
        String lastUpdated = entity.getLastUpdated();
        if (lastUpdated != null) {
            stmt.bindString(5, lastUpdated);
        }
 
        Long eventId = entity.getEventId();
        if (eventId != null) {
            stmt.bindLong(6, eventId);
        }
    }

    @Override
    protected void attachEntity(DB_Todo entity) {
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
    public DB_Todo readEntity(Cursor cursor, int offset) {
        DB_Todo entity = new DB_Todo( //
            cursor.getLong(offset + 0), // todoId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // text
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // status
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // dateCreated
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // lastUpdated
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5) // eventId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DB_Todo entity, int offset) {
        entity.setTodoId(cursor.getLong(offset + 0));
        entity.setText(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setStatus(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDateCreated(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLastUpdated(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setEventId(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DB_Todo entity, long rowId) {
        entity.setTodoId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DB_Todo entity) {
        if(entity != null) {
            return entity.getTodoId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "todoList" to-many relationship of DB_Event. */
    public List<DB_Todo> _queryDB_Event_TodoList(Long eventId) {
        synchronized (this) {
            if (dB_Event_TodoListQuery == null) {
                QueryBuilder<DB_Todo> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.EventId.eq(null));
                dB_Event_TodoListQuery = queryBuilder.build();
            }
        }
        Query<DB_Todo> query = dB_Event_TodoListQuery.forCurrentThread();
        query.setParameter(0, eventId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getEventDao().getAllColumns());
            builder.append(" FROM t_todo T");
            builder.append(" LEFT JOIN t_event T0 ON T.\"event_id\"=T0.\"event_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected DB_Todo loadCurrentDeep(Cursor cursor, boolean lock) {
        DB_Todo entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        DB_Event dB_Event = loadCurrentOther(daoSession.getEventDao(), cursor, offset);
        entity.setDB_Event(dB_Event);

        return entity;    
    }

    public DB_Todo loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<DB_Todo> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<DB_Todo> list = new ArrayList<DB_Todo>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<DB_Todo> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<DB_Todo> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
