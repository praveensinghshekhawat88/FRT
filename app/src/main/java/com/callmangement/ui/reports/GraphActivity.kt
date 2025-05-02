package com.callmangement.ui.reports;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityGraphBinding;
import com.callmangement.model.complaints.ModelComplaint;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.support.charting.components.Legend;
import com.callmangement.support.charting.components.XAxis;
import com.callmangement.support.charting.data.BarData;
import com.callmangement.support.charting.data.BarDataSet;
import com.callmangement.support.charting.data.BarEntry;
import com.callmangement.support.charting.formatter.IndexAxisValueFormatter;
import com.callmangement.support.charting.interfaces.datasets.IBarDataSet;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.utils.PrefManager;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphActivity extends CustomActivity {
    ActivityGraphBinding binding;
    private PrefManager prefManager;
    private ComplaintViewModel viewModel;
    private List<ModelComplaintList> complaintList;
    private final String myFormat = "yyyy-MM-dd";
    private final String complainStatusId = "0";
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private List<ModelDistrictList> district_List = new ArrayList<>();
    public static final int[] MATERIAL_COLORS = {rgb("#5d62b5")};
    public static final int[] MATERIAL_COLORS2 = {rgb("#29c3be")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_graph);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.graph));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        prefManager = new PrefManager(mContext);
        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
            districtId = prefManager.getUSER_DistrictId();
            binding.rlDistrict.setVisibility(View.GONE);
        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){ // for dso
            districtId = prefManager.getUSER_DistrictId();
            binding.rlDistrict.setVisibility(View.GONE);
        } else{
            districtList();
            binding.rlDistrict.setVisibility(View.VISIBLE);
        }
        initView();
        fetchComplaintList();
    }

    private void initView() {
        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    districtId = district_List.get(i).getDistrictId();
                    if (districtNameEng.equalsIgnoreCase("--"+getResources().getString(R.string.district)+"--")) {
                        fetchComplaintListByDistrictWise("0");
                    } else {
                        fetchComplaintListByDistrictWise(districtId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());

    }

    private void fetchComplaintList(){
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Calendar calendarToday = Calendar.getInstance();
        Date today = calendarToday.getTime();
        String todayDate = sdf.format(today);

        Calendar calendarLastThreeMonth = Calendar.getInstance();
        calendarLastThreeMonth.add(Calendar.MONTH, -2);
        calendarLastThreeMonth.set(Calendar.DATE, 1);
        Date lastThreeMonthDate = calendarLastThreeMonth.getTime();
        String lastThreeMonthDateStr = sdf.format(lastThreeMonthDate);

        String fromDate = lastThreeMonthDateStr;
        String toDate = todayDate;

        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaint> call = service.getComplaintListDistStatusDateWise(String.valueOf(prefManager.getUSER_Id()), districtId, complainStatusId, fromDate, toDate,"0","0","0","");
        call.enqueue(new Callback<ModelComplaint>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<ModelComplaint> call, @NonNull Response<ModelComplaint> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    ModelComplaint modelComplaint = response.body();
                    if (Objects.requireNonNull(modelComplaint).getStatus().equals("200")) {
                        complaintList = modelComplaint.getComplaint_List();
                        List<ModelComplaintList> sortedList = new ArrayList<>(complaintList);
                        for (ModelComplaintList model : sortedList) {
                            model.setRegistrationComplainDateTimeStamp(DateTimeUtils.getTimeStamp(model.getComplainRegDateStr()));
                        }
                        Collections.sort(sortedList, Comparator.comparingLong(ModelComplaintList::getRegistrationComplainDateTimeStamp));
                        totalComplaintBarGraph(sortedList);
                        List<String> listMonthName = new ArrayList<>();
                        for (ModelComplaintList modelMonth : sortedList) {
//                            ModelComplaintRegistrationDate monthDetailModel = modelMonth.getComplainRegDate();
//                            String monthName = monthDetailModel.getMonth();

                            String monthDetailModel = modelMonth.getComplainRegDateStr();
                            String[] mnt_list = monthDetailModel.split("-");

                            String date = mnt_list[0];
                            int month = Integer.parseInt(mnt_list[1]);
                            String year = mnt_list[2];

                     String monthString = new DateFormatSymbols().getMonths()[month-1];

                            String monthName = monthString;
                            if (listMonthName.size() == 0)
                                listMonthName.add(monthName);
                            else {
                                boolean existFlag = false;
                                for (int i = 0; i < listMonthName.size(); i++) {
                                    if (monthName.equalsIgnoreCase(listMonthName.get(i))) {
                                        existFlag = true;
                                        break;
                                    }
                                }
                                if (!existFlag)
                                    listMonthName.add(monthName);
                            }
                        }

                        try {
                            binding.textFirstMonth.setText(listMonthName.get(0));
                            binding.textSecondMonth.setText(listMonthName.get(1));
                            binding.textCurrentMonth.setText(listMonthName.get(2));
                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.barGraphTotalComplaints.setVisibility(View.GONE);
                        }
                    }else {
                        makeToast(modelComplaint.getMessage());
                    }
                }else {
                    makeToast(getResources().getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaint> call, @NonNull Throwable t) {
                hideProgress();
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    private void fetchComplaintListByDistrictWise(String districtId){
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        Calendar calendarToday = Calendar.getInstance();
        Date today = calendarToday.getTime();
        String todayDate = sdf.format(today);

        Calendar calendarLastThreeMonth = Calendar.getInstance();
        calendarLastThreeMonth.add(Calendar.MONTH, -2);
        calendarLastThreeMonth.set(Calendar.DATE, 1);
        Date lastThreeMonthDate = calendarLastThreeMonth.getTime();
        String lastThreeMonthDateStr = sdf.format(lastThreeMonthDate);

        String fromDate = lastThreeMonthDateStr;
        String toDate = todayDate;

        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaint> call = service.getComplaintListDistStatusDateWise(String.valueOf(prefManager.getUSER_Id()), districtId, complainStatusId, fromDate, toDate,"0","0","0","");
        call.enqueue(new Callback<ModelComplaint>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<ModelComplaint> call, @NonNull Response<ModelComplaint> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    ModelComplaint modelComplaint = response.body();
                    if (Objects.requireNonNull(modelComplaint).getStatus().equals("200")) {
                        complaintList = modelComplaint.getComplaint_List();
                        List<ModelComplaintList> sortedList = new ArrayList<>(complaintList);
                        for (ModelComplaintList model : sortedList) {
                            model.setRegistrationComplainDateTimeStamp(DateTimeUtils.getTimeStamp(model.getComplainRegDateStr()));
                        }

                        Collections.sort(sortedList, Comparator.comparingLong(ModelComplaintList::getRegistrationComplainDateTimeStamp));
                        totalComplaintBarGraph(sortedList);

                        List<String> listMonthName = new ArrayList<>();
                        for (ModelComplaintList modelMonth : sortedList) {
//                            ModelComplaintRegistrationDate monthDetailModel = modelMonth.getComplainRegDate();
//                            String monthName = monthDetailModel.getMonth();
                   //         String monthName = "Monthhhhhh";


                            String monthDetailModel = modelMonth.getComplainRegDateStr();
                            Log.d("--monthDetailModel--"," "+ monthDetailModel);
                            String[] mnt_list = monthDetailModel.split("-");

                            String date = mnt_list[0];
                            int month = Integer.parseInt(mnt_list[1]);
                            String year = mnt_list[2];

                            String monthString = new DateFormatSymbols().getMonths()[month-1];

                            String monthName = monthString;









                            if (listMonthName.size() == 0)
                                listMonthName.add(monthName);
                            else {
                                boolean existFlag = false;
                                for (int i = 0; i < listMonthName.size(); i++) {
                                    if (monthName.equalsIgnoreCase(listMonthName.get(i))) {
                                        existFlag = true;
                                        break;
                                    }
                                }
                                if (!existFlag)
                                    listMonthName.add(monthName);
                            }
                        }

                        try {
                            binding.textFirstMonth.setText(listMonthName.get(0));
                            binding.textSecondMonth.setText(listMonthName.get(1));
                            binding.textCurrentMonth.setText(listMonthName.get(2));
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.barGraphTotalComplaints.setVisibility(View.GONE);
                        }
                    }else {
                        makeToast(modelComplaint.getMessage());
                    }
                }else {
                    makeToast(getResources().getString(R.string.error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaint> call, @NonNull Throwable t) {
                hideProgress();
                makeToast(getResources().getString(R.string.error_message));
            }

        });
    }

    private void districtList(){
        viewModel.getDistrict().observe(this, modelDistrict -> {
            if (modelDistrict.getStatus().equals("200")){
                district_List = modelDistrict.getDistrict_List();

                if (district_List != null && district_List.size() > 0) {
                    Collections.reverse(district_List);
                    ModelDistrictList l = new ModelDistrictList();
                    l.setDistrictId(String.valueOf(-1));
                    l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //start total complaints bar graph
    private void totalComplaintBarGraph(List<ModelComplaintList> modelComplaintList) {
        binding.barGraphTotalComplaints.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        binding.barGraphTotalComplaints.setMaxVisibleValueCount(60);
        binding.barGraphTotalComplaints.setDrawValueAboveBar(true);

        // scaling can now only be done on x- and y-axis separately
        binding.barGraphTotalComplaints.setPinchZoom(false);

        binding.barGraphTotalComplaints.setDrawBarShadow(false);
        binding.barGraphTotalComplaints.setDrawGridBackground(false);

        // add a nice and smooth animation
        binding.barGraphTotalComplaints.animateY(1500);

//        binding.barGraphTotalComplaints.getLegend().setEnabled(false);

        Legend l = binding.barGraphTotalComplaints.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(10f);
        l.setYEntrySpace(0f);
        l.setYOffset(7f);
        l.setTextSize(12f);

        binding.barGraphTotalComplaints.setExtraOffsets(0f, 0f, 0f, 0f);
        setDataTotalComplaintsBarGraph(modelComplaintList);

    }

    private void setDataTotalComplaintsBarGraph(List<ModelComplaintList> modelComplaintList) {
        float barWidth = 0.3f;
        float barSpace = 0.0f;
        float groupSpace = 0.06f;
        int groupCount = 3;

        ArrayList<BarEntry> valuesTotalComplaints = new ArrayList<>();
        ArrayList<BarEntry> valuesResolvedComplaints = new ArrayList<>();

        List<ModelComplaintList> modelResolveComplaintList = new ArrayList<>();

        for (int i = 0; i < modelComplaintList.size(); i++) {
            if (modelComplaintList.get(i).getComplainStatusId().equals("3")) {
                modelResolveComplaintList.add(modelComplaintList.get(i));
            }
        }

        List<String> listMonthName = new ArrayList<>();
        for (ModelComplaintList modelMonth : modelComplaintList) {
//            ModelComplaintRegistrationDate monthDetailModel = modelMonth.getComplainRegDate();
//            String monthName = monthDetailModel.getMonth();

       //     String monthName = "Monthhhhhh";


            String monthDetailModel = modelMonth.getComplainRegDateStr();
            Log.d("--monthDetailModel--"," "+ monthDetailModel);
            String[] mnt_list = monthDetailModel.split("-");

            String date = mnt_list[0];
            int month = Integer.parseInt(mnt_list[1]);
            String year = mnt_list[2];

            String monthString = new DateFormatSymbols().getMonths()[month-1];

            String monthName = monthString;

            if (listMonthName.size() == 0)
                listMonthName.add(monthName);
            else {
                boolean existFlag = false;
                for (int i = 0; i < listMonthName.size(); i++) {
                    if (monthName.equalsIgnoreCase(listMonthName.get(i))) {
                        existFlag = true;
                        break;
                    }
                }
                if (!existFlag)
                    listMonthName.add(monthName);
            }
        }


        List<ModelComplaintList> listFirstMonthTotal = new ArrayList<>();
        List<ModelComplaintList> listSecondMonthTotal = new ArrayList<>();
        List<ModelComplaintList> listCurrentMonthTotal = new ArrayList<>();

        for (ModelComplaintList model : modelComplaintList) {
       //     String monthName1 = model.getComplainRegDate().getMonth();
               String monthName = model.getComplainRegDateStr();



            Log.d("--monthDetailModel--"," "+ monthName);
            String[] mnt_list = monthName.split("-");

            String date = mnt_list[0];
            int month = Integer.parseInt(mnt_list[1]);
            String year = mnt_list[2];

            String monthString = new DateFormatSymbols().getMonths()[month-1];



         // String monthName1 = "Monthhhhhh";
          String monthName1 = monthString;



            if (listMonthName.size() > 0) {
                for (int j = 0; j < listMonthName.size(); j++) {
                    String monthName2 = listMonthName.get(j);
                    if (monthName1.equalsIgnoreCase(monthName2)){
                        switch (j){
                            case 0:
                                listFirstMonthTotal.add(model);
                                break;
                            case 1:
                                listSecondMonthTotal.add(model);
                                break;
                            case 2:
                                listCurrentMonthTotal.add(model);
                                break;
                        }
                    }
                }
            }
        }


        List<ModelComplaintList> listFirstMonthResolve = new ArrayList<>();
        List<ModelComplaintList> listSecondMonthResolve = new ArrayList<>();
        List<ModelComplaintList> listCurrentMonthResolve = new ArrayList<>();

        for (ModelComplaintList model : modelResolveComplaintList) {
       //     String monthName1 = model.getComplainRegDate().getMonth();
          //  String monthName1 = "Monthhhhhh";


            String monthName = model.getComplainRegDateStr();



            Log.d("--monthDetailModel--", " "+monthName);
            String[] mnt_list = monthName.split("-");

            String date = mnt_list[0];
            int month = Integer.parseInt(mnt_list[1]);
            String year = mnt_list[2];

            String monthString = new DateFormatSymbols().getMonths()[month-1];
            // String monthName1 = "Monthhhhhh";
            String monthName1 = monthString;

            if (listMonthName.size() > 0) {
                for (int j = 0; j < listMonthName.size(); j++) {
                    String monthName2 = listMonthName.get(j);
                    if (monthName1.equalsIgnoreCase(monthName2)){
                        switch (j){
                            case 0:
                                listFirstMonthResolve.add(model);
                                break;
                            case 1:
                                listSecondMonthResolve.add(model);
                                break;
                            case 2:
                                listCurrentMonthResolve.add(model);
                                break;
                        }
                    }
                }
            }
        }

        valuesTotalComplaints.add(new BarEntry(0, listFirstMonthTotal.size()));
        valuesResolvedComplaints.add(new BarEntry(0, listFirstMonthResolve.size()));

        valuesTotalComplaints.add(new BarEntry(1, listSecondMonthTotal.size()));
        valuesResolvedComplaints.add(new BarEntry(1, listSecondMonthResolve.size()));

        valuesTotalComplaints.add(new BarEntry(2, listCurrentMonthTotal.size()));
        valuesResolvedComplaints.add(new BarEntry(2, listCurrentMonthResolve.size()));

        XAxis xAxis = binding.barGraphTotalComplaints.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(false);
//        xAxis.setAxisMinimum(0f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter());

        BarDataSet set1, set2;
        if (binding.barGraphTotalComplaints.getData() != null &&
                binding.barGraphTotalComplaints.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) binding.barGraphTotalComplaints.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) binding.barGraphTotalComplaints.getData().getDataSetByIndex(1);
            set1.setValues(valuesTotalComplaints);
            set2.setValues(valuesResolvedComplaints);
            binding.barGraphTotalComplaints.getData().notifyDataChanged();
            binding.barGraphTotalComplaints.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(valuesTotalComplaints, getResources().getString(R.string.total));
            set1.setColors(MATERIAL_COLORS);
            set1.setDrawValues(true);

            set2 = new BarDataSet(valuesResolvedComplaints, getResources().getString(R.string.complaint_resolve));
            set2.setColors(MATERIAL_COLORS2);
            set2.setDrawValues(true);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(13f);
            data.getDataSetCount();
            binding.barGraphTotalComplaints.setData(data);
            binding.barGraphTotalComplaints.setFitBars(true);
        }

        binding.barGraphTotalComplaints.getXAxis().setAxisMinimum(0);

//        binding.barGraphTotalComplaints.getXAxis().setAxisMaximum(0
//                + binding.barGraphTotalComplaints.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);

        binding.barGraphTotalComplaints.getBarData().setBarWidth(0.40f);
        binding.barGraphTotalComplaints.groupBars(groupSpace, groupSpace, barSpace);
        binding.barGraphTotalComplaints.invalidate();

    }
    //end total complaints bar graph

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }
}