package com.kidsdynamic.swing.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.yy.base.BaseFragmentActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VirtualCameraActivity extends BaseFragmentActivity {

    public static final String FILE = "file";
    private static final String STATE = "state";

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_VIRTUAL_RESULT = 2;

    private static final int REQUEST_PERMISSIONS_CODE = 1;

    private File mCurPhotoFile;
    private boolean saveState = false;
    private boolean isCameraGranted, isReadStorageGranted, isWriteStorageGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            checkPermissions();
        } else {
            saveState = savedInstanceState.getBoolean(STATE, true);
            mCurPhotoFile = (File) savedInstanceState.getSerializable(FILE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                doCameraResult();
            } else if (requestCode == REQUEST_CODE_VIRTUAL_RESULT) {
                Intent newData = new Intent();
                newData.putExtra(FILE, mCurPhotoFile);
                setResult(Activity.RESULT_OK, newData);
                finish();
            }
        } else {
            if (mCurPhotoFile != null) {
                if (mCurPhotoFile.delete())
                    mCurPhotoFile = null;
            }
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE, true);
        outState.putSerializable(FILE, mCurPhotoFile);
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
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
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_PERMISSIONS_CODE);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                isCameraGranted = true;
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                isReadStorageGranted = true;
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                isWriteStorageGranted = true;
                break;
        }
        if (isCameraGranted && isReadStorageGranted && isWriteStorageGranted) {
            startCameraActivity();
        }
    }

    private void startCameraActivity() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        File sdDir = Environment.getExternalStorageDirectory();
        File saveDir = new File(sdDir.toString() + "/DCIM/Camera");
        if (!saveDir.exists()) {
            if (!saveDir.mkdir()) return;
        }

        String strFileName = getFileNameByTimeStamp("yyyyMMdd_kkmmss") + ".jpg";
        mCurPhotoFile = new File(saveDir, strFileName);
        if (mCurPhotoFile.exists()) {
            mCurPhotoFile.deleteOnExit();
        }
        try {
            if (mCurPhotoFile.createNewFile()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                int currentSDKVersion = Build.VERSION.SDK_INT;
                if (currentSDKVersion < Build.VERSION_CODES.N) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurPhotoFile));
                } else {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, mCurPhotoFile.getAbsolutePath());
                    Uri uri = getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SecurityException) {
                finish();
            }
        }
    }

    private void doCameraResult() {
        if (mCurPhotoFile != null && mCurPhotoFile.exists()) {
            try {
                // 发送扫描图片的广播
                Uri uri = Uri.fromFile(mCurPhotoFile);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(uri);
                sendBroadcast(intent);
                if (saveState) {
                    Intent nextIntent = new Intent();
                    nextIntent.putExtra(STATE, saveState);
                    nextIntent.putExtra(FILE, mCurPhotoFile);
                    nextIntent.setClass(VirtualCameraActivity.this, VirtualResultActivity.class);
                    startActivityForResult(nextIntent, REQUEST_CODE_VIRTUAL_RESULT);
                } else {
                    Intent data = new Intent();
                    data.putExtra(FILE, mCurPhotoFile);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileNameByTimeStamp(String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat, Locale.getDefault());
        return format.format(new Date(System.currentTimeMillis()));
    }

}