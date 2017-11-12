package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidsdynamic.swing.R;
import com.kidsdynamic.swing.view.ViewCircle;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DashboardProgressFragment_UI
 * <p>
 * Created by Stefan on 2017/11/7.
 */

public class DashboardProgressFragment_UI extends DashboardBaseFragment {

    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.dashboard_progress_circle)
    ViewCircle progressCircle;
    @BindView(R.id.dashboard_progress_button_first)
    TextView tvFirst;
    @BindView(R.id.dashboard_progress_button_second)
    TextView tvSecond;

    public static DashboardProgressFragment_UI newInstance() {
        Bundle args = new Bundle();
        DashboardProgressFragment_UI fragment = new DashboardProgressFragment_UI();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard_progress, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_dashboard);
        view_left_action.setImageResource(R.drawable.icon_left);
        progressCircle.setStrokeBeginEnd(0, 10);
        progressCircle.setOnProgressListener(new OnCircleProgressListener(progressCircle.getStrokeCount()));
    }

    @Override
    public void onResume() {
        super.onResume();
        progressCircle.startProgress(30, -1, -1);
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

    @OnClick(R.id.main_toolbar_action1)
    public void back() {
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.dashboard_progress_button_first)
    public void onClickFirst() {

    }

    @OnClick(R.id.dashboard_progress_button_second)
    public void onClickSecond() {

    }

    private class OnCircleProgressListener implements ViewCircle.OnProgressListener {

        private int bound, random, count;

        private OnCircleProgressListener(int bound) {
            this.bound = bound;
            random = new Random().nextInt(bound);
        }

        @Override
        public void onProgress(ViewCircle view, int begin, int end) {
            if (begin == bound - 1) count += 1;
            if (count == 1 && end == random) {
                // Syncing
                if (end % 2 == 0) {
                    tvMessage.setText(R.string.dashboard_progress_syncing);
                } else {
                    // We can't find your watch
                    progressCircle.stopProgress();
                    progressCircle.setStrokeBegin(-1);
                    progressCircle.setStrokeEnd(0);
                    tvMessage.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    tvMessage.setPadding(0, 0, 0, 0);
                    tvMessage.setText(R.string.dashboard_progress_not_found);
                    tvMessage.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, 0, R.drawable.monster_green);
                    tvFirst.setVisibility(View.VISIBLE);
                    tvFirst.setText(R.string.dashboard_progress_again);
                    tvSecond.setVisibility(View.VISIBLE);
                }
            } else if (count == 2 && end == random) {
                // completed
                progressCircle.stopProgress();
                progressCircle.setStrokeBegin(0);
                progressCircle.setStrokeEnd(100);
                tvMessage.setGravity(Gravity.CENTER);
                tvMessage.setText(R.string.dashboard_progress_completed);
                tvMessage.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, R.drawable.monster_yellow);
                tvFirst.setVisibility(View.VISIBLE);
                tvFirst.setText(R.string.dashboard_progress_dashboard);
            }
        }
    }

}
