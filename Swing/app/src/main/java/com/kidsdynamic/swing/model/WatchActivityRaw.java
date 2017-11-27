package com.kidsdynamic.swing.model;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by 03542 on 2017/2/21.
 */

public class WatchActivityRaw {
    String mMacId;
    int mTime;
    String mOutdoor;
    String mIndoor;

    private int mOffset = 0;

    WatchActivityRaw() {
        init("", 0, "", "");
    }

    WatchActivityRaw(String macId, byte[] time, byte[] outdoor, byte[] indoor) {
        Calendar now = Calendar.getInstance();
        mOffset = (now.getTimeZone().getOffset(now.getTimeInMillis()))/1000;

        init(macId,
                (byteToDec(time[0], time[1], time[2], time[3]) - mOffset),
                rawString(outdoor),
                rawString(indoor));
    }

    private void init(String macId, int time, String outdoor, String indoor) {
        mMacId = macId;
        mTime = time;
        mOutdoor = outdoor;
        mIndoor = indoor;
    }

    private int byteToDec(byte b0, byte b1, byte b2, byte b3) {
        int dec;

        dec = b0 & 0xFF;
        dec |= (b1 << 8) & 0xFF00;
        dec |= (b2 << 16) & 0xFF0000;
        dec |= (b3 << 24) & 0xFF000000;

        return dec;
    }

    private String byteToStr(byte b0, byte b1, byte b2, byte b3) {
        return "" + byteToDec(b0, b1, b2, b3);
    }

    private String rawString(byte[] b) {
        return String.format(Locale.getDefault(), "%s,%d,%s,%s,%s,%s",
                (byteToDec(b[0], b[1], b[2], b[3]) - mOffset) + "",
                b[4],
                byteToStr(b[5], b[6], b[7], b[8]),
                byteToStr(b[9], b[10], b[11], b[12]),
                byteToStr(b[13], b[14], b[15], b[16]),
                byteToStr(b[17], b[18], b[19], b[20])
        );
    }
}
