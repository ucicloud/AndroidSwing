package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.view.ProgressWheel;

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
    ProgressWheel progressWheel;

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
        progressWheel.setOnProgressListener(new OnCircleProgressListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        progressWheel.startSpinning();
    }

    @Override
    public void onPause() {
        super.onPause();
        progressWheel.stopSpinning();
    }

    @Override
    public void onStop() {
        super.onStop();
        progressWheel.stopSpinning();
    }

    private class OnCircleProgressListener implements ProgressWheel.OnProgressListener {

        private int random;

        public OnCircleProgressListener() {
            random = new Random().nextInt(360);
        }

        @Override
        public void onProgress(int repeatCount, float progress) {
            if (repeatCount >= 1 && progress > random) {
                SignupActivity signupActivity = (SignupActivity) getActivity();
                signupActivity.setFragmentAndAddBackStack(WatchSelectFragment.newInstance());
            }
        }
    }

}
