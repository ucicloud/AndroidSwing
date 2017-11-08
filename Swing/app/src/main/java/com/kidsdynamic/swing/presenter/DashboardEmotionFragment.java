package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.R;

import butterknife.ButterKnife;

/**
 * DashboardEmotionFragment
 * <p>
 * Created by Stefan on 2017/11/7.
 */

public class DashboardEmotionFragment extends DashboardBaseFragment {

    public static DashboardEmotionFragment newInstance() {
        Bundle args = new Bundle();
        DashboardEmotionFragment fragment = new DashboardEmotionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_emotion, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_dashboard);
        view_left_action.setImageResource(R.mipmap.ic_loading);
        view_left_action.setPadding(36, 18, 0, 18);
    }
}
