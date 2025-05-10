package com.callmangement.model.pos_distribution_form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OldMachineData {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("remarks")
    @Expose
    var remarks: Any? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("mobileNo")
    @Expose
    var mobileNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("updatedBy")
    @Expose
    var updatedBy: Any? = null

    @SerializedName("fpscode")
    @Expose
    var fpscode: String? = null

    @SerializedName("districtId")
    @Expose
    var districtId: Int? = null
        get() {
            if (field == null) return 0
            return field
        }

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: Any? = null

    @SerializedName("updatedOn")
    @Expose
    var updatedOn: Any? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("districtName")
    @Expose
    var districtName: String? = null

    @SerializedName("newMachine_Biometric_SeriallNo")
    @Expose
    var newMachineBiometricSeriallNo: Any? = null

    @SerializedName("newMachine_IMEI_2")
    @Expose
    var newMachineIMEI2: Any? = null

    @SerializedName("accessoriesProvided")
    @Expose
    var accessoriesProvided: Any? = null

    @SerializedName("oldMachineVenderid")
    @Expose
    var oldMachineVenderid: Any? = null

    @SerializedName("tranId")
    @Expose
    var tranId: Any? = null

    @SerializedName("tranDate")
    @Expose
    var tranDate: Any? = null

    @SerializedName("oldVenderName")
    @Expose
    var oldVenderName: Any? = null

    @SerializedName("tranDateStr")
    @Expose
    var tranDateStr: Any? = null

    @SerializedName("modelName")
    @Expose
    var modelName: Any? = null

    @SerializedName("dealerName")
    @Expose
    var dealerName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("blockName")
    @Expose
    var blockName: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("newVenderName")
    @Expose
    var newVenderName: Any? = null

    @SerializedName("imageContent")
    @Expose
    var imageContent: Any? = null

    @SerializedName("oldMachine_Biometric_SeriallNo")
    @Expose
    var oldMachineBiometricSeriallNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("whetherOldMachProvdedForReplacmnt")
    @Expose
    var whetherOldMachProvdedForReplacmnt: Boolean? = null

    @SerializedName("oldMachineWorkingStatus")
    @Expose
    var oldMachineWorkingStatus: Any? = null

    @SerializedName("printStatus")
    @Expose
    var printStatus: Boolean? = null

    @SerializedName("printedOn")
    @Expose
    var printedOn: Any? = null

    @SerializedName("ipAddress")
    @Expose
    var ipAddress: Any? = null

    @SerializedName("newMachine_IMEI_1")
    @Expose
    var newMachineIMEI1: Any? = null

    @SerializedName("isUploadFilledForm")
    @Expose
    var isUploadFilledForm: Boolean? = null

    @SerializedName("uploadFilledFormPath")
    @Expose
    var uploadFilledFormPath: Any? = null

    @SerializedName("uploadFilledFormContentType")
    @Expose
    var uploadFilledFormContentType: Any? = null

    @SerializedName("isCompleteWithSatisfactorily")
    @Expose
    var isCompleteWithSatisfactorily: Boolean? = null

    @SerializedName("receivedRemark")
    @Expose
    var receivedRemark: Any? = null

    @SerializedName("uploadFilledFormContentSize")
    @Expose
    var uploadFilledFormContentSize: Any? = null

    @SerializedName("newMachine_SerialNo")
    @Expose
    var newMachineSerialNo: Any? = null

    @SerializedName("oldMachine_DeviceCode")
    @Expose
    var oldMachineDeviceCode: String? = null

    @SerializedName("equipmentModelId")
    @Expose
    var equipmentModelId: Any? = null

    @SerializedName("newMachine_DeviceCode")
    @Expose
    var newMachineDeviceCode: Any? = null

    @SerializedName("ticketNo")
    @Expose
    var ticketNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("oldMachine_SerialNo")
    @Expose
    var oldMachineSerialNo: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("newMachineVenderid")
    @Expose
    var newMachineVenderid: Any? = null

    @SerializedName("newMachineOrderNo")
    @Expose
    var newMachineOrderNo: Any? = null

    @SerializedName("newMachineCartonNo")
    @Expose
    var newMachineCartonNo: Any? = null

    @SerializedName("noOfTimesPrint")
    @Expose
    var noOfTimesPrint: Any? = null
}
