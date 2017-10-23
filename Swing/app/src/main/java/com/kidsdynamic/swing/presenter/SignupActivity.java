package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.kidsdynamic.swing.R;
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

    private String mCurFragmentClzName;

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
        if (!TextUtils.isEmpty(mCurFragmentClzName)
                && SignupLoginFragment.class.getSimpleName().equals(mCurFragmentClzName)) {
            setFragment(SignupStartFragment.newInstance());
            setBackVisibility(View.INVISIBLE);
        }
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment)
//                .addToBackStack(null)
                .commit();
        mCurFragmentClzName = fragment.getClass().getSimpleName();
    }

    public void setBackVisibility(int visibility) {
        ibBack.setVisibility(visibility);
    }

}
