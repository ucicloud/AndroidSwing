package com.kidsdynamic.swing.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.yy.base.BaseFragmentActivity;

import butterknife.ButterKnife;

/**
 * SignupActivity
 * <p>
 * Created by Stefan on 2017/10/23.
 */

public class SignupActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        // TODO: 2017/11/4  测试界面
        //读取配置，是否已经登陆成功，如果是，则不显示登陆界面，直接进入主界面
        if(ConfigUtil.isLoginState(this)){
            loginSuccess();
            return;
        }

    }

    private void loginSuccess() {
        //如果登录成功，则跳转到主界面
        //关闭当前界面，进入主界面
        startActivity(new Intent(this,MainFrameActivity.class));

        this.finish();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // TODO: 2017/11/4 test
        setFragment(SignupStartFragment.newInstance());
//        setFragment(WatchSelectFragment.newInstance());
//        setFragment(WatchHaveFragment.newInstance());
//        setFragment(SignupStartFragment.newInstance());
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment)
//                .addToBackStack(null)
                .commit();
    }

    public void setFragmentAndAddBackStack(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

}
