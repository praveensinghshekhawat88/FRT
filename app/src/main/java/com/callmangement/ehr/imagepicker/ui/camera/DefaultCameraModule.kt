package com.callmangement.ehr.imagepicker.ui.camera

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.callmangement.ehr.imagepicker.helper.ImageHelper.createImageFile
import com.callmangement.ehr.imagepicker.helper.ImageHelper.grantAppPermission
import com.callmangement.ehr.imagepicker.helper.ImageHelper.revokeAppPermission
import com.callmangement.ehr.imagepicker.helper.ImageHelper.singleListFromPath
import com.callmangement.ehr.imagepicker.model.Config
import java.io.Serializable
import java.util.Locale

/**
 * Created by hoanglam on 8/18/17.
 */
class DefaultCameraModule : CameraModule, Serializable {
    protected var imagePath: String? = null


    override fun getCameraIntent(context: Context?, config: Config?): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = createImageFile(
            config!!.savePath!!
        )
        if (imageFile != null) {
            val appContext = context!!.applicationContext
            val providerName =
                String.format(Locale.ENGLISH, "%s%s", appContext.packageName, ".provider")
            val uri = FileProvider.getUriForFile(appContext, providerName, imageFile)
            imagePath = "file:" + imageFile.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            grantAppPermission(context!!, intent, uri)
            return intent
        }
        return null
    }

    override fun getImage(
        context: Context?,
        intent: Intent?,
        imageReadyListener: OnImageReadyListener?
    ) {
        checkNotNull(imageReadyListener) { "OnImageReadyListener must not be null" }
        if (imagePath == null) {
            imageReadyListener.onImageReady(null)
            return
        }
        val imageUri = Uri.parse(imagePath)
        if (imageUri != null) {
            MediaScannerConnection.scanFile(
                context!!.applicationContext, arrayOf(imageUri.path),
                null
            ) { path, uri ->
                var path = path
                if (path != null) {
                    path = imagePath
                }
                imageReadyListener.onImageReady(
                    singleListFromPath(
                        path!!
                    )
                )
                revokeAppPermission(
                    context,
                    imageUri
                )
            }
        }
    }
}
