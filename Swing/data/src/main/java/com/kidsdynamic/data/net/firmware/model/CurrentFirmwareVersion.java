package com.kidsdynamic.data.net.firmware.model;

/**
 * Created by Administrator on 2017/10/21.
 */

public class CurrentFirmwareVersion {
    private String macId; //A81B6ABA0749
    private String firmwareVersion; //KDV0106-J

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
}
