package com.kidsdynamic.data.net.user.model;

/**
 * Created by Administrator on 2017/10/16.
 */

public class UserInfo {

    /**
     * id : 29
     * email : lwz1@swing.com
     * firstName : KIDLLE
     * lastName : YES
     * lastUpdate : 2016-12-18T21:24:57Z
     * dateCreated : 2016-12-06T00:40:10Z
     * zipCode : 11111
     * phoneNumber : 3444943214
     * profile ://头像信息
     * language" "en",
     "ios_registration_id": "D975DE6114780B4396AB5D1A8D8DA1826035A7AD3C48BA420683E85072ED62FB",
     "android_registration_id": "",
     "country": "CN"
     */

    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String lastUpdate;
    private String dateCreated;
    private String zipCode;
    private String phoneNumber;
    private String profile;
    private String registrationId;
    private String language;
    private String ios_registration_id;
    private String android_registration_id;
    private String country;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIos_registration_id() {
        return ios_registration_id;
    }

    public void setIos_registration_id(String ios_registration_id) {
        this.ios_registration_id = ios_registration_id;
    }

    public String getAndroid_registration_id() {
        return android_registration_id;
    }

    public void setAndroid_registration_id(String android_registration_id) {
        this.android_registration_id = android_registration_id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
