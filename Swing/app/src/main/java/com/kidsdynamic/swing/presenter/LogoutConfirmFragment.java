package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.RawActivityManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.ActivityController;
import com.yy.base.utils.ToastCommon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * LogoutConfirmFragment
 * Created by Administrator on 2017/11/1.
 */

public class LogoutConfirmFragment extends ProfileBaseFragment {
    @BindView(R.id.signup_profile_photo)
    protected ViewCircle mViewPhoto;

    @BindView(R.id.tv_logout_account_name)
    protected TextView tv_accountName;

    @BindView(R.id.tv_account_email)
    protected TextView tv_accountEmail;

    @BindView(R.id.btn_confirm_logout)
    protected Button layout_logout;

    @BindView(R.id.btn_cancel)
    protected Button layout_cancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_logout_confirm, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();
        initView();
        return mView;

    }

    private void initView() {
        ViewUtils.setBtnTypeFace(getContext(),layout_cancel,
                layout_logout);
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_option_logout);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

        DB_User currentLoginUserInfo = LoginManager.getCurrentLoginUserInfo();
        if(currentLoginUserInfo != null){
            tv_accountEmail.setText(currentLoginUserInfo.getEmail());
            tv_accountName.setText(LoginManager.getUserName(currentLoginUserInfo));

            if(!TextUtils.isEmpty(currentLoginUserInfo.getProfile())){
                GlideHelper.getBitMap(getContext(), UserManager.getProfileRealUri(currentLoginUserInfo.getProfile()),
                        String.valueOf(currentLoginUserInfo.getLastUpdate()), new AvatarSimpleTarget(mViewPhoto));
            }
        }

    }

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {
        getFragmentManager().popBackStack();
    }

    /*private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewPhoto.setBitmap(bitmap);
            }
        }
    };*/

    //add 2018年2月4日17:14:50 weizg
    @OnClick(R.id.btn_confirm_logout)
    public void logoutOnline(){
        showLoadingDialog(R.string.activity_main_wait);

        final UserApiNeedToken userApiNeedToken =
                ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class, true);

        userApiNeedToken.userLogout().enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                finishLoadingDialog();

                if (response.code() == 200) {
                    logout();
                }else {
                    ToastCommon.makeText(getContext(),R.string.normal_err, response.code());
                    LogUtil2.getUtils().d("onResponse not 200");
                }

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                super.onFailure(call, t);

                ToastCommon.makeText(getContext(),R.string.normal_err, 1);
                finishLoadingDialog();
            }
        });
    }


    protected void logout(){
        //show dialog to confirm
        //del token and

        //请求缓存数据
        RawActivityManager.delAllRawActivity();

        ConfigUtil.logoutState();
//        getActivity().finish();

        ActivityController.getInstance().exit();
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    @OnClick(R.id.btn_cancel)
    protected void cancelLogout(){
        getFragmentManager().popBackStack();
    }
}
