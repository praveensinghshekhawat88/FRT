package com.callmangement.ui.ins_weighing_scale.activity;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDeliverylistBinding;
import com.callmangement.ui.ins_weighing_scale.adapter.DeliveredWgtInsAdapter;
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData;
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeightInsRoot;
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

public class InstallationPendingList extends CustomActivity {
    private ActivityDeliverylistBinding binding;
    private final List<String> spinnerList = new ArrayList<>();
    Activity mActivity;
    PrefManager prefManager;
    RecyclerView mRecyclerView;
    Context mContext;
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
    String fpscodee;
    String value_selectedDay,value_selectedDis;
    private Vibrator vibrator;
    SharedPreferences sharedPreferences;
    ArrayList<WeighInsData> weighInsDataArrayList;
    String originalFileName = "Demo.xlsx"; // Original file name
   // String uniqueFileName = generateUniqueFileName(originalFileName);
   // private final File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityDeliverylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
     //   clearSharePreference();
        mActivity = this;
        mContext = this;
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
      //  binding.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel));
        mRecyclerView = findViewById(R.id.rv_delivered);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.installation_pending_list));

        setUpData();
        setBinding();
        setClickListener();

        //  setClickListener();
    }




    private void setUpData() {


        if (prefManager.getUseR_TYPE_ID().equals("1") && prefManager.getUseR_TYPE().equalsIgnoreCase("Admin")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding. seDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();

        } else if (prefManager.getUseR_TYPE_ID().equals("2") && prefManager.getUseR_TYPE().equalsIgnoreCase("Manager")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding. seDistrict.setVisibility(View.GONE);


            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();

        } else if (prefManager.getUseR_TYPE_ID().equals("4") && prefManager.getUseR_TYPE().equalsIgnoreCase("ServiceEngineer")) {
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

        String USER_NAME = prefManager.getUseR_NAME();
        String USER_EMAIL = prefManager.getUseR_EMAIL();
        String USER_Mobile = prefManager.getUseR_Mobile();
        String USER_DISTRICT = prefManager.getUseR_District();
        Log.d("USER_NAME"," "+USER_NAME);
        //  getIrisWeighInstallation();
        // districtId = getIntent().getStringExtra("districtId");
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
        //  getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);

        Intent intent = getIntent();
        // Get the value from the intent using the key

        if (intent != null) {
            String value_districtId = intent.getStringExtra("key_districtId");
            String value_fromDate = intent.getStringExtra("key_fromDate");
            String value_toDate = intent.getStringExtra("key_toDate");

            value_selectedDay = intent.getStringExtra("key_selectedDate");
            value_selectedDis = intent.getStringExtra("key_selectedDis");
            Log.d("value_selectedDay", value_selectedDay);


            districtId = value_districtId;
            fromDate = value_fromDate;
            toDate = value_toDate;
            getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);

        }
        else {
            binding.spinner.setSelection(1);

            getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);


        }







        // Log.d("useriduserid",""+prefManager.getUseR_TYPE_ID());


        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner();


    }


    private void setBinding()
    {

    }


    private void setClickListener()
    {


        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();


            }
        });


       /* binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                ExcelformTable();
            }
        });*/

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

                fpscodee = binding.inputfps.getText().toString();

                getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);



            }
        });

    }

    private void getIrisWeighInstallation(String districtId, String fromDate, String toDate,String fpscodee) {


        if (Constants.isNetworkAvailable(mActivity)) {

            hideKeyboard(mActivity);
            showProgress();

            //  showProgress(getResources().getString(R.string.please_wait));


            String USER_Id = prefManager.getUseR_Id();
            Log.d("USER_ID", " "+USER_Id);
            Log.d("myDistrictId"," "+ districtId);
            Log.d("myfromDate", " "+fromDate);
            Log.d("mytoDate", " "+toDate);


            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);

            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,fpscodee,districtId,"","1",fromDate,toDate);

            call.enqueue(new Callback<WeightInsRoot>() {
                @Override
                public void onResponse(@NonNull Call<WeightInsRoot> call, @NonNull Response<WeightInsRoot> response) {
                    hideProgress();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding.txtNoRecord.setVisibility(View.GONE);


                                if (response.body().getStatus().equals("200")) {
                                    binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
                                   // binding.buttonexcel.setVisibility(View.VISIBLE);

                                    WeightInsRoot weightInsRoot = response.body();
                                    String message =
                                            weightInsRoot.getMessage();

                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();

                                    //     ArrayList<WeighInsData> weighInsDataArrayList = weightInsRoot.getData();
                                    weighInsDataArrayList = weightInsRoot.getData();
                                    Log.d("weighInsDataArrayList", "Size...." + weighInsDataArrayList.size());

                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(InstallationPendingList.this));
                                    mRecyclerView.addItemDecoration(new DividerItemDecoration(InstallationPendingList.this, DividerItemDecoration.VERTICAL));
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    DeliveredWgtInsAdapter deliveredWgtInsAdapter = new DeliveredWgtInsAdapter(weighInsDataArrayList, mContext,mActivity,prefManager.getUseR_TYPE_ID());
                                    mRecyclerView.setAdapter(deliveredWgtInsAdapter);



                                /*    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    // creating a new variable for gson.
                                    Gson gson = new Gson();

                                    // getting data from gson and storing it in a string.
                                    String json = gson.toJson(  response.body().getData());

                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("courses", json);

                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply();*/




                                    // Use 'dataValue' or perform operations with other properties

                                }
                                else {

                                    binding.txtNoRecord.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                    showAlertDialogWithSingleButton(mActivity, response.body().getMessage());
                                    binding.actionBar.buttonPDF.setVisibility(View.GONE);


                                }






                            } else {
                                binding.txtNoRecord.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                                binding.actionBar.buttonPDF.setVisibility(View.GONE);


                                showAlertDialogWithSingleButton(mActivity, response.body().getMessage());
                            }


                        } else {
                            String msg =  "HTTP Error: "+ response.code();
                            showAlertDialogWithSingleButton(mActivity, msg);
                            binding.actionBar.buttonPDF.setVisibility(View.GONE);

                        }
                    } else {

                        String msg =  "HTTP Error: "+ response.code();
                        showAlertDialogWithSingleButton(mActivity, msg);
                        binding.actionBar.buttonPDF.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeightInsRoot> call, @NonNull Throwable error) {
                    hideProgress();

                    showAlertDialogWithSingleButton(mActivity, error.getMessage());
                    binding.actionBar.buttonPDF.setVisibility(View.GONE);

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
            binding.actionBar.buttonPDF.setVisibility(View.GONE);


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
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            String USER_Id = prefManager.getUseR_Id();
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDistrict_w> call = apiInterface.apiGetDistictList_w();


            call.enqueue(new Callback<ModelDistrict_w>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict_w> call, @NonNull Response<ModelDistrict_w> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {

                            Log.d("mydataresponse",""+response.code());
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
                                        binding.spinnerDistrict.setSelection(Integer.parseInt(value_selectedDis));

                                      /* if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));

                                        }*/


                                      /*  if(d2!=null && !d2.isEmpty())
                                        {
                                            Log.d("ghjh",""+d2);

                                   int userId = Integer.parseInt(d2); // replace with the user ID you want to select

                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelDistrictList user = dataAdapter.getItem(i);
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
        binding.spinner.setSelection(Integer.parseInt(value_selectedDay));

        //  binding.spinner.setSelection(1);
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();

    }
    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 444){
            if(resultCode == RESULT_OK){
                getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
            }else {

            }
        }
    }



    //excel

/*    private void ExcelformTable() {

        mActivity = this;
        //  prefManager = PrefManager.getInstance(mActivity);
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("courses", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<WeighInsData>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        weighInsDataArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (weighInsDataArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            weighInsDataArrayList = new ArrayList<>();
            Log.d("nbb",""+weighInsDataArrayList);

        }
        Log.d("gfhvbb",""+weighInsDataArrayList);

        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        //    String corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat);
        //   String corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        String corStartDate = fromDate;
        String corEndDate = toDate;
        Log.d("formateddate", corStartDate);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Installation Pending "+corStartDate+" - "+corEndDate);

        // Create cell style for center alignment

        HSSFRow rowA = firstSheet.createRow(0);
        rowA.setHeightInPoints(20); // Set row height in points
        HSSFCell cellA = rowA.createCell(0);
      *//*  cellA.setCellValue(new HSSFRichTextString("District\n name"));
        // Create a bold font
        HSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);

        // Apply the bold font to a new cell style
        HSSFCellStyle boldCellStyle = workbook.createCellStyle();
        boldCellStyle.setFont(boldFont);

        cellA.setCellStyle(boldCellStyle);

        firstSheet.setColumnWidth(0, 3000);
*//*
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        // Create a RichTextString with bold formatting
        RichTextString richText = new HSSFRichTextString("District\nName");
        richText.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellA.setCellValue(richText);
        firstSheet.setColumnWidth(0, 4000);
        HSSFCell cellB = rowA.createCell(1);
        RichTextString richText1 = new HSSFRichTextString("FPS\nCode");

        richText1.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1);
        firstSheet.setColumnWidth(1, 2000);
        //   cellB.setCellStyle(cellStyle);
        HSSFCell cellC = rowA.createCell(2);
        firstSheet.setColumnWidth(2, 11000);
        RichTextString richText2 = new HSSFRichTextString("Dealer\nName");
        richText2.applyFont(boldFont);
        cellC.setCellValue(richText2);

        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));

        HSSFCell cellD = rowA.createCell(3);
        firstSheet.setColumnWidth(3, 4000);

        RichTextString richText3 = new HSSFRichTextString("DealerMobileNo");
        richText3.applyFont(boldFont);
        cellD.setCellValue(richText3);
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        HSSFCell cellE = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 4000);
        RichTextString richText4 = new HSSFRichTextString("W_Model");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);
        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        HSSFCell cellF = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 4000);
        RichTextString richText5 = new HSSFRichTextString("W_SerialNo");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        HSSFCell cellG = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 4000);
        RichTextString richText6 = new HSSFRichTextString("TicketNo");
        richText6.applyFont(boldFont);
        cellG.setCellValue(richText6);
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        HSSFCell cellH = rowA.createCell(7);
        firstSheet.setColumnWidth(7, 5000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText7 = new HSSFRichTextString("Delivered\nDate");
        richText7.applyFont(boldFont);
        cellH.setCellValue(richText7);



        Log.d("mylist"," -------------- "+weighInsDataArrayList);
        if(weighInsDataArrayList != null && weighInsDataArrayList.size() > 0) {
            for (int i = 0; i < weighInsDataArrayList.size(); i++) {
                WeighInsData detailsInfo = weighInsDataArrayList.get(i);
                String districtName = String.valueOf(detailsInfo.getDistrictName());
                String dName = String.valueOf(detailsInfo.getDealerName());
                String fps = String.valueOf(detailsInfo.getFpscode());
                String modelName = String.valueOf(detailsInfo.getWeighingScaleModelName());
                String serialNo = String.valueOf(detailsInfo.getWeighingScaleSerialNo());
                String date = String.valueOf(detailsInfo.getWinghingScaleDeliveredOnStr());
                String mobileNo = String.valueOf(detailsInfo.getDealerMobileNo());
                String ticketNo = String.valueOf(detailsInfo.getTicketNo());
                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(fps);
                dataRow.createCell(2).setCellValue(dName);
                dataRow.createCell(3).setCellValue(mobileNo);
                dataRow.createCell(4).setCellValue(modelName);
                dataRow.createCell(5).setCellValue(serialNo);
                dataRow.createCell(6).setCellValue(ticketNo);
                dataRow.createCell(7).setCellValue(date);
            }
        }



        FileOutputStream fos = null;
        try {
            String str_path = Environment.getExternalStorageDirectory().toString();
            File file ;
            file = new File(str_path, getString(R.string.app_name) + ".xls");
            fos = new FileOutputStream(filePathh);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(InstallationPendingList.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
        }

        long timeMillis = System.currentTimeMillis();

        // Generate a random number.
        Random random = new Random();
        int randomNumber = random.nextInt(100000);

        // Combine the current date and time with the random number to generate a unique string.
        String fileName = String.format("excel_%d_%d", timeMillis, randomNumber);
        Log.d("fkddv","fh"+fileName);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Filter for Excel files

        try {
            startActivityForResult(intent, 1); // Use startActivityForResult to get the selected file's URI
        } catch (ActivityNotFoundException e) {
            // Handle the case where no app capable of handling this intent is installed
        }


    }

    private String generateUniqueFileName(String originalFileName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileExtension = getFileExtension(originalFileName);
        return "excel_" + timeStamp + "." + fileExtension;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    public void clearSharePreference(){
        // super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
// Clearing the value associated with the "camp" key
        editor.remove("courses");
// Applying the changes to save the updated SharedPreferences
        editor.apply();
    }*/

}
