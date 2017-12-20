package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRemovedKidRequest;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.ProfileManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * ProfileRemoveKidsConfirmFragment
 * Created by Administrator on 2017/11/29.
 */

public class ProfileRemoveKidsConfirmFragment extends ProfileBaseFragment {
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

    @BindView(R.id.btn_confirm)
    protected Button btn_confirm;

    @BindView(R.id.btn_cancel)
    protected Button btn_cancel;

    private long kidsId;
    private KidsEntityBean kidsInfo;
    private RequestAddSubHostEntity requestInfo;

    private KidsHandler kidsHandler;

    public static ProfileRemoveKidsConfirmFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileRemoveKidsConfirmFragment fragment = new ProfileRemoveKidsConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityMain = (MainFrameActivity) getActivity();

        handleBackKey();

        if(!mActivityMain.mSubHostInfoEntity.isEmpty()){
            requestInfo = mActivityMain.mSubHostInfoEntity.pop();
        }

        Bundle arguments = getArguments();
        if(arguments != null){
            kidsId = arguments.getLong(TAG_KIDS_ID, -1);
        }

        if(kidsId == -1 || requestInfo == null){
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
                        && isRemoveOKStatus()){
                    onTopLeftBtnClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void exitByKidsNull() {
        getFragmentManager().popBackStack();
        ToastCommon.showToast(getContext(),"kids null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_remove_confirm, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    private void initView() {
        ViewUtils.setBtnTypeFace(getContext(),btn_confirm,btn_cancel);
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_kid_remove);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

    }

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {
        if(isRemoveOKStatus()){
            mActivityMain.mSignStack.push(ProfileManager.sign_remove_ok);
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public boolean isRemoveOKStatus(){
        return btn_confirm.getVisibility() == View.INVISIBLE;
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
                String.valueOf(kidsInfo.getLastUpdate()), new AvatarSimpleTarget(mViewPhoto));
    }


    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewPhoto.setBitmap(bitmap);
            }
        }
    };

    @OnClick(R.id.btn_confirm)
    protected void onConfirmRemove(){
        //confirm to remove account
        showLoadingDialog(R.string.signup_login_wait);
        //拒绝 共享请求
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);

        SubHostRemovedKidRequest subHostRemovedKidRequest = new SubHostRemovedKidRequest();
        subHostRemovedKidRequest.setKidId(kidsId);
        subHostRemovedKidRequest.setSubHostId(requestInfo.getId());


        hostApi.subHostRemoveKid(subHostRemovedKidRequest).enqueue(new BaseRetrofitCallback<RequestAddSubHostEntity>() {
            @Override
            public void onResponse(Call<RequestAddSubHostEntity> call, Response<RequestAddSubHostEntity> response) {
                int code = response.code();
                if(code == 200){
                    //切换kids成功后，隐藏btn，更新note
                    afterRemoveKids();
                }else if(code == 401){
                    ToastCommon.makeText(getContext(),R.string.error_subhost_remove_kid_403);
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

    private void afterRemoveKids() {
        btn_confirm.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);
        tv_note.setText(R.string.profile_kids_after_remove_tip);


        kidsHandler = new KidsHandler(this);
        Message message = Message.obtain();
        message.arg1 = 1;
        //3秒后关闭界面
        kidsHandler.sendMessageDelayed(message,3000);
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
        //remove 成功后，需要通知上个界面关闭
        mActivityMain.mSignStack.push(ProfileManager.sign_remove_ok);
        getFragmentManager().popBackStack();
    }

    public static class  KidsHandler extends Handler{

        private final WeakReference<ProfileRemoveKidsConfirmFragment> thisFragment;

        public KidsHandler(ProfileRemoveKidsConfirmFragment fragment){
            thisFragment = new WeakReference<ProfileRemoveKidsConfirmFragment>(fragment);
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
