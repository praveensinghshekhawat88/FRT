package com.callmangement.EHR.ehrActivities;


import static com.callmangement.custom.CustomActivity.hideKeyboard;
import static java.lang.String.valueOf;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callmangement.BuildConfig;
import com.callmangement.EHR.adapter.FeedbackListAdapter;
import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.models.FeedbackbyDCListDatum;
import com.callmangement.EHR.models.FeedbackbyDCListRoot;
import com.callmangement.EHR.models.ModelSEUser;
import com.callmangement.EHR.models.ModelSEUserList;
import com.callmangement.EHR.support.Utils;
import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.databinding.ActivityFeedbackformListBinding;
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w;
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFormListActivity extends BaseActivity {
    private ActivityFeedbackformListBinding binding;
    private final List<String> spinnerList = new ArrayList<>();
    Activity mActivity;
    PrefManager prefManager;
    RecyclerView mRecyclerView;
    Context mContext;
    private Spinner spinnerDistrict;
    private Spinner spinnerSEUsers;
    private String SEUserNameEng = "";

    private int checkDistrict = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private String SEUserId = "0";
    String USER_Id;

    private final String districtIdd = "0";
    private final String myFormat = "yyyy-MM-dd";
    private String fromDate = "";
    private String toDate = "";
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private List<ModelDistrictList_w> district_List = new ArrayList<>();
    private List<ModelSEUserList> SEUser_list = new ArrayList<>();

    private String dis;
    private int checkFilter = 0;
    SwipeRefreshLayout swipeRefreshLayout;
    String fpscodee, selecteduserid;
    String value_selectedDay, value_selectedDis;
    private Vibrator vibrator;
    SharedPreferences sharedPreferences;
    ArrayList<FeedbackbyDCListDatum> feedbackbyDCListDatumArrayList;
    String originalFileName = "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);
    private File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    private int checkSEUser = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        // getSupportActionBar().hide(); // hide the title bar
        binding = ActivityFeedbackformListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        mContext = this;
        clearSharePreference();

        prefManager = new PrefManager(mContext);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        swipeRefreshLayout = findViewById(R.id.refresh_list);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        mRecyclerView = findViewById(R.id.rv_delivered);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.feedback_form_list));
        USER_Id = prefManager.getUSER_Id();

        setUpData();
        setClickListener();
        //  setClickListener();
    }


    private void setUpData() {
        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);
            binding.rlTehsil.setVisibility(View.VISIBLE);

            binding.spacer.setVisibility(View.VISIBLE);
            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();
        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);
            binding.rlTehsil.setVisibility(View.VISIBLE);

            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();
        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.VISIBLE);
            binding.rlTehsil.setVisibility(View.GONE);

            districtId = getIntent().getStringExtra("districtId");
            SEUserId = USER_Id;

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
        //  getIrisWeighInstallation();
        //  districtId = getIntent().getStringExtra("districtId");
        binding.seDistrict.setText(USER_DISTRICT);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerSEUsers = findViewById(R.id.spinnerSEUsers);

        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        SEUserNameEng = "--" + getResources().getString(R.string.se_user) + "--";


        districtList();

        Collections.reverse(SEUser_list);
        ModelSEUserList l = new ModelSEUserList();
        l.setUserId(valueOf(-1));
        l.setUserName("--" + getResources().getString(R.string.se_user) + "--");
        SEUser_list.add(l);
        Collections.reverse(SEUser_list);
        ArrayAdapter<ModelSEUserList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, SEUser_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSEUsers.setAdapter(dataAdapter);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String todayDate = sdf.format(today);
        fromDate = todayDate;
        toDate = todayDate;
        //   getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee);


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

            //    Log.d("value_selectedDay", value_selectedDay);
            //   binding.spinner.setSelection(2);

            districtId = value_districtId;
            fromDate = value_fromDate;
            toDate = value_toDate;
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee);
        } else {
            //  binding.spinner.setSelection(1);

            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee);


        }
        Log.d("----ApiValue-----", districtId + " " + fromDate + " " + toDate + " " + fpscodee);


        // Log.d("useriduserid",""+prefManager.getUSER_TYPE_ID());


        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner();


    }


    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSharePreference();
                Intent i = new Intent(FeedbackFormListActivity.this, MainActivity.class);
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


                           if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
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
                            }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerSEUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkSEUser > 1) {
                    SEUserNameEng = SEUser_list.get(i).getUserName();
                    SEUserId = SEUser_list.get(i).getUserId();
                    if (SEUserId.equals("-1")) {

                    } else {

                        SharedPreferences.Editor editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit();
                        editor.putString("UserId", SEUserId);
                        editor.apply();
                        Log.d("ghgfh", "fghgh" + SEUserNameEng + "" + SEUserId);

                    }
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
                            //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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


              /*  SharedPreferences sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE);
                selecteduserid = sh2.getString("UserId", "");
                Log.d("selecteduserid", "" + selecteduserid);*/


                getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee);

            }
        });


        binding.mfExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);


                if (feedbackbyDCListDatumArrayList != null && feedbackbyDCListDatumArrayList.size() > 0) {
                    //           showProgress();

                    ExcelformTable();

                } else {
                    Toast.makeText(mActivity, "No Data Found", Toast.LENGTH_SHORT).show();
                }
                //  ExcelformTable();
            }
        });


    }

    private void getIrisWeighInstallation(String districtId, String fromDate, String toDate, String fpscodee) {

        if (Constants.isNetworkAvailable(mActivity)) {

            hideKeyboard(mActivity);
            binding.progressBar.setVisibility(View.VISIBLE);

            String USER_Id = prefManager.getUSER_Id();


            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);

            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            Call<FeedbackbyDCListRoot> call = apiInterface.getVisitFeedbackDCList(USER_Id, "0", fpscodee, districtId, fromDate, toDate, SEUserId);
            Log.d("fromDate_toDate", "--" + fromDate + " " + toDate);
            Log.d("responseis ", "" + USER_Id + "  " + "0" + fpscodee + "  " + districtId + " " + fromDate + toDate + " " + SEUserId);

            call.enqueue(new Callback<FeedbackbyDCListRoot>() {
                @Override
                public void onResponse(@NonNull Call<FeedbackbyDCListRoot> call, @NonNull Response<FeedbackbyDCListRoot> response) {
                    binding.progressBar.setVisibility(View.GONE);


                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding.txtNoRecord.setVisibility(View.GONE);
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    FeedbackbyDCListRoot installedRoot = response.body();
                                    String message =
                                            installedRoot.getMessage();
                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();

                                   feedbackbyDCListDatumArrayList = installedRoot.getData();

                                    Log.d("weighInsDataArrayList", "Size...." + feedbackbyDCListDatumArrayList.size());
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(FeedbackFormListActivity.this));
                                    mRecyclerView.addItemDecoration(new DividerItemDecoration(FeedbackFormListActivity.this, DividerItemDecoration.VERTICAL));
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    FeedbackListAdapter feedbackListAdapter = new FeedbackListAdapter(feedbackbyDCListDatumArrayList, mContext);
                                    mRecyclerView.setAdapter(feedbackListAdapter);


                                    SharedPreferences sharedPreferences = getSharedPreferences("pm_shared preferences", MODE_PRIVATE);

                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    // creating a new variable for gson.
                                    Gson gson = new Gson();

                                    // getting data from gson and storing it in a string.
                                    String json = gson.toJson(feedbackbyDCListDatumArrayList);

                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("pm_courses", json);

                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply();











                                    // Use 'dataValue' or perform operations with other properties
                                } else {
                                    binding.txtNoRecord.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }

                            } else {
                                binding.txtNoRecord.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                                makeToast(String.valueOf(response.body().getMessage()));
                            }


                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FeedbackbyDCListRoot> call, @NonNull Throwable error) {
                    // hideProgress();
                    binding.progressBar.setVisibility(View.GONE);


                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));

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





    private void SEUsersList(String districtId) {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            binding.progressBar.setVisibility(View.INVISIBLE);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String paramStr = SEUserListByDistictQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], districtId);
            Call<ModelSEUser> call = apiInterface.callSEUserListApiByDistict(SEUserListByDistict(), paramData);
            call.enqueue(new Callback<ModelSEUser>() {
                @Override
                public void onResponse(@NonNull Call<ModelSEUser> call, @NonNull Response<ModelSEUser> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    SEUser_list = response.body().getSEUsersList();
                                    if (SEUser_list != null && SEUser_list.size() > 0) {
                                        Collections.reverse(SEUser_list);
                                        ModelSEUserList l = new ModelSEUserList();
                                        l.setUserId(valueOf(-1));
                                        l.setUserName("--" + getResources().getString(R.string.se_user) + "--");
                                        SEUser_list.add(l);
                                        Collections.reverse(SEUser_list);
                                        ArrayAdapter<ModelSEUserList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, SEUser_list);
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerSEUsers.setAdapter(dataAdapter);
                                        Log.d("userid", "" + selecteduserid);
                                        if (selecteduserid != null && !selecteduserid.isEmpty()) {
                                            Log.d("ghjh", "" + selecteduserid);
                                            int userId = Integer.parseInt(selecteduserid); // replace with the user ID you want to select
                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelSEUserList user = dataAdapter.getItem(i);
                                                if (String.valueOf(user.getUserId()).equals(String.valueOf(userId))) {
                                                    spinnerSEUsers.setSelection(i);
                                                    break;
                                                }
                                            }

                                        }
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
                public void onFailure(@NonNull Call<ModelSEUser> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);

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
            // hideProgress();
            binding.progressBar.setVisibility(View.VISIBLE);

            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            String USER_Id = prefManager.getUSER_Id();
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDistrict_w> call = apiInterface.apiGetDistictList_w();
            call.enqueue(new Callback<ModelDistrict_w>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict_w> call, @NonNull Response<ModelDistrict_w> response) {
                    //  hideProgress();
                    binding.progressBar.setVisibility(View.GONE);
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
                                        // nowbygagan
                                        //    binding.spinnerDistrict.setSelection(Integer.parseInt(value_selectedDis));

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
                    //  hideProgress();
                    binding.progressBar.setVisibility(View.GONE);


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
        binding.spinner.setSelection(3);
        int selectedItemPosition = binding.spinner.getSelectedItemPosition();
        if (selectedItemPosition == 3) {

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
            getIrisWeighInstallation(districtId, fromDate, toDate, fpscodee);


        } else {

        }


        //NowbyGagan
        //  binding.spinner.setSelection(Integer.parseInt(value_selectedDay));


    }


    private static String convertDateFormat(String dateString, String originalFormat, String desiredFormat) {
        SimpleDateFormat originalDateFormat = new SimpleDateFormat(originalFormat, Locale.US);
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat(desiredFormat, Locale.US);

        try {
            // Parse the original date string into a Date object
            Date date = originalDateFormat.parse(dateString);

            // Format the Date object into the desired format
            return desiredDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return an empty string if there's an error in parsing or formatting
        }
    }


    private void ExcelformTable() {

        mActivity = this;
        prefManager = new PrefManager(mActivity);
        ;
        sharedPreferences = getSharedPreferences("pm_shared preferences", MODE_PRIVATE);
        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("pm_courses", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<FeedbackbyDCListDatum>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        feedbackbyDCListDatumArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (feedbackbyDCListDatumArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            feedbackbyDCListDatumArrayList = new ArrayList<>();
            Log.d("nbb", "" + feedbackbyDCListDatumArrayList);
        }
        Log.d("gfhvbb", "" + feedbackbyDCListDatumArrayList);

        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        // mycomment
        String corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat);
        String corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        //  Log.d("formateddate", "" + corStartDate);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Record " + corStartDate + " - " + corEndDate);
        //  HSSFSheet firstSheet = workbook.createSheet("Record " );

        // Create cell style for center alignment

        HSSFRow rowA = firstSheet.createRow(0);
        rowA.setHeightInPoints(20); // Set row height in points
        HSSFCell cellA = rowA.createCell(0);
      /*  cellA.setCellValue(new HSSFRichTextString("District\n name"));
        // Create a bold font
        HSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);
        // Apply the bold font to a new cell style
        HSSFCellStyle boldCellStyle = workbook.createCellStyle();
        boldCellStyle.setFont(boldFont);
        cellA.setCellStyle(boldCellStyle);
        firstSheet.setColumnWidth(0, 3000);
*/
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
        firstSheet.setColumnWidth(1, 4000);
        //   cellB.setCellStyle(cellStyle);
        HSSFCell cellC = rowA.createCell(2);
        firstSheet.setColumnWidth(2, 4000);
        RichTextString richText2 = new HSSFRichTextString("FeedBack\nID");
        richText2.applyFont(boldFont);
        cellC.setCellValue(richText2);
        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        HSSFCell cellD = rowA.createCell(3);
        firstSheet.setColumnWidth(3, 9000);
        RichTextString richText3 = new HSSFRichTextString("Service  \nEngineer");
        richText3.applyFont(boldFont);
        cellD.setCellValue(richText3);
        HSSFCell cellE = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 4000);
        RichTextString richText4 = new HSSFRichTextString("Date");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));

        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        HSSFCell cellF = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 4000);
        RichTextString richText5 = new HSSFRichTextString("LogInTime");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        HSSFCell cellG = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 4000);
        RichTextString richText6 = new HSSFRichTextString("LogOutTime");
        richText6.applyFont(boldFont);
        cellG.setCellValue(richText6);
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        HSSFCell cellH = rowA.createCell(7);
        firstSheet.setColumnWidth(7, 20000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText7 = new HSSFRichTextString("Remark");
        richText7.applyFont(boldFont);
        cellH.setCellValue(richText7);


        HSSFCell cellI = rowA.createCell(8);
        firstSheet.setColumnWidth(8, 20000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText8 = new HSSFRichTextString("Address");
        richText8.applyFont(boldFont);
        cellI.setCellValue(richText8);


        Log.d("mylist", " -------------- " + feedbackbyDCListDatumArrayList);
        if (feedbackbyDCListDatumArrayList != null && feedbackbyDCListDatumArrayList.size() > 0) {
            for (int i = 0; i < feedbackbyDCListDatumArrayList.size(); i++) {
                FeedbackbyDCListDatum detailsInfo = feedbackbyDCListDatumArrayList.get(i);
                String districtName = String.valueOf(detailsInfo.getDistrictName());
                String fps = String.valueOf(detailsInfo.getFpscode());
                String feedbackid = String.valueOf(detailsInfo.getFeedBackId());
                String feedbackby = String.valueOf(detailsInfo.getFeedBackBy());

                String date = detailsInfo.getUpdatedOn().getDayOfMonth() + "-" + detailsInfo.getUpdatedOn().getMonthValue() + "-" + detailsInfo.getUpdatedOn().getYear();

                String Date = String.valueOf(date);
                String in = String.valueOf(detailsInfo.getLogInTime());
                String out = String.valueOf(detailsInfo.getLogOutTime());
                String remark = String.valueOf(detailsInfo.getRemarks());
                String address = String.valueOf(detailsInfo.getLocationAddress());
                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(fps);
                dataRow.createCell(2).setCellValue(feedbackid);
                dataRow.createCell(3).setCellValue(feedbackby);
                dataRow.createCell(4).setCellValue(Date);
                dataRow.createCell(5).setCellValue(in);
                dataRow.createCell(6).setCellValue(out);
                dataRow.createCell(7).setCellValue(remark);
                dataRow.createCell(8).setCellValue(address);
            }
        }


        FileOutputStream fos = null;
        try {
            String str_path = Environment.getExternalStorageDirectory().toString();
            File file;
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
            Toast.makeText(FeedbackFormListActivity.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
        }


      /*  long timeMillis = System.currentTimeMillis();

        // Generate a random number.
        Random random = new Random();
        int randomNumber = random.nextInt(100000);

        // Combine the current date and time with the random number to generate a unique string.
        String fileName = String.format("excel_%d_%d", timeMillis, randomNumber);
        Log.d("fkddv", "fh" + fileName);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


        try {
            startActivityForResult(intent, 1); // Use startActivityForResult to get the selected file's URI
        } catch (ActivityNotFoundException e) {
        }*/


        // i replace top code with this


        try {
            Uri fileUri = FileProvider.getUriForFile(
                    FeedbackFormListActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", // Replace with your app's provider authority
                    filePathh
            );

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel");
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(FeedbackFormListActivity.this, "No app found to open Excel files.", Toast.LENGTH_SHORT).show();
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





  /*  private  String generateUniqueFileName(String originalFileName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileExtension = getFileExtension(originalFileName);
        return "excel_" + timeStamp + (fileExtension.isEmpty() ? "" : "." + fileExtension);
    }

    private  String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return ""; // Return an empty string if no extension is found
    }*/


    public void clearSharePreference() {
        // super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("pm_shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
// Clearing the value associated with the "camp" key
        editor.remove("pm_courses");
// Applying the changes to save the updated SharedPreferences
        editor.apply();
    }


    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        clearSharePreference();

        Intent i = new Intent(FeedbackFormListActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }


}
