package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.ButterKnife;

/**
 * profile fragments container
 * date:   2017/10/28 11:53 <br/>
 */

public class ProfileContainerFragment extends BaseFragment {


    /*@BindView(R.id.main_toolbar_title)
    protected TextView tv_title;
    @BindView(R.id.main_toolbar_action1)
    protected ImageView view_left_action;
    @BindView(R.id.main_toolbar_action2)
    protected ImageView view_right_action;*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_profile_container, null);

        ButterKnife.bind(this,layoutView);


        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.w("profile","container onActivityCreated");

        initTitleBar();

        registerUIReceiver();

        selectFragment(ProfileMainFragment.class.getName(),null,false);
//        selectFragment(ProfileOptionFragment.class.getName(),null,false);

    }

    @Override
    public void onResume() {
        super.onResume();

//        selectFragment(ProfileMainFragment.class.getName(),null,false);

        Fragment fragmentById = getFragmentManager().findFragmentById(R.id.profile_fragment_container);
        if(fragmentById != null
                && fragmentById instanceof ProfileMainFragment){

            Log.w("profile","profileContainer onResume show ProfileMainFragment");
            fragmentById.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Fragment fragmentById = getFragmentManager().findFragmentById(R.id.profile_fragment_container);
        if(fragmentById != null
                && fragmentById instanceof ProfileMainFragment){

            Log.w("profile","profileContainer onPause show ProfileMainFragment");
            fragmentById.onPause();
        }
    }

    private void registerUIReceiver() {
        /*if(SwingApplication.localBroadcastManager != null){
            IntentFilter intentFilter = new IntentFilter(UI_Update_Action);
            SwingApplication.localBroadcastManager.registerReceiver(UIChangeReceiver,intentFilter);
        }*/
    }

    /*private BroadcastReceiver UIChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int update_type = intent.getIntExtra(UI_Update_Action_Type, -1);

           *//* if(UI_Action_Change_Event_detail == update_type){
                //进入当日日程详情，切换右上角按钮功能
                view_right_action.setImageResource(R.drawable.icon_pen);
                view_right_action.setTag(R.drawable.icon_pen);
            }else if(UI_Action_Change_Event_Add == update_type){
                view_right_action.setImageResource(R.drawable.icon_add);
                view_right_action.setTag(R.drawable.icon_add);
            }*//*
        }
    };*/

    private void initTitleBar() {
        /*tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_calendar);

        view_right_action.setImageResource(R.drawable.icon_add);
        view_right_action.setTag(R.drawable.icon_add);

        view_right_action.setOnClickListener(topbarRightBtnOnclick);*/
    }

    private View.OnClickListener topbarRightBtnOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            if (tag instanceof Integer) {
                Integer intTag = (Integer) tag;
                if(intTag == R.drawable.icon_add){
                    //添加新event
                }else if(intTag == R.drawable.icon_pen){
                    //修改event详情
                    Toast.makeText(getContext(),"modify event detail",Toast.LENGTH_SHORT).show();
                }
            }


        }
    };

    public void selectFragment(String className, Bundle args, boolean isAddToBackStack) {
        Fragment fragment = Fragment.instantiate(getContext(), className, args);

        /*Bundle bundle = new Bundle();
        String[] pageName = className.split("\\.");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName[pageName.length-1]);
        FirebaseLog(LogEvent.Event.SWITCH_PAGE, bundle);*/

        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction()
                .replace(R.id.profile_fragment_container, fragment, className);

        if(isAddToBackStack){
            fragmentTransaction
                    .addToBackStack(null);
        }

        fragmentTransaction .commit();

    }

    public void setFragment(Fragment fragment, boolean isAddBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction()
                .replace(R.id.profile_fragment_container, fragment);
        if (isAddBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

//        SwingApplication.localBroadcastManager.unregisterReceiver(UIChangeReceiver);
    }
}
