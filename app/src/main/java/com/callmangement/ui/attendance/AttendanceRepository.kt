package com.callmangement.ui.attendance

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.model.attendance.ModelAddLocationData
import com.callmangement.model.attendance.ModelAddLocationList
import com.callmangement.model.attendance.ModelAttendanceList
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttendanceRepository(private val mContext: Context) {
    private val prefManager = PrefManager(mContext)
    @JvmField
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val mutableLiveData = MutableLiveData<ModelAttendanceList?>()
    private val modelAddLocationListMutableLiveData = MutableLiveData<ModelAddLocationList?>()

    fun getMutableLiveData(
        user_id: String?,
        district_id: String?,
        fromDate: String?,
        toDate: String?
    ): MutableLiveData<ModelAttendanceList?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getAttendanceRecord(user_id, district_id, fromDate, toDate)
        call.enqueue(object : Callback<ModelAttendanceList?> {
            override fun onResponse(
                call: Call<ModelAttendanceList?>,
                response: Response<ModelAttendanceList?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        mutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model!!.message, Toast.LENGTH_SHORT).show()
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

            override fun onFailure(call: Call<ModelAttendanceList?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return mutableLiveData
    }

    fun getModelAddLocationListMutableLiveData(
        user_id: String?,
        district_id: String?,
        date: String
    ): MutableLiveData<ModelAddLocationList?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getLocationRecord(user_id, district_id)
        call.enqueue(object : Callback<ModelAddLocationList?> {
            override fun onResponse(
                call: Call<ModelAddLocationList?>,
                response: Response<ModelAddLocationList?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        val data = model!!.location_list
                        val list: MutableList<ModelAddLocationData> = ArrayList()
                        for (i in data.indices) {
                            if (data[i].location_Date_Time.contains(date)) {
                                list.add(data[i])
                            }
                        }
                        model.location_list = list
                        modelAddLocationListMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model!!.massage, Toast.LENGTH_SHORT).show()
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

            override fun onFailure(call: Call<ModelAddLocationList?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelAddLocationListMutableLiveData
    }

    companion object {
        private const val TAG = "AttendanceRepository"
    }
}
