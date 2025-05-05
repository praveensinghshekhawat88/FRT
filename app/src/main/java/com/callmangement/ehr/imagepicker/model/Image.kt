package com.callmangement.ehr.imagepicker.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by hoanglam on 7/31/16.
 */
class Image : Parcelable {
    var id: Long
    var name: String?
    @JvmField
    var path: String?

    constructor(id: Long, name: String?, path: String?) {
        this.id = id
        this.name = name
        this.path = path
    }

    protected constructor(`in`: Parcel) {
        this.id = `in`.readLong()
        this.name = `in`.readString()
        this.path = `in`.readString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val image = o as Image
        return image.path.equals(path, ignoreCase = true)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(this.id)
        dest.writeString(this.name)
        dest.writeString(this.path)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Image?> = object : Parcelable.Creator<Image?> {
            override fun createFromParcel(source: Parcel): Image? {
                return Image(source)
            }

            override fun newArray(size: Int): Array<Image?> {
                return arrayOfNulls(size)
            }
        }
    }
}
