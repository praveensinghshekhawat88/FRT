package com.callmangement.ui.reports;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.callmangement.R;
import com.callmangement.adapter.DailyReportsDetailsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDailyReportsDetailsBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class DailyReportsDetailsActivity extends CustomActivity {
    ActivityDailyReportsDetailsBinding binding;
    private DailyReportsDetailsActivityAdapter adapter;
    private List<ModelComplaintList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_reports_details);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.reports_details));
        initView();
    }

    private void initView() {
        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
        setComplaintList();
    }

    @SuppressLint("SetTextI18n")
    private void setComplaintList() {
        binding.rvComplaints.setLayoutManager(new LinearLayoutManager(DailyReportsDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvComplaints.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));

        String complainDataStr = "";
        if (getIntent().getStringExtra("complain_data") != null)
            complainDataStr = getIntent().getStringExtra("complain_data");
        if (!complainDataStr.equals("") && !complainDataStr.equals("[]")) {
            binding.rvComplaints.setVisibility(View.VISIBLE);
            binding.textNoComplaint.setVisibility(View.GONE);
            Gson gson = new Gson();
            list = gson.fromJson(complainDataStr, new TypeToken<List<ModelComplaintList>>() {}.getType());
            adapter = new DailyReportsDetailsActivityAdapter(DailyReportsDetailsActivity.this,list);
            binding.rvComplaints.setAdapter(adapter);
            binding.textTotalComplaint.setText(""+list.size());
        } else {
            binding.rvComplaints.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.GONE);
            binding.textNoComplaint.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            list = gson.fromJson(complainDataStr, new TypeToken<List<ModelComplaintList>>() {}.getType());
            adapter = new DailyReportsDetailsActivityAdapter(DailyReportsDetailsActivity.this,list);
            binding.rvComplaints.setAdapter(adapter);
        }

        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}