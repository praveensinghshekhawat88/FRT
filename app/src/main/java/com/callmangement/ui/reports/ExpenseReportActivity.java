package com.callmangement.ui.reports;

import android.os.Bundle;
import android.view.View;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityExpenseReportBinding;
import com.callmangement.utils.PrefManager;

public class ExpenseReportActivity extends CustomActivity implements View.OnClickListener {
    private ActivityExpenseReportBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.expense_report));
        setUpOnclickListener();
    }

    private void setUpOnclickListener() {
        binding.buttonUploadExpense.setOnClickListener(this);
        binding.buttonViewExpense.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonUploadExpense) {
            startActivity(UploadExpenseActivity.class);
        } else if (id == R.id.buttonViewExpense) {
            startActivity(ViewExpenseActivity.class);
        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }
}