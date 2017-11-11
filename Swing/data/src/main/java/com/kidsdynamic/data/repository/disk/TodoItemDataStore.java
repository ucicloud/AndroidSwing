package com.kidsdynamic.data.repository.disk;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_Todo;
import com.kidsdynamic.data.dao.TodoDao;
import com.kidsdynamic.data.persistent.DbUtil;

import java.util.List;

/**
 * date:   2017/10/26 17:08 <br/>
 */

public class TodoItemDataStore {
    private DbUtil dbUtil;

    public TodoItemDataStore(DbUtil dbUtil){
        this.dbUtil = dbUtil;
    }

    public void dealAll(){
        TodoDao todoDao = dbUtil.getDaoSession().getTodoDao();
        todoDao.deleteAll();
    }

    public boolean saveAll(List<DB_Todo> db_todoList){
        if(ObjectUtils.isListEmpty(db_todoList)){
            return true;
        }

        TodoDao todoDao = dbUtil.getDaoSession().getTodoDao();
        todoDao.insertInTx(db_todoList);

        return true;
    }

    public boolean updateTodoStatus(List<DB_Todo> db_todoList){
        if(ObjectUtils.isListEmpty(db_todoList)){
            return true;
        }

        TodoDao todoDao = dbUtil.getDaoSession().getTodoDao();
        todoDao.updateInTx(db_todoList);

        return true;
    }

    public void deleteByEventId(long eventId){
        TodoDao todoDao = dbUtil.getDaoSession().getTodoDao();
        List<DB_Todo> dbTodos = todoDao.queryBuilder().
                where(TodoDao.Properties.EventId.eq(eventId)).list();

        todoDao.deleteInTx(dbTodos);
    }
}
