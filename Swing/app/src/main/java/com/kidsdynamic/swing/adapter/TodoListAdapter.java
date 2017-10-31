package com.kidsdynamic.swing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.data.net.event.model.EventUtils;
import com.kidsdynamic.data.net.event.model.TodoEntity;
import com.kidsdynamic.swing.R;

import java.util.ArrayList;
import java.util.List;

/**
 * todo list adapter
 * date:   2017/10/31 11:47 <br/>
 */

public class TodoListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<TodoEntity> todoEntityList = new ArrayList<>(1);

    public TodoListAdapter(Context context,List<TodoEntity> todoEntityList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.todoEntityList = todoEntityList;
    }

    @Override
    public int getCount() {
        return todoEntityList.size();
    }

    @Override
    public TodoEntity getItem(int i) {
        return todoEntityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_event_todolist,parent,false);
            viewHolder = ViewHolder.initView(convertView);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        TodoEntity item = getItem(position);
        viewHolder.tv_todo_describe.setText(item.getText());

        if (item.getStatus().equals(EventUtils.TODO_STATUS_DONE)) {
            viewHolder.checkView.setSelected(true);

            viewHolder.tv_todo_describe.setTextColor(
                    context.getResources().getColor(R.color.color_gray_light));
        }else {
            viewHolder.checkView.setSelected(false);
        }

        return convertView;
    }

    private static class ViewHolder{
        TextView tv_todo_describe;
        ImageView checkView;

        static ViewHolder initView(View containView){
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.tv_todo_describe = (TextView) containView.findViewById(R.id.tv_todo_content);
            viewHolder.checkView = (ImageView)containView.findViewById(R.id.todo_check);

            return viewHolder;
        }
    }
}
