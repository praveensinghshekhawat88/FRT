package com.callmangement.ehr.imagepicker.helper

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.model.SavePath
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by hoanglam on 7/31/16.
 */
object ImageHelper {
    private const val TAG = "ImageHelper"

    @JvmStatic
    fun createImageFile(savePath: SavePath): File? {
        // External sdcard location
        val path = savePath.path
        val mediaStorageDir = if (savePath.isFullPath)
            File(path)
        else
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                path
            )

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(
                    TAG,
                    "Oops! Failed create $path"
                )
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMG_$timeStamp"

        var imageFile: File? = null
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", mediaStorageDir)
        } catch (e: IOException) {
            Log.d(
                TAG,
                "Oops! Failed create $imageFileName file"
            )
        }
        return imageFile
    }

    fun getNameFromFilePath(path: String): String {
        if (path.contains(File.separator)) {
            return path.substring(path.lastIndexOf(File.separator) + 1)
        }
        return path
    }

    @JvmStatic
    fun grantAppPermission(context: Context, intent: Intent, fileUri: Uri?) {
        val resolvedIntentActivities = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolvedIntentInfo in resolvedIntentActivities) {
            val packageName = resolvedIntentInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName, fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    @JvmStatic
    fun revokeAppPermission(context: Context, fileUri: Uri?) {
        context.revokeUriPermission(
            fileUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    @JvmStatic
    fun singleListFromPath(path: String): List<Image> {
        val images: MutableList<Image> = ArrayList()
        images.add(Image(0, getNameFromFilePath(path), path))
        return images
    }

    @JvmStatic
    fun isGifFormat(image: Image): Boolean {
        val extension = image.path!!.substring(image.path!!.lastIndexOf(".") + 1)
        return extension.equals("gif", ignoreCase = true)
    }
}
