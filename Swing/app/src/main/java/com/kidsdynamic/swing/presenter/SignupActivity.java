package com.kidsdynamic.swing.presenter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.yy.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * SignupActivity
 * <p>
 * Created by Stefan on 2017/10/23.
 */

public class SignupActivity extends BaseFragmentActivity {
    private static final int REQUEST_CODE_BLE = 11282;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        // TODO: 2017/11/4  测试界面
        //读取配置，是否已经登陆成功，如果是，则不显示登陆界面，直接进入主界面
        if (ConfigUtil.isLoginState(this)) {
            loginSuccess();
            return;
        }

        //登陆时，新增位置权限是否获取检测
        checkPermissions();
    }


    private void loginSuccess() {
        //如果登录成功，则跳转到主界面
        //关闭当前界面，进入主界面
        startActivity(new Intent(this, MainFrameActivity.class));

        this.finish();
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }

        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_BLE);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                break;
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_BLE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setFragment(SignupStartFragment.newInstance(), false);

        //below test fragment
//        setFragment(WatchSelectFragment.newInstance());
//        setFragment(WatchHaveFragment.newInstance());
//        setFragment(SignupStartFragment.newInstance());

        /*Bundle bundle = new Bundle();
        bundle.putString(DeviceManager.BUNDLE_KEY_KID_NAME,
                "Mary");
        bundle.putString(DeviceManager.BUNDLE_KEY_AVATAR,
                UserManager.getProfileRealUri("kid_avatar_13.jpg"));
        selectFragment(WatchAddSuccessFragment.class.getName(),bundle);*/
    }

    public void selectFragment(String className, Bundle args) {
        Fragment fragment = Fragment.instantiate(this, className, args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signup_activity_container, fragment, className)
                .addToBackStack(null)
                .commit();
    }

    public void setFragment(Fragment fragment, boolean isAddBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.signup_activity_container, fragment);
        if (isAddBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

}
