package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kidsdynamic.swing.R;

/**
 * ListLinearLayout
 */
public class ListLinearLayout extends LinearLayout {

    private BaseAdapter mAdapter;
    private View mHeaderView;
    private OnItemClickListener mOnItemClickListener;

    private int divider;
    private float dividerHeight;
    private boolean headerDividersEnabled;
    private boolean footerDividersEnabled;

    public ListLinearLayout(Context context) {
        this(context, null);
    }

    public ListLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListLinearLayout(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ListLinearLayout);
        divider = a.getColor(R.styleable.ListLinearLayout_divideLine,
                context.getResources().getColor(android.R.color.transparent));
        dividerHeight = a.getDimension(
                R.styleable.ListLinearLayout_dividerHeight, 0);
        headerDividersEnabled = a.getBoolean(
                R.styleable.ListLinearLayout_headerDividersEnabled, false);
        footerDividersEnabled = a.getBoolean(
                R.styleable.ListLinearLayout_footerDividersEnabled, false);
        a.recycle();
    }

    public void addHeaderView(View header) {
        if (header != null) {
            addView(header);
            mHeaderView = header;
        }
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        addChild(adapter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void addChild(BaseAdapter adapter) {
        if (adapter == null || adapter.isEmpty()) {
            return;
        }
        removeAllViews();
        if (mHeaderView != null) {
            addView(mHeaderView);
        }
        if (headerDividersEnabled) {
            ImageView divider = initDivider();
            addView(divider);
        }
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View child = adapter.getView(i, null, null);
            child.setTag(i);
            if (mOnItemClickListener != null) {
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            int tag = (Integer) v.getTag();
                            mOnItemClickListener.onItemClick(v, tag);
                        }
                    }
                });
            }
            addView(child);
            if (i < count - 1) {
                ImageView divider = initDivider();
                addView(divider);
            } else if (i == count - 1 && footerDividersEnabled) {
                ImageView divider = initDivider();
                addView(divider);
            }
        }
    }

    private ImageView initDivider() {
        ImageView iv = new ImageView(getContext());
        iv.setBackgroundColor(divider);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                (int) dividerHeight);
        iv.setLayoutParams(lp);
        return iv;
    }

    public interface OnItemClickListener {
        void onItemClick(View child, int position);
    }

}
