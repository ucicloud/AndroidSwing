package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WatchRegisteredFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchRegisteredFragment extends BaseFragment {

    private static final String DATA = "data";

    public static WatchRegisteredFragment newInstance(KidsWithParent kidsWithParent) {
        Bundle args = new Bundle();
        WatchRegisteredFragment fragment = new WatchRegisteredFragment();
        args.putSerializable(DATA, kidsWithParent);
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
        Bundle args = getArguments();
        KidsWithParent kidsWithParent = null != args ? (KidsWithParent) args.getSerializable(DATA) : null;
        Activity activity = getActivity();
        if (activity instanceof SignupActivity) {
            SignupActivity signupActivity = (SignupActivity) activity;
            signupActivity.setFragment(WatchRequestFragment.newInstance(kidsWithParent), true);
        } else if (activity instanceof MainFrameActivity) {
            MainFrameActivity mainFrameActivity = (MainFrameActivity) activity;
            mainFrameActivity.setFragment(WatchRequestFragment.newInstance(kidsWithParent), true);
        }
    }

    @OnClick(R.id.watch_registered_contact)
    public void contactUs() {
        // force to KD's customer webpage
        String url = "http://www.imaginarium.info/";

        String language = Locale.getDefault().getLanguage();
        switch (language) {
            case "zh":
            case "es":
            case "ja":
                url = "https://www.kidsdynamic.com";
                break;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
