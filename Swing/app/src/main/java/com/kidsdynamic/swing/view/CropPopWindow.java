package com.kidsdynamic.swing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.utils.GlideHelper;

import java.io.File;

/**
 * CropPopWindow
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class CropPopWindow extends PopupWindow {

    private CropImageView mCropImageView;

    public CropPopWindow(Context context) {
        super(context);
        init(context);
    }

    @SuppressLint("InflateParams")
    private void init(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View layout = inflater.inflate(R.layout.crop_pop_window, null);
        mCropImageView = (CropImageView) layout.findViewById(R.id.cropImageView);
        int color = context.getResources().getColor(R.color.pop_window_background);
        mCropImageView.setPaintColor(color);
        setContentView(layout);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(color));
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.push_bottom_in);
        layout.startAnimation(anim);

        // Click Save
        TextView tvSave = (TextView) layout.findViewById(R.id.tv_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCropImageView) {
                    mCropImageView.getCroppedImageAsync();
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

    public void setCropImageFile(File file) {
        if (null == mCropImageView) {
            return;
        }
        GlideHelper.showSquareImageView(mCropImageView.getContext(), file, mCropImageView);
    }

    public void setOnCropImageCompleteListener(CropImageView.OnCropImageCompleteListener listener) {
        if (null != mCropImageView) {
            mCropImageView.setOnCropImageCompleteListener(listener);
        }
    }

}
