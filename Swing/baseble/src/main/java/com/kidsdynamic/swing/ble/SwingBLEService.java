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

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SwingBLEService extends Service {

    public BluetoothBinder mBinder = new BluetoothBinder();

    private int action = SwingBLEAttributes.BLE_INIT_ACTION;
    private int handlerState;
    private IDeviceInitCallback initCallback = null;
    private IDeviceSyncCallback syncCallback = null;

    private class SyncModel {
        public List<EventModel> eventList = null;
        public int timeStamp;
        public byte[] ffa4Data1;
        public byte[] ffa4Data2;
        public Set<Integer> timeSet;
        public int repeatTimes;
        public int activityCount;

        public String version;
        public FileInputStream currentFileVer;
        public FileInputStream fileVerA;
        public FileInputStream fileVerB;

        public byte[] requestData = new byte[2 + SwingBLEAttributes.OAD_BLOCK_SIZE];
        public int nBlocks;
        public int nBytes;
        public int iBlocks;
        public int iBytes;

        public SyncModel(List<EventModel> list) {
            if (list == null) {
                eventList = new ArrayList<EventModel>();
            }
            else {
                eventList = list;
            }
            timeSet = new HashSet<Integer>();
            repeatTimes = 5;
            activityCount = 0;
        }

        public void close() {
            if (fileVerA != null) {
                try {
                    fileVerA.close();
                } catch (Exception e){}
            }
            if (fileVerB != null) {
                try {
                    fileVerB.close();
                } catch (Exception e){}
            }
        }

        public void writeOADNotify() {
            //监听OAD_IMAGE_NOTIFY_UUID反馈数据
            boolean success = SwingBLEService.this.notify(SwingBLEAttributes.OAD_SERVICE_UUID, SwingBLEAttributes.OAD_IMAGE_NOTIFY_UUID, new ICharacteristicCallback() {
                @Override
                public void onSuccess(BluetoothGattCharacteristic characteristic) {
                    SwingBLEService.this.stopNotify(SwingBLEAttributes.OAD_SERVICE_UUID, SwingBLEAttributes.OAD_IMAGE_NOTIFY_UUID);
                    ViseLog.i("current bin ver : " + HexUtil.encodeHexStr(characteristic.getValue()));

                    if (handlerState == SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_A) {
                        if (threadHandler != null) {
                            threadHandler.removeMessages(SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_B);
                        }
                        ViseLog.i("USE IMAGE A");
                        currentFileVer = fileVerA;
                    }
                    else if (handlerState == SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_B) {
                        if (threadHandler != null) {
                            threadHandler.removeMessages(SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_TIMEOUT);
                        }
                        ViseLog.i("USE IMAGE B");
                        currentFileVer = fileVerB;
                    }

                    if (currentFileVer != null) {
                        if (threadHandler != null) {
                            threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_UPGRADE_DOWNING_IMAGE_HEADER);
                        }
                    }

                }

                @Override
                public void onFailure(BleException exception) {
                    ViseLog.i("OAD_IMAGE_NOTIFY_UUID Notify onFailure:" + exception);
                    onBleFailure("OAD_IMAGE_NOTIFY_UUID Notify Error");
                }
            });
            if (!success)
                return;
            if (threadHandler != null) {
                threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_A);
            }
        }

        public void handleUpgrade(int what) {
            switch (what) {
                case SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_A:
                {
                    //发送0x00检查当前固件是否是Image A，如果不回应则1.5s后发0x01检查当前固件是否是Image B
                    SwingBLEService.this.write(SwingBLEAttributes.OAD_SERVICE_UUID, SwingBLEAttributes.OAD_IMAGE_NOTIFY_UUID, "00", new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("OAD_IMAGE_NOTIFY_UUID onFailure:" + exception);
                            onBleFailure("OAD_IMAGE_NOTIFY_UUID Write Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            if (threadHandler != null) {
                                threadHandler.sendEmptyMessageDelayed(SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_B, 1500);
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_B:
                {
                    //发0x01检查当前固件是否是Image B
                    SwingBLEService.this.write(SwingBLEAttributes.OAD_SERVICE_UUID, SwingBLEAttributes.OAD_IMAGE_NOTIFY_UUID, "01", new ICharacteristicCallback(){
                        @Override
                        public void onFailure(BleException exception) {
                            ViseLog.i("OAD_IMAGE_NOTIFY_UUID onFailure:" + exception);
                            onBleFailure("OAD_IMAGE_NOTIFY_UUID Write Error");
                        }

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {
                            ViseLog.i(characteristic.getUuid() + " characteristic onSuccess");
                            if (threadHandler != null) {
                                threadHandler.sendEmptyMessageDelayed(SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_TIMEOUT, 1500);
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_UPGRADE_CHECK_IMAGE_TIMEOUT:
                {
                    SwingBLEService.this.stopNotify(SwingBLEAttributes.OAD_SERVICE_UUID, SwingBLEAttributes.OAD_IMAGE_NOTIFY_UUID);
                    ViseLog.i("MSG_UPGRADE_CHECK_IMAGE_TIMEOUT");
                    onBleFailure("MSG_UPGRADE_CHECK_IMAGE_TIMEOUT Error");
                }
                    break;
                case SwingBLEAttributes.MSG_UPGRADE_DOWNING_IMAGE_HEADER:
                {
                    try {
                        currentFileVer.read(requestData, 2, SwingBLEAttributes.OAD_BLOCK_SIZE);

                        byte[] data = new byte[SwingBLEAttributes.OAD_IMG_HDR_SIZE + 2 + 2];

                        data[0] = requestData[2 + 2 + 1];
                        data[1] = requestData[2 + 2];

                        data[2] = requestData[2 + 2 + 2 + 1];
                        data[3] = requestData[2 + 2 + 2];


                        int len;

                        len = data[2] & 0xFF;
                        len |= (data[3] << 8) & 0xFF00;
                        ViseLog.i("Image header = " + HexUtil.encodeHexStr(requestData));
                        ViseLog.i("Image len = " + len + " file len = " + currentFileVer.available());

                        System.arraycopy(requestData, 2 + 2 + 4, data, 4, 4);

                        data[SwingBLEAttributes.OAD_IMG_HDR_SIZE + 0] = 12;
                        data[SwingBLEAttributes.OAD_IMG_HDR_SIZE + 1] = 0x00;

                        data[SwingBLEAttributes.OAD_IMG_HDR_SIZE + 2] = 15;
                        data[SwingBLEAttributes.OAD_IMG_HDR_SIZE + 3] = 0x00;

                        onBleFailure("OYEYE");

                        nBlocks = len / (SwingBLEAttributes.OAD_BLOCK_SIZE / SwingBLEAttributes.HAL_FLASH_WORD_SIZE);
                        nBytes = len * SwingBLEAttributes.HAL_FLASH_WORD_SIZE;
                        iBlocks = 0;
                        iBytes = 0;

                    }
                    catch (Exception e) {
                        ViseLog.i("file read err:" + e);
                        onBleFailure("File Read Error");
                    }
                }
                break;
                case SwingBLEAttributes.MSG_UPGRADE_DOWNING_IMAGE:
                {

                }
                    break;
                default:
                    break;
            }
        }
    }

    private SyncModel syncModel = null;

    private void onBleFailure(String info)
    {
        switch (action) {
            case SwingBLEAttributes.BLE_SYNC_ACTION:
                if (syncCallback != null) {
                    syncCallback.onSyncFail(0);
                    syncCallback = null;
                }
                break;
            case SwingBLEAttributes.BLE_UPGRADE_ACTION:
                {
                    syncModel.close();
                    if (syncCallback != null) {
                        syncCallback.onSyncFail(0);
                        syncCallback = null;
                    }
                }
                break;
            default:
                if (initCallback != null) {
                    initCallback.onInitFail(0);
                    initCallback = null;
                }
                break;
        }
        closeConnect();
    }

    private Handler threadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!ViseBluetooth.getInstance().isConnected()) {
                return;
            }
            handlerState = msg.what;
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
                            switch (action) {
                                case SwingBLEAttributes.BLE_SYNC_ACTION:
                                case SwingBLEAttributes.BLE_UPGRADE_ACTION:
                                    if (syncCallback != null && version != null) {
                                        syncModel.version = version;
                                        syncCallback.onDeviceVersion(version);
                                        ViseLog.i("VERSION value: " + version);
                                    }
                                    break;
                                default:
                                    if (initCallback != null && version != null) {
                                        initCallback.onDeviceVersion(version);
                                        ViseLog.i("VERSION value: " + version);
                                    }
                                    break;
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
                            switch (action) {
                                case SwingBLEAttributes.BLE_SYNC_ACTION:
                                case SwingBLEAttributes.BLE_UPGRADE_ACTION:
                                    if (threadHandler != null) {
                                        threadHandler.sendEmptyMessage(SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_NUMBER);
                                    }
                                    break;
                                default:
                                {
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
                                    break;
                            }
                        }
                    });
                }
                    break;
                case SwingBLEAttributes.MSG_SYNC_WRITE_ALERT_NUMBER:
                {
                    if (syncModel.eventList.size() > 0) {
                        EventModel m = syncModel.eventList.get(0);
                        if (syncCallback != null) {
                            syncCallback.onSyncing("WRITE ALERT remain:" + syncModel.eventList.size());
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
                    if (syncModel.eventList.size() > 0) {
                        EventModel m = syncModel.eventList.get(0);
                        long countdown = toWatchTime(m.getStartDate().getTime());
                        byte[] timeInByte = new byte[]{(byte) (countdown), (byte) (countdown >> 8), (byte) (countdown >> 16), (byte) (countdown >> 24)};
                        write(SwingBLEAttributes.WATCH_SERVICE, SwingBLEAttributes.VOICE_EVET_ALERT_TIME, HexUtil.encodeHexStr(timeInByte), new ICharacteristicCallback(){
                            @Override
                            public void onFailure(BleException exception) {
                                if (exception.getCode() == BleExceptionCode.GATT_ERR) {
                                    //固件中FFA8写成功也会返回错误
                                    syncModel.eventList.remove(0);
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
                                syncModel.eventList.remove(0);
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
                                    //设备数据已经获取完毕
                                    if (action == SwingBLEAttributes.BLE_SYNC_ACTION) {
                                        if (syncCallback != null) {
                                            syncCallback.onSyncComplete();
                                            syncCallback = null;
                                        }
                                        closeConnect();
                                    }
                                    else {
                                        //开始进行升级操作
                                        if (syncCallback != null) {
                                            if (syncCallback.onDeviceNeedUpdate(syncModel.version)) {
                                                syncModel.writeOADNotify();
                                            }
                                            else {
                                                syncCallback.onSyncComplete();
                                                syncCallback = null;
                                                closeConnect();
                                            }
                                        }

                                    }
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
                                syncModel.timeStamp = byteToDec(data);
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
                                syncModel.ffa4Data1 = data;
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
                                if (Arrays.equals(syncModel.ffa4Data1, data)) {
                                    value = 0x00;
                                    syncModel.ffa4Data2 = null;
                                }
                                else {
                                    syncModel.ffa4Data2 = data;
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
                            if (!syncModel.timeSet.contains(syncModel.timeStamp)) {
                                syncModel.timeSet.add(syncModel.timeStamp);
                                syncModel.repeatTimes = 5;
                                // TODO: 2017/10/19 处理Activity数据
                                ActivityModel m = new ActivityModel();
                                m.parseRawData(mac, syncModel.timeStamp, syncModel.ffa4Data1, syncModel.ffa4Data2);

                                syncModel.activityCount++;
                                if (syncCallback != null) {
                                    syncCallback.onSyncing("READ ACTIVITY count:" + syncModel.activityCount);
                                    syncCallback.onSyncActivity(m);
                                }
                                ViseLog.i("activity: time:" + syncModel.timeStamp + " data1:" + HexUtil.encodeHexStr(syncModel.ffa4Data1, false) + " data2:" + HexUtil.encodeHexStr(syncModel.ffa4Data2, false));
                                ViseLog.i("model: time:" + m.getTime() + " timezone:" + m.getTimeZoneOffset() + " macId:" + m.getMacId() + " indoor:" + m.getIndoorActivity() + " outdoor:" + m.getOutdoorActivity());
                            }
                            else {
                                if (--syncModel.repeatTimes < 0) {
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
                    syncModel.handleUpgrade(msg.what);
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
        action = SwingBLEAttributes.BLE_INIT_ACTION;
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
/*
    public void scanAndSync(String mac, List<EventModel> list, IDeviceSyncCallback callback) {
//        if (!ViseBluetooth.getInstance().getBluetoothAdapter().isEnabled()) {
//            ViseBluetooth.getInstance().getBluetoothAdapter().enable();
//        }

        resetInfo();
        action = SwingBLEAttributes.BLE_SYNC_ACTION;
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
*/
    public void scanAndSync2(final String mac, List<EventModel> list, IDeviceSyncCallback callback2) {
//        if (!ViseBluetooth.getInstance().getBluetoothAdapter().isEnabled()) {
//            ViseBluetooth.getInstance().getBluetoothAdapter().enable();
//        }

        resetInfo();
        action = SwingBLEAttributes.BLE_SYNC_ACTION;
        syncCallback = callback2;

        syncModel = new SyncModel(list);

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

    public void scanAndUpgrade(final String mac, FileInputStream fileVerA, FileInputStream fileVerB, IDeviceSyncCallback callback2) {
//        if (!ViseBluetooth.getInstance().getBluetoothAdapter().isEnabled()) {
//            ViseBluetooth.getInstance().getBluetoothAdapter().enable();
//        }

        resetInfo();
        action = SwingBLEAttributes.BLE_UPGRADE_ACTION;
        syncCallback = callback2;

        syncModel = new SyncModel(null);
        syncModel.fileVerA = fileVerA;
        syncModel.fileVerB = fileVerB;

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
