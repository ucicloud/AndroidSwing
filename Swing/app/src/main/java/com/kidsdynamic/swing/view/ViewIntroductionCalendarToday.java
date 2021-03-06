package com.kidsdynamic.swing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kidsdynamic.swing.R;

/**
 * ViewIntroductionCalendarToday
 */

public class ViewIntroductionCalendarToday extends RelativeLayout {

    private TextView tv_Info;
    private Button btn_ok;
    private View layout_tips;

    private OnBtnClickListener mOnClickListener = null;

    public ViewIntroductionCalendarToday(Context context) {
        super(context);
        init(context, null);
    }

    public ViewIntroductionCalendarToday(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewIntroductionCalendarToday(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_instruction_calendar_today, this);

        tv_Info = findViewById(R.id.tv_introduction_info);
        tv_Info.setText(R.string.introduction_calendar_today);
        tv_Info.setGravity(Gravity.CENTER);

        layout_tips = findViewById(R.id.layout_tips);
        layout_tips.setVisibility(GONE);

        btn_ok = findViewById(R.id.btn_action);

        btn_ok.setOnClickListener(onClickListener);

    }

    public void setOnClickListener(ViewIntroductionCalendarToday.OnBtnClickListener listener) {
        mOnClickListener = listener;
    }

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


}
