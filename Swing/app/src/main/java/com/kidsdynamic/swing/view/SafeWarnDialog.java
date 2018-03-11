package com.kidsdynamic.swing.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kidsdynamic.data.persistent.PreferencesUtil;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.utils.ConfigUtil;
import com.kidsdynamic.swing.utils.WeakReferenceHandler;


/**
 * loading 对话框
 */
public class SafeWarnDialog extends AlertDialog {
    private TextView tv_title;
    private TextView tv_content;
    private Button btn_confirm_warn;


    private Context context;
    public SafeWarnDialog(Context context) {
        super(context, R.style.Theme_AppCompat_Dialog);

        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_warn_msg);
//        tv_content= (TextView) findViewById(R.id.tv_content);
        setCanceledOnTouchOutside(false);

        btn_confirm_warn = findViewById(R.id.btn_confirm_safe_warn);

        btn_confirm_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtil.getInstance(context).
                        setPreferenceBooleanValue(ConfigUtil.safe_warn_dialog_first_time, false);

                dismissLoading();
            }
        });

        initView();
    }

    private void initView() {

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_warn_content);
        com.kidsdynamic.swing.utils.ViewUtils.setTextViewNormalTypeFace(getContext(), tv_title, tv_content);

        com.kidsdynamic.swing.utils.ViewUtils.setBtnTypeFace(getContext(),btn_confirm_warn);

    }


/*    public void setContent(String content){
        if (TextUtils.isEmpty(content))
            return;
        tv_content.setText(content);

    }*/



    private long startTime=0;
    private long dur = 500;
    private long endTime = 0;



    public void showLoading(){
        if (isShowing())
            return;
        startTime = System.currentTimeMillis();
        show();
    }



    public void dismissLoading(){
        if (isShowing()){
            endTime= System.currentTimeMillis();
            long del = endTime-startTime;
            if (del>dur){
                myHandler.removeMessages(MSG_DISMISS);
                dismiss();
            }else {
                long delay = startTime + dur -endTime;
                myHandler.sendEmptyMessageDelayed(MSG_DISMISS,delay);
            }

        }
    }

    private MyHandler myHandler = new MyHandler(this);
    private static final int MSG_DISMISS = 0;

   static class MyHandler extends WeakReferenceHandler<SafeWarnDialog> {

       public MyHandler(SafeWarnDialog reference) {
           super(reference);
       }

       @Override
       protected void handleMessage(SafeWarnDialog reference, Message msg) {
           Log.d("loadingDialog","handleMessage");
           //modify 2017年8月28日14:59:29 weizg  关闭对话框前，做非空和现在显示判断
           if(reference != null && reference.isShowing()){
               reference.dismiss();
           }
       }
   }



}
