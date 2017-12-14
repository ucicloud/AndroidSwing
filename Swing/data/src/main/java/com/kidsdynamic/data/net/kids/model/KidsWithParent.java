package com.kidsdynamic.data.net.kids.model;

import com.kidsdynamic.data.net.user.model.UserInfo;

import java.io.Serializable;

/**
 * date:   2017/10/21 15:33 <br/>
 */

public class KidsWithParent implements Serializable {

    /**
     * id : 2
     * name : Kids3
     * dateCreated : 2017‐02‐02T01:14:16Z
     * macId : Mac_ID4
     * firmwareVersion: "",
     * profile :
     * parent : {"id":2,"email":"jack08300@gmail.com","firstName":"Jay","lastName":"Chen","lastUpdate":"0001\u201001\u201001T00:00:00Z","dateCreated":"0001\u201001\u201001T00:00:00Z","zipCode":"","phoneNumber":"","profile":""}
     */

    private int id;
    private String name;
    private String dateCreated;
    private String macId;
    private String firmwareVersion;
    private String profile;
    private UserInfo parent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
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

    public UserInfo getParent() {
        return parent;
    }

    public void setParent(UserInfo parent) {
        this.parent = parent;
    }

}
