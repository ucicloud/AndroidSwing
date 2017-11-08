package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.CalendarManager;
import com.kidsdynamic.swing.model.WatchEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kidsdynamic.swing.domain.EventManager.eventOptionMap;

/**
 * EventRepeatOptionFragment
 */

public class EventRepeatOptionFragment extends CalendarBaseFragment {
    @BindView(R.id.listview_repeat)
    protected ListView listView_event_option;

    private WatchEvent watchEvent;
    private EventRepeatListAdapter eventRepeatListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_calendar_repeat_option, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();

        initValue();
        return mView;
    }

    private void initValue() {
//        mainFrameActivity.mEventStack.pop();
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.calendar_event_repeat);
        view_left_action.setImageResource(R.drawable.icon_left);
    }

    @OnClick(R.id.main_toolbar_title)
    public void onToolbarAction1() {
//        mActivityMain.mEventStack.push(mEvent);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void initView() {
        eventRepeatListAdapter = new EventRepeatListAdapter(getEventOptions());
        listView_event_option.setAdapter(eventRepeatListAdapter);
        listView_event_option.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                handleOnItemClick(i);
            }
        });
    }

    private void handleOnItemClick(int i) {
        Bundle bundle = new Bundle();
        bundle.putString(CalendarManager.KEY_DATA_TYPE,CalendarManager.VALUE_DATA_TYPE_REPEAT);
        bundle.putString(CalendarManager.KEY_DATA_TYPE_REPEAT_VALUE,
                eventRepeatListAdapter.getItem(i));

        String optionShowStr = getContext().getString(
                eventOptionMap.get(eventRepeatListAdapter.getItem(i)));
        bundle.putString(CalendarManager.KEY_DATA_TYPE_REPEAT_STR,
                optionShowStr);

        mainFrameActivity.mCalendarBundleStack.push(bundle);

        getActivity().getSupportFragmentManager().popBackStack();
    }

    private List<String> getEventOptions(){
        List<String> options = new ArrayList<>(4);
        options.add(WatchEvent.REPEAT_NEVER);
        options.add(WatchEvent.REPEAT_DAILY);
        options.add(WatchEvent.REPEAT_WEEKLY);
        options.add(WatchEvent.REPEAT_MONTHLY);

        return options;
    }

    private class EventRepeatListAdapter extends BaseAdapter{
        private final List<String> repeatOptionList;
        private LayoutInflater inflater;

        public EventRepeatListAdapter(List<String> repeatOptionList){
            inflater = LayoutInflater.from(getContext());
            this.repeatOptionList = repeatOptionList;
        }

        @Override
        public int getCount() {
            return repeatOptionList.size();
        }

        @Override
        public String getItem(int i) {
            return repeatOptionList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_event_repeat,parent,false);
                viewHolder = ViewHolder.initView(convertView);

                convertView.setTag(viewHolder);

            }else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }

            //赋值
            String option = getItem(position);
            String optionShowStr = getContext().getString(eventOptionMap.get(option));
            viewHolder.tv_event_option.setText(optionShowStr);

            return convertView;
        }
    }

    private static class ViewHolder{
        TextView tv_event_option;

        static ViewHolder initView(View containView){
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.tv_event_option = (TextView) containView.findViewById(R.id.tv_event_repeat);

            return viewHolder;
        }
    }
}
