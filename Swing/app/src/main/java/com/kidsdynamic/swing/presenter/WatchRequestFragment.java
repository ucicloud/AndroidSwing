package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.data.net.user.model.KidInfo;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.view.ListLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WatchRequestFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchRequestFragment extends BaseFragment {

    @BindView(R.id.watch_request_title)
    TextView tv_title;
    @BindView(R.id.watch_request_container)
    ListLinearLayout ll_select;
    @BindView(R.id.watch_request_dashboard)
    TextView tv_dashboard;

    private DataAdapter dataAdapter;

    public static WatchRequestFragment newInstance() {
        Bundle args = new Bundle();
        WatchRequestFragment fragment = new WatchRequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_request, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        List<KidInfo> list = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            list.add(new KidInfo());
//        }
//        setDataAdapter(list);
    }

    @OnClick(R.id.watch_request_search)
    public void searchAgain() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(WatchSearchFragment.newInstance());
    }

    @OnClick(R.id.watch_request_dashboard)
    public void goDashBoard() {

    }

    public void setDataAdapter(List<KidInfo> list) {
        if (null == dataAdapter) {
            dataAdapter = new DataAdapter(getContext(), list);
        } else {
            dataAdapter.setData(list);
        }
        ll_select.setAdapter(dataAdapter);
    }

    private class DataAdapter extends BaseAdapter {

        private Context mContext;
        private List<KidInfo> mItems = new ArrayList<>();

        private DataAdapter(Context context, List<KidInfo> items) {
            mContext = context;
            mItems = items;
        }

        public void setData(List<KidInfo> items) {
            mItems.clear();
            mItems.addAll(items);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public KidInfo getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final WatchSelectFragment.ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.fragment_watch_request_list_linear_item, parent, false);
                holder = new WatchSelectFragment.ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (WatchSelectFragment.ViewHolder) convertView.getTag();
            }

            KidInfo kidInfo = getItem(position);
            if (null != kidInfo) {
                holder.iv_head.setImageResource(R.drawable.ic_icon_profile_);
                holder.tv_content.setText(String.valueOf(position));
                holder.iv_action.setImageResource(R.drawable.ic_icon_add_orange);
                holder.iv_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doPlusClick(holder.iv_action);
                    }
                });
            }

            return convertView;
        }
    }

    private void doPlusClick(ImageView iv_action) {
//        tv_title.setText("Request access pending");
//        tv_dashboard.setText("Go to Dashboard");
//        iv_action.setImageResource(android.R.drawable.ic_media_next);
    }

//    private void doRequestClick() {
//        SignupActivity signupActivity = (SignupActivity) getActivity();
//        signupActivity.setFragment(WatchRegisteredFragment.newInstance());
//    }

    static class ViewHolder {

        @BindView(R.id.iv_head)
        ImageView iv_head;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.iv_action)
        ImageView iv_action;

        ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
