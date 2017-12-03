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
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ProfileRequestToKidsInfoFragment
 * Created by Administrator on 2017/12/2.
 */

public class ProfileRequestToKidsInfoFragment extends ProfileBaseFragment {
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

    public static ProfileRequestToKidsInfoFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileRequestToKidsInfoFragment fragment = new ProfileRequestToKidsInfoFragment();
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

        //todo test ui
        String requestKidsName = "Ben Smith";
        String requestKidsName2 = "requested";
        String note = getString(R.string.request_kids_note, requestKidsName);

        /*SpannableStringBuilder builder = new SpannableStringBuilder(note);
        ForegroundColorSpan lightBlueSpan = new ForegroundColorSpan(Color.parseColor("#14C0BD"));

        int start = note.indexOf(requestKidsName);
        int end = start + requestKidsName.length();

        builder.setSpan(lightBlueSpan,start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

        tv_note.setText(ViewUtils.setWordColorInStr(note,requestKidsName,requestKidsName2));

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

            if (!getActivity().isDestroyed()) {
//                mViewPhoto.setBitmap(bitmap);
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


}
