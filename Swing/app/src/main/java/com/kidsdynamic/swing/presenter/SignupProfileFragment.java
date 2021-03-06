package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.kidsdynamic.commonlib.utils.SoftKeyBoardUtil;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.avatar.AvatarApi;
import com.kidsdynamic.data.net.avatar.PartUtils;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.net.user.UserApiNoNeedToken;
import com.kidsdynamic.data.net.user.model.LoginEntity;
import com.kidsdynamic.data.net.user.model.LoginSuccessRep;
import com.kidsdynamic.data.net.user.model.MyCountryCodeRep;
import com.kidsdynamic.data.net.user.model.RegisterEntity;
import com.kidsdynamic.data.net.user.model.RegisterFailResponse;
import com.kidsdynamic.data.net.user.model.UpdateProfileSuccess;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
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
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SignupProfileFragment
 * <p>
 * Created by Stefan on 2017/10/24.
 */

public class SignupProfileFragment extends BaseFragment {

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;

    private static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    @BindView(R.id.signup_profile_photo)
    ViewCircle vc_photo;
    @BindView(R.id.signup_profile_first)
    EditText et_first;
    @BindView(R.id.signup_profile_last)
    EditText et_last;
    @BindView(R.id.signup_profile_phone)
    EditText et_phone;
    //    @BindView(R.id.signup_profile_zip)
    //    EditText et_zip;
    @BindView(R.id.signup_profile_submit)
    Button btnSubmit;

    private File profile;

    public static SignupProfileFragment newInstance(String email, String pwd) {
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(PASSWORD, pwd);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEditTextStyle();
    }

    private void initEditTextStyle() {
        com.kidsdynamic.swing.utils.ViewUtils.setTextViewNormalTypeFace(getContext(), et_first,
                et_last, et_phone, btnSubmit);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                doCameraResult(data);
                break;
            case REQUEST_CODE_ALBUM:
                doAlbumResult(data);
                break;
        }
    }

    @OnClick(R.id.ib_back)
    public void back() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(SignupLoginFragment.newInstance(), false);
    }

    @OnClick(R.id.signup_profile_photo)
    public void addPhoto() {
        CharSequence array[] = new CharSequence[]{getString(R.string.profile_take_photo),
                getString(R.string.profile_choose_from_library)};
        BottomPopWindow.Builder builder = new BottomPopWindow.Builder(getContext());
        builder.setItems(array, new BottomPopWindow.Builder.OnWhichClickListener() {
            @Override
            public void onWhichClick(View v, int position) {
                doWhichClick(position);
            }
        });
        BottomPopWindow bottomPopWindow = builder.create();

        bottomPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Point navigationBarSize = ViewUtils.getNavigationBarSize(getContext());
        bottomPopWindow.showAtLocation(getView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                0, navigationBarSize.y);
    }

    @OnClick(R.id.signup_profile_submit)
    public void doSubmit() {
        //del 2017年11月4日09:13:23 only_app
        //头像非必填项
        /*if (null == profile || !profile.exists()) {
            return;
        }*/
        String firstName = et_first.getText().toString().trim();
        if (TextUtils.isEmpty(firstName)) {
            ToastCommon.makeText(getContext(), R.string.error_api_unknown);
            return;
        }
        String lastName = et_last.getText().toString().trim();
        if (TextUtils.isEmpty(lastName)) {
            ToastCommon.makeText(getContext(), R.string.error_api_unknown);
            return;
        }
        SoftKeyBoardUtil.hideSoftKeyboard(getActivity());
        String phoneNumber = et_phone.getText().toString().trim();
//        String zipCode = et_zip.getText().toString().trim();
        Bundle args = getArguments();
        String email = args.getString(EMAIL, "");
        String pwd = args.getString(PASSWORD, "");
        register(email, pwd, firstName, lastName, phoneNumber/*, zipCode*/);
    }

    private void register(@NonNull final String email, @NonNull final String psw,
                          @NonNull String firstName, @NonNull String lastName,
                          String phoneNumber/*, String zipCode*/) {
        showLoadingDialog(R.string.signup_profile_wait);
        //使用用户数据，执行注册，如果注册成功则进行登录，登录成功执行上传头像
        final RegisterEntity registerEntity = new RegisterEntity();
        registerEntity.setEmail(email);
        registerEntity.setPassword(psw);
        registerEntity.setFirstName(firstName);
        registerEntity.setLastName(lastName);
        registerEntity.setPhoneNumber(phoneNumber);
//        registerEntity.setZipCode(zipCode);

        final UserApiNoNeedToken userApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi(UserApiNoNeedToken.class, false);
        userApi.registerUser(registerEntity).enqueue(new BaseRetrofitCallback<RegisterFailResponse>() {
            @Override
            public void onResponse(Call<RegisterFailResponse> call, Response<RegisterFailResponse> response) {
                LogUtil2.getUtils().d("register onResponse");

                int code = response.code();
                if (code == 200) {
                    LogUtil2.getUtils().d("register ok and start login");

                    LoginEntity loginEntity = new LoginEntity();
                    loginEntity.setEmail(email);
                    loginEntity.setPassword(psw);
                    exeLogin(userApi, loginEntity);
                } else if (code == 409) {//邮箱已存在
                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(), R.string.error_api_user_register_409);
                } else {
                    LogUtil2.getUtils().d("register error code: " + code);

                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(), R.string.user_register_fail_common);
                }

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<RegisterFailResponse> call, Throwable t) {
                LogUtil2.getUtils().d("register onFailure");
                super.onFailure(call, t);

                finishLoadingDialog();
            }
        });
    }

    private void exeLogin(UserApiNoNeedToken userApi, LoginEntity loginEntity) {
        userApi.login(loginEntity).enqueue(new BaseRetrofitCallback<LoginSuccessRep>() {
            @Override
            public void onResponse(Call<LoginSuccessRep> call, Response<LoginSuccessRep> response) {

                if (response.code() == 200) {
                    LogUtil2.getUtils().d("login success");
                    LogUtil2.getUtils().d(response.body().getAccess_token());

                    // 缓存token
                    new LoginManager().cacheToken(response.body().getAccess_token());

                    if (profile != null) {
                        uploadAvatar(profile);
                    }

                    //同步数据
                    syncUserData();

                } else {
                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(), R.string.profile_login_fail);

                    gotoLoginFragment();
                    LogUtil2.getUtils().d("login error code: " + response.code());
                }

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<LoginSuccessRep> call, Throwable t) {
                super.onFailure(call, t);

                ToastCommon.makeText(getContext(), R.string.cloud_api_call_net_error);
                finishLoadingDialog();
                gotoLoginFragment();
            }
        });

    }

    private void uploadAvatar(File profile) {
        final AvatarApi avatarApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi4Avatar(AvatarApi.class);
        MultipartBody.Part filePart =
                PartUtils.prepareFilePart("upload", profile.getName(), profile);
        avatarApi.uploadUserAvatar(filePart).enqueue(new BaseRetrofitCallback<UpdateProfileSuccess>() {
            @Override
            public void onResponse(Call<UpdateProfileSuccess> call, Response<UpdateProfileSuccess> response) {
                LogUtil2.getUtils().d("uploadUserAvatar onResponse");
                super.onResponse(call, response);

                //code == 200 upload ok
                //todo 头像上传成功后
            }

            @Override
            public void onFailure(Call<UpdateProfileSuccess> call, Throwable t) {
                super.onFailure(call, t);

                t.printStackTrace();
            }
        });
    }

    private void syncUserData() {
        //登陆成功后，需要获取两个业务数据：
        //“/v1/user/retrieveUserProfile”

        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class, true);

        userApiNeedToken.retrieveUserProfile().enqueue(new BaseRetrofitCallback<UserProfileRep>() {
            @Override
            public void onResponse(Call<UserProfileRep> call, Response<UserProfileRep> response) {
                LogUtil2.getUtils().d("retrieveUserProfile onResponse");

                if (response.code() == 200) {//获取到用户信息
                    LogUtil2.getUtils().d("onResponse: " + response.body());

                    //先清除本地数据，然后再保存
                    new LoginManager().saveLoginData(getContext(), response.body());

                    //记录状态
                    new LoginManager().cacheLoginOK(getContext(), response.body().getUser());

                    //add 2018年3月13日10:31:54 only
                    //获取登录者IP所在区域code
                    getUserIPLocal();

                   /* finishLoadingDialog();

                    SignupActivity signupActivity = (SignupActivity) getActivity();
                    signupActivity.setFragment(WatchHaveFragment.newInstance(), false);*/

                } else {
                    finishLoadingDialog();
                    gotoLoginFragment();
                    ToastCommon.makeText(getContext(), R.string.profile_login_fail);
                }

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserProfileRep> call, Throwable t) {
                Log.d("syncData", "retrieveUserProfile error, ");
                super.onFailure(call, t);

                ToastCommon.makeText(getContext(), R.string.cloud_api_call_net_error);
                finishLoadingDialog();

                gotoLoginFragment();
            }
        });
    }

    //add 注册-登录成功，获取用户信息成功后处理流程
    public void loginFlowOK(){
        finishLoadingDialog();

        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(WatchHaveFragment.newInstance(), false);
    }

    //add 2018年3月13日10:30:24 only 新增获取登录者IP所在区域接口，为了进入到主界面后的安全信息对话框提示做准备
    //该接口不论调用成功失败，不影响登录流程执行
    private void getUserIPLocal(){
        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class, true);
        userApiNeedToken.myCountryCode().enqueue(new Callback<MyCountryCodeRep>() {
            @Override
            public void onResponse(Call<MyCountryCodeRep> call, Response<MyCountryCodeRep> response) {
                if (response.code() == 200) {
                    //如果查询成功，则再本地缓存查询到结果
                    new LoginManager().cacheLoginUserLocal(response.body());
                }

                loginFlowOK();
            }

            @Override
            public void onFailure(Call<MyCountryCodeRep> call, Throwable t) {

                loginFlowOK();
            }
        });
    }

    //如果注册成功，但是登录失败，则跳转到login
    private void gotoLoginFragment() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(SignupLoginFragment.newInstance(), false);
    }

    private void doWhichClick(int position) {
        switch (position) {
            case 0:
                startCameraActivity();
                break;
            case 1:
                startAlbumActivity();
                break;
        }
    }

    /**
     * Start virtual camera
     */
    private void startCameraActivity() {
        Intent intent = new Intent();
        intent.setClass(getContext(), VirtualCameraActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * Do result after taking picture
     *
     * @param data Intent
     */
    private void doCameraResult(Intent data) {
        if (null == data) return;
        File file = (File) data.getSerializableExtra(VirtualCameraActivity.FILE);
        if (file != null && file.exists()) {
            showCropPopWindow(file);
        }
    }

    /**
     * Start virtual album
     */
    private void startAlbumActivity() {
        Intent intent = new Intent();
        intent.setClass(getContext(), VirtualAlbumActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * Do result after picking picture from album
     *
     * @param data Intent
     */
    private void doAlbumResult(Intent data) {
        if (data == null) return;
        File file = (File) data.getSerializableExtra(VirtualAlbumActivity.FILE);
        if (file != null && file.exists()) {
            showCropPopWindow(file);
        }
    }

    private void showCropPopWindow(final File file) {
        final CropPopWindow cropPopWindow = new CropPopWindow(getContext());
        cropPopWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        cropPopWindow.setCropImageFile(file);
        cropPopWindow.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                cropPopWindow.dismiss();
                if (null == result) {
                    return;
                }
                vc_photo.setStrokeWidth(4.0f);
                vc_photo.setCrossWidth(0.0f);
                vc_photo.setBitmap(result.bitmap);
                profile = result.file;
            }
        });
    }

}
