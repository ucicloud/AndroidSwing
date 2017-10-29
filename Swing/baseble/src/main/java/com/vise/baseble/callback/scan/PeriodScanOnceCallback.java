package com.vise.baseble.callback.scan;

import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;

import com.vise.baseble.ViseBluetooth;
import com.vise.baseble.common.BleConstant;
import com.vise.baseble.common.State;
import com.vise.baseble.model.BluetoothLeDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 扫描回调
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/8/8 21:47.
 */
public abstract class PeriodScanOnceCallback extends PeriodScanCallback {

    private List<BluetoothLeDevice> resultList = new ArrayList<>();
    private AtomicBoolean hasFound = new AtomicBoolean(false);

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        synchronized (this) {
            hasFound.set(false);
            for (BluetoothLeDevice result : resultList) {
                if (result.getDevice().equals(bluetoothDevice)) {
                    hasFound.set(true);
                }
            }
            if (!hasFound.get()) {
                BluetoothLeDevice scanResult = new BluetoothLeDevice(bluetoothDevice, rssi, scanRecord, System.currentTimeMillis());
                resultList.add(scanResult);
                onDeviceFound(scanResult);
            }
        }

    }
}
