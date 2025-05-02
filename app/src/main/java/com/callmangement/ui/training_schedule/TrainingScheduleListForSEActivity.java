package com.callmangement.ui.training_schedule;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.TrainingScheduleListForSEActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityTrainingScheduleListForSeBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.model.training_schedule.ModelTrainingScheduleList;
import com.callmangement.utils.EqualSpacingItemDecoration;
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

public class TrainingScheduleListForSEActivity extends CustomActivity implements View.OnClickListener {
    private ActivityTrainingScheduleListForSeBinding binding;
    private TrainingScheduleListForSEActivityAdapter adapter;
    private TrainingScheduleViewModel viewModel;
    private PrefManager prefManager;
    private int checkTehsil = 0;
    private String tehsilNameEng = "";
    private String tehsilId = "0";
    private List<ModelTehsilList> tehsil_list = new ArrayList<>();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private Spinner spinnerTehsil;
    private EditText inputStartDate;
    private EditText inputEndDate;
    private String startDate = "";
    private String endDate = "";
    private String trainingNumber = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_schedule_list_for_se);
        initView();
    }

    private void initView() {
        onClickListener();
        setUpData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTraining();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpData(){
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(TrainingScheduleViewModel.class);

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.buttonFilter.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.training_schedule));

        adapter = new TrainingScheduleListForSEActivityAdapter(mContext);
        adapter.notifyDataSetChanged();
        binding.rvTrainingSchedule.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvTrainingSchedule.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvTrainingSchedule.setAdapter(adapter);

    }

    private void onClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.actionBar.buttonFilter.setOnClickListener(this);
    }

    private void getTraining(){
        isLoading();
        viewModel.getTraining(prefManager.getUSER_Id(), prefManager.getUSER_DistrictId(),tehsilId,trainingNumber,startDate,endDate).observe(this, modelTrainingSchedule -> {
            isLoading();
            if (modelTrainingSchedule.getList().size() > 0){
                binding.textNoTrainingSchedule.setVisibility(View.GONE);
                binding.rvTrainingSchedule.setVisibility(View.VISIBLE);
                adapter.setData(modelTrainingSchedule.getList());
            } else {
                binding.textNoTrainingSchedule.setVisibility(View.VISIBLE);
                binding.rvTrainingSchedule.setVisibility(View.GONE);
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

    private void tehsilList(){
        showProgress();
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelTehsil> call = service.apiGetTehsilByDistict(prefManager.getUSER_DistrictId());
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
                                spinnerTehsil.setAdapter(dataAdapter);
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

    private void filterDialog() {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_filter_se);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

            spinnerTehsil = dialog.findViewById(R.id.spinnerTehsil);
            inputStartDate = dialog.findViewById(R.id.inputStartDate);
            inputEndDate = dialog.findViewById(R.id.inputEndDate);
            EditText inputTrainingNumber = dialog.findViewById(R.id.inputTrainingNumber);
            Button buttonTrainingFilter = dialog.findViewById(R.id.buttonTrainingFilter);

            tehsilNameEng = "--" + getResources().getString(R.string.tehsil) + "--";

            tehsilList();

            spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

            /*from date*/
            DatePickerDialog.OnDateSetListener dateFromDate = (view, year, monthOfYear, dayOfMonth) -> {
                myCalendarFromDate.set(Calendar.YEAR, year);
                myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFromDate();
            };
            inputStartDate.setOnClickListener(view -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                        .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                        myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                        .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                        myCalendarToDate.get(Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            });
            /*to date*/

            buttonTrainingFilter.setOnClickListener(view -> {
                startDate = inputStartDate.getText().toString().trim();
                endDate = inputEndDate.getText().toString().trim();
                trainingNumber = inputTrainingNumber.getText().toString().trim();
                getTraining();
                dialog.dismiss();
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelFromDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputStartDate.setText(sdf.format(myCalendarFromDate.getTime()));
        inputEndDate.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelToDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputEndDate.setText(sdf.format(myCalendarToDate.getTime()));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back)
            onBackPressed();
        else if (id == R.id.buttonFilter)
            filterDialog();
    }

}