package com.callmangement.EHR.ehrActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.EHR.adapter.CampListingAdapter;
import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.api.Constants;
import com.callmangement.R;
import com.callmangement.databinding.ActivityCampsListingBinding;
import com.callmangement.EHR.models.CampDetailsInfo;
import com.callmangement.EHR.models.CampDocInfo;
import com.callmangement.EHR.models.DealersInfo;
import com.callmangement.EHR.models.ModelAllDealersByBlock;
import com.callmangement.EHR.models.ModelCampSchedule;
import com.callmangement.EHR.models.ModelDeleteACamp;
import com.callmangement.EHR.models.ModelDistrict;
import com.callmangement.EHR.models.ModelDistrictList;
import com.callmangement.EHR.models.ModelGetCampDocList;
import com.callmangement.EHR.models.ModelOrganiseACamp;
import com.callmangement.EHR.models.ModelTehsil;
import com.callmangement.EHR.models.ModelTehsilList;
import com.callmangement.EHR.support.EqualSpacingItemDecoration;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Utils;
import com.callmangement.utils.PrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.io.Serializable;
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
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CampsListingActivity extends BaseActivity {

    Activity mActivity;
    private ActivityCampsListingBinding binding;

    PrefManager preference;

    private List<CampDetailsInfo> campDetails_infoArrayList = new ArrayList<>();
    private CampListingAdapter adapter;
    public final int LAUNCH_CAMP_CREATION_ACTIVITY = 155;
    private Spinner spinnerDistrict;
    private Spinner spinnerTehsil;
    private EditText inputStartDate;
    private EditText inputEndDate;
    private String startDate = "";
    private String endDate = "";
    private String trainingNumber = "";
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private int checkTehsil = 0;
    private String tehsilNameEng = "";
    private String tehsilId = "0";
    String d2;
    String tehsilvalue_2;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private List<ModelTehsilList> tehsil_list = new ArrayList<>();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    Spinner spinner;
    private int checkFilter = 0;
    LinearLayoutCompat layoutDateRange;
    private final String myFormat = "yyyy-MM-dd";
   // private final String myFormat = "yyyy-MM-dd";

    private Vibrator vibrator;

    SharedPreferences sharedPreferences;


    String originalFileName = "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);

 //File filePathh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), uniqueFileName);

private final File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCampsListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        clearSharePreference();
        mActivity = this;
        preference =  new PrefManager(mActivity);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        SharedPreferences preferences = getSharedPreferences("MyPrefsMyPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("districtId");
        editor.remove("UserId");
        editor.apply();
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.manage_camps));
        campDetails_infoArrayList = new ArrayList<>();
        adapter = new CampListingAdapter(mActivity, campDetails_infoArrayList, onItemOrganiseClickListener,
                onItemUploadClickListener, onItemUploadCampPhotoClickListener, onItemDownloadClickListener, onItemViewClickListener, onItemCompleteClickListener,
                onItemDeleteClickListener);
        binding.rvAllCamps.setHasFixedSize(true);
        binding.rvAllCamps.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        binding.rvAllCamps.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAllCamps.setAdapter(adapter);

        binding.linCreateItem.setVisibility(View.GONE);
        binding.actionBar.buttonFilter.setVisibility(View.GONE);
        binding.buttonprint.setVisibility(View.GONE);
        binding.buttonprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);

                try {
                    Excelform();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

        });



        binding.buttonexcel.setVisibility(View.GONE);
        binding.buttonexcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                ExcelformTable();




            }
        });

String USER_TYPE = preference.getUSER_TYPE();
        String USER_TYPE_ID = preference.getUSER_TYPE_ID();
        if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
            binding.linCreateItem.setVisibility(View.GONE);
            binding.actionBar.buttonFilter.setVisibility(View.VISIBLE);
            binding.buttonprint.setVisibility(View.VISIBLE);
            binding.buttonexcel.setVisibility(View.VISIBLE);

        } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager")) {
            binding.linCreateItem.setVisibility(View.GONE);
            binding.actionBar.buttonFilter.setVisibility(View.VISIBLE);
            binding.buttonprint.setVisibility(View.VISIBLE);
            binding.buttonexcel.setVisibility(View.VISIBLE);


        } else if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")) {
            binding.linCreateItem.setVisibility(View.VISIBLE);
            binding.actionBar.buttonFilter.setVisibility(View.GONE);
            binding.buttonprint.setVisibility(View.VISIBLE);
            binding.buttonexcel.setVisibility(View.VISIBLE);
        }

        setClickListener();

        String USER_DISTRICT_Id = preference.getUSER_DistrictId();

        if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
            USER_DISTRICT_Id = "0";
        } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager")) {
            USER_DISTRICT_Id = "0";
        }
        getTraining(USER_DISTRICT_Id, "0", "", "", "", "0");
    }




    public void clearSharePreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

// Clearing the value associated with the "camp" key
        editor.remove("coursescamp");

// Applying the changes to save the updated SharedPreferences
        editor.apply();

    }
    CampListingAdapter.OnItemUploadCampPhotoClickListener onItemUploadCampPhotoClickListener = new CampListingAdapter.OnItemUploadCampPhotoClickListener() {
        @Override
        public void onItemClick(CampDetailsInfo campDetailsInfo, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.upload_camp_photo_for_camp_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();
                        Intent intent = new Intent(mActivity, UploadCampDailyReportActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("TrainingNo", campDetailsInfo.getTrainingNo());
                        bundle.putString("TrainingId", campDetailsInfo.getTrainingId());
                        bundle.putString("FlagTypeId", "3");
                   //     Log.d("two", campDetailsInfo.getTrainingNo() +" "  +campDetailsInfo.getTrainingId());
                        intent.putExtras(bundle);
                        startActivity(intent);

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    };

    private void filterDialog() {
        try {
            final Dialog dialog = new Dialog(mActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_filter_camp);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);
            spinner = dialog.findViewById(R.id.spinner);
            layoutDateRange = dialog.findViewById(R.id.layout_date_range);
            spinnerDistrict = dialog.findViewById(R.id.spinnerDistrict);
            spinnerTehsil = dialog.findViewById(R.id.spinnerTehsil);
            inputStartDate = dialog.findViewById(R.id.inputStartDate);
            inputEndDate = dialog.findViewById(R.id.inputEndDate);
            EditText inputTrainingNumber = dialog.findViewById(R.id.inputTrainingNumber);
            Button buttonTrainingFilter = dialog.findViewById(R.id.buttonTrainingFilter);
            districtNameEng = "--" + getResources().getString(R.string.district) + "--";
            tehsilNameEng = "--" + getResources().getString(R.string.tehsil) + "--";
            districtList();
            Collections.reverse(tehsil_list);
            ModelTehsilList l = new ModelTehsilList();
            l.setTehsilId(String.valueOf(-1));
            l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
            tehsil_list.add(l);
            Collections.reverse(tehsil_list);
            ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, tehsil_list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTehsil.setAdapter(dataAdapter);
            List<String> spinnerList = new ArrayList<>();
            spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
            spinnerList.add(getResources().getString(R.string.today));
            spinnerList.add(getResources().getString(R.string.yesterday));
            spinnerList.add(getResources().getString(R.string.current_month));
            spinnerList.add(getResources().getString(R.string.previous_month));
            spinnerList.add(getResources().getString(R.string.custom_filter));
            ArrayAdapter<String> dataAdapterr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
            dataAdapterr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapterr);
            spinner.setSelection(1);
            String selectedString = (String) spinner.getSelectedItem();
            int selectedItemPosition = spinner.getSelectedItemPosition();
            if (selectedItemPosition==1)
            {
                layoutDateRange.setVisibility(View.GONE);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String todayDate = sdf.format(today);
                startDate = todayDate;
                endDate = todayDate;

            }
            else{

            }
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (++checkFilter > 1) {
                        String item = adapterView.getItemAtPosition(i).toString();
                        if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                            Objects.requireNonNull(inputStartDate.getText()).clear();
                            Objects.requireNonNull(inputEndDate.getText()).clear();
                            if (item.equalsIgnoreCase(getResources().getString(R.string.today))) {
                                layoutDateRange.setVisibility(View.GONE);
                                Calendar calendar = Calendar.getInstance();
                                Date today = calendar.getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                String todayDate = sdf.format(today);
                                startDate = todayDate;
                                endDate = todayDate;
                                //  getAttendanceList(USER_Id,startDate,endDate);
                            } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                                layoutDateRange.setVisibility(View.GONE);
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DAY_OF_YEAR, -1);
                                Date yesterday = calendar.getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                String yesterdayDate = sdf.format(yesterday);
                                startDate = yesterdayDate;
                                endDate = yesterdayDate;
                                // getReportList(districtId,startDate,endDate);
                                //  getAttendanceList(USER_Id,startDate,endDate);


                            } else if (item.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                                layoutDateRange.setVisibility(View.GONE);
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

                                startDate = date1;
                                endDate = date2;
                                //   getAttendanceList(USER_Id,startDate,endDate);
                                //   getReportList(districtId,startDate,endDate);

                            } else if (item.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                                layoutDateRange.setVisibility(View.GONE);
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                Calendar aCalendar = Calendar.getInstance();
                                aCalendar.add(Calendar.MONTH, -1);
                                aCalendar.set(Calendar.DATE, 1);
                                Date firstDateOfPreviousMonth = aCalendar.getTime();
                                String date1 = sdf.format(firstDateOfPreviousMonth);
                                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                                Date lastDateOfPreviousMonth = aCalendar.getTime();
                                String date2 = sdf.format(lastDateOfPreviousMonth);
                                startDate = date1;
                                endDate = date2;
                                //  getAttendanceList(USER_Id,startDate,endDate);
                                //  getReportList(districtId,startDate,endDate);

                            } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                                startDate = "";
                                endDate = "";
                                layoutDateRange.setVisibility(View.VISIBLE);


                            }
                        } else {
                            startDate = "";
                            endDate = "";
                            Objects.requireNonNull(inputStartDate.getText()).clear();
                            Objects.requireNonNull(inputEndDate.getText()).clear();
                            layoutDateRange.setVisibility(View.GONE);
                            // getAttendanceList(USER_Id,startDate,endDate);

                            // getReportList(districtId,startDate,endDate);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

           spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (++checkDistrict > 1) {
                        districtNameEng = district_List.get(i).getDistrictNameEng();
                        districtId = district_List.get(i).getDistrictId();
                        SharedPreferences.Editor editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit();
                        editor.putString("districtId", districtId);
                        editor.apply();
                        if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                            tehsil_list.clear();
                            Collections.reverse(tehsil_list);
                            ModelTehsilList list = new ModelTehsilList();
                            list.setTehsilId(String.valueOf(-1));
                            list.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
                            tehsil_list.add(list);
                            Collections.reverse(tehsil_list);
                            ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, tehsil_list);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerTehsil.setAdapter(dataAdapter);
                        } else {
                            tehsilList(districtId);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (++checkTehsil > 1) {
                        tehsilNameEng = tehsil_list.get(i).getTehsilNameEng();
                        tehsilId = tehsil_list.get(i).getTehsilId();

                        if (tehsilId.equals("-1"))
                        {

                        }
                        else
                        {
                            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit();
                            editor.putString("UserId", tehsilId);
                            editor.apply();
                        //    Log.d("ghgfh", "fghgh"+tehsilNameEng +tehsilId);
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            /*from date*/
            DatePickerDialog.OnDateSetListener dateFromDate = (view, year, monthOfYear, dayOfMonth) -> {
                myCalendarFromDate.set(Calendar.YEAR, year);
                myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFromDate();
            };
            inputStartDate.setOnClickListener(view -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,  R.style.DialogThemeTwo,dateFromDate, myCalendarFromDate
                        .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                        myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                long minDateInMillis = myCalendarToDate.getTimeInMillis();
                datePickerDialog.getDatePicker().setMaxDate(minDateInMillis);
                datePickerDialog.show();
            });
            /*from date*/

            /*to date*/
            DatePickerDialog.OnDateSetListener dateToDate = (view, year, monthOfYear, dayOfMonth) -> {
                myCalendarToDate.set(Calendar.YEAR, year);
                myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelToDate();
            };
            inputEndDate.setOnClickListener(view -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,  R.style.DialogThemeTwo,dateToDate, myCalendarToDate
                        .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                        myCalendarToDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                long minDateInMillis = myCalendarFromDate.getTimeInMillis();
                datePickerDialog.getDatePicker().setMinDate(minDateInMillis);
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            });
            /*to date*/

            buttonTrainingFilter.setOnClickListener(view -> {
              //  startDate = inputStartDate.getText().toString().trim();
              //  endDate = inputEndDate.getText().toString().trim();
              //  Log.d("startDate_endDate", startDate + endDate);
                trainingNumber = inputTrainingNumber.getText().toString().trim();
                if(layoutDateRange.getVisibility()==View.VISIBLE &&startDate.isEmpty())
                {
                    Toast.makeText(mActivity, "Select Start date", Toast.LENGTH_SHORT).show();

                    // inputStartDate.setError("Error message");
                    // ((TextView)spinner.getSelectedView()).setError("Error message");

                }
                else if(layoutDateRange.getVisibility()==View.VISIBLE &&endDate.isEmpty())
                {
                    Toast.makeText(mActivity, "Select End date", Toast.LENGTH_SHORT).show();

                    //inputEndDate.setError("Error message");

                }
                else {
                    getTraining(districtId, tehsilId, trainingNumber, startDate, endDate, "0");
                    dialog.dismiss();


                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelFromDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputStartDate.setText(sdf.format(myCalendarFromDate.getTime()));
        //startDate = inputStartDate.getText().toString().trim();
        String  calstartDate = inputStartDate.getText().toString().trim();
        String originalFormat =  "dd-MM-yyyy";
        String desiredFormat = "yyyy-MM-dd";
        startDate = convertDateFormat(calstartDate, originalFormat, desiredFormat);
       // Log.d("formateddate", startDate);
        // inputEndDate.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelToDate() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputEndDate.setText(sdf.format(myCalendarToDate.getTime()));
      //  endDate = inputEndDate.getText().toString().trim();
        String   calendDate = inputEndDate.getText().toString().trim();
        String originalFormat =  "dd-MM-yyyy";
        String desiredFormat = "yyyy-MM-dd";
        endDate = convertDateFormat(calendDate, originalFormat, desiredFormat);
      //  Log.d("formateddate", startDate);


    }

    private void tehsilList(String districtId) {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String paramStr = TehsilListByDistictQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], districtId);
            Call<ModelTehsil> call = apiInterface.callTehsilListApiByDistict(TehsilListByDistict(), paramData);
            call.enqueue(new Callback<ModelTehsil>() {
                @Override
                public void onResponse(@NonNull Call<ModelTehsil> call, @NonNull Response<ModelTehsil> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    tehsil_list = response.body().getTehsil_List();
                                    if (tehsil_list != null && tehsil_list.size() > 0) {
                                        Collections.reverse(tehsil_list);
                                        ModelTehsilList l = new ModelTehsilList();
                                        l.setTehsilId(String.valueOf(-1));
                                        l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
                                        tehsil_list.add(l);
                                        Collections.reverse(tehsil_list);
                                        ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, tehsil_list);
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerTehsil.setAdapter(dataAdapter);
                                        if(tehsilvalue_2!=null&&!tehsilvalue_2.isEmpty())
                                        {
                                          //  Log.d("ghjh", tehsilvalue_2);
                                            int userId = Integer.parseInt(tehsilvalue_2); // replace with the user ID you want to select
                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelTehsilList user = dataAdapter.getItem(i);
                                                if (String.valueOf(user.getTehsilId()).equals(String.valueOf(userId))) {
                                                    spinnerTehsil.setSelection(i);
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
                public void onFailure(@NonNull Call<ModelTehsil> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void districtList() {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            Call<ModelDistrict> call = apiInterface.callDistrictListApi(DistictList());
            call.enqueue(new Callback<ModelDistrict>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict> call, @NonNull Response<ModelDistrict> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    district_List = response.body().getDistrict_List();
                                    if (district_List != null && district_List.size() > 0) {
                                        Collections.reverse(district_List);
                                        ModelDistrictList l = new ModelDistrictList();
                                        l.setDistrictId(String.valueOf(-1));
                                        l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                                        district_List.add(l);
                                        Collections.reverse(district_List);
                                        ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, district_List);
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerDistrict.setAdapter(dataAdapter);
/*
                                    if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));

                                        }*/

                                        if(d2!=null&& !d2.isEmpty())
                                        {
                                          //  Log.d("ghjh", d2);
                                            int userId = Integer.parseInt(d2); // replace with the user ID you want to select
                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelDistrictList user = dataAdapter.getItem(i);
                                                if (String.valueOf(user.getDistrictId()).equals(String.valueOf(userId))) {
                                                    spinnerDistrict.setSelection(i);
                                                    break;
                                                }
                                            }


                                        }
                                        else {
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
                public void onFailure(@NonNull Call<ModelDistrict> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    CampListingAdapter.OnItemDeleteClickListener onItemDeleteClickListener = new CampListingAdapter.OnItemDeleteClickListener() {
        @Override
        public void onItemClick(CampDetailsInfo campDetailsInfo, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.delete_camp_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        String USER_Id = preference.getUSER_Id();
                        deleteACamp(USER_Id, campDetailsInfo.getTrainingId(), campDetailsInfo.getTrainingNo());

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    };

    CampListingAdapter.OnItemCompleteClickListener onItemCompleteClickListener = new CampListingAdapter.OnItemCompleteClickListener() {
        @Override
        public void onItemClick(CampDetailsInfo campDetailsInfo, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.complete_camp_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        String USER_Id = preference.getUSER_Id();
                        completeACamp(USER_Id, campDetailsInfo.getTrainingId(), campDetailsInfo.getTrainingNo(), Constants.CAMP_STATUS_ID_COMPLETED);

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    };

    CampListingAdapter.OnItemViewClickListener onItemViewClickListener = new CampListingAdapter.OnItemViewClickListener() {
        @Override
        public void onItemClick(CampDetailsInfo campDetailsInfo, int position) {

            String USER_Id = preference.getUSER_Id();
            viewUploadedImages(USER_Id, campDetailsInfo);
        }
    };

    CampListingAdapter.OnItemUploadClickListener onItemUploadClickListener = new CampListingAdapter.OnItemUploadClickListener() {
        @Override
        public void onItemClick(CampDetailsInfo campDetailsInfo, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.upload_daily_work_sheet_for_camp_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        Intent intent = new Intent(mActivity, UploadCampDailyReportActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("TrainingNo", campDetailsInfo.getTrainingNo());
                        bundle.putString("TrainingId", campDetailsInfo.getTrainingId());
                        bundle.putString("FlagTypeId", "1");
                    //    Log.d("one", campDetailsInfo.getTrainingNo() +" "  +campDetailsInfo.getTrainingId());
                        intent.putExtras(bundle);
                        startActivity(intent);

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    };

    CampListingAdapter.OnItemDownloadClickListener onItemDownloadClickListener = new CampListingAdapter.OnItemDownloadClickListener() {
        @Override
        public void onItemClick(CampDetailsInfo campDetailsInfo, int position) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.download_daily_work_sheet_for_camp_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        String USER_Id = preference.getUSER_Id();
                        downloadDealersAndCreatePDF(USER_Id, campDetailsInfo.getDistrictID(), campDetailsInfo.getTehsilID(), campDetailsInfo.getBlockId(), campDetailsInfo.getTrainingNo());

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();

        }
    };

    private void downloadDealersAndCreatePDF(String UserID, String DistrictID, String TehsilID, String BlockId, String TrainingNo) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = GetDealersByBlockQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], DistrictID);
            paramData.put(splitArray[2], TehsilID);
            paramData.put(splitArray[3], BlockId);

            Call<ModelAllDealersByBlock> call = apiInterface.callGetDealersByBlockApi(GetDealersByBlock(), paramData);
            call.enqueue(new Callback<ModelAllDealersByBlock>() {
                @Override
                public void onResponse(@NonNull Call<ModelAllDealersByBlock> call, @NonNull Response<ModelAllDealersByBlock> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    if (response.body().getList() != null && response.body().getList().size() > 0) {

                                        Intent intent = new Intent(mActivity, CampDailyWorkReportPdfActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("mylist",(Serializable)  response.body().getList());
                                        bundle.putString("TrainingNo", TrainingNo);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    } else {

                                        ArrayList<DealersInfo> list = new ArrayList<>();

                                        Intent intent = new Intent(mActivity, CampDailyWorkReportPdfActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("mylist", (Serializable) list);
                                        bundle.putString("TrainingNo", TrainingNo);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
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
                public void onFailure(@NonNull Call<ModelAllDealersByBlock> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void viewUploadedImages(String UserID, CampDetailsInfo campDetailsInfo) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = GetCampDocListQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], campDetailsInfo.getTrainingId());

            Call<ModelGetCampDocList> call = apiInterface.callGetCampDocListApi(GetCampDocList(), paramData);
            call.enqueue(new Callback<ModelGetCampDocList>() {
                @Override
                public void onResponse(@NonNull Call<ModelGetCampDocList> call, @NonNull Response<ModelGetCampDocList> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    ArrayList<CampDocInfo> campDocInfoArrayList = new ArrayList<>();

                                    if (response.body().getList() != null && response.body().getList().size() > 0) {

                                        for (int i = 0; i < response.body().getList().size(); i++) {

                                            CampDocInfo campDocInfo = response.body().getList().get(i);

                                            String imagePath = BaseUrl() + campDocInfo.getDocumentPath();
                                            campDocInfo.setDocumentPath(imagePath);

                                            campDocInfoArrayList.add(campDocInfo);
                                        }

                                        Intent intent = new Intent(mActivity, ViewCampDetailsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("mylist", (Serializable)campDocInfoArrayList);
                                        bundle.putSerializable("campDetails",(Serializable) campDetailsInfo);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    } else {

                                        Intent intent = new Intent(mActivity, ViewCampDetailsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("mylist", (Serializable)campDocInfoArrayList);
                                        bundle.putSerializable("campDetails",(Serializable) campDetailsInfo);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

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
                public void onFailure(@NonNull Call<ModelGetCampDocList> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    CampListingAdapter.OnItemOrganiseClickListener onItemOrganiseClickListener = new CampListingAdapter.OnItemOrganiseClickListener() {
        @Override
        public void onItemClick(CampDetailsInfo campDetailsInfo, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.organise_camp_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        String USER_Id = preference.getUSER_Id();
                        organiseACamp(USER_Id, campDetailsInfo.getTrainingId(), campDetailsInfo.getTrainingNo(), Constants.CAMP_STATUS_ID_ORGANISED);

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    };

    private void organiseACamp(String UserID, String TrainingId, String TrainingNo, String TStatusId) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = OrganiseACampQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], TrainingId);
            paramData.put(splitArray[2], TrainingNo);
            paramData.put(splitArray[3], TStatusId);
            Call<ModelOrganiseACamp> call = apiInterface.callOrganiseACampApi(OrganiseACamp(), paramData);
            call.enqueue(new Callback<ModelOrganiseACamp>() {
                @Override
                public void onResponse(@NonNull Call<ModelOrganiseACamp> call, @NonNull Response<ModelOrganiseACamp> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(response.body().getMessage());

                                    campDetails_infoArrayList = new ArrayList<>();
                                    adapter.setData(campDetails_infoArrayList);

                                    String USER_DISTRICT_Id = preference.getUSER_DistrictId();

                                    String USER_TYPE = preference.getUSER_TYPE();
                                    String USER_TYPE_ID = preference.getUSER_TYPE_ID();
                                    if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
                                        USER_DISTRICT_Id = "0";
                                    } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager")) {
                                        USER_DISTRICT_Id = "0";
                                    }

                                    getTraining(USER_DISTRICT_Id, "0", "", "", "", "0");

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
                public void onFailure(@NonNull Call<ModelOrganiseACamp> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void completeACamp(String UserID, String TrainingId, String TrainingNo, String TStatusId) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = OrganiseACampQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], TrainingId);
            paramData.put(splitArray[2], TrainingNo);
            paramData.put(splitArray[3], TStatusId);

            Call<ModelOrganiseACamp> call = apiInterface.callOrganiseACampApi(OrganiseACamp(), paramData);
            call.enqueue(new Callback<ModelOrganiseACamp>() {
                @Override
                public void onResponse(@NonNull Call<ModelOrganiseACamp> call, @NonNull Response<ModelOrganiseACamp> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(response.body().getMessage());

                                    campDetails_infoArrayList = new ArrayList<>();
                                    adapter.setData(campDetails_infoArrayList);

                                    String USER_DISTRICT_Id = preference.getUSER_DistrictId();

                                    String USER_TYPE = preference.getUSER_TYPE();
                                    String USER_TYPE_ID = preference.getUSER_TYPE_ID();
                                    if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
                                        USER_DISTRICT_Id = "0";
                                    } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager")) {
                                        USER_DISTRICT_Id = "0";
                                    }

                                    getTraining(USER_DISTRICT_Id, "0", "", "", "", "0");

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
                public void onFailure(@NonNull Call<ModelOrganiseACamp> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void deleteACamp(String UserID, String TrainingId, String TrainingNo) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = DeleteACampQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], TrainingId);
            paramData.put(splitArray[2], TrainingNo);

            Call<ModelDeleteACamp> call = apiInterface.callDeleteACampApi(DeleteACamp(), paramData);
            call.enqueue(new Callback<ModelDeleteACamp>() {
                @Override
                public void onResponse(@NonNull Call<ModelDeleteACamp> call, @NonNull Response<ModelDeleteACamp> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(response.body().getMessage());

                                    campDetails_infoArrayList = new ArrayList<>();
                                    adapter.setData(campDetails_infoArrayList);


                                    String USER_DISTRICT_Id = preference.getUSER_DistrictId();

                                    String USER_TYPE = preference.getUSER_TYPE();
                                    String USER_TYPE_ID = preference.getUSER_TYPE_ID();
                                    if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
                                        USER_DISTRICT_Id = "0";
                                    } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager")) {
                                        USER_DISTRICT_Id = "0";
                                    }

                                    getTraining(USER_DISTRICT_Id, "0", "", "", "", "0");

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
                public void onFailure(@NonNull Call<ModelDeleteACamp> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void getTraining(final String DistrictID, final String TehsilID, final String TrainingNo, final String StartedDate, final String EndDate, final String TStatusId) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String USER_Id = preference.getUSER_Id();
            String paramStr = GetCampListQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], USER_Id);
            paramData.put(splitArray[1], DistrictID);
            paramData.put(splitArray[2], TehsilID);
            paramData.put(splitArray[3], TrainingNo);
            paramData.put(splitArray[4], StartedDate);
            paramData.put(splitArray[5], EndDate);
            paramData.put(splitArray[6], TStatusId);
            Call<ModelCampSchedule> call = apiInterface.callCampListApi(GetCampList(), paramData);
            call.enqueue(new Callback<ModelCampSchedule>() {
                @Override
                public void onResponse(@NonNull Call<ModelCampSchedule> call, @NonNull Response<ModelCampSchedule> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    if (response.body().getList().size() > 0) {
                                        binding.textNoCampSchedule.setVisibility(View.GONE);
                                        binding.rvAllCamps.setVisibility(View.VISIBLE);
                                        adapter.setData(response.body().getList());
                                    } else {
                                        binding.textNoCampSchedule.setVisibility(View.VISIBLE);
                                        binding.rvAllCamps.setVisibility(View.GONE);
                                    }
                                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    // creating a new variable for gson.
                                    Gson gson = new Gson();
                                    // getting data from gson and storing it in a string.
                                    String json = gson.toJson(  response.body().getList());
                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("coursescamp", json);
                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply();
                                    // after saving data we are displaying a toast message.
                                 //   Toast.makeText(CampsListingActivity.this, "Saved Camp Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();

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
                public void onFailure(@NonNull Call<ModelCampSchedule> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                      /* SharedPreferences.Editor editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit();
                                                            editor.putString("districtId", districtId);
                                                            editor.clear();
                                                            editor.commit();
*/

                                                       /*   SharedPreferences preferences = getSharedPreferences("MyPrefsMyPrefs", Activity.MODE_PRIVATE);
                                                           android.content.SharedPreferences.Editor editor = preferences.edit();
                                                         editor.remove("districtId");
                                                           editor.apply();*/
                                                            Intent intent = new Intent(mActivity, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
        binding.actionBar.buttonFilter.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Utils.hideCustomProgressDialogCommonForAll();

                SharedPreferences sh = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE);
                d2= sh.getString("districtId", "");
                Log.d("kfhhf", d2);
                SharedPreferences sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE);
                tehsilvalue_2= sh2.getString("UserId", "");
                Log.d("d2", d2);
                Log.d("selecteduserid", tehsilId);
                filterDialog();
            }
        });




        binding.actionBar.ivThreeDot.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(CampsListingActivity.this, binding.actionBar.ivThreeDot);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(getResources().getString(R.string.do_you_want_to_logout_from_this_app))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.logout), (dialog, id) -> {
                            dialog.cancel();
                            //logout();
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
                return true;
            });
            popupMenu.show();
        });

        binding.buttonCreateNewCamp.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, CreateNewCampActivity.class);
            startActivityForResult(intent, LAUNCH_CAMP_CREATION_ACTIVITY);
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearSharePreference();
        Intent intent = new Intent(mActivity, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_CAMP_CREATION_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                campDetails_infoArrayList = new ArrayList<>();
                adapter.setData(campDetails_infoArrayList);
                String USER_DISTRICT_Id = preference.getUSER_DistrictId();
                String USER_TYPE = preference.getUSER_TYPE();
                String USER_TYPE_ID = preference.getUSER_TYPE_ID();
                if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
                    USER_DISTRICT_Id = "0";
                } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager")) {
                    USER_DISTRICT_Id = "0";
                }
                getTraining(USER_DISTRICT_Id, "0", "", "", "", "0");
            }
        }



        else   if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

           Uri fileUri = data.getData();
            if (fileUri != null) {
                Intent wpsIntent = new Intent(Intent.ACTION_VIEW);
                wpsIntent.setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                wpsIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(wpsIntent);
                } catch (ActivityNotFoundException e) {
                    // Handle the case where WPS Office or a compatible app is not installed
                    Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=cn.wps.moffice_eng"));
                    startActivity(playStoreIntent);
                }
            }




        }

    }

    private void Excelform() throws ParseException {
      /*  String corFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(corFormat, Locale.getDefault());
        String corStartDate = sdf.format(startDate);
        String corEndDate = sdf.format(endDate);
*/
        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd/MM/yyyy";
        String corStartDate = convertDateFormat(startDate, originalFormat, desiredFormat);
        String corEndDate = convertDateFormat(endDate, originalFormat, desiredFormat);
        Log.d("formateddate", corStartDate);
        Intent intent = new Intent(mActivity, ExcelforCamp.class);
        intent.putExtra("startDateKey", corStartDate);
        intent.putExtra("endDateKey", corEndDate);
        startActivity(intent);
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
            return ""; // Return an empty str
            // ing if there's an error in parsing or formatting
        }
    }







    private void ExcelformTable() {


        mActivity = this;
        preference =  new PrefManager(mActivity);


        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("coursescamp", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<CampDetailsInfo>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        campDetails_infoArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (campDetails_infoArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            campDetails_infoArrayList = new ArrayList<>();
            Log.d("nbb",""+campDetails_infoArrayList);

        }
        Log.d("gfhvbb",""+campDetails_infoArrayList);
        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        String corStartDate = convertDateFormat(startDate, originalFormat, desiredFormat);
        String corEndDate = convertDateFormat(endDate, originalFormat, desiredFormat);
        Log.d("formateddate", corStartDate);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet;
        if(corStartDate.isEmpty() && corStartDate.isEmpty()){
             firstSheet = workbook.createSheet("AllRecord");
        }
        else
        {
             firstSheet = workbook.createSheet("Record "+corStartDate+" - "+corEndDate);
        }
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
        RichTextString richText = new HSSFRichTextString("DistrictName");
        richText.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellA.setCellValue(richText);
        firstSheet.setColumnWidth(0, 4000);
        HSSFCell cellB = rowA.createCell(1);
        RichTextString richText1 = new HSSFRichTextString("BlockName");
        richText1.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1);
        firstSheet.setColumnWidth(1, 4000);
        //   cellB.setCellStyle(cellStyle);
        HSSFCell cellC = rowA.createCell(2);
        firstSheet.setColumnWidth(2, 4000);
        RichTextString richText2 = new HSSFRichTextString("StartDate");
        richText2.applyFont(boldFont);
        cellC.setCellValue(richText2);

        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));

        HSSFCell cellD = rowA.createCell(3);
        firstSheet.setColumnWidth(3, 4000);
        RichTextString richText3 = new HSSFRichTextString("EndDate");
        richText3.applyFont(boldFont);
        cellD.setCellValue(richText3);

        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));

        HSSFCell cellE = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 4000);
        RichTextString richText4 = new HSSFRichTextString("Status");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);



        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));

        HSSFCell cellF = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 20000);
        RichTextString richText5 = new HSSFRichTextString("Address");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);

        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);



        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);





        Log.d("mylist"," -------------- "+campDetails_infoArrayList);

        if(campDetails_infoArrayList != null && campDetails_infoArrayList.size() > 0) {
            for (int i = 0; i < campDetails_infoArrayList.size(); i++) {



                CampDetailsInfo detailsInfo = campDetails_infoArrayList.get(i);

                String districtName = String.valueOf(detailsInfo.getDistrictNameEng());
                String attendanceValue = String.valueOf(detailsInfo.getBlockName());
                String date = String.valueOf(detailsInfo.getStartDate());
                String day = String.valueOf(detailsInfo.getEndDate());
                String intime = String.valueOf(detailsInfo.getStatus());
                String outtime = String.valueOf(detailsInfo.getAddress());
              //  String inadd = String.valueOf(detailsInfo.getAddress_In());
              //  String outadd = String.valueOf(detailsInfo.getAddress_Out());

                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data

                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName);

                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(attendanceValue);
                dataRow.createCell(2).setCellValue(date);
                dataRow.createCell(3).setCellValue(day);
                dataRow.createCell(4).setCellValue(intime);
                dataRow.createCell(5).setCellValue(outtime);
              //  dataRow.createCell(6).setCellValue(inadd);
              //  dataRow.createCell(7).setCellValue(outadd);






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
            Toast.makeText(CampsListingActivity.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
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






}