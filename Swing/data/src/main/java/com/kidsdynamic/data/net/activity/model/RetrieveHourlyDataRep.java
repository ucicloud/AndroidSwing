package com.kidsdynamic.data.net.activity.model;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class RetrieveHourlyDataRep {

    private List<ActivitiesEntity> activities;

    public List<ActivitiesEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivitiesEntity> activities) {
        this.activities = activities;
    }

    public static class ActivitiesEntity {
        /**
         * "id": 51,
         * "macId": "testtter1",
         * "kidId": 120,
         * "type": "INDOOR",
         * "steps": 10,
         * "distance": 0,
         * "receivedDate": "2017-11-26T23:59:59Z",
         * "DateCreated": "2017-11-26T22:07:11Z",
         * "LastUpdated": "2017-11-26T22:07:11Z"
         */

        private int id;
        private String macId;
        private String kidId;
        public String type;
        private int steps;
        private int distance;
        private String receivedDate;
        private String dateCreated;
        private String lastUpdated;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMacId() {
            return macId;
        }

        public void setMacId(String macId) {
            this.macId = macId;
        }

        public String getKidId() {
            return kidId;
        }

        public void setKidId(String kidId) {
            this.kidId = kidId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getSteps() {
            return steps;
        }

        public void setSteps(int steps) {
            this.steps = steps;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public String getReceivedDate() {
            return receivedDate;
        }

        public void setReceivedDate(String receivedDate) {
            this.receivedDate = receivedDate;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
        }
    }

}
