package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SignupLoginFragment
 * <p>
 * Created by Stefan on 2017/10/23.
 */

public class SignupLoginFragment extends BaseFragment {

    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.signup_login_hint_text)
    TextView tvHint;

    public static SignupLoginFragment newInstance() {
        Bundle args = new Bundle();
        SignupLoginFragment fragment = new SignupLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_signup_login, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et_email.setText("123@qq.com");
        et_password.setText("123456");
    }

    @OnClick(R.id.ib_back)
    public void back() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(SignupStartFragment.newInstance());
    }

    @OnClick(R.id.signup_login_login)
    public void login() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (ObjectUtils.isStringEmpty(email)) {
            String error = getString(R.string.error_api_unknown);
            tvHint.setText(String.format("*%s", error));
            tvHint.setVisibility(View.VISIBLE);
            return;
        }
        if (ObjectUtils.isStringEmpty(password)) {
            String error = getString(R.string.error_api_unknown);
            tvHint.setText(String.format("*%s", error));
            tvHint.setVisibility(View.VISIBLE);
            return;
        }
        hideSoftKeyboard(getActivity());
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(SignupProfileFragment.newInstance());
    }

    /**
     * Hide SoftKeyBoard
     *
     * @param activity Activity
     */
    private static boolean hideSoftKeyboard(Activity activity) {
        if (activity == null) {
            return false;
        }
        View view = activity.getCurrentFocus();
        if (null == view) {
            return false;
        }
        IBinder ib = view.getWindowToken();
        if (null == ib) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        return null != imm && imm.hideSoftInputFromWindow(ib, 0);
    }

}
