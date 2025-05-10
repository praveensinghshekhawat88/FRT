package com.callmangement.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityLoginBinding
import com.callmangement.firebase.FirebaseUtils
import com.callmangement.model.attendance.ModelAttendance
import com.callmangement.model.login.ModelLogin
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.support.permissions.PermissionHandler
import com.callmangement.support.permissions.Permissions
import com.callmangement.tracking_service.GpsUtils
import com.callmangement.ui.attendance.AttendanceActivity
import com.callmangement.ui.distributor.activity.DistributorMainActivity
import com.callmangement.ui.home.MainActivity
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
import java.util.Objects

class LoginActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityLoginBinding? = null
    private var viewModel: LoginViewModel? = null
    private var prefManager: PrefManager? = null
    private var email = ""
    private var password = ""
    private var backgroundLocationPermissionApproved = 0
    private var isGPS = false
    private var checkPermissionTypeStr = "initial"
    lateinit var permissions: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initView()
    }

    private fun initView() {
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        FirebaseUtils.registerTopic("all")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful) {
                if (task.result != null) {
                    val deviceToken = task.result
                    prefManager!!.firebasE_DEVICE_TOKEN = deviceToken
                    @SuppressLint("HardwareIds") val android_id =
                        Settings.Secure.getString(
                            mContext!!.contentResolver, Settings.Secure.ANDROID_ID
                        )
                    updateDTokenOnServer(android_id, deviceToken)
                }
            }
        }

        @SuppressLint("HardwareIds") val android_id = Settings.Secure.getString(
            mContext!!.contentResolver, Settings.Secure.ANDROID_ID
        )
        //  Log.d("deviceiddeviceid", " " + android_id);
        prefManager!!.devicE_ID = android_id
        checkPermission()
        setUpOnClickListener()
    }

    private fun setUpOnClickListener() {
        binding!!.buttonLogin.setOnClickListener(this)
    }

    private fun doLogin(
        email: String,
        password: String,
        device_id: String?,
        device_token: String?
    ) {
        if (isNetworkAvailable(mContext!!)) {
            isLoading
            viewModel!!.login(email, password, device_id, device_token).observe(
                this,
                Observer<ModelLogin?> { modelLogin: ModelLogin? ->
                    isLoading
                    if (modelLogin?.status == "200") {
                        val data = modelLogin.user_details
                        prefManager?.userLoginStatus = "true"
                        prefManager?.setUser_Id(data.userId)
                        prefManager?.useR_NAME = data.userName
                        prefManager?.useR_EMAIL = data.emailId
                        prefManager?.useR_Mobile = data.mobileNo
                        prefManager?.useR_DistrictId = data.districtId
                        prefManager?.useR_District = data.district
                        prefManager?.useR_TYPE_ID = data.userTypeId
                        prefManager?.useR_TYPE = data.userTypeName
                        prefManager?.useR_PASSWORD = password

                        //     Log.d("loginid", " " + data.getUserId());
                        if (data.userTypeId == "4" && data.userTypeName.equals(
                                "ServiceEngineer",
                                ignoreCase = true
                            )
                        ) {
                            checkedAttendanceStatus
                        } else if (data.userTypeId == "8" && data.userTypeName.equals(
                                "Distributor",
                                ignoreCase = true
                            )
                        ) {
                            startActivity(DistributorMainActivity::class.java)
                        } else {
                            startActivity(MainActivity::class.java)
                        }
                    }
                })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
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
                        if (model?.status == "200") {
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

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading.observe(
                this
            ) { aBoolean: Boolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    private fun checkPermission() {
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA
            )

            //     Log.d("checkggg", "b");
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            //    Log.d("checkggg", "0");
        }

        val rationale = "Please provide location permission so that you can ..."
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!gpsStatus) turnOnGps()
                        else {
                            if (checkPermissionTypeStr.equals(
                                    "login",
                                    ignoreCase = true
                                )
                            ) loginProcess()
                        }
                    }
                    //   Log.d("goodgood", "1");
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!gpsStatus) turnOnGps()
                        else {
                            if (checkPermissionTypeStr.equals(
                                    "login",
                                    ignoreCase = true
                                )
                            ) loginProcess()
                        }
                    }

                    //   Log.d("goodgood", "2");
                } else {
                    if (!gpsStatus) turnOnGps()
                    else {
                        if (checkPermissionTypeStr.equals(
                                "login",
                                ignoreCase = true
                            )
                        ) loginProcess()
                    }
                    //  Log.d("goodgood", "3");
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
                //   Log.d("deniedPermissions", "" + deniedPermissions);
            }
        })
    }

    private fun collectLocationPermissionDataDialog() {
        val builder = AlertDialog.Builder(
            mContext!!
        )
        builder.setMessage(resources.getString(R.string.collect_location_permission_alert))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                checkBackgroundLocationPermission()
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.setTitle(resources.getString(R.string.alert))
        alert.show()
    }

    private fun checkBackgroundLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        val rationale = "Please provide Access Background Location"
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (!gpsStatus) turnOnGps()
                else {
                    if (checkPermissionTypeStr.equals("login", ignoreCase = true)) loginProcess()
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun turnOnGps() {
        GpsUtils(mContext!!).turnGPSOn { isGPSEnable: Boolean ->
            isGPS = isGPSEnable
        }
    }

    private val gpsStatus: Boolean
        get() {
            val manager =
                getSystemService(LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    private fun loginProcess() {
        email = Objects.requireNonNull(binding!!.inputEmail.text).toString().trim { it <= ' ' }
        password =
            Objects.requireNonNull(binding!!.inputPassword.text).toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            makeToast(resources.getString(R.string.please_enter_email_address))
        } else if (!isValidEmail(email)) {
            makeToast(resources.getString(R.string.please_enter_valid_email_address))
        } else if (password.isEmpty()) {
            makeToast(resources.getString(R.string.please_enter_password))
        } else {
            doLogin(email, password, prefManager!!.devicE_ID, prefManager!!.firebasE_DEVICE_TOKEN)
            //     Log.d("deviceiddeviceid", " " + prefManager.getDEVICE_ID());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102) {
            isGPS = gpsStatus
            if (isGPS && checkPermissionTypeStr.equals("login", ignoreCase = true)) loginProcess()
        }
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
                                //   Log.e("message", message);
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

    override fun onBackPressed() {
        //super.onBackPressed();
        finishAffinity()
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonLogin) {
            checkPermissionTypeStr = "login"
            checkPermission()
        }
    }
}