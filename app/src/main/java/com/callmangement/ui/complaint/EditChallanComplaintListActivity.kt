package com.callmangement.ui.complaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.R;
import com.callmangement.adapter.ChallanUploadListAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityEditChallanComplaintListBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EditChallanComplaintListActivity extends CustomActivity {
    private ActivityEditChallanComplaintListBinding binding;
    private Context mContext;
    private final int REQUEST_CODE = 1;
    private ChallanUploadListAdapter adapter;
    private PrefManager prefManager;
    private final List<ModelTehsilList> tehsilList = new ArrayList<>();
    private List<ModelTehsilList> tehsil_List_main = new ArrayList<>();
    private List<ModelDistrictList> districtList = new ArrayList<>();
    private int checkTehsil = 0;
    private int checkDistrict = 0;
    private String tehsilNameEng = "";
    private String districtNameEng = "";
    private List<ModelComplaintList> modelComplaintList = new ArrayList<>();
    private ComplaintViewModel viewModel;
    private String filterType = "";
    private final String complainStatusId = "";
    private String complainRegNo = "";
    private String districtId = "0";
    private final String myFormat = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_challan_complaint_list);

        mContext = EditChallanComplaintListActivity.this;

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.complaints));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);

        districtId = getIntent().getStringExtra("districtId");
        filterType = getIntent().getStringExtra("filter_type");
        tehsil_List_main = (List<ModelTehsilList>) getIntent().getSerializableExtra("tehsil_list");
        districtList = (List<ModelDistrictList>) getIntent().getSerializableExtra("district_list");
        prefManager = new PrefManager(mContext);
        binding.textNoComplaint.setVisibility(View.GONE);
        tehsilNameEng = "--" + getResources().getString(R.string.tehsil) + "--";
        districtNameEng = "--" + getResources().getString(R.string.district) + "--";


        initView();
        //    fetchDataByFilterType();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        //    tehsilList.addAll(tehsil_List_main);


//        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.GONE);
//            if (tehsilList.size() > 0) {
//                Collections.reverse(tehsilList);
//                ModelTehsilList l = new ModelTehsilList();
//                l.setTehsilId(String.valueOf(-1));
//                l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
//                tehsilList.add(l);
//                Collections.reverse(tehsilList);
//
//                ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, tehsilList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerTehsil.setAdapter(dataAdapter);
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")) { // for dso
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.GONE);
//            if (tehsilList.size() != 0) {
//                Collections.reverse(tehsilList);
//                ModelTehsilList l = new ModelTehsilList();
//                l.setTehsilId(String.valueOf(-1));
//                l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
//                tehsilList.add(l);
//                Collections.reverse(tehsilList);
//
//                ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, tehsilList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerTehsil.setAdapter(dataAdapter);
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) { // for manager
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.VISIBLE);
//            updateTehsilByDistrictId(districtId);
//            if (districtList != null && districtList.size() > 0) {
//                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerDistrict.setAdapter(dataAdapter);
//
//                if (!districtId.equalsIgnoreCase("0")) {
//                    for (int i = 0; i < districtList.size(); i++) {
//                        if (districtList.get(i).getDistrictId().equals(districtId)) {
//                            binding.spinnerDistrict.setSelection(i);
//                        }
//                    }
//                }
//
//            }
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("5") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceCentre")) { // for service center
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.VISIBLE);
//            updateTehsilByDistrictId(districtId);
//            if (districtList != null && districtList.size() > 0) {
//                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerDistrict.setAdapter(dataAdapter);
//
//                if (!districtId.equalsIgnoreCase("0")) {
//                    for (int i = 0; i < districtList.size(); i++) {
//                        if (districtList.get(i).getDistrictId().equals(districtId)) {
//                            binding.spinnerDistrict.setSelection(i);
//                        }
//                    }
//                }
//
//            }
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) { // for Admin
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.VISIBLE);
//            updateTehsilByDistrictId(districtId);
//            if (districtList != null && districtList.size() > 0) {
//                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerDistrict.setAdapter(dataAdapter);
//
//                if (!districtId.equalsIgnoreCase("0")) {
//                    for (int i = 0; i < districtList.size(); i++) {
//                        if (districtList.get(i).getDistrictId().equals(districtId)) {
//                            binding.spinnerDistrict.setSelection(i);
//                        }
//                    }
//                }
//            }
//        }

        adapter = new ChallanUploadListAdapter(mContext);
        adapter.notifyDataSetChanged();
        binding.rvComplaintPending.setHasFixedSize(true);
        binding.rvComplaintPending.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvComplaintPending.setAdapter(adapter);

//        binding.spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (++checkTehsil > 1) {
//                    tehsilNameEng = tehsilList.get(i).getTehsilNameEng();
//                    setDataIntoAdapterByTehsil(tehsilNameEng);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                checkTehsil = 0;
//                if (++checkDistrict > 1) {
//                    districtNameEng = districtList.get(i).getDistrictNameEng();
//                    districtId = districtList.get(i).getDistrictId();
//                    fetchDataByFilterType();
//                    updateTehsilByDistrict(districtNameEng);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        binding.inputSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                adapter.getFilter().filter(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                complainRegNo = Objects.requireNonNull(binding.inputSearch.getText()).toString().trim();

                if (complainRegNo.isEmpty()) {
                    makeToast(getResources().getString(R.string.please_input_complaint_number));
                } else {
                    fetchComplaintList();
                }
            }
        });

    }

//    private void fetchDataByFilterType() {
//        if (!filterType.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
//            if (filterType.equalsIgnoreCase(getResources().getString(R.string.today))) {
//                filterType = getResources().getString(R.string.today);
//                Calendar calendar = Calendar.getInstance();
//                Date today = calendar.getTime();
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                String todayDate = sdf.format(today);
//                fetchComplaintListBySelect(todayDate);
//
//            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
//                filterType = getResources().getString(R.string.yesterday);
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.DAY_OF_YEAR, -1);
//                Date yesterday = calendar.getTime();
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                String yesterdayDate = sdf.format(yesterday);
//                fetchComplaintListBySelect(yesterdayDate);
//
//            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
//                filterType = getResources().getString(R.string.current_month);
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
//                calendar.set(Calendar.HOUR_OF_DAY, 0);
//                calendar.set(Calendar.MINUTE, 0);
//                calendar.set(Calendar.SECOND, 0);
//                calendar.set(Calendar.MILLISECOND, 0);
//                Date firstDateOfCurrentMonth = calendar.getTime();
//                String date1 = sdf.format(firstDateOfCurrentMonth);
//
//                Calendar calendar1 = Calendar.getInstance();
//                Date currentDateOfCurrentMonth = calendar1.getTime();
//                String date2 = sdf.format(currentDateOfCurrentMonth);
//
//                fetchComplaintListByCurrentMonthAndPreviousMonth(date1, date2);
//
//            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
//                filterType = getResources().getString(R.string.previous_month);
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                Calendar aCalendar = Calendar.getInstance();
//                aCalendar.add(Calendar.MONTH, -1);
//                aCalendar.set(Calendar.DATE, 1);
//                Date firstDateOfPreviousMonth = aCalendar.getTime();
//                String date1 = sdf.format(firstDateOfPreviousMonth);
//
//                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//                Date lastDateOfPreviousMonth = aCalendar.getTime();
//                String date2 = sdf.format(lastDateOfPreviousMonth);
//
//                fetchComplaintListByCurrentMonthAndPreviousMonth(date1, date2);
//
//
//            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
//                filterType = getResources().getString(R.string.custom_filter);
//                fetchComplaintListByDateRange(prefManager.getFROM_DATE(), prefManager.getTO_DATE());
//            }
//        } else {
//            filterType = "--" + getResources().getString(R.string.select_filter) + "--";
//            fetchComplaintList();
//        }
//    }

    private void fetchComplaintList() {
        modelComplaintList.clear();
        isLoading();
        viewModel.getModelEditChallanComplaintMutableLiveData(String.valueOf(prefManager.getUSER_Id()), districtId, complainRegNo, "").observe(this, modelComplaint -> {
            isLoading();
            if (modelComplaint.status.equals("200")) {
                modelComplaintList = modelComplaint.getComplaint_List();
                setDataIntoAdapter(modelComplaintList);
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

    @SuppressLint("SetTextI18n")
    private void setDataIntoAdapter(List<ModelComplaintList> modelComplaintList) {
        if (modelComplaintList != null && modelComplaintList.size() > 0) {
            binding.textTotalComplaint.setText("" + modelComplaintList.size());
            binding.rvComplaintPending.setVisibility(View.VISIBLE);
            binding.textNoComplaint.setVisibility(View.GONE);
            adapter.setData(modelComplaintList);
        } else {
            binding.textTotalComplaint.setText("0");
            binding.rvComplaintPending.setVisibility(View.GONE);
            binding.textNoComplaint.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDataIntoAdapterByTehsil(String tehsilNameEng) {
        List<ModelComplaintList> complaintList = new ArrayList<>();
        if (modelComplaintList != null && modelComplaintList.size() > 0) {
            for (int i = 0; i < modelComplaintList.size(); i++) {
                if (tehsilNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.tehsil) + "--")) {
                    if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                        complaintList.add(modelComplaintList.get(i));
                    } else {
                        if (modelComplaintList.get(i).district != null) {
                            if (modelComplaintList.get(i).district.equalsIgnoreCase(districtNameEng)) {
                                complaintList.add(modelComplaintList.get(i));
                            }
                        }
                    }

                } else {
                    if (modelComplaintList.get(i).tehsil != null) {
                        if (modelComplaintList.get(i).tehsil.equalsIgnoreCase(tehsilNameEng)) {
                            complaintList.add(modelComplaintList.get(i));
                        }
                    }
                }
            }
        }

        if (complaintList.size() > 0) {
            binding.textTotalComplaint.setText("" + complaintList.size());
            binding.rvComplaintPending.setVisibility(View.VISIBLE);
            binding.textNoComplaint.setVisibility(View.GONE);
            adapter.setData(complaintList);
        } else {
            binding.textTotalComplaint.setText("0");
            binding.rvComplaintPending.setVisibility(View.GONE);
            binding.textNoComplaint.setVisibility(View.VISIBLE);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setDataIntoAdapterByDistrict(String districtNameEng) {
        List<ModelComplaintList> complaintList = new ArrayList<>();
        if (modelComplaintList != null && modelComplaintList.size() > 0) {
            for (int i = 0; i < modelComplaintList.size(); i++) {
                if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                    complaintList.add(modelComplaintList.get(i));
                } else {
                    if (modelComplaintList.get(i).district != null) {
                        if (modelComplaintList.get(i).district.equalsIgnoreCase(districtNameEng)) {
                            complaintList.add(modelComplaintList.get(i));
                        }
                    }
                }
            }
        }

        if (complaintList.size() > 0) {
            binding.textTotalComplaint.setText("" + complaintList.size());
            binding.rvComplaintPending.setVisibility(View.VISIBLE);
            binding.textNoComplaint.setVisibility(View.GONE);
            adapter.setData(complaintList);
        } else {
            binding.textTotalComplaint.setText("0");
            binding.rvComplaintPending.setVisibility(View.GONE);
            binding.textNoComplaint.setVisibility(View.VISIBLE);
        }
    }

    private void updateTehsilByDistrict(String district) {
        tehsilList.clear();
        if (tehsil_List_main != null && tehsil_List_main.size() > 0) {
            for (int i = 0; i < tehsil_List_main.size(); i++) {
                if (district.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                    tehsilList.add(tehsil_List_main.get(i));
                } else {
                    if (tehsil_List_main.get(i).districtNameEng != null) {
                        if (tehsil_List_main.get(i).districtNameEng.equalsIgnoreCase(district)) {
                            tehsilList.add(tehsil_List_main.get(i));
                        }
                    }
                }
            }
        }
        if (tehsilList.size() > 0) {
            Collections.reverse(tehsilList);
            ModelTehsilList l = new ModelTehsilList();
            l.setTehsilId(String.valueOf(-1));
            l.tehsilNameEng = "--" + getResources().getString(R.string.tehsil) + "--";
            tehsilList.add(l);
            Collections.reverse(tehsilList);

            ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, tehsilList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerTehsil.setAdapter(dataAdapter);
        }
    }

    private void updateTehsilByDistrictId(String districtId) {
        tehsilList.clear();
        if (tehsil_List_main != null && tehsil_List_main.size() > 0) {
            for (int i = 0; i < tehsil_List_main.size(); i++) {
                if (districtId.equalsIgnoreCase("0")) {
                    tehsilList.add(tehsil_List_main.get(i));
                } else {
                    if (tehsil_List_main.get(i).fk_DistrictId != null) {
                        if (tehsil_List_main.get(i).fk_DistrictId.equalsIgnoreCase(districtId)) {
                            tehsilList.add(tehsil_List_main.get(i));
                        }
                    }
                }
            }
        }

        if (tehsilList.size() > 0) {
            Collections.reverse(tehsilList);
            ModelTehsilList l = new ModelTehsilList();
            l.setTehsilId(String.valueOf(-1));
            l.tehsilNameEng = "--" + getResources().getString(R.string.tehsil) + "--";
            tehsilList.add(l);
            Collections.reverse(tehsilList);

            ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, tehsilList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerTehsil.setAdapter(dataAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //        fetchDataByFilterType();
                finish();
            }
        }
    }


}
