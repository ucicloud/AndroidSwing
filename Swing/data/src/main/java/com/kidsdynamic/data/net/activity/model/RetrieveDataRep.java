package com.kidsdynamic.data.net.activity.model;

import java.util.List;

/**
 * Created by Administrator on 2017/10/21.
 */

public class RetrieveDataRep {

    private List<ActivitiesEntity> activities;

    public List<ActivitiesEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivitiesEntity> activities) {
        this.activities = activities;
    }

    public static class ActivitiesEntity {
        /**
         * id : 121
         * macId : hgweorahgbkljwhnpi2
         * kidId : 20
         * type : INDOOR
         * steps : 3298
         * distance : 0
         * receivedDate : 2016-12-13T19:55:02Z
         */

        private int id;
        private String macId;
        private String kidId;
        public String type;
        private int steps;
        private int distance;
        private String receivedDate;

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
    }
}
