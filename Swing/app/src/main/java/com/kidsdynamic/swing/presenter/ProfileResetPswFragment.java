package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.commonlib.utils.SoftKeyBoardUtil;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.avatar.AvatarApi;
import com.kidsdynamic.data.net.avatar.PartUtils;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.net.user.UserApiNoNeedToken;
import com.kidsdynamic.data.net.user.model.UpdatePasswordRequest;
import com.kidsdynamic.data.net.user.model.UpdateProfileEntity;
import com.kidsdynamic.data.net.user.model.UpdateProfileSuccess;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.BottomPopWindow;
import com.kidsdynamic.swing.view.CropImageView;
import com.kidsdynamic.swing.view.CropPopWindow;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;
import com.yy.base.utils.ViewUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * ProfileResetPswFragment
 */

public class ProfileResetPswFragment extends ProfileBaseFragment {

    private MainFrameActivity mActivityMain;
    private View mViewMain;

    @BindView(R.id.profile_new_psw)
    protected EditText mViewNewPsw_1;
    @BindView(R.id.profile_new_psw_2)
    protected EditText mViewNewPsw_2;

    @BindView(R.id.btn_update_profile)
    protected Button btn_save;


    AlertDialog mDialog;
    private Uri mPhotoUri;
    public final static int ACTIVITY_RESULT_CAMERA_REQUEST = 1888;
    public final static int ACTIVITY_RESULT_PHOTO_PICK = 9111;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (MainFrameActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_reset_psw, container, false);

        ButterKnife.bind(this,mViewMain);

//        mViewPhoto.setOnClickListener(mPhotoListener);

        btn_save.setTypeface(SwingFontsCache.getBoldType(getContext()));
        initTitleBar();

        mViewMain.findViewById(R.id.layout_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("profile","onclick layout");
                hideInput();
            }
        });

        return mViewMain;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initValue();
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.signup_login_reset_password);
        view_left_action.setImageResource(R.drawable.icon_left);

        view_right_action.setImageResource(R.drawable.icon_delete);
        view_right_action.setVisibility(View.INVISIBLE);
       /* view_right_action.setTag(R.drawable.icon_add);*/
    }

   /* @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_profile), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_left, R.mipmap.icon_ok);
    }*/

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {
        getActivity().getSupportFragmentManager().popBackStack();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initValue() {
    }


    @Override
    public void onPause() {
        super.onPause();

        InputMethodManager inputMethodManager = (InputMethodManager) mActivityMain
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);
    }


    @OnClick(R.id.btn_update_psw)
    public void resetPsw(){
        SoftKeyBoardUtil.hideSoftKeyboard(getActivity());

        String newPsw_1 = mViewNewPsw_1.getText().toString().trim();
        String newPsw_2 = mViewNewPsw_2.getText().toString().trim();

        if(TextUtils.isEmpty(newPsw_1)
                || TextUtils.isEmpty(newPsw_2)){
            ToastCommon.makeText(getContext(),R.string.enter_password);
            return;
        }

        if(!newPsw_1.equals(newPsw_2)){
            //如果两次输入不一致
            ToastCommon.makeText(getContext(),R.string.password_not_same);
            return;
        }

        updatePsw(newPsw_1);
    }

    private void updatePsw(String newPsw){
        showLoadingDialog(R.string.signup_profile_wait);

        final UserApiNeedToken userApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi(UserApiNeedToken.class, true);

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setNewPassword(newPsw);
        userApi.updatePassword(updatePasswordRequest).enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                //code == 200 update ok
                int code = response.code();
                Log.d("profile","update code:" + code);
                if(code == 200){
                    ToastCommon.makeText(getContext(), R.string.profile_editor_save_success);
                    getFragmentManager().popBackStack();
                }else {
                    ToastCommon.makeText(getContext(), R.string.profile_editor_avatar_failed);
                }

                finishLoadingDialog();
                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                ToastCommon.makeText(getContext(), R.string.profile_editor_avatar_failed);
                super.onFailure(call, t);
            }
        });

    }


}
