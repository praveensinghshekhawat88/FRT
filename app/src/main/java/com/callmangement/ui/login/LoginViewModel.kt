package com.callmangement.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.callmangement.model.login.ModelLogin

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LoginRepository(application.applicationContext)

    val isLoading: LiveData<Boolean>
        get() = repository.isLoading

    fun login(
        email: String?,
        password: String?,
        device_id: String?,
        device_token: String?
    ): LiveData<ModelLogin?> {
        return repository.getMutableLiveData(email, password, device_id, device_token)
    }
}
