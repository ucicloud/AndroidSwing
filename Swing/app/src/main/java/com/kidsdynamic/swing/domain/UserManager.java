package com.kidsdynamic.swing.domain;

import static com.kidsdynamic.data.net.avatar.AvatarApi.BASE_PHOTO_URL;

/**
 * Created by Administrator on 2017/11/4.
 */

public class UserManager {
    public static String getProfileRealUri(String userProfile){
        return BASE_PHOTO_URL + userProfile;
    }
}
