package com.kidsdynamic.swing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.kidsdynamic.swing.R;

/**
 * ViewIntroductionSync
 */

public class ViewIntroductionSync extends RelativeLayout {

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

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_instruction_info_sync_event, this);

    }


}
