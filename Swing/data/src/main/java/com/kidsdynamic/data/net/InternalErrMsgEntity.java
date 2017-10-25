package com.kidsdynamic.data.net;

/**
 * date:   2017/10/17 14:08 <br/>
 */

public class InternalErrMsgEntity {

    /**
     * message : Error when insert data
     * error : Test error
     */

    private String message;
    private String error;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
