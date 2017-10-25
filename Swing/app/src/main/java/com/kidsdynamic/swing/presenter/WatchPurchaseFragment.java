package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.ButterKnife;

/**
 * WatchPurchaseFragment
 * <p>
 * Created by Stefan on 2017/10/25.
 */

public class WatchPurchaseFragment extends BaseFragment {

    public static WatchPurchaseFragment newInstance() {
        Bundle args = new Bundle();
        WatchPurchaseFragment fragment = new WatchPurchaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_watch_purchase, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

}
