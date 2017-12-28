package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * ProfileRequestToFragment
 * Created by Administrator on 2017/12/2.
 */

public class ProfileRequestToFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;
    private static final String TAG_KIDS_ID = "kids_id";

    @BindView(R.id.user_profile_photo)
    protected ViewCircle mViewUserPhoto;

    @BindView(R.id.request_kids_profile_photo)
    protected ViewCircle mViewRequestKidPhoto;

    @BindView(R.id.layout_request_info)
    protected View layout_request_info;

    @BindView(R.id.layout_cancel_confirm)
    protected View layout_cancel_confirm;

    @BindView(R.id.tv_request_kids_tip)
    protected TextView tv_note;

    @BindView(R.id.btn_cancel_request)
    protected Button btn_cancel_request;

    @BindView(R.id.btn_confirm_cancel)
    protected Button btn_confirm_cancel_request;

    @BindView(R.id.btn_no)
    protected Button btn_no;

    private long kidsId;
    private KidsEntityBean kidsInfo;

    private RequestAddSubHostEntity requestTo;

    public static ProfileRequestToFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileRequestToFragment fragment = new ProfileRequestToFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityMain = (MainFrameActivity) getActivity();

        /*Bundle arguments = getArguments();
        if(arguments != null){
            kidsId = arguments.getLong(TAG_KIDS_ID, -1);
            if(kidsId == -1){
                exitByKidsNull();
            }
        }*/

        if (!mActivityMain.mSubHostInfoEntity.isEmpty()) {
            requestTo = mActivityMain.mSubHostInfoEntity.pop();
        }


        if(requestTo == null
                || requestTo.getRequestToUser() == null
                || requestTo.getRequestFromUser() == null){
            exitByKidsNull();
        }

        loadValue();
    }

    private void exitByKidsNull() {
        getFragmentManager().popBackStack();
        ToastCommon.showToast(getContext(),"kids null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_request_to, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    private void initView() {
        btn_cancel_request.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_confirm_cancel_request.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_no.setTypeface(SwingFontsCache.getBoldType(getContext()));

//        profile_logout_confirm_name
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_title_request_to);
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

        /*kidsInfo = DeviceManager.getKidsInfo(getContext(), kidsId);
        if(kidsInfo == null){
            exitByKidsNull();
            return;
        }

        WatchContact watchContact = null;
        if(!mActivityMain.mWatchContactStack.isEmpty()){
            watchContact = mActivityMain.mWatchContactStack.pop();
        }

        loadValue(kidsInfo, watchContact);*/
    }

    private void loadValue() {

        DB_User currentLoginUserInfo = LoginManager.getCurrentLoginUserInfo();
        if(currentLoginUserInfo != null){
            if(!TextUtils.isEmpty(currentLoginUserInfo.getProfile())){
                GlideHelper.getBitMap(getContext(),
                        UserManager.getProfileRealUri(currentLoginUserInfo.getProfile()),
                        String.valueOf(currentLoginUserInfo.getLastUpdate()),
                        new AvatarSimpleTarget(mViewUserPhoto));
            }
        }

        UserInfo requestToUser = requestTo.getRequestToUser();
        String requestToUserName = LoginManager.getUserName(requestToUser.getFirstName(),
                requestToUser.getLastName());

        String note = getString(R.string.request_kids_note, requestToUserName);

        tv_note.setText(ViewUtils.setWordColorInStr(note,requestToUserName));


        //load avatar
        if(!TextUtils.isEmpty(requestToUser.getProfile())){
            GlideHelper.getBitMapOnlyCacheInMemory(getContext(),
                    UserManager.getProfileRealUri(requestToUser.getProfile()),
                    new AvatarSimpleTarget(mViewRequestKidPhoto));

        }


        /*tv_kids_id.setText(String.valueOf(kidsInfo.getKidsId()));
        tv_kidsName.setText(kidsInfo.getName());

        if(watchContact != null){
            //如果不为null，则为edit界面返回数据
            mViewPhoto.setBitmap(watchContact.mPhoto);

        }

        GlideHelper.getBitMap(getContext(),
                UserManager.getProfileRealUri(kidsInfo.getProfile()),
                String.valueOf(kidsInfo.getLastUpdate()), userAvatarSimpleTarget);*/
    }


    private SimpleTarget<Bitmap> currentUserAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (getActivity() != null && !getActivity().isDestroyed()) {
                mViewUserPhoto.setBitmap(bitmap);
            }
        }
    };

    @OnClick(R.id.btn_cancel_request)
    protected void onCancelRequest(){
        //show cancel confirm UI
        layout_request_info.setVisibility(View.GONE);
        layout_cancel_confirm.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.btn_no)
    protected void onNoCancel(){
        layout_request_info.setVisibility(View.VISIBLE);
        layout_cancel_confirm.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_confirm_cancel)
    public void onCancelConfirmClick(){
        if(requestTo == null){
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);
        //取消 共享申请
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);
        hostApi.subHostDelete(requestTo.getId()).
                enqueue(new BaseRetrofitCallback<RequestAddSubHostEntity>() {
            @Override
            public void onResponse(Call<RequestAddSubHostEntity> call,
                                   Response<RequestAddSubHostEntity> response) {
                int code = response.code();
                if(code == 200){
                    // 2017/12/6
                    getFragmentManager().popBackStack();
                }else if(code == 401){
                    //unauthorized
                    ToastCommon.makeText(getContext(),R.string.error_api_event_delete_401);
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
