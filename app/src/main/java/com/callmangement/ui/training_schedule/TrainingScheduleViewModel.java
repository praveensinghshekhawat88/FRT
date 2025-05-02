package com.callmangement.ui.training_schedule;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.callmangement.model.district.ModelDistrict;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.training_schedule.ModelCreateTrainingSchedule;
import com.callmangement.model.training_schedule.ModelTrainingSchedule;
import com.callmangement.model.training_schedule.ModelUpdateTrainingSchedule;

public class TrainingScheduleViewModel extends AndroidViewModel {
    TrainingScheduleRepository repository;

    public TrainingScheduleViewModel(@NonNull Application application) {
        super(application);
        this.repository = new TrainingScheduleRepository(application.getApplicationContext());
    }

    public LiveData<Boolean> getIsLoading(){
        return repository.getIsLoading();
    }

    public LiveData<ModelTehsil> getTehsil(String district_id){
        return repository.getModelTehsilMutableLiveData(district_id);
    }

    public LiveData<ModelDistrict> getDistrict(){
        return repository.getModelDistrictMutableLiveData();
    }

    public LiveData<ModelCreateTrainingSchedule> saveTraining(String UserID, String DistrictID, String TehsilID, String StartedDate, String EndDate, String Address, String Description, String Title){
        return repository.getModelCreateTrainingScheduleMutableLiveData(UserID, DistrictID, TehsilID, StartedDate, EndDate, Address, Description, Title);
    }

    public LiveData<ModelTrainingSchedule> getTraining(String UserID, String DistrictID, String TehsilID, String TrainingNo, String StartedDate, String EndDate){
        return repository.getModelTrainingScheduleMutableLiveData(UserID, DistrictID, TehsilID, TrainingNo, StartedDate, EndDate);
    }

    public LiveData<ModelUpdateTrainingSchedule> updateTraining(String UserID, String DistrictID, String TehsilID, String TrainingId, String StartedDate, String EndDate, String TrainingNo, String Address, String Description, String Title){
        return repository.getModelUpdateTrainingScheduleMutableLiveData(UserID, DistrictID, TehsilID, TrainingId, StartedDate, EndDate, TrainingNo, Address, Description, Title);
    }

}
