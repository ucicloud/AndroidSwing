package com.kidsdynamic.swing.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.kidsdynamic.swing.utils.SwingFontsCache;

/**
 * AvenirTextView：
 *
 * android:textStyle="bold"
 * or normal
 * date:   2017/10/30 15:02 <br/>
 */

public class AvenirTextView extends TextView {
    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public AvenirTextView(Context context) {
        super(context);
    }

    public AvenirTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context,attrs);
    }

    public AvenirTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context,attrs);
    }

    public AvenirTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        applyCustomFont(context,attrs);
    }


    public void applyCustomFont(Context context, AttributeSet attrs){
        if(attrs == null){
            return;
        }

        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        //为了减少内存使用，使用缓存的typeface
        if(textStyle == Typeface.BOLD){
//            setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font_bold"));
            setTypeface(SwingFontsCache.getBoldType(getContext()));
        }else {

//            setTypeface(Typeface.createFromAsset(getContext().getAssets(),"font_normal"));
            setTypeface(SwingFontsCache.getNormalType(getContext()));
        }

    }
}
