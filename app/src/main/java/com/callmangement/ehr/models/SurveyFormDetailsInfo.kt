package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SurveyFormDetailsInfo : Serializable {
    @JvmField
    @SerializedName("ticketNo")
    var ticketNo: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("address")
    var address: String? = null

    @SerializedName("createdBy")
    var createdBy: String? = null

    @SerializedName("isActive")
    var isActive: String? = null

    @SerializedName("createdOn")
    var createdOn: String? = null

    @SerializedName("updatedOn")
    var updatedOn: String? = null

    @SerializedName("updatedBy")
    var updatedBy: String? = null

    @JvmField
    @SerializedName("customerName")
    var customerName: String? = null

    @JvmField
    @SerializedName("gstin_No")
    var gstin_No: String? = null

    @JvmField
    @SerializedName("typeOfCall")
    var typeOfCall: String? = null

    @JvmField
    @SerializedName("statusId")
    var statusId: String? = null

    @JvmField
    @SerializedName("mobileNumber")
    var mobileNumber: String? = null

    @JvmField
    @SerializedName("billRemark")
    var billRemark: String? = null

    @JvmField
    @SerializedName("serveyFormId")
    var serveyFormId: String? = null

    @JvmField
    @SerializedName("typeOfCallId")
    var typeOfCallId: String? = null

    @JvmField
    @SerializedName("itemDetail")
    var itemDetail: String? = null

    @JvmField
    @SerializedName("accessesory")
    var accessesory: String? = null

    @JvmField
    @SerializedName("engineerName")
    var engineerName: String? = null

    @JvmField
    @SerializedName("installedMachineSpecification")
    var installedMachineSpecification: String? = null

    @SerializedName("formUploadedOnStr")
    var formUploadedOnStr: String? = null

    @JvmField
    @SerializedName("pointOfContact")
    var pointOfContact: String? = null

    @JvmField
    @SerializedName("customer_Remark")
    var customer_Remark: String? = null

    @SerializedName("installationDate")
    var installationDate: String? = null

    @JvmField
    @SerializedName("installationDone")
    var installationDone: String? = null

    @JvmField
    @SerializedName("purchaseOrderDtl")
    var purchaseOrderDtl: String? = null

    @SerializedName("formUploadedOn")
    var formUploadedOn: String? = null

    @JvmField
    @SerializedName("bill_ChallanNo")
    var bill_ChallanNo: String? = null

    @JvmField
    @SerializedName("installationDateStr")
    var installationDateStr: String? = null
}

