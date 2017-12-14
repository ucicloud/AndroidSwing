package com.kidsdynamic.swing.model;

/**
 * Created by Administrator on 2017/11/9.
 */

public class KidsEntityBean {
    //分享类型：0 无；1 自己分享给别人；2 别人分享给自己
    public static final int shareType_none = 0;
    public static final int shareType_mine = 1;
    public static final int shareType_from_other = 2;

    private long kidsId;
    private String name;
    private long lastUpdate;
    private long dateCreated;
    private String macId;
    private String firmwareVersion;
    private String profile;
    private String state;
    private long parentId;
    private Integer battery;
    private long subHostId;
    private int shareType;

    //是否选中
    private boolean isSelected = false;

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

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
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

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }

    public long getSubHostId() {
        return subHostId;
    }

    public void setSubHostId(long subHostId) {
        this.subHostId = subHostId;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
