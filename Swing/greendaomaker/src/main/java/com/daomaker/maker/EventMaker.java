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
        event.addStringProperty("kidIds").columnName("kid_ids");
        event.addStringProperty("name").columnName("event_name");
        event.addStringProperty("startDate").columnName("start_date");
        event.addStringProperty("endDate").columnName("end_date");
        event.addStringProperty("color").columnName("color");
        event.addStringProperty("description").columnName("description");
        event.addIntProperty("alert").columnName("alert");
        event.addStringProperty("repeat").columnName("repeat");//DAILY, MONTHLY
        event.addIntProperty("timezoneOffset").columnName("timezoneOffset");


        //event to-do list table
        Entity todo = schema.addEntity("DB_Todo");
        todo.setTableName("t_todo");
        todo.setClassNameDao("TodoDao");
        todo.addLongProperty("todoId").columnName("todo_id").notNull().primaryKey();
        todo.addStringProperty("text").columnName("text");
        todo.addStringProperty("status").columnName("status");
        todo.addStringProperty("dateCreated").columnName("date_created");
        todo.addStringProperty("lastUpdated").columnName("last_updated");


        //建立event表与todo表一对多关系
        Property property = todo.addLongProperty("eventId").columnName("event_id").getProperty();
        todo.addToOne(event,property);

        event.addToMany(todo,property).setName("todoList");


        return event;
    }
}
