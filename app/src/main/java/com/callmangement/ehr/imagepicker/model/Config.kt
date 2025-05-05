package com.callmangement.ehr.imagepicker.model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils

/**
 * Created by hoanglam on 8/11/17.
 */
class Config : Parcelable {
    private var toolbarColor: String? = null
    private var statusBarColor: String? = null
    private var toolbarTextColor: String? = null
    private var toolbarIconColor: String? = null
    private var progressBarColor: String? = null
    private var backgroundColor: String? = null
    var isCameraOnly: Boolean = false
    var isMultipleMode: Boolean = false
    var isFolderMode: Boolean = false
    var isShowCamera: Boolean = false
    var maxSize: Int = 0
    var doneTitle: String? = null
    var folderTitle: String? = null
    var imageTitle: String? = null
    @JvmField
    var savePath: SavePath? = null
    var selectedImages: MutableList<Image>? = null


    constructor()

    protected constructor(`in`: Parcel) {
        this.toolbarColor = `in`.readString()
        this.statusBarColor = `in`.readString()
        this.toolbarTextColor = `in`.readString()
        this.toolbarIconColor = `in`.readString()
        this.progressBarColor = `in`.readString()
        this.backgroundColor = `in`.readString()
        this.isCameraOnly = `in`.readByte().toInt() != 0
        this.isMultipleMode = `in`.readByte().toInt() != 0
        this.isFolderMode = `in`.readByte().toInt() != 0
        this.isShowCamera = `in`.readByte().toInt() != 0
        this.maxSize = `in`.readInt()
        this.doneTitle = `in`.readString()
        this.folderTitle = `in`.readString()
        this.imageTitle = `in`.readString()
        this.savePath = `in`.readParcelable(
            SavePath::class.java.classLoader
        )
        this.selectedImages = `in`.createTypedArrayList<Image>(Image.Companion.CREATOR)
    }

    fun getToolbarColor(): Int {
        if (TextUtils.isEmpty(toolbarColor)) {
            return Color.parseColor("#212121")
        }
        return Color.parseColor(toolbarColor)
    }

    fun setToolbarColor(toolbarColor: String?) {
        this.toolbarColor = toolbarColor
    }

    fun getStatusBarColor(): Int {
        if (TextUtils.isEmpty(statusBarColor)) {
            return Color.parseColor("#000000")
        }
        return Color.parseColor(statusBarColor)
    }

    fun setStatusBarColor(statusBarColor: String?) {
        this.statusBarColor = statusBarColor
    }

    fun getToolbarTextColor(): Int {
        if (TextUtils.isEmpty(toolbarTextColor)) {
            return Color.parseColor("#FFFFFF")
        }
        return Color.parseColor(toolbarTextColor)
    }

    fun setToolbarTextColor(toolbarTextColor: String?) {
        this.toolbarTextColor = toolbarTextColor
    }

    fun getToolbarIconColor(): Int {
        if (TextUtils.isEmpty(toolbarIconColor)) {
            return Color.parseColor("#FFFFFF")
        }
        return Color.parseColor(toolbarIconColor)
    }

    fun setToolbarIconColor(toolbarIconColor: String?) {
        this.toolbarIconColor = toolbarIconColor
    }

    fun getProgressBarColor(): Int {
        if (TextUtils.isEmpty(progressBarColor)) {
            return Color.parseColor("#4CAF50")
        }
        return Color.parseColor(progressBarColor)
    }

    fun setProgressBarColor(progressBarColor: String?) {
        this.progressBarColor = progressBarColor
    }

    fun getBackgroundColor(): Int {
        if (TextUtils.isEmpty(backgroundColor)) {
            return Color.parseColor("#FFFFFF")
        }
        return Color.parseColor(backgroundColor)
    }

    fun setBackgroundColor(backgroundColor: String?) {
        this.backgroundColor = backgroundColor
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.toolbarColor)
        dest.writeString(this.statusBarColor)
        dest.writeString(this.toolbarTextColor)
        dest.writeString(this.toolbarIconColor)
        dest.writeString(this.progressBarColor)
        dest.writeString(this.backgroundColor)
        dest.writeByte(if (this.isCameraOnly) 1.toByte() else 0.toByte())
        dest.writeByte(if (this.isMultipleMode) 1.toByte() else 0.toByte())
        dest.writeByte(if (this.isFolderMode) 1.toByte() else 0.toByte())
        dest.writeByte(if (this.isShowCamera) 1.toByte() else 0.toByte())
        dest.writeInt(this.maxSize)
        dest.writeString(this.doneTitle)
        dest.writeString(this.folderTitle)
        dest.writeString(this.imageTitle)
        dest.writeParcelable(this.savePath, flags)
        dest.writeTypedList(this.selectedImages)
    }

    companion object {
        const val EXTRA_CONFIG: String = "ImagePickerConfig"
        const val EXTRA_IMAGES: String = "ImagePickerImages"


        //    public static final int RC_PICK_IMAGES = 100;
        //    public static final int RC_CAPTURE_IMAGE = 101;
        const val RC_WRITE_EXTERNAL_STORAGE_PERMISSION: Int = 102
        const val RC_CAMERA_PERMISSION: Int = 103


        const val MAX_SIZE: Int = Int.MAX_VALUE
        @JvmField
        val CREATOR: Parcelable.Creator<Config?> = object : Parcelable.Creator<Config?> {
            override fun createFromParcel(source: Parcel): Config? {
                return Config(source)
            }

            override fun newArray(size: Int): Array<Config?> {
                return arrayOfNulls(size)
            }
        }
    }
}

