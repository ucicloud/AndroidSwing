package com.kidsdynamic.swing.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.view.ViewCircle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WatchSyncFragment
 * <p>
 * Created by Stefan on 2017/10/26.
 */

public class WatchSyncFragment extends BaseFragment {

    @BindView(R.id.watch_sync_photo)
    ViewCircle vc_photo;

    public static WatchSyncFragment newInstance() {
        Bundle args = new Bundle();
        WatchSyncFragment fragment = new WatchSyncFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_sync, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }


    @OnClick(R.id.watch_sync_yes)
    public void yes() {

    }

    @OnClick(R.id.watch_sync_no)
    public void no() {
        //跳转到主界面
        startActivity(new Intent(getActivity(),MainFrameActivity.class));
    }

    @OnClick(R.id.watch_sync_yes)
    public void add() {

    }

}
