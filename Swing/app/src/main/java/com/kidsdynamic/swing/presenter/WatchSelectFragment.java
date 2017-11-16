package com.kidsdynamic.swing.presenter;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.data.net.ApiGen;
import com.kidsdynamic.data.net.kids.KidsApi;
import com.kidsdynamic.data.net.kids.model.KidsWithParent;
import com.kidsdynamic.data.net.kids.model.WhoRegisterMacIDResp;
import com.kidsdynamic.data.utils.LogUtil2;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.ble.IDeviceInitCallback;
import com.kidsdynamic.swing.ble.IDeviceScanCallback;
import com.kidsdynamic.swing.ble.SwingBLEService;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.net.BaseRetrofitCallback;
import com.kidsdynamic.swing.utils.GlideHelper;
import com.kidsdynamic.swing.view.ListLinearLayout;
import com.vise.baseble.model.BluetoothLeDevice;
import com.yy.base.utils.ToastCommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * WatchSelectFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchSelectFragment extends BaseFragment {

    private static final int REQUEST_CODE_BLE = 12;

    @BindView(R.id.watch_select_container)
    ListLinearLayout ll_select;

    @BindView(R.id.watch_select_dashboard)
    TextView tv_gotoDashboard;

    private DataAdapter dataAdapter;
    private Map<String, BluetoothLeDevice> mDeviceMap = new HashMap<>();
    private SwingBLEService mBluetoothService;

    public static WatchSelectFragment newInstance() {
        Bundle args = new Bundle();
        WatchSelectFragment fragment = new WatchSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_select, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataAdapter = new DataAdapter(getContext());
        ll_select.setAdapter(dataAdapter);
//        checkPermissions();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBluetoothService != null) {
            mBluetoothService.cancelScan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            unbindService();
        }
    }

    private void bindService() {
        Context context = getContext();
        Intent bindIntent = new Intent(context, SwingBLEService.class);
        context.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        Context context = getContext();
        context.unbindService(mFhrSCon);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((SwingBLEService.BluetoothBinder) service).getService();
            exec();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_BLE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        Context context = getContext();
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(getActivity(), deniedPermissions, REQUEST_CODE_BLE);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (mBluetoothService == null) {
                    bindService();
                } else {
                    exec();
                }
                break;
        }
    }

    private void exec() {
        if (mBluetoothService == null) {
            return;
        }
        mBluetoothService.closeConnect();
        //一直扫描，直到该界面被压栈或是被关闭
        mBluetoothService.scanDevice(0, new IDeviceScanCallback() {
            @Override
            public void onStartScan() {
                mDeviceMap.clear();
                dataAdapter.clear();
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanTimeOut() {

            }

            @Override
            public void onScanning(BluetoothLeDevice scanResult) {
                checkWatchBindStatus(scanResult);
            }
        });
    }

    public void checkWatchBindStatus(final BluetoothLeDevice scanResult) {
        if (null == scanResult) {
            return;
        }

        KidsApi kidsApi = ApiGen.getInstance(getContext().getApplicationContext()).
                generateApi(KidsApi.class, true);

        //业务上的macId不包含":"
        final String watchMacId = DeviceManager.getMacID(scanResult.getAddress());
//        final String watchMacId = "111113871799";//测试用
        kidsApi.whoRegisteredMacID(watchMacId).enqueue(new BaseRetrofitCallback<WhoRegisterMacIDResp>() {
            @Override
            public void onResponse(Call<WhoRegisterMacIDResp> call, Response<WhoRegisterMacIDResp> response) {
                LogUtil2.getUtils().d("whoRegisteredMacID onResponse: " + response.code());
                super.onResponse(call, response);

                KidsWithParent kidsWithParent = null;
                if (response.code() == 200) {
                    kidsWithParent = response.body().getKid();
                    LogUtil2.getUtils().d("watch binder: ");
                    LogUtil2.getUtils().d("watch binder info: " + kidsWithParent.getName());
                    LogUtil2.getUtils().d("watch binder info: " + kidsWithParent.getParent().getFirstName());
                    //绑定过的设备暂时不显示，二期多设备功能才显示。
                } else if (response.code() == 404) {
                    kidsWithParent = new KidsWithParent();
                    kidsWithParent.setId(-1);
                    kidsWithParent.setName(scanResult.getName());
                    kidsWithParent.setMacId(watchMacId);
                    LogUtil2.getUtils().d("watch not bind");

                    //业务上用的macId需要吧address中的":"删除
                    mDeviceMap.put(watchMacId, scanResult);
                    dataAdapter.addItem(kidsWithParent);
                    ll_select.setAdapter(dataAdapter);
                }


            }

            @Override
            public void onFailure(Call<WhoRegisterMacIDResp> call, Throwable t) {
                LogUtil2.getUtils().d("whoRegisteredMacID onFailure");
                super.onFailure(call, t);
            }
        });
    }

    private class DataAdapter extends BaseAdapter {

        private Context mContext;
        private List<KidsWithParent> mItems;

        private DataAdapter(Context context) {
            mContext = context;
            mItems = new ArrayList<>();
        }

        private void addItem(KidsWithParent kidsWithParent) {
            mItems.add(kidsWithParent);
        }

        private void clear() {
            mItems.clear();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public KidsWithParent getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.fragment_watch_select_list_linear_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final KidsWithParent kidsWithParent = getItem(position);
            if (null != kidsWithParent) {
                long id = kidsWithParent.getId();
                if (-1 == id) {
                    holder.iv_head.setImageResource(R.drawable.ic_icon_profile_);
                    holder.iv_head.setBackgroundResource(R.color.color_white);
                    holder.tv_content.setText(String.format("%1$s %2$s", kidsWithParent.getName(),
                            DeviceManager.getMacAddress(kidsWithParent.getMacId())));
                    holder.iv_action.setImageResource(R.drawable.ic_icon_add_orange);
                    holder.iv_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doPlusClick(kidsWithParent.getMacId());
                        }
                    });
                } else {
                    String profile = kidsWithParent.getProfile();
                    if (!TextUtils.isEmpty(profile)) {
                        GlideHelper.showCircleImageView(mContext, profile, holder.iv_head);
                        holder.iv_head.setBackgroundResource(R.color.transparent);
                    } else {
                        holder.iv_head.setImageResource(R.drawable.ic_icon_profile_);
                        holder.iv_head.setBackgroundResource(R.color.color_white);
                    }
                    holder.tv_content.setText(kidsWithParent.getName());
                    holder.iv_action.setImageResource(R.drawable.icon_arrow_up_orange);
                    holder.iv_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doRequestClick(kidsWithParent.getMacId());
                        }
                    });
                }
            }

            return convertView;
        }
    }

    private void doPlusClick(final String macId) {
        BluetoothLeDevice device = mDeviceMap.get(macId);

        if (device == null) {
            ToastCommon.makeText(getActivity(), R.string.error_api_unknown);
            return;
        }
        showLoadingDialog(R.string.signup_login_wait);
        mBluetoothService.connectAndInitDevice(device, new IDeviceInitCallback() {
            @Override
            public void onInitComplete(String mac) {
                finishLoadingDialog();

                FragmentActivity activity = getActivity();
                if(activity instanceof MainFrameActivity){
                    ((MainFrameActivity) activity).
                            setFragment(WatchProfileFragment.newInstance(macId), true);
                }else {
                    SignupActivity signupActivity = (SignupActivity) getActivity();
                    signupActivity.setFragment(WatchProfileFragment.newInstance(macId), true);
                }
            }

            @Override
            public void onInitFail(int reason) {
                finishLoadingDialog();
                LogUtil2.getUtils().d("watch init fail,reason:" + reason);
            }

            @Override
            public void onDeviceBattery(int battery) {
                DeviceManager.saveBindWatchBattery(macId, battery);
            }
        });

    }

    private void doRequestClick(final String macId) {
        BluetoothLeDevice device = mDeviceMap.get(macId);

        if (device == null) {
            ToastCommon.makeText(getActivity(), R.string.error_api_unknown);
            return;
        }

        showLoadingDialog(R.string.signup_login_wait);
        mBluetoothService.connectAndInitDevice(device, new IDeviceInitCallback() {
            @Override
            public void onInitComplete(String mac) {

            }

            @Override
            public void onInitFail(int reason) {
                finishLoadingDialog();
                LogUtil2.getUtils().d("watch init fail, reason:" + reason);
            }

            @Override
            public void onDeviceBattery(int battery) {
                finishLoadingDialog();
                DeviceManager.saveBindWatchBattery(macId, battery);
                SignupActivity signupActivity = (SignupActivity) getActivity();
                signupActivity.setFragment(WatchRegisteredFragment.newInstance(), true);
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

    @OnClick(R.id.watch_select_dashboard)
    protected void gotoDashboard() {
        startActivity(new Intent(getActivity(), MainFrameActivity.class));
        getActivity().finish();
    }

}
