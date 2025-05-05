package com.callmangement.ehr.imagepicker.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by hoanglam on 8/18/17.
 */
class SavePath : Parcelable {
    @JvmField
    val path: String?
    @JvmField
    val isFullPath: Boolean

    constructor(path: String?, isFullPath: Boolean) {
        this.path = path
        this.isFullPath = isFullPath
    }

    protected constructor(`in`: Parcel) {
        this.path = `in`.readString()
        this.isFullPath = `in`.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.path)
        dest.writeByte(if (this.isFullPath) 1.toByte() else 0.toByte())
    }

    companion object {
        val DEFAULT: SavePath = SavePath("Camera", false)

        @JvmField
        val CREATOR: Parcelable.Creator<SavePath?> = object : Parcelable.Creator<SavePath?> {
            override fun createFromParcel(source: Parcel): SavePath? {
                return SavePath(source)
            }

            override fun newArray(size: Int): Array<SavePath?> {
                return arrayOfNulls(size)
            }
        }
    }
}
