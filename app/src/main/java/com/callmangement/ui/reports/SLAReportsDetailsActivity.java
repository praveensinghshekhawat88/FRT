package com.callmangement.ui.reports;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.callmangement.R;
import com.callmangement.adapter.SLAReportsDetailsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivitySlaReportsDetailsBinding;
import com.callmangement.model.reports.ModelSLAReportDetailsList;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class SLAReportsDetailsActivity extends CustomActivity {
    ActivitySlaReportsDetailsBinding binding;
    private SLAReportsDetailsActivityAdapter adapter;
    private PrefManager prefManager;
    private List<ModelSLAReportDetailsList> complaintList = new ArrayList<>();
    private final String myFormat = "yyyy-MM-dd";
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private ComplaintViewModel viewModel;
    private Integer districtId = 0;
    private Integer intervalDays = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sla_reports_details);
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.reports_details));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        districtId = getIntent().getIntExtra("districtId", 0);
        intervalDays = getIntent().getIntExtra("intervalDays", 0);
        initView();
        String fromDate = "";
        String toDate = "";
        fetchSLAReportDetails(fromDate, toDate, intervalDays, ""+districtId);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        adapter = new SLAReportsDetailsActivityAdapter(SLAReportsDetailsActivity.this);
        adapter.notifyDataSetChanged();
        binding.rvComplaints.setHasFixedSize(true);
        binding.rvComplaints.setLayoutManager(new LinearLayoutManager(SLAReportsDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvComplaints.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvComplaints.setAdapter(adapter);
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

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());

    }

    @SuppressLint("SetTextI18n")
    private void fetchSLAReportDetails(String fromDate, String toDate, Integer day, String districtId) {
        try {
            complaintList.clear();
            isLoading();
            viewModel.getSLAReportDetails(String.valueOf(prefManager.getUSER_Id()), fromDate, toDate, day, districtId).observe(this, modelComplaint -> {
                isLoading();
                if (modelComplaint.status.equals("200")) {
                    complaintList = modelComplaint.sla_reports_infos;
                    if (complaintList != null && complaintList.size() > 0) {
                        binding.rvComplaints.setVisibility(View.VISIBLE);
                        binding.textNoComplaint.setVisibility(View.GONE);
                        binding.textTotalComplaint.setText(""+complaintList.size());
                        adapter.setData(complaintList);
                    }else {
                        binding.rvComplaints.setVisibility(View.GONE);
                        binding.bottomLayout.setVisibility(View.GONE);
                        binding.textNoComplaint.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void isLoading(){
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean){
                showProgress(getResources().getString(R.string.please_wait));
            }else {
                hideProgress();
            }
        });
    }
}