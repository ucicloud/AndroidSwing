package com.kidsdynamic.swing.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

/**
 * ViewUtils
 * Created by Administrator on 2017/12/3.
 */

public class ViewUtils {

    public static SpannableStringBuilder setWordColorInStr(String rawStr, String... colorStrs){

        SpannableStringBuilder builder = new SpannableStringBuilder(rawStr);

        if(TextUtils.isEmpty(rawStr) || colorStrs == null || colorStrs.length <= 0){
            return builder;
        }



        for (String str :
                colorStrs) {
            int start = rawStr.indexOf(str);
            int end = start + str.length();

            ForegroundColorSpan lightBlueSpan = new ForegroundColorSpan(Color.parseColor("#14C0BD"));
            builder.setSpan(lightBlueSpan,start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;

    }
}
