/*
 * Created by Nguyen Hoang Lam
 * Date: ${DATE}
 */
package com.callmangement.ehr.imagepicker.ui.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.model.SavePath
import com.callmangement.ehr.imagepicker.ui.camera.CameraActivty
import com.callmangement.R

/**
 * Created by hoanglam on 8/4/16.
 */
class ImagePicker(builder: Builder) {
    protected var config: Config = builder.config

    internal class ActivityBuilder(private val activity: Activity) : Builder(
        activity
    ) {
        override fun start(resultCode: Int) {
            RC_PICK_IMAGES_CUSTOM = resultCode
            val intent: Intent
            if (!config.isCameraOnly) {
                intent = Intent(activity, ImagePickerActivity::class.java)
                intent.putExtra(Config.EXTRA_CONFIG, config)
                activity.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM)
            } else {
                intent = Intent(activity, CameraActivty::class.java)
                intent.putExtra(Config.EXTRA_CONFIG, config)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                activity.overridePendingTransition(0, 0)
                activity.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM)
            }
        }
    }

    internal class FragmentBuilder(private val fragment: Fragment) : Builder(
        fragment
    ) {
        override fun start(resultCode: Int) {
            val intent: Intent
            if (!config.isCameraOnly) {
                intent = Intent(fragment.activity, ImagePickerActivity::class.java)
                intent.putExtra(Config.EXTRA_CONFIG, config)
                fragment.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM)
            } else {
                intent = Intent(fragment.activity, CameraActivty::class.java)
                intent.putExtra(Config.EXTRA_CONFIG, config)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                fragment.requireActivity().overridePendingTransition(0, 0)
                fragment.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM)
            }
        }
    }

    abstract class Builder : BaseBuilder {
        constructor(activity: Activity) : super(activity)

        constructor(fragment: Fragment) : super(fragment.requireContext())

        fun setToolbarColor(toolbarColor: String?): Builder {
            config.setToolbarColor(toolbarColor)
            return this
        }

        fun setStatusBarColor(statusBarColor: String?): Builder {
            config.setStatusBarColor(statusBarColor)
            return this
        }

        fun setToolbarTextColor(toolbarTextColor: String?): Builder {
            config.setToolbarTextColor(toolbarTextColor)
            return this
        }

        fun setToolbarIconColor(toolbarIconColor: String?): Builder {
            config.setToolbarIconColor(toolbarIconColor)
            return this
        }

        fun setProgressBarColor(progressBarColor: String?): Builder {
            config.setProgressBarColor(progressBarColor)
            return this
        }

        fun setBackgroundColor(backgroundColor: String?): Builder {
            config.setBackgroundColor(backgroundColor)
            return this
        }

        fun setCameraOnly(isCameraOnly: Boolean): Builder {
            config.isCameraOnly = isCameraOnly
            return this
        }

        fun setMultipleMode(isMultipleMode: Boolean): Builder {
            config.isMultipleMode = isMultipleMode
            return this
        }

        fun setFolderMode(isFolderMode: Boolean): Builder {
            config.isFolderMode = isFolderMode
            return this
        }

        fun setShowCamera(isShowCamera: Boolean): Builder {
            config.isShowCamera = isShowCamera
            return this
        }

        fun setMaxSize(maxSize: Int): Builder {
            config.maxSize = maxSize
            return this
        }

        fun setDoneTitle(doneTitle: String?): Builder {
            config.doneTitle = doneTitle
            return this
        }

        fun setFolderTitle(folderTitle: String?): Builder {
            config.folderTitle = folderTitle
            return this
        }

        fun setImageTitle(imageTitle: String?): Builder {
            config.imageTitle = imageTitle
            return this
        }

        fun setSavePath(path: String?): Builder {
            config.savePath = SavePath(path, false)
            return this
        }

        fun setSelectedImages(selectedImages: MutableList<Image>?): Builder {
            config.selectedImages = selectedImages
            return this
        }

        abstract fun start(resultCode: Int)
    }

    abstract class BaseBuilder(context: Context) {
        var config: Config =
            Config()

        init {
            val resources = context.resources
            config.isCameraOnly = false
            config.isMultipleMode = true
            config.isFolderMode = true
            config.isShowCamera = true
            config.maxSize = Config.MAX_SIZE
            config.doneTitle = resources.getString(R.string.imagepicker_action_done)
            config.folderTitle = resources.getString(R.string.imagepicker_title_folder)
            config.imageTitle = resources.getString(R.string.imagepicker_title_image)
            config.savePath = SavePath.DEFAULT
            config.selectedImages =
                ArrayList()
        }
    }

    companion object {
        @JvmField
        var RC_PICK_IMAGES_CUSTOM: Int = 1

        @JvmStatic
        fun with(activity: Activity): Builder {
            return ActivityBuilder(activity)
        }

        fun with(fragment: Fragment): Builder {
            return FragmentBuilder(fragment)
        }
    }
}

