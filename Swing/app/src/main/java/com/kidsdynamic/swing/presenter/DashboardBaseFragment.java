package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/1.
 */

public class DashboardBaseFragment extends BaseFragment {


    @BindView(R.id.main_toolbar_title)
    protected TextView tv_title;
    @BindView(R.id.main_toolbar_action1)
    protected ImageView view_left_action;
    @BindView(R.id.main_toolbar_action2)
    protected ImageView view_right_action;


    public void selectFragment(String className, Bundle args, boolean isAddToBackStack) {
        Fragment fragment = Fragment.instantiate(getContext(), className, args);

        /*Bundle bundle = new Bundle();
        String[] pageName = className.split("\\.");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName[pageName.length-1]);
        FirebaseLog(LogEvent.Event.SWITCH_PAGE, bundle);*/

        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_fragment_container, fragment, className);

        if (isAddToBackStack) {
            fragmentTransaction
                    .addToBackStack(null);
        }

        fragmentTransaction.commit();

    }

    public void setFragment(Fragment fragment, boolean isAddBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_fragment_container, fragment);
        if (isAddBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

}
