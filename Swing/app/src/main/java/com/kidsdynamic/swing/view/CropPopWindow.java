package com.kidsdynamic.swing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * CropPopWindow
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class CropPopWindow extends PopupWindow {

    private CropImageView cropImageView;

    public CropPopWindow(Context context) {
        super(context);
        init(context);
    }

    @SuppressLint("InflateParams")
    private void init(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View layout = inflater.inflate(R.layout.crop_pop_window, null);
        cropImageView = layout.findViewById(R.id.cropImageView);
        setContentView(layout);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(context.getResources().
                getColor(R.color.pop_window_background)));
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.push_bottom_in);
        layout.startAnimation(anim);

        // Click Save
        TextView tvSave = (TextView) layout.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != cropImageView) {
                    cropImageView.getCroppedImageAsync();
                }
            }
        });

        // Click Cancel
        TextView tvCancel = (TextView) layout.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.push_bottom_out);
                layout.startAnimation(anim);
                layout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 300);
            }
        });
    }

    public void setCropImageUri(Uri uri) {
        if (null == cropImageView) {
            return;
        }
        cropImageView.setImageUriAsync(uri);
    }

    public void setOnCropImageCompleteListener(CropImageView.OnCropImageCompleteListener listener) {
        if (null != cropImageView) {
            cropImageView.setOnCropImageCompleteListener(listener);
        }
    }

}
