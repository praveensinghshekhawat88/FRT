package com.callmangement.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.callmangement.model.login.ModelLogin;
import com.callmangement.model.login.ModelVerifyOTP;
import com.callmangement.model.logout.ModelLogout;

public class LoginViewModel extends AndroidViewModel {
    private final LoginRepository repository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new LoginRepository(application.getApplicationContext());
    }

    public LiveData<Boolean> getIsLoading(){
        return repository.getIsLoading();
    }

    public LiveData<ModelLogin> login(String email, String password, String device_id, String device_token){
        return repository.getMutableLiveData(email, password, device_id, device_token);
    }

}
