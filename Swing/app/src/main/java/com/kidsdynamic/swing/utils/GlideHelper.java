package com.kidsdynamic.swing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.ObjectKey;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;

import java.util.concurrent.Executors;

/**
 * GlideHelper
 * <p>
 * Created by Stefan on 2017/10/28.
 */

public class GlideHelper {

    public static void showCircleImageView(Context context, Object mode, ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.default_avatar);
            Glide.with(context.getApplicationContext())
                    .load(mode)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCircleImageViewWithSignature(Context context, Object mode, String lastModify, ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .signature(new ObjectKey(lastModify));
            Glide.with(context.getApplicationContext())
                    .load(mode)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSquareImageView(Context context, Object mode, ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.fitCenterTransform();
            Glide.with(context.getApplicationContext())
                    .load(mode)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getBitMap(Context context, String uri, String lastModify, SimpleTarget<Bitmap> SimpleTarget) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.default_avatar)
                    .signature(new ObjectKey(lastModify));

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getBitMapFromCloud(Context context, String uri, SimpleTarget<Bitmap> SimpleTarget) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.default_avatar)
                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*$$$$$$$$$$$$$$$$$$$$$$$$$*/
    //方法慎用，会全部清除缓存的图片
    /*$$$$$$$$$$$$$$$$$$$$$$$$$*/
    public static void clearCache(){
        try {

            Glide.get(SwingApplication.getAppContext()).clearMemory();

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Glide.get(SwingApplication.getAppContext()).clearDiskCache();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取img并显示到指定imagview；该方法只在内存缓存获取到的图片
     * @param context context
     * @param mode url
     * @param imageView 要显示的控件
     */
    public static void getCircleImageViewOnlyCacheInMemory(Context context, Object mode, ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.default_avatar)
                    .skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(context.getApplicationContext())
                    .load(mode)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
