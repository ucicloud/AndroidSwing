package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/4/15.
 */

public class TestFragment_2 extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    View myAnswerView;
    View view_tobeProfessor;
    View view_myAsk;

    TextView tv_userName;
    TextView tv_myAskCount;
    TextView tv_myWealthNum;

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//         super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_mine, null);
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_test_2, null);
//        layoutView.findViewById(R.id.)

        ButterKnife.bind(this,layoutView);


        return layoutView;
    }

    public void changeFragmentTest(){

    }


}
