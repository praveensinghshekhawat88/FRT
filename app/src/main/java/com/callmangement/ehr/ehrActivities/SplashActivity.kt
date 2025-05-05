package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.callmangement.R
import com.callmangement.databinding.EhrActivitySplashBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.ModelMobileVersion
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.ehr.support.Preference
import com.callmangement.ehr.support.Preference.Companion.getInstance
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Objects

class SplashActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: EhrActivitySplashBinding? = null
    private var preference: Preference? = null
    private var versionCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = EhrActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        preference = getInstance(mActivity!!)
        setClickListener()
        setUpData()
    }

    private fun setUpData() {
        try {
            val pInfo = mActivity!!.packageManager.getPackageInfo(
                packageName, 0
            )
            val versionName = pInfo.versionName
            binding!!.textAppVersion.text =
                resources.getString(R.string.app_version) + " " + versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        checkVersion()
    }

    private fun setClickListener() {
        binding!!.buttonRetry.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                binding!!.progressBar.visibility = View.VISIBLE
                binding!!.textCheckingForUpdate.visibility = View.VISIBLE
                binding!!.buttonRetry.visibility = View.GONE
                checkVersion()
            }
        })
    }

    private fun checkVersion() {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )
            try {
                val pInfo = mActivity!!.packageManager.getPackageInfo(
                    packageName, 0
                )
                versionCode = pInfo.versionCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val paramStr = CheckVersionQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = "" + versionCode
            val call = apiInterface.callCheckVersionApi(CheckVersion(), paramData)
            call!!.enqueue(object : Callback<ModelMobileVersion?> {
                override fun onResponse(
                    call: Call<ModelMobileVersion?>,
                    response: Response<ModelMobileVersion?>
                ) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    if (versionCode >= response.body()!!.version_Code) {
                                        if (preference!!.getString(Preference.USER_LOGIN_STATUS) == "true") {
                                            val intent = Intent(mActivity, MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        } else {
                                            val intent =
                                                Intent(mActivity, LoginActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        }
                                    } else {
                                        if (!mActivity!!.isFinishing) {
                                            val builder = AlertDialog.Builder(
                                                mActivity!!
                                            )
                                            builder.setMessage(resources.getString(R.string.new_version_is_available))
                                                .setCancelable(false)
                                                .setPositiveButton(
                                                    resources.getString(R.string.go_to_play_store)
                                                ) { dialog: DialogInterface, id: Int ->
                                                    dialog.cancel()
                                                    clearLoginPreferenceData()
                                                    val appPackageName = packageName
                                                    try {
                                                        startActivity(
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse(
                                                                    "market://details?id=$appPackageName"
                                                                )
                                                            )
                                                        )
                                                    } catch (e1: Exception) {
                                                        try {
                                                            startActivity(
                                                                Intent(
                                                                    Intent.ACTION_VIEW,
                                                                    Uri.parse(
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
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                                binding!!.progressBar.visibility = View.GONE
                                binding!!.textCheckingForUpdate.visibility = View.GONE
                                binding!!.buttonRetry.visibility = View.VISIBLE
                            }
                        } else {
                            makeToast(resources.getString(R.string.error) + response.code())
                            binding!!.progressBar.visibility = View.GONE
                            binding!!.textCheckingForUpdate.visibility = View.GONE
                            binding!!.buttonRetry.visibility = View.VISIBLE
                        }
                    } else {
                        makeToast(resources.getString(R.string.servererror) + response.code())
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.textCheckingForUpdate.visibility = View.GONE
                        binding!!.buttonRetry.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ModelMobileVersion?>, error: Throwable) {
                    makeToast(resources.getString(R.string.error))
                    binding!!.progressBar.visibility = View.GONE
                    binding!!.textCheckingForUpdate.visibility = View.GONE
                    binding!!.buttonRetry.visibility = View.VISIBLE
                    call.cancel()
                    Log.d("response", "responseresponse" + error.message)
                }
            })
        } else {
            binding!!.progressBar.visibility = View.GONE
            binding!!.textCheckingForUpdate.visibility = View.GONE
            binding!!.buttonRetry.visibility = View.VISIBLE
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }


    private fun clearLoginPreferenceData() {
        preference!!.putString(Preference.USER_LOGIN_STATUS, "false")
        preference!!.putString(Preference.USER_ID, "0")
        preference!!.putString(Preference.USER_NAME, "")
        preference!!.putString(Preference.USER_EMAIL, "")
        preference!!.putString(Preference.USER_Mobile, "")
        preference!!.putString(Preference.USER_DISTRICT_ID, "0")
        preference!!.putString(Preference.USER_DISTRICT, "")
        preference!!.putString(Preference.USER_TYPE, "")
        preference!!.putString(Preference.USER_TYPE_ID, "")
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }
}