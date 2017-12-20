package com.kidsdynamic.data.net.host.model;

/**
 * Created by Administrator on 2017/10/21.
 */

public class SubHostRemovedKidRequest {

    /**
     * subHostId : 1
     * kidId : 1
     */

    private long subHostId;
    private long kidId;

    public long getSubHostId() {
        return subHostId;
    }

    public void setSubHostId(long subHostId) {
        this.subHostId = subHostId;
    }

    public long getKidId() {
        return kidId;
    }

    public void setKidId(long kidId) {
        this.kidId = kidId;
    }
}
