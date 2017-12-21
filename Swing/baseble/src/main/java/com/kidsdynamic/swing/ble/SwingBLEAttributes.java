package com.kidsdynamic.swing.ble;

/**
 * Created by maple on 2017/1/11.
 */

public class SwingBLEAttributes {
    public final static String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public final static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

    public final static String DEVICE_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    public final static String FIRMWARE_VERSION = "00002a26-0000-1000-8000-00805f9b34fb";

    public final static String WATCH_SERVICE = "0000ffa0-0000-1000-8000-00805f9b34fb";
    public final static String ACCEL_ENABLE = "0000ffa1-0000-1000-8000-00805f9b34fb";
    public final static String ACCEL_RANGE = "0000ffa2-0000-1000-8000-00805f9b34fb";
    public final static String TIME = "0000ffa3-0000-1000-8000-00805f9b34fb";
    public final static String DATA = "0000ffa4-0000-1000-8000-00805f9b34fb";
    public final static String CHECKSUM = "0000ffa5-0000-1000-8000-00805f9b34fb";
    public final static String ADDRESS = "0000ffa6-0000-1000-8000-00805f9b34fb";
    public final static String VOICE_ALERT = "0000ffa7-0000-1000-8000-00805f9b34fb";
    public final static String VOICE_EVET_ALERT_TIME = "0000ffa8-0000-1000-8000-00805f9b34fb";
    public final static String HEADER = "0000ffa9-0000-1000-8000-00805f9b34fb";

    public static final int MSG_INIT_WRITE_ACCEL = 1;
    public static final int MSG_INIT_WRITE_TIME = 2;
    public static final int MSG_INIT_READ_ADDR = 3;

    public static final int MSG_SYNC_WRITE_ALERT_NUMBER = 4;
    public static final int MSG_SYNC_WRITE_ALERT_TIME = 5;
    public static final int MSG_SYNC_READ_HEADER = 6;
    public static final int MSG_SYNC_READ_TIME = 7;
    public static final int MSG_SYNC_READ_DATA1 = 8;
    public static final int MSG_SYNC_READ_DATA2 = 9;
    public static final int MSG_SYNC_WRITE_CHECK_SUM = 10;

    public static final int MSG_SYNC_READ_VERSION = 11;

    public static final int BLE_INIT_ACTION = 1;
    public static final int BLE_SYNC_ACTION = 2;
    public static final int BLE_UPGRADE_ACTION = 3;

}

