package com.kidsdynamic.data.net.user.model;

/**
 * Created by Administrator on 2017/10/17.
 */

public class KidInfo {

    /**
     * id : 18
     * name : Jay
     * dateCreated : 2016-12-11T22:37:15Z
     * macId : 13031FCFE5E02
     * profile :  kid_avatar_5.jpg
     */

    private int id;
    private String name;
    private String dateCreated;
    private String macId;
    private String profile;

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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
