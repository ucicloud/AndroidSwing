package com.kidsdynamic.swing.ble;

/**
 * Created by maple on 2017/10/24.
 */

public interface IDeviceInitCallback {
    void onInitComplete(String mac);

    void onInitFail(int reason);

    void onDeviceBattery(int battery);
}
