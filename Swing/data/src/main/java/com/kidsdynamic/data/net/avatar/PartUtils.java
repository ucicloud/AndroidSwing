package com.kidsdynamic.data.net.avatar;

import android.support.annotation.NonNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * date:   2017/10/18 19:57 <br/>
 */

public class PartUtils {

    //http://www.cnblogs.com/gznuhaoge/p/5874042.html
    public static MultipartBody.Part prepareFilePart(String partName, String fileName, @NonNull File file){
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        return MultipartBody.Part.createFormData(partName, fileName, requestFile);
    }
}
