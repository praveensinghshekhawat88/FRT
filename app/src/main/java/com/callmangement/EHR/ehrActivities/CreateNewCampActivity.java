package com.callmangement.EHR.ehrActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.R;
import com.callmangement.databinding.ActivityCreateNewCampBinding;
import com.callmangement.EHR.models.ModelCreateACamp;
import com.callmangement.EHR.models.ModelTehsil;
import com.callmangement.EHR.models.ModelTehsilList;
import com.callmangement.EHR.support.Preference;
import com.callmangement.EHR.support.Utils;
import com.callmangement.utils.PrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewCampActivity extends BaseActivity {

    Activity mActivity;
    private ActivityCreateNewCampBinding binding;

    private int checkTehsil = 0;
    private String tehsilNameEng = "";
    private String tehsilId = "0";

    private List<ModelTehsilList> tehsil_list = new ArrayList<>();

    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final Calendar myCalendarFromDate = Calendar.getInstance();

    PrefManager preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCreateNewCampBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void setUpData() {
        tehsilNameEng = "--" + getResources().getString(R.string.tehsil) + "--";

        Collections.reverse(tehsil_list);
        ModelTehsilList l = new ModelTehsilList();
        l.setTehsilId(String.valueOf(-1));
        l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
        tehsil_list.add(l);
        Collections.reverse(tehsil_list);
        ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, tehsil_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTehsil.setAdapter(dataAdapter);
    }

    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.create_new_camp));
         binding.inputDescription.setText("pos shivir");
        setUpData();
        setClickListener();

        String USER_DISTRICT_Id = preference.getUSER_DistrictId();
        tehsilList(USER_DISTRICT_Id);

    }



    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
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
                                        binding.spinnerTehsil.setAdapter(dataAdapter);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void setClickListener() {

        binding.actionBar.ivBack.setOnClickListener(view ->
        {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();

        });

        binding.actionBar.ivThreeDot.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(mActivity, binding.actionBar.ivThreeDot);
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

//        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (++checkDistrict > 1) {
//                    districtNameEng = district_List.get(i).getDistrictNameEng();
//                    districtId = district_List.get(i).getDistrictId();
//                    if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
//                        tehsil_list.clear();
//                        Collections.reverse(tehsil_list);
//                        ModelTehsilList list = new ModelTehsilList();
//                        list.setTehsilId(String.valueOf(-1));
//                        list.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
//                        tehsil_list.add(list);
//                        Collections.reverse(tehsil_list);
//                        ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, tehsil_list);
//                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        binding.spinnerTehsil.setAdapter(dataAdapter);
//                    } else {
//                        tehsilList(districtId);
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });

        binding.spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkTehsil > 1) {
                    tehsilNameEng = tehsil_list.get(i).getTehsilNameEng();
                    tehsilId = tehsil_list.get(i).getTehsilId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.textStartDateTime.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener dateFromDate = (view1, year, monthOfYear, dayOfMonth) -> {
                myCalendarFromDate.set(Calendar.YEAR, year);
                myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                timePickerFromDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, R.style.DialogThemeTwo,dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

        binding.textEndDateTime.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener dateToDate = (view1, year, monthOfYear, dayOfMonth) -> {
                myCalendarToDate.set(Calendar.YEAR, year);
                myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                timePickerToDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, R.style.DialogThemeTwo,dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        binding.buttonCreateTraining.setOnClickListener(view -> {
            String startDateTime = Objects.requireNonNull(binding.textStartDateTime.getText()).toString().trim();
            String endDateTime = Objects.requireNonNull(binding.textEndDateTime.getText()).toString().trim();
            String address = binding.inputPlace.getText().toString().trim();
            String description = binding.inputDescription.getText().toString().trim();

            if (tehsilNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.tehsil) + "--")) {
                makeToast(getResources().getString(R.string.please_select_tehsil));
            } else if (TextUtils.isEmpty(startDateTime)) {
                makeToast(getResources().getString(R.string.please_select_training_start_date));
            }/* else if (TextUtils.isEmpty(endDateTime)) {
                makeToast(getResources().getString(R.string.please_select_training_end_date));
            }*/ else if (TextUtils.isEmpty(address)) {
                makeToast(getResources().getString(R.string.please_input_training_address));
            } else if (TextUtils.isEmpty(description)) {
                makeToast(getResources().getString(R.string.please_input_training_description));
            } else {

                String USER_Id = preference.getUSER_Id();
                String USER_DISTRICT_Id = preference.getUSER_DistrictId();

                saveTraining(USER_Id, USER_DISTRICT_Id, tehsilId, startDateTime, endDateTime, Utils.convertStringToUTF8(address), Utils.convertStringToUTF8(description));
            }

        });
    }

    private void saveTraining(String UserID, String DistrictID, String TehsilID, String StartedDate, String EndDate, String Address, String Description) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String paramStr = CreateCampQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], DistrictID);
            paramData.put(splitArray[2], TehsilID);
            paramData.put(splitArray[3], Description);
            paramData.put(splitArray[4], StartedDate);
            paramData.put(splitArray[5], EndDate);
            paramData.put(splitArray[6], Address);

            Call<ModelCreateACamp> call = apiInterface.callCreateACampApi(CreateCamp(), paramData);
            call.enqueue(new Callback<ModelCreateACamp>() {
                @Override
                public void onResponse(@NonNull Call<ModelCreateACamp> call, @NonNull Response<ModelCreateACamp> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(response.body().getMessage());
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();
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
                public void onFailure(@NonNull Call<ModelCreateACamp> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    private void updateLabelFromDate(String time) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textStartDateTime.setText(sdf.format(myCalendarFromDate.getTime()) + " " + time);
        Objects.requireNonNull(binding.textEndDateTime.getText()).clear();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelToDate(String time) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textEndDateTime.setText(sdf.format(myCalendarToDate.getTime()) + " " + time);
    }

    private void timePickerFromDate() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        int second = mcurrentTime.get(Calendar.SECOND);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            @SuppressLint("DefaultLocale") String timeStr = String.format("%02d:%02d:%02d", selectedHour, selectedMinute, second);
            updateLabelFromDate(timeStr);
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void timePickerToDate() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        int second = mcurrentTime.get(Calendar.SECOND);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            @SuppressLint("DefaultLocale") String timeStr = String.format("%02d:%02d:%02d", selectedHour, selectedMinute, second);
            updateLabelToDate(timeStr);
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

}