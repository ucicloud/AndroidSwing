package com.kidsdynamic.data.net.host.model;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class AcceptSubHostRequest {

    /**
     * subHostId : 1
     * kidId : [9,10]
     */

    private long subHostId;
    private List<Long> kidId;

    public long getSubHostId() {
        return subHostId;
    }

    public void setSubHostId(long subHostId) {
        this.subHostId = subHostId;
    }

    public List<Long> getKidId() {
        return kidId;
    }

    public void setKidId(List<Long> kidId) {
        this.kidId = kidId;
    }
}
