package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.yy.base.BaseFragmentActivity;

public class VirtualResultActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_OK);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VirtualResultActivity.this.finish();
            }
        }, 30);
    }

}