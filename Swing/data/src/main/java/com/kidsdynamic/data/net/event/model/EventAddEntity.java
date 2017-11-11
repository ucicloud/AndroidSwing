package com.kidsdynamic.data.net.event.model;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class EventAddEntity {

    /**
     * kidId : [3,4]
     * Name : Other kids event
     * startDate : 2017-02-23T08:20:00Z
     * endDate : 2017-08-31T08:20:00Z
     * timezoneOffset : 300
     * color : #F05D25
     * description : ahhahahahahhaa
     * alert : 25
     * todo : ["test todo 1","test todo 2"]
     */

    private String Name;
    private String startDate;
    private String endDate;
    private int timezoneOffset;
    private String color;
    private String description;
    private int alert;
    private String repeat;//DAILY, MONTHLY
    private List<Long> kidId;
    private List<String> todo;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
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

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public List<Long> getKidId() {
        return kidId;
    }

    public void setKidId(List<Long> kidId) {
        this.kidId = kidId;
    }

    public List<String> getTodo() {
        return todo;
    }

    public void setTodo(List<String> todo) {
        this.todo = todo;
    }
}
