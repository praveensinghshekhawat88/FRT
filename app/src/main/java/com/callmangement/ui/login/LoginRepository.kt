package com.callmangement.ui.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.callmangement.R
import com.callmangement.model.login.ModelLogin
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val mContext: Context) {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val mutableLiveData = MutableLiveData<ModelLogin?>()

    fun getMutableLiveData(
        email: String?,
        password: String?,
        device_id: String?,
        device_token: String?
    ): MutableLiveData<ModelLogin?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.login(email, password, device_id, device_token)
        call.enqueue(object : Callback<ModelLogin?> {
            override fun onResponse(call: Call<ModelLogin?>, response: Response<ModelLogin?>) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    //Toast.makeText(mContext, ""+response.code(), Toast.LENGTH_SHORT).show();
                    val model = response.body()
                    if (model!!.status == "200") {
                        mutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
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

            override fun onFailure(call: Call<ModelLogin?>, t: Throwable) {
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

    companion object {
        private const val TAG = "LoginRepository"
    }
}
