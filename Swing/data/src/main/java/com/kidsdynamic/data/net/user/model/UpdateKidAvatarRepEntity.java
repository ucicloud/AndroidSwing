package com.kidsdynamic.data.net.user.model;

/**
 * Created by Administrator on 2017/10/18.
 */

public class UpdateKidAvatarRepEntity {

    /**
     * kid : {"id":13,"name":"kid13","dateCreated":"2017-01-29T23:07:38Z","macId":"Mac_ID3","profile":"kid_avatar_13.jpg","parent":{"id":5,"email":"jack08301@gmail.com","name":"Jay","lastUpdate":"2017-01-11T04:16:44Z","dateCreated":"2017-01-11T04:16:44Z","zipCode":"11111","phoneNumber":"11111","profile":"avatar_5.jpg","registrationId":"123test"}}
     */

    private KidEntity kid;

    public KidEntity getKid() {
        return kid;
    }

    public void setKid(KidEntity kid) {
        this.kid = kid;
    }

    public static class KidEntity {
        /**
         * id : 13
         * name : kid13
         * dateCreated : 2017-01-29T23:07:38Z
         * macId : Mac_ID3
         * profile : kid_avatar_13.jpg
         * parent : {"id":5,"email":"jack08301@gmail.com","name":"Jay","lastUpdate":"2017-01-11T04:16:44Z","dateCreated":"2017-01-11T04:16:44Z","zipCode":"11111","phoneNumber":"11111","profile":"avatar_5.jpg","registrationId":"123test"}
         */

        private int id;
        private String name;
        private String dateCreated;
        private String macId;
        private String profile;
        private ParentEntity parent;

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

        public ParentEntity getParent() {
            return parent;
        }

        public void setParent(ParentEntity parent) {
            this.parent = parent;
        }

        public static class ParentEntity {
            /**
             * id : 5
             * email : jack08301@gmail.com
             * name : Jay
             * lastUpdate : 2017-01-11T04:16:44Z
             * dateCreated : 2017-01-11T04:16:44Z
             * zipCode : 11111
             * phoneNumber : 11111
             * profile : avatar_5.jpg
             * registrationId : 123test
             */

            private int id;
            private String email;
            private String name;
            private String lastUpdate;
            private String dateCreated;
            private String zipCode;
            private String phoneNumber;
            private String profile;
            private String registrationId;

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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
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
        }
    }
}
