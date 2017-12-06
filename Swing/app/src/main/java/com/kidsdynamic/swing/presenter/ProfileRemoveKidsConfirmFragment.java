package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewPhoto.setBitmap(bitmap);
            }
        }
    };

    @OnClick(R.id.btn_confirm)
    protected void onConfirmSwitch(){
        //confirm to switch account
        // TODO: 2017/12/1


        //切换kids成功后，隐藏btn，更新note
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
