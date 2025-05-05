package com.callmangement.ehr.ehrActivities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.callmangement.R
import com.callmangement.databinding.EhrActivityLoginBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.ModelLogin
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.ehr.support.Preference
import com.callmangement.ehr.support.Preference.Companion.getInstance
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Objects

class LoginActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: EhrActivityLoginBinding? = null
    private var email = ""
    private var password = ""
    private var preference: Preference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = EhrActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        preference = getInstance(mActivity!!)

        //Manager
        //   binding.inputEmail.setText("Manager@gmail.com");
        //    binding.inputPassword.setText("Manager@123");

        //ServiceEngineer
        //   binding.inputEmail.setText("jaipur_se17@gmail.com");
        //  binding.inputPassword.setText("jaipur@17");
        binding!!.inputEmail.setText("")
        binding!!.inputPassword.setText("")


        //SurveyUser
//        binding.inputEmail.setText("SurveyUser1@gmail.com");
//        binding.inputPassword.setText("SurveyUser1@123");
        setClickListener()
    }

    private fun setClickListener() {
        binding!!.buttonLogin.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                email =
                    Objects.requireNonNull(binding!!.inputEmail.text).toString().trim { it <= ' ' }
                password = Objects.requireNonNull(binding!!.inputPassword.text).toString()
                    .trim { it <= ' ' }
                if (email.isEmpty()) {
                    makeToast(resources.getString(R.string.please_enter_email_address))
                } else if (!isValidEmail(email)) {
                    makeToast(resources.getString(R.string.please_enter_valid_email_address))
                } else if (password.isEmpty()) {
                    makeToast(resources.getString(R.string.please_enter_password))
                } else {
                    // String android_id = Build.SERIAL;
                    @SuppressLint("HardwareIds") val android_id = Settings.Secure.getString(
                        mActivity!!.contentResolver, Settings.Secure.ANDROID_ID
                    )
                    // String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    doLogin(email, password, android_id)
                    Log.d("android_idandroid_id", "android_id$android_id")
                }
            }
        })
    }

    private fun doLogin(email: String, password: String, device_id: String) {
        Log.d("device_iddevice_id", " $device_id")

        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )
            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )
            val paramStr = LoginQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = email
            paramData[splitArray[1]] = password
            paramData[splitArray[2]] = device_id
            val call = apiInterface.callLoginApi(Login(), paramData)
            call!!.enqueue(object : Callback<ModelLogin?> {
                override fun onResponse(call: Call<ModelLogin?>, response: Response<ModelLogin?>) {
                    hideCustomProgressDialogCommonForAll()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    savePreferenceDataOnLogin(response.body()!!)
                                    val intent = Intent(mActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelLogin?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun savePreferenceDataOnLogin(data: ModelLogin) {
        preference!!.putString(Preference.USER_LOGIN_STATUS, "true")
        Log.d("usertype", " " + data.user_details.userId)
        preference!!.putString(Preference.USER_ID, data.user_details.userId)
        preference!!.putString(Preference.USER_NAME, data.user_details.userName)
        preference!!.putString(Preference.USER_EMAIL, data.user_details.emailId)
        preference!!.putString(Preference.USER_Mobile, data.user_details.mobileNo)
        preference!!.putString(Preference.USER_DISTRICT_ID, data.user_details.districtId)
        preference!!.putString(Preference.USER_DISTRICT, data.user_details.district)
        preference!!.putString(Preference.USER_TYPE_ID, data.user_details.userTypeId)
        preference!!.putString(Preference.USER_TYPE, data.user_details.userTypeName)
    }


    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
        }
    }
}