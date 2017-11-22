package com.kidsdynamic.swing.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.swing.BuildConfig;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.view.ViewUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * ProfileOptionFragment
 * Created by Administrator on 2017/11/1.
 */

public class ProfileOptionFragment extends ProfileBaseFragment {

    @BindView(R.id.profile_option_logout)
    protected View view_logout;

    @BindView(R.id.profile_option_version)
    protected TextView tv_version;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_profile_option, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();
        initView();
        return mView;

    }

    private void initView() {
        tv_version.setText(BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE);
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_option);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        getFragmentManager().popBackStack();
    }


    @OnClick(R.id.profile_option_logout)
    protected void logout(){
        //show dialog to confirm
        selectFragment(LogoutConfirmFragment.class.getName(),null,true);
    }

    @OnClick(R.id.profile_option_contact)
    protected void contactUs(){

        /*String url = "http://www.kidsdynamic.com";
        String language = mActivityMain.mConfig.getString(ActivityConfig.KEY_LANGUAGE);
        switch (language) {
            case "ru":
            case "es":
                url = "http://www.imaginarium.info/";
                break;
        }*/

        // force to KD's customer webpage
        String url = "http://www.imaginarium.info/";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.profile_option_manual)
    protected void UserManual(){
        String url = "https://childrenlab.s3.amazonaws.com/pdf/Swing_User_Guide.pdf";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    //todo 一期先实现成重置口令
    @OnClick(R.id.profile_option_change_password)
    public void resetPsw() {
        DB_User currentLoginUserInfo = LoginManager.getCurrentLoginUserInfo();
        String email = "";
        if(currentLoginUserInfo != null){
            email = currentLoginUserInfo.getEmail();
        }

        final String email2 = email;
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.profile_option_password)
                .setMessage(R.string.profile_reset_password_confirmation)
                .setNegativeButton(R.string.profile_request_to_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.watch_have_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //调用接口重置
                        dialogInterface.dismiss();
                        exeResetPswFlow(email2);
                    }
                }).show();

    }

    private void exeResetPswFlow(@NonNull final String email) {

        showLoadingDialog(R.string.signup_login_wait);

        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class, false);

        HashMap<String, String> resetPswParam = new HashMap<>(1);
        resetPswParam.put("email", email);
        userApiNeedToken.sendResetPasswordEmail(resetPswParam).enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                finishLoadingDialog();

                int code = response.code();
                if (code == 200) {
                    ViewUtils.showMsgDialog(getActivity(),
                            getString(R.string.profile_reset_password_note, email));
                } else {
                    Toast.makeText(getContext(), R.string.error_api_unknown + " " + code, Toast.LENGTH_SHORT).show();
                }

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                super.onFailure(call, t);
                finishLoadingDialog();

                Toast.makeText(getContext(), R.string.net_err, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
