package com.daomaker.maker;

import com.daomaker.Maker;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * @since 2016/1/14/0014.
 */
public class EventMaker implements Maker {
    @Override
    public Entity build(Schema schema) {
        Entity event = schema.addEntity("DB_Event");
        event.setTableName("t_event");
        event.setClassNameDao("EventDao");
//        entity.addIdProperty().primaryKey().autoincrement().unique();
        event.addLongProperty("eventId").columnName("event_id").notNull().primaryKey();
        event.addLongProperty("userId").columnName("user_id");
        event.addStringProperty("kidIds").columnName("kid_ids");
        event.addStringProperty("name").columnName("event_name");
        event.addLongProperty("startDate").columnName("start_date");
        event.addLongProperty("endDate").columnName("end_date");
        event.addStringProperty("color").columnName("color");
        event.addStringProperty("description").columnName("description");
        event.addIntProperty("alert").columnName("alert");
        event.addStringProperty("repeat").columnName("repeat");//DAILY, MONTHLY
        event.addIntProperty("timezoneOffset").columnName("timezoneOffset");
        event.addLongProperty("dateCreated").columnName("date_created");
        event.addLongProperty("lastUpdate").columnName("last_update");
        event.addStringProperty("status").columnName("status");


        //event to-do list table
        Entity todo = schema.addEntity("DB_Todo");
        todo.setTableName("t_todo");
        todo.setClassNameDao("TodoDao");
        todo.addLongProperty("todoId").columnName("todo_id").notNull().primaryKey();
        todo.addStringProperty("text").columnName("text");
        todo.addStringProperty("status").columnName("status");
        todo.addLongProperty("dateCreated").columnName("date_created");
        todo.addLongProperty("lastUpdated").columnName("last_updated");


        //建立event表与todo表一对多关系
        Property property = todo.addLongProperty("eventId").columnName("event_id").getProperty();
        todo.addToOne(event,property);

        event.addToMany(todo,property).setName("todoList");


        //event kids list table
        Entity eventKid = schema.addEntity("DB_EventKids");
        eventKid.setTableName("t_event_kids");
        eventKid.setClassNameDao("EventKidsDao");
        eventKid.addLongProperty("kidsId").columnName("kids_id").notNull().primaryKey();
        eventKid.addStringProperty("name").columnName("name");
        eventKid.addStringProperty("macId").columnName("macId");
        eventKid.addStringProperty("firmwareVersion").columnName("firmwareVersion");
        eventKid.addStringProperty("profile").columnName("profile");
        eventKid.addLongProperty("lastUpdate").columnName("last_update");

        //建立event表与eventKids表间的一对多关系
        Property property2 = eventKid.addLongProperty("eventId").columnName("event_id").getProperty();
        eventKid.addToOne(event,property2);

        event.addToMany(eventKid,property2).setName("eventKids");

        return event;
    }
}
