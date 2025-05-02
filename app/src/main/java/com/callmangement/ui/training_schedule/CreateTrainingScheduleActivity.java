package com.callmangement.ui.training_schedule;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityCreateTrainingScheduleBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.model.training_schedule.ModelCreateTrainingSchedule;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTrainingScheduleActivity extends CustomActivity implements View.OnClickListener {
    private ActivityCreateTrainingScheduleBinding binding;
    private TrainingScheduleViewModel viewModel;
    private PrefManager prefManager;
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private int checkTehsil = 0;
    private String tehsilNameEng = "";
    private String tehsilId = "0";
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private List<ModelTehsilList> tehsil_list = new ArrayList<>();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final Calendar myCalendarFromDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_training_schedule);
        initView();
    }

    private void initView() {
        setUpData();
        onClickListener();
        districtList();
    }

    private void setUpData(){
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(TrainingScheduleViewModel.class);
        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        tehsilNameEng = "--"+getResources().getString(R.string.tehsil)+"--";

        Collections.reverse(tehsil_list);
        ModelTehsilList l = new ModelTehsilList();
        l.setTehsilId(String.valueOf(-1));
        l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
        tehsil_list.add(l);
        Collections.reverse(tehsil_list);
        ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, tehsil_list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTehsil.setAdapter(dataAdapter);

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.create_training_schedule));

    }

    private void onClickListener(){
        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    districtId = district_List.get(i).getDistrictId();
                    if (districtNameEng.equalsIgnoreCase("--"+getResources().getString(R.string.district)+"--")) {
                        tehsil_list.clear();
                        Collections.reverse(tehsil_list);
                        ModelTehsilList list = new ModelTehsilList();
                        list.setTehsilId(String.valueOf(-1));
                        list.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
                        tehsil_list.add(list);
                        Collections.reverse(tehsil_list);
                        ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, tehsil_list);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spinnerTehsil.setAdapter(dataAdapter);
                    } else {
                        tehsilList(districtId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        binding.spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(++checkTehsil > 1) {
                    tehsilNameEng = tehsil_list.get(i).getTehsilNameEng();
                    tehsilId = tehsil_list.get(i).getTehsilId();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonCreateTraining.setOnClickListener(this);
        binding.textStartDateTime.setOnClickListener(this);
        binding.textEndDateTime.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelFromDate(String time) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textStartDateTime.setText(sdf.format(myCalendarFromDate.getTime())+" "+time);
        Objects.requireNonNull(binding.textEndDateTime.getText()).clear();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelToDate(String time) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textEndDateTime.setText(sdf.format(myCalendarToDate.getTime())+" "+time);
    }

    private void timePickerFromDate(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        int second = mcurrentTime.get(Calendar.SECOND);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            @SuppressLint("DefaultLocale") String timeStr = String.format("%02d:%02d:%02d",selectedHour,selectedMinute,second);
            updateLabelFromDate(timeStr);
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void timePickerToDate(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        int second = mcurrentTime.get(Calendar.SECOND);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            @SuppressLint("DefaultLocale") String timeStr = String.format("%02d:%02d:%02d",selectedHour,selectedMinute,second);
            updateLabelToDate(timeStr);
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void createTrainingSchedule(){
        String startDateTime = Objects.requireNonNull(binding.textStartDateTime.getText()).toString().trim();
        String endDateTime = Objects.requireNonNull(binding.textEndDateTime.getText()).toString().trim();
        String address = binding.inputPlace.getText().toString().trim();
        String description = binding.inputDescription.getText().toString().trim();
        String title = binding.inputTitle.getText().toString().trim();

        if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")){
            makeToast(getResources().getString(R.string.please_select_district));
        }else if (tehsilNameEng.equalsIgnoreCase("--"+getResources().getString(R.string.tehsil)+"--")){
            makeToast(getResources().getString(R.string.please_select_tehsil));
        }else if (TextUtils.isEmpty(startDateTime)){
            makeToast(getResources().getString(R.string.please_select_training_start_date));
        }else if (TextUtils.isEmpty(endDateTime)){
            makeToast(getResources().getString(R.string.please_select_training_end_date));
        }else if (TextUtils.isEmpty(title)){
            makeToast(getResources().getString(R.string.please_input_training_title));
        }else if (TextUtils.isEmpty(address)){
            makeToast(getResources().getString(R.string.please_input_training_address));
        }else if (TextUtils.isEmpty(description)){
            makeToast(getResources().getString(R.string.please_input_training_description));
        }else {
            isLoading();
            viewModel.saveTraining(prefManager.getUSER_Id(), districtId, tehsilId, startDateTime, endDateTime, Constants.convertStringToUTF8(address), Constants.convertStringToUTF8(description), Constants.convertStringToUTF8(title)).observe(this, modelCreateTrainingSchedule -> {
                isLoading();
                if (modelCreateTrainingSchedule.getStatus().equals("200")) {
                    binding.spinnerDistrict.setSelection(0);
                    binding.spinnerTehsil.setSelection(0);
                    binding.textStartDateTime.getText().clear();
                    binding.textEndDateTime.getText().clear();
                    binding.inputPlace.getText().clear();
                    binding.inputDescription.getText().clear();
                    binding.inputTitle.getText().clear();
                }
            });
        }
    }

    private void districtList(){
        isLoading();
        viewModel.getDistrict().observe(this, modelDistrict -> {
            if (modelDistrict.getStatus().equals("200")){
                isLoading();
                district_List = modelDistrict.getDistrict_List();
                if (district_List != null && district_List.size() > 0) {
                    Collections.reverse(district_List);
                    ModelDistrictList l = new ModelDistrictList();
                    l.setDistrictId(String.valueOf(-1));
                    l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                    district_List.add(l);
                    Collections.reverse(district_List);
                    ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, district_List);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerDistrict.setAdapter(dataAdapter);
                }
            }
        });
    }

    private void tehsilList(String districtId){
        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelTehsil> call = service.apiGetTehsilByDistict(districtId);
        call.enqueue(new Callback<ModelTehsil>() {
            @Override
            public void onResponse(@NonNull Call<ModelTehsil> call, @NonNull Response<ModelTehsil> response) {
                hideProgress();
                if (response.code() == 200){
                    if (response.isSuccessful()){
                        ModelTehsil modelTehsil = response.body();
                        if (Objects.requireNonNull(modelTehsil).getStatus().equals("200")) {
                            tehsil_list = modelTehsil.getTehsil_List();
                            if (tehsil_list != null && tehsil_list.size() > 0) {
                                Collections.reverse(tehsil_list);
                                ModelTehsilList l = new ModelTehsilList();
                                l.setTehsilId(String.valueOf(-1));
                                l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
                                tehsil_list.add(l);
                                Collections.reverse(tehsil_list);
                                ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, tehsil_list);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.spinnerTehsil.setAdapter(dataAdapter);
                            }
                        } else {
                            makeToast(modelTehsil.getMessage());
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ModelTehsil> call, @NonNull Throwable t) {
                hideProgress();
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }

    private void isLoading() {
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.buttonCreateTraining) {
            createTrainingSchedule();
        } else if (id == R.id.textStartDateTime){
            DatePickerDialog.OnDateSetListener dateFromDate = (view1, year, monthOfYear, dayOfMonth) -> {
                myCalendarFromDate.set(Calendar.YEAR, year);
                myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                timePickerFromDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();

        } else if (id == R.id.textEndDateTime){
            DatePickerDialog.OnDateSetListener dateToDate = (view1, year, monthOfYear, dayOfMonth) -> {
                myCalendarToDate.set(Calendar.YEAR, year);
                myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                timePickerToDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
    }
}