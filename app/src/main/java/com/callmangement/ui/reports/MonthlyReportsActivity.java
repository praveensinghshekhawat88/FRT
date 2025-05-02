package com.callmangement.ui.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.adapter.MonthlyReportsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityMonthlyReportsBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.reports.MonthDateModel;
import com.callmangement.model.reports.MonthReportModel;
import com.callmangement.model.reports.Monthly_Reports_Info;
import com.callmangement.report_pdf.ReportPdfActivity;
import com.callmangement.support.rackmonthpicker.RackMonthPicker;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.Constants;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthlyReportsActivity extends CustomActivity {
    private ActivityMonthlyReportsBinding binding;
    private List<ModelComplaintList> complaintList;
    private PrefManager prefManager;
    private MonthlyReportsActivityAdapter adapter;
    private final String myFormat = "yyyy-MM-dd";
    private ComplaintViewModel viewModel;
    private List<Object> objectList = new ArrayList<>();
    private final String complainStatusId = "0";
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private String fromDate;
    private String toDate;
    private boolean isLoading = false;
    private boolean allPagesLoaded = false;
    private int currentPage = 1;
    private int PAGE_SIZE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_monthly_reports);
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.buttonEXCEL.setVisibility(View.GONE);

        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.monthly_reports));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        districtNameEng = prefManager.getUSER_District();
        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
            districtId = prefManager.getUSER_DistrictId();
            binding.rlDistrict.setVisibility(View.GONE);
        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")) { // for dso
            districtId = prefManager.getUSER_DistrictId();
            binding.rlDistrict.setVisibility(View.GONE);
        } else {
            districtList();
            binding.rlDistrict.setVisibility(View.VISIBLE);
        }
        initView();
        setComplaintsListObserver();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void initView() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        @SuppressLint("SimpleDateFormat") String monthStr = new SimpleDateFormat("MMM").format(c.getTime());

        binding.textSelectYearMonth.setText(monthStr + ", " + year);

        adapter = new MonthlyReportsActivityAdapter(MonthlyReportsActivity.this);
        binding.rvMonthlyReports.setHasFixedSize(true);
        binding.rvMonthlyReports.setLayoutManager(new LinearLayoutManager(MonthlyReportsActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvMonthlyReports.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvMonthlyReports.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.rvMonthlyReports.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !allPagesLoaded && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {

                    isLoading = true;
                    fetchData();
                }
            }
        });

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).districtNameEng;
                    districtId = district_List.get(i).getDistrictId();
                    if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                        //    fetchComplaintListByDistrictWise("0");
                        districtId = "0";
                        currentPage = 1;
                        objectList.clear();
                        adapter.notifyDataSetChanged();
                        allPagesLoaded = false;
                        fetchData();
                    } else {
                        //    fetchComplaintListByDistrictWise(districtId);
                        currentPage = 1;
                        objectList.clear();
                        adapter.notifyDataSetChanged();
                        allPagesLoaded = false;
                        fetchData();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.textSelectYearMonth.setOnClickListener(view -> new RackMonthPicker(mContext)
                .setLocale(Locale.ENGLISH)
                .setPositiveButton((month1, startDate, endDate, year1, monthLabel) -> {
                    fromDate = year1 + "-" + month1 + "-" + startDate;
                    toDate = year1 + "-" + month1 + "-" + endDate;
                    binding.textSelectYearMonth.setText(monthLabel);
                    //    fetchComplaintListMonthAndYearWise(fromDate, toDate);
                    currentPage = 1;
                    objectList.clear();
                    adapter.notifyDataSetChanged();
                    allPagesLoaded = false;
                    fetchData();
                })
                .setNegativeButton(AppCompatDialog::dismiss).show());

        binding.actionBar.buttonPDF.setOnClickListener(view -> {
            if (objectList != null && objectList.size() > 0) {
                Constants.listMonthReport = getFormattedList(objectList);
                Intent intent = new Intent(mContext, ReportPdfActivity.class);
                intent.putExtra("from_where", "monthly");
                intent.putExtra("title", "MONTHLY CALL LOGGED SUMMARY");
                intent.putExtra("district", districtNameEng.equals("") ? "All District" : districtNameEng);
                intent.putExtra("name", prefManager.getUSER_NAME());
                intent.putExtra("email", prefManager.getUSER_EMAIL());
                startActivity(intent);
            } else
                Toast.makeText(mContext, getResources().getString(R.string.no_record_found_to_export_pdf), Toast.LENGTH_SHORT).show();
        });

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
    }


    private void setComplaintsListObserver() {
        SimpleDateFormat sdf_in_api = new SimpleDateFormat(myFormat, Locale.US);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date firstDateOfCurrentMonth = calendar.getTime();
        //String date1 = sdf.format(firstDateOfCurrentMonth);

        Calendar calendarToday = Calendar.getInstance();
        Date today = calendarToday.getTime();
        //String todayDate = sdf.format(today);

        String firstDateOfCurrentMonthInAPI = sdf_in_api.format(firstDateOfCurrentMonth);
        String todayDateInAPI = sdf_in_api.format(today);

        fromDate = firstDateOfCurrentMonthInAPI;
        toDate = todayDateInAPI;

        //isLoading();
        showProgress(getResources().getString(R.string.please_wait));
        viewModel.getComplaintsDistStatusDateWise(String.valueOf(prefManager.getUSER_Id()), districtId, complainStatusId, fromDate, toDate,
                "1", "" + currentPage, "" + PAGE_SIZE, "").observe(this, modelComplaint -> {
            //     isLoading();
            hideProgress();
            adapter.showLoader(false);
            Log.d("status", "status---" + modelComplaint.status);

            if (modelComplaint.status.equals("200")) {

                if (modelComplaint.currentPage == modelComplaint.totalPages)
                    allPagesLoaded = true;
                else
                    allPagesLoaded = false;

                complaintList = modelComplaint.getComplaint_List();
//                List<ModelComplaintList> sortedList = new ArrayList<>(complaintList);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Collections.sort(sortedList, Comparator.comparing(ModelComplaintList::getComplainRegDateStr));
//                }
                //        objectList = new ArrayList<>(sortedList);
                objectList.addAll(complaintList);
                Log.d("objectList", "objectList---" + objectList.size());
                if (objectList.size() > 0) {
                    binding.rvMonthlyReports.setVisibility(View.VISIBLE);
                    binding.textNoComplaint.setVisibility(View.GONE);
                    adapter.setData(getFormattedList(objectList));
                } else {
                    binding.rvMonthlyReports.setVisibility(View.GONE);
                    binding.textNoComplaint.setVisibility(View.VISIBLE);
                }

                currentPage++;

            } else if (modelComplaint.status.equals("201")) {
                binding.rvMonthlyReports.setVisibility(View.GONE);
                binding.textNoComplaint.setVisibility(View.VISIBLE);

            }

            isLoading = false;
        });
    }

    private void fetchData() {
        if (currentPage == 1)
            showProgress(getResources().getString(R.string.please_wait));
        else
            adapter.showLoader(true);

        viewModel.getComplaintsDistStatusDateWise(String.valueOf(prefManager.getUSER_Id()),
                districtId, complainStatusId, fromDate, toDate, "1", "" + currentPage, "" + PAGE_SIZE, "");
    }


    private List<MonthReportModel> getFormattedList(List<Object> objectList) {
        List<MonthReportModel> monthReportList = new ArrayList<>();
        List<Object> formattedObjectList = new ArrayList<>();
        if (objectList.size() > 0) {
            String date = ((ModelComplaintList) objectList.get(0)).complainRegDateStr;
            if (objectList.size() > 1)
                formattedObjectList.add(new MonthDateModel(date));
            formattedObjectList.add(objectList.get(0));
            for (int i = 1; i < objectList.size(); i++) {
                String date1 = ((ModelComplaintList) objectList.get(i)).complainRegDateStr;
                String date2 = ((ModelComplaintList) objectList.get(i - 1)).complainRegDateStr;
                if (!date1.equals(date2)) {
                    formattedObjectList.add(new MonthDateModel(((ModelComplaintList) objectList.get(i)).complainRegDateStr));
                    formattedObjectList.add(objectList.get(i));
                } else formattedObjectList.add(objectList.get(i));
            }
        }
        try {
            if (formattedObjectList.size() > 0) {
                List<ModelComplaintList> innerDateWiseList = null;
                String date = "";
                if (formattedObjectList.size() > 1) {
                    for (int j = 0; j < formattedObjectList.size(); j++) {
                        MonthReportModel monthReportModel = new MonthReportModel();
                        if (formattedObjectList.get(j) instanceof MonthDateModel) {
                            if (innerDateWiseList != null) {
                                monthReportModel.date = date;
                                monthReportModel.list = (innerDateWiseList);
                                monthReportList.add(monthReportModel);
                            }
                            date = ((MonthDateModel) formattedObjectList.get(j)).date;
                            innerDateWiseList = new ArrayList<>();
                        } else {
                            if (innerDateWiseList != null)
                                innerDateWiseList.add((ModelComplaintList) formattedObjectList.get(j));
                        }
                    }
                    if (innerDateWiseList != null) {
                        MonthReportModel monthReportModel = new MonthReportModel();
                        monthReportModel.date = date;
                        monthReportModel.list = (innerDateWiseList);
                        monthReportList.add(monthReportModel);
                    }
                } else {
                    MonthReportModel monthReportModel = new MonthReportModel();
                    monthReportModel.date = ((ModelComplaintList) formattedObjectList.get(0)).complainRegDateStr;
                    innerDateWiseList = new ArrayList<>();
                    innerDateWiseList.add((ModelComplaintList) formattedObjectList.get(0));
                    monthReportModel.list = (innerDateWiseList);
                    monthReportList.add(monthReportModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthReportList;
    }

    private void districtList() {
        viewModel.getDistrict().observe(this, modelDistrict -> {
            if (modelDistrict.status.equals("200")) {
                district_List = modelDistrict.district_List;

                if (district_List != null && district_List.size() > 0) {
                    Collections.reverse(district_List);
                    ModelDistrictList l = new ModelDistrictList();
                    l.setDistrictId(String.valueOf(-1));
                    l.districtNameEng = "--" + getResources().getString(R.string.district) + "--";
                    district_List.add(l);
                    Collections.reverse(district_List);

                    ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, district_List);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerDistrict.setAdapter(dataAdapter);

                }

            }
        });
    }

    private void isLoading() {
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    private static List<String> getDates(String dateString1, String dateString2) {
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
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
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