package com.callmangement.ui.distributor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PosDistributionDetail : Serializable {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("districtName")
    @Expose
    var districtName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("isFormUploaded")
    @Expose
    var isFormUploaded: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("isPhotoUploaded")
    @Expose
    var isPhotoUploaded: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("newMachineOrderNo")
    @Expose
    var newMachineOrderNo: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("newMachineCartonNo")
    @Expose
    var newMachineCartonNo: Any? = null

    @SerializedName("accessoriesProvided")
    @Expose
    var accessoriesProvided: Any? = null

    @SerializedName("oldMachineWorkingStatus")
    @Expose
    var oldMachineWorkingStatus: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @JvmField
    @SerializedName("equipmentModelId")
    @Expose
    var equipmentModelId: Int? = null

    @JvmField
    @SerializedName("isCompleteWithSatisfactorily")
    @Expose
    var isCompleteWithSatisfactorily: Boolean? = null

    @JvmField
    @SerializedName("oldMachineVenderid")
    @Expose
    var oldMachineVenderid: Int? = null

    @JvmField
    @SerializedName("newMachineVenderid")
    @Expose
    var newMachineVenderid: Int? = null

    @JvmField
    @SerializedName("oldMachine_SerialNo")
    @Expose
    var oldMachineSerialNo: String? = null

    @SerializedName("newMachine_SerialNo")
    @Expose
    var newMachineSerialNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("receivedRemark")
    @Expose
    var receivedRemark: String? = null

    @SerializedName("newMachine_IMEI_1")
    @Expose
    var newMachineIMEI1: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("newMachine_IMEI_2")
    @Expose
    var newMachineIMEI2: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("uploadFilledFormContentType")
    @Expose
    var uploadFilledFormContentType: Any? = null

    @SerializedName("isUploadFilledForm")
    @Expose
    var isUploadFilledForm: Boolean? = null

    @SerializedName("newMachine_DeviceCode")
    @Expose
    var newMachineDeviceCode: String? = null

    @SerializedName("oldMachine_DeviceCode")
    @Expose
    var oldMachineDeviceCode: String? = null

    @SerializedName("uploadFilledFormPath")
    @Expose
    var uploadFilledFormPath: Any? = null

    @SerializedName("noOfTimesPrint")
    @Expose
    var noOfTimesPrint: Int? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Int? = null

    @SerializedName("newMachine_Biometric_SeriallNo")
    @Expose
    var newMachineBiometricSeriallNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @JvmField
    @SerializedName("tranDateStr")
    @Expose
    var tranDateStr: String? = null

    @SerializedName("modelName")
    @Expose
    var modelName: String? = null

    @SerializedName("oldVenderName")
    @Expose
    var oldVenderName: String? = null

    @JvmField
    @SerializedName("ticketNo")
    @Expose
    var ticketNo: String? = null

    @SerializedName("newVenderName")
    @Expose
    var newVenderName: String? = null

    @SerializedName("tranDate")
    @Expose
    var tranDate: TranDate? = null

    @SerializedName("printStatus")
    @Expose
    var printStatus: Boolean? = null

    @SerializedName("printedOn")
    @Expose
    var printedOn: String? = null

    @SerializedName("ipAddress")
    @Expose
    var ipAddress: String? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: Any? = null

    @SerializedName("imageContent")
    @Expose
    var imageContent: Any? = null

    @JvmField
    @SerializedName("whetherOldMachProvdedForReplacmnt")
    @Expose
    var whetherOldMachProvdedForReplacmnt: Boolean? = null

    @JvmField
    @SerializedName("oldMachine_Biometric_SeriallNo")
    @Expose
    var oldMachineBiometricSeriallNo: String? = null

    @SerializedName("uploadFilledFormContentSize")
    @Expose
    var uploadFilledFormContentSize: Any? = null

    @SerializedName("tranId")
    @Expose
    var tranId: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("dealerName")
    @Expose
    var dealerName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("remarks")
    @Expose
    var remarks: Any? = null

    @JvmField
    @SerializedName("blockName")
    @Expose
    var blockName: String? = null

    @SerializedName("districtId")
    @Expose
    var districtId: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("msg")
    @Expose
    var msg: Any? = null

    @SerializedName("updatedBy")
    @Expose
    var updatedBy: Int? = null

    @SerializedName("updatedOn")
    @Expose
    var updatedOn: Any? = null

    @JvmField
    @SerializedName("mobileNo")
    @Expose
    var mobileNo: String? = null

    @SerializedName("fpscode")
    @Expose
    var fpscode: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("upPhotoPath")
    @Expose
    var upPhotoPath: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("upFormPath")
    @Expose
    var upFormPath: String? = null
        get() {
            if (field == null) return ""
            return field
        }
}
