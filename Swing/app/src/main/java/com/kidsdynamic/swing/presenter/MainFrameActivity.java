package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.model.WatchEvent;
import com.yy.base.BaseFragmentActivity;

import java.util.HashMap;
import java.util.Stack;

/**
 * 主界面
 * @data 2017年4月15日19:33:10
 */
public class MainFrameActivity extends BaseFragmentActivity {
    private View view_tab_device;
    private View view_tab_calendar;
    private View view_tab_dashboard;
    private View view_tab_profile;//我的

    private View [] tabViews = {view_tab_device,view_tab_calendar, view_tab_dashboard, view_tab_profile};
    private int [] tabViews_res_id = {R.id.main_console_device,  R.id.main_console_calendar,
            R.id.main_console_dashboard, R.id.main_control_profile};

//    private Fragment [] fragments = new Fragment[tabViews.length];

    private HashMap<Integer,Fragment> fragmentHashMap = new HashMap<>(tabViews.length);

    private int currentTabKey; // 当前Tab页面索引


    //用于CalendarFragment间的数据交换
//    public Stack<WatchContact> mContactStack;
    public Stack<Bundle> mCalendarBundleStack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame_main);

        initValue();

        //初始化4个fragemnt：
        //主界面中底部按钮，关联到fragment的切换加载

        //底部按钮，首先初始化，设置icon和text

        initView();

        //初始化fragment对象
        initFragments();

        //默认显示第三项
        tabViews[2].setSelected(true);
        addFragment(fragmentHashMap.get(R.id.main_console_dashboard),R.id.main_console_dashboard);
        currentTabKey = R.id.main_console_dashboard;
        /*addFragment(fragmentHashMap.get(R.id.main_console_device),R.id.main_console_device);
        currentTabKey = R.id.main_console_device;*/

    }

    private void initValue() {
        mCalendarBundleStack = new Stack<>();
    }

    private void initFragments(){

        fragmentHashMap.put(R.id.main_console_device, new FragmentDevice());
        fragmentHashMap.put(R.id.main_console_calendar,new CalendarContainerFragment());
        fragmentHashMap.put(R.id.main_console_dashboard,new DashboardContainerFragment());
        fragmentHashMap.put(R.id.main_control_profile,new ProfileContainerFragment());
    }

    private void initView() {
        initTabView();
    }

    private void initTabView(){
        int tabSize = tabViews.length;
        OnTabItemClickListener onTabItemClickListener = new OnTabItemClickListener();
        for (int i = 0; i < tabSize; i++) {
            tabViews[i] = findViewById(tabViews_res_id[i]);
            tabViews[i].setOnClickListener(onTabItemClickListener);
        }

    }

    private class OnTabItemClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //首先设置点击item未选中状态，其他的为非选择状态
            int clickedItemIndex = getTabItemIndexFromResId(v.getId());
            selectTabItem(clickedItemIndex);

            switch (v.getId()){
                case R.id.main_console_device://watch
                    switchShowFragment(R.id.main_console_device);
                    break;
                case R.id.main_console_calendar://calendar
                    switchShowFragment(R.id.main_console_calendar);
                    break;
                case R.id.main_console_dashboard:
                    switchShowFragment(R.id.main_console_dashboard);
                    break;
                case R.id.main_control_profile:
                    switchShowFragment(R.id.main_control_profile);
                    break;
            }
        }
    }

    private int getTabItemIndexFromResId(int resId){
        int itemIndex = -1;

        for (int index = 0; index < tabViews.length; index++) {
            if(tabViews_res_id[index] == resId){
                itemIndex = index;
                break;
            }
        }

        return itemIndex;
    }

    private void selectTabItem(int selectItemIndex){
        if(selectItemIndex < 0 || selectItemIndex >= tabViews.length){
            return;
        }

        tabViews[selectItemIndex].setSelected(true);
        for (int i = 0; i < tabViews.length; i++) {
            if(i == selectItemIndex){
                tabViews[i].setSelected(true);
            }else {
                tabViews[i].setSelected(false);
            }
        }

    }


    private void addFragment(Fragment fragment, int fragmentKey){

        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,fragment,String.valueOf(fragmentKey))
                    .commit();
        }
    }

    private void switchShowFragment(int fragmentKey){
        Fragment fragment = fragmentHashMap.get(fragmentKey);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        //首先暂停，并隐藏当前fragment
        getCurrentFragment().onPause();
        fragmentTransaction.hide(getCurrentFragment());

        //如果将要显示的fragment已经add，则resume，show
        if(fragment.isAdded()){
            fragment.onResume();
            fragmentTransaction.show(fragment);
        }else {
            //否则，add
            fragmentTransaction
                    .add(R.id.fragment_container,fragment,String.valueOf(fragmentKey));
        }

        //add 2017年10月29日16:35:44 weizg
        //切换界面前，弹出所以栈中历史fragment
        clearFragmentStack();

        fragmentTransaction.commit();

       currentTabKey = fragmentKey;
    }

    private void clearFragmentStack(){
        try{
            if(getSupportFragmentManager().getBackStackEntryCount() > 0){
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*private void setCurrentFragment(int fragmentKey) {

    }*/

    private Fragment getCurrentFragment(){
        return fragmentHashMap.get(currentTabKey);
    }



}
