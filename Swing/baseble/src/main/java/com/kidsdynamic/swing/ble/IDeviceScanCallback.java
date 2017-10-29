package com.kidsdynamic.swing.ble;

import com.vise.baseble.model.BluetoothLeDevice;

/**
 * Created by maple on 2017/10/24.
 */

public interface IDeviceScanCallback {

    void onStartScan();

    void onScanTimeOut();

    void onScanning(BluetoothLeDevice scanResult);

}
