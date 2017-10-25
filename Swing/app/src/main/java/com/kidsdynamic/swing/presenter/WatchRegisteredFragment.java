package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WatchRegisteredFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchRegisteredFragment extends BaseFragment {

    public static WatchRegisteredFragment newInstance() {
        Bundle args = new Bundle();
        WatchRegisteredFragment fragment = new WatchRegisteredFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_registered, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @OnClick(R.id.watch_registered_request)
    public void requestAccess() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(WatchRequestFragment.newInstance());
    }

    @OnClick(R.id.watch_registered_contact)
    public void contactUs() {

    }

}
