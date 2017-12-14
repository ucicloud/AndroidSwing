package com.kidsdynamic.swing.ble;

/**
 * Created by maple on 2017/10/24.
 */

public interface IDeviceSyncCallback {
    void onSyncComplete();

    void onSyncFail(int reason);

    void onSyncing(String tip);

    void onSyncActivity(ActivityModel activity);

    void onDeviceBattery(int battery);

    void onDeviceVersion(String version);
}
