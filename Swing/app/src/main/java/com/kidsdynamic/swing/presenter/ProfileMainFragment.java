package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ProfileMainFragment
 * Created by Administrator on 2017/11/1.
 */

public class ProfileMainFragment extends ProfileBaseFragment {

    @BindView(R.id.profile_option_logout)
    protected View view_logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_profile_option, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();
        return mView;

    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_option);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

    }


    @OnClick(R.id.profile_option_logout)
    protected void logout(){
        //show dialog to confirm
        selectFragment(LogoutConfirmFragment.class.getName(),null,true);
    }

}
