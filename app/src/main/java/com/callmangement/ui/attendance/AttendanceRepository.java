package com.callmangement.ui.attendance;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.model.attendance.ModelAddLocationData;
import com.callmangement.model.attendance.ModelAddLocationList;
import com.callmangement.model.attendance.ModelAttendanceList;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceRepository {
    private static final String TAG = "AttendanceRepository";
    private final Context mContext;
    private final PrefManager prefManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<ModelAttendanceList> mutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelAddLocationList> modelAddLocationListMutableLiveData = new MutableLiveData<>();

    public AttendanceRepository(Context mContext) {
        this.mContext = mContext;
        prefManager = new PrefManager(mContext);
    }

    public MutableLiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public MutableLiveData<ModelAttendanceList> getMutableLiveData(String user_id, String district_id, String fromDate, String toDate){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelAttendanceList> call = service.getAttendanceRecord(user_id, district_id, fromDate, toDate);
        call.enqueue(new Callback<ModelAttendanceList>() {
            @Override
            public void onResponse(@NonNull Call<ModelAttendanceList> call, @NonNull Response<ModelAttendanceList> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelAttendanceList model = response.body();
                    if (Objects.requireNonNull(model).status.equals("200")){
                        mutableLiveData.setValue(model);
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ModelAttendanceList> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<ModelAddLocationList> getModelAddLocationListMutableLiveData(String user_id, String district_id, String date){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelAddLocationList> call = service.getLocationRecord(user_id, district_id);
        call.enqueue(new Callback<ModelAddLocationList>() {
            @Override
            public void onResponse(@NonNull Call<ModelAddLocationList> call, @NonNull Response<ModelAddLocationList> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    ModelAddLocationList model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        List<ModelAddLocationData> data = model.location_list;
                        List<ModelAddLocationData> list = new ArrayList<>();
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).getLocation_Date_Time().contains(date)) {
                                list.add(data.get(i));
                            }
                        }
                        model.location_list = (list);
                        modelAddLocationListMutableLiveData.setValue(model);
                    }else {
                        Toast.makeText(mContext, model.getMassage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelAddLocationList> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelAddLocationListMutableLiveData;
    }
}
