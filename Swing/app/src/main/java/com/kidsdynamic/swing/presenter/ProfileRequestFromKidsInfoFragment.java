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
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.DenySubHost;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
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
 * ProfileRequestFromKidsInfoFragment
 * Created by Administrator on 2017/12/2.
 */

public class ProfileRequestFromKidsInfoFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;
    private static final String TAG_KIDS_ID = "kids_id";

    @BindView(R.id.user_profile_photo)
    protected ViewCircle mViewUserPhoto;

    @BindView(R.id.request_kids_profile_photo)
    protected ViewCircle mViewRequestKidPhoto;

//    request from layout start
    @BindView(R.id.layout_request_from_info)
    protected View layout_request_info;

    @BindView(R.id.tv_request_kids_tip)
    protected TextView tv_request_from_note;

    @BindView(R.id.btn_accept_request)
    protected Button btn_accept_request;

    @BindView(R.id.btn_decline)
    protected Button btn_decline;
//    request from layout end

//    sharing now layout确认界面 start
    @BindView(R.id.layout_sharing_now)
    protected View layout_sharing_now;

    @BindView(R.id.tv_sharing_info_tip)
    protected TextView tv_sharing_info_tip;

    @BindView(R.id.btn_remove_sharing)
    protected Button btn_remove_sharing;
//    sharing now layout确认界面 end

//    decline confirm layout确认界面 start
    @BindView(R.id.layout_stop_share)
    protected View layout_stop_share;

    @BindView(R.id.tv_stop_share_tip)
    protected TextView tv_stop_share_tip;

    @BindView(R.id.btn_confirm_stop_share)
    protected Button btn_confirm_stop_share;

    @BindView(R.id.btn_no)
    protected Button btn_no_stop;
//    decline confirm layout确认界面 end

    private List<View> layouts = new ArrayList<>(3);


    private long kidsId;
    private KidsEntityBean kidsInfo;

    private RequestAddSubHostEntity requestFrom;

    public static ProfileRequestFromKidsInfoFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileRequestFromKidsInfoFragment fragment = new ProfileRequestFromKidsInfoFragment();
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

        layouts.add(layout_request_info);
        layouts.add(layout_sharing_now);
        layouts.add(layout_stop_share);
    }

    private void exitByKidsNull() {
        getFragmentManager().popBackStack();
        ToastCommon.showToast(getContext(),"kids null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_request_from, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    private void initView() {
        ViewUtils.setBtnTypeFace(getContext(),btn_accept_request,btn_decline,
                btn_remove_sharing,btn_confirm_stop_share,
                btn_no_stop);

        //todo test ui
        String requestUserName = "Dan Smith";
        String requestKidsName = "Alex Smith";
        String request_from_note = getString(R.string.profile_got_request_note,
                requestUserName);

        String sharing_note = getString(R.string.profile_sharing_now_note,
                requestKidsName,requestUserName);

        String share_stop_note = getString(R.string.profile_stop_share,requestUserName);


        tv_request_from_note.setText(ViewUtils.setWordColorInStr(request_from_note,
                requestUserName));

        tv_sharing_info_tip.setText(ViewUtils.setWordColorInStr(sharing_note,
                requestKidsName,requestUserName));

        tv_stop_share_tip.setText(share_stop_note);

    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_title_request_from);
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

    private void loadValue(KidsEntityBean kidsInfo, WatchContact watchContact) {

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


    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (getActivity() != null && !getActivity().isDestroyed()) {
//                mViewPhoto.setBitmap(bitmap);
            }
        }
    };

    @OnClick(R.id.btn_accept_request)
    protected void onAcceptRequest(){
        //accept request
//        showLayout(R.id.layout_sharing_now);

        //新的流程为，调用accept后，跳转到select watch to share 界面
        selectFragment(ProfileShareKidsSelectFragment.class.getName(),null,
                true);

    }

    @OnClick(R.id.btn_decline)
    protected void onDeclineRequestFrom(){
        showLayout(R.id.layout_stop_share);
    }

    @OnClick(R.id.btn_confirm_stop_share)
    protected void onConfirmStopShare(){

        if(requestFrom == null){
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);
        //拒绝 共享请求
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);

        DenySubHost subHostId = new DenySubHost();
        subHostId.setSubHostId(requestFrom.getId());
        hostApi.subHostDeny(subHostId).enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                int code = response.code();

                if(200 == code){
                    // TODO: 2017/12/6
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

//        showLayout(R.id.layout_stop_share);
    }

    @OnClick(R.id.btn_no)
    protected void onNoStopShare(){
        showLayout(R.id.layout_request_from_info);
    }

    @OnClick(R.id.btn_remove_sharing)
    protected void onRemoveShare(){
        getFragmentManager().popBackStack();
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
