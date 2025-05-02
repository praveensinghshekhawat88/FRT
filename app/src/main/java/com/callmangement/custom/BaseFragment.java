package com.callmangement.custom;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class BaseFragment extends Fragment {
    public FragmentActivity mContext;
    private ProgressDialog mDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mContext!=null){
            mContext = (FragmentActivity) context;
        }
    }

    public void showProgress(String message) {
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void showProgress() {
        hideProgress();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void hideProgress() {
        try {
            if (mDialog!=null) mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
