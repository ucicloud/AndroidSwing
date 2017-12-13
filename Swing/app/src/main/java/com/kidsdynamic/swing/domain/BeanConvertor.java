package com.kidsdynamic.swing.domain;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_CloudActivity;
import com.kidsdynamic.data.dao.DB_Event;
import com.kidsdynamic.data.dao.DB_EventKids;
import com.kidsdynamic.data.dao.DB_FormatActivity;
import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.dao.DB_RawActivity;
import com.kidsdynamic.data.dao.DB_Todo;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.activity.model.RawActivityDataEntity;
import com.kidsdynamic.data.net.event.model.EventAddEntity;
import com.kidsdynamic.data.net.event.model.EventInfo;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.net.event.model.TodoEntity;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.user.model.KidInfo;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.repository.disk.ActivityCloudDataStore;
import com.kidsdynamic.data.repository.disk.RawActivityDataStore;
import com.kidsdynamic.swing.ble.ActivityModel;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchActivity;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * date:   2017/10/26 15:33 <br/>
 */

public class BeanConvertor {
    public static final String db_kids_split = "#";

    public static DB_User getDBUser(@NonNull UserProfileRep userProfileRep) {
        DB_User db_user = new DB_User();
        UserProfileRep.UserEntity userEntity = userProfileRep.getUser();

        db_user.setUserId(userEntity.getId());
        db_user.setEmail(userEntity.getEmail());
        db_user.setFirstName(userEntity.getFirstName());
        db_user.setLastName(userEntity.getLastName());

        //dataCreate, lastUpdate 使用utc时间
        db_user.setDataCreate(getUTCTimeStamp(userEntity.getDateCreated()));
        db_user.setLastUpdate(getUTCTimeStamp(userEntity.getLastUpdate()));

        db_user.setPhoneNum(userEntity.getPhoneNumber());
        db_user.setProfile(userEntity.getProfile());
        db_user.setZipCode(userEntity.getZipCode());

        return db_user;
    }

    public static DB_User getDBUser(@NonNull UserInfo userEntity) {
        DB_User db_user = new DB_User();

        db_user.setUserId(userEntity.getId());
        db_user.setEmail(userEntity.getEmail());
        db_user.setFirstName(userEntity.getFirstName());
        db_user.setLastName(userEntity.getLastName());

        //dataCreate, lastUpdate 使用utc时间
        db_user.setDataCreate(getUTCTimeStamp(userEntity.getDateCreated()));
        db_user.setLastUpdate(getUTCTimeStamp(userEntity.getLastUpdate()));

        db_user.setPhoneNum(userEntity.getPhoneNumber());
        db_user.setProfile(userEntity.getProfile());
        db_user.setZipCode(userEntity.getZipCode());

        return db_user;
    }

    public static DB_User updateDBUser(@NonNull DB_User db_user, @NonNull UserInfo userEntity) {

        db_user.setUserId(userEntity.getId());
        db_user.setEmail(userEntity.getEmail());
        db_user.setFirstName(userEntity.getFirstName());
        db_user.setLastName(userEntity.getLastName());

        //dataCreate, lastUpdate 使用utc时间
        db_user.setDataCreate(getUTCTimeStamp(userEntity.getDateCreated()));

        //因为目前服务端不更新lastupdate，客户端本地更新
//        db_user.setLastUpdate(getUTCTimeStamp(userEntity.getLastUpdate()));
        db_user.setLastUpdate(System.currentTimeMillis());

        db_user.setPhoneNum(userEntity.getPhoneNumber());
        db_user.setProfile(userEntity.getProfile());
        db_user.setZipCode(userEntity.getZipCode());

        return db_user;
    }


    public static List<DB_Kids> getDBKidsList(@NonNull UserProfileRep userProfileRep) {
        List<DB_Kids> db_kidsList = new ArrayList<>(3);
        List<UserProfileRep.KidsEntity> kidsList = userProfileRep.getKids();

        for (int i = 0; i < kidsList.size(); i++) {
            UserProfileRep.KidsEntity kidsEntity = kidsList.get(i);

            DB_Kids db_kids = new DB_Kids();

            db_kids.setKidsId(kidsEntity.getId());
            db_kids.setName(kidsEntity.getName());
            // TODO: 2017/11/28 数据库修改
            db_kids.setLastUpdate(System.currentTimeMillis());
            db_kids.setDateCreated(getUTCTimeStamp(kidsEntity.getDateCreated()));
            db_kids.setMacId(kidsEntity.getMacId());
            db_kids.setProfile(kidsEntity.getProfile());
            db_kids.setParentId(userProfileRep.getUser().getId());
            db_kids.setFirmwareVersion(kidsEntity.getFirmwareVersion());

            db_kids.setBattery(-1);
            db_kids.setSubHostId(Long.valueOf(-1));
            db_kids.setShareType(-1);

            db_kidsList.add(db_kids);
        }

        return db_kidsList;
    }


    public static List<DB_Event> getDBEventList(@NonNull List<EventWithTodo> eventWithTodoList) {
        List<DB_Event> db_eventList = new ArrayList<>();
        for (EventWithTodo eventWithTodo : eventWithTodoList) {
            db_eventList.add(getDBEvent(eventWithTodo));
        }

        return db_eventList;
    }

    public static DB_Event getDBEvent(@NonNull EventWithTodo eventWithTodo) {
        DB_Event db_event = new DB_Event();

        db_event.setEventId(eventWithTodo.getId());
        db_event.setUserId(eventWithTodo.getUser().getId());
        db_event.setKidIds(getAllKidsIdFromList(eventWithTodo.getKid()));
        db_event.setName(eventWithTodo.getName());

        //event的开始，结束时间使用local时间
        db_event.setStartDate(getLocalTimeStamp(eventWithTodo.getStartDate()));
        db_event.setEndDate(getLocalTimeStamp(eventWithTodo.getEndDate()));

        db_event.setColor(eventWithTodo.getColor());
        db_event.setDescription(eventWithTodo.getDescription());
        db_event.setAlert(eventWithTodo.getAlert());
        db_event.setRepeat(eventWithTodo.getRepeat());
        db_event.setTimezoneOffset(eventWithTodo.getTimezoneOffset());
        db_event.setStatus(eventWithTodo.getStatus());

        //create data, update data use utc time
        db_event.setDateCreated(getUTCTimeStamp(eventWithTodo.getDateCreated()));
        db_event.setLastUpdate(getUTCTimeStamp(eventWithTodo.getLastUpdated()));

        return db_event;
    }

    private static String getAllKidsIdFromList(@NonNull List<KidInfo> kidInfoList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < kidInfoList.size(); i++) {
            stringBuilder.append(kidInfoList.get(i).getId());
            if (i != (kidInfoList.size() - 1)) {
                stringBuilder.append(db_kids_split);
            }
        }

        return stringBuilder.toString();
    }


    static List<DB_Todo> getDBTodo(@NonNull List<EventWithTodo> eventWithTodoList) {
        List<DB_Todo> db_todoList = new ArrayList<>();

        for (EventWithTodo eventWithTodo : eventWithTodoList) {

            List<TodoEntity> todoEntityList = eventWithTodo.getTodo();

            if (!ObjectUtils.isListEmpty(todoEntityList)) {
                for (TodoEntity todoEntity : todoEntityList) {
                    DB_Todo db_todo = new DB_Todo();

                    db_todo.setTodoId(todoEntity.getId());
                    db_todo.setText(todoEntity.getText());
                    db_todo.setStatus(todoEntity.getStatus());
                    db_todo.setDateCreated(getUTCTimeStamp(todoEntity.getDateCreated()));
                    db_todo.setLastUpdated(getUTCTimeStamp(todoEntity.getLastUpdated()));
                    db_todo.setEventId((long) eventWithTodo.getId());

                    db_todoList.add(db_todo);
                }
            }
        }

        return db_todoList;
    }

    static List<DB_EventKids> getDBEventKids(@NonNull List<EventWithTodo> eventWithTodoList){
        List<DB_EventKids> db_eventKidsList = new ArrayList<>(5);

        for (EventWithTodo eventWithTodo : eventWithTodoList) {
            List<KidInfo> kidInfos = eventWithTodo.getKid();
            if(!ObjectUtils.isListEmpty(kidInfos)){

                for (KidInfo kidInfo : kidInfos) {
                    DB_EventKids db_eventKids = new DB_EventKids();

                    db_eventKids.setKidsId(kidInfo.getId());
                    db_eventKids.setName(kidInfo.getName());
                    db_eventKids.setMacId(kidInfo.getMacId());

                    db_eventKids.setProfile(kidInfo.getProfile());

                    db_eventKids.setDateCreated(getUTCTimeStamp(kidInfo.getDateCreated()));
                    db_eventKids.setLastUpdate(System.currentTimeMillis());
                    db_eventKids.setEventId((long)eventWithTodo.getId());

                    db_eventKidsList.add(db_eventKids);
                }
            }
        }

        return db_eventKidsList;
    }

    static DB_Kids getDBKidsInfo(@NonNull KidsWithParent kidsWithParent) {

        DB_Kids db_kids = new DB_Kids();

        db_kids.setKidsId(kidsWithParent.getId());
        db_kids.setName(kidsWithParent.getName());
        db_kids.setLastUpdate(System.currentTimeMillis());
        db_kids.setDateCreated(getUTCTimeStamp(kidsWithParent.getDateCreated()));
        db_kids.setMacId(kidsWithParent.getMacId());
        db_kids.setProfile(kidsWithParent.getProfile());
        db_kids.setParentId(kidsWithParent.getParent().getId());
        db_kids.setFirmwareVersion(kidsWithParent.getFirmwareVersion());

        db_kids.setBattery(-1);
        db_kids.setSubHostId(Long.valueOf(-1));
        db_kids.setShareType(-1);

        return db_kids;
    }

    public static long getUTCTimeStamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = format.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            date = null;
        }
        if (date == null)
            return 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.getTimeInMillis();
    }

    public static long getLocalTimeStamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date;
        try {
            date = format.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            date = null;
        }
        if (date == null)
            return 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.getTimeInMillis();
    }

    public static String getUtcTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }

    private static String getLocalTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }

    public static String getLocalTimeString(long timestamp, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static List<WatchEvent> getWatchEvent(List<DB_Event> dbEvents) {
        List<WatchEvent> watchEvents = new ArrayList<>(dbEvents.size());
        for (DB_Event db_event : dbEvents) {
            watchEvents.add(getWatchEvent(db_event));
        }

        return watchEvents;
    }

    static WatchEvent getWatchEvent(DB_Event db_event) {

        WatchEvent watchEvent = new WatchEvent();
        watchEvent.mId = db_event.getEventId();
        watchEvent.mUserId = db_event.getUserId();
        watchEvent.mName = db_event.getName();
        watchEvent.mStartDate = db_event.getStartDate();
        watchEvent.mEndDate = db_event.getEndDate();
        watchEvent.mKids = getEventKidsList(db_event);
        watchEvent.mColor = db_event.getColor();
        watchEvent.mStatus = db_event.getStatus();
        watchEvent.mDescription = db_event.getDescription();
        watchEvent.mAlert = db_event.getAlert();
        watchEvent.mRepeat = db_event.getRepeat();
        watchEvent.mTimezoneOffset = db_event.getTimezoneOffset();
        watchEvent.mAlertTimeStamp = watchEvent.mStartDate;

        if (db_event.getDateCreated() != null) {
            watchEvent.mDateCreated = db_event.getDateCreated();
        }

        if (db_event.getLastUpdate() != null) {
            watchEvent.mLastUpdated = db_event.getLastUpdate();
        }

        watchEvent.mTodoList = getWatchTodos(db_event.getUserId(), db_event.getTodoList());

        return watchEvent;

    }

    private static List<Long> getEventKidsList(DB_Event db_event) {
        List<Long> kidsList = new ArrayList<>();
        String kidIds = db_event.getKidIds();
        String[] kidsArray = kidIds.split(db_kids_split);
        for (String kids :
                kidsArray) {
            kidsList.add(Long.valueOf(kids));
        }

        return kidsList;
    }

    private static List<WatchTodo> getWatchTodos(long userId, List<DB_Todo> dbTodos) {
        List<WatchTodo> watchTodos = new ArrayList<>(dbTodos.size());
        for (DB_Todo db_todo : dbTodos) {
            watchTodos.add(getWatchTodo(userId, db_todo));
        }

        return watchTodos;
    }

    private static WatchTodo getWatchTodo(long userId, DB_Todo db_todo) {
//        new WatchTodo(1, 452, 0, "1 ", WatchTodo.STATUS_DONE);
        WatchTodo watchTodo = new WatchTodo();
        watchTodo.mId = db_todo.getTodoId();
        watchTodo.mUserId = userId;
        watchTodo.mEventId = db_todo.getEventId();
        watchTodo.mText = db_todo.getText();

        if (!TextUtils.isEmpty(db_todo.getStatus())) {
            watchTodo.mStatus = db_todo.getStatus();
        } else {
            watchTodo.mStatus = WatchTodo.STATUS_PENDING;
        }

        return watchTodo;
    }

    public static KidsEntityBean convert(@NonNull DB_Kids db_kids) {

        KidsEntityBean kidsEntityBean = new KidsEntityBean();
        kidsEntityBean.setKidsId(db_kids.getKidsId());
        kidsEntityBean.setName(db_kids.getName());
        kidsEntityBean.setLastUpdate(db_kids.getLastUpdate());
        kidsEntityBean.setDateCreated(db_kids.getDateCreated());
        kidsEntityBean.setMacId(db_kids.getMacId());
        kidsEntityBean.setFirmwareVersion(db_kids.getFirmwareVersion());
        kidsEntityBean.setProfile(db_kids.getProfile());
        kidsEntityBean.setState(db_kids.getState());
        kidsEntityBean.setParentId(db_kids.getParentId());

        kidsEntityBean.setBattery(db_kids.getBattery());
        if(db_kids.getSubHostId() == null){
            kidsEntityBean.setSubHostId(-1);
        }
        kidsEntityBean.setShareType(db_kids.getShareType());

        return kidsEntityBean;
    }

    public static EventAddEntity getEventAddBean(@NonNull WatchEvent event) {

        EventAddEntity eventAddEntity = new EventAddEntity();

        eventAddEntity.setKidId(event.mKids);
        eventAddEntity.setName(event.mName);
        eventAddEntity.setStartDate(getLocalTimeString(event.mStartDate));
        eventAddEntity.setEndDate(getLocalTimeString(event.mEndDate));
        eventAddEntity.setColor(event.mColor);
        eventAddEntity.setDescription(event.mDescription);
        eventAddEntity.setAlert(event.mAlert);
        eventAddEntity.setRepeat(event.mRepeat);
        eventAddEntity.setTimezoneOffset(CalendarManager.getTimezoneOffset());

        List<String> todos = new ArrayList<>();
        for (WatchTodo todo : event.mTodoList)
            todos.add(todo.mText);

        eventAddEntity.setTodo(todos);

        return eventAddEntity;
    }

    public static EventInfo getEventInfo4Update(@NonNull WatchEvent event) {

        EventInfo eventInfo = new EventInfo();

        eventInfo.setEventId(event.mId);
        eventInfo.setKidId(event.mKids);
        eventInfo.setName(event.mName);
        eventInfo.setStartDate(getLocalTimeString(event.mStartDate));
        eventInfo.setEndDate(getLocalTimeString(event.mEndDate));
        eventInfo.setColor(event.mColor);
        eventInfo.setDescription(event.mDescription);
        eventInfo.setAlert(event.mAlert);
        eventInfo.setRepeat(event.mRepeat);
        eventInfo.setTimezoneOffset(CalendarManager.getTimezoneOffset());

        List<String> todos = new ArrayList<>();
        for (WatchTodo todo : event.mTodoList)
            todos.add(todo.mText);

        eventInfo.setTodo(todos);

        return eventInfo;
    }

    public static DB_Todo getDBTodo(@NonNull WatchTodo watchTodo) {
        DB_Todo db_todo = new DB_Todo();

        db_todo.setTodoId(watchTodo.mId);
        db_todo.setText(watchTodo.mText);
        db_todo.setStatus(watchTodo.mStatus);
        db_todo.setDateCreated(watchTodo.mDateCreated);
        db_todo.setLastUpdated(watchTodo.mLastUpdated);
        db_todo.setEventId(watchTodo.mEventId);

        return db_todo;
    }

    static List<DB_FormatActivity> getDBFormatActivity(@NonNull List<WatchActivity> watchActivityList) {
        List<DB_FormatActivity> dbFormatActivities = new ArrayList<>(1);
        if (ObjectUtils.isListEmpty(watchActivityList)) {
            return dbFormatActivities;
        }

        for (WatchActivity watchActivity :
                watchActivityList) {
            if (watchActivity.mOutdoor.mId <= 0 && watchActivity.mIndoor.mId <= 0) {
                continue;
            }

            dbFormatActivities.add(getDBFormatActivity(watchActivity));
        }

        return dbFormatActivities;
    }

    static DB_FormatActivity getDBFormatActivity(WatchActivity watchActivity) {
        DB_FormatActivity db_formatActivity = new DB_FormatActivity();

        long actvId = watchActivity.mIndoor.mId > 0 ? watchActivity.mIndoor.mId : watchActivity.mOutdoor.mId;
        db_formatActivity.setActvId(actvId);
        db_formatActivity.setIndoorId(watchActivity.mIndoor.mId);
        db_formatActivity.setIndoorSteps(watchActivity.mIndoor.mSteps);
        db_formatActivity.setOutdoorId(watchActivity.mOutdoor.mId);
        db_formatActivity.setOutdoorSteps(watchActivity.mOutdoor.mSteps);

        if (watchActivity.mIndoor.mId > 0) {
            db_formatActivity.setMacId(watchActivity.mIndoor.mMacId);
            db_formatActivity.setKidId(watchActivity.mIndoor.mKidId);
            db_formatActivity.setTime(watchActivity.mIndoor.mTimestamp);
            db_formatActivity.setDistance(watchActivity.mIndoor.mDistance);
        } else {
            db_formatActivity.setMacId(watchActivity.mOutdoor.mMacId);
            db_formatActivity.setKidId(watchActivity.mOutdoor.mKidId);
            db_formatActivity.setTime(watchActivity.mOutdoor.mTimestamp);
            db_formatActivity.setDistance(watchActivity.mOutdoor.mDistance);
        }

        return db_formatActivity;
    }

    static List<DB_CloudActivity> getDBCloudActivity(@NonNull List<WatchActivity> watchActivityList) {
        List<DB_CloudActivity> dbCloudActivities = new ArrayList<>(1);
        if (ObjectUtils.isListEmpty(watchActivityList)) {
            return dbCloudActivities;
        }

        for (WatchActivity watchActivity :
                watchActivityList) {
            dbCloudActivities.add(getDBCloudActivity(watchActivity));
        }

        return dbCloudActivities;
    }

    static DB_CloudActivity getDBCloudActivity(@NonNull WatchActivity watchActivity) {
        DB_CloudActivity db_cloudActivity = new DB_CloudActivity();

        WatchActivity.Act dataAct = null;
        String type = "";
        if (watchActivity.mIndoor.mId > 0) {
            dataAct = watchActivity.mIndoor;
            type = ActivityCloudDataStore.Activity_type_indoor;
        } else if (watchActivity.mOutdoor.mId > 0) {
            dataAct = watchActivity.mOutdoor;
            type = ActivityCloudDataStore.Activity_type_outdoor;
        }

        if (dataAct == null) {
            return null;
        }


        db_cloudActivity.setActvId(dataAct.mId);
        db_cloudActivity.setKidId(dataAct.mKidId);
        db_cloudActivity.setMacId(dataAct.mMacId);
        db_cloudActivity.setDistance(dataAct.mDistance);
        db_cloudActivity.setReceivedDate(dataAct.mTimestamp);
        db_cloudActivity.setSteps(dataAct.mSteps);

        db_cloudActivity.setType(type);

        return db_cloudActivity;
    }

    /*static DB_CloudActivity getDBCloudActivity(@NonNull RetrieveDataRep.ActivitiesEntity activitiesEntity){
        DB_CloudActivity db_cloudActivity = new DB_CloudActivity();
        db_cloudActivity.setActvId((long) activitiesEntity.getId());
        db_cloudActivity.setMacId(activitiesEntity.getMacId());
        db_cloudActivity.setKidId(activitiesEntity.getKidId());
        db_cloudActivity.setType(activitiesEntity.getType());
        db_cloudActivity.setSteps((long) activitiesEntity.getSteps());
        db_cloudActivity.setDistance((long) activitiesEntity.getDistance());
        db_cloudActivity.setReceivedDate(activitiesEntity.getReceivedDate());
    }*/

    static DB_RawActivity getDBRawActivity(ActivityModel activityModel) {
        DB_RawActivity db_rawActivity = new DB_RawActivity();

        db_rawActivity.setMacId(activityModel.getMacId());
        db_rawActivity.setIndoorActivity(activityModel.getIndoorActivity());
        db_rawActivity.setOutdoorActivity(activityModel.getOutdoorActivity());
        db_rawActivity.setTime((long) activityModel.getTime());
        db_rawActivity.setTimeZoneOffset(activityModel.getTimeZoneOffset());
        db_rawActivity.setStatus(RawActivityDataStore.status_pending);

        return db_rawActivity;
    }

    static RawActivityDataEntity getRawActivityDataEntity(DB_RawActivity dbRawActivity) {
        RawActivityDataEntity rawActivityDataEntity = new RawActivityDataEntity();

        rawActivityDataEntity.setMacId(dbRawActivity.getMacId());
        rawActivityDataEntity.setIndoorActivity(dbRawActivity.getIndoorActivity());
        rawActivityDataEntity.setOutdoorActivity(dbRawActivity.getOutdoorActivity());
        rawActivityDataEntity.setTime(dbRawActivity.getTime());
        rawActivityDataEntity.setTimeZoneOffset(dbRawActivity.getTimeZoneOffset());

        return rawActivityDataEntity;
    }

    public static WatchActivity getWatchActivity(DB_FormatActivity db_formatActivity) {
        WatchActivity watchActivity = new WatchActivity();
        watchActivity.mIndoor.mId = db_formatActivity.getIndoorId();
        watchActivity.mIndoor.mSteps = db_formatActivity.getIndoorSteps();
        watchActivity.mOutdoor.mId = db_formatActivity.getOutdoorId();
        watchActivity.mOutdoor.mSteps = db_formatActivity.getOutdoorSteps();
        watchActivity.mIndoor.mMacId = db_formatActivity.getMacId();
        watchActivity.mOutdoor.mMacId = watchActivity.mIndoor.mMacId;
        watchActivity.mIndoor.mKidId = db_formatActivity.getKidId();
        watchActivity.mOutdoor.mKidId = watchActivity.mIndoor.mKidId;
        watchActivity.mIndoor.mTimestamp = db_formatActivity.getTime();
        watchActivity.mOutdoor.mTimestamp = watchActivity.mIndoor.mTimestamp;
        return watchActivity;
    }

    public static List<WatchActivity> getWatchActivity(List<DB_FormatActivity> db_formatActivities) {
        List<WatchActivity> list = new ArrayList<>();
        for (DB_FormatActivity db_formatActivity : db_formatActivities) {
            WatchActivity watchActivity = getWatchActivity(db_formatActivity);
            list.add(watchActivity);
        }
        return list;
    }

    public static WatchContact.Kid getKidsForUI(KidsEntityBean kidsEntityBean) {
        WatchContact.Kid kid = new WatchContact.Kid();
        kid.mId = kidsEntityBean.getKidsId();
        kid.mName = kidsEntityBean.getName();
        kid.mLastUpdate = kidsEntityBean.getLastUpdate();
        kid.mDateCreated = kidsEntityBean.getDateCreated();
        kid.mMacId = kidsEntityBean.getMacId();
        kid.mUserId = kidsEntityBean.getParentId();
        kid.mFirmwareVersion = kidsEntityBean.getFirmwareVersion();
        kid.mProfile = kidsEntityBean.getProfile();

        return kid;

    }

    public static String getStepString(long steps) {
        return String.format(Locale.getDefault(), "%,d", steps);
    }

    public static DB_Kids updateDBKids(@NonNull DB_Kids db_kids,
                                       @NonNull KidsWithParent kidsWithParent,
                                       long updateTime) {
        db_kids.setKidsId(kidsWithParent.getId());
        db_kids.setName(kidsWithParent.getName());
        db_kids.setMacId(kidsWithParent.getMacId());
        db_kids.setDateCreated(getUTCTimeStamp(kidsWithParent.getDateCreated()));
        db_kids.setProfile(kidsWithParent.getProfile());

        //因为目前服务端不更新lastupdate，客户端本地更新
        db_kids.setLastUpdate(updateTime);

        return db_kids;
    }

    static DB_EventKids updateEventKids(DB_EventKids dbEventKids,
                                               KidsWithParent kidsWithParent,
                                        long updateTime) {

        dbEventKids.setKidsId(kidsWithParent.getId());
        dbEventKids.setName(kidsWithParent.getName());
        dbEventKids.setMacId(kidsWithParent.getMacId());
        dbEventKids.setProfile(kidsWithParent.getProfile());

        //因为目前服务端不更新lastupdate，客户端本地更新
        dbEventKids.setLastUpdate(updateTime);

        return dbEventKids;
    }

    static List<DB_EventKids> updateEventKidsList(List<DB_EventKids> db_eventKidsList,
                                                  KidsWithParent kidsWithParent,
                                                  long updateTime){

        for (DB_EventKids dbEventKids :
                db_eventKidsList) {

            dbEventKids.setKidsId(kidsWithParent.getId());
            dbEventKids.setName(kidsWithParent.getName());
            dbEventKids.setMacId(kidsWithParent.getMacId());
            dbEventKids.setProfile(kidsWithParent.getProfile());

            //因为目前服务端不更新lastupdate，客户端本地更新
            dbEventKids.setLastUpdate(updateTime);
        }

        return db_eventKidsList;
    }


    public static WatchContact.User getWatchContact(UserInfo userInfo) {

        WatchContact.User user = new WatchContact.User();
        user.mId = userInfo.getId();
        user.mEmail = userInfo.getEmail();
        user.mFirstName = userInfo.getFirstName();
        user.mLastUpdate = getUTCTimeStamp(userInfo.getLastUpdate());
        user.mDateCreated = getUTCTimeStamp(userInfo.getDateCreated());

        user.mPhoneNumber = userInfo.getPhoneNumber();
        user.mProfile = userInfo.getProfile();
        user.mZipCode = userInfo.getZipCode();


        return user;

    }
}
