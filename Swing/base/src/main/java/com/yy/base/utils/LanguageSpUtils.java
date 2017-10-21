package com.yy.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;


import com.yy.base.ActivityController;

import java.util.Locale;

/**
 *  多语言存储类及切换方法
 */

public class LanguageSpUtils {

    private static final String lang_sp = "language";
    private static final String key_lang = "key_lang";

    /**
     * 获取应用采用的语言的索引
     * @param context
     * @return
     */
    public static int getLocalIndex(Context context){
        return getSharedPreferences(context).getInt(key_lang,0);
    }

    /**
     * 保存 切换的语言
     * @param context
     * @param local
     */
    private static void saveLocalIndex(Context context,int local){
        SharedPreferences sharedPreferences =getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key_lang,local);
        editor.apply();
    }


    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(lang_sp,Context.MODE_PRIVATE);
    }

    /**
     * 保存设置选项 并 重新跳转到主页面
     * @param context
     * @param index
     */
    public static void saveSetting(Context context,int index,Class<?> cls){
        saveLocalIndex(context,index);
        restart(context,cls);
    }

    private static void restart(Context context,Class<?> cls){
        ActivityController.getInstance().popAllActivity();
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }



    /**
     * 根据 设置选项，显示相应的语言
     */
    public static void changeLanguage(Context context){
        int index = LanguageSpUtils.getLocalIndex(context);
        Locale locale = Locale.getDefault();

        //modify 2017年6月1日19:27:17 weizg 因为在mate8升级到最新系统后，发现当前语言为中文是，Locale.getDefault()
        //方法返回的是en，导致客户端默认显示成英文，先删除跟随系统，默认是中文显示
        if (index ==0){
            locale =Locale.SIMPLIFIED_CHINESE;
        }else if (index ==1 || !(locale.equals(Locale.SIMPLIFIED_CHINESE)) ){
            locale = Locale.ENGLISH;
        }
/*
        if (index ==1){
            locale =Locale.SIMPLIFIED_CHINESE;
        }else if (index ==2 || !(locale.equals(Locale.SIMPLIFIED_CHINESE)) ){
            locale = Locale.ENGLISH;
        }
*/
        setLanguage(context,locale);
    }

    /**
     * 设置语言
     * @param locale
     */
    private static void setLanguage(Context cxt,Locale locale) {
        Resources resources = cxt.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }


    /**
     * 判断当前应用语言是否为中文
     * @return
     */
    public static boolean isZh(Context cxt){
        Locale local = cxt.getResources().getConfiguration().locale;
        return local.equals(Locale.SIMPLIFIED_CHINESE);
    }

}
