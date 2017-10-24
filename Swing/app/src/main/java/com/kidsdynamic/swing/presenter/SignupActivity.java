package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.kidsdynamic.swing.R;
import com.yy.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

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
