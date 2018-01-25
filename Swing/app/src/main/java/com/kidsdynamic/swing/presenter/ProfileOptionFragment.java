package com.kidsdynamic.swing.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRequests;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.swing.BuildConfig;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    private MainFrameActivity mActivityMain;
    @BindView(R.id.profile_option_logout)
    protected View view_logout;

    @BindView(R.id.profile_option_version)
    protected TextView tv_version;

    private SubHostRequests requestInfo;

    @BindView(R.id.tv_edit_profile)
    TextView tv_edit_profile;
    @BindView(R.id.tv_change_psw)
    TextView tv_change_psw;
    @BindView(R.id.tv_edit_kids)
    TextView tv_edit_kids;
    @BindView(R.id.tv_manager_access)
    TextView tv_manager_access;
    @BindView(R.id.tv_switch_account)
    TextView tv_switch_account;
    @BindView(R.id.tv_logout)
    TextView tv_logout;
    @BindView(R.id.tv_about)
    TextView tv_about;
    @BindView(R.id.tv_language)
    TextView tv_language;
    @BindView(R.id.tv_watch_update)
    TextView tv_watch_update;
    @BindView(R.id.tv_watch_update_red_point)
    TextView tv_watch_update_red_point;
    @BindView(R.id.tv_contact_us)
    TextView tv_contact_us;
    @BindView(R.id.tv_privacy)
    TextView tv_privacy;
    @BindView(R.id.tv_user_guide)
    TextView tv_user_guide;
    @BindView(R.id.tv_version)
    TextView tv_label_version;

    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private boolean isReadStorageGranted, isWriteStorageGranted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (MainFrameActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_profile_option, container, false);

        ButterKnife.bind(this, mView);
        initTitleBar();
        initView();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DeviceManager.isFirmwareNeedUpdate()) {
            tv_watch_update_red_point.setVisibility(View.VISIBLE);
        } else {
            tv_watch_update_red_point.setVisibility(View.INVISIBLE);
        }
        registerUIReceiver();
    }

    private void initView() {
        tv_version.setText(String.format("%s %s", BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE));

        ViewUtils.setTextViewBoldTypeFace(getContext(), tv_edit_profile, tv_change_psw,
                tv_edit_kids, tv_manager_access, tv_switch_account, tv_logout, tv_about,
                tv_language, tv_watch_update, tv_watch_update_red_point, tv_contact_us, tv_privacy,
                tv_user_guide, tv_version, tv_label_version);
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_option);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mActivityMain.mSubHostList.isEmpty()) {
            requestInfo = mActivityMain.mSubHostList.pop();
        }
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        getFragmentManager().popBackStack();
    }


    @OnClick(R.id.profile_option_watch_share)
    protected void onYourWatchShareWithOther() {

        KidsEntityBean focusKidsInfo = DeviceManager.getFocusKidsInfo(getContext());
        if (null == focusKidsInfo) {
            return;
        }
        long focusKidsId = focusKidsInfo.getKidsId();
        if (focusKidsId == -1) {
            ToastCommon.makeText(getContext(), R.string.have_no_device);
            return;
        }

        //判断focus设备类型，如果是自己的设备跳转到profileKidsEditorFragment，
        // 如果是共享的，跳转到ProfileKidsFromSharedInfoFragment
        if (focusKidsInfo.getShareType() == DeviceManager.kidsType_other_kids) {
            //如果是别人共享给自己的
            if (requestInfo != null) {
                RequestAddSubHostEntity sharedKidsSubHostEntity = DeviceManager.getSharedKidsSubHostEntity(requestInfo, focusKidsInfo.getKidsId());
                if (sharedKidsSubHostEntity != null) {
                    mActivityMain.mSubHostInfoEntity.push(sharedKidsSubHostEntity);

                    selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(focusKidsInfo.getKidsId()), true);
                } else {
                    ToastCommon.showToast(getContext(), "subhost null");
                }
            } else {
                //如果尚未获取到最新的subHostList，则以数据库数据为准
                RequestAddSubHostEntity sharedKidsSubHostEntity = new RequestAddSubHostEntity();
                sharedKidsSubHostEntity.setId(focusKidsInfo.getSubHostId());

                mActivityMain.mSubHostInfoEntity.push(sharedKidsSubHostEntity);
                selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(focusKidsInfo.getKidsId()), true);
            }

        } else {
            //自己设备
            mActivityMain.mSubHostList.push(requestInfo);

            selectFragment(ProfileKidsInfoFragment.newInstance(focusKidsId), true);
        }

    }

    @OnClick(R.id.profile_option_switch_watch_account)
    protected void onSwitchAccount() {
        mActivityMain.mSubHostList.push(requestInfo);
        selectFragment(ProfileSwitchAccountFragment.class.getName(), null, true);
    }

    @OnClick(R.id.profile_option_logout)
    protected void logout() {
        //show dialog to confirm
        selectFragment(LogoutConfirmFragment.class.getName(), null, true);
    }

    @OnClick(R.id.profile_option_watch_update)
    protected void watchUpdate() {
        checkPermissions();
//        Intent intent = new Intent();
//        Activity activity = getActivity();
//        intent.setClass(activity, FirmwareUpgradeActivity.class);
//        activity.startActivity(intent);
    }

    @OnClick(R.id.profile_option_contact)
    protected void contactUs() {

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

    @OnClick(R.id.profile_option_manual)
    protected void UserManual() {
        String url = "https://childrenlab.s3.amazonaws.com/pdf/Swing_User_Guide.pdf";

        String lang = Locale.getDefault().getLanguage();

        switch (lang) {
            case "zh":
                url = "https://childrenlab.s3.amazonaws.com/pdf/Swing_UserGuide_CN_KD_v1.0.pdf";
                break;
            case "es":
                url = "https://childrenlab.s3.amazonaws.com/pdf/Swing_UserGuide_ENG_KD_v1.0.pdf";
                break;
            case "ja":
                url = "https://childrenlab.s3.amazonaws.com/pdf/Swing_UserGuide_JP_KD_v1.0.pdf";
                break;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.profile_edit)
    public void editUserProfile() {
        selectFragment(ProfileEditorFragment.class.getName(), null, true);
    }

    @OnClick(R.id.profile_option_profile)
    public void editFocusKidsProfile() {
        //modify 2018年1月11日14:24:27 only 当前focus的设备可能是别人共享的，
        KidsEntityBean focusKidsInfo = DeviceManager.getFocusKidsInfo(getContext());

        if (focusKidsInfo == null) {
            ToastCommon.makeText(getContext(), R.string.have_no_device);
            return;
        }


        //判断focus设备类型，如果是自己的设备跳转到profileKidsEditorFragment，
        // 如果是共享的，应该提醒用户无权限编辑
        if (focusKidsInfo.getShareType() == DeviceManager.kidsType_other_kids) {
            //如果是别人共享给自己的

            ToastCommon.makeText(getContext(), R.string.no_primary_edit);
            /*if(requestInfo != null){
                RequestAddSubHostEntity sharedKidsSubHostEntity = DeviceManager.getSharedKidsSubHostEntity(requestInfo, focusKidsInfo.getKidsId());
                if(sharedKidsSubHostEntity != null){
                    mActivityMain.mSubHostInfoEntity.push(sharedKidsSubHostEntity);

                    selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(focusKidsInfo.getKidsId()),true);
                }else {
                    ToastCommon.showToast(getContext(),"subhost null");
                }
            }else {
                //如果尚未获取到最新的subHostList，则以数据库数据为准
                RequestAddSubHostEntity sharedKidsSubHostEntity = new RequestAddSubHostEntity();
                sharedKidsSubHostEntity.setId(focusKidsInfo.getSubHostId());

                mActivityMain.mSubHostInfoEntity.push(sharedKidsSubHostEntity);
                selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(focusKidsInfo.getKidsId()),true);
            }*/

        } else {
            //自己的设备
            //跳转到编辑kids 信息界面
            WatchContact.Kid watchKidsInfo =
                    BeanConvertor.getKidsForUI(focusKidsInfo);

            mActivityMain.mWatchContactStack.push(watchKidsInfo);

            selectFragment(ProfileKidsEditorFragment.class.getName(), null, true);
        }

    }

    //二期修改为启动界面，输入新密码
    @OnClick(R.id.profile_option_change_password)
    public void resetPsw() {

        selectFragment(ProfileResetPswFragment.class.getName(), null, true);

        /*DB_User currentLoginUserInfo = LoginManager.getCurrentLoginUserInfo();
        String email = "";
        if (currentLoginUserInfo != null) {
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
*/
    }

    @OnClick(R.id.profile_option_language)
    public void startLanguageFragment() {
        selectFragment(ProfileLanguageFragment.newInstance(), true);
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

    private BroadcastReceiver UIChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("UIChangeReceiver", "change broadcast");

            if (intent == null) {
                return;
            }

            int update_type = intent.getIntExtra(MainFrameActivity.Tag_Key, -1);
            if (update_type == MainFrameActivity.TAG_FIRMwARE_UPDATE) {
                if (intent.hasExtra(MainFrameActivity.TAG_UPDATE)) {
                    tv_watch_update_red_point.setVisibility(View.VISIBLE);
                } else {
                    tv_watch_update_red_point.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    private void registerUIReceiver() {
        if (SwingApplication.localBroadcastManager != null) {
            IntentFilter intentFilter = new IntentFilter(MainFrameActivity.UI_Update_Action);
            SwingApplication.localBroadcastManager.registerReceiver(UIChangeReceiver,
                    intentFilter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SwingApplication.localBroadcastManager.unregisterReceiver(UIChangeReceiver);
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        Activity activity = getActivity();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(activity, deniedPermissions, REQUEST_PERMISSIONS_CODE);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                isReadStorageGranted = true;
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                isWriteStorageGranted = true;
                break;
        }
        if (isReadStorageGranted && isWriteStorageGranted) {
            checkWatchUpdate();
        }
    }

    private void checkWatchUpdate() {
        String firmwareMacId = DeviceManager.getFirmwareMacId();
        String firmwareVersion = DeviceManager.getFirmwareVersion();
        if (TextUtils.isEmpty(firmwareMacId) || TextUtils.isEmpty(firmwareVersion)) {
            return;
        }
        new DeviceManager().checkFirmwareUpdate(firmwareMacId, firmwareVersion, this, true);
    }

}
