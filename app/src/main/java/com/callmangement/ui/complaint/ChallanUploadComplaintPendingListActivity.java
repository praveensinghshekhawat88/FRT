package com.callmangement.ui.complaint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.ChallanUploadListAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityChallanUploadComplaintPendingListBinding;
import com.callmangement.databinding.ActivityComplaintPendingListBinding;
import com.callmangement.model.complaints.ModelChallanUploadComplaint;
import com.callmangement.model.complaints.ModelComplaint;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.utils.PrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallanUploadComplaintPendingListActivity extends CustomActivity {
    private ActivityChallanUploadComplaintPendingListBinding binding;
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
    private String districtId = "0";
    private final String myFormat = "yyyy-MM-dd";
    private int currentPage = 1;
    private String isPagination = "1";
    private String pageSize = "200";
    private boolean isLoading = false;
    private boolean isLastPage = false;
    int TotalItems;
    String fpscodee;
    private int checkFilter = 0;
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private String fromDate = "";
    private String toDate = "";
    private final List<String> spinnerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_challan_upload_complaint_pending_list);

        mContext = ChallanUploadComplaintPendingListActivity.this;

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
        fetchDataByMainType();
        DateRangeSpinner();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        tehsilList.addAll(tehsil_List_main);

        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
            binding.rlTehsil.setVisibility(View.VISIBLE);
            binding.rlDistrict.setVisibility(View.GONE);
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

        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")) { // for dso
            binding.rlTehsil.setVisibility(View.VISIBLE);
            binding.rlDistrict.setVisibility(View.GONE);
            if (tehsilList.size() != 0) {
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

        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) { // for manager
            binding.rlTehsil.setVisibility(View.VISIBLE);
            binding.rlDistrict.setVisibility(View.VISIBLE);
            updateTehsilByDistrictId(districtId);
            if (districtList != null && districtList.size() > 0) {
                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerDistrict.setAdapter(dataAdapter);

                if (!districtId.equalsIgnoreCase("0")) {
                    for (int i = 0; i < districtList.size(); i++) {
                        if (districtList.get(i).getDistrictId().equals(districtId)) {
                            binding.spinnerDistrict.setSelection(i);
                        }
                    }
                }

            }
        } else if (prefManager.getUSER_TYPE_ID().equals("5") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceCentre")) { // for service center
            binding.rlTehsil.setVisibility(View.VISIBLE);
            binding.rlDistrict.setVisibility(View.VISIBLE);
            updateTehsilByDistrictId(districtId);
            if (districtList != null && districtList.size() > 0) {
                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerDistrict.setAdapter(dataAdapter);

                if (!districtId.equalsIgnoreCase("0")) {
                    for (int i = 0; i < districtList.size(); i++) {
                        if (districtList.get(i).getDistrictId().equals(districtId)) {
                            binding.spinnerDistrict.setSelection(i);
                        }
                    }
                }

            }
        } else if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) { // for Admin
            binding.rlTehsil.setVisibility(View.VISIBLE);
            binding.rlDistrict.setVisibility(View.VISIBLE);
            updateTehsilByDistrictId(districtId);
            if (districtList != null && districtList.size() > 0) {
                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerDistrict.setAdapter(dataAdapter);

                if (!districtId.equalsIgnoreCase("0")) {
                    for (int i = 0; i < districtList.size(); i++) {
                        if (districtList.get(i).getDistrictId().equals(districtId)) {
                            binding.spinnerDistrict.setSelection(i);
                        }
                    }
                }
            }
        }

        adapter = new ChallanUploadListAdapter(mContext);
        adapter.notifyDataSetChanged();
        binding.rvComplaintPending.setHasFixedSize(true);
        binding.rvComplaintPending.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvComplaintPending.setAdapter(adapter);


        binding.rvComplaintPending.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoading = true;
                    currentPage++;
                    new Handler(Looper.getMainLooper()).post(() -> fetchComplaintList());
                }
            }
        });











        binding.spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkTehsil > 1) {
                    tehsilNameEng = tehsilList.get(i).tehsilNameEng;
                    setDataIntoAdapterByTehsil(tehsilNameEng);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkTehsil = 0;
                if (++checkDistrict > 1) {
                    districtNameEng = districtList.get(i).districtNameEng;
                    districtId = districtList.get(i).getDistrictId();
                    //  fetchDataByFilterType();
                    //  updateTehsilByDistrict(districtNameEng);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

      /*  binding.inputSearch.addTextChangedListener(new TextWatcher() {
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
        });*/


        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());

        binding.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrateDevice(view.getContext());


                currentPage = 1;
                fpscodee = binding.inputFPS.getText().toString();
           fetchComplaintList();


            }
        });

    }




    private void isLoadingprogress() {
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
            //  binding.textTotalComplaint.setText("" + modelComplaintList.size());
            binding.textTotalComplaint.setText("" + TotalItems);
            binding.rvComplaintPending.setVisibility(View.VISIBLE);
            binding.textNoComplaint.setVisibility(View.GONE);
            adapter.setData(modelComplaintList);
        } else {
            binding.textTotalComplaint.setText("0");
            binding.rvComplaintPending.setVisibility(View.GONE);
            binding.textNoComplaint.setVisibility(View.VISIBLE);

        }
        isLoading = false;

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

                fetchComplaintList();

            }
        }
    }



    private void updateLabelFromDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()));
        Objects.requireNonNull(binding.textToDate.getText()).clear();
        fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
    }

    private void updateLabelToDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
    }

    private void vibrateDevice(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }
        }
    }







    private void fetchDataByMainType() {
        if (!filterType.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
            if (filterType.equalsIgnoreCase(getResources().getString(R.string.today))) {
                filterType = getResources().getString(R.string.today);
                binding.spinner.setSelection(1);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String todayDate = sdf.format(today);

                fromDate = todayDate;
                toDate = todayDate;

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                filterType = getResources().getString(R.string.yesterday);
                binding.spinner.setSelection(2);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                Date yesterday = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String yesterdayDate = sdf.format(yesterday);
                fromDate = yesterdayDate;
                toDate = yesterdayDate;


            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                filterType = getResources().getString(R.string.current_month);
                binding.spinner.setSelection(3);

                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date firstDateOfCurrentMonth = calendar.getTime();
                String date1 = sdf.format(firstDateOfCurrentMonth);

                Calendar calendar1 = Calendar.getInstance();
                Date currentDateOfCurrentMonth = calendar1.getTime();
                String date2 = sdf.format(currentDateOfCurrentMonth);

                fromDate = date1;
                toDate = date2;



            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                binding.spinner.setSelection(4);

                filterType = getResources().getString(R.string.previous_month);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.MONTH, -1);
                aCalendar.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = aCalendar.getTime();
                String date1 = sdf.format(firstDateOfPreviousMonth);

                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date lastDateOfPreviousMonth = aCalendar.getTime();
                String date2 = sdf.format(lastDateOfPreviousMonth);
                fromDate = date1;
                toDate = date2;




            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                filterType = getResources().getString(R.string.custom_filter);
                binding.spinner.setSelection(5);


                fromDate = prefManager.getFROM_DATE();
                toDate = prefManager.getTO_DATE();
                binding.layoutDateRange.setVisibility(View.VISIBLE);
                binding.textFromDate.setText(fromDate);
                binding.textToDate.setText(toDate);

                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                try {
                    myCalendarFromDate.setTime(sdf.parse(fromDate));
                    myCalendarToDate.setTime(sdf.parse(toDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            fetchComplaintList();








        } else {
            filterType = "--" + getResources().getString(R.string.select_filter) + "--";
            fromDate = "";
            toDate = "";
            fetchComplaintList();
        }
    }











    private void DateRangeSpinner() {
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        spinnerList.add(getResources().getString(R.string.today));
        spinnerList.add(getResources().getString(R.string.yesterday));
        spinnerList.add(getResources().getString(R.string.current_month));
        spinnerList.add(getResources().getString(R.string.previous_month));
        spinnerList.add(getResources().getString(R.string.custom_filter));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);
        // binding.spinner.setSelection(Integer.parseInt(value_selectedDay));

        //  binding.spinner.setSelection(1);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkFilter > 1) {
                    String item = adapterView.getItemAtPosition(i).toString();

                    if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        if (item.equalsIgnoreCase(getResources().getString(R.string.today))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            Date today = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String todayDate = sdf.format(today);
                            fromDate = todayDate;
                            toDate = todayDate;


                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fromDate = yesterdayDate;
                            toDate = yesterdayDate;


                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date firstDateOfCurrentMonth = calendar.getTime();
                            String date1 = sdf.format(firstDateOfCurrentMonth);

                            Calendar calendar1 = Calendar.getInstance();
                            Date currentDateOfCurrentMonth = calendar1.getTime();
                            String date2 = sdf.format(currentDateOfCurrentMonth);
                            fromDate = date1;
                            toDate = date2;


                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            Calendar aCalendar = Calendar.getInstance();
                            aCalendar.add(Calendar.MONTH, -1);
                            aCalendar.set(Calendar.DATE, 1);
                            Date firstDateOfPreviousMonth = aCalendar.getTime();
                            String date1 = sdf.format(firstDateOfPreviousMonth);
                            aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            Date lastDateOfPreviousMonth = aCalendar.getTime();
                            String date2 = sdf.format(lastDateOfPreviousMonth);
                            fromDate = date1;
                            toDate = date2;


                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            binding.layoutDateRange.setVisibility(View.VISIBLE);

                        }
                    } else {
                        fromDate = "";
                        toDate = "";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

                        // fetchComplaintList();


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

       binding.textFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateFromDate = (view1, year, monthOfYear, dayOfMonth) -> {
                    myCalendarFromDate.set(Calendar.YEAR, year);
                    myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                    myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabelFromDate();
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                        .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                        myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                long minDateInMillis = myCalendarToDate.getTimeInMillis();
                datePickerDialog.getDatePicker().setMaxDate(minDateInMillis);
                datePickerDialog.show();

            }
        });


        binding.textToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateToDate = (view1, year, monthOfYear, dayOfMonth) -> {
                    myCalendarToDate.set(Calendar.YEAR, year);
                    myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                    myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabelToDate();
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                        .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                        myCalendarToDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                long minDateInMillis = myCalendarFromDate.getTimeInMillis();
                datePickerDialog.getDatePicker().setMinDate(minDateInMillis);
                datePickerDialog.show();
            }
        });

    }

    private void fetchComplaintList() {
        showProgress(getResources().getString(R.string.please_wait));

        Log.d("Parameter--", "UserId: " + prefManager.getUSER_Id() + " DistrictId: " + districtId + " StatusId: " + complainStatusId
                + " FPS: " + fpscodee + " From: " + fromDate + " To: " + toDate + " Pagination: " + isPagination
                + " Page: " + currentPage + " PageSize: " + pageSize);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);

        Call<ModelChallanUploadComplaint> call = service.getModelChallanUploadComplaintListData(prefManager.getUSER_Id(), districtId, complainStatusId, fpscodee, fromDate, toDate,isPagination,String.valueOf(currentPage),pageSize);
        Log.d("--response---", "  " + prefManager.getUSER_Id() + "  " + districtId + "  " + complainStatusId + "  " + fromDate + toDate);
        call.enqueue(new Callback<ModelChallanUploadComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelChallanUploadComplaint> call, @NonNull Response<ModelChallanUploadComplaint> response) {
                hideProgress();
                if (response.isSuccessful()) {
                    ModelChallanUploadComplaint model = response.body();
                    if (model != null && model.status.equals("200")) {
                        int totalPage = model.totalPages;
                        int CurrentPage = model.currentPage;
                        TotalItems = model.totalItems;

                        Log.d("CheckNow--", totalPage + " " + CurrentPage + " " + TotalItems);

                        if (currentPage == 1) {
                            // Fresh search: Clear and set new data
                            modelComplaintList.clear();
                            modelComplaintList.addAll(model.getComplaint_List());
                            setDataIntoAdapter(modelComplaintList);
                        } else {
                            // Scrolling: Add more data
                            adapter.addData(model.getComplaint_List());
                        }

                        if (currentPage == totalPage) {
                            isLastPage = true;
                        }

                        isLoading = false;
                    } else {
                        modelComplaintList.clear();
                        setDataIntoAdapter(modelComplaintList);
                        isLoading = false;
                        Toast.makeText(mContext, model != null ? model.message : "No data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hideProgress();
                    isLoading = false;
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelChallanUploadComplaint> call, @NonNull Throwable t) {
                hideProgress();
                isLoading = false;
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                Log.d("error----", "is" + t.getMessage());
            }
        });
    }


}