package com.kidsdynamic.data.net.avatar;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * date:   2017/10/18 19:57 <br/>
 */

public class PartUtils {
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";


    //http://www.cnblogs.com/gznuhaoge/p/5874042.html
    public static MultipartBody.Part prepareFilePart(String partName, String fileName, @NonNull File file){
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        return MultipartBody.Part.createFormData(partName, fileName, requestFile);
    }


    public static void putRequestBodyMap(Map map, String key, String value) {
        putRequestBodyMap(map, key, createPartFromString(value));
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null) {
            descriptionString = "";
        }

        return RequestBody.create(
                MediaType.parse("text"), descriptionString);
    }
    public static void putRequestBodyMap(Map map, String key, RequestBody body) {
        if (!TextUtils.isEmpty(key) && body != null) {
            map.put(key, body);
        }
    }
}
