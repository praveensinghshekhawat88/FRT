package com.callmangement.ui.distributor.activity

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
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import com.callmangement.Network.APIService
import com.callmangement.Network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDistributorMainBinding
import com.callmangement.firebase.FirebaseUtils
import com.callmangement.model.login.ModelLogin
import com.callmangement.model.logout.ModelLogout
import com.callmangement.support.permissions.PermissionHandler
import com.callmangement.support.permissions.Permissions
import com.callmangement.tracking_service.GpsUtils
import com.callmangement.ui.distributor.activity.DistributedPosListActivity
import com.callmangement.ui.login.LoginActivity
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.Objects

class DistributorMainActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityDistributorMainBinding? = null
    private var prefManager: PrefManager? = null
    private var myLocale: Locale? = null
    private var backgroundLocationPermissionApproved = 0
    private var isGPS = false
    private var checkPermissionTypeStr = "initial"
    var globalId: Int = -1
    private val IsSE_PunchIN = "false"
    lateinit var permissions: Array<String>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistributorMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivBack.visibility = View.GONE
        binding!!.actionBar.ivThreeDot.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.app_name)
        prefManager = PrefManager(mContext)
        binding!!.textUsername.text = prefManager!!.useR_NAME
        binding!!.textEmail.text = prefManager!!.useR_EMAIL
        initView()
    }

    private fun initView() {
        checkPermission()
        setUpClickListener()
        layoutShowAndHide()
    }

    private fun layoutShowAndHide() {
        if (prefManager!!.useR_Change_Language != "") {
            if (prefManager!!.useR_Change_Language == "en") {
                binding!!.actionBar.ivEngToHindi.visibility = View.VISIBLE
                binding!!.actionBar.ivHindiToEng.visibility = View.GONE
                prefManager!!.useR_Change_Language = "en"
            } else {
                binding!!.actionBar.ivEngToHindi.visibility = View.GONE
                binding!!.actionBar.ivHindiToEng.visibility = View.VISIBLE
                prefManager!!.useR_Change_Language = "hi"
            }
        } else {
            binding!!.actionBar.ivEngToHindi.visibility = View.VISIBLE
            binding!!.actionBar.ivHindiToEng.visibility = View.GONE
        }
    }

    private fun checkPermission() {
        //    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        //      , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA
            )
            Log.d("checkggg", "b")
        } else {
            permissions =
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            Log.d("checkggg", "0")
        }
        val rationale = "Please provide location permission so that you can ..."
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(mContext, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!gpsStatus) turnOnGps()
                        else {
                            if (checkPermissionTypeStr.equals(
                                    "next_process",
                                    ignoreCase = true
                                )
                            ) onClickWithPermissionCheck()
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (backgroundLocationPermissionApproved != 0) Handler(Looper.getMainLooper()).postDelayed(
                        { collectLocationPermissionDataDialog() }, 500
                    )
                    else {
                        if (!gpsStatus) turnOnGps()
                        else {
                            if (checkPermissionTypeStr.equals(
                                    "next_process",
                                    ignoreCase = true
                                )
                            ) onClickWithPermissionCheck()
                        }
                    }
                } else {
                    if (!gpsStatus) turnOnGps()
                    else {
                        if (checkPermissionTypeStr.equals(
                                "next_process",
                                ignoreCase = true
                            )
                        ) onClickWithPermissionCheck()
                    }
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun collectLocationPermissionDataDialog() {
        val builder = AlertDialog.Builder(mContext)
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
                    if (checkPermissionTypeStr.equals(
                            "next_process",
                            ignoreCase = true
                        )
                    ) onClickWithPermissionCheck()
                }
            }

            override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun turnOnGps() {
        GpsUtils(mContext).turnGPSOn { isGPSEnable: Boolean ->
            isGPS = isGPSEnable
        }
    }

    private val gpsStatus: Boolean
        get() {
            val manager =
                getSystemService(LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    private fun setUpClickListener() {
        binding!!.actionBar.ivEngToHindi.setOnClickListener { view: View? ->
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(resources.getString(R.string.eng_to_hi_message))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    binding!!.actionBar.ivEngToHindi.visibility = View.VISIBLE
                    binding!!.actionBar.ivHindiToEng.visibility = View.GONE
                    prefManager!!.useR_Change_Language = "hi"
                    setLocaleUpdate("hi")
                }
                .setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }

        binding!!.actionBar.ivHindiToEng.setOnClickListener { view: View? ->
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(resources.getString(R.string.hi_to_eng_message))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    binding!!.actionBar.ivEngToHindi.visibility = View.GONE
                    binding!!.actionBar.ivHindiToEng.visibility = View.VISIBLE
                    prefManager!!.useR_Change_Language = "en"
                    setLocaleUpdate("en")
                }
                .setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }

        binding!!.actionBar.ivThreeDot.setOnClickListener { view: View? ->
            val popupMenu =
                PopupMenu(mContext, binding!!.actionBar.ivThreeDot)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                val builder = AlertDialog.Builder(mContext)
                builder.setMessage(resources.getString(R.string.do_you_want_to_logout_from_this_app))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.logout)) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                        logout()
                    }
                    .setNegativeButton(
                        resources.getString(R.string.cancel)
                    ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
                true
            }
            popupMenu.show()
        }

        binding!!.buttonNewPOSDistributionForm.setOnClickListener(this)
        binding!!.buttonUploadImage.setOnClickListener(this)
        binding!!.buttonDistributedPosList.setOnClickListener(this)
        binding!!.buttonPosDistributionReport.setOnClickListener(this)
    }

    private fun checkLoginStatus() {
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.login(
            prefManager!!.useR_EMAIL,
            prefManager!!.useR_PASSWORD,
            prefManager!!.devicE_ID,
            prefManager!!.firebasE_DEVICE_TOKEN
        )
        call.enqueue(object : Callback<ModelLogin?> {
            override fun onResponse(call: Call<ModelLogin?>, response: Response<ModelLogin?>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val model = response.body()
                        if (model!!.status != "200") {
                            logout()
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelLogin?>, t: Throwable) {
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private fun logout() {
        showProgress(resources.getString(R.string.logout))
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.logOutApp(
            prefManager!!.useR_Id, prefManager!!.useR_EMAIL
        )
        call.enqueue(object : Callback<ModelLogout?> {
            override fun onResponse(call: Call<ModelLogout?>, response: Response<ModelLogout?>) {
                hideProgress()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            FirebaseUtils.unregisterTopic("all")
                            prefManager!!.clear()
                            prefManager!!.useR_PunchIn = IsSE_PunchIN
                            prefManager!!.setUser_Id("0")
                            prefManager!!.useR_DistrictId = "0"
                            @SuppressLint("HardwareIds") val android_id = Settings.Secure.getString(
                                mContext.contentResolver,
                                Settings.Secure.ANDROID_ID
                            )
                            prefManager!!.devicE_ID = android_id
                            startActivity(
                                Intent(mContext, LoginActivity::class.java).addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                )
                            )
                            finish()
                        } else {
                            makeToast(model!!.message)
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelLogout?>, t: Throwable) {
                hideProgress()
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102) {
            isGPS = gpsStatus
            if (isGPS && checkPermissionTypeStr.equals(
                    "next_process",
                    ignoreCase = true
                )
            ) onClickWithPermissionCheck()
        }
    }

    private fun onClickWithPermissionCheck() {
        if (globalId == R.id.buttonNewPOSDistributionForm) {
            startActivity(NewPosDistributionFormActivity::class.java)
        } else if (globalId == R.id.buttonUploadImage) {
            startActivity(UploadImageActivity::class.java)
        } else if (globalId == R.id.buttonDistributedPosList) {
            startActivity(DistributedPosListActivity::class.java)
        } else if (globalId == R.id.button_pos_distribution_report) {
            startActivity(PosDistributionReportActivity::class.java)
        }
    }

    fun setLocale(lang: String) {
        myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    fun setLocaleUpdate(lang: String) {
        prefManager!!.useR_Change_Language = lang
        myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        startActivity(DistributorMainActivity::class.java)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage(resources.getString(R.string.do_you_want_to_exit_from_this_app))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                dialog.cancel()
                finishAffinity()
            }
            .setNegativeButton(
                resources.getString(R.string.cancel)
            ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.setTitle(resources.getString(R.string.cancel))
        alert.show()
    }

    override fun onStart() {
        val locale = prefManager!!.useR_Change_Language
        if (locale != "") {
            if (locale == "hi") {
                setLocale("hi")
            } else {
                setLocale("en")
            }
        } else {
            prefManager!!.useR_Change_Language = "en"
        }
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }

    override fun onClick(view: View) {
        globalId = view.id
        checkPermissionTypeStr = "next_process"
        checkPermission()
    }

    companion object {
        fun newIntent(context: Context?, title: String?): Intent {
            val starter = Intent(context, DistributorMainActivity::class.java)
            starter.putExtra("param", title)
            return starter
        }
    }
}