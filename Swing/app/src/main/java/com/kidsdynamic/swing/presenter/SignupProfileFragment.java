package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.ButterKnife;

/**
 * SignupProfileFragment
 * <p>
 * Created by Stefan on 2017/10/24.
 */

public class SignupProfileFragment extends BaseFragment {

    public static SignupProfileFragment newInstance() {
        Bundle args = new Bundle();
        SignupProfileFragment fragment = new SignupProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_signup_profile, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

}
