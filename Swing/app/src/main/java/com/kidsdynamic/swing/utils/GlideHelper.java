package com.kidsdynamic.swing.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kidsdynamic.swing.R;

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
            Glide.with(context)
                    .load(mode)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSquareImageView(Context context, Object mode, ImageView imageView) {
        try {
            RequestOptions requestOptions = RequestOptions.fitCenterTransform()
                    .placeholder(R.drawable.default_avatar);;
            Glide.with(context)
                    .load(mode)
                    .apply(requestOptions)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
