package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kidsdynamic.commonlib.utils.SoftKeyBoardUtil;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.avatar.AvatarApi;
import com.kidsdynamic.data.net.avatar.PartUtils;
import com.kidsdynamic.data.net.kids.KidsApi;
import com.kidsdynamic.data.net.kids.model.KidsAddRequest;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.user.model.UpdateKidRepEntity;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.domain.ConfigManager;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.BottomPopWindow;
import com.kidsdynamic.swing.view.CropImageView;
import com.kidsdynamic.swing.view.CropPopWindow;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;
import com.yy.base.utils.ViewUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * WatchProfileFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchProfileFragment extends BaseFragment {

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;

    private static final String MAC_ID = "mac_id";
    private static final String FIRMWARE_VERSION = "firmware_Version";

    @BindView(R.id.watch_profile_photo)
    ViewCircle vc_photo;
    @BindView(R.id.watch_profile_first)
    EditText et_first;
    @BindView(R.id.watch_profile_last)
    EditText et_last;
//    @BindView(R.id.watch_profile_zip)
//    EditText et_zip;

    private File profile;

    public static WatchProfileFragment newInstance(String macId, String firmwareVersion) {
        Bundle args = new Bundle();
        args.putString(MAC_ID, macId);
        args.putString(FIRMWARE_VERSION, firmwareVersion);
        WatchProfileFragment fragment = new WatchProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_profile, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et_first.setTypeface(SwingFontsCache.getNormalType(getContext()));
        et_last.setTypeface(SwingFontsCache.getNormalType(getContext()));
//        et_zip.setTypeface(SwingFontsCache.getNormalType(getContext()));
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

        FragmentActivity activity = getActivity();
        if (activity instanceof MainFrameActivity) {
            activity.getSupportFragmentManager().popBackStack();
        } else {
            SignupActivity signupActivity = (SignupActivity) getActivity();
//        signupActivity.setFragment(SignupLoginFragment.newInstance());
            signupActivity.getSupportFragmentManager().popBackStack();
        }

    }

    @OnClick(R.id.watch_profile_photo)
    public void addPhoto() {
        SoftKeyBoardUtil.hideSoftKeyboard(getActivity());
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

        Point navigationBarSize = ViewUtils.getNavigationBarSize(getContext());
        bottomPopWindow.showAtLocation(getView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                0, navigationBarSize.y);
    }

    @OnClick(R.id.watch_profile_submit)
    public void doSubmit() {
        //del 2017年11月4日11:32:47 only_app 头像非必填字段
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
        Bundle args = getArguments();
        String macId = args.getString(MAC_ID);
        String firmwareVersion = args.getString(FIRMWARE_VERSION);
        addKids(macId, firmwareVersion, profile, String.format("%1$s %2$s", firstName, lastName));
    }

    /**
     * add kids
     *
     * @param watchMacId String
     * @param kidsName   String
     */
    public void addKids(String watchMacId, final String firmwareVersion, final File profile, final String kidsName) {
        showLoadingDialog(R.string.watch_profile_wait);
        KidsApi kidsApi = ApiGen
                .getInstance(getContext().getApplicationContext())
                .generateApi(KidsApi.class, true);

        KidsAddRequest kidsAddRequest = new KidsAddRequest();
        kidsAddRequest.setMacId(watchMacId);
        kidsAddRequest.setName(kidsName);

        kidsApi.addKid(kidsAddRequest).enqueue(new BaseRetrofitCallback<KidsWithParent>() {
            @Override
            public void onResponse(Call<KidsWithParent> call, Response<KidsWithParent> response) {
                LogUtil2.getUtils().d("addKid onResponse: " + response.code());

                if (response.code() == 200) {
                    //add successfully
                    KidsWithParent kidsWithParent = response.body();
                    int kidId = kidsWithParent.getId();
                    LogUtil2.getUtils().d("addKid rep kid ID: " + kidId);

                    //如果当前没有focusKids，则设置新增的为
                    if (DeviceManager.getFocusKidsId() <= 0) {
                        DeviceManager.updateFocusKids(kidId);
                    }

                    kidsWithParent.setFirmwareVersion(firmwareVersion);
                    //add 2017年11月7日13:45:21 only_app save kids info to db
                    new DeviceManager().saveKidsData(getContext(), kidsWithParent);

                    //更新firmwareVersion
                    DeviceManager.updateEventKidsFirmwareVersion(kidsWithParent.getMacId(),firmwareVersion);
                    //上报服务器
                    new DeviceManager().uploadFirmwareVersion(kidsWithParent.getMacId(),firmwareVersion);

                    if (profile != null) {
                        uploadAvatar(profile, String.valueOf(kidId));
                    } else {

                        finishLoadingDialog();

                        Bundle bundle = new Bundle();
                        bundle.putString(DeviceManager.BUNDLE_KEY_KID_NAME,
                                kidsName);

                        gotoAddSuccessFragment(bundle);
                    }

                } else if (response.code() == 409) {
                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(), R.string.error_api_kid_add_409);

                } else {
                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(), R.string.error_api_unknown);

                    LogUtil2.getUtils().d("addKid error code:" + response.code());

                    //test
                    /*SignupActivity signupActivity = (SignupActivity) getActivity();
                    signupActivity.setFragment(DashboardMainFragment.newInstance());*/
                }

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<KidsWithParent> call, Throwable t) {
                super.onFailure(call, t);

                finishLoadingDialog();
            }
        });
    }

    /**
     * upload kid's avatar
     *
     * @param profile File
     * @param kidsId  String
     */
    public void uploadAvatar(final File profile, final String kidsId) {
        final AvatarApi avatarApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi4Avatar(AvatarApi.class);
        MultipartBody.Part filePart =
                PartUtils.prepareFilePart("upload", profile.getName(), profile);
        avatarApi
                .uploadKidAvatar(getUploadKidAvatarPram(kidsId), filePart)
                .enqueue(new Callback<UpdateKidRepEntity>() {
                    @Override
                    public void onResponse(Call<UpdateKidRepEntity> call,
                                           Response<UpdateKidRepEntity> response) {
                        LogUtil2.getUtils().d("uploadKidAvatar onResponse");

                        finishLoadingDialog();

                        //code == 200 upload ok
                        int code = response.code();
                        if (200 == code) {//上传头像成功后，跳转界面
                            Bundle bundle = new Bundle();
                            bundle.putString(DeviceManager.BUNDLE_KEY_KID_NAME,
                                    response.body().getKid().getName());
                            bundle.putString(DeviceManager.BUNDLE_KEY_AVATAR,
                                    UserManager.getProfileRealUri(response.body().getKid().getProfile()));

                            gotoAddSuccessFragment(bundle);

                            //发送头像更新消息
                            sendKidsAvatarUpdate(profile,Long.valueOf(kidsId));
                        } else {
                            ToastCommon.makeText(getContext(), R.string.error_api_unknown);
                            LogUtil2.getUtils().d("uploadKidAvatar error code:" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateKidRepEntity> call, Throwable t) {
                        finishLoadingDialog();
                        LogUtil2.getUtils().d("uploadKidAvatar onFailure");
                        t.printStackTrace();
                    }
                });
    }

    private void sendKidsAvatarUpdate(File avatarFile, long kidsId) {

        if(avatarFile == null){
            return;
        }

        Intent intent = new Intent(ConfigManager.Avatar_Update_Action);
        intent.putExtra(ConfigManager.Tag_Key,ConfigManager.Tag_Update_Type_Kids_Avatar);
        intent.putExtra(ConfigManager.Tag_KidsId_Key,kidsId);

        /*Uri uriForFile = FileProvider.getUriForFile(getContext().getApplicationContext(),
                getContext().getPackageName(), avatarFile);*/
        intent.putExtra(ConfigManager.Tag_Avatar_File_Uri_Key,avatarFile.getAbsolutePath());

        SwingApplication.localBroadcastManager.sendBroadcast(intent);
    }

    private void gotoAddSuccessFragment(Bundle bundle) {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainFrameActivity) {
            ((MainFrameActivity) activity).
                    selectFragment(WatchAddSuccessFragment.class.getName(), bundle, true);
        } else {
            SignupActivity signupActivity = (SignupActivity) getActivity();
            signupActivity.selectFragment(WatchAddSuccessFragment.class.getName(), bundle);
        }
    }

    private Map<String, RequestBody> getUploadKidAvatarPram(String kidsId) {
        HashMap<String, RequestBody> paramMap = new HashMap<>();
        PartUtils.putRequestBodyMap(paramMap, AvatarApi.param_kidId, kidsId);
        return paramMap;
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
                profile = file;
            }
        });
    }

}
