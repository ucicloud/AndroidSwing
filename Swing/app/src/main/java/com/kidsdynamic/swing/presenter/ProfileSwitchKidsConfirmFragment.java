package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.ProfileManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ProfileSwitchKidsConfirmFragment
 * Created by Administrator on 2017/11/29.
 */

public class ProfileSwitchKidsConfirmFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;
    private static final String TAG_KIDS_ID = "kids_id";

    @BindView(R.id.kids_profile_photo)
    protected ViewCircle mViewPhoto;

    @BindView(R.id.tv_kids_name)
    protected TextView tv_kidsName;

    @BindView(R.id.tv_kids_id)
    protected TextView tv_kids_id;

    @BindView(R.id.switch_confirm_note)
    protected TextView tv_note;

    @BindView(R.id.btn_confirm_switch)
    protected Button btn_switch_confirm;

    @BindView(R.id.btn_cancel)
    protected Button btn_cancel;

    private long kidsId;
    private KidsEntityBean kidsInfo;

    private KidsHandler kidsHandler;

    public static ProfileSwitchKidsConfirmFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileSwitchKidsConfirmFragment fragment = new ProfileSwitchKidsConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityMain = (MainFrameActivity) getActivity();

        handleBackKey();

        Bundle arguments = getArguments();
        if(arguments != null){
            kidsId = arguments.getLong(TAG_KIDS_ID, -1);
        }

        if(kidsId == -1){
            exitByKidsNull();
        }
    }

    private void handleBackKey() {

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_BACK
                        && isConfirmOKStatus()){
                    onTopLeftBtnClick();
                    return true;
                }
                return false;
            }
        });
    }

    public boolean isConfirmOKStatus(){
        return btn_switch_confirm.getVisibility() == View.INVISIBLE;
    }

    private void exitByKidsNull() {
        getFragmentManager().popBackStack();
        ToastCommon.showToast(getContext(),"kids null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_switch, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    private void initView() {
        btn_switch_confirm.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_cancel.setTypeface(SwingFontsCache.getBoldType(getContext()));
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_title_switch_account);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

    }

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {

        if(isConfirmOKStatus()){
            mActivityMain.mSignStack.push(ProfileManager.sign_remove_ok);
        }
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

        if(!TextUtils.isEmpty(kidsInfo.getProfile())){
            GlideHelper.getBitMap(getContext(),
                    UserManager.getProfileRealUri(kidsInfo.getProfile()),
                    String.valueOf(kidsInfo.getLastUpdate()), new AvatarSimpleTarget(mViewPhoto));
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

    @OnClick(R.id.btn_confirm_switch)
    protected void onConfirmSwitch(){
        //confirm to switch account

        //更新本地缓存
        if(!DeviceManager.updateFocusKids(kidsId)){
            ToastCommon.makeText(getContext(),R.string.normal_err,-1);
            return;
        }

        //切换kids成功后，隐藏btn，更新note
        btn_switch_confirm.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);
        tv_note.setText(R.string.profile_kids_switch_done_tip);


        //发送更新广播
        updateTabAvatar();

        kidsHandler = new KidsHandler(this);
        Message message = Message.obtain();
        message.arg1 = 1;
        //3秒后关闭界面
        kidsHandler.sendMessageDelayed(message,3000);
    }

    private void updateTabAvatar() {
        WatchContact watchContact = new WatchContact();
        watchContact.mPhoto = mViewPhoto.getBitmap();
        watchContact.mLabel = "editProfile";

        mActivityMain.mWatchContactStack.push(watchContact);
        DeviceManager.sendBroadcastUpdateAvatar();
    }

    @OnClick(R.id.btn_cancel)
    protected void onCancel(){
        getFragmentManager().popBackStack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(kidsHandler != null){
            //清除所有在队列中消息
            kidsHandler.removeCallbacksAndMessages(null);
        }
    }

    public void exit(){
        //switch 成功后，需要通知上个界面关闭
        mActivityMain.mSignStack.push(ProfileManager.sign_remove_ok);
        getFragmentManager().popBackStack();
    }

    public static class  KidsHandler extends Handler{

        private final WeakReference<ProfileSwitchKidsConfirmFragment> thisFragment;

        public KidsHandler(ProfileSwitchKidsConfirmFragment fragment){
            thisFragment = new WeakReference<ProfileSwitchKidsConfirmFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(thisFragment.get() == null){
                return;
            }

            thisFragment.get().exit();
        }
    }

}
