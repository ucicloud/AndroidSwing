package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.DenySubHost;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRemovedKidRequest;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.ProfileManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * ProfileRequestFromShareNowFragment
 * Created by Administrator on 2017/12/2.
 */

public class ProfileRequestFromShareNowFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;

    @BindView(R.id.user_profile_photo)
    protected ViewCircle mViewRequestFromUserPhoto;

    @BindView(R.id.request_kids_profile_photo)
    protected ViewCircle mViewRequestToUserPhoto;

    //    sharing now layout确认界面 start
    @BindView(R.id.layout_sharing_now)
    protected View layout_sharing_now;

    @BindView(R.id.tv_sharing_info_tip)
    protected TextView tv_sharing_info_tip;

    @BindView(R.id.btn_remove_sharing)
    protected Button btn_remove_sharing;
//    sharing now layout确认界面 end

    private List<View> layouts = new ArrayList<>(3);



    private long kidsId;
    private KidsEntityBean kidsInfo;

    private RequestAddSubHostEntity requestInfo;
    private DB_User loginUserInfo;

    /*public static ProfileRequestFromShareNowFragment newInstance(String layoutType) {
        Bundle args = new Bundle();
        args.putString(TAG_LAYOUT_TYPE,layoutType);
        ProfileRequestFromShareNowFragment fragment = new ProfileRequestFromShareNowFragment();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layouts.add(layout_sharing_now);

        /*Bundle arguments = getArguments();
        if(arguments != null){
            layoutType = arguments.getString(TAG_LAYOUT_TYPE, TAG_LAYOUT_TYPE_deny_confirm);
            if(TAG_LAYOUT_TYPE_deny_confirm.equals(layoutType)){
                showLayout(R.id.layout_stop_share);
            }else if(TAG_LAYOUT_TYPE_share_now.equals(layoutType)){
                showLayout(R.id.layout_sharing_now);
            }
        }*/

        mActivityMain = (MainFrameActivity) getActivity();

        handleBackKey();

        if(!mActivityMain.mSubHostInfoEntity.isEmpty()){
            requestInfo = mActivityMain.mSubHostInfoEntity.pop();
        }

        loginUserInfo = LoginManager.getCurrentLoginUserInfo();

        if(requestInfo == null || requestInfo.getRequestFromUser() == null
                || loginUserInfo == null){
            exitByKidsNull();
            return;
        }


        loadInfo();


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
                if(keycode == KeyEvent.KEYCODE_BACK){
                    onTopLeftBtnClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void exitByKidsNull() {
        getFragmentManager().popBackStack();
        ToastCommon.showToast(getContext(),"info null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_request_from_share_now, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        return mView;

    }

    private void loadInfo() {
        ViewUtils.setBtnTypeFace(getContext(),btn_remove_sharing);


        String requestUserName = LoginManager.getUserName(
                requestInfo.getRequestFromUser().getFirstName(),
                requestInfo.getRequestFromUser().getLastName());
        String currentUserName = LoginManager.getUserName(loginUserInfo.getFirstName(),
                loginUserInfo.getLastName());


        /*String share_stop_note = getString(R.string.profile_stop_share,requestUserName);
        tv_stop_share_tip.setText(share_stop_note);*/

        //share now 界面的note
       String sharing_note = getString(R.string.profile_sharing_now_note,
               currentUserName,requestUserName);

        tv_sharing_info_tip.setText(ViewUtils.setWordColorInStr(sharing_note,
                currentUserName,requestUserName));

        //load avatar
        loadAvatar();

    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_title_request_from);
        view_left_action.setImageResource(R.drawable.icon_left);

    }

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {

        /*if(TAG_LAYOUT_TYPE_deny_confirm.equals(layoutType)){
            getFragmentManager().popBackStack();
        }else if(TAG_LAYOUT_TYPE_share_now.equals(layoutType)){
            mActivityMain.mSignStack.push(ProfileManager.sign_deny_ok);
            getFragmentManager().popBackStack();
        }*/

        mActivityMain.mSignStack.push(ProfileManager.sign_deny_ok);
        getFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void loadAvatar() {

        //request from user
        String requestFromUerProfile = requestInfo.getRequestFromUser().getProfile();
        if(!TextUtils.isEmpty(requestFromUerProfile)){
            GlideHelper.getBitMapOnlyCacheInMemory(getContext(),
                    UserManager.getProfileRealUri(requestFromUerProfile),
                    requestFromUserAvatarSimpleTarget);
        }

        //current user
        GlideHelper.getBitMap(getContext(),
                UserManager.getProfileRealUri(loginUserInfo.getProfile()),
                loginUserInfo.getLastUpdate()+"",
                currentUserAvatarSimpleTarget);
    }


    private SimpleTarget<Bitmap> requestFromUserAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewRequestFromUserPhoto.setBitmap(bitmap);
            }
        }
    };

    private SimpleTarget<Bitmap> currentUserAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewRequestToUserPhoto.setBitmap(bitmap);
            }
        }
    };


    private void denySubHost() {
        if(requestInfo == null){
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);
        //拒绝 共享请求
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);

        DenySubHost subHostId = new DenySubHost();
        subHostId.setSubHostId(requestInfo.getId());
        hostApi.subHostDeny(subHostId).enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                int code = response.code();

                if(200 == code){
                    getFragmentManager().popBackStack();
                    //deny 成功后，需要通知上个界面关闭
                    mActivityMain.mSignStack.push(ProfileManager.sign_deny_ok);
                    //通知上个界面是那个请求被删除
                    mActivityMain.mRemovedSubHostInfoEntity.push(requestInfo);
                }else {
                    ToastCommon.makeText(getContext(),R.string.normal_err,code);
                }

                finishLoadingDialog();
                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                finishLoadingDialog();

                super.onFailure(call, t);
            }
        });
    }

    private void deleteSubHost() {
        if(requestInfo == null){
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);
        //拒绝 共享请求
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);

        SubHostRemovedKidRequest subHostRemovedKidRequest = new SubHostRemovedKidRequest();
        subHostRemovedKidRequest.setSubHostId(requestInfo.getId());
        subHostRemovedKidRequest.setKidId(kidsId);

        hostApi.subHostRemoveKid(subHostRemovedKidRequest).enqueue(new BaseRetrofitCallback<RequestAddSubHostEntity>() {
            @Override
            public void onResponse(Call<RequestAddSubHostEntity> call, Response<RequestAddSubHostEntity> response) {
                int code = response.code();

                if(200 == code){

                    getFragmentManager().popBackStack();
                    //deny 成功后，需要通知上个界面关闭
                    mActivityMain.mSignStack.push(ProfileManager.sign_deny_ok);
                    //通知上个界面是那个请求被删除
                    mActivityMain.mRemovedSubHostInfoEntity.push(requestInfo);
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

    @OnClick(R.id.btn_remove_sharing)
    protected void onRemoveShare(){
        deleteSubHost();
    }

    private void showLayout(int layoutId){
        for (View layout : layouts) {
            if(layout.getId() == layoutId){
                layout.setVisibility(View.VISIBLE);
            }else {
                layout.setVisibility(View.GONE);
            }


        }
    }


}
