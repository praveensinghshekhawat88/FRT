package com.callmangement.ui.ins_weighing_scale.activity;

import static java.lang.String.valueOf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityWeighingScaleDashboardBinding;
import com.callmangement.ui.home.MainActivity;
import com.callmangement.ui.ins_weighing_scale.model.Count.CountDatum;
import com.callmangement.ui.ins_weighing_scale.model.Count.CountRoot;
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


public class WeighingScaleDashboard extends CustomActivity {
    private final List<String> spinnerList = new ArrayList<>();
    private final String myFormat = "yyyy-MM-dd";
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    CardView cvNewDelivery, cvCompleteDelivery, cvAbout;
    Activity mActivity;
    Context mContext;
    AlertDialog.Builder builder;
    PrefManager prefManager;
    private ActivityWeighingScaleDashboardBinding binding;
    //   PrefManager preference;
    private List<ModelDistrictList_w> district_List = new ArrayList<>();
    private int checkDistrict = 0;
    private String districtId = "0";
    private final String districtIdd = "0";
    private Spinner spinnerDistrict;
    private String districtNameEng = "";
    private String dis;
    private int checkFilter = 0;
    private String fromDate = "";
    private String toDate = "";
    SwipeRefreshLayout swipeRefreshLayout;
    String Delivered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeighingScaleDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        mContext = this;
        prefManager = new PrefManager(mContext);
        swipeRefreshLayout = findViewById(R.id.refreshLayoutt);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.dashboard));
        //preference = PrefManager.getInstance(mActivity);
        //   prefManager = new PrefManager(mContext);
        setUpData();
        setClickListener();
    }


    private void setUpData() {
        if (prefManager.getUseR_TYPE_ID().equals("1") && prefManager.getUseR_TYPE().equalsIgnoreCase("Admin")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.cvReplace.setVisibility(View.GONE);
            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();

        } else if (prefManager.getUseR_TYPE_ID().equals("2") && prefManager.getUseR_TYPE().equalsIgnoreCase("Manager")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);
            binding.cvReplace.setVisibility(View.GONE);
            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();
        } else if (prefManager.getUseR_TYPE_ID().equals("4") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.VISIBLE);
            binding.cvReplace.setVisibility(View.VISIBLE);
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
        String USER_NAME = prefManager.getUseR_NAME();
        String USER_EMAIL = prefManager.getUseR_EMAIL();
        String USER_Mobile = prefManager.getUseR_Mobile();
        String USER_DISTRICT = prefManager.getUseR_District();
        Log.d("USER_NAME", " " + USER_NAME);
        Log.d("USER_Email", " " + USER_EMAIL);
        Log.d("USER_Dis", " " + USER_DISTRICT);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.dashboard));
        binding.textUsername.setText(USER_NAME);
        binding.textEmail.setText(USER_EMAIL);
        binding.textmbl.setText(USER_Mobile);
        //29-01-2024
        //  districtId = getIntent().getStringExtra("districtId");
        binding.seDistrict.setText(USER_DISTRICT);
        builder = new AlertDialog.Builder(this);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        districtList();
        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String todayDate = sdf.format(today);
        fromDate = todayDate;
        toDate = todayDate;
        getInstallationCntApi(districtId, fromDate, toDate);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getInstallationCntApi(districtId, fromDate, toDate);
            }
        });


        // Log.d("useriduserid",""+prefManager.getUseR_TYPE_ID());


        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner();


    }

    private void setClickListener() {
        binding.cvInstalled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Check if the device supports vibration
                if (vibrator.hasVibrator()) {
                    // Vibrate for 500 milliseconds (0.5 seconds)
                    vibrator.vibrate(100);
                    String selectedDate = String.valueOf(binding.spinner.getSelectedItemPosition());
                    String selectedDis = String.valueOf(binding.spinnerDistrict.getSelectedItemPosition());

                    Log.d("selectedDate", selectedDate);

                    Intent i = new Intent(WeighingScaleDashboard.this, InstalledList.class);
                    i.putExtra("key_districtId", districtId);
                    i.putExtra("key_fromDate", fromDate);
                    i.putExtra("key_toDate", toDate);
                    i.putExtra("key_selectedDate", selectedDate);
                    i.putExtra("key_selectedDis", selectedDis);

                    startActivity(i);
                }
            }
        });

        binding.cvInstallationPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                if (Delivered.equals("0")) {
                    showAlertDialogWithSingleButton(mActivity, getResources().getString(R.string.noinstpending));
                    // Check if the device supports vibration
                } else {
                    String selectedDate = String.valueOf(binding.spinner.getSelectedItemPosition());
                    String selectedDis = String.valueOf(binding.spinnerDistrict.getSelectedItemPosition());
                    Intent i = new Intent(WeighingScaleDashboard.this, InstallationPendingList.class);
                    i.putExtra("key_districtId", districtId);
                    i.putExtra("key_fromDate", fromDate);
                    i.putExtra("key_toDate", toDate);
                    i.putExtra("key_selectedDate", selectedDate);
                    i.putExtra("key_selectedDis", selectedDis);
                    startActivityForResult(i, 222);
                    Log.d("selectedDis", "selectedDis" + selectedDis);


                }

                // Check if the device supports vibration

            }
        });
        /*binding.logout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                logoutAlartDialoge();

            }
        });*/

        binding.cvReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Check if the device supports vibration
                if (vibrator.hasVibrator()) {
                    // Vibrate for 500 milliseconds (0.5 seconds)
                    vibrator.vibrate(100);
                    String selectedDate = String.valueOf(binding.spinner.getSelectedItemPosition());
                    String selectedDis = String.valueOf(binding.spinnerDistrict.getSelectedItemPosition());

                    Log.d("selectedDate", selectedDate);

                    Intent i = new Intent(WeighingScaleDashboard.this, InstalledReplaceListActivity.class);
                    i.putExtra("key_districtId", districtId);
                    i.putExtra("key_fromDate", fromDate);
                    i.putExtra("key_toDate", toDate);
                    i.putExtra("key_selectedDate", selectedDate);
                    i.putExtra("key_selectedDis", selectedDis);

                    startActivity(i);
                }
            }
        });


        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WeighingScaleDashboard.this, MainActivity.class);
                startActivity(i);

            }
        });


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

                    getInstallationCntApi(districtId, fromDate, toDate);

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

                            getInstallationCntApi(districtId, fromDate, toDate);

                            // getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fromDate = yesterdayDate;
                            toDate = yesterdayDate;
                            getInstallationCntApi(districtId, fromDate, toDate);

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

                            getInstallationCntApi(districtId, fromDate, toDate);

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

                            getInstallationCntApi(districtId, fromDate, toDate);

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

                        getInstallationCntApi(districtId, fromDate, toDate);

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
                getInstallationCntApi(districtId, fromDate, toDate);
            }
        });


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
        getInstallationCntApi(districtId, fromDate, toDate);

    }

    private void getInstallationCntApi(String districtId, String fromDate, String toDate) {

//Progress Bar while connection establishes
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            showProgress();
            String USER_Id = prefManager.getUseR_Id();
            Log.d("USER_ID"," "+ USER_Id);
            Log.d("districtId", " "+districtId);
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<CountRoot> call = service.appWeightCountApiiris(USER_Id, districtId, "0", "0", fromDate, toDate, "0");

            //   APIInterface apiInterface = APIClient.getRetrofitClientWithoutHeaders(mActivity!!!!,Utils.Baseurl).create(APIInterface.class);

            //   Call<CountRoot> call = apiInterface.appWeightCountApi(USER_Id,"" ,districtId,"", "0",fromDate,toDate);

            call.enqueue(new Callback<CountRoot>() {
                @Override
                public void onResponse(@NonNull Call<CountRoot> call, @NonNull Response<CountRoot> response) {
                    // Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body().getStatus().equals("200")) {
                                    CountRoot countRoot = response.body();
                                    ArrayList<CountDatum> countDatum =
                                            countRoot.getData();
                                    // makeToast(String.valueOf(response.body().getMessage()));

                                    if (countDatum != null && !countDatum.isEmpty()) {
                                        // Perform operations with the data in countDatum
                                        for (CountDatum datum : countDatum) {
                                            // Access individual data items in the countDatum ArrayList
                                            // Example: Accessing a property of CountDatum
                                            String total = String.valueOf(datum.getTotalDelivered());
                                            Delivered = String.valueOf(datum.getInstallationPending());
                                            String Installed = String.valueOf(datum.getInstalled());
                                            binding.tvTotalcount.setText(total);
                                            binding.tvDelivered.setText(Delivered);
                                            binding.tvInstalled.setText(Installed);
                                            // Use 'dataValue' or perform operations with other properties
                                        }
                                    } else {
                                        makeToast(String.valueOf(response.body().getMessage()));
                                        // Handle the case when countDatum is empty or null
                                    }
                                } else {
                                    // Handle the case when the response status is not 200 or the response body is null
                                }
                            } else {
                                makeToast(String.valueOf(response.body().getMessage()));
                            }
                        } else {
                            String msg = "HTTP Error: " + response.code();
                            Toast.makeText(WeighingScaleDashboard.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        String msg = "HTTP Error: " + response.code();
                        Toast.makeText(WeighingScaleDashboard.this, msg, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<CountRoot> call, @NonNull Throwable error) {
                    //  Utils.hideCustomProgressDialogCommonForAll();
                    hideProgress();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));

        }

    }

    private void districtList() {
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            String USER_Id = prefManager.getUseR_Id();
            //  APIInterface apiInterface = APIClient.getRetrofitClientWithoutHeaders(mActivity!!!!,Utils.Baseurl).create(APIInterface.class);
            //  Call<ModelDistrict_w> call = apiInterface.apiGetDistictList();


            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDistrict_w> call = service.apiGetDistictList_w();


            call.enqueue(new Callback<ModelDistrict_w>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict_w> call, @NonNull Response<ModelDistrict_w> response) {
                //    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {

                            Log.d("mydataresponse", "" + response.code());
                            if (response.body() != null) {
                                if (response.body().getStatus().equals("200")) {
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
                                      /* if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));

                                        }*/


                                      /*  if(d2!=null && !d2.isEmpty())
                                        {
                                            Log.d("ghjh",""+d2);

                                   int userId = Integer.parseInt(d2); // replace with the user ID you want to select

                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelDistrictList_w user = dataAdapter.getItem(i);
                                                if (String.valueOf(user.getDistrictId()).equals(String.valueOf(userId))) {
                                                    spinnerDistrict.setSelection(i);
                                                    break;
                                                }
                                            }


                                        }*/

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
        binding.spinner.setSelection(1);
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

        Intent i = new Intent(WeighingScaleDashboard.this, MainActivity.class);
        startActivity(i);



       /* //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        finishAffinity();

                        // Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                //Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        //Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                             //   Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Exit");
        alert.show();

*/

    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 222) {
            getInstallationCntApi(districtId, fromDate, toDate);
        }
    }
}
