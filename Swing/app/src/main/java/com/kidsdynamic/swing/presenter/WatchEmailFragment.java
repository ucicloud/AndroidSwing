package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.commonlib.utils.SoftKeyBoardUtil;
import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.host.HostApi;
import com.kidsdynamic.data.net.host.model.AddSubHost;
import com.kidsdynamic.data.net.host.model.RequestAddSubHostEntity;
import com.kidsdynamic.data.net.user.UserApiNeedToken;
import com.kidsdynamic.data.net.user.model.UserInfo;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.BeanConvertor;
import com.kidsdynamic.swing.domain.LoginManager;
import com.kidsdynamic.swing.domain.UserManager;
import com.kidsdynamic.swing.model.WatchContact;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.utils.SwingFontsCache;
import com.yy.base.utils.Functions;
import com.yy.base.utils.ToastCommon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * WatchEmailFragment
 * <p>
 * Created by Stefan on 2017/12/22.
 */

public class WatchEmailFragment extends BaseFragment {

    @BindView(R.id.et_email)
    protected EditText mViewUserEmail;

    @BindView(R.id.btn_search)
    protected Button btn_search;

    @BindView(R.id.iv_head)
    protected ImageView user_avatar;
    @BindView(R.id.tv_content)
    protected TextView user_name;
    @BindView(R.id.iv_action)
    protected ImageView img_add;

    @BindView(R.id.layout_user_info)
    protected View layout_user_info;

    private WatchContact.User watchUserInfo = null;

    public static WatchEmailFragment newInstance() {
        Bundle args = new Bundle();
        WatchEmailFragment fragment = new WatchEmailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_email, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_search.setTypeface(SwingFontsCache.getBoldType(getContext()));
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        SoftKeyBoardUtil.hideSoftKeyboard(mViewUserEmail);
//    }

    @OnClick(R.id.btn_search)
    public void onSearch() {
        SoftKeyBoardUtil.hideSoftKeyboard(getActivity());

        String userEmail = mViewUserEmail.getText().toString().trim();
        if (!Functions.isValidEmail(userEmail)) {
            ToastCommon.makeText(getContext(), R.string.error_email_null);
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);

        final UserApiNeedToken userApiNeedToken = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(UserApiNeedToken.class, false);

        userApiNeedToken.findByEmail(userEmail).enqueue(new BaseRetrofitCallback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {

                if (response.code() == 200) {
                    showSearchedUserInfo(response.body());
                } else if (response.code() == 400) {
                    ToastCommon.makeText(getContext(), R.string.error_api_user_find_by_email_404);
                } else {
                    ToastCommon.makeText(getContext(), R.string.normal_err, response.code());
                }

                finishLoadingDialog();

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                super.onFailure(call, t);

                finishLoadingDialog();
            }
        });

    }

    private void showSearchedUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            ToastCommon.makeText(getContext(), R.string.error_api_unknown);
            return;
        }

        user_name.setText(LoginManager.getUserName(userInfo.getFirstName(),
                userInfo.getLastName()));

        if (!TextUtils.isEmpty(userInfo.getProfile())) {
            GlideHelper.getCircleImageViewOnlyCacheInMemory(getContext(),
                    UserManager.getProfileRealUri(userInfo.getProfile()),
                    user_avatar);
        }

        img_add.setImageResource(R.drawable.ic_icon_add_orange);

        watchUserInfo = BeanConvertor.getWatchContact(userInfo);

        layout_user_info.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.iv_action)
    protected void onAddClick() {
        if (watchUserInfo == null) {
            ToastCommon.makeText(getContext(), R.string.error_api_unknown);
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);

        //请求加入
        HostApi hostApi = ApiGen.getInstance(getActivity().getApplicationContext()).
                generateApi(HostApi.class, true);

        AddSubHost subHostId = new AddSubHost();
        subHostId.setHostId(watchUserInfo.mId);
        hostApi.subHostAdd(subHostId).enqueue(new BaseRetrofitCallback<RequestAddSubHostEntity>() {
            @Override
            public void onResponse(Call<RequestAddSubHostEntity> call,
                                   Response<RequestAddSubHostEntity> response) {
                int code = response.code();
                if (code == 200) {
//                    mActivityMain.mWatchContactStack.push(watchUserInfo);
//                    selectFragment(ProfileKidsRequestAccessFragment.class.getName(), null,
//                            false);
                    RequestAddSubHostEntity entity = response.body();
                    UserInfo userInfo = entity.getRequestToUser();
                    SignupActivity signupActivity = (SignupActivity) getActivity();
                    signupActivity.setFragment(WatchRequestFragment.newInstance(userInfo), false);
                } else if (code == 409) {
                    //请求已经存在
                    ToastCommon.makeText(getContext(), R.string.error_subhost_add_409);
                } else {
                    ToastCommon.makeText(getContext(), R.string.normal_err, code);
                }

                finishLoadingDialog();

                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<RequestAddSubHostEntity> call, Throwable t) {
                finishLoadingDialog();

                super.onFailure(call, t);
            }
        });

    }

}
