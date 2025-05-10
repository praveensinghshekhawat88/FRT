package com.callmangement.ui.training_schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.model.training_schedule.ModelCreateTrainingSchedule
import com.callmangement.model.training_schedule.ModelTrainingSchedule
import com.callmangement.model.training_schedule.ModelUpdateTrainingSchedule

class TrainingScheduleViewModel(application: Application) : AndroidViewModel(application) {
    var repository: TrainingScheduleRepository =
        TrainingScheduleRepository(application.applicationContext)

    val isLoading: LiveData<Boolean>
        get() = repository.isLoading

    fun getTehsil(district_id: String?): LiveData<ModelTehsil?> {
        return repository.getModelTehsilMutableLiveData(district_id)
    }

    val district: LiveData<ModelDistrict?>
        get() = repository.getModelDistrictMutableLiveData()

    fun saveTraining(
        UserID: String?,
        DistrictID: String?,
        TehsilID: String?,
        StartedDate: String?,
        EndDate: String?,
        Address: String?,
        Description: String?,
        Title: String?
    ): LiveData<ModelCreateTrainingSchedule?> {
        return repository.getModelCreateTrainingScheduleMutableLiveData(
            UserID,
            DistrictID,
            TehsilID,
            StartedDate,
            EndDate,
            Address,
            Description,
            Title
        )
    }

    fun getTraining(
        UserID: String?,
        DistrictID: String?,
        TehsilID: String?,
        TrainingNo: String?,
        StartedDate: String?,
        EndDate: String?
    ): LiveData<ModelTrainingSchedule?> {
        return repository.getModelTrainingScheduleMutableLiveData(
            UserID,
            DistrictID,
            TehsilID,
            TrainingNo,
            StartedDate,
            EndDate
        )
    }

    fun updateTraining(
        UserID: String?,
        DistrictID: String?,
        TehsilID: String?,
        TrainingId: String?,
        StartedDate: String?,
        EndDate: String?,
        TrainingNo: String?,
        Address: String?,
        Description: String?,
        Title: String?
    ): LiveData<ModelUpdateTrainingSchedule?> {
        return repository.getModelUpdateTrainingScheduleMutableLiveData(
            UserID,
            DistrictID,
            TehsilID,
            TrainingId,
            StartedDate,
            EndDate,
            TrainingNo,
            Address,
            Description,
            Title
        )
    }
}
