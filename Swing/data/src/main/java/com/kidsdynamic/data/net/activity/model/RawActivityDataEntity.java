package com.kidsdynamic.data.net.activity.model;

/**
 * Created by Administrator on 2017/10/21.
 */

public class RawActivityDataEntity {
//    indoorActivity	Yes	String	1481299119,0,216,2,3,4
//    outdoorActivity	Yes	String	1481299119,1,0,0,0,0
//    timeZoneOffset	Yes	Integer	-300
//    time	Yes	Long	1470885849
//    macId	Yes	String	hgweorahgbkljwhnpi2

    private String indoorActivity;
    private String outdoorActivity;
    private int timeZoneOffset;
    private long time;
    private String macId;

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

    public int getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(int timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }
}
