package com.kidsdynamic.swing.presenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.AcceptSubHostRequest;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.ProfileManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 选择要分享的kids watch
 * ProfileShareKidsSelectFragment
 * Created by Administrator on 2017/12/2.
 */

public class ProfileShareKidsSelectFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;
    private static final String TAG_KIDS_ID = "kids_id";


    @BindView(R.id.profile_share_device_container)
    protected LinearLayout mViewDeviceContainer;

//    request from layout start

    @BindView(R.id.tv_request_kids_tip)
    protected TextView tv_request_from_note;

    @BindView(R.id.btn_confirm_select)
    protected Button btn_accept_request;

    @BindView(R.id.btn_decline)
    protected Button btn_decline;
//    request from layout end

    private List<View> layouts = new ArrayList<>(3);
    private LongSparseArray<WatchContact.Kid> watchContactHashMap = new LongSparseArray<>(3);


    private long kidsId;
    private KidsEntityBean kidsInfo;

    private RequestAddSubHostEntity requestInfo;

    public static ProfileShareKidsSelectFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileShareKidsSelectFragment fragment = new ProfileShareKidsSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityMain = (MainFrameActivity) getActivity();

        if(!mActivityMain.mSubHostInfoEntity.isEmpty()){
            requestInfo = mActivityMain.mSubHostInfoEntity.pop();
        }

        if(requestInfo == null ||
                requestInfo.getRequestFromUser() == null
                /*|| loginUserInfo == null*/){
            exitByKidsNull();
            return;
        }

        loadInfo();
    }

    private void exitByKidsNull() {
        getFragmentManager().popBackStack();
        ToastCommon.showToast(getContext(),"request info null");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_share_to_other_select, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        return mView;

    }

    private void loadInfo() {
        ViewUtils.setBtnTypeFace(getContext(),btn_accept_request,btn_decline,
                btn_accept_request,btn_decline);

        String requestUserName = LoginManager.getUserName(
                requestInfo.getRequestFromUser().getFirstName(),
                requestInfo.getRequestFromUser().getLastName());

        String request_from_note = getString(R.string.profile_kids_select_watch_share_tip,
                requestUserName);

        tv_request_from_note.setText(ViewUtils.setWordColorInStr(request_from_note,
                requestUserName));


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

        DB_User parent = LoginManager.getCurrentLoginUserInfo();

        if (!mActivityMain.mSignStack.isEmpty()) {
            String signStr = mActivityMain.mSignStack.pop();
            if(ProfileManager.sign_deny_ok.equals(signStr)){
                mActivityMain.mSignStack.push(ProfileManager.sign_deny_ok);
                getFragmentManager().popBackStack();
                return;
            }
        }

        // 載入用戶的所有手錶
        if(parent != null){
            List<WatchContact.Kid> kidsForUI = DeviceManager.getKidsForUI(getContext(), parent.getUserId());
            for (WatchContact.Kid device : kidsForUI){
                addContact(mViewDeviceContainer, device, mContactListener);
                watchContactHashMap.put(device.mId,device);
            }
        }
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

    @OnClick(R.id.btn_confirm_select)
    protected void onAcceptRequest(){

        // 2017/12/8 调用 “/v1/subHost/accept” 同意分享请求

        //accept request
//        showLayout(R.id.layout_sharing_now);

        List<Long> selectedKids = new ArrayList<>(3);
        for (int i = 0; i < watchContactHashMap.size(); i++) {
            WatchContact.Kid kid = watchContactHashMap.valueAt(i);
            if(kid.isSelect){
                selectedKids.add(kid.mId);
            }
        }


        Log.w("profile", "selectKids " + Arrays.toString(selectedKids.toArray()));

        if(ObjectUtils.isListEmpty(selectedKids)){
            ToastCommon.makeText(getContext(),R.string.share_no_select_kids);
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);
        //接受 共享请求
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);

        AcceptSubHostRequest acceptSubHostRequest = new AcceptSubHostRequest();
        acceptSubHostRequest.setSubHostId(requestInfo.getId());
        acceptSubHostRequest.setKidId(selectedKids);

        hostApi.subHostAccept(acceptSubHostRequest).enqueue(new BaseRetrofitCallback<RequestAddSubHostEntity>() {
            @Override
            public void onResponse(Call<RequestAddSubHostEntity> call, Response<RequestAddSubHostEntity> response) {

                int code = response.code();
                if(code == 200){
                    //accept success

                    mActivityMain.mSubHostInfoEntity.push(requestInfo);
                    selectFragment(ProfileRequestFromShareNowFragment.class.getName(),null,
                            true);
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

    @OnClick(R.id.btn_decline)
    protected void onDeclineRequestFrom(){

        mActivityMain.mSubHostInfoEntity.push(requestInfo);
        selectFragment(ProfileRequestFromDenyConfirmFragment.class.getName(),
                null,true);
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
        photo.setStrokeColorNormal(ContextCompat.getColor(mActivityMain, R.color.color_blue_light2));
        photo.setTag(contact);

        int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, 0, margin, 0);

        layout.addView(photo, 0, layoutParams);
        photo.setOnClickListener(listener);
    }

    private class AvatarSimpleTarget extends SimpleTarget<Bitmap>{

        ViewCircle viewCircle;

        AvatarSimpleTarget(ViewCircle viewCircle){
            this.viewCircle = viewCircle;
        }

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            if(viewCircle != null && getActivity() != null && !getActivity().isDestroyed()){
                viewCircle.setBitmap(bitmap);
            }
        }
    }

    private View.OnClickListener mContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewCircle viewCircle = (ViewCircle) view;
            ViewParent viewContainer = view.getParent();
            WatchContact.Kid contact = (WatchContact.Kid) viewCircle.getTag();

            if (viewContainer == mViewDeviceContainer) {

                //一期暂时没跳转
//                showKidsProfile(contact);

                viewCircle.setActive(!viewCircle.getActive());
                contact.isSelect = viewCircle.getActive();

                watchContactHashMap.get(contact.mId).isSelect = viewCircle.getActive();

            }
        }
    };

}
