package com.kidsdynamic.swing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kidsdynamic.swing.R;

/**
 * ViewIntroductionSync
 */

public class ViewIntroductionSync extends RelativeLayout {

    private TextView tv_Info;
    private Button btn_ok;
    private View layout_tips;

    private ImageView img_check;

    private OnBtnClickListener mOnClickListener = null;
    private OnCheckClickListener mOnCheckClickListener = null;

    public ViewIntroductionSync(Context context) {
        super(context);
        init(context, null);
    }

    public ViewIntroductionSync(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewIntroductionSync(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setCheckUIShow(){
        if(layout_tips != null && layout_tips.getVisibility() != VISIBLE){
            layout_tips.setVisibility(VISIBLE);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_instruction_calendar_month, this);

        tv_Info = findViewById(R.id.tv_introduction_info);
        tv_Info.setText(R.string.introduction_calendar_month);
        tv_Info.setGravity(Gravity.CENTER);

        layout_tips = findViewById(R.id.layout_tips);
        layout_tips.setVisibility(GONE);

        img_check = layout_tips.findViewById(R.id.todo_check);
        layout_tips.setOnClickListener(onCheckClickListener);

        btn_ok = findViewById(R.id.btn_action);

        btn_ok.setOnClickListener(onClickListener);

    }

    public void setOnClickListener(ViewIntroductionSync.OnBtnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnCheckClickListener(ViewIntroductionSync.OnCheckClickListener listener) {
        mOnCheckClickListener = listener;
    }

    private OnClickListener onCheckClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            img_check.setSelected(!img_check.isSelected());

            if(mOnCheckClickListener != null){
                mOnCheckClickListener.onCheckClick(view,img_check.isSelected());

            }
        }
    };

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mOnClickListener != null){
                mOnClickListener.onClick(view);

            }
        }
    };


    public interface OnBtnClickListener {
        void onClick(View view);
    }

    public interface OnCheckClickListener {

        void onCheckClick(View view, boolean isCheck);
    }



}
