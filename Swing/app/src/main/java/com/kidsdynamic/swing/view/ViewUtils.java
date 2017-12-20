package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;

import com.kidsdynamic.swing.R;

import java.io.ByteArrayOutputStream;

/**
 * date:   2017/10/27 11:56 <br/>
 */

public class ViewUtils {
    public static void showMsgDialog(Context context, String msg){
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setNegativeButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public static Bitmap getSmallBitmap(Bitmap bitmap){
        if(bitmap == null){
            return null;
        }

        ByteArrayOutputStream baos = null;
        try{
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,5,baos);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(baos != null){
                    baos.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
