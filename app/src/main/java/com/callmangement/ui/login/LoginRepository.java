package com.callmangement.ui.login;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.model.login.ModelLogin;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private static final String TAG = "LoginRepository";
    private final Context mContext;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<ModelLogin> mutableLiveData = new MutableLiveData<>();

    public LoginRepository(Context mContext){
        this.mContext = mContext;
    }

    public MutableLiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public MutableLiveData<ModelLogin> getMutableLiveData(String email, String password, String device_id, String device_token){
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelLogin> call = service.login(email, password, device_id, device_token);
        call.enqueue(new Callback<ModelLogin>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogin> call, @NonNull Response<ModelLogin> response) {
                if (response.isSuccessful()){
                    isLoading.setValue(false);
                    //Toast.makeText(mContext, ""+response.code(), Toast.LENGTH_SHORT).show();
                    ModelLogin model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        mutableLiveData.setValue(model);
                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogin> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return mutableLiveData;
    }
}
