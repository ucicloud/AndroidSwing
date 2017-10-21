package com.kidsdynamic.data.net.event.model;

import com.kidsdynamic.data.net.user.model.KidInfo;
import com.kidsdynamic.data.net.user.model.UserInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */

public class EventWithTodo {

    /**
     * id : 4
     * user : {"id":2,"email":"jack08300@gmail.com","firstName":"Jay","lastName":"Chen","lastUpdate":"0001-01-01T00:00:00Z","dateCreated":"0001-01-01T00:00:00Z","zipCode":"","phoneNumber":"","profile":"avatar_2.jpg"}
     * kids : [{"id":5,"name":"hello","dateCreated":"2017-02-03T00:40:15Z","macId":"8D071FCFE5E0","profile":"kid_avatar_5.jpg"}]
     * name : Test event name2
     * startDate : 2017-02-23T08:20:00Z
     * endDate : 2017-08-31T08:20:00Z
     * color : #F05D25
     * status : OPEN
     * description : Hafdewadhah
     * alert : 49
     * repeat : "DAILY"
     * timezoneOffset : 300
     * dateCreated : 2017-02-12T22:41:55Z
     * lastUpdated : 2017-02-12T22:41:55Z
     * todo : [{"id":12,"text":"test todo 2","status":"PENDING","dateCreated":"2017-02-13T04:59:44Z","lastUpdated":"2017-02-13T01:01:51Z"}]
     */

    private int id;
    private UserInfo user;
    private String name;
    private String startDate;
    private String endDate;
    private String color;
    private String status;
    private String description;
    private int alert;
    private String repeat;
    private int timezoneOffset;
    private String dateCreated;
    private String lastUpdated;
    private List<KidInfo> kids;
    private List<TodoEntity> todo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAlert() {
        return alert;
    }

    public void setAlert(int alert) {
        this.alert = alert;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<KidInfo> getKids() {
        return kids;
    }

    public void setKids(List<KidInfo> kids) {
        this.kids = kids;
    }

    public List<TodoEntity> getTodo() {
        return todo;
    }

    public void setTodo(List<TodoEntity> todo) {
        this.todo = todo;
    }

}
