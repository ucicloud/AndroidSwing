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
 * WatchSorryFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchSorryFragment extends BaseFragment {

    public static WatchSorryFragment newInstance() {
        Bundle args = new Bundle();
        WatchSorryFragment fragment = new WatchSorryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_sorry, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @OnClick(R.id.watch_sorry_search)
    public void searchAgain() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(WatchSearchFragment.newInstance(), false);
    }

    @OnClick(R.id.watch_sorry_request)
    public void request() {

    }

    @OnClick(R.id.watch_sorry_contact)
    public void contactUs() {

    }

}
