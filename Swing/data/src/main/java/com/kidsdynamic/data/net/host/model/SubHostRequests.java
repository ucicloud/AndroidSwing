package com.kidsdynamic.data.net.host.model;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class SubHostRequests {

    private List<RequestAddSubHostEntity> requestFrom;
    private List<RequestAddSubHostEntity> requestTo;

    public List<RequestAddSubHostEntity> getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(List<RequestAddSubHostEntity> requestFrom) {
        this.requestFrom = requestFrom;
    }

    public List<RequestAddSubHostEntity> getRequestTo() {
        return requestTo;
    }

    public void setRequestTo(List<RequestAddSubHostEntity> requestTo) {
        this.requestTo = requestTo;
    }
}
