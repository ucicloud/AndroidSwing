package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
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

/**
 * WatchSelectFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchSelectFragment extends BaseFragment {

    @BindView(R.id.watch_select_container)
    ListLinearLayout ll_select;

    private DataAdapter dataAdapter;

    public static WatchSelectFragment newInstance() {
        Bundle args = new Bundle();
        WatchSelectFragment fragment = new WatchSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_select, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<KidInfo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new KidInfo());
        }
        setDataAdapter(list);
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.fragment_watch_select_list_linear_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            KidInfo kidInfo = getItem(position);
            if (null != kidInfo) {
                holder.iv_head.setImageResource(android.R.drawable.ic_menu_compass);
                holder.tv_content.setText(String.valueOf(position));
                if (position < getCount() - 1) {
                    holder.iv_action.setImageResource(android.R.drawable.ic_menu_add);
                    holder.iv_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doPlusClick();
                        }
                    });
                } else {
                    holder.iv_action.setImageResource(android.R.drawable.ic_menu_more);
                    holder.iv_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doRequestClick();
                        }
                    });
                }
            }

            return convertView;
        }
    }

    private void doPlusClick() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(WatchProfileFragment.newInstance());
    }

    private void doRequestClick() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(WatchRegisteredFragment.newInstance());
    }

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
