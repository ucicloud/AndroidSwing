package com.kidsdynamic.data.net.user.model;

/**
 * Created by Administrator on 2017/10/16.
 */

public class RegisterFailResponse {
    /*
    *
Status Code  Meaning


200 Register success
400 Bad request. Missing some parameters
409 Conflict. The email is already registered
500 Internal error. Please send me the error. I will fix it

•Success - it doesn't return JSON
•Fail - response body:
{
  "message": "The email is already registered"
}

•Internal error - response body:
{
  "message": "Error when insert data",
  "error": "Test error"
}

    * */

    /**
     * message : Error when insert data
     * error : Test error;字段可能为空
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
