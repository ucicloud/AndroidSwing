package com.kidsdynamic.swing.model;

import java.io.Serializable;

/**
 * 界面展示使用的WatchActivity.
 */

public class WatchActivity implements Serializable {
    public static final int STEP_ALMOST = 7000;
    public static final int STEP_EXCELLENT = 10000;
    public static final int STEP_GOAL = 12000;

    public Act mIndoor = new Act();
    public Act mOutdoor = new Act();

    public static class Act implements Serializable {
        public long mId;
        public String mMacId;
        public String mKidId;
        public long mSteps;
        public long mTimestamp;
        public long mDistance;

        public Act() {
            init(0, "", "", 0, 0, 0);
        }

        public Act(Act src) {
            init(src.mId, src.mMacId, src.mKidId, src.mSteps, src.mTimestamp, src.mDistance);
        }

        void init(long id, String macId, String kidId, long steps, long timestamp, long distance) {
            mId = id;
            mMacId = macId;
            mKidId = kidId;
            mSteps = steps;
            mTimestamp = timestamp;
            mDistance = distance;
        }
    }

    public WatchActivity() {
        init(0, "", "", 0, 0, "", "", 0, 0, 0, 0);
    }

    public WatchActivity(long kidId, long timestamp) {
        init(0, "", kidId + "", 0, 0, "", kidId + "", 0, timestamp, 0, 0);
    }

    public WatchActivity(WatchActivity src) {
        init(src.mIndoor.mId,
                src.mIndoor.mMacId,
                src.mIndoor.mKidId,
                src.mIndoor.mSteps,
                src.mOutdoor.mId,
                src.mOutdoor.mMacId,
                src.mOutdoor.mKidId,
                src.mOutdoor.mSteps,
                0,
                src.mIndoor.mDistance,
                src.mOutdoor.mDistance);
    }

    private void init(
            long indoorId,
            String indoorMacId,
            String indoorKidId,
            long indoorSteps,
            long outdoorId,
            String outdoorMacId,
            String outdoorKidId,
            long outdoorSteps,
            long timestamp,
            long indoorDistance,
            long outdoorDistance) {

        mIndoor.mId = indoorId;
        mIndoor.mMacId = indoorMacId;
        mIndoor.mKidId = indoorKidId;
        mIndoor.mSteps = indoorSteps;
        mIndoor.mDistance = indoorDistance;

        mOutdoor.mId = outdoorId;
        mOutdoor.mMacId = outdoorMacId;
        mOutdoor.mKidId = outdoorKidId;
        mOutdoor.mSteps = outdoorSteps;
        mOutdoor.mDistance = outdoorDistance;

        mIndoor.mTimestamp = timestamp;
        mOutdoor.mTimestamp = timestamp;
    }

    public boolean addInTimeRange(WatchActivity src, long rangeStart, long rangeEnd) {
        if (src.mIndoor.mTimestamp >= rangeStart && src.mIndoor.mTimestamp <= rangeEnd) {
            mIndoor.mSteps += src.mIndoor.mSteps;
            mOutdoor.mSteps += src.mOutdoor.mSteps;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {

        return new StringBuilder()
                .append("{Indoor mId:").append(mIndoor.mId)
                .append(" Indoor mMacId:").append(mIndoor.mMacId)
                .append(" Indoor mKidId:").append(mIndoor.mKidId)
                .append(" Indoor mStep:").append(mIndoor.mSteps)
                .append(" Outdoor mId:").append(mOutdoor.mId)
                .append(" Outdoor mMacId:").append(mOutdoor.mMacId)
                .append(" Outdoor mKidId:").append(mOutdoor.mKidId)
                .append(" Outdoor mStep:").append(mOutdoor.mSteps)
                .append("}").toString();
    }

}
