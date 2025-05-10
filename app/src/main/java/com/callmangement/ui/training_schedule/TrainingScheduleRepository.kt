package com.callmangement.ui.training_schedule

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.callmangement.R
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.model.training_schedule.ModelCreateTrainingSchedule
import com.callmangement.model.training_schedule.ModelTrainingSchedule
import com.callmangement.model.training_schedule.ModelUpdateTrainingSchedule
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrainingScheduleRepository(private val mContext: Context) {
    private val prefManager = PrefManager(mContext)
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val modelTehsilMutableLiveData = MutableLiveData<ModelTehsil?>()
    private val modelDistrictMutableLiveData = MutableLiveData<ModelDistrict?>()
    private val modelCreateTrainingScheduleMutableLiveData =
        MutableLiveData<ModelCreateTrainingSchedule?>()
    private val modelTrainingScheduleMutableLiveData = MutableLiveData<ModelTrainingSchedule?>()
    private val modelUpdateTrainingScheduleMutableLiveData =
        MutableLiveData<ModelUpdateTrainingSchedule?>()

    fun getModelTehsilMutableLiveData(district_id: String?): MutableLiveData<ModelTehsil?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.apiGetTehsilByDistict(district_id)
        call.enqueue(object : Callback<ModelTehsil?> {
            override fun onResponse(call: Call<ModelTehsil?>, response: Response<ModelTehsil?>) {
                if (response.isSuccessful) {
                    isLoading.value = false
                    val modelTehsil = response.body()
                    if (modelTehsil!!.status == "200") {
                        modelTehsilMutableLiveData.setValue(modelTehsil)
                    } else {
                        Toast.makeText(mContext, modelTehsil.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.value = false
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelTehsil?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelTehsilMutableLiveData
    }

    fun getModelDistrictMutableLiveData(): MutableLiveData<ModelDistrict?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.apiGetDistictList()
        call.enqueue(object : Callback<ModelDistrict?> {
            override fun onResponse(
                call: Call<ModelDistrict?>,
                response: Response<ModelDistrict?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val modelDistrict = response.body()
                    if (modelDistrict!!.status == "200") {
                        modelDistrictMutableLiveData.setValue(modelDistrict)
                    } else {
                        Toast.makeText(mContext, modelDistrict.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelDistrict?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelDistrictMutableLiveData
    }

    fun getModelCreateTrainingScheduleMutableLiveData(
        UserID: String?,
        DistrictID: String?,
        TehsilID: String?,
        StartedDate: String?,
        EndDate: String?,
        Address: String?,
        Description: String?,
        Title: String?
    ): MutableLiveData<ModelCreateTrainingSchedule?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.saveTraining(
            UserID,
            DistrictID,
            TehsilID,
            StartedDate,
            EndDate,
            Address,
            Description,
            Title
        )
        call.enqueue(object : Callback<ModelCreateTrainingSchedule?> {
            override fun onResponse(
                call: Call<ModelCreateTrainingSchedule?>,
                response: Response<ModelCreateTrainingSchedule?>
            ) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val model = response.body()
                    if (model!!.status == "200") {
                        modelCreateTrainingScheduleMutableLiveData.value = model
                    }
                    Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelCreateTrainingSchedule?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelCreateTrainingScheduleMutableLiveData
    }

    fun getModelTrainingScheduleMutableLiveData(
        UserID: String?,
        DistrictID: String?,
        TehsilID: String?,
        TrainingNo: String?,
        StartedDate: String?,
        EndDate: String?
    ): MutableLiveData<ModelTrainingSchedule?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call =
            service.getTrainigList(UserID, DistrictID, TehsilID, TrainingNo, StartedDate, EndDate)
        call.enqueue(object : Callback<ModelTrainingSchedule?> {
            override fun onResponse(
                call: Call<ModelTrainingSchedule?>,
                response: Response<ModelTrainingSchedule?>
            ) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val model = response.body()
                    modelTrainingScheduleMutableLiveData.setValue(model)
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelTrainingSchedule?>, t: Throwable) {
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelTrainingScheduleMutableLiveData
    }

    fun getModelUpdateTrainingScheduleMutableLiveData(
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
    ): MutableLiveData<ModelUpdateTrainingSchedule?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.updateTraining(
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
        call.enqueue(object : Callback<ModelUpdateTrainingSchedule?> {
            override fun onResponse(
                call: Call<ModelUpdateTrainingSchedule?>,
                response: Response<ModelUpdateTrainingSchedule?>
            ) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val model = response.body()
                    if (model!!.status == "200") {
                        modelUpdateTrainingScheduleMutableLiveData.value = model
                    }
                    Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                } else {
                    isLoading.value = false
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelUpdateTrainingSchedule?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelUpdateTrainingScheduleMutableLiveData
    }

    companion object {
        private const val TAG = "TrainingScheduleRepository"
    }
}
