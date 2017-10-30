package com.kidsdynamic.swing.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.yy.base.utils.FontCache;

/**
 * Swing font
 * date:   2017/10/30 17:32 <br/>
 */

public class SwingFontsCache {
    private static final String font_bold_name = "font_bold";
    private static final String font_normal_name = "font_normal";

    public static Typeface getBoldType(Context context){
        return FontCache.getTypeface(font_bold_name, context);
    }

    public static Typeface getNormalType(Context context){
        return FontCache.getTypeface(font_normal_name, context);
    }
}
