package com.callmangement.ui.errors.activity;

import static android.R.layout.simple_spinner_item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.SLAReportsDetailsActivityAdapter;
import com.callmangement.adapter.TotalComplaintListActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityErrorPosdistBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.expense.ModelExpenseStatus;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.ui.complaint.TotalComplaintListActivity;
import com.callmangement.ui.distributor.model.PosDistributionDetail;
import com.callmangement.ui.errors.adapter.ErrorPosAdapter;
import com.callmangement.ui.errors.model.GetErrorTypesRoot;
import com.callmangement.ui.errors.model.GetErrortypesDatum;
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum;
import com.callmangement.ui.errors.model.GetPosDeviceErrorsRoot;
import com.callmangement.ui.home.MainActivity;
import com.callmangement.ui.reports.SLAReportsDetailsActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.EqualSpacingItemDecoration;
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

public class ErrorPosActivity extends CustomActivity implements View.OnClickListener {
    private ActivityErrorPosdistBinding binding;
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
    private final String districtIdd = "0";
    private String fromDate = "";
    private String toDate = "";
    String itt;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    ErrorPosAdapter adapter;
    ArrayList<GetPosDeviceErrorDatum>  getPosDeviceErrorDatumArrayList ;
    String fpscodee;
    String ErrorTypeId;

    private final ArrayList<String> playerNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityErrorPosdistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);



        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_distribution_errors));
            binding.tvAdd.setVisibility(View.GONE);




        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_distribution_errors));
            binding.tvAdd.setVisibility(View.GONE);




        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_distribution_errors));
            binding.tvAdd.setVisibility(View.VISIBLE);
           // Log.d("thfghfh"," "+districtIdd);
            //   districtId = String.valueOf(district_List.get(Integer.parseInt(districtIdd)));
          //  binding.spinnerDistrict.setSelection(Integer.parseInt(districtId));


        }
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        fetchSpinnersata();
        setUpOnclickListener();
        setUpLayout();
        districtList();
        setUpDateRangeSpinner();
        setUpExpenseStatusSpinner();
        setupfilter();
//      getExpenseList(expenseStatusId,districtId,fromDate,toDate);


        binding.btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String fps = binding.inputSearch.getText().toString();
                fpscodee = binding.inputSearch.getText().toString();
                getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
            }
        });



   //     adapter = new ErrorPosAdapter(mContext, getPosDeviceErrorDatumArrayList,prefManager.getUSER_TYPE_ID());
   //     adapter.notifyDataSetChanged();
    //    binding.rvExpenses.setHasFixedSize(true);
    //    binding.rvExpenses.setLayoutManager(new LinearLayoutManager(ErrorPosActivity.this, LinearLayoutManager.VERTICAL, false));
     //   binding.rvExpenses.setAdapter(adapter);

    }




    private void fetchSpinnersata()
    {
        if (Constants.isNetworkAvailable(mContext)){
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<GetErrorTypesRoot> call = service.GetErrorTypes(prefManager.getUSER_Id(), "0");
            call.enqueue(new Callback<GetErrorTypesRoot>() {
                @Override
                public void onResponse(@NonNull Call<GetErrorTypesRoot> call, @NonNull Response<GetErrorTypesRoot> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        if (response.code() == 200){
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    GetErrorTypesRoot getErrorTypesRoot = response.body();
                                 //   Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getErrorTypesRoot);
                                    GetErrortypesDatum  selecttype = new GetErrortypesDatum("---SelectErrorType----",0);
                                    ArrayList<GetErrortypesDatum>  getErrortypesDatum =
                                            getErrorTypesRoot.getData();

                                    ArrayList<GetErrortypesDatum>  getErrortypesDatum1 =
                                           new ArrayList<GetErrortypesDatum>();
                                    getErrortypesDatum1.add(selecttype);

                                  //  playerNames.add("--" + getResources().getString(R.string.select_errortype) + "--");


                                    playerNames.add(selecttype.getErrorType());

                                 //   playerNames.add("--" + getResources().getString(R.string.select_errortype) + "--");


                                 for (int i = 0; i < getErrortypesDatum.size(); i++){
                                        playerNames.add(getErrortypesDatum.get(i).getErrorType());
                                     getErrortypesDatum1.add(getErrortypesDatum.get(i));
                                    }


                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorPosActivity.this, simple_spinner_item, playerNames);
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    binding.spinnererrortype.setAdapter(spinnerArrayAdapter);
                                    binding.spinnererrortype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                            ErrorTypeId = String.valueOf(getErrortypesDatum1.get(i).getErrorTypeId());
                                          //  Log.d("modelExpensestatuslist", " " + ErrorTypeId);

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                    String msg =  getErrorTypesRoot.getMessage();
                                    String status =  getErrorTypesRoot.getStatus();

                                  /*  ArrayList<GetErrortypesDatum>  getErrortypesDatum =
                                            getErrorTypesRoot.getData();
                                    String msg =  getErrorTypesRoot.getMessage();
                                    String status =  getErrorTypesRoot.getStatus();

                                     JSONArray jsonArray = getErrortypesDatum
                                    String CreatedCampCount = getErrortypesDatum.get(Ar).;
                                    String OrganizedCampCount = dashboardUserDetails.getOrganizedCampCount();
                                    String AttendanceCount = dashboardUserDetails.getAttendanceCount();
                                    binding.textcreatedCampCount.setText(CreatedCampCount);
                                    binding.textCampsCount.setText(OrganizedCampCount);
                                    binding.textAttendanceCount.setText(AttendanceCount);*/
                                    //   Log.d("ghmn", "asmbnmldjfjas" + CreatedCampCount + "  " + OrganizedCampCount + " " + AttendanceCount);
                                } else {
                                    makeToast(String.valueOf(response.body().getMessage()));
                                }
                            } else {
                                Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();

                            }
                        }else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<GetErrorTypesRoot> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpOnclickListener(){
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

                       //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fromDate = yesterdayDate;
                            toDate = yesterdayDate;
                         //   getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

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

                      //      getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

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

                     //       getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

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


                    //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



       /* binding.inputFpsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){

                     List<PosDistributionDetail> filterList = new ArrayList<>();
                    if (userReportList.size() > 0){
                        for (PosDistributionDetail model : userReportList){
                            if (model.getFpscode().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                    setAdapter(filterList);
                } else setAdapter(userReportList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/







        binding.spinnerExpenseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkExpenseStatus > 1) {
                    expenseStatusId = modelExpenseStatusList.get(i).getId();
                    Log.d("modelExpensestatuslist"," "+expenseStatusId);
               //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.actionBar.ivBack.setOnClickListener(this);
        binding.textFromDate.setOnClickListener(this);
        binding.textToDate.setOnClickListener(this);
        binding.tvAdd.setOnClickListener(this);
    }



    private void setupfilter() {

        adapter = new ErrorPosAdapter(mContext, getPosDeviceErrorDatumArrayList,prefManager.getUSER_TYPE_ID());
        adapter.notifyDataSetChanged();
        binding.rvExpenses.setHasFixedSize(true);
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(ErrorPosActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvExpenses.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvExpenses.setAdapter(adapter);

      /*  binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              //  adapter.getFilter().filter(charSequence);
                fpscodee = charSequence.toString();
                getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/
        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());


    }






    private void setUpLayout(){
        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding. seDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);

            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();

        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding. seDistrict.setVisibility(View.GONE);


            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();

        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding. seDistrict.setVisibility(View.VISIBLE);
            districtId = getIntent().getStringExtra("districtId");
            if (districtIdd!=null    && !districtIdd.isEmpty())
            {
            try {
                    SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("districtId_key", Integer.parseInt(districtId));
                    editor.commit();
                } catch (NumberFormatException e) {
                }

            }
            else {

            }




            // districtId = prefManager.getUSER_DistrictId();
        }

    Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String todayDate = sdf.format(today);
        fromDate = todayDate;
        toDate = todayDate;
       getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
        Log.d("useriduserid", prefManager.getUSER_TYPE_ID());




        swipeRefreshLayout = findViewById(R.id.refreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);


            }
        });





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
        binding.spinner.setSelection(1);
        String selectedString = (String) binding.spinner.getSelectedItem();
        int selectedItemPosition = binding.spinner.getSelectedItemPosition();
        if (selectedItemPosition==1)
        {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String todayDate = sdf.format(today);
            fromDate = todayDate;
            toDate = todayDate;

        }
        else{

        }



    }

    private void setUpExpenseStatusSpinner() {
        ModelExpenseStatus model1 = new ModelExpenseStatus();
        model1.setId("0");
        model1.setExpense_status("--"+getResources().getString(R.string.errorstatus)+"--");
        modelExpenseStatusList.add(model1);
        ModelExpenseStatus model2 = new ModelExpenseStatus();
        model2.setId("1");
        model2.setExpense_status(getResources().getString(R.string.pending));
        modelExpenseStatusList.add(model2);
        ModelExpenseStatus model3 = new ModelExpenseStatus();
        model3.setId("2");
        model3.setExpense_status(getResources().getString(R.string.checking));
        modelExpenseStatusList.add(model3);
        ModelExpenseStatus model4 = new ModelExpenseStatus();
        model4.setId("3");
        model4.setExpense_status(getResources().getString(R.string.resolved));
        modelExpenseStatusList.add(model4);
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
    //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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

                 SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    Integer  myIntValue = sp.getInt("districtId_key", -1);
                    Log.d("districid",""+myIntValue);

                    if (myIntValue!=-1 )
                    {
                        String itt = String.valueOf(district_List.get(myIntValue));
                        Log.d("district_List"," "+itt);
                        if (itt!=null){
                            binding.seDistrict.setText(itt);

                        }
                    }
                    else {

                    }






                  //  binding.spinnerDistrict.setSelection(Integer.parseInt(itt));
                  // binding.spinnerDistrict.setSelection(Integer.parseInt(itt));
                }
            }
        });
    }

    private void getPosError(String expenseStatusId, String districtId, String fromDate, String toDate, String fpscodee){
        Log.d("fromdate","  "+fromDate+toDate);
        Log.d("districtId","  "+districtId);
        Log.d("expenseStatusId","  "+expenseStatusId);
        Log.d("ErrorTypeId","  "+ErrorTypeId);
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<GetPosDeviceErrorsRoot> call = service.GetPOSDeviceErrors("",prefManager.getUSER_Id(),fpscodee,districtId,"",expenseStatusId,fromDate, toDate,ErrorTypeId);
            call.enqueue(new Callback<GetPosDeviceErrorsRoot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<GetPosDeviceErrorsRoot> call, @NonNull Response<GetPosDeviceErrorsRoot> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {

                                    if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                        GetPosDeviceErrorsRoot getPosDeviceErrorsRoot = response.body();
                                        Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+getPosDeviceErrorsRoot);
                                     getPosDeviceErrorDatumArrayList=
                                        //  ArrayList<GetPosDeviceErrorDatum>  getPosDeviceErrorDatumArrayList =
                                                getPosDeviceErrorsRoot.getData();
                                        if (getPosDeviceErrorDatumArrayList.size() > 0) {
                                            binding.rvExpenses.setVisibility(View.VISIBLE);
                                            binding.textNoDataFound.setVisibility(View.GONE);
                                          //  List<ModelExpensesList> modelExpensesList = modelResponse.getExpensesList();
                                            setUpAdapter(getPosDeviceErrorDatumArrayList);
                                        } else {
                                            binding.rvExpenses.setVisibility(View.GONE);
                                            binding.textNoDataFound.setVisibility(View.VISIBLE);
                                        }


                                  /*  ArrayList<GetErrortypesDatum>  getErrortypesDatum =
                                            getErrorTypesRoot.getData();
                                    String msg =  getErrorTypesRoot.getMessage();
                                    String status =  getErrorTypesRoot.getStatus();

                                     JSONArray jsonArray = getErrortypesDatum

                                    String CreatedCampCount = getErrortypesDatum.get(Ar).;
                                    String OrganizedCampCount = dashboardUserDetails.getOrganizedCampCount();
                                    String AttendanceCount = dashboardUserDetails.getAttendanceCount();
                                    binding.textcreatedCampCount.setText(CreatedCampCount);
                                    binding.textCampsCount.setText(OrganizedCampCount);
                                    binding.textAttendanceCount.setText(AttendanceCount);*/
                                        //   Log.d("ghmn", "asmbnmldjfjas" + CreatedCampCount + "  " + OrganizedCampCount + " " + AttendanceCount);
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
                public void onFailure(@NonNull Call<GetPosDeviceErrorsRoot> call, @NonNull Throwable t) {
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

    private void setUpAdapter(ArrayList<GetPosDeviceErrorDatum> getPosDeviceErrorDatumArrayList) {
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvExpenses.setAdapter(new ErrorPosAdapter(mContext, getPosDeviceErrorDatumArrayList,prefManager.getUSER_TYPE_ID()));
    }

    @Override
    protected void onResume() {
        super.onResume();
     //   getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
           // onBackPressed();
            Intent i = new Intent(ErrorPosActivity.this, MainActivity.class);
            startActivity(i);


        }else if (id == R.id.textFromDate) {
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

        else if (id==R.id.tv_add)
        {
            Intent i = new Intent(ErrorPosActivity.this, AddnewErrorPosSE.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        Intent i = new Intent(ErrorPosActivity.this, MainActivity.class);
        startActivity(i);
    }













}