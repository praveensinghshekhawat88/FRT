package com.callmangement.ui.home

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.Secure
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivitySplashBinding
import com.callmangement.firebase.FirebaseUtils
import com.callmangement.model.ModelMobileVersion
import com.callmangement.model.attendance.ModelAttendance
import com.callmangement.network.APIService
import com.callmangement.network.AppConfig
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.attendance.AttendanceActivity
import com.callmangement.ui.distributor.activity.DistributorMainActivity
import com.callmangement.ui.login.LoginActivity
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.DateTimeUtils.currentDate
import com.callmangement.utils.PrefManager
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.Objects

@SuppressLint("CustomSplashScreen")
class SplashActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivitySplashBinding? = null
    private var prefManager: PrefManager? = null
    private var myLocale: Locale? = null
    private val IsSE_PunchIN = "false"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        initView()
    }

    private fun initView() {
        setUpOnClickListener()
        setUpData()
    }

    private fun setUpOnClickListener() {
        binding!!.buttonRetry.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpData() {
        prefManager = PrefManager(mContext!!)
        FirebaseUtils.registerTopic("all")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful) {
                if (task.result != null) {
                    val deviceToken = task.result
                    prefManager!!.firebasE_DEVICE_TOKEN = deviceToken
                    @SuppressLint("HardwareIds") val android_id = Secure.getString(
                        mContext!!.contentResolver, Secure.ANDROID_ID
                    )
                    updateDTokenOnServer(android_id, deviceToken)
                }
            }
        }
        @SuppressLint("HardwareIds") val android_id =
            Secure.getString(mContext!!.contentResolver, Secure.ANDROID_ID)
        prefManager!!.devicE_ID = android_id
        binding!!.textAppVersion.text =
            resources.getString(R.string.app_version) + " " + AppConfig.VERSION_NAME

        // Log.e("device_token", prefManager.getFIREBASE_DEVICE_TOKEN());
    }

    override fun onResume() {
        super.onResume()
        checkVersion()
    }

    override fun onStart() {
        val locale = prefManager!!.USER_Change_Language
        if (locale != "") {
            if (locale == "hi") {
                setLocale("hi")
            } else {
                setLocale("en")
            }
        } else {
            prefManager!!.USER_Change_Language = "en"
        }
        super.onStart()
    }

    fun setLocale(lang: String) {
        myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    private fun checkVersion() {
        if (isNetworkAvailable(mContext!!)) {
            binding!!.progressBar.visibility = View.VISIBLE
            binding!!.textCheckingForUpdate.visibility = View.VISIBLE
            binding!!.buttonRetry.visibility = View.GONE
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getMobileAppVersion(AppConfig.VERSION_CODE)
            call.enqueue(object : Callback<ModelMobileVersion?> {
                override fun onResponse(
                    call: Call<ModelMobileVersion?>,
                    response: Response<ModelMobileVersion?>
                ) {
                    if (response.isSuccessful) {
                        val model = response.body()
                        if (Objects.requireNonNull<ModelMobileVersion?>(model).status == "200") {
                            if (AppConfig.VERSION_CODE >= model!!.version_Code) {
                                Handler().postDelayed({
                                    if (prefManager!!.userLoginStatus == "true") {
                                        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                                                "ServiceEngineer",
                                                ignoreCase = true
                                            )
                                        ) {
                                            checkedAttendanceStatus
                                        } else if (prefManager!!.useR_TYPE_ID == "8" && prefManager!!.useR_TYPE.equals(
                                                "Distributor",
                                                ignoreCase = true
                                            )
                                        ) {
                                            startActivity(DistributorMainActivity::class.java)
                                        } else {
                                            startActivity(MainActivity::class.java)
                                        }
                                    } else {
                                        startActivity(LoginActivity::class.java)
                                    }
                                }, 3000)
                            } else {
                                if (!mContext!!.isFinishing) {
                                    val builder = AlertDialog.Builder(
                                        mContext!!
                                    )
                                    builder.setMessage(resources.getString(R.string.new_version_is_available))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            resources.getString(R.string.go_to_play_store)
                                        ) { dialog: DialogInterface, id: Int ->
                                            dialog.cancel()
                                            prefManager!!.userLoginStatus = "false"
                                            prefManager!!.clear()
                                            FirebaseUtils.unregisterTopic("all")
                                            prefManager!!.useR_PunchIn = IsSE_PunchIN
                                            prefManager!!.setUser_Id("0")
                                            prefManager!!.useR_DistrictId = "0"
                                            val appPackageName = packageName
                                            try {
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW, Uri.parse(
                                                            "market://details?id=$appPackageName"
                                                        )
                                                    )
                                                )
                                            } catch (anfe: Exception) {
                                                try {
                                                    startActivity(
                                                        Intent(
                                                            Intent.ACTION_VIEW, Uri.parse(
                                                                "https://play.google.com/store/apps/details?id=$appPackageName"
                                                            )
                                                        )
                                                    )
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                        .setNegativeButton(
                                            resources.getString(R.string.cancel)
                                        ) { dialog: DialogInterface, i: Int ->
                                            dialog.cancel()
                                            finish()
                                        }
                                    val alert = builder.create()
                                    alert.setTitle(resources.getString(R.string.alert))
                                    alert.show()
                                }
                            }
                        } else {
                            makeToast(model!!.message)
                            binding!!.progressBar.visibility = View.GONE
                            binding!!.textCheckingForUpdate.visibility = View.GONE
                            binding!!.buttonRetry.visibility = View.VISIBLE
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.textCheckingForUpdate.visibility = View.GONE
                        binding!!.buttonRetry.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ModelMobileVersion?>, t: Throwable) {
                    makeToast(resources.getString(R.string.error_message))
                    binding!!.progressBar.visibility = View.GONE
                    binding!!.textCheckingForUpdate.visibility = View.GONE
                    binding!!.buttonRetry.visibility = View.VISIBLE
                }
            })
        } else {
            binding!!.progressBar.visibility = View.GONE
            binding!!.textCheckingForUpdate.visibility = View.GONE
            binding!!.buttonRetry.visibility = View.VISIBLE
            makeToastLong(resources.getString(R.string.no_internet_connection))
        }
    }

    private val checkedAttendanceStatus: Unit
        get() {
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getCheckedAttendance(prefManager!!.useR_Id, currentDate)
            call.enqueue(object : Callback<ModelAttendance?> {
                override fun onResponse(
                    call: Call<ModelAttendance?>,
                    response: Response<ModelAttendance?>
                ) {
                    if (response.isSuccessful) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            startActivity(MainActivity::class.java)
                        } else {
                            startActivity(AttendanceActivity::class.java)
                        }
                    }
                }

                override fun onFailure(call: Call<ModelAttendance?>, t: Throwable) {
                }
            })
        }

    private fun updateDTokenOnServer(device_id: String, device_token: String?) {
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.updateDToken(device_id, device_token)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                val responseStr = response.body()!!.string()
                                val jsonObject = JSONObject(responseStr)
                                val message = jsonObject.optString("message")
                                //    Log.e("message", message);
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
            }
        })
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonRetry) {
            checkVersion()
        }
    }
}

