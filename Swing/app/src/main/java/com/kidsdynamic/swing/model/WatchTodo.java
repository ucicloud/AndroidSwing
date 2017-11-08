package com.kidsdynamic.swing.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class WatchTodo implements Serializable {

    public final static String STATUS_DONE = "DONE";
    public final static String STATUS_PENDING = "PENDING";

    public long mId;
    public long mUserId;
    public long mEventId;
    public String mText;
    public String mStatus;
    public long mDateCreated;
    public long mLastUpdated;

    public WatchTodo() {
        long now = System.currentTimeMillis();
        init(0, 0, 0, "", "", now, now);
    }

    public WatchTodo(int id, int userId, int eventId, String text, String status) {
        Calendar calc = Calendar.getInstance();
        long now = calc.getTimeInMillis();

        init(id, userId, eventId, text, status, now, now);
    }

    public WatchTodo(int id, int userId, int eventId, String text, String status,
                     long dateCreated, long lastUpdated) {
        init(id, userId, eventId, text, status, dateCreated, lastUpdated);
    }

    public WatchTodo(WatchTodo src) {
        init(src.mId, src.mUserId, src.mEventId, src.mText, src.mStatus, src.mDateCreated, src.mLastUpdated);
    }

    private void init(long id, long userId, long eventId, String text, String status,
                      long dateCreated, long lastUpdated) {
        mId = id;
        mUserId = userId;
        mEventId = eventId;
        mText = text;
        mStatus = status;
        mDateCreated = dateCreated;
        mLastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss", Locale.US);

        return new StringBuilder()
                .append("{mId:").append(mId)
                .append(" mUserId:").append(mUserId)
                .append(" mEventId:").append(mEventId)
                .append(" mText:").append(mText)
                .append(" mStatus:").append(mStatus)
                .append(" mDateCreated:").append(sdf.format(mDateCreated))
                .append(" mLastUpdated:").append(sdf.format(mLastUpdated))
                .append("}").toString();
    }
}
