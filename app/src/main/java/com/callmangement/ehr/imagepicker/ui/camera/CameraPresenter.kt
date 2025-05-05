package com.callmangement.ehr.imagepicker.ui.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.ui.common.BasePresenter
import com.callmangement.R

/**
 * Created by hoanglam on 8/22/17.
 */
class CameraPresenter : BasePresenter<CameraView?>() {
    private val cameraModule: CameraModule = DefaultCameraModule()

    fun captureImage(activity: Activity, config: Config?, requestCode: Int) {
        val context = activity.applicationContext
        val intent = cameraModule.getCameraIntent(activity, config)
        if (intent == null) {
            Toast.makeText(
                context,
                context.getString(R.string.imagepicker_error_create_image_file),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        activity.startActivityForResult(intent, requestCode)
    }


    fun finishCaptureImage(context: Context?, data: Intent?, config: Config?) {
        cameraModule.getImage(context, data, object : OnImageReadyListener {
            override fun onImageReady(images: List<Image?>?) {
                // handle the image list
                val cameraView = view
                Log.d("MYIMAGE", "" + cameraView)
                if (cameraView != null) {
                    cameraView.finishPickImages(images)
                } else {
                    Toast.makeText(context, "Network issue! Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
