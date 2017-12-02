package com.kidsdynamic.swing.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.yy.base.BaseFragmentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * VirtualAlbumActivity
 * <p>
 * Created by Stefan on 2017/11/6.
 */

public class VirtualAlbumActivity extends BaseFragmentActivity {

    public static final String FILE = "file";

    private static final int REQUEST_CODE_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            finish();
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_ALBUM:
                doAlbumResult(data);
                break;
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ALBUM:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        Context context = getApplicationContext();
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_ALBUM);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                startAlbumActivity();
                break;
        }
    }

    private void startAlbumActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    private void doAlbumResult(Intent data) {
        if (data == null) {
            finish();
            return;
        }
        Uri uri = data.getData();
        if (null == uri) {
            finish();
            return;
        }
        File file = getRealPathFromUri(uri);
        Intent newData = new Intent();
        newData.putExtra(FILE, file);
        setResult(Activity.RESULT_OK, newData);
        finish();
    }

    @Nullable
    private File getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        String[] project = {MediaStore.Images.Media.DATA};

        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            if (null == contentResolver) {
                return null;
            }
            cursor = contentResolver.query(contentUri, project, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(project[0]);
                String path = cursor.getString(columnIndex);
                if (!TextUtils.isEmpty(path)) {
                    return new File(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}
