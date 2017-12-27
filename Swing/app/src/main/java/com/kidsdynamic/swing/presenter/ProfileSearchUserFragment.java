package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.commonlib.utils.SoftKeyBoardUtil;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.AddSubHost;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.yy.base.utils.Functions;
import com.yy.base.utils.ToastCommon;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * ProfileSearchUserFragment
 */

public class ProfileSearchUserFragment extends ProfileBaseFragment {
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;

    private MainFrameActivity mActivityMain;
    private View mViewMain;

    @BindView(R.id.et_email)
    protected EditText mViewUserEmail;

    @BindView(R.id.btn_search)
    protected Button btn_search;

    @BindView(R.id.iv_head)
    protected ImageView user_avatar;
    @BindView(R.id.tv_content)
    protected TextView user_name;
    @BindView(R.id.iv_action)
    protected ImageView img_add;

    @BindView(R.id.layout_user_info)
    protected View layout_user_info;

    private Bitmap mUserAvatar = null;
    private String mUserAvatarFileName = null;
//    private String mUserAvatarFilename = null;
    private boolean mUserAvatarChanged = false;

    private Dialog processDialog = null;

//    private DB_User mProfileInfo;
    private WatchContact watchContact = null;
    private WatchContact.User watchUserInfo = null;
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
        mViewMain = inflater.inflate(R.layout.fragment_search_user, container, false);

        ButterKnife.bind(this,mViewMain);

//        mViewPhoto.setOnClickListener(mPhotoListener);

        mViewUserEmail.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_search.setTypeface(SwingFontsCache.getBoldType(getContext()));
        initTitleBar();

        return mViewMain;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initValue();
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_request_access);
        view_left_action.setImageResource(R.drawable.icon_left);

        view_right_action.setImageResource(R.drawable.icon_delete);
        view_right_action.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void onToolbarAction2() {
        processDialog = ProgressDialog.show(mActivityMain,
                getResources().getString(R.string.profile_editor_processing),
                getResources().getString(R.string.profile_editor_wait), true);
    }

    @Override
    public void onResume() {
        super.onResume();

        //如果用户新选择了头像

    }

    private void initValue() {
        /*mUserAvatar = null;
        mUserAvatarChanged = false;

        if(!mActivityMain.mWatchContactStack.isEmpty()){
            watchContact = mActivityMain.mWatchContactStack.pop();
            
            if(watchContact instanceof WatchContact.Kid){
                watchKidsInfo = (WatchContact.Kid) watchContact;
            }
            
        }

        if(watchKidsInfo == null){
            ToastCommon.showToast(getContext(),"kids info null");
            onTopLeftBtnClick();
            return;
        }else {
            profileLoad();
        }

        if (mUserAvatar != null){
            loadAvatar();
        }*/
    }

    private void loadAvatar() {
        if(!TextUtils.isEmpty(mUserAvatarFileName)){
            GlideHelper.getCircleImageViewOnlyCacheInMemory(getContext(),
                    UserManager.getProfileRealUri(mUserAvatarFileName),
                    user_avatar);
        }
    }

/*    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            mViewPhoto.setBitmap(bitmap);
        }
    };*/

    @Override
    public void onPause() {
        if (processDialog != null)
            processDialog.dismiss();

        super.onPause();

        InputMethodManager inputMethodManager = (InputMethodManager) mActivityMain
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);
    }


    @OnClick(R.id.btn_search)
    public void onSearch(){
        SoftKeyBoardUtil.hideSoftKeyboard(getActivity());

        String userEmail = mViewUserEmail.getText().toString().trim();
        if(!Functions.isValidEmail(userEmail)){
            ToastCommon.makeText(getContext(),R.string.error_email_null);
            return;
        }

        //检查是否在搜索自己
        DB_User currentLoginUserInfo = LoginManager.getCurrentLoginUserInfo();
        if(currentLoginUserInfo != null
                && userEmail.equals(currentLoginUserInfo.getEmail())){
            ToastCommon.makeText(getContext(),R.string.you_on_this_account_now);
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);

        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class, false);


        userApiNeedToken.findByEmail(userEmail).enqueue(new BaseRetrofitCallback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {

                if(response.code() == 200){
                    showSearchedUserInfo(response.body());
                }else if(response.code() == 400){
                    ToastCommon.makeText(getContext(),R.string.error_api_user_find_by_email_404);
                }else {
                    ToastCommon.makeText(getContext(),R.string.normal_err, response.code());
                }

                finishLoadingDialog();

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                super.onFailure(call, t);

                finishLoadingDialog();
            }
        });

    }

    private void showSearchedUserInfo(UserInfo userInfo) {
        if(userInfo == null){
            ToastCommon.makeText(getContext(),R.string.error_api_unknown);
            return;
        }

        user_name.setText(LoginManager.getUserName(userInfo.getFirstName(),
                userInfo.getLastName()));

        if(!TextUtils.isEmpty(userInfo.getProfile())){
            GlideHelper.getCircleImageViewOnlyCacheInMemory(getContext(),
                    UserManager.getProfileRealUri(userInfo.getProfile()),
                    user_avatar);
        }

        img_add.setImageResource(R.drawable.ic_icon_add_light_blue);

        watchUserInfo = BeanConvertor.getWatchContact(userInfo);

        layout_user_info.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.iv_action)
    protected void onAddClick(){
        if(watchUserInfo == null){
            ToastCommon.makeText(getContext(),R.string.error_api_unknown);
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);

        //请求加入
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);

        AddSubHost subHostId = new AddSubHost();
        subHostId.setHostId(watchUserInfo.mId);
        hostApi.subHostAdd(subHostId).enqueue(new BaseRetrofitCallback<RequestAddSubHostEntity>() {
            @Override
            public void onResponse(Call<RequestAddSubHostEntity> call,
                                   Response<RequestAddSubHostEntity> response) {
                int code = response.code();
                if(code == 200){
                    mActivityMain.mWatchContactStack.push(watchUserInfo);

                    selectFragment(ProfileKidsRequestAccessFragment.class.getName(),null,
                            false);
                }else if(code == 409){

                    //test
                    /*mActivityMain.mWatchContactStack.push(watchUserInfo);

                    selectFragment(ProfileKidsRequestAccessFragment.class.getName(),null,
                            false);*/

                    //请求已经存在
                    ToastCommon.makeText(getContext(),R.string.error_subhost_add_409);
                }else {
                    ToastCommon.makeText(getContext(),R.string.normal_err,code);
                }

                finishLoadingDialog();

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<RequestAddSubHostEntity> call, Throwable t) {
                finishLoadingDialog();

                super.onFailure(call, t);
            }
        });


    }


}
