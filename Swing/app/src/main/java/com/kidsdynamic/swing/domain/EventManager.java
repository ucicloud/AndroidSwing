package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.repository.disk.EventDataStore;
import com.kidsdynamic.data.repository.disk.TodoItemDataStore;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.model.WatchEvent;

import java.util.HashMap;
import java.util.List;

/**
 * date:   2017/10/26 16:57 <br/>
 */

public class EventManager {
    public static HashMap<String, Integer> eventOptionMap = new HashMap<>(4);
    static {
        eventOptionMap.put(WatchEvent.REPEAT_NEVER, R.string.event_repeat_never);
        eventOptionMap.put(WatchEvent.REPEAT_DAILY, R.string.event_repeat_daily);
        eventOptionMap.put(WatchEvent.REPEAT_WEEKLY, R.string.event_repeat_weekly);
        eventOptionMap.put(WatchEvent.REPEAT_MONTHLY, R.string.event_repeat_monthly);
    }

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
