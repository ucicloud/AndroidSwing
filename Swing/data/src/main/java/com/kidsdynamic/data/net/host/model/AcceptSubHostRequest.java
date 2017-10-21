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

    private int subHostId;
    private List<Integer> kidId;

    public int getSubHostId() {
        return subHostId;
    }

    public void setSubHostId(int subHostId) {
        this.subHostId = subHostId;
    }

    public List<Integer> getKidId() {
        return kidId;
    }

    public void setKidId(List<Integer> kidId) {
        this.kidId = kidId;
    }
}
