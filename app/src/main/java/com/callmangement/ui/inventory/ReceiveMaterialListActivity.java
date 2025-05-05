package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.ReceiveMaterialListActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityReceiveMaterialListBinding;
import com.callmangement.model.inventrory.ModelDispatchInvoice;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

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

public class ReceiveMaterialListActivity extends CustomActivity implements View.OnClickListener {
    private ActivityReceiveMaterialListBinding binding;
    private PrefManager prefManager;
    private Activity mActivity;
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final List<String> spinnerList = new ArrayList<>();
    private String filterType = "";
    private int checkFilter = 0;
    private final String myFormat = "yyyy-MM-dd";
    private String fromDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiveMaterialListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.receive_material));
        prefManager = new PrefManager(mContext);
        initView();
    }

    private void initView() {
        mActivity = this;
        setUpClickListener();
        setUpSpinnerDataList();
    }

    private void setUpClickListener(){
        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkFilter > 1) {
                    String item = adapterView.getItemAtPosition(i).toString();
                    if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        if (item.equalsIgnoreCase(getResources().getString(R.string.today))) {
                            filterType = getResources().getString(R.string.today);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            Date today = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String todayDate = sdf.format(today);
                            getPartsReciverInvoices(todayDate,todayDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            filterType = getResources().getString(R.string.yesterday);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            getPartsReciverInvoices(yesterdayDate,yesterdayDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                            filterType = getResources().getString(R.string.current_month);
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
                            getPartsReciverInvoices(date1,date2);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                            filterType = getResources().getString(R.string.previous_month);
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
                            getPartsReciverInvoices(date1,date2);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            filterType = getResources().getString(R.string.custom_filter);
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        getPartsReciverInvoices("","");
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.textFromDate.setOnClickListener(this);
        binding.textToDate.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void updateLabelFromDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()));
        Objects.requireNonNull(binding.textToDate.getText()).clear();
        fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
    }

    private void updateLabelToDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        String toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
        getPartsReciverInvoices(fromDate, toDate);
    }

    private void setUpSpinnerDataList(){
//        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
        filterType = getResources().getString(R.string.today);
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        spinnerList.add(getResources().getString(R.string.today));
        spinnerList.add(getResources().getString(R.string.yesterday));
        spinnerList.add(getResources().getString(R.string.current_month));
        spinnerList.add(getResources().getString(R.string.previous_month));
        spinnerList.add(getResources().getString(R.string.custom_filter));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilter.setAdapter(dataAdapter);
        binding.spinnerFilter.setSelection(1);
    }

    private void fetchDataByFilterType() {
        if (!filterType.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
            Objects.requireNonNull(binding.textFromDate.getText()).clear();
            Objects.requireNonNull(binding.textToDate.getText()).clear();
            if (filterType.equalsIgnoreCase(getResources().getString(R.string.today))) {
                filterType = getResources().getString(R.string.today);
                binding.layoutDateRange.setVisibility(View.GONE);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String todayDate = sdf.format(today);
                getPartsReciverInvoices(todayDate,todayDate);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                filterType = getResources().getString(R.string.yesterday);
                binding.layoutDateRange.setVisibility(View.GONE);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                Date yesterday = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String yesterdayDate = sdf.format(yesterday);
                getPartsReciverInvoices(yesterdayDate,yesterdayDate);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                filterType = getResources().getString(R.string.current_month);
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
                getPartsReciverInvoices(date1,date2);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                filterType = getResources().getString(R.string.previous_month);
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
                getPartsReciverInvoices(date1,date2);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                filterType = getResources().getString(R.string.custom_filter);
                binding.layoutDateRange.setVisibility(View.VISIBLE);
            }
        } else {
            filterType = "--" + getResources().getString(R.string.select_filter) + "--";
            Objects.requireNonNull(binding.textFromDate.getText()).clear();
            Objects.requireNonNull(binding.textToDate.getText()).clear();
            binding.layoutDateRange.setVisibility(View.GONE);
            getPartsReciverInvoices("","");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPartsReciverInvoices(String date1, String date2){
        if (Constants.isNetworkAvailable(mActivity)){
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDispatchInvoice> call = service.getPartsReciverInvoices("0", prefManager.getUseR_Id(),"0", date1, date2);
            call.enqueue(new Callback<ModelDispatchInvoice>() {
                @Override
                public void onResponse(@NonNull Call<ModelDispatchInvoice> call, @NonNull Response<ModelDispatchInvoice> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        ModelDispatchInvoice model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")) {
                            hideProgress();
                            if (model.getPartsDispatchInvoiceList().size() > 0){
                                binding.rvReceiveMaterial.setVisibility(View.VISIBLE);
                                binding.textNoRecordFound.setVisibility(View.GONE);
                                List<ModelPartsDispatchInvoiceList> partsDispatchInvoiceList = model.getPartsDispatchInvoiceList();
                                Collections.reverse(partsDispatchInvoiceList);
                                binding.rvReceiveMaterial.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                                binding.rvReceiveMaterial.setAdapter(new ReceiveMaterialListActivityAdapter(mContext, partsDispatchInvoiceList));
                            }else {
                                binding.rvReceiveMaterial.setVisibility(View.GONE);
                                binding.textNoRecordFound.setVisibility(View.VISIBLE);
                            }

                        }else {
                            binding.rvReceiveMaterial.setVisibility(View.GONE);
                            binding.textNoRecordFound.setVisibility(View.VISIBLE);
                        }

                    }else {
                        Toast.makeText(mActivity, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelDispatchInvoice> call, @NonNull Throwable t) {
                    hideProgress();
                    Toast.makeText(mActivity, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(mActivity, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        binding.spinnerFilter.setSelection(1);
//        Calendar calendar = Calendar.getInstance();
//        Date today = calendar.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        String todayDate = sdf.format(today);
//        getPartsReciverInvoices(todayDate,todayDate);
        fetchDataByFilterType();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        }else if (id == R.id.textFromDate){
            DatePickerDialog.OnDateSetListener dateFromDate = (viewFromData, year, monthOfYear, dayOfMonth) -> {
                myCalendarFromDate.set(Calendar.YEAR, year);
                myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFromDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        } else if (id == R.id.textToDate){
            DatePickerDialog.OnDateSetListener dateToDate = (viewToDate, year, monthOfYear, dayOfMonth) -> {
                myCalendarToDate.set(Calendar.YEAR, year);
                myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelToDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
    }
}