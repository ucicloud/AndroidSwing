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
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

import butterknife.BindView;
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

    @BindView(R.id.calendarView)
    MaterialCalendarView materialCalendarView;

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

        initView();
        return layoutView;
    }

    public void changeFragmentTest(){

    }

    private void initView(){
        materialCalendarView.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS)
                .setFirstDayOfWeek(Calendar.MONDAY).commit();
    }

}
