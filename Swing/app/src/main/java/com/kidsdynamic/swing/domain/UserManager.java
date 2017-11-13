package com.kidsdynamic.swing.domain;

import com.kidsdynamic.data.net.ApiGen;


/**
 * Created by Administrator on 2017/11/4.
 */

public class UserManager {
    public static String getProfileRealUri(String userProfile){
        return ApiGen.BASE_PHOTO_URL + userProfile;
    }
}
