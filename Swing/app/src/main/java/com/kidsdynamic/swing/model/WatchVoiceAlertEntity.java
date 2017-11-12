package com.kidsdynamic.swing.model;

/**
 * WatchVoiceAlertEntity: 向watch同步数据的业务bean </br></br>
 * Created by only_app on 2017/11/12.
 */

public class WatchVoiceAlertEntity {
    byte mAlert;
    long mTimeStamp;

    public WatchVoiceAlertEntity(byte number, long countdown) {
        mAlert = number;
        mTimeStamp = countdown;
    }
}
