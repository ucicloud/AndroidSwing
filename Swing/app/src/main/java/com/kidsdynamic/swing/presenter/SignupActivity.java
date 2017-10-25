package com.kidsdynamic.swing.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;

import com.kidsdynamic.swing.MainActivity;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.yy.base.BaseFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SignupActivity
 * <p>
 * Created by Stefan on 2017/10/23.
 */

public class SignupActivity extends BaseFragmentActivity {

    @BindView(R.id.ib_back)
    ImageButton ibBack;

    private Class mCurFragmentClz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        // TODO: 2017/10/25 weizg
        //读取配置，是否已经登陆成功，如果是，则不显示登陆界面，直接进入主界面
        if(ConfigUtil.isLoginState(this)){
            loginSuccess();
            return;
        }
    }

    private void loginSuccess() {
        // TODO: 2017/10/25
        //如果登录成功，则跳转到主界面
        //关闭当前界面，进入主界面
        startActivity(new Intent(this,MainActivity.class));

        this.finish();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setFragment(SignupStartFragment.newInstance());
    }

    @OnClick(R.id.ib_back)
    void clickBack() {
        if (null == mCurFragmentClz) {
            return;
        }
        if (SignupLoginFragment.class == mCurFragmentClz) {
            setFragment(SignupStartFragment.newInstance());
            setBackVisibility(View.INVISIBLE);
        } else if (SignupProfileFragment.class == mCurFragmentClz) {
            setFragment(SignupLoginFragment.newInstance());
            setBackVisibility(View.VISIBLE);
        }
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment)
//                .addToBackStack(null)
                .commit();
        mCurFragmentClz = fragment.getClass();
    }

    public void setBackVisibility(int visibility) {
        ibBack.setVisibility(visibility);
    }

}
