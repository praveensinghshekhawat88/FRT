package com.callmangement.ui.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.callmangement.R;
import com.callmangement.adapter.SLAReportsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivitySlaReportsBinding;
import com.callmangement.model.reports.Days_Reports_Info;
import com.callmangement.model.reports.Monthly_Reports_Info;
import com.callmangement.model.reports.SLA_Reports_Info;
import com.callmangement.report_pdf.SLAReportPdfActivity;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SLAReportsActivity extends CustomActivity {
    ActivitySlaReportsBinding binding;
    private List<SLA_Reports_Info> sla_reports_infoArrayList = new ArrayList<>();
    private PrefManager prefManager;
    private SLAReportsActivityAdapter adapter;
    private final String myFormat = "yyyy-MM-dd"; //In which you need put here
    private ComplaintViewModel viewModel;

    private final List<Days_Reports_Info> daysList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sla_reports);
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.sla_reports));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        initView();

        Integer day = 3;
        String fromDate = "";
        String toDate = "";
        String districtId = "0";

        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            districtId = prefManager.getUSER_DistrictId();
        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){
            districtId = prefManager.getUSER_DistrictId();
        }

        fetchSLAReportList(fromDate, toDate, day, districtId);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        adapter = new SLAReportsActivityAdapter(SLAReportsActivity.this);
        adapter.notifyDataSetChanged();
        binding.rvSlaReports.setHasFixedSize(true);
        binding.rvSlaReports.setLayoutManager(new LinearLayoutManager(SLAReportsActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvSlaReports.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvSlaReports.setAdapter(adapter);

        binding.actionBar.buttonPDF.setOnClickListener(view -> {
            if (sla_reports_infoArrayList != null && sla_reports_infoArrayList.size() > 0){
                startActivity(new Intent(mContext, SLAReportPdfActivity.class)
                        .putExtra("param", (Serializable) sla_reports_infoArrayList)
                        .putExtra("ResolveInDays", adapter.getDays())
                        .putExtra("title", "SLA SUMMARY REPORT")
                        .putExtra("district", prefManager.getUSER_District())
                        .putExtra("name", prefManager.getUSER_NAME())
                        .putExtra("email", prefManager.getUSER_EMAIL()));
            }else {
                Toast.makeText(mContext, getResources().getString(R.string.no_record_found_to_export_pdf), Toast.LENGTH_SHORT).show();
            }
        });

        for(int i = 3;i<31;i++) {
            Days_Reports_Info days_reports_info = new Days_Reports_Info();
            days_reports_info.day = i + " Days";
            days_reports_info.days_count = i;
            daysList.add(days_reports_info);
        }

        if (daysList.size() > 0) {
            ArrayAdapter<Days_Reports_Info> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, daysList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerDays.setAdapter(dataAdapter);
        }

        binding.spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Integer day = daysList.get(i).days_count;
                String fromDate = "";
                String toDate = "";
                String districtId = "0";
                if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
                    districtId = prefManager.getUSER_DistrictId();
                } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){
                    districtId = prefManager.getUSER_DistrictId();
                }
                fetchSLAReportDayWise(fromDate, toDate, day, districtId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void fetchSLAReportList(String fromDate, String toDate, Integer ResolveInDays, String districtId){
        sla_reports_infoArrayList.clear();
        isLoading();
        viewModel.getSLAReportList(String.valueOf(prefManager.getUSER_Id()), fromDate, toDate, ResolveInDays, districtId).observe(this, modelSLAReport -> {
            isLoading();
            if (modelSLAReport.status.equals("200")) {
                sla_reports_infoArrayList = modelSLAReport.sla_reports_infos;
                if (sla_reports_infoArrayList.size()!=0) {
                    binding.rvSlaReports.setVisibility(View.VISIBLE);
                    binding.textNoComplaint.setVisibility(View.GONE);
                    adapter.setDays(ResolveInDays);
                    adapter.setData(sla_reports_infoArrayList);
                } else {
                    binding.rvSlaReports.setVisibility(View.GONE);
                    binding.textNoComplaint.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void fetchSLAReportDayWise(String fromDate, String toDate, Integer ResolveInDays, String districtId){
        sla_reports_infoArrayList.clear();
        isLoading();
        viewModel.getSLAReportList(String.valueOf(prefManager.getUSER_Id()), fromDate, toDate, ResolveInDays, districtId).observe(this, modelSLAReport -> {
            isLoading();
            if (modelSLAReport.status.equals("200")) {
                sla_reports_infoArrayList = modelSLAReport.sla_reports_infos;
                if (sla_reports_infoArrayList.size()!=0) {
                    binding.rvSlaReports.setVisibility(View.VISIBLE);
                    binding.textNoComplaint.setVisibility(View.GONE);
                    adapter.setDays(ResolveInDays);
                    adapter.setData(sla_reports_infoArrayList);
                }else {
                    binding.rvSlaReports.setVisibility(View.GONE);
                    binding.textNoComplaint.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void isLoading(){
        viewModel.isLoading().observe(this, aBoolean -> {
            if (aBoolean){
                showProgress(getResources().getString(R.string.please_wait));
            }else {
                hideProgress();
            }
        });
    }

    private static List<String> getDates(String dateString1, String dateString2)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        ArrayList<String> dates = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(Objects.requireNonNull(date1));


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(Objects.requireNonNull(date2));

        while(!cal1.after(cal2))
        {
            String newDate = sdf.format(cal1.getTime());
            dates.add(newDate);
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public class CustomComparator implements Comparator<Monthly_Reports_Info> {
        @Override
        public int compare(Monthly_Reports_Info o1, Monthly_Reports_Info o2) {
            return o1.date.compareTo(o2.date);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}