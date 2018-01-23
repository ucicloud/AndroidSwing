package com.kidsdynamic.swing.bletest;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kidsdynamic.swing.ble.ActivityModel;
import com.kidsdynamic.swing.ble.EventModel;
import com.kidsdynamic.swing.ble.IDeviceInitCallback;
import com.kidsdynamic.swing.ble.IDeviceScanCallback;
import com.kidsdynamic.swing.ble.IDeviceSyncCallback;
import com.kidsdynamic.swing.ble.SwingBLEService;
import com.kidsdynamic.swing.R;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SwingScanActivity extends AppCompatActivity implements View.OnClickListener {

    private int op;
    private String address;
    private Button btn_start, btn_stop, btn_sync;
    private TextView sync_view;
    private ImageView img_loading;
    private Animation operatingAnim;
    private ResultAdapter mResultAdapter;
    private ProgressDialog progressDialog;

    private SwingBLEService mBluetoothService;

    private static final String TAG = "maple";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViseLog.getLogConfig().configAllowLog(true);//配置日志信息
        //ViseLog.plant(new LogcatTree());//添加Logcat打印信息
        ViseLog.plant(new SwingLogActivity.StringTree());
        setContentView(R.layout.activity_swing_ble_scan);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null)
            unbindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_ble_main, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                {
                    startActivity(new Intent(SwingScanActivity.this, SwingLogActivity.class));
                }
                    break;
            }
            return true;
        }
    };

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SWING BLE TEST");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        btn_sync = (Button) findViewById(R.id.btn_sync);
        btn_sync.setOnClickListener(this);
        btn_sync = (Button) findViewById(R.id.btn_update);
        btn_sync.setOnClickListener(this);
        sync_view = (TextView) findViewById(R.id.tv_mac);
        img_loading = (ImageView) findViewById(R.id.img_loading);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mResultAdapter = new ResultAdapter(this);
        ListView listView_device = (ListView) findViewById(R.id.list_device);
        listView_device.setAdapter(mResultAdapter);
        listView_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBluetoothService != null) {
                    mBluetoothService.cancelScan();

                    progressDialog.setMessage("初始化设备中");
                    progressDialog.show();
                    img_loading.clearAnimation();
                    btn_start.setEnabled(true);
                    btn_stop.setVisibility(View.INVISIBLE);
                    BluetoothLeDevice device = mResultAdapter.getItem(position);
                    address = device.getAddress();
                    mBluetoothService.connectAndInitDevice(device, new IDeviceInitCallback() {
                        @Override
                        public void onInitComplete(String mac) {
                            Log.d(TAG, "onInitComplete mac " + mac);
                            progressDialog.dismiss();
                            sync_view.setText(address);
                            Toast.makeText(SwingScanActivity.this, "初始化成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onInitFail(int reason) {
                            progressDialog.dismiss();
                            Toast.makeText(SwingScanActivity.this, "初始化失败", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onDeviceBattery(int battery) {
                            Log.d(TAG, "onDeviceBattery battery " + battery);
                        }

                        @Override
                        public void onDeviceVersion(String version) {
                            Log.d(TAG, "onDeviceVersion version " + version);
                        }
                    });
                    mResultAdapter.clear();
                    mResultAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
            {
                op = 1;
                checkPermissions();
            }
                break;

            case R.id.btn_stop:
                if (mBluetoothService != null) {
                    mBluetoothService.cancelScan();
                    img_loading.clearAnimation();
                    btn_start.setEnabled(true);
                    btn_stop.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.btn_sync:
            {
                op = 2;
                checkPermissions();
            }
                break;
            case R.id.btn_update:
            {
                op = 3;
                checkPermissions();
            }
            break;
        }
    }

    private class ResultAdapter extends BaseAdapter {

        private Context context;
        private List<BluetoothLeDevice> scanResultList;

        ResultAdapter(Context context) {
            this.context = context;
            scanResultList = new ArrayList<>();
        }

        void addResult(BluetoothLeDevice result) {
            scanResultList.add(result);
        }

        void clear() {
            scanResultList.clear();
        }

        @Override
        public int getCount() {
            return scanResultList.size();
        }

        @Override
        public BluetoothLeDevice getItem(int position) {
            if (position > scanResultList.size())
                return null;
            return scanResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResultAdapter.ViewHolder holder;
            if (convertView != null) {
                holder = (ResultAdapter.ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_scan_result, null);
                holder = new ResultAdapter.ViewHolder();
                convertView.setTag(holder);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
                holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);
            }

            BluetoothLeDevice result = scanResultList.get(position);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            String mac = device.getAddress();
            int rssi = result.getRssi();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            return convertView;
        }

        class ViewHolder {
            TextView txt_name;
            TextView txt_mac;
            TextView txt_rssi;
        }
    }

    private void bindService() {
        Intent bindIntent = new Intent(this, SwingBLEService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((SwingBLEService.BluetoothBinder) service).getService();
            exec();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
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
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (mBluetoothService == null) {
                    bindService();
                } else {
                    exec();
                }
                break;
        }
    }

    private boolean oye = false;

    private void exec() {
        switch (op) {
            case 1:
            {
                mBluetoothService.closeConnect();
                mBluetoothService.scanDevice(0, new IDeviceScanCallback() {
                    @Override
                    public void onStartScan() {
                        mResultAdapter.clear();
                        mResultAdapter.notifyDataSetChanged();
                        img_loading.startAnimation(operatingAnim);
                        btn_start.setEnabled(false);
                        btn_stop.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onScanTimeOut() {
                        img_loading.clearAnimation();
                        btn_start.setEnabled(true);
                        btn_stop.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onScanning(BluetoothLeDevice scanResult) {
                        mResultAdapter.addResult(scanResult);
                        mResultAdapter.notifyDataSetChanged();
                    }
                });
            }
                break;
            case 2:
            {
                String mac = sync_view.getText().toString();
                if (mac == null || mac.length() == 0)
                {
                    Toast.makeText(SwingScanActivity.this, "请先扫描并初始化设备", Toast.LENGTH_LONG).show();
                    return;
                }

                List<EventModel> list = genEvents();

                mBluetoothService.closeConnect();
                progressDialog.setMessage("同步设备中");
                progressDialog.show();
                mBluetoothService.scanAndSync2(mac, list, new IDeviceSyncCallback() {
                    @Override
                    public void onSyncComplete() {
                        progressDialog.dismiss();
                        Toast.makeText(SwingScanActivity.this, "同步成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSyncFail(int reason) {
                        progressDialog.dismiss();
                        Toast.makeText(SwingScanActivity.this, "同步失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSyncing(String tip) {
                        progressDialog.setMessage(tip);
                    }

                    @Override
                    public void onSyncActivity(ActivityModel activity) {

                    }

                    @Override
                    public void onDeviceBattery(int battery) {
                        Log.d(TAG, "onDeviceBattery battery " + battery);
                    }

                    @Override
                    public void onDeviceVersion(String version) {
                        Log.d(TAG, "onDeviceVersion version " + version);
                    }

                    @Override
                    public boolean onDeviceNeedUpdate(String version) {
                        return false;
                    }

                    @Override
                    public void onDeviceUpdating(float percent, String timeRemain) {

                    }
                });

//                mBluetoothService.scanAndSync2("7F:9F:57:8D:3F:6A", list);
//                mBluetoothService.scanAndSync("60:64:05:86:1C:BE", list);
//                mBluetoothService.scanAndSync("60:64:05:86:21:52", list);
            }
                break;
            case 3:
            {
                String mac = sync_view.getText().toString();
                if (mac == null || mac.length() == 0)
                {
                    Toast.makeText(SwingScanActivity.this, "请先扫描并初始化设备", Toast.LENGTH_LONG).show();
                    return;
                }

//                oye = !oye;
                String name = null;
                FileInputStream fileA = null;
                FileInputStream fileB = null;
                if (oye)
                {
                    try {
                        name = "A_20171010.bin";
                        fileA = new FileInputStream("/sdcard/A_20171010.bin");
                        fileB = new FileInputStream("/sdcard/B_20171010.bin");
                    } catch (Exception e) {
                        Toast.makeText(SwingScanActivity.this, "固件不存在", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else {
                    try {
                        name = "KDV0009-A_A_111017.bin";
                        fileA = new FileInputStream("/sdcard/KDV0009-A_A_111017.bin");
                        fileB = new FileInputStream("/sdcard/KDV0009-A_B_111017.bin");
                    } catch (Exception e) {
                        Toast.makeText(SwingScanActivity.this, "固件不存在", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                
                mBluetoothService.closeConnect();
                progressDialog.setMessage("升级设备中" + name);
                progressDialog.show();

                mBluetoothService.scanAndUpgrade(mac, fileA, fileB, new IDeviceSyncCallback() {
                    @Override
                    public void onSyncComplete() {
                        progressDialog.dismiss();
                        Toast.makeText(SwingScanActivity.this, "升级成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSyncFail(int reason) {
                        progressDialog.dismiss();
                        Toast.makeText(SwingScanActivity.this, "升级失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSyncing(String tip) {
                        progressDialog.setMessage(tip);
                    }

                    @Override
                    public void onSyncActivity(ActivityModel activity) {

                    }

                    @Override
                    public void onDeviceBattery(int battery) {
                        Log.d(TAG, "onDeviceBattery battery " + battery);
                    }

                    @Override
                    public void onDeviceVersion(String version) {
                        Log.d(TAG, "onDeviceVersion version " + version);
                    }

                    @Override
                    public boolean onDeviceNeedUpdate(String version) {
                        return true;
                    }

                    @Override
                    public void onDeviceUpdating(float percent, String timeRemain) {
                        progressDialog.setMessage(timeRemain +" %" + percent * 100);
                    }
                });

            }
            break;
        }
    }

    private List<EventModel> genEvents() {
        Date now = new Date();
        List<EventModel> list = new ArrayList<EventModel>();
        int j = 0;
        for (int k = 0; k < 7; k++)//k < 7
        {
            for (int i = 37; i <= 64; i++, j++)//64
            {
                if (list.size() >= 5) {
                    return list;
                }

                EventModel m = new EventModel();
                m.setAlert(i);
                m.setStartDate(new Date(now.getTime() + j * 10000 + 10 * 60000));
                list.add(m);
            }
        }

        return list;
    }

}
