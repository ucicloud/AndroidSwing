package com.kidsdynamic.data.net;

import android.content.Context;
import android.util.Log;

import com.bobomee.android.htttp.okhttp.okHttp;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lwz on 2018/3/9.
 */

public class CrashUploader {

    static class AndroidSwingLog {
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        private String text;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void uploadCrashLog(final Context context)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = context.getExternalCacheDir() + "/swing/crashlog/";
                File dir = new File(path);

                if (!dir.exists() || !dir.isDirectory())
                    return;
                File[] files = dir.listFiles();
                Gson gson = new Gson();
                for (File file : files) {
                    try {
                        FileInputStream is = new FileInputStream(file);
                        AndroidSwingLog m = new AndroidSwingLog();
                        m.setText(getString(is));
                        is.close();

                        //使用Gson将对象转换为json字符串
                        String json = gson.toJson(m);

                        //MediaType  设置Content-Type 标头中包含的媒体类型值
                        RequestBody requestBody = FormBody.create(JSON, json);
                        OkHttpClient client = okHttp.INSTANCE.getOkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://zbjwrwlo.api.lncld.net/1.1/classes/AndroidSwingLog")//请求接口。如果需要传参拼接到接口后面。
                                .addHeader("X-LC-Id", "zBJwrWLoydY2zVeSkxENhDL4-gzGzoHsz")
                                .addHeader("X-LC-Key", "JPosFv55xiaCsCp6wNmfuejs")
                                .post(requestBody)
                                .build();//创建Request 对象
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            Log.d("SwingCrash", "log upload success " + response.code());
                            file.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
