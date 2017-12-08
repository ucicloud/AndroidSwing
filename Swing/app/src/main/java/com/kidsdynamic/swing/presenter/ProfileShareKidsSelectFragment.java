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
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择要分享的kids watch
 * ProfileShareKidsSelectFragment
 * Created by Administrator on 2017/12/2.
 */

public class ProfileShareKidsSelectFragment extends ProfileBaseFragment {
    private MainFrameActivity mActivityMain;
    private static final String TAG_KIDS_ID = "kids_id";


//    request from layout start

    @BindView(R.id.tv_request_kids_tip)
    protected TextView tv_request_from_note;

    @BindView(R.id.btn_confirm_select)
    protected Button btn_accept_request;

    @BindView(R.id.btn_decline)
    protected Button btn_decline;
//    request from layout end

    private List<View> layouts = new ArrayList<>(3);


    private long kidsId;
    private KidsEntityBean kidsInfo;

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
        View mView = inflater.inflate(R.layout.fragment_kids_share_to_other_select, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    private void initView() {
        ViewUtils.setBtnTypeFace(getContext(),btn_accept_request,btn_decline,
                btn_accept_request,btn_decline);

        //todo test ui
        String requestUserName = "Dan Smith";
        String requestKidsName = "Alex Smith";
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

        // TODO: 2017/12/8 调用 “/v1/subHost/accept” 同意分享请求

        //accept request
        showLayout(R.id.layout_sharing_now);

    }

    @OnClick(R.id.btn_decline)
    protected void onDeclineRequestFrom(){
        showLayout(R.id.layout_stop_share);
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
