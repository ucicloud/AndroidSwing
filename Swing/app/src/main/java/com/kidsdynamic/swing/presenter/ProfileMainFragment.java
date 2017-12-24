package com.kidsdynamic.swing.presenter;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRequests;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.domain.datasource.RemoteDataSource;
import com.kidsdynamic.swing.domain.viewmodel.SubHostListModel;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewCircle;
import com.kidsdynamic.swing.utils.ViewUtils;

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

    private SubHostListModel subHostListModel = null;
    private boolean isLoadSubHost = false;
    private SubHostRequests mSubHostRequests;

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
        mViewMail.setTypeface(SwingFontsCache.getNormalType(getContext()));

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


        ViewUtils.setTextViewBoldTypeFace(getContext(),mViewName);

        ViewUtils.setTextViewBoldTypeFace(getContext(),mViewRequestFromTitle,
                (TextView) mViewMain.findViewById(R.id.tv_your_pending),
                (TextView) mViewMain.findViewById(R.id.tv_devices_shared_with_me),
                (TextView) mViewMain.findViewById(R.id.tv_my_devices));

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSubHostRequests = DeviceManager.getSubHostRequestsInCache();

//        subscribeUI();
    }

    private void subscribeUI() {
        Log.w("profile","subscribeUI");

        if (!isAdded()) {
            return;
        }


        SwingApplication appInstance = (SwingApplication) SwingApplication.getAppContext();

        SubHostListModel.Factory factory = new SubHostListModel.Factory(
                appInstance,
                RemoteDataSource.getInstance(appInstance));
        subHostListModel = ViewModelProviders.of(this,factory).get(SubHostListModel.class);
        subHostListModel.getSubHostRequestsLiveData().observe(this, new Observer<SubHostRequests>() {
            @Override
            public void onChanged(@Nullable SubHostRequests subHostRequests) {
                //update UI
                mSubHostRequests = subHostRequests;

                //更新数据库
                DeviceManager.saveKidsData4Shared(mSubHostRequests.getRequestTo());

                loadSubHostData();
                updateRequestFromTitle();
            }
        });

        subHostListModel.getLoadState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean == null){
                    return;
                }

                isLoadSubHost = aBoolean;
            }
        });

        if(!isLoadSubHost){
            subHostListModel.refreshSubHostData("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w("profile","onResume");

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
                    String.valueOf(parent.getLastUpdate()), new AvatarSimpleTarget(mViewPhoto));
        }

        if(watchContact != null && watchContact.mPhoto != null && parent != null){
            mViewPhoto.setBitmap(watchContact.mPhoto);
            GlideHelper.getBitMapWithWH(getContext(), UserManager.getProfileRealUri(parent.getProfile()),
                    String.valueOf(parent.getLastUpdate()),
                    mViewPhoto.getWidth(),mViewPhoto.getHeight(),
                    new AvatarSimpleTarget(mViewPhoto));
        }

        // 載入用戶的所有手錶
        if(parent != null){
            List<WatchContact.Kid> kidsForUI = DeviceManager.getKidsForUI(getContext(), parent.getUserId());
            mViewDeviceContainer.removeAllViews();
            //添加按钮
            addPlusCircleView(mViewDeviceContainer,mViewDeviceAdd);
            for (WatchContact device : kidsForUI)
                addContact(mViewDeviceContainer,null, device, mContactListener);
        }

        // 載入所有其它用戶與當前用戶分享的手錶
        List<WatchContact.Kid> kidsForUI = DeviceManager.getKidsForUI_sharedKids(getContext());
        for (WatchContact.Kid device : kidsForUI){

            if(mSubHostRequests != null){

                RequestAddSubHostEntity sharedKidsSubHostEntity = DeviceManager.getSharedKidsSubHostEntity(mSubHostRequests, device.mId);
                if(sharedKidsSubHostEntity != null){
                    addContact(mViewSharedContainer,sharedKidsSubHostEntity, device, mContactListener);
                }else {
                    //如果本地数据库中有watch数据，但是subhostlist中没有，那么可能本地数据有误，删除数据缓存
                    DeviceManager.delKidsInDB(device.mId);
                }
            }else {
                //如果尚未获取到最新的subHostList，则以数据库数据为准
                RequestAddSubHostEntity sharedKidsSubHostEntity = new RequestAddSubHostEntity();
                sharedKidsSubHostEntity.setId(device.subHostId);
                addContact(mViewSharedContainer,sharedKidsSubHostEntity, device, mContactListener);
            }

        }

        // 載入用戶發出需求的用戶
        /* for (WatchContact user : mActivityMain.mOperator.getRequestToList()) {
            if (((WatchContact.User) user).mRequestStatus.equals(WatchContact.User.STATUS_PENDING))
                addContact(mViewRequestToContainer, user, null);
        }

        // 載入所用向用戶發出請求的用戶
        for (WatchContact user : mActivityMain.mOperator.getRequestFromList()) {
            //if (((WatchContact.User) user).mRequestStatus.equals("WatchContact.User.STATUS_PENDING"))
            addContact(mViewRequestFromContainer, user, mContactListener);
        }*/

        loadSubHostData();

        updateRequestFromTitle();

        //当前focus的kids
        KidsEntityBean focusKidsInfo = DeviceManager.getFocusKidsInfo(getContext());
        if(focusKidsInfo != null){
            focusContact(BeanConvertor.getKidsForUI(focusKidsInfo), true);
        }

        subscribeUI();
    }


    private void loadSubHostData(){
        if(mSubHostRequests == null){
            return;
        }


        //当前用户发出的请求
        List<RequestAddSubHostEntity> requestTo = mSubHostRequests.getRequestTo();
        mViewRequestToContainer.removeAllViews();
        //添加按钮
        addPlusCircleView(mViewRequestToContainer,mViewRequestToAdd);

        if(!ObjectUtils.isListEmpty(requestTo)){
            for (RequestAddSubHostEntity requestToEntity : requestTo) {
                if (requestToEntity.getStatus().equals(WatchContact.User.STATUS_PENDING)) {
                    addContact(mViewRequestToContainer,requestToEntity,
                            BeanConvertor.getWatchContact(requestToEntity.getRequestToUser()),
                            mContactListener);
                }/*else if(requestToEntity.getStatus().equals(WatchContact.User.STATUS_ACCEPTED)){
                    //别人已经同意分析的kids
                    addContact(mViewSharedContainer,requestToEntity,
                            BeanConvertor.getWatchContact(requestToEntity.getRequestToUser()),
                            mContactListener);
                }*/
            }
        }


        //其他用户向自己发出的请求
        List<RequestAddSubHostEntity> requestFrom = mSubHostRequests.getRequestFrom();
        mViewRequestFromContainer.removeAllViews();
        if(!ObjectUtils.isListEmpty(requestFrom)){
            for (RequestAddSubHostEntity requestFromEntity : requestFrom) {
                if (requestFromEntity.getStatus().equals(WatchContact.User.STATUS_PENDING)){
                    addContact(mViewRequestFromContainer,requestFromEntity,
                            BeanConvertor.getWatchContact(requestFromEntity.getRequestFromUser()),
                                    mContactListener);
                }
            }

        }

    }

    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewPhoto.setBitmap(bitmap);
            }
        }
    };


   @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        //修改个人信息界面
        selectFragment(ProfileEditorFragment.class.getName(), null,true);
    }

    @OnClick(R.id.main_toolbar_action2)
    public void onToolbarAction2() {
       //option 界面
        mActivityMain.mSubHostList.push(mSubHostRequests);
        selectFragment(ProfileOptionFragment.class.getName(),null,true);
    }

    public void onToolbarTitle() {
    }

    private View.OnClickListener mAddDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentProfileSearch.class.getName(), null);
            selectFragment(WatchSearchFragment.newInstance(),true);
        }
    };

    private View.OnClickListener mAddRequestToListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mActivityMain.selectFragment(FragmentProfileRequestTo.class.getName(), null);

            selectFragment(ProfileSearchUserFragment.class.getName(),null,true);
        }
    };

    private View.OnClickListener mContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewCircle viewCircle = (ViewCircle) view;
            ViewParent viewContainer = view.getParent();
            WatchContact contact = (WatchContact) viewCircle.getTag(R.id.profile_main_photo);
            RequestAddSubHostEntity requestAddSubHostEntity =
                    (RequestAddSubHostEntity) viewCircle.getTag(R.id.profile_main_request_to_container);

            if (viewContainer == mViewDeviceContainer) {
                showKidsProfile(contact);

            } else if (viewContainer == mViewSharedContainer) {
//                focusContact(contact, false);
                showSharedKidsProfile(requestAddSubHostEntity,contact);

            } else if (viewContainer == mViewRequestToContainer) {
                showRequestToFragment(requestAddSubHostEntity,contact);
            } else if (viewContainer == mViewRequestFromContainer) {
                showRequestFromFragment(requestAddSubHostEntity,contact);
            }
        }
    };

    private void showKidsProfile(WatchContact contact){

        if(contact instanceof WatchContact.Kid){
            mActivityMain.mSubHostList.push(mSubHostRequests);

            WatchContact.Kid watchKidsInfo = (WatchContact.Kid) contact;
            selectFragment(ProfileKidsInfoFragment.newInstance(watchKidsInfo.mId),true);
        }

       /* if(contact instanceof WatchContact.Kid){
            WatchContact.Kid watchKidsInfo = (WatchContact.Kid) contact;
//            selectFragment(ProfileRemoveKidsConfirmFragment.newInstance(watchKidsInfo.mId),true);
            selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(watchKidsInfo.mId),true);
//            selectFragment(ProfileSearchUserFragment.class.getName(),null,true);
        }*/

    }

    //他人共享的watch 信息展示界面
    private void showSharedKidsProfile(RequestAddSubHostEntity requestAddSubHostEntity,WatchContact contact){

        if(contact instanceof WatchContact.Kid){
            mActivityMain.mSubHostInfoEntity.push(requestAddSubHostEntity);
            WatchContact.Kid watchKidsInfo = (WatchContact.Kid) contact;

            selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(watchKidsInfo.mId),true);
        }

    }

    private void showRequestToFragment(RequestAddSubHostEntity requestAddSubHostEntity, final WatchContact... contacts){

        mActivityMain.mSubHostInfoEntity.push(requestAddSubHostEntity);
        selectFragment(ProfileRequestToFragment.class.getName(),null,true);

    }

    private void showRequestFromFragment(RequestAddSubHostEntity requestAddSubHostEntity, final WatchContact... contacts){

        mActivityMain.mSubHostInfoEntity.push(requestAddSubHostEntity);
        selectFragment(ProfileRequestFromFragment.class.getName(),null,true);

    }


    private void testGotoKidsInfoShare(RequestAddSubHostEntity requestAddSubHostEntity, WatchContact... contacts){

        if(contacts != null && contacts.length >0 &&
                contacts[0] instanceof WatchContact.Kid){

            WatchContact.Kid kid = (WatchContact.Kid) contacts[0];
            selectFragment(ProfileKidsFromSharedInfoFragment.newInstance(kid.mId),true);
        }
    }

    private void addPlusCircleView(LinearLayout layout,View view){
        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);

        layout.addView(view, 0, layoutParams);
    }

    private void addContact(LinearLayout layout, RequestAddSubHostEntity requestAddSubHostEntity,
                            WatchContact contact, View.OnClickListener listener) {

//        layout.removeAllViews();

        ViewCircle photo = new ViewCircle(mActivityMain);
        photo.setBitmap(contact.mPhoto);


        loadAvatar(contact, photo);

        photo.setStrokeCount(12);
        photo.setStrokeBeginEnd(12, -1);
        photo.setStrokeType(ViewCircle.STROKE_TYPE_ARC);
        photo.setStrokeColorActive(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        photo.setStrokeColorNormal(ContextCompat.getColor(mActivityMain, R.color.color_white));
        photo.setStrokeWidth(4.0f);

        photo.setTag(R.id.profile_main_photo,contact);
        photo.setTag(R.id.profile_main_request_to_container,requestAddSubHostEntity);

        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);

        layout.addView(photo, 0, layoutParams);
        photo.setOnClickListener(listener);
    }

    private void loadAvatar(WatchContact contact, ViewCircle photo) {
        if(contact instanceof WatchContact.Kid){
            WatchContact.Kid kid = (WatchContact.Kid) contact;
            if(!TextUtils.isEmpty(kid.mProfile)){

                GlideHelper.getBitMapWithWH(getContext().getApplicationContext(),
                        UserManager.getProfileRealUri(kid.mProfile),
                        kid.mLastUpdate+"",
                        photo.getWidth(),photo.getHeight(),
                        new AvatarSimpleTarget(photo));
            }
        }else if(contact instanceof WatchContact.User){
            WatchContact.User user = (WatchContact.User) contact;

            if(!TextUtils.isEmpty(user.mProfile)){
                GlideHelper.getBitMapOnlyCacheInMemoryWithWH(getContext().getApplicationContext(),
                        UserManager.getProfileRealUri(user.mProfile),
                        photo.getWidth(),photo.getHeight(),
                        new AvatarSimpleTarget(photo));
            }
        }
    }

    private void delContact(LinearLayout layout, WatchContact contact) {
        int count = layout.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            View child = layout.getChildAt(idx);
            WatchContact someone = (WatchContact) child.getTag(R.id.profile_main_photo);
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
            WatchContact.Kid kid2 = (WatchContact.Kid) viewCircle.getTag(R.id.profile_main_photo);

            boolean focus = kid1 != null && kid2 != null && kid1.mId == kid2.mId && kid1.mUserId == kid2.mUserId;
            viewCircle.setActive(focus);
        }

        count = mViewSharedContainer.getChildCount();
        for (int idx = 0; idx < count; idx++) {
            ViewCircle viewCircle = (ViewCircle) mViewSharedContainer.getChildAt(idx);
            WatchContact.Kid kid1 = (WatchContact.Kid) contact;
            WatchContact.Kid kid2 = (WatchContact.Kid) viewCircle.getTag(R.id.profile_main_photo);
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
