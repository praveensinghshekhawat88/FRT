package com.callmangement.ui.reports;

import android.os.Bundle;
import android.view.View;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityReportsBinding;
import com.callmangement.utils.PrefManager;

public class ReportsActivity extends CustomActivity implements View.OnClickListener {
    private ActivityReportsBinding binding;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.reports));
        prefManager = new PrefManager(mContext);
        initView();
    }

    private void initView() {

        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.buttonExpenseReport.setVisibility(View.GONE);
            binding.buttonSEExpenseReport.setVisibility(View.VISIBLE);

        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.buttonExpenseReport.setVisibility(View.GONE);
            binding.buttonSEExpenseReport.setVisibility(View.VISIBLE);

        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.buttonExpenseReport.setVisibility(View.VISIBLE);
            binding.buttonSEExpenseReport.setVisibility(View.GONE);

        } else {
            binding.buttonExpenseReport.setVisibility(View.GONE);
            binding.buttonSEExpenseReport.setVisibility(View.GONE);

        }

        setUpOnclickListener();
    }

    private void setUpOnclickListener() {

        binding.buttonDailyReports.setOnClickListener(this);
        binding.buttonMonthlyReports.setOnClickListener(this);
        binding.buttonSLAReports.setOnClickListener(this);
        binding.buttonGraph.setOnClickListener(this);
        binding.buttonFpsRepeatOnServiceCenter.setOnClickListener(this);
        binding.buttonExpenseReport.setOnClickListener(this);
        binding.buttonSEExpenseReport.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonDailyReports) {
            startActivity(DailyReportsActivity.class);
        } else if (id == R.id.buttonMonthlyReports) {
            startActivity(MonthlyReportsActivity.class);
        } else if (id == R.id.buttonSLAReports) {
            startActivity(SLAReportsActivity.class);
        } else if (id == R.id.buttonGraph) {
            startActivity(GraphActivity.class);
        } else if (id == R.id.buttonFpsRepeatOnServiceCenter) {
            startActivity(FPSRepeatOnServiceCenterActivity.class);
        } else if (id == R.id.buttonExpenseReport) {
            startActivity(ExpenseReportActivity.class);
        } else if (id == R.id.buttonSEExpenseReport) {
            startActivity(ViewExpenseActivity.class);
        } else if (id == R.id.iv_back) {
            onBackPressed();
        }
    }
}