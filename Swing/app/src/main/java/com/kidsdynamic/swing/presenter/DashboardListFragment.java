package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.utils.SwingFontsCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * DashboardListFragment
 * <p>
 * Created by Stefan on 2017/11/20.
 */

public class DashboardListFragment extends DashboardBaseFragment {

    private static final String LIST_TYPE = "list_type";
    private static final String EMOTION_INT = "emotion_int";

    private static final int LIST_TODAY = 0;
    private static final int LIST_WEEK = 1;
    private static final int LIST_MONTH = 2;
    private static final int LIST_YEAR = 3;

    @BindView(R.id.dashboard_list_root)
    View mViewRoot;
    @BindView(R.id.dashboard_list_view)
    ListView mListView;
    @BindView(R.id.dashboard_list_radio)
    RadioGroup mRadioGroup;
    @BindView(R.id.dashboard_list_indoor)
    RadioButton mRadioButtonIndoor;
    @BindView(R.id.dashboard_list_outdoor)
    RadioButton mRadioButtonOutdoor;

    private DataAdapter dataAdapter;
    private int mEmotion;
    private int mEmotionColor;

    public static DashboardListFragment newInstance(int listType, int emotion) {
        Bundle args = new Bundle();
        args.putInt(LIST_TYPE, listType);
        args.putInt(EMOTION_INT, emotion);
        DashboardListFragment fragment = new DashboardListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view_left_action.setImageResource(R.drawable.icon_left);
        mRadioButtonIndoor.setChecked(true);
        mRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        Bundle args = getArguments();

        mEmotion = args.getInt(EMOTION_INT, EMOTION_LOW);
        setEmotion(mEmotion);

        int count = 0;
        Random random = new Random();
        int listType = args.getInt(LIST_TYPE, LIST_TODAY);
        if (LIST_TODAY == listType) {
            tv_title.setText(R.string.dashboard_chart_today);
            count = random.nextInt(24);
        } else if (LIST_WEEK == listType) {
            tv_title.setText(R.string.dashboard_chart_this_week);
            count = 7;
        } else if (LIST_MONTH == listType) {
            tv_title.setText(R.string.dashboard_chart_this_month);
            int value = random.nextInt();
            if (value % 3 == 0) {
                count = 31;
            } else if (value % 3 == 1) {
                count = 30;
            } else {
                count = 28;
            }
        } else {
            tv_title.setText(R.string.dashboard_chart_this_year);
            count = 12;
        }

        List<Object> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new Object());
        }
        setDataAdapter(list, listType);

    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            setChart(getChart());
        }
    };

    private void setEmotion(int emotion) {
        int mBorderButtonBg;
        ColorStateList mBorderButtonTextColorStateList;
        switch (emotion) {
            case EMOTION_LOW:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_blue_main);
                mBorderButtonBg = R.drawable.border_button_bg_blue;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(), R.color.text_blue_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster01);
                break;

            case EMOTION_ALMOST:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_green_main);
                mBorderButtonBg = R.drawable.border_button_bg_green;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(), R.color.text_green_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster02);
                break;

            default:
                mEmotionColor = ContextCompat.getColor(getContext(), R.color.color_orange_main);
                mBorderButtonBg = R.drawable.border_button_bg_orange;
                mBorderButtonTextColorStateList = ContextCompat.getColorStateList(getContext(), R.color.text_orange_white_change_selector);

                mViewRoot.setBackgroundResource(R.drawable.background_dashboard_monster03);
                break;
        }

        mRadioButtonIndoor.setBackgroundResource(mBorderButtonBg);
        mRadioButtonIndoor.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mRadioButtonIndoor.setTextColor(mBorderButtonTextColorStateList);
        mRadioButtonOutdoor.setBackgroundResource(mBorderButtonBg);
        mRadioButtonIndoor.setTypeface(SwingFontsCache.getBoldType(getContext()));
        mRadioButtonOutdoor.setTextColor(mBorderButtonTextColorStateList);

        mEmotion = emotion;
    }

    private void setDataAdapter(List<Object> list, int type) {
        if (null == dataAdapter) {
            dataAdapter = new DataAdapter(getContext(), list, type);
        } else {
            dataAdapter.setData(list);
            dataAdapter.setType(type);
        }
        mListView.setAdapter(dataAdapter);
    }

    private class DataAdapter extends BaseAdapter {

        private Context context;
        private List<Object> items = new ArrayList<>();
        private int type;

        DataAdapter(Context context, List<Object> items, int type) {
            this.context = context;
            this.items.addAll(items);
            this.type = type;
        }

        void setType(int type) {
            this.type = type;
        }

        void setData(List<Object> items) {
            this.items.clear();
            this.items.addAll(items);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (LIST_TODAY == type) {
                TodayHolder holder;
                if (null == convertView) {
                    convertView = LayoutInflater.from(context)
                            .inflate(R.layout.item_activity_today, parent, false);
                    holder = new TodayHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (TodayHolder) convertView.getTag();
                }
                holder.tvTimeStart.setTextColor(mEmotionColor);
                holder.tvTimeEnd.setTextColor(mEmotionColor);
                holder.tvStepsValue.setTextColor(mEmotionColor);
                holder.tvStepsUnit.setTextColor(mEmotionColor);
            } else {
                CommonHolder holder;
                if (null == convertView) {
                    convertView = LayoutInflater.from(context)
                            .inflate(R.layout.item_activity_common, parent, false);
                    holder = new CommonHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (CommonHolder) convertView.getTag();
                }
                holder.tvTimeDisplay.setTextColor(mEmotionColor);
                holder.tvStepsValue.setTextColor(mEmotionColor);
                holder.tvStepsUnit.setTextColor(mEmotionColor);
            }
            return convertView;
        }
    }

    static class TodayHolder extends BaseHolder {
        @BindView(R.id.tvTimeStart)
        TextView tvTimeStart;
        @BindView(R.id.tvTimeEnd)
        TextView tvTimeEnd;

        TodayHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

    static class CommonHolder extends BaseHolder {
        @BindView(R.id.tvTimeDisplay)
        TextView tvTimeDisplay;
        @BindView(R.id.tvTimeDesc)
        TextView tvTimeDesc;

        CommonHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

    static class BaseHolder {
        @BindView(R.id.tvStepsValue)
        TextView tvStepsValue;
        @BindView(R.id.tvStepsUnit)
        TextView tvStepsUnit;
    }

}
