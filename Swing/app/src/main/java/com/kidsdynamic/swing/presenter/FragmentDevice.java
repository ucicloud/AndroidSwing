package com.kidsdynamic.swing.presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.kidsdynamic.data.dao.DB_Kids;
import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.domain.DeviceManager;
import com.kidsdynamic.swing.utils.ViewUtils;
import com.kidsdynamic.swing.view.ViewCircle;

import java.util.Locale;


public class FragmentDevice extends BaseFragment {
    private final static int STATUS_SEARCH = 0;
    private final static int STATUS_NOTFOUND = 1;
    private final static int STATUS_FOUND = 2;
    private final static int STATUS_NOKID = 3;

    private View mViewMain;

    private TextView tv_title;
    private TextView mViewStatus;
    private TextView mViewCapacity;
    private ViewCircle mViewProgress;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewMain = inflater.inflate(R.layout.fragment_device, container, false);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewMain.getWindowToken(), 0);

        initTitle();

        mViewStatus = (TextView) mViewMain.findViewById(R.id.device_status);
        mViewCapacity = (TextView) mViewMain.findViewById(R.id.device_capacity);
        mViewProgress = (ViewCircle) mViewMain.findViewById(R.id.device_battery);
        mViewProgress.setOnProgressListener(mProgressListener);

        ViewUtils.setTextViewBoldTypeFace(getContext(),mViewStatus,mViewCapacity);

        mHandler = new Handler();

        return mViewMain;
    }

    private void initTitle() {

        tv_title = ((TextView) mViewMain.findViewById(R.id.main_toolbar_title));
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取缓存电量
        String title = getString(R.string.device_owner, "My");
        int battery = -1;

        DB_Kids focusWatchInfo = DeviceManager.getFocusWatchInfo(getContext());
        if(focusWatchInfo != null){
            title = getString(R.string.device_owner, focusWatchInfo.getName());
            battery = DeviceManager.getCacheWatchBattery(focusWatchInfo.getMacId());

            showBattery(battery);
        }

        tv_title.setText(title);

        //todo test battery show
       /* if(battery <= 0){
            setCapacity(60);
        }*/

        /*if (mActivityMain.mOperator.mFocusBatteryName.equals("")) {
            mHandler.postDelayed(mRunnable, 1000);

            if (mActivityMain.mOperator.getFocusKid() != null) {
                setStatus(STATUS_SEARCH);
                setTitle(mActivityMain.mOperator.getFocusKid().mName);

                mViewProgress.setStrokeBeginEnd(0, 10);
                mViewProgress.startProgress(30, -1, -1);
            } else {
                setStatus(STATUS_NOKID);
                mViewProgress.setActive(false);
            }
        } else {
            setTitle(mActivityMain.mOperator.mFocusBatteryName);
            setStatus(STATUS_FOUND);
            setCapacity(mActivityMain.mOperator.mFocusBatteryValue);
        }*/
    }

    private void showBattery(int battery) {
        setCapacity(battery);
    }

    @Override
    public void onPause() {
        super.onPause();
//        mActivityMain.mBLEMachine.Cancel();

        mViewProgress.stopProgress();

        mHandler.removeCallbacksAndMessages(null);
//        mHandler.removeCallbacks(mRunnable);
    }

    private void setStatus(int status) {
        if (status == STATUS_SEARCH) {
            mViewStatus.setText(getResources().getString(R.string.device_searching));
        } else if (status == STATUS_NOTFOUND) {
            mViewStatus.setText(getResources().getString(R.string.device_not_found));
        } else if (status == STATUS_FOUND) {
            mViewStatus.setText(getResources().getString(R.string.device_battery));
        } else if (status == STATUS_NOKID) {
            mViewStatus.setText(getResources().getString(R.string.device_no_watch));
        }
    }

    private void setTitle(String name) {
        String string = String.format(Locale.getDefault(),
                getResources().getString(R.string.device_owner), name);
//        mActivityMain.toolbarSetTitle(string, false);
    }

    private void setCapacity(int percent) {
        String string = String.format(Locale.getDefault(), "%d%%", percent);
        mViewCapacity.setText(string);

        if(percent == 0){
            mViewProgress.setStrokeBeginEnd(-1, -1);
        }else {
            mViewProgress.setStrokeBeginEnd(0, percent);
        }
    }

    /*private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            WatchContact.Kid kid = mActivityMain.mOperator.getFocusKid();
            if (kid != null)
                mActivityMain.mBLEMachine.Battery(mOnBatteryListener, ServerMachine.getMacAddress(kid.mMacId));
        }
    };

    BLEMachine.onBatteryListener mOnBatteryListener = new BLEMachine.onBatteryListener() {
        @Override
        public void onBattery(byte value) {
            mViewProgress.stopProgress();

            if (value == (byte) 0xFF) {
                setStatus(STATUS_NOTFOUND);
                mViewProgress.setActive(false);
            } else {
                setStatus(STATUS_FOUND);
                setCapacity(value);

                mActivityMain.mOperator.mFocusBatteryName = mActivityMain.mOperator.getFocusKid().mName;
                mActivityMain.mOperator.mFocusBatteryValue = value;
            }
        }
    };
*/
    private ViewCircle.OnProgressListener mProgressListener = new ViewCircle.OnProgressListener() {
        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
        }
    };

}
