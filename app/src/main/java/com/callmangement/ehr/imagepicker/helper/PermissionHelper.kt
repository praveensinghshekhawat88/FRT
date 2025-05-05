package com.callmangement.ehr.imagepicker.helper

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat

/**
 * Created by hoanglam on 8/21/17.
 */
object PermissionHelper {
    fun checkPermission(activity: Activity, permission: String, listener: PermissionAskListener) {
        if (!hasSelfPermission(activity, permission)) {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                listener.onPermissionPreviouslyDenied()
            } else {
                if (PreferenceHelper.isFirstTimeAskingPermission(activity, permission)) {
                    PreferenceHelper.firstTimeAskingPermission(activity, permission, false)
                    listener.onNeedPermission()
                } else {
                    listener.onPermissionDisabled()
                }
            }
        } else {
            listener.onPermissionGranted()
        }
    }

    @JvmStatic
    fun openAppSettings(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        activity.startActivity(intent)
    }

    fun asArray(vararg permissions: String): Array<String?> {
        require(permissions.size != 0) { "There is no given permission" }

        val dest = arrayOfNulls<String>(permissions.size)
        System.arraycopy(permissions, 0, dest, 0, permissions.size)
        return dest
    }

    @JvmStatic
    fun hasGranted(grantResult: Int): Boolean {
        return grantResult == PackageManager.PERMISSION_GRANTED
    }

    @JvmStatic
    fun hasGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (!hasGranted(result)) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun hasSelfPermission(context: Context, permission: String): Boolean {
        if (shouldAskPermission()) {
            return permissionHasGranted(context, permission)
        }
        return true
    }

    @JvmStatic
    fun hasSelfPermissions(context: Context, permissions: Array<String>): Boolean {
        if (shouldAskPermission()) {
            for (permission in permissions) {
                if (!permissionHasGranted(context, permission)) {
                    return false
                }
            }
        }
        return true
    }

    @JvmStatic
    fun requestAllPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        if (shouldAskPermission()) {
            internalRequestPermissions(activity, permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun internalRequestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ) {
        requireNotNull(activity) { "Given activity is null." }
        activity.requestPermissions(permissions, requestCode)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun permissionHasGranted(context: Context, permission: String): Boolean {
        return hasGranted(context.checkSelfPermission(permission))
    }

    private fun shouldAskPermission(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    }

    @JvmStatic
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    interface PermissionAskListener {
        fun onNeedPermission()

        fun onPermissionPreviouslyDenied()

        fun onPermissionDisabled()

        fun onPermissionGranted()
    }
}
