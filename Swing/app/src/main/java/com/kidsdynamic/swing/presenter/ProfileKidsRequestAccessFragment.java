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
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.commonlib.utils.SoftKeyBoardUtil;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.yy.base.utils.ToastCommon;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ProfileKidsRequestAccessFragment
 */

public class ProfileKidsRequestAccessFragment extends ProfileBaseFragment {
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;

    private MainFrameActivity mActivityMain;
    private View mViewMain;

    @BindView(R.id.btn_search_again)
    protected Button btn_search_again;

    @BindView(R.id.btn_cancel)
    protected Button btn_cancel;

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
        mViewMain = inflater.inflate(R.layout.fragment_kids_request_access, container, false);

        ButterKnife.bind(this,mViewMain);

//        mViewPhoto.setOnClickListener(mPhotoListener);

        btn_search_again.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_cancel.setTypeface(SwingFontsCache.getBoldType(getContext()));

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
        getFragmentManager().popBackStack();
    }

    public void onToolbarAction2() {
        processDialog = ProgressDialog.show(mActivityMain,
                getResources().getString(R.string.profile_editor_processing),
                getResources().getString(R.string.profile_editor_wait), true);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initValue() {
        mUserAvatar = null;
        mUserAvatarChanged = false;

        if(!mActivityMain.mWatchContactStack.isEmpty()){
            watchContact = mActivityMain.mWatchContactStack.pop();

            if(watchContact instanceof WatchContact.User){
                watchUserInfo = (WatchContact.User) watchContact;
            }

        }

        if(watchUserInfo == null){
            ToastCommon.showToast(getContext(),"user info null");
            onTopLeftBtnClick();
        }else {
            profileLoad();
        }
    }

    private void profileLoad(){
        user_name.setText(LoginManager.getUserName(watchUserInfo.mFirstName,
                watchUserInfo.mLastName));

        if(!TextUtils.isEmpty(watchUserInfo.mProfile)){
            GlideHelper.getCircleImageViewOnlyCacheInMemory(getContext(),
                    UserManager.getProfileRealUri(watchUserInfo.mProfile),
                    user_avatar);
        }

        layout_user_info.setVisibility(View.VISIBLE);

        img_add.setImageResource(R.drawable.icon_done);

        loadAvatar();
    }

    private void loadAvatar() {
        mUserAvatarFileName =  watchUserInfo.mProfile;
        if(!TextUtils.isEmpty(mUserAvatarFileName)){
            GlideHelper.getCircleImageViewOnlyCacheInMemory(getContext(),
                    UserManager.getProfileRealUri(mUserAvatarFileName),
                    user_avatar);
        }
    }


    @Override
    public void onPause() {
        if (processDialog != null)
            processDialog.dismiss();

        super.onPause();

        InputMethodManager inputMethodManager = (InputMethodManager) mActivityMain
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);
    }


    @OnClick(R.id.btn_search_again)
    public void onSearchAgain(){
        SoftKeyBoardUtil.hideSoftKeyboard(getActivity());

        getFragmentManager().popBackStack();
        selectFragment(ProfileSearchUserFragment.class.getName(),null,true);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel(){
       getFragmentManager().popBackStack();
    }


}
