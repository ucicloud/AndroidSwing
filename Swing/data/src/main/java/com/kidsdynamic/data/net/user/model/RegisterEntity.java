package com.kidsdynamic.data.net.user.model;

/**
 * Created by Administrator on 2017/10/16.
 */

public class RegisterEntity {

    /*
    Parameters Required Type Example
    email Yes String test@kidsdynami.com
    password Yes String aaaaaa
    firstName Yes String Jay
    lastName Yes String Chen
    phoneNumber No String 3442314231
    zipCode No String 11101
    language No String en, ja, es, fr

    * */

    String email;
    String password;
    String firstName;
    String lastName;
    String phoneNumber;
    String zipCode;
//    String language; //example:en, ja, es, fr

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }


/*    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }*/
}
