package com.kidsdynamic.swing.model;

/**
 * Created by Administrator on 2017/11/9.
 */

public class KidsEntityBean {
    private long kidsId;
    private String name;
    private Long dateCreated;
    private String macId;
    private String firmwareVersion;
    private String profile;
    private String state;
    private long parentId;

    public long getKidsId() {
        return kidsId;
    }

    public void setKidsId(long kidsId) {
        this.kidsId = kidsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }
}
