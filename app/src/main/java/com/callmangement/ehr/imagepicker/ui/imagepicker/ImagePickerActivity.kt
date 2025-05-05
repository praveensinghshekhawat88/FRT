package com.callmangement.ehr.imagepicker.ui.imagepicker

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.ehr.imagepicker.helper.CameraHelper
import com.callmangement.ehr.imagepicker.helper.LogHelper
import com.callmangement.ehr.imagepicker.helper.PermissionHelper
import com.callmangement.ehr.imagepicker.listener.OnBackAction
import com.callmangement.ehr.imagepicker.listener.OnFolderClickListener
import com.callmangement.ehr.imagepicker.listener.OnImageClickListener
import com.callmangement.ehr.imagepicker.listener.OnImageSelectionListener
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.ehr.imagepicker.model.Folder
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.widget.ImagePickerToolbar
import com.callmangement.ehr.imagepicker.widget.ProgressWheel
import com.callmangement.ehr.imagepicker.widget.SnackBarView
import com.callmangement.R

/**
 * Created by hoanglam on 7/31/16.
 */
class ImagePickerActivity : AppCompatActivity(), ImagePickerView {
    private var toolbar: ImagePickerToolbar? = null
    private var recyclerViewManager: RecyclerViewManager? = null
    private var recyclerView: RecyclerView? = null
    private var progressWheel: ProgressWheel? = null
    private var emptyLayout: View? = null
    private var snackBar: SnackBarView? = null

    private var config: Config? = null
    private var handler: Handler? = null
    private var observer: ContentObserver? = null
    private var presenter: ImagePickerPresenter? = null
    private val logger: LogHelper = LogHelper.instance


    private val imageClickListener =
        object : OnImageClickListener {
            override fun onImageClick(view: View?, position: Int, isSelected: Boolean): Boolean {
                return recyclerViewManager!!.selectImage()
            }
        }

    private val folderClickListener =
        object : OnFolderClickListener {
            override fun onFolderClick(folder: Folder?) {
                setImageAdapter(
                    folder!!.images,
                    folder.folderName
                )
            }
        }

    private val backClickListener = View.OnClickListener { onBackPressed() }

    private val cameraClickListener = View.OnClickListener { captureImageWithPermission() }

    private val doneClickListener = View.OnClickListener { onDone() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        if (intent == null) {
            finish()
            return
        }

        config = intent.getParcelableExtra(Config.EXTRA_CONFIG)

        setContentView(R.layout.imagepicker_screen_picker)

        setupView()
        setupComponents()
        setupToolbar()
    }

    private fun setupView() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        progressWheel = findViewById(R.id.progressWheel)
        emptyLayout = findViewById(R.id.layout_empty)
        snackBar = findViewById(R.id.snackbar)

        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = config!!.getStatusBarColor()
        }

        progressWheel!!.setBarColor(config!!.getProgressBarColor())
        findViewById<View>(R.id.container).setBackgroundColor(config!!.getBackgroundColor())
    }

    private fun setupComponents() {
        recyclerViewManager = RecyclerViewManager(
            recyclerView!!, config!!, resources.configuration.orientation
        )
        recyclerViewManager!!.setupAdapters(imageClickListener, folderClickListener)
        recyclerViewManager!!.setOnImageSelectionListener(object : OnImageSelectionListener {
            override fun onSelectionUpdate(images: List<Image?>?) {
                invalidateToolbar()
                if (images != null) {
                    if (!config!!.isMultipleMode && images.isNotEmpty()) {
                        onDone()
                    }
                }
            }
        })


        presenter = ImagePickerPresenter(
            ImageFileLoader(
                this
            )
        )
        presenter!!.attachView(this)
    }

    private fun setupToolbar() {
        toolbar!!.config(config!!)
        toolbar!!.setOnBackClickListener(backClickListener)
        toolbar!!.setOnCameraClickListener(cameraClickListener)
        toolbar!!.setOnDoneClickListener(doneClickListener)
    }

    override fun onResume() {
        super.onResume()
        dataWithPermission
    }


    private fun setImageAdapter(images: List<Image>?, title: String) {
        recyclerViewManager!!.setImageAdapter(images, title)
        invalidateToolbar()
    }

    private fun setFolderAdapter(folders: List<Folder?>?) {
        recyclerViewManager!!.setFolderAdapter(folders)
        invalidateToolbar()
    }

    private fun invalidateToolbar() {
        toolbar!!.setTitle(recyclerViewManager!!.getTitle())
        toolbar!!.showDoneButton(recyclerViewManager!!.isShowDoneButton)
    }

    private fun onDone() {
        presenter!!.onDoneSelectImages(recyclerViewManager!!.selectedImages)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recyclerViewManager!!.changeOrientation(newConfig.orientation)
    }


    private val dataWithPermission: Unit
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissions =
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)

                PermissionHelper.checkPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    object :
                        PermissionHelper.PermissionAskListener {
                        override fun onNeedPermission() {
                            PermissionHelper.requestAllPermissions(
                                this@ImagePickerActivity,
                                permissions,
                                Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                            )
                        }

                        override fun onPermissionPreviouslyDenied() {
                            PermissionHelper.requestAllPermissions(
                                this@ImagePickerActivity,
                                permissions,
                                Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                            )
                        }

                        override fun onPermissionDisabled() {
                            snackBar!!.show(
                                R.string.imagepicker_msg_no_write_external_storage_permission
                            ) {
                                PermissionHelper.openAppSettings(
                                    this@ImagePickerActivity
                                )
                            }
                        }

                        override fun onPermissionGranted() {
                            data
                        }
                    })
            } else {
                val permissions =
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                PermissionHelper.checkPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    object :
                        PermissionHelper.PermissionAskListener {
                        override fun onNeedPermission() {
                            PermissionHelper.requestAllPermissions(
                                this@ImagePickerActivity,
                                permissions,
                                Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                            )
                        }

                        override fun onPermissionPreviouslyDenied() {
                            PermissionHelper.requestAllPermissions(
                                this@ImagePickerActivity,
                                permissions,
                                Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                            )
                        }

                        override fun onPermissionDisabled() {
                            snackBar!!.show(
                                R.string.imagepicker_msg_no_write_external_storage_permission
                            ) {
                                PermissionHelper.openAppSettings(
                                    this@ImagePickerActivity
                                )
                            }
                        }

                        override fun onPermissionGranted() {
                            data
                        }
                    })
            }
        }

    private val data: Unit
        get() {
            presenter!!.abortLoading()
            presenter!!.loadImages(config!!.isFolderMode)
        }


    private fun captureImageWithPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        PermissionHelper.checkPermission(
            this,
            Manifest.permission.CAMERA,
            object : PermissionHelper.PermissionAskListener {
                override fun onNeedPermission() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_CAMERA_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_CAMERA_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    snackBar!!.show(
                        R.string.imagepicker_msg_no_camera_permission
                    ) {
                        PermissionHelper.openAppSettings(
                            this@ImagePickerActivity
                        )
                    }
                }

                override fun onPermissionGranted() {
                    captureImage()
                }
            })
    }


    private fun captureImage() {
        if (!CameraHelper.checkCameraAvailability(this)) {
            return
        }
        presenter!!.captureImage(this, config, ImagePicker.Companion.RC_PICK_IMAGES_CUSTOM)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.Companion.RC_PICK_IMAGES_CUSTOM && resultCode == RESULT_OK) {
            presenter!!.finishCaptureImage(this, data, config!!)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                run {
                    if (PermissionHelper.hasGranted(grantResults)) {
                        logger.d("Write External permission granted")
                        data
                        return
                    }
                    logger.e(
                        "Permission not granted: results len = " + grantResults.size +
                                " Result code = " + (if (grantResults.size > 0) grantResults[0] else "(empty)")
                    )
                    finish()
                }
                run {
                    if (PermissionHelper.hasGranted(grantResults)) {
                        logger.d("Camera permission granted")
                        captureImage()
                        return
                    }
                    logger.e(
                        "Permission not granted: results len = " + grantResults.size +
                                " Result code = " + (if (grantResults.size > 0) grantResults[0] else "(empty)")
                    )
                }
            }

            Config.RC_CAMERA_PERMISSION -> {
                if (PermissionHelper.hasGranted(grantResults)) {
                    logger.d("Camera permission granted")
                    captureImage()
                    return
                }
                logger.e(
                    "Permission not granted: results len = " + grantResults.size +
                            " Result code = " + (if (grantResults.size > 0) grantResults[0] else "(empty)")
                )
            }

            else -> {
                logger.d("Got unexpected permission result: $requestCode")
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (handler == null) {
            handler = Handler()
        }
        observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                data
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            false,
            observer!!
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        if (presenter != null) {
            presenter!!.abortLoading()
            presenter!!.detachView()
        }

        if (observer != null) {
            contentResolver.unregisterContentObserver(observer!!)
            observer = null
        }

        if (handler != null) {
            handler!!.removeCallbacksAndMessages(null)
            handler = null
        }
    }

    override fun onBackPressed() {
        recyclerViewManager!!.handleBack(object : OnBackAction {
            override fun onBackToFolder() {
                invalidateToolbar()
            }

            override fun onFinishImagePicker() {
                setResult(RESULT_CANCELED)
                finish()
            }
        })
    }

    /**
     * MVP view methods
     */
    override fun showLoading(isLoading: Boolean) {
        progressWheel!!.visibility =
            if (isLoading) View.VISIBLE else View.GONE
        recyclerView!!.visibility =
            if (isLoading) View.GONE else View.VISIBLE
        emptyLayout!!.visibility = View.GONE
    }



    override fun showFetchCompleted(images: List<Image>?, folders: List<Folder>?) {
        if (config!!.isFolderMode) {
            setFolderAdapter(folders)
        } else {
            setImageAdapter(images, config!!.imageTitle!!)
        }
    }

    override fun showError(throwable: Throwable?) {
        var message = getString(R.string.imagepicker_error_unknown)
        if (throwable != null && throwable is NullPointerException) {
            message = getString(R.string.imagepicker_error_images_not_exist)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showEmpty() {
        progressWheel!!.visibility = View.GONE
        recyclerView!!.visibility = View.GONE
        emptyLayout!!.visibility = View.VISIBLE
    }

    override fun showCapturedImage() {
        dataWithPermission
    }

    override fun finishPickImages(images: List<Image?>?) {
        val data = Intent()
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, images as ArrayList<out Parcelable?>?)
        setResult(RESULT_OK, data)
        finish()
    }

}
