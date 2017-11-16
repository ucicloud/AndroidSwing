package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidsdynamic.data.dao.DB_User;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.RawActivityManager;
import com.kidsdynamic.swing.utils.ConfigUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * LogoutConfirmFragment
 * Created by Administrator on 2017/11/1.
 */

public class LogoutConfirmFragment extends ProfileBaseFragment {

    @BindView(R.id.tv_logout_account_name)
    protected TextView tv_accountName;

    @BindView(R.id.tv_account_email)
    protected TextView tv_accountEmail;

    @BindView(R.id.btn_confirm_logout)
    protected View layout_logout;

    @BindView(R.id.btn_cancel)
    protected View layout_cancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_logout_confirm, container, false);

        ButterKnife.bind(this,mView);
        initTitleBar();
        return mView;

    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_option_logout);
        view_left_action.setImageResource(R.drawable.icon_left);

        /*view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);*/

        DB_User currentLoginUserInfo = LoginManager.getCurrentLoginUserInfo();
        if(currentLoginUserInfo != null){
            tv_accountEmail.setText(currentLoginUserInfo.getEmail());
            tv_accountName.setText(getUserName(currentLoginUserInfo));
        }

    }

    private String getUserName(DB_User currentLoginUserInfo){
        String firstName = currentLoginUserInfo.getFirstName();
        String lastName = currentLoginUserInfo.getLastName();
        if(!TextUtils.isEmpty(firstName)
                && !TextUtils.isEmpty(lastName)){
            return firstName + " " + lastName;
        }

        if(!TextUtils.isEmpty(firstName)){
            return firstName;
        }

        if(!TextUtils.isEmpty(lastName)){
            return lastName;
        }

        return "";
    }
    @OnClick(R.id.btn_confirm_logout)
    protected void logout(){
        //show dialog to confirm
        //del token and

        //请求缓存数据
        RawActivityManager.delAllRawActivity();

        ConfigUtil.logoutState();
        getActivity().finish();

    }

    @OnClick(R.id.btn_cancel)
    protected void cancelLogout(){
        getFragmentManager().popBackStack();
    }
}
