package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.yy.base.utils.LanguageSpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * ProfileLanguageFragment
 * <p>
 * Created by Stefan on 2017/12/2.
 */

public class ProfileLanguageFragment extends ProfileBaseFragment {

    @BindView(R.id.listview_language)
    ListView mListView;

    public static ProfileLanguageFragment newInstance() {
        Bundle args = new Bundle();
        ProfileLanguageFragment fragment = new ProfileLanguageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_profile_language, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTitleBar();
        initData();
    }

    @OnClick(R.id.main_toolbar_action1)
    public void onTopLeftBtnClick() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @OnItemClick(R.id.listview_language)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Where where = (Where) parent.getItemAtPosition(position);
        String localLanguage = LanguageSpUtils.getLocalLanguage(getContext());
        if (!TextUtils.isEmpty(localLanguage) && TextUtils.equals(where.mLanguage, localLanguage)) {
            return;
        }
        LanguageSpUtils.saveSetting(getContext(), where.mLanguage, MainFrameActivity.class);
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.profile_option_language);
        view_left_action.setImageResource(R.drawable.icon_left);
    }

    private void initData() {
        Resources res = getResources();
        List<Where> whereList = new ArrayList<>();
        whereList.add(new Where(res.getString(R.string.language_en), LanguageSpUtils.EN));
        whereList.add(new Where(res.getString(R.string.language_zh_tw), LanguageSpUtils.ZH_TW));
        whereList.add(new Where(res.getString(R.string.language_zh_cn), LanguageSpUtils.ZH_CN));
        whereList.add(new Where(res.getString(R.string.language_ja), LanguageSpUtils.JA));
        whereList.add(new Where(res.getString(R.string.language_ru), LanguageSpUtils.RU));
        whereList.add(new Where(res.getString(R.string.language_es), LanguageSpUtils.ES));

        LanguageAdapter adapter = new LanguageAdapter(getContext(), whereList);
        String localLanguage = LanguageSpUtils.getLocalLanguage(getContext());
        Locale locale = Locale.getDefault();
        if (TextUtils.isEmpty(localLanguage)) {
            if (locale.equals(Locale.CHINESE)) {
                localLanguage = String.format("%s_%s",
                        Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
            } else {
                localLanguage = locale.getLanguage();
            }
        }
        adapter.selWhere(localLanguage);
        mListView.setAdapter(adapter);
    }

    private class LanguageAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private List<Where> items = new ArrayList<>();

        private String language;

        LanguageAdapter(Context context, List<Where> items) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            if (null != items && !items.isEmpty()) {
                this.items.addAll(items);
            }
        }

        private void selWhere(String language) {
            this.language = language;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Where getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_language_option, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Where where = getItem(position);
            holder.tv_language_option.setText(where.mName);
            if (where.mLanguage.equals(language)) {
                holder.tv_language_option.setTextColor(ContextCompat.getColor(context, R.color.color_orange_main));
            } else {
                holder.tv_language_option.setTextColor(ContextCompat.getColor(context, R.color.color_gray_main));
            }
            return convertView;
        }
    }

    static class ViewHolder {

        @BindView(R.id.tv_language_option)
        TextView tv_language_option;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    final class Where {
        String mName;
        String mLanguage;

        Where(String name, String language) {
            mName = name;
            mLanguage = language;
        }
    }

}
