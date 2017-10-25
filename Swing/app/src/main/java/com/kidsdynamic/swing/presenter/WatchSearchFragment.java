package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
    ViewCircle vc_progress;

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
        vc_progress.setOnProgressListener(new OnCircleProgressListener(1, vc_progress.getStrokeCount()));
        vc_progress.setStrokeBeginEnd(0, 0);
        vc_progress.startProgress(250, -1, -1);
    }

    @Override
    public void onStop() {
        super.onStop();
        vc_progress.stopProgress();
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
                SignupActivity signupActivity = (SignupActivity) getActivity();
                if (random % 2 == 0) {
                    signupActivity.setFragment(WatchSelectFragment.newInstance());
                } else {
                    signupActivity.setFragment(WatchSorryFragment.newInstance());
                }
            }
        }
    }

}
