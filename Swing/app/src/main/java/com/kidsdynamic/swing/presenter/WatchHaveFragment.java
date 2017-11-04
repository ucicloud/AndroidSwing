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
 * WatchHaveFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchHaveFragment extends BaseFragment {

    public static WatchHaveFragment newInstance() {
        Bundle args = new Bundle();
        WatchHaveFragment fragment = new WatchHaveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_have, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @OnClick({R.id.watch_have_yes, R.id.watch_have_no})
    public void onClick(View v) {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        switch (v.getId()) {
            case R.id.watch_have_yes:
                signupActivity.setFragmentAndAddBackStack(WatchSearchFragment.newInstance());
                break;
            case R.id.watch_have_no:
                signupActivity.setFragment(WatchPurchaseFragment.newInstance());
                break;
        }
    }

}
