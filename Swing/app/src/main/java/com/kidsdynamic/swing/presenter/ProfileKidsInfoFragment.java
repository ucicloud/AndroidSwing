package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

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

    @BindView(R.id.btn_edit_kid_profile)
    protected Button btn_editKidsProfile;

    @BindView(R.id.btn_switch_to_account)
    protected Button btn_switchAccount;

    @BindView(R.id.btn_remove)
    protected Button btn_remove;

    private long kidsId;
    private KidsEntityBean kidsInfo;

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
            if(kidsId == -1){
                exitByKidsNull();
            }
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
        btn_editKidsProfile.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_switchAccount.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_remove.setTypeface(SwingFontsCache.getBoldType(getContext()));
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
    }


    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (!getActivity().isDestroyed()) {
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
        selectFragment(ProfileSwitchKidsFragment.newInstance(kidsId),true);
    }


}
