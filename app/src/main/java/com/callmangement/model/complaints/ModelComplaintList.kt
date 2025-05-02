package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelComplaintList : Serializable {
    @JvmField
    var registrationComplainDateTimeStamp: Long = 0

    //   @SerializedName("complainRegDate") private ModelComplaintRegistrationDate complainRegDate = new ModelComplaintRegistrationDate();
    @SerializedName("check_csrf")
    var check_csrf: String? = null

    @SerializedName("createdBy")
    var createdBy: String? = null

    @SerializedName("complainStatusId")
    var complainStatusId: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @JvmField
    @SerializedName("complainStatus")
    var complainStatus: String? = null

    @SerializedName("complainTypeId")
    var complainTypeId: String? = null

    @SerializedName("complainAssignTo")
    var complainAssignTo: String? = null

    @SerializedName("assignedUserID")
    var assignedUserID: String? = null

    @SerializedName("complainAssignDate")
    var complainAssignDate: String? = null

    @JvmField
    @SerializedName("fpscode")
    var fpscode: String? = null

    @JvmField
    @SerializedName("mobileNo")
    var mobileNo: String? = null

    @JvmField
    @SerializedName("district")
    var district: String? = null

    @JvmField
    @SerializedName("customerName")
    var customerName: String? = null

    @JvmField
    @SerializedName("tehsil")
    var tehsil: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @JvmField
    @SerializedName("complainRegNo")
    var complainRegNo: String? = null

    @SerializedName("isPhysicalDamage")
    var physicalDamage: Boolean? = null

    @JvmField
    @SerializedName("seremark")
    var seremark: String? = null

    @JvmField
    @SerializedName("complainDesc")
    var complainDesc: String? = null

    @JvmField
    @SerializedName("complainType")
    var complainType: String? = null

    @SerializedName("customerId")
    var customerId: String? = null

    @SerializedName("orderBySrNo")
    var orderBySrNo: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("districtId")
    var districtId: String? = null

    @JvmField
    @SerializedName("complainId")
    var complainId: String? = null

    @SerializedName("tehsilId")
    var tehsilId: String? = null

    //    public ModelComplaintRegistrationDate getComplainRegDate() {
    //        return complainRegDate;
    //    }
    //
    //    public void setComplainRegDate(ModelComplaintRegistrationDate complainRegDate) {
    //        this.complainRegDate = complainRegDate;
    //    }
    @JvmField
    @SerializedName("replacedPartsDetail")
    var replacedPartsDetail: String? = null

    @JvmField
    @SerializedName("courierServicesDetail")
    var courierServicesDetail: String? = null

    @JvmField
    @SerializedName("complainRegDateStr")
    var complainRegDateStr: String? = null

    @JvmField
    @SerializedName("imagePath")
    var imagePath: String? = null

    @JvmField
    @SerializedName("sermarkDateStr")
    var sermarkDateStr: String? = null

    @JvmField
    @SerializedName("challanNo")
    var challanNo: String? = null

    @SerializedName("complPartsIds")
    private var complPartsIds: String? = null

    @SerializedName("isSentToServiceCentreOn")
    private var isSentToServiceCentreOn: String? = null

    @SerializedName("isSentToServiceCentre")
    private var isSentToServiceCentre: String? = null

    @SerializedName("cntReptOnSerCenter")
    var cntReptOnSerCenter: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    fun getComplPartsIds(): String {
        if (complPartsIds == null || complPartsIds!!.isEmpty()) return ""
        return complPartsIds!!
    }

    fun setComplPartsIds(complPartsIds: String?) {
        this.complPartsIds = complPartsIds
    }

    fun getIsSentToServiceCentreOn(): String {
        if (isSentToServiceCentreOn == null || isSentToServiceCentreOn!!.isEmpty()) return ""
        return isSentToServiceCentreOn!!
    }

    fun setIsSentToServiceCentreOn(isSentToServiceCentreOn: String?) {
        this.isSentToServiceCentreOn = isSentToServiceCentreOn
    }

    fun getIsSentToServiceCentre(): String {
        if (isSentToServiceCentre == null || isSentToServiceCentre!!.isEmpty()) return "false"
        return isSentToServiceCentre!!
    }

    fun setIsSentToServiceCentre(isSentToServiceCentre: String?) {
        this.isSentToServiceCentre = isSentToServiceCentre
    }
}
