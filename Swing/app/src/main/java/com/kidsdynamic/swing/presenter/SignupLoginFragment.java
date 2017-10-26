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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.event.EventApi;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.net.user.UserApiNoNeedToken;
import com.kidsdynamic.data.net.user.model.LoginEntity;
import com.kidsdynamic.data.net.user.model.LoginSuccessRep;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @BindView(R.id.signup_login_reset_pwd)
    Button btn_resetPsw;

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
        //查询账户是否注册；如果未注册则展示注册界面（输入last name, first name;avatar）;注册成功后，再次执行
        //登录流程；登录成功后，开始同步数据（同步那些？）
        //如果是已经注册账户，则尝试登陆，如果登陆失败，则显示找回密码按钮

        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (ObjectUtils.isStringEmpty(email)) {
            showErrInfo(R.string.error_api_unknown);
            return;
        }
        if (ObjectUtils.isStringEmpty(password)) {
            showErrInfo(R.string.error_api_unknown);
            return;
        }

        hideSoftKeyboard(getActivity());

        //执行登录业务
        exeLoginFlow(email,password);
    }

    private void showErrInfo(int msgId) {
        String error = getString(msgId);
        tvHint.setText(String.format("*%s", error));
        tvHint.setVisibility(View.VISIBLE);
    }

    void exeLoginFlow(String email, String psw){
        //show waiting dialog
        showLoadingDialog(R.string.activity_main_wait);

        final LoginEntity loginEntity = new LoginEntity();
        loginEntity.setEmail(email);
        loginEntity.setPassword(psw);

        //查询账户是否注册；如果未注册则展示注册界面（输入last name, first name;avatar）;注册成功后，再次执行
        //登录流程；登录成功后，开始同步数据（同步那些？）
        final UserApiNoNeedToken userApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNoNeedToken.class,false);

        userApi.checkEmailAvailableToRegister(email).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                LogUtil2.getUtils().d("check mail onResponse code: " + response.code());

                if (response.code() == 200) {
                    SignupLoginFragment.this.finishLoadingDialog();
                    //邮箱未注册，则展示注册界面
                    showRegisterUI();

                }else if(response.code() == 409){
                    //邮箱已经注册，则执行登录流程
                    loginByEmail(userApi,loginEntity);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                //dismiss waiting dialog,show error; terminate exe
                SignupLoginFragment.this.finishLoadingDialog();

                t.printStackTrace();
                LogUtil2.getUtils().d("check mail fail, " );

                showErrInfo(R.string.signup_profile_login_failed);
            }
        });

    }

    private void showRegisterUI() {
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

    private void loginByEmail(UserApiNoNeedToken userApi, LoginEntity loginEntity) {
        userApi.login(loginEntity).enqueue(new Callback<LoginSuccessRep>() {
            @Override
            public void onResponse(Call<LoginSuccessRep> call, Response<LoginSuccessRep> response) {
                //

                if(response.code() == 200){
                    LogUtil2.getUtils().d("login success");
                    LogUtil2.getUtils().d(response.body().getAccess_token());
                    //缓存token
                    new LoginManager().cacheToken(response.body().getAccess_token());

                    //同步数据
                    syncData();

                }else{
                    SignupLoginFragment.this.finishLoadingDialog();
                    LogUtil2.getUtils().d("login error, code: " + response.code());
                    btn_resetPsw.setVisibility(View.VISIBLE);

                    showErrInfo(R.string.signup_profile_login_failed);
                }
            }

            @Override
            public void onFailure(Call<LoginSuccessRep> call, Throwable t) {
                //fail, dismiss dialog, show error info
                SignupLoginFragment.this.finishLoadingDialog();
                showErrInfo(R.string.signup_profile_login_failed);
            }
        });
    }

    private void syncData() {
        // TODO: 2017/10/17 同步账户数据 成功后关闭等待对话框，跳转到主界面；失败提醒
        Toast.makeText(getActivity(),"login ok, start sync data", Toast.LENGTH_SHORT).show();

        //登陆成功后，需要获取两个业务数据：
        //“/v1/user/retrieveUserProfile”
        //“/v1/event/retrieveAllEventsWithTodo”

        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class,true);

        userApiNeedToken.retrieveUserProfile().enqueue(new Callback<UserProfileRep>() {
            @Override
            public void onResponse(Call<UserProfileRep> call, Response<UserProfileRep> response) {
                LogUtil2.getUtils().d("onResponse: " + response.code());
                if(response.code() == 200){
                    LogUtil2.getUtils().d("onResponse: " + response.body());

                    //todo save to db
                    //继续获取信息
                    getKidInfos();

                }else {
                    //todo dismiss dialog
                    LogUtil2.getUtils().d("onResponse not 200" );
                }
            }

            @Override
            public void onFailure(Call<UserProfileRep> call, Throwable t) {
                //todo dismiss dialog
                LogUtil2.getUtils().d("retrieveUserProfile error, ");
                t.printStackTrace();
            }
        });
    }

    private void getKidInfos() {
        LogUtil2.getUtils().d("getKidInfos ");
        final EventApi eventApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(EventApi.class,true);

        eventApi.retrieveAllEventsWithTodo().enqueue(new Callback<List<EventWithTodo>>() {
            @Override
            public void onResponse(Call<List<EventWithTodo>> call, Response<List<EventWithTodo>> response) {
                if(response.code() == 200){
                    LogUtil2.getUtils().d("onResponse: " + response.body());

                    //todo dismiss dialog save to db
                    Toast.makeText(getActivity()," sync data ok, show next UI", Toast.LENGTH_SHORT).show();

                    //todo 登陆成功后，进入到主界面

                }else{
                    LogUtil2.getUtils().d("login error, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EventWithTodo>> call, Throwable t) {
                finishLoadingDialog();

                LogUtil2.getUtils().d("retrieveAllEventsWithTodo error, ");
                t.printStackTrace();
            }
        });
    }


}
