package com.kidsdynamic.swing.domain;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_Event;
import com.kidsdynamic.data.dao.EventDao;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.persistent.DbUtil;
import com.kidsdynamic.data.repository.disk.EventDataStore;
import com.kidsdynamic.data.repository.disk.TodoItemDataStore;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.model.WatchTodo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

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

    public static List<WatchEvent> getEventList(long userId, long startTimeStamp, long endTimeStamp){
        boolean isTestData = true;
        if(isTestData){
            return getTestEventList(startTimeStamp, endTimeStamp);
        }

        List<WatchEvent> result = new ArrayList<>();
        //首先查询非repeat，且时间符合的event
        DbUtil dbUtil = DbUtil.getInstance(SwingApplication.getAppContext());
        EventDataStore eventDataStore = new EventDataStore(dbUtil);

        EventDao eventDao = eventDataStore.getDbUtil().getDaoSession().getEventDao();
        QueryBuilder<DB_Event> qb = eventDao.queryBuilder();

        /*rawQuery("SELECT * FROM " + TABLE_EVENT +
                " WHERE " + REPEAT + "=''" + " AND " +
                "((" + startTimeStamp + ">=" + START_DATE + " AND " + startTimeStamp + "<=" + END_DATE + ") OR" +
                " (" + startTimeStamp + "<=" + START_DATE + " AND " + endTimeStamp + ">=" + END_DATE + ") OR" +
                " (" + endTimeStamp + ">=" + START_DATE + " AND " + endTimeStamp + "<=" + END_DATE + "))", null);*/
        List<DB_Event> dbEvents = qb.where(EventDao.Properties.UserId.eq(userId),
                EventDao.Properties.Repeat.eq(""),
                qb.or(qb.and(EventDao.Properties.StartDate.le(startTimeStamp), EventDao.Properties.EndDate.ge(startTimeStamp)),
                        qb.and(EventDao.Properties.StartDate.ge(startTimeStamp), EventDao.Properties.EndDate.le(endTimeStamp))),
                qb.and(EventDao.Properties.StartDate.le(endTimeStamp), EventDao.Properties.EndDate.ge(endTimeStamp))
        ).orderAsc(EventDao.Properties.StartDate).list();

        if(!ObjectUtils.isListEmpty(dbEvents)){
            result.addAll(BeanConvertor.getWatchEvent(dbEvents));
        }

        //分别获取Daily, Weekly及Monthly的event；此回傳列表已會展開repeat, 換言之，若有Daily，則會直接
        // 展成一個月的events.
        // TODO: 2017/11/8  

        return result;

    }

    public static List<WatchEvent> getTestEventList(long start, long end){

        List<WatchEvent> list = new ArrayList<>();
        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 28, 8, 30,
                2017, 9, 28, 9, 10,
                WatchEvent.ColorList[0],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz",
                WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList((long)8);
        list.get(list.size() - 1).mTodoList = Arrays.asList(
                new WatchTodo(1, 452, 0, "1 Todo todo todo todo", WatchTodo.STATUS_DONE),
                new WatchTodo(2, 452, 0, "2 Todo todo todo todo", WatchTodo.STATUS_PENDING)
        );

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 27, 10, 0, 2017, 9, 27, 10, 50, WatchEvent.ColorList[1],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList((long)8);

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 27, 8, 30, 2017, 9, 27, 11, 30, WatchEvent.ColorList[2],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList((long)8);

        //26 号
        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 26, 10, 30, 2017, 9, 26, 11, 10, WatchEvent.ColorList[3],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList((long)8);

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 26, 10, 0, 2017, 9, 26, 10, 50, WatchEvent.ColorList[1],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList((long)8);

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 26, 8, 30, 2017, 9, 26, 11, 30, WatchEvent.ColorList[2],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList((long)8);

        list.add(new WatchEvent(0, 452, "Name",
                2017, 9, 26, 9, 30, 2017, 9, 26, 11, 30, WatchEvent.ColorList[2],
                "Deacription 1234567890 abcdefghijklmnopqrstuvwxyz", WatchEvent.AlarmList[0].mId, WatchEvent.REPEAT_NEVER));
        list.get(list.size() - 1).mKids = Arrays.asList((long)8);


        return list;
    }
}
