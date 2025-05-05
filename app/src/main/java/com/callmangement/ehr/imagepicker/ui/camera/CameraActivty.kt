package com.callmangement.ehr.imagepicker.ui.camera

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.callmangement.ehr.imagepicker.helper.CameraHelper.checkCameraAvailability
import com.callmangement.ehr.imagepicker.helper.LogHelper.Companion.instance
import com.callmangement.ehr.imagepicker.helper.PermissionHelper.hasGranted
import com.callmangement.ehr.imagepicker.helper.PermissionHelper.hasSelfPermission
import com.callmangement.ehr.imagepicker.helper.PermissionHelper.hasSelfPermissions
import com.callmangement.ehr.imagepicker.helper.PermissionHelper.openAppSettings
import com.callmangement.ehr.imagepicker.helper.PermissionHelper.requestAllPermissions
import com.callmangement.ehr.imagepicker.helper.PermissionHelper.shouldShowRequestPermissionRationale
import com.callmangement.ehr.imagepicker.helper.PreferenceHelper.firstTimeAskingPermission
import com.callmangement.ehr.imagepicker.helper.PreferenceHelper.isFirstTimeAskingPermission
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.ehr.imagepicker.widget.SnackBarView
import com.callmangement.R

/**
 * Created by hoanglam on 8/21/17.
 */
class CameraActivty : AppCompatActivity(), CameraView {
    private val permissions =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    private var snackBar: SnackBarView? = null

    private var config: Config? = null
    private var presenter: CameraPresenter? = null
    private val logger = instance
    private var isOpeningCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagepicker_screen_cameraehr)


        val intent = intent
        if (intent == null) {
            finish()
            return
        }
        config = intent.getParcelableExtra(Config.EXTRA_CONFIG)

        snackBar = findViewById(R.id.snackbar)

        if (hasSelfPermissions(this, permissions) && isOpeningCamera) {
            isOpeningCamera = false
        } else if (!snackBar!!.isShowing) {
            presenter = CameraPresenter()
            presenter!!.attachView(this)
            captureImageWithPermission()
        }
    }


    override fun onResume() {
        super.onResume()


        // captureImage();
    }

    private fun captureImageWithPermission() {
        // captureImage();

        if (hasSelfPermissions(this, permissions)) {
            captureImage()
        } else {
            logger.w("Camera permission is not granted. Requesting permission")
            requestCameraPermission()
        }
    }

    private fun captureImage() {
        if (!checkCameraAvailability(this)) {
            finish()
            return
        }
        presenter!!.captureImage(this, config, ImagePicker.RC_PICK_IMAGES_CUSTOM)
        isOpeningCamera = true
    }

    private fun requestCameraPermission() {
        logger.w("Write External permission is not granted. Requesting permission")
        var hasPermissionDisbled = false
        val wesGranted = hasSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val wesGranted_13 = hasSelfPermission(
            this, Manifest.permission.READ_MEDIA_IMAGES
        )
        val cameraGranted = hasSelfPermission(
            this, Manifest.permission.CAMERA
        )



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!wesGranted_13 && !shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                if (!isFirstTimeAskingPermission(
                        this, Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    hasPermissionDisbled = true
                }
            }
        } else {
            if (!wesGranted && !shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                if (!isFirstTimeAskingPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    hasPermissionDisbled = true
                }
            }
        }
        if (!cameraGranted && !shouldShowRequestPermissionRationale(
                this, Manifest.permission.CAMERA
            )
        ) {
            if (!isFirstTimeAskingPermission(
                    this, Manifest.permission.CAMERA
                )
            ) {
                hasPermissionDisbled = true
            }
        }

        val permissions: MutableList<String> = ArrayList()

        if (!hasPermissionDisbled) {
            if (!wesGranted) {
                //       permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                //       PreferenceHelper.firstTimeAskingPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, false);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                    firstTimeAskingPermission(
                        this, Manifest.permission.READ_MEDIA_IMAGES, false
                    )
                } else {
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    firstTimeAskingPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE, false
                    )
                }
            }

            if (!cameraGranted) {
                permissions.add(Manifest.permission.CAMERA)
                firstTimeAskingPermission(
                    this, Manifest.permission.CAMERA, false
                )
            }

            requestAllPermissions(
                this,
                permissions.toTypedArray<String>(),
                Config.RC_CAMERA_PERMISSION
            )
        } else {
            snackBar!!.show(
                R.string.imagepicker_msg_no_write_external_storage_camera_permission
            ) {
                openAppSettings(
                    this@CameraActivty
                )
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Config.RC_CAMERA_PERMISSION -> {
                if (hasGranted(grantResults)) {
                    logger.d("Camera permission granted")
                    captureImage()
                    return
                }

                logger.e(
                    "Permission not granted: results len = " + grantResults.size +
                            " Result code = " + (if (grantResults.size > 0) grantResults[0] else "(empty)")
                )

                var shouldShowSnackBar = false
                for (grantResult in grantResults) {
                    if (hasGranted(grantResult)) {
                        shouldShowSnackBar = true
                        break
                    }
                }

                if (shouldShowSnackBar) {
                    snackBar!!.show(
                        R.string.imagepicker_msg_no_write_external_storage_camera_permission
                    ) {
                        openAppSettings(
                            this@CameraActivty
                        )
                    }
                } else {
                    //  finish();
                }
            }

            else -> {
                logger.d("Got unexpected permission result: $requestCode")
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                finish()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.RC_PICK_IMAGES_CUSTOM) {
            presenter!!.finishCaptureImage(this, data, config)
        } else {
            setResult(RESULT_CANCELED, Intent())
            finish()
        }
    }


    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter!!.detachView()
        }
    }

    override fun finishPickImages(images: List<Image?>?) {
        val data = Intent()
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, images as ArrayList<out Parcelable?>)
        setResult(RESULT_OK, data)
        finish()
    }
}
