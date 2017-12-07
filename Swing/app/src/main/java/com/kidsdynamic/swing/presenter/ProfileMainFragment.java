package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.view.ViewCircle;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ProfileMainFragment <br/><br/>
 *
 * 2017年11月20日21:40:52 only_app
 *
 */

public class ProfileMainFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;
    private View mViewMain;
    private ViewCircle mViewPhoto;
    private ViewCircle mViewDeviceAdd;
    private ViewCircle mViewRequestToAdd;
    private TextView mViewName;
    private TextView mViewMail;
    private TextView mViewRequestFromTitle;
    private LinearLayout mViewDeviceContainer;
    private LinearLayout mViewSharedContainer;
    private LinearLayout mViewRequestToContainer;
    private LinearLayout mViewRequestFromContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (MainFrameActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_profile_main, container, false);

        mViewPhoto =  mViewMain.findViewById(R.id.profile_main_photo);
        mViewName =  mViewMain.findViewById(R.id.profile_main_name);
        mViewMail =  mViewMain.findViewById(R.id.profile_main_mail);

        mViewDeviceContainer =  mViewMain.findViewById(R.id.profile_main_device_container);
        mViewDeviceAdd =  mViewMain.findViewById(R.id.profile_main_device_add);
        mViewDeviceAdd.setOnClickListener(mAddDeviceListener);

        mViewSharedContainer = mViewMain.findViewById(R.id.profile_main_shared_container);

        mViewRequestToContainer =  mViewMain.findViewById(R.id.profile_main_request_to_container);
        mViewRequestFromTitle = mViewMain.findViewById(R.id.profile_main_request_from_title);
        mViewRequestToAdd =  mViewMain.findViewById(R.id.profile_main_request_to_add);
        mViewRequestToAdd.setOnClickListener(mAddRequestToListener);

        mViewRequestFromContainer = mViewMain.findViewById(R.id.profile_main_request_from_container);

        ButterKnife.bind(this,mViewMain);

        initTitleBar();
        return mViewMain;
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_profile);
        view_left_action.setImageResource(R.drawable.icon_edit);

        view_right_action.setImageResource(R.drawable.icon_settings);
    }

    @Override
    public void onResume() {
        super.onResume();

        delAllContact(mViewDeviceContainer);
        delAllContact(mViewSharedContainer);
        delAllContact(mViewRequestToContainer);
        delAllContact(mViewRequestFromContainer);

        DB_User parent = LoginManager.getCurrentLoginUserInfo();

        if (parent != null) {
            mViewName.setText(LoginManager.getUserName(parent));
            mViewMail.setText(parent.getEmail());
        }

        WatchContact watchContact = null;
        if(!mActivityMain.mWatchContactStack.isEmpty()){
            watchContact = mActivityMain.mWatchContactStack.pop();
        }


        if (watchContact == null && parent != null && !TextUtils.isEmpty(parent.getProfile())) {
            GlideHelper.getBitMap(getContext(), UserManager.getProfileRealUri(parent.getProfile()),
                    String.valueOf(parent.getLastUpdate()), userAvatarSimpleTarget);
        }

        if(watchContact != null && watchContact.mPhoto != null && parent != null){
            mViewPhoto.setBitmap(watchContact.mPhoto);
            GlideHelper.getBitMap(getContext(), UserManager.getProfileRealUri(parent.getProfile()),
                    String.valueOf(parent.getLastUpdate()),
                    userAvatarSimpleTarget);
        }

        // 載入用戶的所有手錶
        if(parent != null){
            List<WatchContact.Kid> kidsForUI = DeviceManager.getKidsForUI(getContext(), parent.getUserId());
            for (WatchContact device : kidsForUI)
                addContact(mViewDeviceContainer, device, mContactListener);
        }


        // TODO: 2017/11/20 二期
        // 載入所有其它用戶與當前用戶分享的手錶
        /*for (WatchContact device : mActivityMain.mOperator.getSharedList())
            addContact(mViewSharedContainer, device, mContactListener);

        // 載入用戶發出需求的用戶
        for (WatchContact user : mActivityMain.mOperator.getRequestToList()) {
            if (((WatchContact.User) user).mRequestStatus.equals(WatchContact.User.STATUS_PENDING))
                addContact(mViewRequestToContainer, user, null);
        }

        // 載入所用向用戶發出請求的用戶
        for (WatchContact user : mActivityMain.mOperator.getRequestFromList()) {
            //if (((WatchContact.User) user).mRequestStatus.equals("WatchContact.User.STATUS_PENDING"))
            addContact(mViewRequestFromContainer, user, mContactListener);
        }*/

        updateRequestFromTitle();

        //当前focus的kids
//        focusContact(mActivityMain.mOperator.getFocusKid(), true);
    }

    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewPhoto.setBitmap(bitmap);
            }
        }
    };


    private class AvatarSimpleTarget extends SimpleTarget<Bitmap>{

        ViewCircle viewCircle;

        public AvatarSimpleTarget(ViewCircle viewCircle){
            this.viewCircle = viewCircle;
        }

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            if(viewCircle != null && getActivity() != null && !getActivity().isDestroyed()){
                viewCircle.setBitmap(bitmap);
            }
        }
    }

   /* @Override
    public ViewFragmentConfig getConfig() {
        return new ViewFragmentConfig(
                getResources().getString(R.string.title_profile), true, true, false,
                ActivityMain.RESOURCE_IGNORE, R.mipmap.icon_edit, R.mipmap.icon_settings);
    }*/

   @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        //修改个人信息界面
        selectFragment(ProfileEditorFragment.class.getName(), null,true);
    }

    @OnClick(R.id.main_toolbar_action2)
    public void onToolbarAction2() {
       //option 界面
        selectFragment(ProfileOptionFragment.class.getName(),null,true);
    }

    public void onToolbarTitle() {
    }

    private View.OnClickListener mAddDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentProfileSearch.class.getName(), null);
        }
    };

    private View.OnClickListener mAddRequestToListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentProfileRequestTo.class.getName(), null);
        }
    };

    private View.OnClickListener mContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewCircle viewCircle = (ViewCircle) view;
            ViewParent viewContainer = view.getParent();
            WatchContact contact = (WatchContact) viewCircle.getTag();

            if (viewContainer == mViewDeviceContainer) {

                //一期暂时没跳转
//                showKidsProfile(contact);

            } else if (viewContainer == mViewSharedContainer) {
                focusContact(contact, false);

            } else if (viewContainer == mViewRequestToContainer) {

            }/* else if (viewContainer == mViewRequestFromContainer) {
                mActivityMain.mContactStack.push(contact);
                mActivityMain.selectFragment(FragmentProfileRequestFrom.class.getName(), null);
            }*/
        }
    };

    private void showKidsProfile(WatchContact contact){

        // TODO: 2017/12/2 test request
        /*if(contact instanceof WatchContact.Kid){
            WatchContact.Kid watchKidsInfo = (WatchContact.Kid) contact;
            selectFragment(ProfileKidsInfoFragment.newInstance(watchKidsInfo.mId),true);
        }*/

        //下面的挑战为测试代码
//        selectFragment(ProfileRequestToKidsInfoFragment.newInstance(1),true);
//        selectFragment(ProfileRequestFromKidsInfoFragment.newInstance(1),true);

        if(contact instanceof WatchContact.Kid){
            WatchContact.Kid watchKidsInfo = (WatchContact.Kid) contact;
//            selectFragment(ProfileRemoveKidsConfirmFragment.newInstance(watchKidsInfo.mId),true);
//            selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(watchKidsInfo.mId),true);
            selectFragment(ProfileSearchUserFragment.class.getName(),null,true);
        }

    }

    private void addContact(LinearLayout layout, WatchContact contact, View.OnClickListener listener) {

        layout.removeAllViews();

        ViewCircle photo = new ViewCircle(mActivityMain);
        photo.setBitmap(contact.mPhoto);

        if(contact instanceof WatchContact.Kid){
            WatchContact.Kid kid = (WatchContact.Kid) contact;
            GlideHelper.getBitMap(getContext().getApplicationContext(),
                    UserManager.getProfileRealUri(kid.mProfile),
                    kid.mLastUpdate+"",new AvatarSimpleTarget(photo));
        }

        photo.setStrokeCount(12);
        photo.setStrokeBeginEnd(12, -1);
        photo.setStrokeType(ViewCircle.STROKE_TYPE_ARC);
        photo.setStrokeColorActive(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        photo.setStrokeColorNormal(ContextCompat.getColor(mActivityMain, R.color.color_white));
        photo.setTag(contact);

        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);

        layout.addView(photo, 0, layoutParams);
        photo.setOnClickListener(listener);
    }

    private void delContact(LinearLayout layout, WatchContact contact) {
        int count = layout.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View child = layout.getChildAt(idx);
            WatchContact someone = (WatchContact) child.getTag();
            if (someone == contact) {
                layout.removeViewAt(idx);
                break;
            }
        }
    }

    private void delAllContact(LinearLayout layout) {
        int remain = layout == mViewDeviceContainer || layout == mViewRequestToContainer ? 1 : 0;

        while (layout.getChildCount() > remain)
            layout.removeViewAt(layout.getChildCount() - 1);
    }

    private void focusContact(WatchContact contact, boolean onCreate) {
        int count;

        count = mViewDeviceContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewCircle viewCircle = (ViewCircle) mViewDeviceContainer.getChildAt(idx);
            WatchContact.Kid kid1 = (WatchContact.Kid) contact;
            WatchContact.Kid kid2 = (WatchContact.Kid) viewCircle.getTag();

            boolean focus = kid1 != null && kid2 != null && kid1.mId == kid2.mId && kid1.mUserId == kid2.mUserId;
            viewCircle.setActive(focus);
        }

        count = mViewSharedContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewCircle viewCircle = (ViewCircle) mViewSharedContainer.getChildAt(idx);
            WatchContact.Kid kid1 = (WatchContact.Kid) contact;
            WatchContact.Kid kid2 = (WatchContact.Kid) viewCircle.getTag();
            boolean focus = (kid1 != null && kid2 != null && kid1.mId == kid2.mId && kid1.mUserId == kid2.mUserId);
            viewCircle.setActive(focus);
        }

        //设置focus kids； 获取头像，
        /*if (!onCreate){
            mActivityMain.mOperator.setFocusKid((WatchContact.Kid) contact);
        }

        mActivityMain.updateFocusAvatar();*/
    }

    private void updateRequestFromTitle() {
        int count = mViewRequestFromContainer.getChildCount();

        String string = String.format(Locale.getDefault(),
                getResources().getString(R.string.profile_main_request_from), count);
        mViewRequestFromTitle.setText(string);
    }
}
