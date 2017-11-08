package com.kidsdynamic.data.dao;

import java.util.List;
import com.kidsdynamic.data.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "t_user".
 */
public class DB_User {

    private long userId;
    /** Not-null value. */
    private String email;
    private String firstName;
    private String lastName;
    private Long lastUpdate;
    private Long dataCreate;
    private String zipCode;
    private String phoneNum;
    private String profile;
    private Integer focusID;
    private Integer focusPID;
    private String registrationId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient UserDao myDao;

    private List<DB_Kids> kidsList;

    public DB_User() {
    }

    public DB_User(long userId) {
        this.userId = userId;
    }

    public DB_User(long userId, String email, String firstName, String lastName, Long lastUpdate, Long dataCreate, String zipCode, String phoneNum, String profile, Integer focusID, Integer focusPID, String registrationId) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastUpdate = lastUpdate;
        this.dataCreate = dataCreate;
        this.zipCode = zipCode;
        this.phoneNum = phoneNum;
        this.profile = profile;
        this.focusID = focusID;
        this.focusPID = focusPID;
        this.registrationId = registrationId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    /** Not-null value. */
    public String getEmail() {
        return email;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getDataCreate() {
        return dataCreate;
    }

    public void setDataCreate(Long dataCreate) {
        this.dataCreate = dataCreate;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Integer getFocusID() {
        return focusID;
    }

    public void setFocusID(Integer focusID) {
        this.focusID = focusID;
    }

    public Integer getFocusPID() {
        return focusPID;
    }

    public void setFocusPID(Integer focusPID) {
        this.focusPID = focusPID;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<DB_Kids> getKidsList() {
        if (kidsList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KidsDao targetDao = daoSession.getKidsDao();
            List<DB_Kids> kidsListNew = targetDao._queryDB_User_KidsList(userId);
            synchronized (this) {
                if(kidsList == null) {
                    kidsList = kidsListNew;
                }
            }
        }
        return kidsList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetKidsList() {
        kidsList = null;
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
