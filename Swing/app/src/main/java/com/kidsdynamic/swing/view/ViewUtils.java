package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.kidsdynamic.swing.R;

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
}
