package com.kidsdynamic.swing.ble;

import java.util.Date;

/**
 * Created by lwz on 2017/10/19.
 */

public class EventModel {
    private int alert;
    private Date startDate;

    public int getAlert() {
        return alert;
    }

    public void setAlert(int alert) {
        this.alert = alert;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
