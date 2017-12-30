package com.kidsdynamic.swing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.SwingApplication;

import java.io.File;

/**
 * 2017年12月30日11:05:33
 * 1. 头像id每次更新都会变更，不在使用本地的lastModify字段；其他用户头像也不再使用缓存一个小时的策略；logout也只清除内存，不再清除disk
 *
 * GlideHelper
 * <p>
 * Created by Stefan on 2017/10/28.
 */

public class GlideHelper {

    private static RequestOptions getRequestOptions(String lastModify) {
        return RequestOptions.circleCropTransform()
                .encodeQuality(50)
                /*.signature(new ObjectKey(lastModify))*/;
    }

    public static void showCircleImageView(Context context, Object mode, ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .encodeQuality(50)
                    .placeholder(R.drawable.ic_icon_profile_);
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
            RequestOptions requestOptions = getRequestOptions(lastModify);
            Glide.with(context.getApplicationContext())
                    .load(mode)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showCircleImageViewWithSignature(Context context, File file, String lastModify,
                                                        ImageView imageView) {
        try {
            RequestOptions requestOptions = getRequestOptions(lastModify);
            Glide.with(context.getApplicationContext())
                    .load(file)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showCircleImageViewWithSignatureWH(Context context, Object mode, String lastModify,
                                                          int width, int height,
                                                        ImageView imageView) {
        try {
            RequestOptions requestOptions = getRequestOptions(lastModify);
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
                    .encodeQuality(50)
                    .placeholder(R.drawable.ic_icon_profile_)
                    /*.signature(new ObjectKey(lastModify))*/;

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getBitMapWithWH(Context context, String uri, String lastModify,
                                 int width, int height,
                                 SimpleTarget<Bitmap> SimpleTarget) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .encodeQuality(50)
                    .placeholder(R.drawable.ic_icon_profile_)
//                    .override(width,height)
                    /*.signature(new ObjectKey(lastModify))*/;

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getBitMapCacheOneHour(Context context, String uri,
                                 SimpleTarget<Bitmap> SimpleTarget) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .encodeQuality(50)
                    .placeholder(R.drawable.ic_icon_profile_)
//                    .override(width,height)
                    /*.signature(new ObjectKey(TimeUtils.getTodayDateString()))*/;

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getBitMapPreload(Context context, String uri, String lastModify) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .encodeQuality(50)
                    .placeholder(R.drawable.ic_icon_profile_)
//                    .override(width,height)
                    /*.signature(new ObjectKey(lastModify))*/;

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .preload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getBitMapWithWH(Context context, String uri, String lastModify,
                                 int width, int height,
                                 ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.ic_icon_profile_)
                    .override(width,height)
                    /*.signature(new ObjectKey(lastModify))*/;

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //修改缓存策略：图片换成一小时
    public static void getBitMapOnlyCacheInMemory(Context context, String uri,
                                                  SimpleTarget<Bitmap> SimpleTarget) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.ic_icon_profile_)
                    .encodeQuality(50)
                    .skipMemoryCache(false)/*.diskCacheStrategy(DiskCacheStrategy.NONE)*/
                    /*.signature(new ObjectKey(TimeUtils.getTodayDateString()))*/;;
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getBitMapOnlyCacheInMemory(Context context, File file,
                                                  SimpleTarget<Bitmap> SimpleTarget) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.ic_icon_profile_)
                    .encodeQuality(50)
                    .skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(file)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //该方法加载的头像，会一个小时
    public static void getBitMapOnlyCacheInMemoryWithWH(Context context, String uri,
                                                  int width,int height,
                                                  SimpleTarget<Bitmap> SimpleTarget) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.ic_icon_profile_)
//                    .override(width,height)
                    .encodeQuality(50)
                    .skipMemoryCache(false)
                    /*.signature(new ObjectKey(TimeUtils.getTodayDateString()))*/;
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(SimpleTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getBitMapOnlyCacheInMemoryWithWH(Context context, String uri,
                                                  int width,int height,
                                                        ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.ic_icon_profile_)
                    .override(width,height)
                    .skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(uri)
                    .apply(requestOptions)
                    .into(imageView);
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

            //因为头像id生成规律修改为，每次更新都不同，所以不需要清除本地cache
            /*Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Glide.get(SwingApplication.getAppContext()).clearDiskCache();
                }
            });*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取img并显示到指定imagview；该方法缓存获取到的图片,但有效期只有一个小时
     * @param context context
     * @param url url
     * @param imageView 要显示的控件
     */
    public static void getCircleImageViewOnlyCacheInMemory(Context context, String url, ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.ic_icon_profile_)
                    .encodeQuality(50)
                    .skipMemoryCache(false)/*.diskCacheStrategy(DiskCacheStrategy.NONE)*/
                    /*.signature(new ObjectKey(TimeUtils.getTodayDateString()))*/;
            Glide.with(context.getApplicationContext())
                    .load(url)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
