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
 * SignupStartFragment
 * <p>
 * Created by Stefan on 2017/10/23.
 */

public class SignupStartFragment extends BaseFragment {

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
        return layout;
    }


    @OnClick(R.id.signup_start_login)
    public void clickLogin(View v) {
        //查询账户是否注册；如果未注册则展示注册界面（输入last name, first name;avatar）;注册成功后，再次执行
        //登录流程；登录成功后，开始同步数据（同步那些？）
        //如果是已经注册账户，则尝试登陆，如果登陆失败，则显示找回密码按钮

        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(SignupLoginFragment.newInstance());
        signupActivity.setBackVisibility(View.VISIBLE);
    }

}
