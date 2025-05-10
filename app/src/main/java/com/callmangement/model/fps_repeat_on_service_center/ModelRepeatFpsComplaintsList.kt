package com.callmangement.model.fps_repeat_on_service_center

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelRepeatFpsComplaintsList : Serializable {
    @SerializedName("getStatus")
    @Expose
    var getStatus: Any? = null

    @SerializedName("fromDate")
    @Expose
    var fromDate: Any? = null

    @SerializedName("toDate")
    @Expose
    var toDate: Any? = null

    @SerializedName("check_csrf")
    @Expose
    var checkCsrf: Any? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("isSentToServiceCentreOn")
    @Expose
    var isSentToServiceCentreOn: Any? = null

    @SerializedName("complainAssignDate")
    @Expose
    var complainAssignDate: Any? = null

    @SerializedName("assignedUserID")
    @Expose
    var assignedUserID: Any? = null

    @SerializedName("isResolvedInLessTwoDays")
    @Expose
    var isResolvedInLessTwoDays: Boolean? = null

    @SerializedName("complainAssignTo")
    @Expose
    var complainAssignTo: Any? = null

    @SerializedName("customerName")
    @Expose
    var customerName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("mobileNo")
    @Expose
    var mobileNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("tehsil")
    @Expose
    var tehsil: Any? = null

    @SerializedName("district")
    @Expose
    var district: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("fpscode")
    @Expose
    var fpscode: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("districtId")
    @Expose
    var districtId: Any? = null

    @SerializedName("complainRegNo")
    @Expose
    var complainRegNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainDesc")
    @Expose
    var complainDesc: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("updatedBy")
    @Expose
    var updatedBy: Any? = null

    @SerializedName("scremark")
    @Expose
    var scremark: Any? = null

    @SerializedName("seremark")
    @Expose
    var seremark: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("challanNo")
    @Expose
    var challanNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainId")
    @Expose
    var complainId: Any? = null

    @SerializedName("tehsilId")
    @Expose
    var tehsilId: Any? = null

    @SerializedName("complainStatusOnThisDate")
    @Expose
    var complainStatusOnThisDate: Any? = null

    @SerializedName("customerId")
    @Expose
    var customerId: Any? = null

    @SerializedName("orderBySrNo")
    @Expose
    var orderBySrNo: Any? = null

    @SerializedName("sermarkDate")
    @Expose
    var sermarkDate: Any? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: Any? = null

    @SerializedName("complainType")
    @Expose
    var complainType: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("imagePath")
    @Expose
    var imagePath: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("updatedOn")
    @Expose
    var updatedOn: Any? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("resolveTime")
    @Expose
    var resolveTime: Any? = null

    @SerializedName("sladays_2")
    @Expose
    var sladays2: Any? = null

    @SerializedName("sladays_RD")
    @Expose
    var sladaysRD: Any? = null

    @SerializedName("complainStatus")
    @Expose
    var complainStatus: Any? = null

    @SerializedName("complainTypeId")
    @Expose
    var complainTypeId: Any? = null

    @SerializedName("complainRegDate")
    @Expose
    var complainRegDate: Any? = null

    @SerializedName("isPhysicalDamage")
    @Expose
    var isPhysicalDamage: Boolean? = null

    @SerializedName("complainStatusId")
    @Expose
    var complainStatusId: Any? = null

    @SerializedName("sermarkDateStr")
    @Expose
    var sermarkDateStr: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("screplacedPartsDetail")
    @Expose
    var screplacedPartsDetail: Any? = null

    @SerializedName("sccourierServicesDetail")
    @Expose
    var sccourierServicesDetail: Any? = null

    @SerializedName("courierServicesDetail")
    @Expose
    var courierServicesDetail: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("complainRegDateStr")
    @Expose
    var complainRegDateStr: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("replacedPartsDetail")
    @Expose
    var replacedPartsDetail: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("cntReptOnSerCenter")
    @Expose
    var cntReptOnSerCenter: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("isSentToServiceCentreOnStr")
    @Expose
    var isSentToServiceCentreOnStr: String? = null
        get() {
            if (field == null) return ""
            return field
        }
}
