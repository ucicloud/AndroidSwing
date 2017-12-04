package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.utils.GlideHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * DashboardMainFragment
 * <p>
 * Created by Stefan on 2017/10/26.
 */

public class DashboardMainFragment extends DashboardBaseFragment {

    @BindView(R.id.watch_sync_photo)
    AvatarImageView vc_photo;

    @BindView(R.id.tv_kids_name)
    TextView tv_kids_name;

    private String mAvatarFilename = "";
    private String kidName;

    public static DashboardMainFragment newInstance() {
        Bundle args = new Bundle();
        DashboardMainFragment fragment = new DashboardMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null) {
            mAvatarFilename = getArguments().getString(DeviceManager.BUNDLE_KEY_AVATAR, "");
            kidName = getArguments().getString(DeviceManager.BUNDLE_KEY_KID_NAME, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_main, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTitleBar();
        //just test
        DB_Kids focusWatchInfo = DeviceManager.getFocusWatchInfo(getContext());
        if (focusWatchInfo != null) {
            kidName = focusWatchInfo.getName();
            tv_kids_name.setText(kidName);

            String profileRealUri = UserManager.getProfileRealUri(focusWatchInfo.getProfile());
//            Log.d("dash_main", "avatar: " + profileRealUri);
//            GlideHelper.showCircleImageView(getContext(), profileRealUri, vc_photo);
            GlideHelper.showCircleImageViewWithSignature(
                    getContext(),
                    profileRealUri,String.valueOf(focusWatchInfo.getLastUpdate()),
                    vc_photo);
        }

    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_sync);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/
    }

    @OnClick(R.id.watch_sync_yes)
    public void yes() {
        setFragment(DashboardProgressFragment.newInstance(), true);
    }

    @OnClick(R.id.watch_sync_no)
    public void no() {
        /*getActivity().finish();
        //跳转到主界面
        startActivity(new Intent(getActivity(),MainFrameActivity.class));*/
        setFragment(DashboardEmotionFragment.newInstance(), true);
    }

    @OnClick(R.id.watch_sync_add)
    public void add() {

    }

}
