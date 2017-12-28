package com.kidsdynamic.swing.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.utils.GlideHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * WatchAddSuccessFragment
 * <p>
 * Created by Stefan on 2017/10/26.
 */

public class WatchAddSuccessFragment extends BaseFragment {

    @BindView(R.id.watch_sync_photo)
    AvatarImageView vc_photo;

    @BindView(R.id.tv_kids_name)
    TextView tv_kids_name;

    private String mAvatarProfile = "";
    private String mAvatarLocalFilePath = "";
    private String deviceName;
    private long devId;

    public static WatchAddSuccessFragment newInstance() {
        Bundle args = new Bundle();
        WatchAddSuccessFragment fragment = new WatchAddSuccessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null) {
            mAvatarProfile = getArguments().getString(DeviceManager.BUNDLE_KEY_AVATAR, "");
            deviceName = getArguments().getString(DeviceManager.BUNDLE_KEY_KID_NAME, "");
            devId = getArguments().getLong(DeviceManager.BUNDLE_KEY_KID_ID, -1);
            mAvatarLocalFilePath = getArguments().getString(DeviceManager.BUNDLE_KEY_AVATAR_FILE, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_add_success, container, false);
        ButterKnife.bind(this, layout);

        tv_kids_name.setText(deviceName);

        if(!TextUtils.isEmpty(mAvatarLocalFilePath)){
            File avatarFile = new File(mAvatarLocalFilePath);
            GlideHelper.getBitMapOnlyCacheInMemory(getContext(),avatarFile,
                    new AvatarSimpleTargetImageView(vc_photo));
        }

        //自己的设备
        KidsEntityBean kidsInfo = DeviceManager.getKidsInfo(getContext(), devId);
        if(kidsInfo != null && !TextUtils.isEmpty(kidsInfo.getProfile())){

            GlideHelper.getBitMapWithWH(getContext().getApplicationContext(),
                    UserManager.getProfileRealUri(mAvatarProfile),
                    kidsInfo.getLastUpdate()+"",
                    vc_photo.getWidth(),vc_photo.getHeight(),
                    new AvatarSimpleTargetImageView(vc_photo));
        }
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /* @OnClick(R.id.watch_sync_yes)
    public void yes() {

    }*/

    @OnClick(R.id.watch_go_to_dashboard)
    public void gotoDashboard() {

        FragmentActivity activity = getActivity();
        if(activity instanceof MainFrameActivity){
            ((MainFrameActivity) activity).
                    switchToDashBoardFragment();
        }else {

            getActivity().finish();
            //跳转到主界面
            startActivity(new Intent(getActivity(),MainFrameActivity.class));
        }

    }

    @OnClick(R.id.watch_add_other)
    public void add() {

    }

}
