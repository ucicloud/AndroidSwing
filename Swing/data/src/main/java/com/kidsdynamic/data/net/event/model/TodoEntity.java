package com.kidsdynamic.data.net.event.model;

/**
 * Created by Administrator on 2017/10/17.
 */

public class TodoEntity {
    /**
     * id : 12
     * text : test to-do 2
     * status : PENDING
     * dateCreated : 2017-02-13T04:59:44Z
     * lastUpdated : 2017-02-13T01:01:51Z
     */

    private int id;
    private String text;
    private String status;
    private String dateCreated;
    private String lastUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
