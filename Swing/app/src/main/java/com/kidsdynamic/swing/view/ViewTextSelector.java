package com.kidsdynamic.swing.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kidsdynamic.swing.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 03543 on 2017/2/19.
 */

public class ViewTextSelector extends LinearLayout {

    private int mTextSize = 20;
    private int mTextColor = Color.BLACK;
    private int mTextStyle = Typeface.BOLD;
    private int mSelectorColor = Color.BLACK;

    private ViewTextSelector mThis = this;
    private TextView mViewPrev;
    private TextView mViewNext;
    private TextView mViewList;
    private OnSelectListener mSelectListener = null;

    private List<String> mList;
    private int mSelected;

    public ViewTextSelector(Context context) {
        super(context);
        init(context, null);
    }

    public ViewTextSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(
                    attrs, R.styleable.ViewTextSelector);

            final int count = typedArray.getIndexCount();
            for (int idx = 0; idx < count; idx++) {
                final int attr = typedArray.getIndex(idx);

                if (attr == R.styleable.ViewTextSelector_android_textSize) {
                    mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize);
                } else if (attr == R.styleable.ViewTextSelector_android_textColor) {
                    mTextColor = typedArray.getColor(attr, mTextColor);
                } else if (attr == R.styleable.ViewTextSelector_android_textStyle) {
                    mTextStyle = typedArray.getInteger(attr, mTextStyle);
                } else if (attr == R.styleable.ViewTextSelector_selectorColor) {
                    mSelectorColor = typedArray.getColor(attr, mSelectorColor);
                }
            }

            typedArray.recycle();
        }

        mViewPrev = new TextView(getContext());
        mViewPrev.setText("◀"); // U+25C0 &#9664;
        mViewPrev.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewPrev.setTextColor(mTextColor);
        mViewPrev.setGravity(Gravity.CENTER);
        mViewPrev.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        mViewPrev.setOnClickListener(mShiftListener);
        addView(mViewPrev);

        mViewList = new TextView(context);
        mViewList.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewList.setTypeface(mViewList.getTypeface(), mTextStyle);
        mViewList.setTextColor(mTextColor);
        mViewList.setGravity(Gravity.CENTER);
        mViewList.setLayoutParams(new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, 3));
        addView(mViewList);

        mViewNext = new TextView(getContext());
        mViewNext.setText("▶"); // U+25B6 &#9654;
        mViewNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mViewNext.setTextColor(mTextColor);
        mViewNext.setGravity(Gravity.CENTER);
        mViewNext.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        mViewNext.setOnClickListener(mShiftListener);
        addView(mViewNext);

        mList = new ArrayList<>();
    }

    public void clear() {
        mList.clear();
        mViewList.setText("");
    }

    public void add(String item) {
        mList.add(item);
        select(mList.size() - 1);
    }

    public void add(List<String> list) {
        if (list.size() == 0)
            return;

        mList.addAll(list);
        select(mList.size() - list.size());
    }

    public String item(int position) {
        return mList.get(position);
    }

    public int getCount() {
        return mList.size();
    }

    public void select(int position) {
        if (position >= mList.size())
            position = mList.size() - 1;
        if (position < 0)
            position = 0;

        mSelected = position;
        mViewList.setText(mList.get(position));
    }

    public void selectNext() {
        mSelected++;
        mSelected = mSelected >= mList.size() ? 0 : mSelected;

        select(mSelected);
    }

    public void selectPrev() {
        mSelected--;
        mSelected = mSelected < 0 ? (mList.size() - 1) : mSelected;

        select(mSelected);
    }

    public OnClickListener mShiftListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mList.size() == 0)
                return;

            if (view == mViewNext) {
                selectNext();
                if (mSelectListener != null)
                    mSelectListener.OnSelect(mThis, mSelected);

            } else if (view == mViewPrev) {
                selectPrev();
                if (mSelectListener != null)
                    mSelectListener.OnSelect(mThis, mSelected);
            }
        }
    };

    public interface OnSelectListener {
        void OnSelect(View view, int position);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    public void setTextColor(int color) {
        mTextColor = color;
        mViewList.setTextColor(color);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextSize(int size) {
        mTextSize = size;
        mViewList.setTextSize(size);
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setSelectorColor(int color) {
        mSelectorColor = color;

        mViewPrev.setTextColor(color);
        mViewNext.setTextColor(color);
    }

    public int getSelectorColor() {
        return mSelectorColor;
    }
}
