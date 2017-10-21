package com.kidsdynamic.data.net.kids.model;

/**
 * Created by Administrator on 2017/10/21.
 */

public class BatteryStatus {

    /**
     * macId : test123
     * batteryLife : 98
     * DateReceived : 14442523
     */

    private String macId;
    private int batteryLife;
    private int DateReceived;

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public int getBatteryLife() {
        return batteryLife;
    }

    public void setBatteryLife(int batteryLife) {
        this.batteryLife = batteryLife;
    }

    public int getDateReceived() {
        return DateReceived;
    }

    public void setDateReceived(int DateReceived) {
        this.DateReceived = DateReceived;
    }
}
