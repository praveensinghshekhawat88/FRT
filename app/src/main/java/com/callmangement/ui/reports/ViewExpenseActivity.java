package com.callmangement.ui.reports;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.ViewExpenseActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityViewExpenseBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.expense.ModelExpenseStatus;
import com.callmangement.model.expense.ModelExpensesList;
import com.callmangement.model.expense.ModelExpesne;
import com.callmangement.ui.complaint.ComplaintViewModel;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewExpenseActivity extends CustomActivity implements View.OnClickListener {
    private ActivityViewExpenseBinding binding;
    private PrefManager prefManager;
    private String expenseStatusId = "0";
    //    private List<ModelExpensesList> modelExpensesList = new ArrayList<>();
    private final List<ModelExpenseStatus> modelExpenseStatusList = new ArrayList<>();
    private ComplaintViewModel viewModel;
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final List<String> spinnerList = new ArrayList<>();
    private final String myFormat = "yyyy-MM-dd";
    private int checkFilter = 0;
    private int checkExpenseStatus = 0;
    private int checkDistrict = 0;
    private String districtId = "0";
    private String fromDate = "";
    private String toDate = "";
    private List<ModelDistrictList> district_List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.se_expense_report));
        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.se_expense_report));
        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.expense_report));
        }
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        setUpOnclickListener();
        setUpLayout();
        districtList();
        setUpDateRangeSpinner();
        setUpExpenseStatusSpinner();
//        getExpenseList(expenseStatusId,districtId,fromDate,toDate);
    }

    private void setUpOnclickListener() {
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

                            getExpenseList(expenseStatusId, districtId, fromDate, toDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fromDate = yesterdayDate;
                            toDate = yesterdayDate;
                            getExpenseList(expenseStatusId, districtId, fromDate, toDate);

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

                            getExpenseList(expenseStatusId, districtId, fromDate, toDate);

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

                            getExpenseList(expenseStatusId, districtId, fromDate, toDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        fromDate = "";
                        toDate = "";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        getExpenseList(expenseStatusId, districtId, fromDate, toDate);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkDistrict > 1) {
                    districtId = district_List.get(i).getDistrictId();
                    getExpenseList(expenseStatusId, districtId, fromDate, toDate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerExpenseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkExpenseStatus > 1) {
                    expenseStatusId = modelExpenseStatusList.get(i).getId();
                    getExpenseList(expenseStatusId, districtId, fromDate, toDate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.actionBar.ivBack.setOnClickListener(this);
        binding.textFromDate.setOnClickListener(this);
        binding.textToDate.setOnClickListener(this);
    }

    private void setUpLayout() {
        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.GONE);
            districtId = prefManager.getUSER_DistrictId();
        }
    }

    private void setUpDateRangeSpinner() {
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        spinnerList.add(getResources().getString(R.string.today));
        spinnerList.add(getResources().getString(R.string.yesterday));
        spinnerList.add(getResources().getString(R.string.current_month));
        spinnerList.add(getResources().getString(R.string.previous_month));
        spinnerList.add(getResources().getString(R.string.custom_filter));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);
    }

    private void setUpExpenseStatusSpinner() {
        ModelExpenseStatus model1 = new ModelExpenseStatus();
        model1.setId("0");
        model1.setExpense_status("--" + getResources().getString(R.string.expense_status) + "--");
        modelExpenseStatusList.add(model1);

        ModelExpenseStatus model2 = new ModelExpenseStatus();
        model2.setId("1");
        model2.setExpense_status(getResources().getString(R.string.pending));
        modelExpenseStatusList.add(model2);

        ModelExpenseStatus model3 = new ModelExpenseStatus();
        model3.setId("2");
        model3.setExpense_status(getResources().getString(R.string.completed));
        modelExpenseStatusList.add(model3);

        ArrayAdapter<ModelExpenseStatus> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, modelExpenseStatusList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerExpenseStatus.setAdapter(dataAdapter);
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
        getExpenseList(expenseStatusId, districtId, fromDate, toDate);
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

    private void getExpenseList(String expenseStatusId, String districtId, String fromDate, String toDate) {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.getExpensesList(prefManager.getUSER_Id(), expenseStatusId, districtId, fromDate, toDate);
            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    String responseStr = response.body().string();
                                    ModelExpesne modelResponse = (ModelExpesne) getObject(responseStr, ModelExpesne.class);
                                    if (modelResponse != null) {
                                        if (modelResponse.status.equals("200")) {
                                            if (modelResponse.getExpensesList().size() > 0) {
                                                binding.rvExpenses.setVisibility(View.VISIBLE);
                                                binding.textNoDataFound.setVisibility(View.GONE);
                                                List<ModelExpensesList> modelExpensesList = modelResponse.getExpensesList();
                                                setUpAdapter(modelExpensesList);
                                            } else {
                                                binding.rvExpenses.setVisibility(View.GONE);
                                                binding.textNoDataFound.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            binding.rvExpenses.setVisibility(View.GONE);
                                            binding.textNoDataFound.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        binding.rvExpenses.setVisibility(View.GONE);
                                        binding.textNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    binding.rvExpenses.setVisibility(View.GONE);
                                    binding.textNoDataFound.setVisibility(View.VISIBLE);
                                }
                            } else {
                                binding.rvExpenses.setVisibility(View.GONE);
                                binding.textNoDataFound.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.rvExpenses.setVisibility(View.GONE);
                            binding.textNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                        binding.rvExpenses.setVisibility(View.GONE);
                        binding.textNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                    binding.rvExpenses.setVisibility(View.GONE);
                    binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpAdapter(List<ModelExpensesList> modelExpensesList) {
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvExpenses.setAdapter(new ViewExpenseActivityAdapter(mContext, modelExpensesList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getExpenseList(expenseStatusId, districtId, fromDate, toDate);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.textFromDate) {
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
            datePickerDialog.show();

        } else if (id == R.id.textToDate) {
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
            datePickerDialog.show();

        }
    }

}