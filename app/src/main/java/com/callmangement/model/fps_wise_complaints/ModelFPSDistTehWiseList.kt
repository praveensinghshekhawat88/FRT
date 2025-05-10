package com.callmangement.model.fps_wise_complaints

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelFPSDistTehWiseList : Serializable {
    @SerializedName("status")
    var status: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("districtName")
    var districtName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("msg")
    var msg: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("tehsilId")
    var tehsilId: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("tehsilName")
    var tehsilName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("mobileNo")
    var mobileNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainDesc")
    var complainDesc: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainRegNo")
    var complainRegNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("fpscode")
    var fpscode: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainId")
    var complainId: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("fk_DistrictId")
    var fk_DistrictId: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainStatus")
    var complainStatus: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("customerNameEng")
    var customerNameEng: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainRegDate")
    var complainRegDate: String? = null
        get() {
            if (field == null) return ""
            return field
        }
}
