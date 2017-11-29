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
import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.kidsdynamic.swing.view.ViewCircle;
import com.yy.base.utils.ToastCommon;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ProfileKidsProfileFragment
 * Created by Administrator on 2017/11/29.
 */

public class ProfileKidsProfileFragment extends ProfileBaseFragment {
    private static final String TAG_KIDS_ID = "kids_id";

    @BindView(R.id.profile_photo)
    protected ViewCircle mViewPhoto;

    @BindView(R.id.tv_kids_name)
    protected TextView tv_kidsName;

    @BindView(R.id.tv_kids_id)
    protected TextView tv_kids_id;

    @BindView(R.id.btn_edit_kid_profile)
    protected Button btn_editKidsProfile;

    @BindView(R.id.btn_switch_to_account)
    protected Button btn_switchAccount;

    @BindView(R.id.btn_remove)
    protected Button btn_remove;

    private long kidsId;

    public static ProfileKidsProfileFragment newInstance(long kidsId) {
        Bundle args = new Bundle();
        args.putLong(TAG_KIDS_ID,kidsId);
        ProfileKidsProfileFragment fragment = new ProfileKidsProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null){
            kidsId = arguments.getLong(TAG_KIDS_ID, -1);
            if(kidsId == -1){
                getFragmentManager().popBackStack();
                ToastCommon.showToast(getContext(),"kids null");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_kids_profile, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();

        initView();
        return mView;

    }

    private void initView() {
        btn_editKidsProfile.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_switchAccount.setTypeface(SwingFontsCache.getBoldType(getContext()));
        btn_remove.setTypeface(SwingFontsCache.getBoldType(getContext()));
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_kids_profile);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

        DB_User currentLoginUserInfo = LoginManager.getCurrentLoginUserInfo();
        if(currentLoginUserInfo != null){
            tv_kids_id.setText(currentLoginUserInfo.getEmail());
            tv_kidsName.setText(LoginManager.getUserName(currentLoginUserInfo));

            GlideHelper.getBitMap(getContext(), UserManager.getProfileRealUri(currentLoginUserInfo.getProfile()),
                    String.valueOf(currentLoginUserInfo.getLastUpdate()), userAvatarSimpleTarget);
        }

    }

    private SimpleTarget<Bitmap> userAvatarSimpleTarget = new SimpleTarget<Bitmap>(){

        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {

            if (!getActivity().isDestroyed()) {
                mViewPhoto.setBitmap(bitmap);
            }
        }
    };


}
