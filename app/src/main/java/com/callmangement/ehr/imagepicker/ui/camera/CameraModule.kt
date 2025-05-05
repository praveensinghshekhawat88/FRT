package com.callmangement.ehr.imagepicker.ui.camera

import android.content.Context
import android.content.Intent
import com.callmangement.ehr.imagepicker.model.Config

/**
 * Created by hoanglam on 8/18/17.
 */
interface CameraModule {
    fun getCameraIntent(context: Context?, config: Config?): Intent?

    fun getImage(context: Context?, intent: Intent?, imageReadyListener: OnImageReadyListener?)
}
