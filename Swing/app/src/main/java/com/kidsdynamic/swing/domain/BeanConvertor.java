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

import java.util.ArrayList;
import java.util.List;

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

        db_user.setDataCreate(userEntity.getDateCreated());
        db_user.setLastUpdate(userEntity.getLastUpdate());
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
            db_kids.setDateCreated(kidsEntity.getDateCreated());
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
        db_event.setKidIds(getAllKidsIdFromList(eventWithTodo.getKid()));
        db_event.setName(eventWithTodo.getName());
        db_event.setStartDate(eventWithTodo.getStartDate());
        db_event.setEndDate(eventWithTodo.getEndDate());
        db_event.setColor(eventWithTodo.getColor());
        db_event.setDescription(eventWithTodo.getDescription());
        db_event.setAlert(eventWithTodo.getAlert());
        db_event.setRepeat(eventWithTodo.getRepeat());
        db_event.setTimezoneOffset(eventWithTodo.getTimezoneOffset());

        return db_event;
    }

    private static String getAllKidsIdFromList(@NonNull List<KidInfo> kidInfoList){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < kidInfoList.size(); i++) {
            stringBuilder.append(kidInfoList.get(i).getId());
            if(i != (kidInfoList.size() -1)){
                stringBuilder.append(";");
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
                    db_todo.setDateCreated(todoEntity.getDateCreated());
                    db_todo.setLastUpdated(todoEntity.getLastUpdated());
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
            db_kids.setDateCreated(kidsWithParent.getDateCreated());
            db_kids.setMacId(kidsWithParent.getMacId());
            db_kids.setProfile(kidsWithParent.getProfile());
            db_kids.setParentId(kidsWithParent.getParent().getId());

        return db_kids;
    }

}
