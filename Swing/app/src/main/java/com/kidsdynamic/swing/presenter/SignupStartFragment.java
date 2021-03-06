package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SignupStartFragment
 * <p>
 * Created by Stefan on 2017/10/23.
 */

public class SignupStartFragment extends BaseFragment {

    @BindView(R.id.signup_start_login)
    Button btn_login;
    @BindView(R.id.signup_start_or)
    TextView tvOr;
    @BindView(R.id.signup_login_facebook)
    Button btnFacebook;
    @BindView(R.id.signup_login_google)
    Button btnGoogle;

    public static SignupStartFragment newInstance() {
        Bundle args = new Bundle();
        SignupStartFragment fragment = new SignupStartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_signup_start, container, false);
        ButterKnife.bind(this, layout);

        ViewUtils.setBtnTypeFace(getContext(), btn_login);
        ViewUtils.setTextViewNormalTypeFace(getContext(), tvOr, btnFacebook, btnGoogle);
        return layout;
    }

    @OnClick(R.id.signup_start_login)
    public void login(View v) {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(SignupLoginFragment.newInstance(), false);
    }

}
