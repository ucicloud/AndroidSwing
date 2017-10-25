package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.view.BottomPopWindow;
import com.kidsdynamic.swing.view.CropPopWindow;
import com.kidsdynamic.swing.view.ViewCircle;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * WatchProfileFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchProfileFragment extends BaseFragment {

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_ALBUM = 2;

    @BindView(R.id.watch_profile_photo)
    ViewCircle vc_photo;
    @BindView(R.id.watch_profile_first)
    EditText et_first;
    @BindView(R.id.watch_profile_last)
    EditText et_last;
    @BindView(R.id.watch_profile_zip)
    EditText et_zip;

    public static WatchProfileFragment newInstance() {
        Bundle args = new Bundle();
        WatchProfileFragment fragment = new WatchProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_profile, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                doCameraResult(data);
                break;
            case REQUEST_CODE_ALBUM:
                doAlbumResult(data);
                break;
        }
    }

    @OnClick(R.id.ib_back)
    public void back() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(SignupLoginFragment.newInstance());
    }

    @OnClick(R.id.watch_profile_photo)
    public void addPhoto() {
        CharSequence array[] = new CharSequence[]{"Take a new picture", "Choose from Library"};
        BottomPopWindow.Builder builder = new BottomPopWindow.Builder(getContext());
        builder.setItems(array, new BottomPopWindow.Builder.OnWhichClickListener() {
            @Override
            public void onWhichClick(View v, int position) {
                doWhichClick(position);
            }
        });
        BottomPopWindow bottomPopWindow = builder.create();
        bottomPopWindow.showAtLocation(getView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @OnClick(R.id.watch_profile_submit)
    public void doSubmit() {
        SignupActivity signupActivity = (SignupActivity) getActivity();
        signupActivity.setFragment(WatchHaveFragment.newInstance());
    }

    private void doWhichClick(int position) {
        switch (position) {
            case 0:
                // TODO: 2017/10/25 add Camera permission
                startCameraActivity();
                break;
            case 1:
                // TODO: 2017/10/25 add reading storage permission
                startAlbumActivity();
                break;
        }
    }

    /**
     * Start virtual camera
     */
    private void startCameraActivity() {
        Intent intent = new Intent();
        intent.setClass(getContext(), VirtualCameraActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * Do result after taking picture
     *
     * @param data Intent
     */
    private void doCameraResult(Intent data) {
        if (null == data) return;
        File file = (File) data.getSerializableExtra(VirtualCameraActivity.FILE);
        if (file != null && file.exists()) {
            showCropPopWindow(Uri.fromFile(file));
        }
    }

    /**
     * Start album
     */
    private void startAlbumActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * Do result after picking picture from album
     *
     * @param data Intent
     */
    private void doAlbumResult(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        if (uri != null) {
            showCropPopWindow(uri);
        }
    }

    private void showCropPopWindow(Uri uri) {
        final CropPopWindow cropPopWindow = new CropPopWindow(getContext());
        cropPopWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        cropPopWindow.setCropImageUri(uri);
        cropPopWindow.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                cropPopWindow.dismiss();
                if (null == result) {
                    return;
                }
                vc_photo.setStrokeWidth(4.0f);
                vc_photo.setCrossWidth(0.0f);
                vc_photo.setBitmap(result.getBitmap());
            }
        });
    }

}
