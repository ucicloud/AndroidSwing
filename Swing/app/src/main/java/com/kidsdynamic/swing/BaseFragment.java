package com.kidsdynamic.swing;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * fragment 基类
 * BaseFragment
 */

public class BaseFragment extends Fragment {
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
}
