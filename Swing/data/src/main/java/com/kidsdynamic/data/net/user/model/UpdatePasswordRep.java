package com.kidsdynamic.data.net.user.model;

/**
 * UpdatePasswordRep
 * Created by  on 2017/12/22.
 */

public class UpdatePasswordRep {

    /*
    *
Success - Return 200 status with empty json
{
}
Error 400
{
    "message": "The password has to be longer than 6 characters"
}
    * */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
