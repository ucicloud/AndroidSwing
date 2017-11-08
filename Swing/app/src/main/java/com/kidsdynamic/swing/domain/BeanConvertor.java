package com.kidsdynamic.swing.domain;

import android.support.annotation.NonNull;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_Event;
import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.dao.DB_Todo;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.net.event.model.TodoEntity;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.user.model.KidInfo;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * date:   2017/10/26 15:33 <br/>
 */

public class BeanConvertor {

    public static DB_User getDBUser(@NonNull UserProfileRep userProfileRep){
        DB_User db_user = new DB_User();
        UserProfileRep.UserEntity userEntity = userProfileRep.getUser();

        db_user.setUserId(userEntity.getId());
        db_user.setEmail(userEntity.getEmail());
        db_user.setFirstName(userEntity.getName());
        db_user.setLastName(userEntity.getName());

        //dataCreate, lastUpdate 使用utc时间
        db_user.setDataCreate(getUTCTimeStamp(userEntity.getDateCreated()));
        db_user.setLastUpdate(getUTCTimeStamp(userEntity.getLastUpdate()));
        db_user.setPhoneNum(userEntity.getPhoneNumber());
        db_user.setProfile(userEntity.getProfile());
        db_user.setZipCode(userEntity.getZipCode());

        return db_user;
    }

    public static List<DB_Kids> getDBKidsList(@NonNull UserProfileRep userProfileRep){
        List<DB_Kids> db_kidsList = new ArrayList<>(3);
        List<UserProfileRep.KidsEntity> kidsList = userProfileRep.getKids();

        for (int i = 0; i < kidsList.size(); i++) {
            UserProfileRep.KidsEntity kidsEntity = kidsList.get(i);

            DB_Kids db_kids = new DB_Kids();

            db_kids.setKidsId(kidsEntity.getId());
            db_kids.setName(kidsEntity.getName());
            db_kids.setDateCreated(getUTCTimeStamp(kidsEntity.getDateCreated()));
            db_kids.setMacId(kidsEntity.getMacId());
            db_kids.setProfile(kidsEntity.getProfile());
            db_kids.setParentId(userProfileRep.getUser().getId());
        }

        return db_kidsList;
    }


    public static List<DB_Event> getDBEventList(@NonNull List<EventWithTodo> eventWithTodoList){
        List<DB_Event> db_eventList = new ArrayList<>();
        for (EventWithTodo eventWithTodo : eventWithTodoList) {
            db_eventList.add(getDBEvent(eventWithTodo));
        }

        return db_eventList;
    }

    public static DB_Event getDBEvent(@NonNull EventWithTodo eventWithTodo){
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

        return db_event;
    }

    private static String getAllKidsIdFromList(@NonNull List<KidInfo> kidInfoList){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < kidInfoList.size(); i++) {
            stringBuilder.append(kidInfoList.get(i).getId());
            if(i != (kidInfoList.size() -1)){
                stringBuilder.append("#");
            }
        }

        return stringBuilder.toString();
    }


    public static List<DB_Todo> getDBTodo(@NonNull List<EventWithTodo> eventWithTodoList){
        List<DB_Todo> db_todoList = new ArrayList<>();

        for (EventWithTodo eventWithTodo : eventWithTodoList) {

            List<TodoEntity> todoEntityList = eventWithTodo.getTodo();

            if(!ObjectUtils.isListEmpty(todoEntityList)){
                for (TodoEntity todoEntity : todoEntityList) {
                    DB_Todo db_todo = new DB_Todo();

                    db_todo.setTodoId(todoEntity.getId());
                    db_todo.setText(todoEntity.getText());
                    db_todo.setDateCreated(getUTCTimeStamp(todoEntity.getDateCreated()));
                    db_todo.setLastUpdated(getUTCTimeStamp(todoEntity.getLastUpdated()));
                    db_todo.setEventId((long) eventWithTodo.getId());

                    db_todoList.add(db_todo);
                }
            }
        }

        return db_todoList;
    }

    public static DB_Kids getDBKidsInfo(@NonNull KidsWithParent kidsWithParent){

            DB_Kids db_kids = new DB_Kids();

            db_kids.setKidsId(kidsWithParent.getId());
            db_kids.setName(kidsWithParent.getName());
            db_kids.setDateCreated(getUTCTimeStamp(kidsWithParent.getDateCreated()));
            db_kids.setMacId(kidsWithParent.getMacId());
            db_kids.setProfile(kidsWithParent.getProfile());
            db_kids.setParentId(kidsWithParent.getParent().getId());

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

    public static String getLocalTimeString(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date = new Date();
        date.setTime(timeStamp);
        return format.format(date);
    }


    public static List<WatchEvent> getWatchEvent(List<DB_Event> dbEvents){
        List<WatchEvent> watchEvents = new ArrayList<>(dbEvents.size());
        for (DB_Event db_event: dbEvents) {
            watchEvents.add(getWatchEvent(db_event));
        }

        return watchEvents;
    }

    public static WatchEvent getWatchEvent(DB_Event db_event){

        WatchEvent watchEvent = new WatchEvent();
        watchEvent.mId = db_event.getEventId();
        watchEvent.mUserId = db_event.getUserId();
        watchEvent.mName = db_event.getName();
        watchEvent.mStartDate = db_event.getStartDate();
        watchEvent.mEndDate = db_event.getEndDate();
        watchEvent.mKids = new ArrayList<Long>();//todo
        watchEvent.mColor = db_event.getColor();
        watchEvent.mStatus = db_event.getStatus();
        watchEvent.mDescription = db_event.getDescription();
        watchEvent.mAlert = db_event.getAlert();
        watchEvent.mRepeat = db_event.getRepeat();
        watchEvent.mTimezoneOffset = db_event.getTimezoneOffset();
        watchEvent.mDateCreated = db_event.getDateCreated();
        watchEvent.mLastUpdated = db_event.getLastUpdate();

        watchEvent.mTodoList = getWatchTodos(db_event.getUserId(),db_event.getTodoList());

        return watchEvent;

    }

    public static List<WatchTodo> getWatchTodos(long userId, List<DB_Todo> dbTodos){
        List<WatchTodo> watchTodos = new ArrayList<>(dbTodos.size());
        for (DB_Todo db_todo: dbTodos) {
            watchTodos.add(getWatchTodo(userId,db_todo));
        }

        return watchTodos;
    }

    public static WatchTodo getWatchTodo(long userId,DB_Todo db_todo){
//        new WatchTodo(1, 452, 0, "1 ", WatchTodo.STATUS_DONE);
        WatchTodo watchTodo = new WatchTodo();
        watchTodo.mId = db_todo.getTodoId();
        watchTodo.mUserId = userId;
        watchTodo.mEventId = db_todo.getEventId();
        watchTodo.mText = db_todo.getText();
        watchTodo.mStatus = db_todo.getStatus();

        return watchTodo;
    }

}
