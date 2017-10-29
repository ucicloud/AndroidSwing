package com.kidsdynamic.swing.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsdynamic.swing.BaseFragment;
import com.kidsdynamic.swing.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * calendar fragments container
 * date:   2017/10/28 11:53 <br/>
 */

public class CalendarContainerFragment extends BaseFragment {

    @BindView(R.id.main_toolbar_title)
    protected TextView tv_title;
    @BindView(R.id.main_toolbar_action1)
    protected ImageView view_left_action;
    @BindView(R.id.main_toolbar_action2)
    protected ImageView view_right_action;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_calendar_container, null);

        ButterKnife.bind(this,layoutView);


        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initTitleBar();

        selectFragment(CalendarMainFragment.class.getName(),null,false);

        /*getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if(keycode == KeyEvent.KEYCODE_BACK){
                    ToastCommon.makeText(getContext(),R.string.error_api_kid_who_registered_mac_id_404);

                    if (getFragmentManager().getBackStackEntryCount() == 0) {
                        getActivity().finish();
                    }
                    return true;
                }
                return false;
            }
        });*/
    }

    private void initTitleBar() {
        tv_title.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_title.setText(R.string.title_calendar);
        view_left_action.setImageResource(R.drawable.icon_calendar);
        view_right_action.setImageResource(R.drawable.icon_add);
    }

    public void selectFragment(String className, Bundle args, boolean isAddToBackStack) {
        Fragment fragment = Fragment.instantiate(getContext(), className, args);

        /*Bundle bundle = new Bundle();
        String[] pageName = className.split("\\.");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName[pageName.length-1]);
        FirebaseLog(LogEvent.Event.SWITCH_PAGE, bundle);*/

        FragmentTransaction fragmentTransaction = getFragmentManager()
                .beginTransaction()
                .replace(R.id.calender_fragment_container, fragment, className);

        if(isAddToBackStack){
            fragmentTransaction
                    .addToBackStack(null);
        }

        fragmentTransaction .commit();

    }




}
