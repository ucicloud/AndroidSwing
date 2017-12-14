package com.kidsdynamic.swing.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.host.model.SubHostRequests;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.RawActivityManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.view.ViewIntroductionAlarmList;
import com.kidsdynamic.swing.view.ViewIntroductionCalendarToday;
import com.kidsdynamic.swing.view.ViewIntroductionSync;
import com.kidsdynamic.swing.view.ViewIntroductionTodoDetail;
import com.yy.base.BaseFragmentActivity;

import java.util.HashMap;
import java.util.Stack;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 主界面
 *
 * @data 2017年4月15日19:33:10
 */
public class MainFrameActivity extends BaseFragmentActivity {
    private View view_tab_device;
    private View view_tab_calendar;
    private View view_tab_dashboard;
    private AvatarImageView view_tab_profile;

    protected View view_info;
    protected FrameLayout view_container;

    private View[] tabViews = {view_tab_device, view_tab_calendar, view_tab_dashboard, view_tab_profile};
    private int[] tabViews_res_id = {R.id.main_console_device, R.id.main_console_calendar,
            R.id.main_console_dashboard, R.id.main_control_profile};

//    private Fragment [] fragments = new Fragment[tabViews.length];

    private HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>(tabViews.length);

    private int currentTabKey; // 当前Tab页面索引


    //用于CalendarFragment间的数据交换
//    public Stack<WatchContact> mContactStack;
    public Stack<Bundle> mCalendarBundleStack;
    public Stack<String> mSignStack;

    public Stack<WatchEvent> mEventStack;
    public Stack<WatchContact> mWatchContactStack;
    public Stack<RequestAddSubHostEntity> mSubHostInfoEntity;
    public Stack<SubHostRequests> mSubHostList;

    public final static String UI_Update_Action = "MainFrame_UI_action";
    public final static String Tag_Key = "tag_key";
    public final static int Tag_Avatar_update = 1;

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
        addFragment(fragmentHashMap.get(R.id.main_console_dashboard), R.id.main_console_dashboard);
        currentTabKey = R.id.main_console_dashboard;
        /*addFragment(fragmentHashMap.get(R.id.main_console_device),R.id.main_console_device);
        currentTabKey = R.id.main_console_device;*/

        checkLoginState();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
        LogUtil2.getUtils().d("FireBase InstanceId->" + refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        if (TextUtils.isEmpty(token)) {
            return;
        }
        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getApplicationContext()).
                generateApi(UserApiNeedToken.class, true);
        userApiNeedToken.updateAndroidRegistrationId(token).enqueue(new BaseRetrofitCallback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void checkLoginState() {
        //如果登陆状态为false
        if (!ConfigUtil.isLoginState(getApplicationContext())) {
            LoginManager.clearAcvShowLogin();
        }
    }

    protected void viewInfoClick() {
        view_info.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFocusKidsAvatar();
    }

    private void loadFocusKidsAvatar() {
        DB_Kids focusWatchInfo = DeviceManager.getFocusWatchInfo(this.getApplicationContext());
        if (focusWatchInfo != null) {
            String profileRealUri = UserManager.getProfileRealUri(focusWatchInfo.getProfile());
            GlideHelper.showCircleImageViewWithSignature(
                    this, profileRealUri, String.valueOf(focusWatchInfo.getLastUpdate()),
                    view_tab_profile);
        }
    }


    private void initValue() {
        mCalendarBundleStack = new Stack<>();

        mEventStack = new Stack<>();

        mWatchContactStack = new Stack<>();

        mSubHostInfoEntity = new Stack<>();
        mSignStack = new Stack<>();
        mSubHostList = new Stack<>();


        //UI更新广播监听
        registerUIReceiver();
    }

    private void registerUIReceiver() {
        if (SwingApplication.localBroadcastManager != null) {
            IntentFilter intentFilter = new IntentFilter(UI_Update_Action);
            SwingApplication.localBroadcastManager.registerReceiver(UIChangeReceiver,
                    intentFilter);
        }
    }

    private BroadcastReceiver UIChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null) {
                return;
            }

            int update_type = intent.getIntExtra(Tag_Key, -1);

            if (update_type == Tag_Avatar_update) {
                if (!mWatchContactStack.isEmpty()) {
                    //todo 在二期功能前，消费该对象
                    WatchContact watchContact = mWatchContactStack.pop();
                    if (watchContact != null && watchContact.mPhoto != null) {
                        view_tab_profile.setBitmap(watchContact.mPhoto);
                    }

                    loadFocusKidsAvatar();
                }
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        SwingApplication.localBroadcastManager.unregisterReceiver(UIChangeReceiver);
    }

    private void initFragments() {

        fragmentHashMap.put(R.id.main_console_device, new FragmentDevice());
        fragmentHashMap.put(R.id.main_console_calendar, new CalendarContainerFragment());
        fragmentHashMap.put(R.id.main_console_dashboard, new DashboardContainerFragment());
        fragmentHashMap.put(R.id.main_control_profile, new ProfileContainerFragment());
    }

    private void initView() {
        view_tab_profile = findViewById(R.id.main_control_profile);
        initTabView();

       /* view_info = findViewById(R.id.view_bg);
        view_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewInfoClick();
            }
        });*/

        //功能引导UI显示
        view_container = findViewById(R.id.view_bg);
         /*ViewIntroductionSync viewIntroductionSync = new ViewIntroductionSync(this.getApplication());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        view_container.addView(viewIntroductionSync,layoutParams);
        view_container.setVisibility(View.VISIBLE);
        view_container.requestFocus();

        view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_container.setVisibility(View.INVISIBLE);
            }
        });*/
    }

    private void initTabView() {
        int tabSize = tabViews.length;
        OnTabItemClickListener onTabItemClickListener = new OnTabItemClickListener();
        for (int i = 0; i < tabSize; i++) {
            tabViews[i] = findViewById(tabViews_res_id[i]);
            tabViews[i].setOnClickListener(onTabItemClickListener);
        }

    }

    private class OnTabItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //首先设置点击item为选中状态，其他的为非选择状态
            int clickedItemIndex = getTabItemIndexFromResId(v.getId());
            selectTabItem(clickedItemIndex);

            switch (v.getId()) {
                case R.id.main_console_device://watch
                    switchShowFragment(R.id.main_console_device);
                    break;
                case R.id.main_console_calendar://calendar
                    switchShowFragment(R.id.main_console_calendar);
                    //因介绍界面效果不好，暂时不显示
//                    showIntroductionUI();
                    break;
                case R.id.main_console_dashboard:
                    uploadRawActivityInBack();
                    switchShowFragment(R.id.main_console_dashboard);
                    break;
                case R.id.main_control_profile:
                    switchShowFragment(R.id.main_control_profile);
                    break;
            }
        }
    }

    //根据需求，切换到dashboard界面时，后台上次一次activity数据
    private void uploadRawActivityInBack() {
        new RawActivityManager().uploadRawActivityInBack();
    }

    private void showIntroductionUI() {
        Boolean calendarFirstTime = PreferencesUtil.getInstance(getApplicationContext()).
                gPrefBooleanValue(ConfigUtil.calendar_first_time, true);
        view_container.setVisibility(View.VISIBLE);
        view_container.setBackgroundResource(R.drawable.calendar_landing_instructions);

        /*if(calendarFirstTime){
            view_info.setVisibility(View.VISIBLE);
            PreferencesUtil.getInstance(getApplicationContext()).
                    setPreferenceBooleanValue(ConfigUtil.calendar_first_time,false);
        }*/
    }

    //eventlist 界面的介绍页
    public void showAlarmIntroductionUI() {
        ViewIntroductionAlarmList viewIntroductionAlarmList = new ViewIntroductionAlarmList(this.getApplication());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        viewIntroductionAlarmList.setOnClickListener(new ViewIntroductionAlarmList.OnBtnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        addIntroductionView(viewIntroductionAlarmList, layoutParams);

        view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        PreferencesUtil.getInstance(getApplicationContext()).
                setPreferenceBooleanValue(ConfigUtil.event_list_first_time, false);
    }

    //eventlist 界面的介绍页
    public void showCalendarMonthIntroductionUI() {
        ViewIntroductionSync viewIntroductionSync = new ViewIntroductionSync(this.getApplication());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        viewIntroductionSync.setOnClickListener(new ViewIntroductionSync.OnBtnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        addIntroductionView(viewIntroductionSync, layoutParams);

        view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        PreferencesUtil.getInstance(getApplicationContext()).
                setPreferenceBooleanValue(ConfigUtil.calendar_month_first_time, false);
    }

    //todoDetail 界面的介绍页
    public void showTodoDetailIntroductionUI() {
        ViewIntroductionTodoDetail viewIntroductionTodoDetail = new ViewIntroductionTodoDetail(this.getApplication());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        viewIntroductionTodoDetail.setOnClickListener(new ViewIntroductionTodoDetail.OnBtnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        addIntroductionView(viewIntroductionTodoDetail, layoutParams);

        view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        PreferencesUtil.getInstance(getApplicationContext()).
                setPreferenceBooleanValue(ConfigUtil.todo_detail_first_time, false);
    }

    //calendar 首界面的介绍页
    public void showCalendarMainIntroductionUI() {
        ViewIntroductionCalendarToday viewIntroductionCalendar = new ViewIntroductionCalendarToday(this.getApplication());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        viewIntroductionCalendar.setOnClickListener(new ViewIntroductionCalendarToday.OnBtnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        addIntroductionView(viewIntroductionCalendar, layoutParams);

        view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIntroView();
            }
        });

        PreferencesUtil.getInstance(getApplicationContext()).
                setPreferenceBooleanValue(ConfigUtil.calendar_main_first_time, false);
    }

    private void addIntroductionView(ViewGroup viewIntroductionAlarmList,
                                     RelativeLayout.LayoutParams layoutParams) {
        view_container.addView(viewIntroductionAlarmList, layoutParams);
        view_container.setVisibility(View.VISIBLE);
        view_container.requestFocus();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (view_container.getVisibility() == View.VISIBLE) {
                hideIntroView();

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void hideIntroView() {
        view_container.setVisibility(View.INVISIBLE);
        view_container.removeAllViews();
        view_container.setOnClickListener(null);
    }

    private int getTabItemIndexFromResId(int resId) {
        int itemIndex = -1;

        for (int index = 0; index < tabViews.length; index++) {
            if (tabViews_res_id[index] == resId) {
                itemIndex = index;
                break;
            }
        }

        return itemIndex;
    }

    private void selectTabItem(int selectItemIndex) {
        if (selectItemIndex < 0 || selectItemIndex >= tabViews.length) {
            return;
        }

        tabViews[selectItemIndex].setSelected(true);
        for (int i = 0; i < tabViews.length; i++) {
            if (i == selectItemIndex) {
                tabViews[i].setSelected(true);
            } else {
                tabViews[i].setSelected(false);
            }
        }

    }


    private void addFragment(Fragment fragment, int fragmentKey) {

        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, String.valueOf(fragmentKey))
                    .commit();
        }
    }

    private void switchShowFragment(int fragmentKey) {
        Fragment fragment = fragmentHashMap.get(fragmentKey);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        //首先暂停，并隐藏当前fragment
        getCurrentFragment().onPause();
        fragmentTransaction.hide(getCurrentFragment());

        //如果将要显示的fragment已经add，则resume，show
        if (fragment.isAdded()) {
            fragment.onResume();
            fragmentTransaction.show(fragment);
        } else {
            //否则，add
            fragmentTransaction
                    .add(R.id.fragment_container, fragment, String.valueOf(fragmentKey));
        }

        //add 2017年10月29日16:35:44 weizg
        //切换界面前，弹出所以栈中历史fragment
        clearFragmentStack();

        fragmentTransaction.commit();

        currentTabKey = fragmentKey;
    }

    private void clearFragmentStack() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void setCurrentFragment(int fragmentKey) {

    }*/

    private Fragment getCurrentFragment() {
        return fragmentHashMap.get(currentTabKey);
    }


    public void switchToDashBoardFragment() {
        selectTabItem(R.id.main_console_dashboard);

        Fragment fragment = fragmentHashMap.get(R.id.main_console_dashboard);
        if (fragment instanceof DashboardContainerFragment) {
            ((DashboardContainerFragment) fragment).setType_goto(DashboardContainerFragment.type_goto_activityFragment);
        }

        switchShowFragment(R.id.main_console_dashboard);

        int clickedItemIndex = getTabItemIndexFromResId(R.id.main_console_dashboard);
        selectTabItem(clickedItemIndex);
    }

    public void setFragment(Fragment fragment, boolean isAddBackStack) {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof DashboardContainerFragment) {
            ((DashboardContainerFragment) currentFragment).setFragment(fragment, isAddBackStack);
        } else if (currentFragment instanceof CalendarContainerFragment) {
            ((CalendarContainerFragment) currentFragment).setFragment(fragment, isAddBackStack);
        }
    }

    public void selectFragment(String className, Bundle args, boolean isAddToBackStack) {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof DashboardContainerFragment) {
            ((DashboardContainerFragment) currentFragment).selectFragment(className, args, isAddToBackStack);
        } else if (currentFragment instanceof CalendarContainerFragment) {
            ((CalendarContainerFragment) currentFragment).selectFragment(className, args, isAddToBackStack);
        }
    }

}
