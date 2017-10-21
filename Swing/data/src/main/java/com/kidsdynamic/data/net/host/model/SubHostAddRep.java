package com.kidsdynamic.data.net.host.model;

import com.kidsdynamic.data.net.user.model.UserInfo;

/**
 * Created by Administrator on 2017/10/21.
 */

public class SubHostAddRep {

    /**
     * id : 3
     * requestFromUser : {"id":2,"email":"jack08300@gmail.com","firstName":"Jay","lastName":"Chen","lastUpdate":"0001-01-01T00:00:00Z","dateCreated":"0001-01-01T00:00:00Z","zipCode":"11111","phoneNumber":"11111","profile":"","registrationId":"123test"}
     * requestToUser : {"id":63,"email":"jack08301@gmail.com","firstName":"JJJ","lastName":"TTT","lastUpdate":"0001-01-01T00:00:00Z","dateCreated":"2017-01-17T00:56:06Z","zipCode":"11111","phoneNumber":"","profile":""}
     * status : PENDING
     * createdDate : 2017-01-18T04:03:22Z
     * lastUpdated : 2017-01-18T04:03:22Z
     */

    private int id;
    private UserInfo requestFromUser;
    private UserInfo requestToUser;
    private String status;
    private String createdDate;
    private String lastUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getRequestFromUser() {
        return requestFromUser;
    }

    public void setRequestFromUser(UserInfo requestFromUser) {
        this.requestFromUser = requestFromUser;
    }

    public UserInfo getRequestToUser() {
        return requestToUser;
    }

    public void setRequestToUser(UserInfo requestToUser) {
        this.requestToUser = requestToUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
