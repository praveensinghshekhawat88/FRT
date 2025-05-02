package com.callmangement.ui.training_schedule;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.model.district.ModelDistrict;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.training_schedule.ModelCreateTrainingSchedule;
import com.callmangement.model.training_schedule.ModelTrainingSchedule;
import com.callmangement.model.training_schedule.ModelUpdateTrainingSchedule;
import com.callmangement.utils.PrefManager;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingScheduleRepository {
    private static final String TAG = "TrainingScheduleRepository";
    private final Context mContext;
    private final PrefManager prefManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<ModelTehsil> modelTehsilMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelDistrict> modelDistrictMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelCreateTrainingSchedule> modelCreateTrainingScheduleMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelTrainingSchedule> modelTrainingScheduleMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelUpdateTrainingSchedule> modelUpdateTrainingScheduleMutableLiveData = new MutableLiveData<>();

    public TrainingScheduleRepository(Context applicationContext) {
        this.mContext = applicationContext;
        prefManager = new PrefManager(mContext);
    }

    public MutableLiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public MutableLiveData<ModelTehsil> getModelTehsilMutableLiveData(String district_id){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelTehsil> call = service.apiGetTehsilByDistict(district_id);
        call.enqueue(new Callback<ModelTehsil>() {
            @Override
            public void onResponse(@NonNull Call<ModelTehsil> call, @NonNull Response<ModelTehsil> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelTehsil modelTehsil = response.body();
                    if (Objects.requireNonNull(modelTehsil).status.equals("200")){
                        modelTehsilMutableLiveData.setValue(modelTehsil);
                    }else {
                        Toast.makeText(mContext, modelTehsil.message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelTehsil> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelTehsilMutableLiveData;
    }

    public MutableLiveData<ModelDistrict> getModelDistrictMutableLiveData(){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelDistrict> call = service.apiGetDistictList();
        call.enqueue(new Callback<ModelDistrict>() {
            @Override
            public void onResponse(@NonNull Call<ModelDistrict> call, @NonNull Response<ModelDistrict> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelDistrict modelDistrict = response.body();
                    if (Objects.requireNonNull(modelDistrict).status.equals("200")){
                        modelDistrictMutableLiveData.setValue(modelDistrict);
                    }else {
                        Toast.makeText(mContext, modelDistrict.message, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelDistrict> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelDistrictMutableLiveData;
    }

    public MutableLiveData<ModelCreateTrainingSchedule> getModelCreateTrainingScheduleMutableLiveData(String UserID, String DistrictID, String TehsilID, String StartedDate, String EndDate, String Address, String Description, String Title){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelCreateTrainingSchedule> call = service.saveTraining(UserID, DistrictID, TehsilID, StartedDate, EndDate, Address, Description, Title);
        call.enqueue(new Callback<ModelCreateTrainingSchedule>() {
            @Override
            public void onResponse(@NonNull Call<ModelCreateTrainingSchedule> call, @NonNull Response<ModelCreateTrainingSchedule> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    ModelCreateTrainingSchedule model = response.body();
                    if (Objects.requireNonNull(model).status.equals("200")){
                        modelCreateTrainingScheduleMutableLiveData.setValue(model);
                    }
                    Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show();
                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ModelCreateTrainingSchedule> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelCreateTrainingScheduleMutableLiveData;
    }

    public MutableLiveData<ModelTrainingSchedule> getModelTrainingScheduleMutableLiveData(String UserID, String DistrictID, String TehsilID, String TrainingNo, String StartedDate, String EndDate){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelTrainingSchedule> call = service.getTrainigList(UserID, DistrictID, TehsilID, TrainingNo, StartedDate, EndDate);
        call.enqueue(new Callback<ModelTrainingSchedule>() {
            @Override
            public void onResponse(@NonNull Call<ModelTrainingSchedule> call, @NonNull Response<ModelTrainingSchedule> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    ModelTrainingSchedule model = response.body();
                    modelTrainingScheduleMutableLiveData.setValue(model);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ModelTrainingSchedule> call, @NonNull Throwable t) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelTrainingScheduleMutableLiveData;
    }

    public MutableLiveData<ModelUpdateTrainingSchedule> getModelUpdateTrainingScheduleMutableLiveData(String UserID, String DistrictID, String TehsilID, String TrainingId, String StartedDate, String EndDate, String TrainingNo, String Address, String Description, String Title){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelUpdateTrainingSchedule> call = service.updateTraining(UserID, DistrictID, TehsilID, TrainingId, StartedDate, EndDate, TrainingNo, Address, Description, Title);
        call.enqueue(new Callback<ModelUpdateTrainingSchedule>() {
            @Override
            public void onResponse(@NonNull Call<ModelUpdateTrainingSchedule> call, @NonNull Response<ModelUpdateTrainingSchedule> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    ModelUpdateTrainingSchedule model = response.body();
                    if (Objects.requireNonNull(model).status.equals("200")){
                        modelUpdateTrainingScheduleMutableLiveData.setValue(model);
                    }
                    Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show();
                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(@NonNull Call<ModelUpdateTrainingSchedule> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelUpdateTrainingScheduleMutableLiveData;
    }

}
