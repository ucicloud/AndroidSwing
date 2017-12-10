package com.kidsdynamic.data.net.host.model;

/**
 * Created by Administrator on 2017/10/21.
 */

public class SubHostRemovedKidRequest {

    /**
     * subHostId : 1
     * kidId : 1
     */

    private int subHostId;
    private long kidId;

    public int getSubHostId() {
        return subHostId;
    }

    public void setSubHostId(int subHostId) {
        this.subHostId = subHostId;
    }

    public long getKidId() {
        return kidId;
    }

    public void setKidId(long kidId) {
        this.kidId = kidId;
    }
}
