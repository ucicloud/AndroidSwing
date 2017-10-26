package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidsdynamic.data.dao.EventDao;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.repository.disk.EventDataStore;
import com.kidsdynamic.data.repository.disk.TodoItemDataStore;

import java.util.List;

/**
 * date:   2017/10/26 16:57 <br/>
 */

public class EventManager {

    public void saveEventForLogin(@NonNull Context context, @NonNull List<EventWithTodo> eventWithTodoList){
        DbUtil dbUtil = DbUtil.getInstance(context.getApplicationContext());

        //首先清空本地数据，然后保存本次
        EventDataStore eventDataStore = new EventDataStore(dbUtil);
        eventDataStore.dealAll();

        TodoItemDataStore todoItemDataStore = new TodoItemDataStore(dbUtil);
        todoItemDataStore.dealAll();

        //save
        eventDataStore.saveAll(BeanConvertor.getDBEventList(eventWithTodoList));
        todoItemDataStore.saveAll(BeanConvertor.getDBTodo(eventWithTodoList));

    }
}
