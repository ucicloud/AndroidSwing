package com.kidsdynamic.swing.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.model.WatchTodo;

/**
 * ViewTodoForDetail
 */

public class ViewTodoForDetail extends RelativeLayout {

    private View mView;
    private ImageView mViewCheck;
    private TextView mViewText;
    private TextView mViewDelete;
    private View mViewSeparator;

    private ViewTodoForDetail mThis = this;
    private OnEditListener mOnEditListener = null;

    public ViewTodoForDetail(Context context) {
        super(context);
        init(context, null);
    }

    public ViewTodoForDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ViewTodoForDetail(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.view_todo_for_detail, this);

        mView = findViewById(R.id.view_todo_for_detail);

        mViewCheck = (ImageView) findViewById(R.id.todo_check);

        mViewText = findViewById(R.id.todo_text);
        mViewText.addTextChangedListener(mTextWatch);
        mViewText.setOnFocusChangeListener(mFocusListener);

        mViewDelete = (TextView) findViewById(R.id.todo_delete);

        mViewSeparator = findViewById(R.id.todo_separator);
    }

    public void load(WatchTodo todo) {
        if(!TextUtils.isEmpty(todo.mStatus)){
            mViewCheck.setSelected(todo.mStatus.equals(WatchTodo.STATUS_DONE));
            if(todo.mStatus.equals(WatchTodo.STATUS_DONE)){
                mViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_light));
            }else {
                mViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_white));
            }
        }else {
            mViewCheck.setSelected(false);
        }
        mViewText.setText(todo.mText);
    }

    public void save(WatchTodo todo) {
        todo.mStatus = isChecked() ? WatchTodo.STATUS_DONE : WatchTodo.STATUS_PENDING;
        todo.mText = mViewText.getText().toString();
    }

    public void setEditMode() {
        mView.setEnabled(true);
        mViewCheck.setEnabled(true);
        mViewText.setEnabled(true);
        mViewDelete.setEnabled(true);

        mViewText.setTypeface(mViewText.getTypeface(), Typeface.NORMAL);
        mViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_deep));

        mView.setOnClickListener(mCheckListener);
//        mViewCheck.setOnClickListener(mCheckListener);
        mViewText.setOnTouchListener(mOnTouchListener);
        mViewDelete.setOnClickListener(mDeleteListener);
    }

    public void setCheckMode() {
        mView.setEnabled(true);
        mViewCheck.setEnabled(true);
        mViewText.setEnabled(false);
        mViewDelete.setEnabled(false);

        mViewText.setTypeface(mViewText.getTypeface(), Typeface.BOLD);
        mViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_white));

        mView.setOnClickListener(mCheckListener);
//        mViewCheck.setOnClickListener(mCheckListener);
        mViewText.setOnTouchListener(null);
        mViewDelete.setOnClickListener(null);
    }

    public void setLockMode() {
        mView.setEnabled(false);
        mViewCheck.setEnabled(false);
        mViewText.setEnabled(false);
        mViewDelete.setEnabled(false);

        mViewText.setTypeface(mViewText.getTypeface(), Typeface.NORMAL);
        mViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_light));

        mView.setOnClickListener(null);
//        mViewCheck.setOnClickListener(null);
        mViewText.setOnTouchListener(null);
        mViewDelete.setOnClickListener(null);
    }

    public boolean isChecked() {
        return mViewCheck.isSelected();
    }

    public void setChecked(boolean check) {
        mViewCheck.setSelected(check);
    }

    public String getText() {
        return mViewText.getText().toString();
    }

    public void setText(String text) {
        mViewText.setText(text);
    }

    public void setSeparatorVisibility(int visible) {
        mViewSeparator.setVisibility(visible);
    }

    public int getSeparatorVisibility() {
        return mViewSeparator.getVisibility();
    }

    public interface OnEditListener {
        void onDelete(ViewTodoForDetail viewTodo, View view);

        void onCheck(ViewTodoForDetail viewTodo, View view, boolean checked);

        void onText(ViewTodoForDetail viewTodo, View view, String text);
    }

    public void setOnEditListener(OnEditListener listener) {
        mOnEditListener = listener;
    }

    public OnEditListener getOnEditListener() {
        return mOnEditListener;
    }

    private OnClickListener mCheckListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            setChecked(!isChecked());

            if(mViewCheck.isSelected()){
                mViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_gray_light));
            }else {
                mViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.color_white));
            }

            if (mOnEditListener != null)
                mOnEditListener.onCheck(mThis, view, isChecked());
        }
    };

    private OnClickListener mDeleteListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOnEditListener != null)
                mOnEditListener.onDelete(mThis, view);
        }
    };

    private TextWatcher mTextWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mOnEditListener != null)
                mOnEditListener.onText(mThis, mViewText, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private OnFocusChangeListener mFocusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            setDeleteVisible(false);
        }
    };

    private boolean getDeleteVisible() {
        return !(mViewDelete.getLayoutParams().width == 0);
    }

    private void setDeleteVisible(boolean visible) {
        LayoutParams layoutParams = (LayoutParams) mViewDelete.getLayoutParams();
        layoutParams.width = visible ? LayoutParams.WRAP_CONTENT : 0;
        mViewDelete.setLayoutParams(layoutParams);
    }

    private OnTouchListener mOnTouchListener = new OnSwipeTouchListener(getContext()) {
        @Override
        public void onSwipeRight() {
            setDeleteVisible(false);
        }

        @Override
        public void onSwipeLeft() {
            setDeleteVisible(true);
        }

        @Override
        public void onSwipeTop() {
        }

        @Override
        public void onSwipeBottom() {
        }
    };

    private class OnSwipeTouchListener implements OnTouchListener {

        private final GestureDetector mGestureDetector;

        public OnSwipeTouchListener(Context context) {
            mGestureDetector = new GestureDetector(context, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mGestureDetector.onTouchEvent(event);
        }

        public class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}
