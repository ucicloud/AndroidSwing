package com.kidsdynamic.swing.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * WatchContact
 */

public class WatchContact implements Serializable {
    public Bitmap mPhoto;
    public String mLabel;

    public WatchContact() {
        mPhoto = null;
        mLabel = "";
    }

    public WatchContact(Bitmap photo, String label) {
        mPhoto = photo;
        mLabel = label;
    }

    @Override
    public String toString() {

        return new StringBuilder()
                .append("{mPhoto:").append(mPhoto != null ? "Exist" : "null")
                .append(" mLabel:").append(mLabel)
                .append("}").toString();
    }

    public static class Kid extends WatchContact {
        public long mId;
        public String mName;
        public long mLastUpdate;
        public long mDateCreated;
        public String mMacId;
        public String mFirmwareVersion;
        public String mProfile;
        public long mUserId;
        public boolean mBound = false;

        public Kid() {
            super(null, "");
            mId = 0;
            mName = "";
            mLastUpdate = 0;
            mDateCreated = 0;
            mMacId = "";
            mUserId = 0;
            mProfile = "";
            mFirmwareVersion = "";

            mBound = false;
        }

        public Kid(Bitmap photo, String label) {
            super(photo, label);
            mId = 0;
            mName = "";
            mLastUpdate = 0;
            mDateCreated = 0;
            mMacId = "";
            mUserId = 0;
            mProfile = "";
            mFirmwareVersion = "";
            mBound = false;
        }

        public Kid(Bitmap photo, int id, String firstName, String lastName, long lastUpdate,long dateCreated, String macId, int parentId) {
            super(photo, firstName + " " + lastName);

            mId = id;
            mName = firstName;
            mLastUpdate = lastUpdate;
            mDateCreated = dateCreated;
            mMacId = macId;
            mUserId = parentId;
            mProfile = "";
            mBound = true;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss", Locale.US);

            return new StringBuilder()
                    .append("{contact:").append(super.toString())
                    .append(" mId:").append(mId)
                    .append(" mName:").append(mName)
                    .append(" mDateCreated:").append(sdf.format(mDateCreated))
                    .append(" mMacId").append(sdf.format(mMacId))
                    .append(" mUserId:").append(mUserId)
                    .append(" mProfile:").append(mProfile)
                    .append(" mBound:").append(mBound)
                    .append("}").toString();
        }
    }

    public static class User extends WatchContact {
        public long mId;
        public String mEmail;
        public String mFirstName;
        public String mLastName;
        public long mLastUpdate;
        public long mDateCreated;
        public String mZipCode;
        public String mPhoneNumber;
        public String mProfile;
        public int mSubHostId;
        public String mRequestStatus;
        public List<Kid> mRequestKids;

        public final static String STATUS_PENDING = "PENDING";
        public final static String STATUS_ACCEPTED = "ACCEPTED";

        public User() {
            super(null, "");
            mId = 0;
            mEmail = "";
            mFirstName = "";
            mLastName = "";
            mLastUpdate = 0;
            mDateCreated = 0;
            mZipCode = "";
            mPhoneNumber = "";
            mProfile = "";
            mRequestStatus = "";
            mRequestKids = new ArrayList<>();
        }

        public User(Bitmap photo, String label) {
            super(photo, label);
            mId = 0;
            mEmail = "";
            mFirstName = "";
            mLastName = "";
            mLastUpdate = 0;
            mDateCreated = 0;
            mZipCode = "";
            mPhoneNumber = "";
            mProfile = "";
            mRequestStatus = "";
            mRequestKids = new ArrayList<>();
        }

        public User(Bitmap photo, long id, String email, String firstName, String lastName, long lastUpdate, long dateCreated, String zipCode, String phoneNumber, String profile) {
            super(photo, firstName + " " + lastName);
            mId = id;
            mEmail = email;
            mFirstName = firstName;
            mLastName = lastName;
            mLastUpdate = lastUpdate;
            mDateCreated = dateCreated;
            mZipCode = zipCode;
            mPhoneNumber = phoneNumber;
            mProfile = profile;
            mRequestStatus = "";
            mRequestKids = new ArrayList<>();
        }

        public User(Bitmap photo, long id, String email, String firstName, String lastName, long lastUpdate, long dateCreated, String zipCode, String phoneNumber) {
            super(photo, firstName + " " + lastName);
            mId = id;
            mEmail = email;
            mFirstName = firstName;
            mLastName = lastName;
            mLastUpdate = lastUpdate;
            mDateCreated = dateCreated;
            mZipCode = zipCode;
            mPhoneNumber = phoneNumber;
            mProfile = "";
            mRequestStatus = "";
            mRequestKids = new ArrayList<>();
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss", Locale.US);

            return new StringBuilder()
                    .append("{contact:").append(super.toString())
                    .append(" mId:").append(mId)
                    .append(" mEmail:").append(mEmail)
                    .append(" mName:").append(mFirstName)
                    .append(" mLastName:").append(mLastName)
                    .append(" mLastUpdate:").append(sdf.format(mLastUpdate))
                    .append(" mDateCreated:").append(sdf.format(mDateCreated))
                    .append(" mZip").append(mZipCode)
                    .append(" mPhoneNumber:").append(mPhoneNumber)
                    .append(" mProfile:").append(mProfile)
                    .append("}").toString();
        }
    }

   /* static View inflate(Context context, @LayoutRes int layout, WatchContact contact) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, null);

        ViewCircle viewPhoto = (ViewCircle) view.findViewById(R.id.watch_contact_photo);
        viewPhoto.setBitmap(contact.mPhoto);

        TextView viewLabel = (TextView) view.findViewById(R.id.watch_contact_label);
        viewLabel.setText(contact.mLabel);

        int height = context.getResources().getDisplayMetrics().heightPixels / 12;
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        layoutParams.gravity = Gravity.CENTER;
        view.setLayoutParams(layoutParams);

        view.setTag(contact);

        return view;
    }

    static View inflateBind(Context context, Kid device) {
        View view = inflate(context, R.layout.watch_contact_bind, device);

        ImageView viewIcon = (ImageView) view.findViewById(R.id.watch_contact_bind_icon);
        viewIcon.setImageResource(device.mBound ? R.mipmap.iconbutton_bind : R.mipmap.iconbutton_add);

        return view;
    }

    static View inflateOnOff(Context context, WatchContact contact) {
        View view = inflate(context, R.layout.watch_contact_onoff, contact);

        return view;
    }

    static View inflateButton(Context context, WatchContact contact, String text) {
        View view = inflate(context, R.layout.watch_contact_button, contact);

        Button button = (Button) view.findViewById(R.id.watch_contact_button_button);
        button.setText(text);

        return view;
    }

    static View inflateCheck(Context context, WatchContact contact, boolean isCheck) {
        View view = inflate(context, R.layout.watch_contact_check, contact);

        ImageView icon = (ImageView) view.findViewById(R.id.watch_contact_check_icon);
        icon.setSelected(isCheck);

        return view;
    }*/
}
