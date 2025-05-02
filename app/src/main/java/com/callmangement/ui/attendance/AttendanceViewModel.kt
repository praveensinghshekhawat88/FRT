package com.callmangement.ui.attendance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.callmangement.model.attendance.ModelAddLocationList
import com.callmangement.model.attendance.ModelAttendanceList

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AttendanceRepository(application.applicationContext)

    val isLoading: LiveData<Boolean?>?
        get() = repository.isLoading

    fun getAttendanceList(
        user_id: String?,
        district_id: String?,
        fromDate: String?,
        toDate: String?
    ): LiveData<ModelAttendanceList?>? {
        return repository.getMutableLiveData(user_id, district_id, fromDate, toDate)
    }

    fun getLocationList(
        user_id: String?,
        district_id: String?,
        date: String?
    ): LiveData<ModelAddLocationList?>? {
        return repository.getModelAddLocationListMutableLiveData(user_id, district_id, date)
    }
}
