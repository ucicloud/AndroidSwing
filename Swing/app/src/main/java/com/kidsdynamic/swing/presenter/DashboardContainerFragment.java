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

import com.kidsdynamic.commonlib.utils.ObjectUtils;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.model.KidsEntityBean;

import java.util.List;

import butterknife.ButterKnife;

/**
 * dashboard fragments container
 * date:   2017/10/28 11:53 <br/>
 */

public class DashboardContainerFragment extends BaseFragment {

    private String type_goto = "-1";
    private Fragment currentFragment;
    public static final String type_goto_activityFragment = "1";

    private String ui_state_no = "123";
    private String ui_state_have = "456";
    private String ui_state = ui_state_no;

    public void setUIStateInit(){
        ui_state = ui_state_no;
    }



    /*@BindView(R.id.main_toolbar_title)
    protected TextView tv_title;
    @BindView(R.id.main_toolbar_action1)
    protected ImageView view_left_action;
    @BindView(R.id.main_toolbar_action2)
    protected ImageView view_right_action;*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_dashboard_container, null);

        ButterKnife.bind(this, layoutView);


        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initTitleBar();

        registerUIReceiver();

    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment fragmentById = getFragmentManager().findFragmentById(R.id.dashboard_fragment_container);
        Log.w("dash",">>>>>>>currentFragment: " + fragmentById);


        if(ui_state.equals(ui_state_have)){
            return;
        }

        //modify 2017年11月12日11:01:44 only_app
        //如果当前还未绑定kids，则进入到无设备界面；注意情况：绑定了设备，但未设置focus device
        long currentLoginUserId = LoginManager.getCurrentLoginUserId(getContext());

        // 修改为同时获取自己的设备和别人共享的设备
       /* List<KidsEntityBean> allKidsByUserId =
                DeviceManager.getAllKidsByUserId(getContext(), currentLoginUserId);*/
        List<KidsEntityBean> allKidsByUserId =
                DeviceManager.getAllKidsAndShared(getContext());


        if (ObjectUtils.isListEmpty(allKidsByUserId)) {

            // modify 2017年12月13日 Stefan
            // 进入到无设备界面后，会执行到WatchProfileFragment界面，需要return
            /*if (null != currentFragment && currentFragment instanceof WatchProfileFragment) {
                return;
            }*/

            Log.w("dash",">>>>>>>allKidsByUserId: ");

            selectFragment(DashboardNoDevicesFragment.class.getName(), null, false);
            ui_state = ui_state_have;
        } else {

            ui_state = ui_state_have;

            Log.w("dash",">>>>>>>ui_state_have: ");

            //modify 2017年12月5日19:31:19 only
            //r如果指定跳转到activity界面
            if (type_goto_activityFragment.equals(type_goto)) {
                Bundle args = new Bundle();
                args.putString(DashboardMainFragment.key_goto_type,
                        DashboardMainFragment.type_goto_DashboardEmotion);
                selectFragment(DashboardMainFragment.class.getName(), args, false);

                type_goto = "-1";
            } else {
                /*if (null != currentFragment && !(currentFragment instanceof DashboardMainFragment)) {
                    return;
                }*/
                selectFragment(DashboardMainFragment.class.getName(), null, false);
            }
        }

        Fragment fragmentById2 = getFragmentManager().findFragmentById(R.id.dashboard_fragment_container);
        Log.w("dash",">>>>>>>end currentFragment: " + fragmentById2);
    }

    public String getType_goto() {
        return type_goto;
    }

    public void setType_goto(String type_goto) {
        this.type_goto = type_goto;
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
                if (intTag == R.drawable.icon_add) {
                    //添加新event
                } else if (intTag == R.drawable.icon_pen) {
                    //修改event详情
                    Toast.makeText(getContext(), "modify event detail", Toast.LENGTH_SHORT).show();
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
                .replace(R.id.dashboard_fragment_container, fragment, className);

        if (isAddToBackStack) {
            fragmentTransaction
                    .addToBackStack(null);
        }

        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    public void setFragment(Fragment fragment, boolean isAddBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_fragment_container, fragment);
        if (isAddBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        SwingApplication.localBroadcastManager.unregisterReceiver(UIChangeReceiver);
    }
}
