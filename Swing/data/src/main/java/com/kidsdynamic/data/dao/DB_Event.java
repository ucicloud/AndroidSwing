package com.kidsdynamic.data.dao;

import java.util.List;
import com.kidsdynamic.data.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "t_event".
 */
public class DB_Event {

    private long eventId;
    private Long userId;
    private String kidIds;
    private String name;
    private Long startDate;
    private Long endDate;
    private String color;
    private String description;
    private Integer alert;
    private String repeat;
    private Integer timezoneOffset;
    private Long dateCreated;
    private Long lastUpdate;
    private String status;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient EventDao myDao;

    private List<DB_Todo> todoList;
    private List<DB_EventKids> eventKids;

    public DB_Event() {
    }

    public DB_Event(long eventId) {
        this.eventId = eventId;
    }

    public DB_Event(long eventId, Long userId, String kidIds, String name, Long startDate, Long endDate, String color, String description, Integer alert, String repeat, Integer timezoneOffset, Long dateCreated, Long lastUpdate, String status) {
        this.eventId = eventId;
        this.userId = userId;
        this.kidIds = kidIds;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.description = description;
        this.alert = alert;
        this.repeat = repeat;
        this.timezoneOffset = timezoneOffset;
        this.dateCreated = dateCreated;
        this.lastUpdate = lastUpdate;
        this.status = status;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEventDao() : null;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKidIds() {
        return kidIds;
    }

    public void setKidIds(String kidIds) {
        this.kidIds = kidIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAlert() {
        return alert;
    }

    public void setAlert(Integer alert) {
        this.alert = alert;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<DB_Todo> getTodoList() {
        if (todoList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TodoDao targetDao = daoSession.getTodoDao();
            List<DB_Todo> todoListNew = targetDao._queryDB_Event_TodoList(eventId);
            synchronized (this) {
                if(todoList == null) {
                    todoList = todoListNew;
                }
            }
        }
        return todoList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTodoList() {
        todoList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<DB_EventKids> getEventKids() {
        if (eventKids == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EventKidsDao targetDao = daoSession.getEventKidsDao();
            List<DB_EventKids> eventKidsNew = targetDao._queryDB_Event_EventKids(eventId);
            synchronized (this) {
                if(eventKids == null) {
                    eventKids = eventKidsNew;
                }
            }
        }
        return eventKids;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetEventKids() {
        eventKids = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
