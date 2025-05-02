package com.callmangement.ui.biometric_delivery;

import static java.lang.String.valueOf;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityBiometricDeliveryListBinding;
import com.callmangement.databinding.ActivitySensorSummaryBinding;
import com.callmangement.ui.biometric_delivery.adapter.SensorDistributionListAdapter;
import com.callmangement.ui.biometric_delivery.adapter.SensorSummaryAdapter;
import com.callmangement.ui.biometric_delivery.model.SensorDistributionDetailsListResp;
import com.callmangement.ui.biometric_delivery.model.SensorSummaryResponse;
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w;
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w;
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
import retrofit2.http.Field;


public class BiometricDeliveryListActivity extends CustomActivity {
    private ActivityBiometricDeliveryListBinding binding;
    private final List<String> spinnerList = new ArrayList<>();
    private Activity mActivity;
    private PrefManager prefManager;
    private Context mContext;
    private Spinner spinnerDistrict;
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private final String districtIdd = "0";
    private final String myFormat = "yyyy-MM-dd";
    private String fromDate = "";
    private String toDate = "";
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private List<ModelDistrictList_w> district_List = new ArrayList<>();
    private String dis;
    private int checkFilter = 0;
    private String fpscodee;
    private String value_selectedDay, value_selectedDis;
    private String FILTER_TYPE_ID = "";
    private Vibrator vibrator;
    ArrayList<SensorDistributionDetailsListResp.Data> sensorDistributionList = new ArrayList<>();
    private SensorDistributionListAdapter sensorDistributionListAdapter;
    private boolean isLoading = false;
    private boolean allPagesLoaded = false;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityBiometricDeliveryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        //   clearSharePreference();
        mActivity = this;
        mContext = this;
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        //    binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        //  binding.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.biometric_delivery_list));

        setUpData();
        setBinding();
        setClickListener();

        //  setClickListener();
    }

    private void setUpData() {

        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();

        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);

            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();

        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.VISIBLE);
            districtId = getIntent().getStringExtra("districtId");
            if (districtIdd != null && !districtIdd.isEmpty()) {
                try {
                    SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("districtId_key", Integer.parseInt(districtId));
                    editor.commit();
                } catch (NumberFormatException e) {
                }

            } else {

            }
        }

        String USER_NAME = prefManager.getUSER_NAME();
        String USER_EMAIL = prefManager.getUSER_EMAIL();
        String USER_Mobile = prefManager.getUSER_Mobile();
        String USER_DISTRICT = prefManager.getUSER_District();
        Log.d("USER_NAME", " " + USER_NAME);

        binding.seDistrict.setText(USER_DISTRICT);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        districtList();
        districtNameEng = "--" + getResources().getString(R.string.district) + "--";

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String todayDate = sdf.format(today);
        fromDate = todayDate;
        toDate = todayDate;

        binding.rvDelivered.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rvDelivered.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        sensorDistributionListAdapter = new SensorDistributionListAdapter(sensorDistributionList);
        binding.rvDelivered.setAdapter(sensorDistributionListAdapter);

        binding.rvDelivered.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    getSensorDistributionList(districtId, fromDate, toDate);
                    //    currentPage++;
                }
            }
        });


        Intent intent = getIntent();
        // Get the value from the intent using the key
        if (intent != null) {
            String value_districtId = intent.getStringExtra("key_districtId");
            String value_fromDate = intent.getStringExtra("key_fromDate");
            String value_toDate = intent.getStringExtra("key_toDate");

            value_selectedDay = intent.getStringExtra("key_selectedDate");
            value_selectedDis = intent.getStringExtra("key_selectedDis");
            FILTER_TYPE_ID = intent.getStringExtra("FILTER_TYPE_ID");
            Log.d("value_selectedDay", value_selectedDay);

            districtId = value_districtId;
            fromDate = value_fromDate;
            toDate = value_toDate;
            getSensorDistributionList(districtId, fromDate, toDate);

        } else {
            binding.spinner.setSelection(1);
            getSensorDistributionList(districtId, fromDate, toDate);
        }

        setUpDateRangeSpinner();

    }

    private void setBinding() {

    }

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //  binding.ivChallanImage.setOnClickListener(this);

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    dis = district_List.get(i).getDistrictNameEng();
                    Log.d("dfgfd", " " + dis);
                    districtId = district_List.get(i).getDistrictId();
                    Log.d("fggfgh", " " + districtId);
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                    editor.putString("dis", dis);
                    editor.putString("districtId", districtId);
                    editor.apply();

                          /*  if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                                SEUser_list.clear();
                                Collections.reverse(SEUser_list);
                                ModelSEUserList list = new ModelSEUserList();
                                list.setUserId(valueOf(-1));
                                list.setUserName("--" + getResources().getString(R.string.se_user) + "--");
                                SEUser_list.add(list);
                                Collections.reverse(SEUser_list);
                                ArrayAdapter<ModelSEUserList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, SEUser_list);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerSEUsers.setAdapter(dataAdapter);
                            } else {
                                SEUsersList(districtId);
                            }*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        binding.btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentPage = 1;
                allPagesLoaded = false;
                sensorDistributionList.clear();
                sensorDistributionListAdapter.notifyDataSetChanged();
                getSensorDistributionList(districtId, fromDate, toDate);
            }
        });
    }

    private void getSensorDistributionList(String districtId, String fromDate, String toDate) {

        if (Constants.isNetworkAvailable(mActivity)) {

            hideKeyboard(mActivity);
            showProgress();

            fpscodee = binding.inputfps.getText().toString();

            String seUserId = "0";
            String USER_Id = prefManager.getUSER_Id();
            Log.d("USER_ID", " " + USER_Id);
            Log.d("myDistrictId", " " + districtId);
            Log.d("myfromDate", " " + fromDate);
            Log.d("mytoDate", " " + toDate);

            if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE()
                    .equalsIgnoreCase("ServiceEngineer")) {
                seUserId = USER_Id;
            }

            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<SensorDistributionDetailsListResp> call = apiInterface.getSensorDistributionDetailsList(USER_Id, fpscodee, districtId,
                    "", "", FILTER_TYPE_ID, "1", "" + currentPage, "50", seUserId, fromDate, toDate);

            call.enqueue(new Callback<SensorDistributionDetailsListResp>() {
                @Override
                public void onResponse(@NonNull Call<SensorDistributionDetailsListResp> call, @NonNull Response<SensorDistributionDetailsListResp> response) {
                    hideProgress();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding.txtNoRecord.setVisibility(View.GONE);


                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    //                    binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);

                                    SensorDistributionDetailsListResp sensorSummaryResponse = response.body();
                                    String message = sensorSummaryResponse.getMessage();

                                    if (sensorSummaryResponse.getCurrentPage() == sensorSummaryResponse.getTotalPages())
                                        allPagesLoaded = true;
                                    else
                                        allPagesLoaded = false;

                                    //     sensorDistributionList = sensorSummaryResponse.getData();
                                    sensorDistributionList.addAll(sensorSummaryResponse.getData());
                                    sensorDistributionListAdapter.notifyDataSetChanged();
                                    Log.d("sensorDistributionList", "Size...." + sensorDistributionList.size());

//                                    binding.rvDelivered.setLayoutManager(new LinearLayoutManager(mContext));
//                                    binding.rvDelivered.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
//                                    binding.rvDelivered.setVisibility(View.VISIBLE);
//                                    SensorDistributionListAdapter sensorDistributionListAdapter = new SensorDistributionListAdapter(sensorDistributionList);
//                                    binding.rvDelivered.setAdapter(sensorDistributionListAdapter);

                                    binding.rvDelivered.setVisibility(View.VISIBLE);

                                    currentPage++;

                                } else {
                                    binding.txtNoRecord.setVisibility(View.VISIBLE);
                                    binding.rvDelivered.setVisibility(View.GONE);
                                    showAlertDialogWithSingleButton(mActivity, response.body().getMessage());
                                    binding.actionBar.buttonPDF.setVisibility(View.GONE);
                                }
                            } else {
                                binding.txtNoRecord.setVisibility(View.VISIBLE);
                                binding.rvDelivered.setVisibility(View.GONE);
                                binding.actionBar.buttonPDF.setVisibility(View.GONE);
                                showAlertDialogWithSingleButton(mActivity, response.body().getMessage());
                            }
                        } else {
                            String msg = "HTTP Error: " + response.code();
                            showAlertDialogWithSingleButton(mActivity, msg);
                            binding.actionBar.buttonPDF.setVisibility(View.GONE);
                        }
                    } else {
                        String msg = "HTTP Error: " + response.code();
                        showAlertDialogWithSingleButton(mActivity, msg);
                        binding.actionBar.buttonPDF.setVisibility(View.GONE);
                    }
                    isLoading = false;
                }

                @Override
                public void onFailure(@NonNull Call<SensorDistributionDetailsListResp> call, @NonNull Throwable error) {
                    hideProgress();
                    showAlertDialogWithSingleButton(mActivity, error.getMessage());
                    binding.actionBar.buttonPDF.setVisibility(View.GONE);
                    call.cancel();
                    isLoading = false;
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
            binding.actionBar.buttonPDF.setVisibility(View.GONE);
            isLoading = false;
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
        //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
    }

    private void districtList() {

        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            hideProgress();
            String USER_Id = prefManager.getUSER_Id();
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDistrict_w> call = apiInterface.apiGetDistictList_w();

            call.enqueue(new Callback<ModelDistrict_w>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict_w> call, @NonNull Response<ModelDistrict_w> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {

                            Log.d("mydataresponse", "" + response.code());
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    district_List = response.body().getDistrict_List();
                                    if (district_List != null && district_List.size() > 0) {
                                        Collections.reverse(district_List);
                                        ModelDistrictList_w l = new ModelDistrictList_w();
                                        l.setDistrictId(valueOf(-1));
                                        l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                                        district_List.add(l);
                                        Collections.reverse(district_List);
                                        ArrayAdapter<ModelDistrictList_w> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, district_List);
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerDistrict.setAdapter(dataAdapter);
                                        binding.spinnerDistrict.setSelection(Integer.parseInt(value_selectedDis));

                                    }
                                } else {
                                    makeToast(response.body().getMessage());
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelDistrict_w> call, @NonNull Throwable error) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
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
        binding.spinner.setSelection(Integer.parseInt(value_selectedDay));

        //  binding.spinner.setSelection(1);
        String selectedString = (String) binding.spinner.getSelectedItem();
        int selectedItemPosition = binding.spinner.getSelectedItemPosition();
        if (selectedItemPosition == 1) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String todayDate = sdf.format(today);
            fromDate = todayDate;
            toDate = todayDate;

        } else {
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

}
