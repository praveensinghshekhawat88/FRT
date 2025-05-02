package com.callmangement.model.pos_distribution_form

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewMachineData {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("imageContent")
    @Expose
    var imageContent: Any? = null

    @SerializedName("printStatus")
    @Expose
    var printStatus: Boolean? = null

    @SerializedName("oldVenderName")
    @Expose
    var oldVenderName: Any? = null

    @SerializedName("newVenderName")
    @Expose
    var newVenderName: Any? = null

    @SerializedName("printedOn")
    @Expose
    var printedOn: Any? = null

    @SerializedName("modelName")
    @Expose
    var modelName: Any? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: Any? = null

    @SerializedName("newMachineOrderNo")
    @Expose
    var newMachineOrderNo: Int? = null

    @SerializedName("newMachineCartonNo")
    @Expose
    var newMachineCartonNo: Int? = null

    @SerializedName("oldMachineVenderid")
    @Expose
    var oldMachineVenderid: Any? = null

    @SerializedName("oldMachineWorkingStatus")
    @Expose
    var oldMachineWorkingStatus: Any? = null

    @SerializedName("equipmentModelId")
    @Expose
    var equipmentModelId: Any? = null

    @SerializedName("isCompleteWithSatisfactorily")
    @Expose
    var isCompleteWithSatisfactorily: Boolean? = null

    @SerializedName("newMachineVenderid")
    @Expose
    var newMachineVenderid: Any? = null

    @SerializedName("accessoriesProvided")
    @Expose
    var accessoriesProvided: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("oldMachine_SerialNo")
    @Expose
    var oldMachineSerialNo: Any? = null

    @JvmField
    @SerializedName("newMachine_IMEI_2")
    @Expose
    var newMachineIMEI2: String? = null

    @SerializedName("noOfTimesPrint")
    @Expose
    var noOfTimesPrint: Any? = null

    @SerializedName("receivedRemark")
    @Expose
    var receivedRemark: Any? = null

    @JvmField
    @SerializedName("newMachine_SerialNo")
    @Expose
    var newMachineSerialNo: String? = null

    @SerializedName("uploadFilledFormContentType")
    @Expose
    var uploadFilledFormContentType: Any? = null

    @SerializedName("isUploadFilledForm")
    @Expose
    var isUploadFilledForm: Boolean? = null

    @JvmField
    @SerializedName("newMachine_IMEI_1")
    @Expose
    var newMachineIMEI1: String? = null

    @SerializedName("uploadFilledFormPath")
    @Expose
    var uploadFilledFormPath: Any? = null

    @SerializedName("oldMachine_DeviceCode")
    @Expose
    var oldMachineDeviceCode: Any? = null

    @SerializedName("newMachine_DeviceCode")
    @Expose
    var newMachineDeviceCode: String? = null

    @SerializedName("districtName")
    @Expose
    var districtName: Any? = null

    @SerializedName("whetherOldMachProvdedForReplacmnt")
    @Expose
    var whetherOldMachProvdedForReplacmnt: Boolean? = null

    @SerializedName("oldMachine_Biometric_SeriallNo")
    @Expose
    var oldMachineBiometricSeriallNo: Any? = null

    @JvmField
    @SerializedName("newMachine_Biometric_SeriallNo")
    @Expose
    var newMachineBiometricSeriallNo: String? = null

    @SerializedName("createdOn")
    @Expose
    var createdOn: Any? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("tranDateStr")
    @Expose
    var tranDateStr: Any? = null

    @SerializedName("ticketNo")
    @Expose
    var ticketNo: Any? = null

    @SerializedName("ipAddress")
    @Expose
    var ipAddress: Any? = null

    @SerializedName("tranDate")
    @Expose
    var tranDate: Any? = null

    @SerializedName("uploadFilledFormContentSize")
    @Expose
    var uploadFilledFormContentSize: Any? = null

    @SerializedName("updatedOn")
    @Expose
    var updatedOn: Any? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("updatedBy")
    @Expose
    var updatedBy: Any? = null

    @SerializedName("mobileNo")
    @Expose
    var mobileNo: Any? = null

    @SerializedName("districtId")
    @Expose
    var districtId: Any? = null

    @SerializedName("fpscode")
    @Expose
    var fpscode: Any? = null

    @SerializedName("dealerName")
    @Expose
    var dealerName: Any? = null

    @SerializedName("remarks")
    @Expose
    var remarks: Any? = null

    @SerializedName("blockName")
    @Expose
    var blockName: Any? = null

    @SerializedName("tranId")
    @Expose
    var tranId: Any? = null
}
