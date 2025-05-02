package com.callmangement.ui.attendance;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.callmangement.model.attendance.ModelAddLocationList;
import com.callmangement.model.attendance.ModelAttendanceList;

public class AttendanceViewModel extends AndroidViewModel {
    private final AttendanceRepository repository;

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
        repository = new AttendanceRepository(application.getApplicationContext());
    }

    public LiveData<Boolean> getIsLoading(){
        return repository.getIsLoading();
    }

    public LiveData<ModelAttendanceList> getAttendanceList(String user_id, String district_id, String fromDate, String toDate){
        return repository.getMutableLiveData(user_id, district_id, fromDate, toDate);
    }

    public LiveData<ModelAddLocationList> getLocationList(String user_id, String district_id, String date){
        return repository.getModelAddLocationListMutableLiveData(user_id, district_id, date);
    }
}
