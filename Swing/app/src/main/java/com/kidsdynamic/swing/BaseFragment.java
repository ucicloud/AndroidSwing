package com.kidsdynamic.swing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

/**
 * fragment 基类
 * BaseFragment
 */

public class BaseFragment extends Fragment {

    protected final static String BUNDLE_KEY_DATE = "DATE";

    private ProgressDialog progressDialog;

    public static BaseFragment newInstance(String info) {
        Bundle args = new Bundle();
        BaseFragment fragment = new BaseFragment();
        args.putString("info", info);
        fragment.setArguments(args);
        return fragment;
    }

/*    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        TextView tvInfo = (TextView) view.findViewById(R.id.textView);
        tvInfo.setText(getArguments().getString("info"));
        *//*tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Don't click me.please!.", Snackbar.LENGTH_SHORT).show();
            }
        });*//*
        return view;
    }*/

    public void showLoadingDialog(int msgId){
        progressDialog = ProgressDialog.show(getActivity(), null,
                getString(msgId), true, false);
    }

    public void finishLoadingDialog(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public void hideInput(){
        if(getActivity() != null && getActivity().getCurrentFocus() != null &&
                getActivity().getCurrentFocus().getWindowToken() != null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
