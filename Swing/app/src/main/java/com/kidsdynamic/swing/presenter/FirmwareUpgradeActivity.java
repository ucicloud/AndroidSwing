package com.kidsdynamic.swing.presenter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.commonlib.utils.FileUtil;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.ble.ActivityModel;
import com.kidsdynamic.swing.ble.IDeviceScanCallback;
import com.kidsdynamic.swing.ble.IDeviceSyncCallback;
import com.kidsdynamic.swing.ble.SwingBLEService;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.domain.RawActivityManager;
import com.kidsdynamic.swing.view.ViewCircle;
import com.vise.baseble.model.BluetoothLeDevice;
import com.yy.base.BaseFragmentActivity;
import com.yy.base.utils.LogUtil;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * FirmwareUpgradeActivity
 * <p>
 * Created by Stefan on 2018/1/23.
 */

public class FirmwareUpgradeActivity extends BaseFragmentActivity {

    private static final int REQUEST_CODE_BLE = 1128;

    private final int PROGRESS_INTERVAL = 10;

    private final static int SEARCH_TIMEOUT = 300;     // 300 * PROGRESS_INTERVAL milliseconds
    private int mSearchTimeout;
    private final static int SYNC_TIMEOUT = 1000;      // 1000 * PROGRESS_INTERVAL milliseconds
    private int mSyncTimeout;

    @BindView(R.id.main_toolbar_title)
    protected TextView tv_title;
    @BindView(R.id.main_toolbar_action1)
    protected ImageView view_left_action;

    @BindView(R.id.firmware_upgrade_label)
    protected TextView mViewLabel;
    @BindView(R.id.firmware_upgrade_button)
    protected TextView mViewButton;
    @BindView(R.id.firmware_upgrade_circle)
    protected ViewCircle mViewProgress;
    @BindView(R.id.firmware_upgrade_hint)
    protected TextView mViewHint;

    private String mMacId;
    private String mMacAddress;
    private BluetoothLeDevice mSearchWatchResult = null;
    private final static int SYNC_STATE_INIT = 0;
    private final static int SYNC_STATE_SUCCESS = 1;
    private final static int SYNC_STATE_FAIL = 2;
    private int mSyncState = SYNC_STATE_INIT;
    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;

    private SwingBLEService mBluetoothService;
    private int mBindBlePurpose = -1;
    //绑定蓝牙service目的--搜索设备
    private final int bindBlePurpose_search_device = 1;
    //绑定蓝牙service目的--同步数据
    private final int bindBlePurpose_sync_data = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_firmware_upgrade);
        ButterKnife.bind(this);
        initTitleBar();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != bluetoothManager) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (null != manager && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Message", "Your GPS seems to be disabled, do you want to enable it?");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startFlow();
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
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onToolbarAction1() {
        finish();
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_sync);
        view_left_action.setImageResource(R.drawable.icon_left);
    }

    private void startFlow() {
        mMacId = DeviceManager.getFirmwareMacId();
//        mMacId = "6064058617B4";  // 测试用
        mMacAddress = DeviceManager.getMacAddress(mMacId);
        Log.d("swing", "Kid mac " + mMacAddress);

        //开始界面等待效果
        viewSearching();
        bleSearchStart();
    }

    //搜索watch回调，viewCircle间隔固定时间回调该监听
    private ViewCircle.OnProgressListener mSearchProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            mSearchTimeout--;

            //watch search result listener
            if (mSearchWatchResult != null) {
                viewFound();
                bleSearchCancel();
            } else if (mSearchTimeout == 0) {
                viewNotFound(R.string.dashboard_progress_not_found);
                bleSearchCancel();
            }
        }
    };

    private ViewCircle.OnProgressListener mSyncProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
//            mSyncTimeout--;

            if (mSyncState == SYNC_STATE_SUCCESS) {
                viewCompleted();
                bleDisconnect();
            } else if (
//                    mSyncTimeout == 0 ||
                    mSyncState == SYNC_STATE_FAIL) {
                viewNotFound(R.string.dashboard_progress_sync_fail);
                bleSyncCancel();
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

    private Button.OnClickListener mSyncCompleteListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            finish();
        }

    };

    private Button.OnClickListener mSyncListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            view_left_action.setVisibility(View.INVISIBLE);
            viewSyncing();
            bleSyncStart();
        }
    };

    private void bleSearchStart() {
        mSearchWatchResult = null;
        checkPermissions();
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_BLE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        Context context = getApplicationContext();
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }

        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_BLE);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (mBluetoothService == null) {
                    bindService(bindBlePurpose_search_device);
                } else {
                    searchWatch();
                }
                break;
        }
    }


    private void searchWatch() {
        mBluetoothService.closeConnect();
        //一直扫描，直到该界面被压栈或是被关闭
        mBluetoothService.scanDevice(0, new IDeviceScanCallback() {
            @Override
            public void onStartScan() {
                Log.d("'FirmwareUpgrade", "onStartScan");
            }

            @Override
            public void onScanTimeOut() {
                Log.d("'FirmwareUpgrade", "onScanTimeOut");
            }

            @Override
            public void onScanning(BluetoothLeDevice scanResult) {
                Log.d("'FirmwareUpgrade", "onScanning " + scanResult);
                //如果查找到指定的设备
                if (scanResult != null && scanResult.getAddress().equals(mMacAddress)) {
                    mSearchWatchResult = scanResult;
                }
            }
        });
    }

    private void bleSearchCancel() {
        if (mBluetoothService != null) {
            mBluetoothService.cancelScan();
        }
    }

    private void bleSyncStart() {
        mSyncState = SYNC_STATE_INIT;
        if (mBluetoothService == null) {
            bindService(bindBlePurpose_sync_data);
        } else {
            scanAndUpgrade();
        }
    }

    private void scanAndUpgrade() {
        String firmwareAFilePath = DeviceManager.getFirmwareAFilePath();
        String firmwareBFilePath = DeviceManager.getFirmwareBFilePath();
        if (TextUtils.isEmpty(firmwareAFilePath) || TextUtils.isEmpty(firmwareBFilePath)) {
            viewNotFound(R.string.dashboard_progress_sync_fail);
            return;
        }
        FileInputStream fisA = FileUtil.getFileInputStream(firmwareAFilePath);
        FileInputStream fisB = FileUtil.getFileInputStream(firmwareBFilePath);
        if (null == fisA || null == fisB) {
            viewNotFound(R.string.dashboard_progress_sync_fail);
            return;
        }
        mBluetoothService.closeConnect();
        mBluetoothService.scanAndUpgrade(mMacAddress, fisA, fisB, new IDeviceSyncCallback() {
            @Override
            public void onSyncComplete() {
                LogUtil.getUtils().d("sync data complete: ok");

                DeviceManager.clearParamsForFirmwareUpgrade();
                DeviceManager.sendBroadcastFirmwareUpdate(false);

                viewSyncing();
                mViewLabel.setText(getResources().getString(R.string.uploading));
                new RawActivityManager().uploadRawActivity(mMacId, mUpdateRawActivityListener);
                DeviceManager.setFirmwareNeedUpdate(false);



            }

            @Override
            public void onSyncFail(int reason) {
                LogUtil.getUtils().d("sync data complete: fail, reason: " + reason);
                //sync fail后，需要修改标志位
                mSyncState = SYNC_STATE_FAIL;
                viewNotFound(R.string.dashboard_progress_sync_fail);
                bleSyncCancel();
            }

            @Override
            public void onSyncing(String tip) {
                LogUtil.getUtils().d("syncing tip: " + tip);
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
                DeviceManager.saveBindWatchBattery(mMacId, battery);
            }

            @Override
            public void onDeviceVersion(String version) {

            }

            @Override
            public boolean onDeviceNeedUpdate(String version) {
                return true;
            }

            @Override
            public void onDeviceUpdating(float percent, String timeRemain) {
                StringBuilder sb = new StringBuilder();
                String strTip = getString(R.string.updating_your_watch);
                sb.append(strTip);
                sb.append("\n");
                sb.append(timeRemain);
                mViewLabel.setText(sb);

                mViewProgress.stopProgress();
                mViewProgress.setStrokeBeginEnd(0, (int) (percent * 100));
            }
        });
    }

    private void bleSyncCancel() {
        if (mBluetoothService != null) {
            mBluetoothService.cancelScan();
        }
    }

    private void bleDisconnect() {
        if (mBluetoothService != null) {
            mBluetoothService.closeConnect();
        }
    }

    private void viewSearching() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_searching));
        mViewLabel.setGravity(Gravity.CENTER);
        mViewLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        mViewHint.setText("");
        mViewHint.setVisibility(View.INVISIBLE);

        mViewButton.setVisibility(View.INVISIBLE);
        mViewButton.setOnClickListener(null);

        mSearchTimeout = SEARCH_TIMEOUT;
        mViewProgress.setOnProgressListener(mSearchProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewFound() {
        mViewLabel.setText(getResources().getString(R.string.dashboard_progress_found));

        mViewButton.setText(getResources().getString(R.string.dashboard_progress_sync_now));
        mViewButton.setVisibility(View.VISIBLE);
        mViewButton.setOnClickListener(mSyncListener);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    private void viewSyncing() {
        mViewLabel.setText(R.string.dashboard_progress_syncing);

        mViewHint.setText(R.string.have_watch_close_by);
        mViewHint.setVisibility(View.VISIBLE);
        mViewButton.setVisibility(View.INVISIBLE);
        mViewButton.setOnClickListener(null);

        mSyncTimeout = SYNC_TIMEOUT;
        mViewProgress.setOnProgressListener(mSyncProgressListener);
        mViewProgress.setStrokeBeginEnd(0, 10);
        mViewProgress.startProgress(PROGRESS_INTERVAL, -1, -1);
    }

    private void viewUpdating() {
        mViewLabel.setText(R.string.updating_your_watch);

        mViewHint.setText(R.string.have_watch_close_by);
        mViewHint.setVisibility(View.VISIBLE);

        mViewButton.setVisibility(View.INVISIBLE);
        mViewButton.setOnClickListener(null);
    }

    private void viewCompleted() {
        mViewLabel.setText(R.string.update_completed);
        mViewLabel.setGravity(Gravity.CENTER);
        mViewLabel.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, R.drawable.monster_yellow);

        mViewHint.setText("");
        mViewHint.setVisibility(View.INVISIBLE);
        mViewButton.setText(R.string.dashboard_progress_dashboard);
        mViewButton.setVisibility(View.VISIBLE);
        mViewButton.setOnClickListener(mSyncCompleteListener);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(true);
    }

    private void viewNotFound(int errMsgStrId) {
        mViewLabel.setText(errMsgStrId);
        mViewLabel.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        mViewLabel.setPadding(0, 0, 0, 0);
        mViewLabel.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, R.drawable.monster_green);

        mViewButton.setText(R.string.dashboard_progress_again);
        mViewButton.setVisibility(View.VISIBLE);
        mViewButton.setOnClickListener(mAgainListener);

        mViewProgress.stopProgress();
        mViewProgress.setOnProgressListener(null);
        mViewProgress.setActive(false);
    }

    //向服务端逐条上传数据业务结果的listener
    RawActivityManager.IFinishListener mUpdateRawActivityListener = new RawActivityManager.IFinishListener() {
        @Override
        public void onFinish(Object arg) {
            mSyncState = SYNC_STATE_SUCCESS;
        }

        @Override
        public void onFailed(String Command, int statusCode) {
            mSyncState = SYNC_STATE_SUCCESS;
            Log.d("FirmwareUpgrade", "upload raw activity fail: " + Command);
        }
    };

    //绑定蓝牙服务
    private void bindService(int bindPurpose) {
        mBindBlePurpose = bindPurpose;

        Intent bindIntent = new Intent(FirmwareUpgradeActivity.this, SwingBLEService.class);
        bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    //解绑定蓝牙服务
    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    //蓝牙服务连接成功后，回调接口
    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((SwingBLEService.BluetoothBinder) service).getService();

            if (mBindBlePurpose == bindBlePurpose_search_device) {//搜索设备
                searchWatch();
            } else if (mBindBlePurpose == bindBlePurpose_sync_data) {//同步数据
                scanAndUpgrade();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

}
