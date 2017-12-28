package com.kidsdynamic.data.net.activity.model;

import java.util.List;

/**
 * RetrieveMonthlyActivityRep
 * <p>
 * Created by Stefan on 2017/12/27.
 */

public class RetrieveMonthlyActivityRep {

    private List<ActivitiesEntity> activities;

    public List<ActivitiesEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivitiesEntity> activities) {
        this.activities = activities;
    }

    public static class ActivitiesEntity {
//        "macId": "AAAAAABBBB01",
//        "type": "INDOOR",
//        "month": 2,
//        "steps": 346,
//        "distance": 0

        private String macId;
        public String type;
        private int month;
        private int steps;
        private int distance;

        public String getMacId() {
            return macId;
        }

        public void setMacId(String macId) {
            this.macId = macId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
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

    }

}
