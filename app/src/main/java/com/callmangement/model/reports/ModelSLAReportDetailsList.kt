package com.callmangement.model.reports

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelSLAReportDetailsList : Serializable {
    @JvmField
    @SerializedName("complainStatusId")
    var complainStatusId: String? = null

    @JvmField
    @SerializedName("complainStatus")
    var complainStatus: String? = null

    @JvmField
    @SerializedName("fpscode")
    var fpscode: String? = null

    @JvmField
    @SerializedName("mobileNo")
    var mobileNo: String? = null

    @JvmField
    @SerializedName("customerName")
    var customerName: String? = null

    @JvmField
    @SerializedName("complainRegNo")
    var complainRegNo: String? = null

    @JvmField
    @SerializedName("tehsil")
    var tehsil: String? = null

    @SerializedName("customerId")
    var customerId: String? = null

    @JvmField
    @SerializedName("complainRegDateStr")
    var complainRegDateStr: String? = null

    @JvmField
    @SerializedName("sermarkDateStr")
    var sermarkDateStr: String? = null

    @JvmField
    @SerializedName("resolveTime")
    var resolveTime: Int? = null
}
