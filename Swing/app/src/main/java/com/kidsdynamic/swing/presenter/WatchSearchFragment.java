package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.view.ViewCircle;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * WatchSearchFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchSearchFragment extends BaseFragment {

    @BindView(R.id.watch_search_progress)
    ViewCircle progressCircle;

    public static WatchSearchFragment newInstance() {
        Bundle args = new Bundle();
        WatchSearchFragment fragment = new WatchSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_search, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressCircle.setStrokeBeginEnd(0, 10);
        progressCircle.setOnProgressListener(new OnCircleProgressListener(1, progressCircle.getStrokeCount()));
    }

    @Override
    public void onResume() {
        super.onResume();
        progressCircle.startProgress(10, -1, -1);
    }

    @Override
    public void onPause() {
        super.onPause();
        progressCircle.stopProgress();
    }

    @Override
    public void onStop() {
        super.onStop();
        progressCircle.stopProgress();
    }

    private class OnCircleProgressListener implements ViewCircle.OnProgressListener {

        private int destCount, bound, random, count;

        private OnCircleProgressListener(int destCount, int bound) {
            this.destCount = destCount;
            this.bound = bound;
            random = new Random().nextInt(bound);
        }

        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            if (begin == bound - 1) count += 1;
            if (count == destCount && end == random) {
                //modify 2017年11月16日16:42:33 only_app
                //根据当前fragment所在activity来使用不同的方法跳转
                FragmentActivity activity = getActivity();
                if(activity instanceof MainFrameActivity){
                    ((MainFrameActivity) activity).setFragment(WatchSelectFragment.newInstance(), true);
                }else {
                    SignupActivity signupActivity = (SignupActivity) getActivity();
                    signupActivity.setFragment(WatchSelectFragment.newInstance(), true);
                }
            }
        }
    }

}
