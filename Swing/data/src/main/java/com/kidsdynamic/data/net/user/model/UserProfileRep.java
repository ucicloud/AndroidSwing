package com.kidsdynamic.data.net.user.model;

import java.util.List;

/**
 * /v1/user/retrieveUserProfile 响应结果
 * Created by Administrator on 2017/10/17.
 */

public class UserProfileRep {

    /**
     * kids : [{"id":18,"name":"Jay","dateCreated":"2016-12-11T22:37:15Z","macId":"13031FCFE5E02","profile":""},{"id":19,"name":"KIDLLE","dateCreated":"2016-12-18T04:17:35Z","macId":"hgweorahgbkljwhnpi","profile":""},{"id":20,"name":"KIDLLE","dateCreated":"2016-12-18T21:19:54Z","macId":"hgweorahgbkljwhnpi2","profile":""}]
     * user : {"id":29,"email":"lwz1@swing.com","name":"KIDLLE","lastUpdate":"2016-12-18T21:24:57Z","dateCreated":"2016-12-06T00:40:10Z","zipCode":"11111","phoneNumber":"","profile":""}
     */

    private UserEntity user;
    private List<KidsEntity> kids;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<KidsEntity> getKids() {
        return kids;
    }

    public void setKids(List<KidsEntity> kids) {
        this.kids = kids;
    }

    public static class UserEntity {
        /**
         "user": {
         "id": 169,
         "email": "lwz3@swing.com",
         "firstName": "q",
         "lastName": "s",
         "lastUpdate": "2017-05-18T13:25:14Z",
         "dateCreated": "2017-03-30T02:07:14Z",
         "zipCode": "",
         "phoneNumber": "",
         "profile": "",
         "language": "es",
         "ios_registration_id": "0E6C48FCF11FCE28C6501F5819FEF348246936DB6F5BFC10AE86981DC19F30A1",
         "android_registration_id": "",
         "country": "CN"
         }
         */

        private long id;
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

        public long getId() {
            return id;
        }

        public void setId(long id) {
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
    }

    public static class KidsEntity {
        /**
         * id : 18
         * name : Jay
         * dateCreated : 2016-12-11T22:37:15Z
         * macId : 13031FCFE5E02
         * profile :
         */

        private int id;
        private String name;
        private String dateCreated;
        private String macId;
        private String profile;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getMacId() {
            return macId;
        }

        public void setMacId(String macId) {
            this.macId = macId;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }
    }
}
