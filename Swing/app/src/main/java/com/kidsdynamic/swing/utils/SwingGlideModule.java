package com.kidsdynamic.swing.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/12/30.
 */

@GlideModule
public class SwingGlideModule extends AppGlideModule {
    private int timeOut = 40;

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .build();

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);
        glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
    }
}
