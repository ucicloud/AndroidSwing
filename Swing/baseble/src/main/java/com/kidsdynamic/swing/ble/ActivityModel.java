package com.kidsdynamic.swing.ble;

import java.util.Calendar;

/**
 * Created by lwz on 2017/10/19.
 */

public class ActivityModel {
    private String indoorActivity;
    private String outdoorActivity;
    private int time;
    private String macId;
    private int timeZoneOffset;

    private static int byteToDec(byte[] data, int pos) {
        int dec;

        dec = data[pos] & 0xFF;
        dec |= (data[pos + 1] << 8) & 0xFF00;
        dec |= (data[pos + 2] << 16) & 0xFF0000;
        dec |= (data[pos + 3] << 24) & 0xFF000000;

        return dec;
    }

    public void parseRawData(String mac, int timeStamp, byte[] data1, byte[] data2) {
        Calendar now = Calendar.getInstance();
        int offset = Calendar.getInstance().getTimeZone().getOffset(now.getTimeInMillis()) / 1000;
        timeZoneOffset = offset / 60;
        time = timeStamp - offset;
        macId = mac.replaceAll(":", "");
        if (data1 != null) {
            parseData(data1);
        }
        if (data2 != null) {
            parseData(data2);
        }
    }

    private void parseData(byte[] data) {
        int d1 = 0, d2 = 0, d3 = 0, d4 = 0;
        if (data[4] == 0x01) {
            for (int i = 5, index = 0; i < data.length; i+=4, index++) {
                switch(index) {
                    case 0:
                        d1 = byteToDec(data, i);
                        break;
                    case 1:
                        d2 = byteToDec(data, i);
                        break;
                    case 2:
                        d3 = byteToDec(data, i);
                        break;
                    case 3:
                        d4 = byteToDec(data, i);
                        break;
                }
            }
            StringBuffer sb = new StringBuffer();
            sb.append(time);
            sb.append(",");
            sb.append(1);
            sb.append(",");
            sb.append(d1);
            sb.append(",");
            sb.append(d2);
            sb.append(",");
            sb.append(d3);
            sb.append(",");
            sb.append(d4);
            outdoorActivity = sb.toString();
        }
        else {
            for (int i = 5, index = 0; i < data.length; i+=4, index++) {
                switch(index) {
                    case 0:
                        d1 = byteToDec(data, i);
                        break;
                    case 1:
                        d2 = byteToDec(data, i);
                        break;
                    case 2:
                        d3 = byteToDec(data, i);
                        break;
                    case 3:
                        d4 = byteToDec(data, i);
                        break;
                }
            }
            StringBuffer sb = new StringBuffer();
            sb.append(time);
            sb.append(",");
            sb.append(0);
            sb.append(",");
            sb.append(d1);
            sb.append(",");
            sb.append(d2);
            sb.append(",");
            sb.append(d3);
            sb.append(",");
            sb.append(d4);
            indoorActivity = sb.toString();
        }
    }

    public String getIndoorActivity() {
        return indoorActivity;
    }

    public void setIndoorActivity(String indoorActivity) {
        this.indoorActivity = indoorActivity;
    }

    public String getOutdoorActivity() {
        return outdoorActivity;
    }

    public void setOutdoorActivity(String outdoorActivity) {
        this.outdoorActivity = outdoorActivity;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public int getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(int timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }
}
