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

    public final static String OAD_SERVICE_UUID = "F000FFC0-0451-4000-B000-000000000000";
    public final static String OAD_IMAGE_NOTIFY_UUID = "F000FFC1-0451-4000-B000-000000000000";
    public final static String OAD_IMAGE_BLOCK_REQUEST_UUID = "F000FFC2-0451-4000-B000-000000000000";

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

    public static final int MSG_UPGRADE_ENABLE_NOTIFY = 12;
    public static final int MSG_UPGRADE_CHECK_IMAGE_A = 13;
    public static final int MSG_UPGRADE_CHECK_IMAGE_B = 14;
    public static final int MSG_UPGRADE_CHECK_IMAGE_TIMEOUT = 15;
    public static final int MSG_UPGRADE_DOWNING_IMAGE_HEADER = 16;
    public static final int MSG_UPGRADE_DOWNING_IMAGE = 17;
    public static final int MSG_UPGRADE_DOWNING_IMAGE_NO_RESPONSE = 18;
    public static final int MSG_UPGRADE_DONE_IMAGE = 19;

    public static final int BLE_INIT_ACTION = 1;
    public static final int BLE_SYNC_ACTION = 2;
    public static final int BLE_UPGRADE_ACTION = 3;

    public static final int OAD_IMG_HDR_OSET = 0x0002;
    // Image Identification size
    public static final int OAD_IMG_ID_SIZE = 4;

    // Image header size (version + length + image id size)
    public static final int OAD_IMG_HDR_SIZE = ( 2 + 2 + OAD_IMG_ID_SIZE );
// The Image is transporte in 16-byte blocks in order to avoid using blob operations.
    public static final int OAD_BLOCK_SIZE = 16;

    public static final int HAL_FLASH_WORD_SIZE = 4;

    public static final int OAD_TRANSMIT_INTERVAL = 30;//ms
    public static final int OAD_ONCE_NUMBER = 4;
}

