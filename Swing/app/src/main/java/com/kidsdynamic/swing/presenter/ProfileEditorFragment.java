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
import com.kidsdynamic.data.net.user.UserApiNoNeedToken;
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
 * ProfileEditorFragment
 */

public class ProfileEditorFragment extends ProfileBaseFragment {
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;

    private MainFrameActivity mActivityMain;
    private View mViewMain;

    @BindView(R.id.profile_editor_photo)
    protected ViewCircle mViewPhoto;
    @BindView(R.id.profile_editor_first)
    protected EditText mViewFirst;
    @BindView(R.id.profile_editor_last)
    protected EditText mViewLast;
    @BindView(R.id.profile_editor_phone)
    protected EditText mViewPhone;
    @BindView(R.id.profile_editor_mail)
    protected EditText mViewMail;
    @BindView(R.id.profile_editor_zip)
    protected EditText mViewZip;

    @BindView(R.id.btn_update_profile)
    protected Button btn_save;


    private Bitmap mUserAvatar = null;
    private String mUserAvatarFileName = null;
//    private String mUserAvatarFilename = null;
    private boolean mUserAvatarChanged = false;

    private Dialog processDialog = null;

    private DB_User mProfileInfo;
    private File avatarFile;

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
        mViewMain = inflater.inflate(R.layout.fragment_profile_editor, container, false);

        ButterKnife.bind(this,mViewMain);

//        mViewPhoto.setOnClickListener(mPhotoListener);

        btn_save.setTypeface(SwingFontsCache.getBoldType(getContext()));
        com.kidsdynamic.swing.utils.ViewUtils.setTextViewBoldTypeFace(getContext(),
                mViewFirst,mViewLast,mViewPhone,mViewMail,mViewZip);

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
        tv_title.setText(R.string.profile_option_edit);
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

    public void onToolbarAction2() {
        processDialog = ProgressDialog.show(mActivityMain,
                getResources().getString(R.string.profile_editor_processing),
                getResources().getString(R.string.profile_editor_wait), true);
//        profileSave();
    }

    @Override
    public void onResume() {
        super.onResume();

        //如果用户新选择了头像

    }

    private void initValue() {
        mUserAvatar = null;
        mUserAvatarChanged = false;

        mProfileInfo = LoginManager.getCurrentLoginUserInfo();

        if(mProfileInfo == null){
            ToastCommon.showToast(getContext(),"user info null");
            onTopLeftBtnClick();
            return;
        }else {
            profileLoad();
        }

        if (mUserAvatar != null){
            loadAvatar();
        }
    }

    private void loadAvatar() {
        if(!TextUtils.isEmpty(mUserAvatarFileName)){
            GlideHelper.getBitMap(getContext(), UserManager.getProfileRealUri(mUserAvatarFileName),
                    String.valueOf(mProfileInfo.getLastUpdate()),new AvatarSimpleTarget(mViewPhoto));
        }
    }

    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            mViewPhoto.setBitmap(bitmap);
        }
    };

    @Override
    public void onPause() {
        if (processDialog != null)
            processDialog.dismiss();

        super.onPause();

        InputMethodManager inputMethodManager = (InputMethodManager) mActivityMain
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);
    }

    private void profileLoad() {
        mViewFirst.setText(mProfileInfo.getFirstName());
        mViewLast.setText(mProfileInfo.getLastName());
        mViewPhone.setText(mProfileInfo.getPhoneNum());
        mViewMail.setText(mProfileInfo.getEmail());
        mViewZip.setText(mProfileInfo.getZipCode());
        mUserAvatarFileName = mProfileInfo.getProfile();


        //加载现有头像
        if(!TextUtils.isEmpty(mUserAvatarFileName)){
            GlideHelper.getBitMap(getContext(), UserManager.getProfileRealUri(mUserAvatarFileName),
                    String.valueOf(mProfileInfo.getLastUpdate()),new AvatarSimpleTarget(mViewPhoto));
        }
    }

    @OnClick(R.id.btn_update_profile)
    public void updateProfile(){
        SoftKeyBoardUtil.hideSoftKeyboard(getActivity());

        String first = mViewFirst.getText().toString().trim();
        String last = mViewLast.getText().toString().trim();
        String phone = mViewPhone.getText().toString().trim();
        String zip = mViewZip.getText().toString().trim();

        if (!first.equals(mProfileInfo.getFirstName()) ||
                !last.equals(mProfileInfo.getLastName()) ||
                !phone.equals(mProfileInfo.getPhoneNum()) ||
                !zip.equals(mProfileInfo.getZipCode())) {
            //如果内容有变化

            UpdateProfileEntity updateProfileEntity = new UpdateProfileEntity();
            updateProfileEntity.setFirstName(first);
            updateProfileEntity.setLastName(last);
            updateProfileEntity.setPhoneNumber(phone);
            updateProfileEntity.setZipCode(zip);

            saveProfileChange(updateProfileEntity);
        } else if (mUserAvatar != null && mUserAvatarChanged) {

            showLoadingDialog(R.string.signup_profile_wait);
            //更新头像，如果变更
            updateUserAvatar();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void saveProfileChange(UpdateProfileEntity updateProfileEntity){
        showLoadingDialog(R.string.signup_profile_wait);

        final UserApiNoNeedToken userApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi(UserApiNoNeedToken.class, true);

        userApi.updateProfile(updateProfileEntity).enqueue(new BaseRetrofitCallback<UserInfo>() {

            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                //code == 200 update ok
                int code = response.code();
                Log.d("profile","update code:" + code);
                if(code == 200){
                    //更新本地数据库
                    UserManager.updateProfile2DB(response.body());

                    if(mUserAvatar != null && mUserAvatarChanged){
                        //更新头像，如果变更
                        updateUserAvatar();
                    }else {
                        finishLoadingDialog();
                        getFragmentManager().popBackStack();
                    }

                }else {
                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(), R.string.profile_editor_avatar_failed);
                }

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

                ToastCommon.makeText(getContext(), R.string.profile_editor_avatar_failed);

                super.onFailure(call, t);
            }
        });
    }

    private void updateUserAvatar() {

        final AvatarApi avatarApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi4Avatar(AvatarApi.class);
        MultipartBody.Part filePart =
                PartUtils.prepareFilePart("upload", avatarFile.getName(), avatarFile);
        avatarApi.uploadUserAvatar(filePart).enqueue(new BaseRetrofitCallback<UpdateProfileSuccess>() {
            @Override
            public void onResponse(Call<UpdateProfileSuccess> call, Response<UpdateProfileSuccess> response) {
                LogUtil2.getUtils().d("uploadUserAvatar onResponse");

                //code == 200 upload ok
                int code = response.code();
                //头像上传成功后
                if(code == 200){

                    uploadAvatarOK(response);

                }else {
                    finishLoadingDialog();
                    ToastCommon.makeText(getContext(),R.string.profile_editor_avatar_failed);
                }


                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UpdateProfileSuccess> call, Throwable t) {
                finishLoadingDialog();

                super.onFailure(call, t);
                t.printStackTrace();

            }
        });
    }

    private void uploadAvatarOK(Response<UpdateProfileSuccess> response) {
        if(response.body() != null){
            //更新本地数据库
            UserManager.updateProfile2DB(response.body().getUser());

            WatchContact watchContact = new WatchContact();
            watchContact.mPhoto = mUserAvatar;
            watchContact.mLabel = "editProfile";

            mActivityMain.mWatchContactStack.push(watchContact);

            ToastCommon.makeText(getContext(), R.string.profile_editor_save_success);
            finishLoadingDialog();
            getFragmentManager().popBackStack();
        }else {
            finishLoadingDialog();
            ToastCommon.makeText(getContext(),R.string.profile_editor_avatar_failed);
        }
    }

    @OnClick(R.id.profile_editor_photo)
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
     * Start virtual album
     */
    private void startAlbumActivity() {
        Intent intent = new Intent();
        intent.setClass(getContext(), VirtualAlbumActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
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
                mViewPhoto.setStrokeWidth(4.0f);
                mViewPhoto.setCrossWidth(0.0f);
                mViewPhoto.setBitmap(result.bitmap);

                mUserAvatar = result.bitmap;
                avatarFile = file;

                mUserAvatarChanged = true;
            }
        });
    }

}
