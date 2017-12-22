package com.kidsdynamic.swing.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.AddSubHost;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.view.ListLinearLayout;
import com.yy.base.utils.ToastCommon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * WatchRequestFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchRequestFragment extends BaseFragment {

    private static final String DATA = "data";

    @BindView(R.id.watch_request_title)
    TextView tv_title;
    @BindView(R.id.watch_request_container)
    ListLinearLayout ll_select;
    @BindView(R.id.watch_request_dashboard)
    TextView tv_dashboard;

    public static WatchRequestFragment newInstance(KidsWithParent kidsWithParent) {
        Bundle args = new Bundle();
        WatchRequestFragment fragment = new WatchRequestFragment();
        args.putSerializable(DATA, kidsWithParent);
        fragment.setArguments(args);
        return fragment;
    }

    public static WatchRequestFragment newInstance(UserInfo userInfo) {
        Bundle args = new Bundle();
        WatchRequestFragment fragment = new WatchRequestFragment();
        args.putSerializable(DATA, userInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_request, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        Serializable serialObj = args.getSerializable(DATA);
        if (null == serialObj) {
            return;
        }
        if (serialObj instanceof KidsWithParent) {
            List<KidsWithParent> list = new ArrayList<>();
            KidsWithParent kidsWithParent = (KidsWithParent) serialObj;
            list.add(kidsWithParent);
            setDataAdapterFromWatchSelect(list);
        } else if (serialObj instanceof UserInfo) {
            tv_title.setText(R.string.watch_request_access_pending);
            tv_dashboard.setText(R.string.watch_select_dashboard);

            List<UserInfo> list = new ArrayList<>();
            UserInfo userInfo = (UserInfo) serialObj;
            list.add(userInfo);
            setDataAdapterFromWatchEmail(list);
        }
    }

    @OnClick(R.id.watch_request_search)
    public void searchAgain() {
        Activity activity = getActivity();
        if (activity instanceof SignupActivity) {
            SignupActivity signupActivity = (SignupActivity) activity;
            signupActivity.setFragment(WatchSearchFragment.newInstance(), true);
        } else if (activity instanceof MainFrameActivity) {
            MainFrameActivity mainFrameActivity = (MainFrameActivity) activity;
            mainFrameActivity.setFragment(WatchSearchFragment.newInstance(), true);
        }
    }

    @OnClick(R.id.watch_request_dashboard)
    public void goDashBoard() {
        Activity activity = getActivity();
        if (activity instanceof SignupActivity) {
            activity.finish();
            startActivity(new Intent(activity, MainFrameActivity.class));
        } else if (activity instanceof MainFrameActivity) {
            MainFrameActivity mainFrameActivity = (MainFrameActivity) activity;
            mainFrameActivity.setFragment(DashboardEmotionFragment.newInstance(), true);
        }
    }

    public void setDataAdapterFromWatchSelect(List<KidsWithParent> list) {
        DataAdapter<KidsWithParent> dataAdapter = new DataAdapter<>(getContext(), list);
        ll_select.setAdapter(dataAdapter);
    }

    public void setDataAdapterFromWatchEmail(List<UserInfo> list) {
        DataAdapter<UserInfo> dataAdapter = new DataAdapter<>(getContext(), list);
        ll_select.setAdapter(dataAdapter);
    }

    private class DataAdapter<T> extends BaseAdapter {

        private Context mContext;
        private List<T> mItems = new ArrayList<>();

        private DataAdapter(Context context, List<T> items) {
            mContext = context;
            mItems = items;
        }

        public void setData(List<T> items) {
            mItems.clear();
            mItems.addAll(items);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public T getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final WatchSelectFragment.ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.fragment_watch_request_list_linear_item, parent, false);
                holder = new WatchSelectFragment.ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (WatchSelectFragment.ViewHolder) convertView.getTag();
            }
            T t = getItem(position);
            if (t instanceof KidsWithParent) {
                KidsWithParent kidsWithParent = (KidsWithParent) t;
                String url;
                final long id = kidsWithParent.getId();
                String profile = kidsWithParent.getProfile();
                if (!TextUtils.isEmpty(profile)) {
                    url = UserManager.getProfileRealUri(profile);
                } else {
                    url = UserManager.getProfileRealUri(id);
                }
                if (TextUtils.isEmpty(url)) {
                    holder.iv_head.setImageResource(R.drawable.ic_icon_profile_);
                } else {
                    GlideHelper.showCircleImageView(mContext, url, holder.iv_head);
                }
                holder.iv_head.setBackgroundResource(R.color.transparent);
                holder.tv_content.setText(kidsWithParent.getName());
                holder.iv_action.setImageResource(R.drawable.icon_arrow_up_orange);
                holder.iv_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doRequestClick(id, tv_title, tv_dashboard, holder.iv_action);
                    }
                });
            } else if (t instanceof UserInfo) {
                UserInfo userInfo = (UserInfo) t;
                String url;
                long id = userInfo.getId();
                String profile = userInfo.getProfile();
                if (!TextUtils.isEmpty(profile)) {
                    url = UserManager.getProfileRealUri(profile);
                } else {
                    url = UserManager.getProfileRealUri(id);
                }
                if (TextUtils.isEmpty(url)) {
                    holder.iv_head.setImageResource(R.drawable.ic_icon_profile_);
                } else {
                    GlideHelper.showCircleImageView(mContext, url, holder.iv_head);
                }
                holder.iv_head.setBackgroundResource(R.color.transparent);
                String name = String.format("%1$s %2$s", userInfo.getFirstName(), userInfo.getLastName());
                holder.tv_content.setText(name);
                holder.iv_action.setImageResource(R.drawable.icon_done);
                holder.iv_action.setClickable(false);
            }

            return convertView;
        }
    }

    private void doRequestClick(long hostId, final TextView tvTitle, final TextView tvDashboard,
                                final ImageView iv_action) {
        showLoadingDialog(R.string.signup_login_wait);
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);
        AddSubHost subHostId = new AddSubHost();
        subHostId.setHostId(hostId);
        hostApi.subHostAdd(subHostId)
                .enqueue(new BaseRetrofitCallback<RequestAddSubHostEntity>() {
                    @Override
                    public void onResponse(Call<RequestAddSubHostEntity> call,
                                           Response<RequestAddSubHostEntity> response) {
                        super.onResponse(call, response);
                        finishLoadingDialog();
                        int code = response.code();
                        if (code == 200) {
                            tvTitle.setText(R.string.watch_request_access_pending);
                            tvDashboard.setText(R.string.watch_select_dashboard);
                            iv_action.setImageResource(R.drawable.icon_done);
                        } else if (code == 409) {
                            ToastCommon.makeText(getContext(), R.string.error_subhost_add_409);
                        } else {
                            ToastCommon.makeText(getContext(), R.string.normal_err, code);
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestAddSubHostEntity> call, Throwable t) {
                        super.onFailure(call, t);
                        finishLoadingDialog();
                    }
                });
    }

    static class ViewHolder {

        @BindView(R.id.iv_head)
        ImageView iv_head;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.iv_action)
        ImageView iv_action;

        ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
