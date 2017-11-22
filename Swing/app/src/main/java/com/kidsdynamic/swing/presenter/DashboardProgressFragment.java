package com.kidsdynamic.swing.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.net.user.model.UserProfileRep;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.ble.ActivityModel;
import com.kidsdynamic.swing.ble.EventModel;
import com.kidsdynamic.swing.ble.IDeviceScanCallback;
import com.kidsdynamic.swing.ble.IDeviceSyncCallback;
import com.kidsdynamic.swing.ble.SwingBLEService;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.EventManager;
import com.kidsdynamic.swing.domain.KidActivityManager;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.RawActivityManager;
import com.kidsdynamic.swing.model.KidsEntityBean;
import com.kidsdynamic.swing.model.WatchEvent;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.view.ViewCircle;
import com.vise.baseble.model.BluetoothLeDevice;
import com.yy.base.utils.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * DashboardProgressFragment </br></br>
 * Created by only_app.
 */

public class DashboardProgressFragment extends DashboardBaseFragment {
    private final int PROGRESS_INTERVAL = 100;

    private final static int SEARCH_TIMEOUT = 150;     // 150 * PROGRESS_INTERVAL milliseconds
    private int mSearchTimeout;
    private final static int SYNC_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mSyncTimeout;
    private final static int UPLOAD_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mUploadTimeout;
    private final static int DOWNLOAD_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mDownloadTimeout;


    private MainFrameActivity mActivityMain;
    private View mViewMain;

    @BindView(R.id.view_sync_complete)
    protected View view_sync_complete;
    @BindView(R.id.tv_message)
    protected TextView mViewLabel;
    @BindView(R.id.dashboard_progress_button_first)
    protected TextView mViewButton1;
    @BindView(R.id.dashboard_progress_button_second)
    protected TextView mViewButton2;
    @BindView(R.id.dashboard_progress_circle)
    protected ViewCircle mViewProgress;

    private KidsEntityBean mDevice;
    private String mMacAddress;
    private BluetoothLeDevice mSearchWatchResult = null;
//    private List<WatchVoiceAlertEntity> mVoiceAlertList;
    private List<EventModel> mVoiceAlertList;
    private final static int SYNC_STATE_INIT = 0;
    private final static int SYNC_STATE_SUCCESS = 1;
    private final static int SYNC_STATE_FAIL = 2;
    private int mSyncState = SYNC_STATE_INIT;
    private boolean mActivityUpdateFinish = false;
    private boolean mServerSyncFinish = false;
    private BluetoothAdapter mBluetoothAdapter;

    private int REQUEST_ENABLE_BT = 1;
    private boolean devicesEnabled = true;

    private SwingBLEService mBluetoothService;
    private int mBindBlePurpose = -1;
    //绑定蓝牙service目的--搜索设备
    private final int bindBlePurpose_search_device = 1;
    //绑定蓝牙service目的--同步数据
    private final int bindBlePurpose_sync_data = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMain = (MainFrameActivity) getActivity();

        mBluetoothAdapter = ((BluetoothManager) mActivityMain.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            devicesEnabled = false;
        }

        final LocationManager manager = (LocationManager) mActivityMain.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Message", "Your GPS seems to be disabled, do you want to enable it?");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dashboard_progress_enable_gps)
                    .setCancelable(false)
                    .setPositiveButton(R.string.watch_have_yes, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.watch_have_no, new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

            devicesEnabled = false;
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_dashboard_progress, container, false);

        ButterKnife.bind(this,mViewMain);
        initTitleBar();
       /* Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Sync Start");
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/
        return mViewMain;
    }


    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_sync);
        view_left_action.setImageResource(R.drawable.icon_left);

    }

    @Override
    public void onResume() {
        super.onResume();

        //todo push server
        /*Intent intent = new Intent(mActivityMain, ServerPushService.class);
        intent.putExtra("PAUSE", true);
        mActivityMain.startService(intent);*/

        /*mDevice = mActivityMain.mContactStack.isEmpty() ?
                mActivityMain.mOperator.getFocusKid() :
                (WatchContact.Kid) mActivityMain.mContactStack.pop();*/

        mDevice = DeviceManager.getFocusKidsInfo(getContext());

        if (mDevice == null)
            mDevice = new KidsEntityBean();

        //获取待同步数据
        List<WatchEvent> eventList = EventManager.getEventsForSync(mDevice);

        mVoiceAlertList = new ArrayList<>();
        for (WatchEvent event : eventList) {
//            mVoiceAlertList.add(new WatchVoiceAlertEntity((byte) event.mAlert, event.mAlertTimeStamp));
            EventModel eventModel = new EventModel();
            eventModel.setAlert(event.mAlert);
            eventModel.setStartDate(new Date(event.mAlertTimeStamp));
            mVoiceAlertList.add(eventModel);
        }

        mMacAddress = DeviceManager.getMacAddress(mDevice.getMacId());
        Log.d("swing", "Kid mac " + mMacAddress);

        //开始界面等待效果，并先与服务器获取数据，然后向手表同步数据
        viewSync();

        mServerSyncFinish = false;
        //先执行用户信息的同步
        syncUserData();
    }

    @Override
    public void onPause() {
        // TODO: 2017/11/12 ble
        /*bleSearchCancel();
        mActivityMain.mBLEMachine.Cancel();

        if (!mServerSyncFinish)
            mActivityMain.mServiceMachine.Restart();*/

        //todo push service
       /* Intent intent = new Intent(mActivityMain, ServerPushService.class);
        mActivityMain.startService(intent);*/

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBluetoothService != null) {
            mBluetoothService.cancelScan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            unbindService();
        }
    }

    //从服务端获取用户数据，更新本地缓存
    private void syncUserData(){
        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(
                getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class, true);

        userApiNeedToken.retrieveUserProfile().enqueue(new BaseRetrofitCallback<UserProfileRep>() {
            @Override
            public void onResponse(Call<UserProfileRep> call, Response<UserProfileRep> response) {
                LogUtil2.getUtils().d("retrieveUserProfile onResponse");

                if (response.code() == 200) {//获取到用户信息
                    //先清除本地数据，然后再保存
                    new LoginManager().saveLoginData(getContext(), response.body());
                }

                //设置同步用户数据已经完成
                mServerSyncFinish = true;

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserProfileRep> call, Throwable t) {
                Log.d("syncData", "retrieveUserProfile error, ");
                super.onFailure(call, t);

                //设置同步用户数据已经完成
                mServerSyncFinish = true;
            }
        });
    }

    //与服务器同步数据结果listener
   /* WatchOperator.finishListener mFinishListener = new WatchOperator.finishListener() {
        @Override
        public void onFinish(Object arg) {
            mServerSyncFinish = true;
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mServerSyncFinish = true;
        }
    };*/


    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        getFragmentManager().popBackStack();
    }

    private int mServerSyncState = 0;
    private ViewCircle.OnProgressListener mServerSyncProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            switch(mServerSyncState) {
                case 0:
                    if (mServerSyncFinish) {

                        /*Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Server Sync Finished");
                        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/

                        //开始从服务器获取activity数据
                        new KidActivityManager().getActivityDataFromCloud
                                (getContext(),mDevice.getKidsId(),mActivityUpdateListener);
                        mServerSyncState = 1;
                    }
                    break;
                case 1:
                    if (mActivityUpdateFinish) {
                        viewSearching();
                        bleSearchStart();
                        mServerSyncState = 2;
                    }
                    break;
                case 2:
                    mServerSyncFinish = false;
                    mActivityUpdateFinish = false;
                    mServerSyncState = 0;
                    break;
            }
        }
    };


    //搜索watch回调，viewCircle间隔固定时间回调该监听
    private ViewCircle.OnProgressListener mSearchProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mSearchTimeout--;

            //watch search result listener
            if (mSearchWatchResult != null) {
                viewFound();
            } else if (mSearchTimeout == 0) {
                /*Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Search Swing Watch Timeout");
                mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/

                viewNotFound(R.string.dashboard_progress_not_found);
                bleSearchCancel();
            }
        }
    };

    private ViewCircle.OnProgressListener mSyncProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            //mSyncTimeout--;

            if (mSyncState == SYNC_STATE_SUCCESS) {
                viewCompleted();
            } else if (mSyncTimeout == 0 || mSyncState == SYNC_STATE_FAIL) {
                viewNotFound(R.string.dashboard_progress_sync_fail);
                bleSyncCancel();
            }
        }
    };

    private ViewCircle.OnProgressListener mUploadProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mUploadTimeout--;

            if (true) {
                // Gio Say: asynchronous upload
                viewDownload();

            } else if (mUploadTimeout == 0) {
                viewServerFailed();
            }
        }
    };

    private ViewCircle.OnProgressListener mDownloadProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mDownloadTimeout--;

            if (mActivityUpdateFinish) {
                viewCompleted();
            } else if (mDownloadTimeout == 0) {
                viewServerFailed();
            }
        }
    };

    private Button.OnClickListener mAgainListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewSearching();
            bleSearchStart();
        }
    };

    private Button.OnClickListener mExitListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            //应该切换到dash fragment中
            mActivityMain.switchToDashBoardFragment();
//            selectFragment(DashboardEmotionFragment.class.getName(), null,true);
        }
    };

    private Button.OnClickListener mSyncListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewSyncing();
            bleSyncStart();
        }
    };

    private void bleSearchStart() {
        mSearchWatchResult = null;
        if (mBluetoothService == null) {
            bindService(bindBlePurpose_search_device);
        }else {
            //start search dev
            searchWatch();

        }
        /*mActivityMain.mBLEMachine.Search(mOnSearchListener, mMacAddress);
        mSearchResult = null;*/
    }

    private void searchWatch(){
        mBluetoothService.closeConnect();
        //一直扫描，直到该界面被压栈或是被关闭
        mBluetoothService.scanDevice(0, new IDeviceScanCallback() {
            @Override
            public void onStartScan() {

            }

            @Override
            public void onScanTimeOut() {

            }

            @Override
            public void onScanning(BluetoothLeDevice scanResult) {

                Log.d("dashProgress", "onScanning " + scanResult);
                //如果查找到指定的设备
                if(scanResult != null
                        && scanResult.getAddress().equals(mMacAddress)){
                    mSearchWatchResult = scanResult;
                }
            }
        });
    }

    private void bleSearchCancel() {
//        mActivityMain.mBLEMachine.Search(null, "");
        if(mBluetoothService != null){
            mBluetoothService.cancelScan();
        }
    }

    private void bleSyncStart() {

        //开始向watch同步数据 todo
        /*if (mSearchResult != null)
            mActivityMain.mBLEMachine.Sync(mOnSyncListener, mSearchResult, mVoiceAlertList);*/
        mSyncState = SYNC_STATE_INIT;

        if(mBluetoothService == null){
            bindService(bindBlePurpose_sync_data);
        }else {
            syncData2Watch();
        }
    }

    private void syncData2Watch(){
//        List<EventModel> list = genEvents();

        mBluetoothService.closeConnect();
        mBluetoothService.scanAndSync2(mMacAddress, mVoiceAlertList, new IDeviceSyncCallback() {
            @Override
            public void onSyncComplete() {
                LogUtil.getUtils().d("sync data complete: ok");
                //与watch同步数据完成后，开始逐条上传数据

                new RawActivityManager().uploadRawActivity(mDevice.getMacId(),
                        mUpdateRawActivityListener);
                //向服务器上传数据完成后，设置成功标志位
               /* mSyncState = SYNC_STATE_SUCCESS;
                mActivityMain.mBLEMachine.FirmwareVersion(mOnFirmwareListener, mSearchResult.mAddress);*/
            }

            @Override
            public void onSyncFail(int reason) {
                LogUtil.getUtils().d("sync data complete: fail, reason: " + reason);
                //sync fail后，需要修改标志位
                mSyncState = SYNC_STATE_FAIL;
            }

            @Override
            public void onSyncing(String tip) {
                //test fun; no need care

            }

            @Override
            public void onSyncActivity(ActivityModel activity) {

                Log.d("dashProgress", "activity " + activity);

                //首先把这些数据保存到数据库；然后同步完成，向服务器获取上传数据
                RawActivityManager.saveRawActivity(activity);
            }

            @Override
            public void onDeviceBattery(int battery) {
                Log.d("dashProgress", "onDeviceBattery battery " + battery);
                DeviceManager.saveBindWatchBattery(mDevice.getMacId(), battery);

            }
        });
    }

    private void bleSyncCancel() {
//        mActivityMain.mBLEMachine.Disconnect();
    }

    private void viewSync() {
        mViewLabel.setText(getResources().getString(R.string.signup_profile_processing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.setOnProgressListener(mServerSyncProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewSearching() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_searching));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mSearchTimeout = SEARCH_TIMEOUT;
        mViewProgress.setOnProgressListener(mSearchProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewFound() {
        /*Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Watch Found");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_found));
        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_sync_now));

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mSyncListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    private void viewUpload() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_syncing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mUploadTimeout = UPLOAD_TIMEOUT;
        mViewProgress.setOnProgressListener(mUploadProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewDownload() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_syncing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mDownloadTimeout = DOWNLOAD_TIMEOUT;
        mViewProgress.setOnProgressListener(mDownloadProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewSyncing() {
        /*Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Start to Sync Data");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_syncing));

        mViewButton1.setVisibility(View.INVISIBLE);
        mViewButton1.setOnClickListener(null);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mSyncTimeout = SYNC_TIMEOUT;
        mViewProgress.setOnProgressListener(mSyncProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewCompleted() {
        /*Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Sync Data Completed");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_completed));
        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_dashboard));

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mExitListener);

        mViewButton2.setVisibility(View.INVISIBLE);
        mViewButton2.setOnClickListener(null);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(true);

        mViewLabel.setVisibility(View.INVISIBLE);
        mViewProgress.setVisibility(View.INVISIBLE);
        view_sync_complete.setVisibility(View.VISIBLE);
    }

    private void viewNotFound(int errMsgStrId) {
       /* Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Swing Watch Not Found");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/

//        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_not_found));
        mViewLabel.setText(getResources().getString(errMsgStrId));

        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_again));
        mViewButton2.setText(getResources().getString(R.string.dashboard_progress_last));

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mAgainListener);

        mViewButton2.setVisibility(View.VISIBLE);
        mViewButton2.setOnClickListener(mExitListener);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    private void viewServerFailed() {
       /* Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Can't upload data to server");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mDevice.mMacId);
        mActivityMain.FirebaseLog(LogEvent.Event.SYNC_WATCH_DATA, bundle);*/

        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_server_not_reach));

        mViewButton1.setText(getResources().getString(R.string.dashboard_progress_again));
        mViewButton2.setText(getResources().getString(R.string.dashboard_progress_last));

        mViewButton1.setVisibility(View.VISIBLE);
        mViewButton1.setOnClickListener(mAgainListener);

        mViewButton2.setVisibility(View.VISIBLE);
        mViewButton2.setOnClickListener(mExitListener);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    //查找watch业务的listener
    /*BLEMachine.onSearchListener mOnSearchListener = new BLEMachine.onSearchListener() {
        @Override
        public void onSearch(ArrayList<BLEMachine.Device> result) {
            for (BLEMachine.Device dev : result) {
                if (dev.mAddress.equals(mMacAddress)) {
                    mSearchResult = dev;
                    break;
                }
            }
        }
    };*/

    // 向watch同步数据业务的listener
   /* BLEMachine.onSyncListener mOnSyncListener = new BLEMachine.onSyncListener() {
        @Override
        public void onSync(int resultCode, ArrayList<WatchActivityRaw> result) {
            if (resultCode == SYNC_RESULT_SUCCESS) {
                for (WatchActivityRaw res : result) {
                    mActivityMain.mOperator.pushUploadItem(res);
                }
                mSyncState = SYNC_STATE_SUCCESS;
                mActivityMain.mBLEMachine.FirmwareVersion(mOnFirmwareListener, mSearchResult.mAddress);
            } else {
                // Todo : first connect?
                mSyncState = SYNC_STATE_FAIL;
            }

        }

        @Override
        public void onBattery(WatchBattery battery) {
            Log.d("Battery: ",  String.valueOf(battery));
            mActivityMain.mOperator.mFocusBatteryName = mDevice.mName;
            mActivityMain.mOperator.mFocusBatteryValue = battery.batteryLife;

            mActivityMain.mOperator.pushUploadBattery(battery);
        }
    };*/

    //获取watch firmversion操作 listener
    /*BLEMachine.onFirmwareListener mOnFirmwareListener = new BLEMachine.onFirmwareListener() {
        @Override
        public void onFirmwareVersion(String firmwareVersion) {
            Log.d("Firmwareversion: ",  firmwareVersion + " " + ServerMachine.getMacID(mSearchResult.mAddress));

            mSearchResult.mFirmwareVersion = firmwareVersion;
            mActivityMain.mServiceMachine.deviceFirmwareUpload(firmwareVersion, ServerMachine.getMacID(mSearchResult.mAddress));
            mActivityMain.mOperator.updateFirmwareVersion(firmwareVersion, ServerMachine.getMacID(mSearchResult.mAddress));
        }
    };*/

    //从服务端获取activity数据业务结果的listener
    KidActivityManager.IFinishListener mActivityUpdateListener = new KidActivityManager.IFinishListener() {
        @Override
        public void onFinish(Object arg) {
            mActivityUpdateFinish = true;
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mActivityUpdateFinish = true;
            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    //从服务端获取activity数据业务结果的listener
    RawActivityManager.IFinishListener mUpdateRawActivityListener = new RawActivityManager.IFinishListener() {
        @Override
        public void onFinish(Object arg) {
            mSyncState = SYNC_STATE_SUCCESS;
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mSyncState = SYNC_STATE_FAIL;
            Log.d("dashboardProgress","upload raw activity fail: " + Command);
//            Toast.makeText(mActivityMain, Command, Toast.LENGTH_SHORT).show();
        }
    };

    //绑定蓝牙服务
    private void bindService(int bindPurpose) {
        mBindBlePurpose = bindPurpose;

        Context context = getContext();
        Intent bindIntent = new Intent(context, SwingBLEService.class);
        context.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    //解绑定蓝牙服务
    private void unbindService() {
        Context context = getContext();
        context.unbindService(mFhrSCon);
    }

    //蓝牙服务连接成功后，回调接口
    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((SwingBLEService.BluetoothBinder) service).getService();

            if(mBindBlePurpose == bindBlePurpose_search_device){//搜索设备
                searchWatch();
            }else if(mBindBlePurpose == bindBlePurpose_sync_data){//同步数据
                syncData2Watch();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };
}
