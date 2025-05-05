package com.callmangement.ehr.support

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import com.callmangement.R
import java.nio.charset.StandardCharsets

object Utils {
    var popupWindow: PopupWindow? = null
    private const val BUFFER_SIZE = 6 * 1024

    @JvmStatic
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideDialogKeyboard(context: Activity, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun openKeyboard(context: Activity, editText: EditText?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard(context: Activity) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            context.currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.activeNetworkInfo
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private var progressDialogCommonForAll: ProgressDialog? = null

    @JvmStatic
    fun showCustomProgressDialogCommonForAll(context: Activity, message: String?) {
        progressDialogCommonForAll = ProgressDialog(context)
        progressDialogCommonForAll = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
        val spannableString = SpannableString(message)
        spannableString.setSpan(RelativeSizeSpan(1.3f), 0, spannableString.length, 0)
        spannableString.setSpan(
            ForegroundColorSpan(context.resources.getColor(R.color.teal_200)),
            0,
            spannableString.length,
            0
        )
        progressDialogCommonForAll!!.setMessage(spannableString)
        progressDialogCommonForAll!!.setCancelable(false)
        try {
            if (progressDialogCommonForAll != null) progressDialogCommonForAll!!.show()
        } catch (e: Exception) {
        }
    }

    @JvmStatic
    fun hideCustomProgressDialogCommonForAll() {
        try {
            if (progressDialogCommonForAll != null) {
                progressDialogCommonForAll!!.dismiss()
            }
        } catch (e: Exception) {
        }
    }

    @JvmStatic
    fun convertStringToUTF8(s: String): String {
        var out: String? = null
        out = String(s.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
        return out
    }
}




