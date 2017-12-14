package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRequests;
import com.kidsdynamic.data.net.user.model.KidInfo;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ProfileKidsInfoFragment
 * Created by Administrator on 2017/11/29.
 */

public class ProfileKidsInfoFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;
    private static final String TAG_KIDS_ID = "kids_id";

    @BindView(R.id.profile_photo)
    protected ViewCircle mViewPhoto;

    @BindView(R.id.tv_kids_name)
    protected TextView tv_kidsName;

    @BindView(R.id.tv_kids_id)
    protected TextView tv_kids_id;

    @BindView(R.id.layout_kids_sharing_with)
    protected LinearLayout layout_kids_sharing_with;
    @BindView(R.id.layout_kids_request_from)
    protected LinearLayout layout_kids_request_from;

    @BindView(R.id.btn_edit_kid_profile)
    protected Button btn_editKidsProfile;

    @BindView(R.id.btn_switch_to_account)
    protected Button btn_switchAccount;

    @BindView(R.id.btn_remove)
    protected Button btn_remove;

    private long kidsId;
    private KidsEntityBean kidsInfo;

    private SubHostRequests requestInfo;

    public static ProfileKidsInfoFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileKidsInfoFragment fragment = new ProfileKidsInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityMain = (MainFrameActivity) getActivity();

        Bundle arguments = getArguments();
        if(arguments != null){
            kidsId = arguments.getLong(TAG_KIDS_ID, -1);
        }

        if(kidsId == -1){
            exitByKidsNull();
        }
    }

    private void exitByKidsNull() {
        getFragmentManager().popBackStack();
        ToastCommon.showToast(getContext(),"kids null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_profile, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    private void initView() {

        ViewUtils.setBtnTypeFace(getContext(),btn_editKidsProfile,
                btn_switchAccount,btn_remove);

    /*    btn_editKidsProfile.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_switchAccount.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_remove.setTypeface(SwingFontsCache.getBoldType(getContext()));*/
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_kids_profile);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

    }

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();

        kidsInfo = DeviceManager.getKidsInfo(getContext(), kidsId);
        if(kidsInfo == null){
            exitByKidsNull();
            return;
        }

        if (!mActivityMain.mSubHostList.isEmpty()) {
            requestInfo = mActivityMain.mSubHostList.pop();
        }

        WatchContact watchContact = null;
        if(!mActivityMain.mWatchContactStack.isEmpty()){
            watchContact = mActivityMain.mWatchContactStack.pop();
        }

        loadValue(kidsInfo, watchContact);
    }

    private void loadValue(KidsEntityBean kidsInfo, WatchContact watchContact) {

        tv_kids_id.setText(String.valueOf(kidsInfo.getKidsId()));
        tv_kidsName.setText(kidsInfo.getName());

        if(watchContact != null){
            //如果不为null，则为edit界面返回数据
            mViewPhoto.setBitmap(watchContact.mPhoto);

        }

        GlideHelper.getBitMap(getContext(),
                UserManager.getProfileRealUri(kidsInfo.getProfile()),
                String.valueOf(kidsInfo.getLastUpdate()), userAvatarSimpleTarget);

        //当前kids的shared信息
        //获取当前kids已经被分享的user list
        //请求

    }

    private void loadSharingWith(){
        if(requestInfo == null){
            return;
        }

        List<RequestAddSubHostEntity> requestFrom = requestInfo.getRequestFrom();
        if(ObjectUtils.isListEmpty(requestFrom)){
            return;
        }

        List<UserInfo> sharingUserList = new ArrayList<>(5);

        for (RequestAddSubHostEntity requestAddSubHostEntity: requestFrom) {
            if (requestAddSubHostEntity.getStatus().equals(WatchContact.User.STATUS_ACCEPTED)
                    && isContainKids(requestAddSubHostEntity.getKids(),kidsId)) {
                sharingUserList.add(requestAddSubHostEntity.getRequestFromUser());
            }
        }

        if(!ObjectUtils.isListEmpty(sharingUserList)){
            layout_kids_sharing_with.setVisibility(View.VISIBLE);
        }

        //todo

    }

    private boolean isContainKids(List<KidInfo> kidsList, long kidsId){
        boolean res = false;
        if(!ObjectUtils.isListEmpty(kidsList)){
            for (KidInfo kidInfo:kidsList) {
                if(kidInfo.getId() == kidsId){
                    res = true;
                    break;
                }
            }
        }

        return res;
    }

    private void addContact(LinearLayout layout, RequestAddSubHostEntity requestAddSubHostEntity,
                            WatchContact contact, View.OnClickListener listener) {

//        layout.removeAllViews();

        ViewCircle photo = new ViewCircle(mActivityMain);
        photo.setBitmap(contact.mPhoto);

        if(contact instanceof WatchContact.Kid){
            WatchContact.Kid kid = (WatchContact.Kid) contact;
            GlideHelper.getBitMap(getContext().getApplicationContext(),
                    UserManager.getProfileRealUri(kid.mProfile),
                    kid.mLastUpdate+"", new AvatarSimpleTarget(photo));
        }

        photo.setStrokeCount(12);
        photo.setStrokeBeginEnd(12, -1);
        photo.setStrokeType(ViewCircle.STROKE_TYPE_ARC);
        photo.setStrokeColorActive(ContextCompat.getColor(mActivityMain, R.color.color_orange_main));
        photo.setStrokeColorNormal(ContextCompat.getColor(mActivityMain, R.color.color_white));
        photo.setTag(R.id.profile_main_photo,contact);
        photo.setTag(R.id.profile_main_request_to_container,requestAddSubHostEntity);

        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);

        layout.addView(photo, 0, layoutParams);
        photo.setOnClickListener(listener);
    }


    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewPhoto.setBitmap(bitmap);
            }
        }
    };

    @OnClick(R.id.btn_edit_kid_profile)
    protected void onEditProfileClick(){
        //跳转到编辑kids 信息界面
        WatchContact.Kid watchKidsInfo =
                BeanConvertor.getKidsForUI(kidsInfo);

        if(mViewPhoto.getBitmap() != null){
            watchKidsInfo.mPhoto = mViewPhoto.getBitmap();
        }

        mActivityMain.mWatchContactStack.push(watchKidsInfo);

        selectFragment(ProfileKidsEditorFragment.class.getName(),null,true);

    }

    @OnClick(R.id.btn_switch_to_account)
    protected void onSwitchAccount(){
        selectFragment(ProfileSwitchKidsConfirmFragment.newInstance(kidsId),true);
    }

    @OnClick(R.id.btn_remove)
    protected void onRemove(){
        selectFragment(ProfileSwitchKidsConfirmFragment.newInstance(kidsId),true);
    }

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


}
