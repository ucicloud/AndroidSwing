package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidsdynamic.swing.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DashboardRequestFragment
 * <p>
 * Created by Stefan on 2017/11/7.
 */

public class DashboardRequestFragment extends DashboardBaseFragment {

    @BindView(R.id.dashboard_request_message)
    TextView tvMessage;
    @BindView(R.id.dashboard_request_access)
    TextView tvAccess;

    public static DashboardRequestFragment newInstance() {
        Bundle args = new Bundle();
        DashboardRequestFragment fragment = new DashboardRequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_request, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_dashboard);
//        view_left_action.setImageResource(R.drawable.icon_left);

        // just test
        int random = new Random().nextInt();
        if (random % 2 == 0) {
            tvMessage.setText(R.string.dashboard_request_no_data);
            tvAccess.setVisibility(View.VISIBLE);
        } else {
            tvMessage.setText(R.string.dashboard_request_pending);
        }
    }

    @OnClick(R.id.dashboard_request_access)
    public void request() {

    }

    @OnClick(R.id.dashboard_request_profile)
    public void goToProfile() {

    }

}
