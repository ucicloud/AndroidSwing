package com.kidsdynamic.swing.ble;

/**
 * Created by maple on 2017/1/11.
 */

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.vise.baseble.common.BleExceptionCode;
import com.vise.baseble.utils.HexUtil;
import com.vise.baseble.ViseBluetooth;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.callback.data.ICharacteristicCallback;
import com.vise.baseble.callback.data.IRssiCallback;
import com.vise.baseble.callback.scan.PeriodScanOnceCallback;
import com.vise.baseble.callback.scan.PeriodScanCallback;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.log.ViseLog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SwingBLEService extends Service {

    public BluetoothBinder mBinder = new BluetoothBinder();

    private boolean isSync = false;
    private IDeviceInitCallback initCallback = null;
    private IDeviceSyncCallback syncCallback = null;

    private List<EventModel> eventList = null;
    private int timeStamp;
    private byte[] ffa4Data1;
    private byte[] ffa4Data2;
    private Set<Integer> timeSet;
    private int repeatTimes;
    private int activityCount;

    private void onBleFailure(String info)
    {
        if (isSync)
        {
            if (syncCallback != null) {
                syncCallback.onSyncFail(0);
                syncCallback = null;
            }
        }
        else
        {
            if (initCallback != null) {
                initCallback.onInitFail(0);
                initCallback = null;
            }
        }
        closeConnect();
    }

    private Handler threadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!ViseBluetooth.getInstance().isConnected()) {
                return;
            }

            switch (msg.what) {
                case SwingBLEAttributes.MSG_SYNC_READ_VERSION:
                {
                    read(SwingBLEAttributes.DEVICE_SERVICE, SwingBLEAttributes.FIRMWARE_VERSION, new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("VERSION onFailure:" + exception);
                            onBleFailure("VERSION Read Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            //读设备版本号
                            byte[] data = characteristic.getValue();
                            String version = null;
                            if (data != null && data.length > 0) {
                                try{
                                    version = new String(data);
                                }
                                catch (Exception e){}
                            }
                            if (isSync)
                            {
                                if (syncCallback != null && version != null) {
                                    syncCallback.onDeviceVersion(version);
                                    ViseLog.i("VERSION value: " + version);
                                }
                            }
                            else
                            {
                                if (initCallback != null && version != null) {
                                    initCallback.onDeviceVersion(version);
                                    ViseLog.i("VERSION value: " + version);
                                }
                            }
                            if (threadHandler != null) {
                                threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_INIT_WRITE_ACCEL);
                            }
                        }
                    });
                }
                break;
                case SwingBLEAttributes.MSG_INIT_WRITE_ACCEL:
                {
                    write(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.ACCEL_ENABLE, "01", new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("ACCEL_ENABLE onFailure:" + exception);
                            onBleFailure("ACCEL_ENABLE Write Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            if (threadHandler != null) {
                                threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_INIT_WRITE_TIME);
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_INIT_WRITE_TIME:
                {
                    int currentTime = (int) (getCurrentTime() / 1000);
                    byte[] timeInByte = new byte[]{(byte) (currentTime), (byte) (currentTime >> 8), (byte) (currentTime >> 16), (byte) (currentTime >> 24)};
                    write(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.TIME, HexUtil.encodeHexStr(timeInByte), new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("TIME onFailure:" + exception);
                            onBleFailure("TIME Write Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            if (threadHandler != null) {
                                threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_INIT_READ_ADDR);
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_INIT_READ_ADDR:
                {
                    read(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.ADDRESS, new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("ADDRESS onFailure:" + exception);
                            onBleFailure("ADDRESS Read Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            if (isSync) {
                                if (threadHandler != null) {
                                    threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_NUMBER);
                                }
                            }
                            else {
                                //初始化完成
                                byte[] data = characteristic.getValue();
                                String mac = null;
                                if (data != null && data.length > 0) {
                                    mac = HexUtil.encodeHexStr(data, false);
                                }
                                if (initCallback != null) {
                                    initCallback.onInitComplete(mac);
                                    ViseLog.i("ADDRESS value: " + mac);
                                    initCallback = null;
                                }
                                closeConnect();
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_NUMBER:
                {
                    if (eventList.size() > 0) {
                        EventModel m = eventList.get(0);
                        if (syncCallback != null) {
                            syncCallback.onSyncing("WRITE ALERT remain:" + eventList.size());
                        }
                        write(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.VOICE_ALERT, HexUtil.encodeHexStr(new byte[]{(byte)m.getAlert()}), new ICharacteristicCallback(){
                            @Override
                            public void onFailure(BleException exception) {
                                ViseLog.i("VOICE_ALERT onFailure:" + exception);
                                onBleFailure("VOICE_ALERT Write Error");
                            }

                            @Override
                            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                                if (threadHandler != null) {
                                    threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_TIME);
                                }
                            }
                        });
                    }
                    else {
                        if (threadHandler != null) {
                            threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_HEADER);
                        }
                    }
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_TIME:
                {
                    if (eventList.size() > 0) {
                        EventModel m = eventList.get(0);
                        long countdown = toWatchTime(m.getStartDate().getTime());
                        byte[] timeInByte = new byte[]{(byte) (countdown), (byte) (countdown >> 8), (byte) (countdown >> 16), (byte) (countdown >> 24)};
                        write(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.VOICE_EVET_ALERT_TIME, HexUtil.encodeHexStr(timeInByte), new ICharacteristicCallback(){
                            @Override
                            public void onFailure(BleException exception) {
                                if (exception.getCode() == BleExceptionCode.GATT_ERR) {
                                    //固件中FFA8写成功也会返回错误
                                    eventList.remove(0);
                                    if (threadHandler != null) {
                                        threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_NUMBER);
                                    }
                                }
                                else {
                                    onBleFailure("VOICE_EVET_ALERT_TIME Write Error");
                                }
                            }

                            @Override
                            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                                eventList.remove(0);
                                if (threadHandler != null) {
                                    threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_NUMBER);
                                }
                            }
                        });
                    }
                    else {
                        if (threadHandler != null) {
                            threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_HEADER);
                        }
                    }
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_READ_HEADER:
                {
                    read(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.HEADER, new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("HEADER onFailure:" + exception);
                            onBleFailure("HEADER Read Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            byte[] data = characteristic.getValue();
                            if (data != null && data.length >= 2) {
                                if (data[0] == 0x01 && data[1] == 0x00) {
                                    if (threadHandler != null) {
                                        threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_TIME);
                                    }
                                }
                                else {
                                    if (syncCallback != null) {
                                        syncCallback.onSyncComplete();
                                        syncCallback = null;
                                    }
                                    closeConnect();
                                }
                            }
                            else {
                                onBleFailure("HEADER is null");
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_READ_TIME:
                {
                    read(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.TIME, new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("TIME onFailure:" + exception);
                            onBleFailure("TIME Read Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess4");
                            byte[] data = characteristic.getValue();
                            if (data != null && data.length >= 4) {
                                timeStamp = byteToDec(data);
                                if (threadHandler != null) {
                                    threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_DATA1);
                                }
                            }
                            else {
                                onBleFailure("TIME is null");
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_READ_DATA1:
                {
                    read(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.DATA, new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("DATA onFailure:" + exception);
                            onBleFailure("DATA Read Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            byte[] data = characteristic.getValue();
                            if (data != null && data.length > 0) {
                                ffa4Data1 = data;
                                if (threadHandler != null) {
                                    threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_DATA2);
                                }
                            }
                            else {
                                onBleFailure("DATA is null");
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_READ_DATA2:
                {
                    read(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.DATA, new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("DATA onFailure:" + exception);
                            onBleFailure("DATA Read Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            byte[] data = characteristic.getValue();
                            if (data != null && data.length > 0) {
                                byte value;
                                if (Arrays.equals(ffa4Data1, data)) {
                                    value = 0x00;
                                    ffa4Data2 = null;
                                }
                                else {
                                    ffa4Data2 = data;
                                    value = 0x01;
                                }
                                if (threadHandler != null) {
                                    threadHandler.sendMessage(threadHandler.obtainMessage(SwingBLEAttributes.MSG_SYNC_WRITE_CHECK_SUM, value, 0));
                                }
                            }
                            else {
                                onBleFailure("DATA is null");
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_WRITE_CHECK_SUM:
                {
                    byte value = (byte) msg.arg1;
                    write(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.CHECKSUM, HexUtil.encodeHexStr(new byte[]{value}), new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("CHECKSUM onFailure:" + exception);
                            onBleFailure("CHECKSUM Write Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            //判断是否存在重复数据
                            if (!timeSet.contains(timeStamp)) {
                                timeSet.add(timeStamp);
                                repeatTimes = 5;
                                //2017/10/19 处理Activity数据
                                ActivityModel m = new ActivityModel();
                                m.parseRawData(mac, timeStamp, ffa4Data1, ffa4Data2);

                                activityCount++;
                                if (syncCallback != null) {
                                    syncCallback.onSyncing("READ ACTIVITY count:" + activityCount);
                                    syncCallback.onSyncActivity(m);
                                }
                                ViseLog.i("activity: time:" + timeStamp + " data1:" + HexUtil.encodeHexStr(ffa4Data1, false) + " data2:" + HexUtil.encodeHexStr(ffa4Data2, false));
                                ViseLog.i("model: time:" + m.getTime() + " timezone:" + m.getTimeZoneOffset() + " macId:" + m.getMacId() + " indoor:" + m.getIndoorActivity() + " outdoor:" + m.getOutdoorActivity());
                            }
                            else {
                                if (--repeatTimes < 0) {
                                    ViseLog.i("activity repeat times is max.");
                                    if (syncCallback != null) {
                                        syncCallback.onSyncComplete();
                                        syncCallback = null;
                                    }
                                    closeConnect();
                                    return;
                                }
                            }

                            if (threadHandler != null) {
                                threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_HEADER);
                            }
                        }
                    });
                }
                    break;
                default:
                    break;
            }
//            super.handleMessage(msg);
        }
    };
    private String name;
    private String mac;
    private BluetoothGatt gatt;
    private BluetoothGattService service;

    @Override
    public void onCreate() {
        //蓝牙信息初始化，全局唯一，必须在应用初始化时调用
        ViseBluetooth.getInstance().init(getApplicationContext());

        ViseBluetooth.getInstance().setConnectTimeout(30000);
        ViseBluetooth.getInstance().setOperateTimeout(10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initCallback = null;
        syncCallback = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ViseBluetooth.getInstance().clear();
        return super.onUnbind(intent);
    }

    public class BluetoothBinder extends Binder {
        public SwingBLEService getService() {
            return SwingBLEService.this;
        }
    }

    private PeriodScanCallback periodScanCallback = null;

    public void scanDevice(int timeoutMillis, final IDeviceScanCallback callback) {
//        if (!ViseBluetooth.getInstance().getBluetoothAdapter().isEnabled()) {
//            ViseBluetooth.getInstance().getBluetoothAdapter().enable();
//        }

        resetInfo();

        if (callback != null) {
            callback.onStartScan();
        }

        if (periodScanCallback != null) {
            ViseBluetooth.getInstance().stopScan(periodScanCallback);
        }

        periodScanCallback = new PeriodScanOnceCallback() {
            @Override
            public void scanTimeout() {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onScanTimeOut();
                        }
                    }
                });
            }

            @Override
            public void onDeviceFound(final BluetoothLeDevice bluetoothLeDevice) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            String name = bluetoothLeDevice.getDevice().getName();
                            if (name != null && name.startsWith("SWING")) {
                                callback.onScanning(bluetoothLeDevice);
                            }
                        }
                    }
                });
            }
        };

        ViseBluetooth.getInstance().setScanTimeout(timeoutMillis)
                .startScan(periodScanCallback);
    }

    public void cancelScan() {
        if (periodScanCallback != null) {
            ViseBluetooth.getInstance().stopScan(periodScanCallback);
            periodScanCallback = null;
        }
    }

    public void connectAndInitDevice(final BluetoothLeDevice scanResult, IDeviceInitCallback callback) {
//        if (!ViseBluetooth.getInstance().getBluetoothAdapter().isEnabled()) {
//            ViseBluetooth.getInstance().getBluetoothAdapter().enable();
//        }

        isSync = false;
        initCallback = callback;

        SwingBLEService.this.name = scanResult.getDevice().getName();
        SwingBLEService.this.mac = scanResult.getDevice().getAddress();
        ViseBluetooth.getInstance().connect(scanResult, false, new IConnectCallback() {
            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {
                ViseLog.i("onConnecting");
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                ViseLog.i("onConnectSuccess");
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                SwingBLEService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ViseLog.i("onServicesDiscovered");
                        read(SwingBLEAttributes.BATTERY_SERVICE, SwingBLEAttributes.BATTERY_LEVEL, new ICharacteristicCallback(){
                            @Override
                            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                byte[] data = characteristic.getValue();
                                if (data != null && data.length > 0) {
                                    if (initCallback != null) {
                                        initCallback.onDeviceBattery(data[0]);
                                        ViseLog.i("BATTERY_LEVEL value: " + data[0]);
                                    }
                                }
                                if (threadHandler != null) {
                                    threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_VERSION);
                                }
                            }

                            @Override
                            public void onFailure(BleException exception) {
                                ViseLog.i("BATTERY_LEVEL onFailure:" + exception);
                                onBleFailure("BATTERY_LEVEL Read Error");
                            }
                        });
                    }
                });
            }

            @Override
            public void onConnectFailure(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onBleFailure("onConnectFailure");
                    }
                });
            }

            @Override
            public void onDisconnect() {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onBleFailure("onDisconnect");
                    }
                });
            }
        });
    }

    public void scanAndSync(String mac, List<EventModel> list, IDeviceSyncCallback callback) {
//        if (!ViseBluetooth.getInstance().getBluetoothAdapter().isEnabled()) {
//            ViseBluetooth.getInstance().getBluetoothAdapter().enable();
//        }

        resetInfo();
        isSync = true;
        syncCallback = callback;
        eventList = list;
        timeSet = new HashSet<Integer>();
        repeatTimes = 5;
        activityCount = 0;

        ViseBluetooth.getInstance().setScanTimeout(20000).connectByLMac(mac, false, new IConnectCallback() {
            @Override
            public void onConnecting(BluetoothGatt gatt, int status) {
                ViseLog.i("onConnecting");
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                ViseLog.i("onConnectSuccess");
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                SwingBLEService.this.name = gatt.getDevice().getName();
                SwingBLEService.this.mac = gatt.getDevice().getAddress();
                SwingBLEService.this.gatt = gatt;

                if (Build.VERSION.SDK_INT >= 21) {
                    if (gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)) {
                        ViseLog.i("enable CONNECTION_PRIORITY_HIGH");
                    }
                }

                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        read(SwingBLEAttributes.BATTERY_SERVICE, SwingBLEAttributes.BATTERY_LEVEL, new ICharacteristicCallback(){
                            @Override
                            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                byte[] data = characteristic.getValue();
                                if (data != null && data.length > 0) {
                                    if (syncCallback != null) {
                                        syncCallback.onDeviceBattery(data[0]);
                                        ViseLog.i("BATTERY_LEVEL value: " + data[0]);
                                    }
                                }
                                if (threadHandler != null) {
                                    threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_VERSION);
                                }
                            }

                            @Override
                            public void onFailure(BleException exception) {
                                ViseLog.i("BATTERY_LEVEL onFailure:" + exception);
                                onBleFailure("BATTERY_LEVEL Read Error");
                            }
                        });
                    }
                });
            }

            @Override
            public void onConnectFailure(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onBleFailure("onConnectFailure");
                    }
                });
            }

            @Override
            public void onDisconnect() {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onBleFailure("onDisconnect");
                    }
                });
            }
        });
    }

    public void scanAndSync2(final String mac, List<EventModel> list, IDeviceSyncCallback callback2) {
//        if (!ViseBluetooth.getInstance().getBluetoothAdapter().isEnabled()) {
//            ViseBluetooth.getInstance().getBluetoothAdapter().enable();
//        }

        resetInfo();
        isSync = true;
        syncCallback = callback2;

        eventList = list;
        timeSet = new HashSet<Integer>();
        repeatTimes = 5;
        activityCount = 0;

        if (periodScanCallback != null) {
            ViseBluetooth.getInstance().stopScan(periodScanCallback);
        }

        PeriodScanOnceCallback callback = new PeriodScanOnceCallback() {
            @Override
            public void scanTimeout() {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        onBleFailure("ScanTimeout");
                    }
                });
            }

            @Override
            public void onDeviceFound(final BluetoothLeDevice bluetoothLeDevice) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (syncCallback != null) {
                            String address = bluetoothLeDevice.getDevice().getAddress();
                            if (address != null && address.equals(mac)) {
                                setScan(false).removeHandlerMsg().scan();
                                ViseBluetooth.getInstance().connect(bluetoothLeDevice, false, new IConnectCallback() {
                                    @Override
                                    public void onConnecting(BluetoothGatt gatt, int status) {
                                        ViseLog.i("onConnecting");
                                    }

                                    @Override
                                    public void onConnectSuccess(BluetoothGatt gatt, int status) {
                                        ViseLog.i("onConnectSuccess");
                                    }

                                    @Override
                                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                                        SwingBLEService.this.name = gatt.getDevice().getName();
                                        SwingBLEService.this.mac = gatt.getDevice().getAddress();
                                        SwingBLEService.this.gatt = gatt;

                                        if (Build.VERSION.SDK_INT >= 21) {
                                            if (gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)) {
                                                ViseLog.i("enable CONNECTION_PRIORITY_HIGH");
                                            }
                                        }

                                        runOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                read(SwingBLEAttributes.BATTERY_SERVICE, SwingBLEAttributes.BATTERY_LEVEL, new ICharacteristicCallback(){
                                                    @Override
                                                    public void onSuccess(BluetoothGattCharacteristic characteristic) {
                                                        byte[] data = characteristic.getValue();
                                                        if (data != null && data.length > 0) {
                                                            if (syncCallback != null) {
                                                                syncCallback.onDeviceBattery(data[0]);
                                                                ViseLog.i("BATTERY_LEVEL value: " + data[0]);
                                                            }
                                                        }
                                                        if (threadHandler != null) {
                                                            threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_READ_VERSION);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(BleException exception) {
                                                        ViseLog.i("BATTERY_LEVEL onFailure:" + exception);
                                                        onBleFailure("BATTERY_LEVEL Read Error");
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onConnectFailure(BleException exception) {
                                        runOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                onBleFailure("onConnectFailure");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onDisconnect() {
                                        runOnMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                onBleFailure("onDisconnect");
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                });
            }
        };

        ViseBluetooth.getInstance().setScanTimeout(20000)
                .startScan(callback);
    }

    public boolean read(String uuid_service, String uuid_read, ICharacteristicCallback callback) {
        return ViseBluetooth.getInstance().withUUIDString(uuid_service, uuid_read, null).readCharacteristic(callback);
    }

    public boolean write(String uuid_service, String uuid_write, String hex, ICharacteristicCallback callback) {
        return ViseBluetooth.getInstance().withUUIDString(uuid_service, uuid_write, null).writeCharacteristic(HexUtil.hexStringToBytes(hex), callback);
    }

    public boolean notify(String uuid_service, String uuid_notify, ICharacteristicCallback callback) {
        return ViseBluetooth.getInstance().withUUIDString(uuid_service, uuid_notify, null).enableCharacteristicNotification(callback, false);
    }

    public boolean indicate(String uuid_service, String uuid_indicate, ICharacteristicCallback callback) {
        return ViseBluetooth.getInstance().withUUIDString(uuid_service, uuid_indicate, null).enableCharacteristicNotification(callback, true);
    }

    public boolean stopNotify(String uuid_service, String uuid_notify) {
        return ViseBluetooth.getInstance().withUUIDString(uuid_service, uuid_notify, null).setNotification(false, false);
    }

    public boolean stopIndicate(String uuid_service, String uuid_indicate) {
        return ViseBluetooth.getInstance().withUUIDString(uuid_service, uuid_indicate, null).setNotification(false, true);
    }

    public boolean readRssi(IRssiCallback callback) {
        return ViseBluetooth.getInstance().readRemoteRssi(callback);
    }

    public void closeConnect() {
        ViseBluetooth.getInstance().close();
    }


    private void resetInfo() {
        name = null;
        mac = null;
        gatt = null;
        service = null;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setService(BluetoothGattService service) {
        this.service = service;
    }

    public BluetoothGattService getService() {
        return service;
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }

    private static long getCurrentTime() {
        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        return now.getTimeInMillis() + offset;
    }



    private static int toWatchTime(long utc) {
        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        return (int) ((utc + offset) / 1000);
    }

    public static long toUtcTime(int time) {
        Calendar now = Calendar.getInstance();
        int offset = now.getTimeZone().getOffset(now.getTimeInMillis());
        return (time - offset) * 1000;
    }

    private static int byteToDec(byte[] data) {
        int dec;

        dec = data[0] & 0xFF;
        dec |= (data[1] << 8) & 0xFF00;
        dec |= (data[2] << 16) & 0xFF0000;
        dec |= (data[3] << 24) & 0xFF000000;

        return dec;
    }

}
