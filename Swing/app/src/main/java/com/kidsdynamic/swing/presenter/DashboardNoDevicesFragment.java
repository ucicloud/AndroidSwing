package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidsdynamic.swing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DashboardNoDevicesFragment
 * <p>
 * Created by only_app on 2017/11/12.
 */

public class DashboardNoDevicesFragment extends DashboardBaseFragment {

    @BindView(R.id.dashboard_request_message)
    TextView tvMessage;
    @BindView(R.id.dashboard_add_device)
    TextView tvAddDevice;
    @BindView(R.id.dashboard_no_devices_goto)
    TextView tvGotoDashboard;

    public static DashboardNoDevicesFragment newInstance() {
        Bundle args = new Bundle();
        DashboardNoDevicesFragment fragment = new DashboardNoDevicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_nodevices, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_sync);
//        view_left_action.setImageResource(R.drawable.icon_left);

        tvMessage.setText(R.string.dashboard_main_no_device);
    }

    @OnClick(R.id.dashboard_add_device)
    public void gotoSearchFragment() {

    }

    @OnClick(R.id.dashboard_no_devices_goto)
    public void goToDashboard() {

    }

}
