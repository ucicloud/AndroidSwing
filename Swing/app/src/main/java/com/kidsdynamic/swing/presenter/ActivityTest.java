package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kidsdynamic.swing.R;
import com.yy.base.BaseFragmentActivity;

import butterknife.ButterKnife;

/**
 * date:   2017/10/17 13:39 <br/>
 */

public class ActivityTest extends BaseFragmentActivity {
    // TODO: 2017/10/17 presenter目录存放界面相关类


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        ButterKnife.bind(this);

        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container1, new TestFragment())
                .commit();
    }
}
