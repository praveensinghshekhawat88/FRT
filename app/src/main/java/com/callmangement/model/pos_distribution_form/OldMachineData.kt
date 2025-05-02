
package com.callmangement.model.pos_distribution_form;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OldMachineData {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("remarks")
    @Expose
    private Object remarks;
    @SerializedName("createdBy")
    @Expose
    private Object createdBy;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("updatedBy")
    @Expose
    private Object updatedBy;
    @SerializedName("fpscode")
    @Expose
    private String fpscode;
    @SerializedName("districtId")
    @Expose
    private Integer districtId;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("createdOn")
    @Expose
    private Object createdOn;
    @SerializedName("updatedOn")
    @Expose
    private Object updatedOn;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("districtName")
    @Expose
    private String districtName;
    @SerializedName("newMachine_Biometric_SeriallNo")
    @Expose
    private Object newMachineBiometricSeriallNo;
    @SerializedName("newMachine_IMEI_2")
    @Expose
    private Object newMachineIMEI2;
    @SerializedName("accessoriesProvided")
    @Expose
    private Object accessoriesProvided;
    @SerializedName("oldMachineVenderid")
    @Expose
    private Object oldMachineVenderid;
    @SerializedName("tranId")
    @Expose
    private Object tranId;
    @SerializedName("tranDate")
    @Expose
    private Object tranDate;
    @SerializedName("oldVenderName")
    @Expose
    private Object oldVenderName;
    @SerializedName("tranDateStr")
    @Expose
    private Object tranDateStr;
    @SerializedName("modelName")
    @Expose
    private Object modelName;
    @SerializedName("dealerName")
    @Expose
    private String dealerName;
    @SerializedName("blockName")
    @Expose
    private String blockName;
    @SerializedName("newVenderName")
    @Expose
    private Object newVenderName;
    @SerializedName("imageContent")
    @Expose
    private Object imageContent;
    @SerializedName("oldMachine_Biometric_SeriallNo")
    @Expose
    private String oldMachineBiometricSeriallNo;
    @SerializedName("whetherOldMachProvdedForReplacmnt")
    @Expose
    private Boolean whetherOldMachProvdedForReplacmnt;
    @SerializedName("oldMachineWorkingStatus")
    @Expose
    private Object oldMachineWorkingStatus;
    @SerializedName("printStatus")
    @Expose
    private Boolean printStatus;
    @SerializedName("printedOn")
    @Expose
    private Object printedOn;
    @SerializedName("ipAddress")
    @Expose
    private Object ipAddress;
    @SerializedName("newMachine_IMEI_1")
    @Expose
    private Object newMachineIMEI1;
    @SerializedName("isUploadFilledForm")
    @Expose
    private Boolean isUploadFilledForm;
    @SerializedName("uploadFilledFormPath")
    @Expose
    private Object uploadFilledFormPath;
    @SerializedName("uploadFilledFormContentType")
    @Expose
    private Object uploadFilledFormContentType;
    @SerializedName("isCompleteWithSatisfactorily")
    @Expose
    private Boolean isCompleteWithSatisfactorily;
    @SerializedName("receivedRemark")
    @Expose
    private Object receivedRemark;
    @SerializedName("uploadFilledFormContentSize")
    @Expose
    private Object uploadFilledFormContentSize;
    @SerializedName("newMachine_SerialNo")
    @Expose
    private Object newMachineSerialNo;
    @SerializedName("oldMachine_DeviceCode")
    @Expose
    private String oldMachineDeviceCode;
    @SerializedName("equipmentModelId")
    @Expose
    private Object equipmentModelId;
    @SerializedName("newMachine_DeviceCode")
    @Expose
    private Object newMachineDeviceCode;
    @SerializedName("ticketNo")
    @Expose
    private String ticketNo;
    @SerializedName("oldMachine_SerialNo")
    @Expose
    private String oldMachineSerialNo;
    @SerializedName("newMachineVenderid")
    @Expose
    private Object newMachineVenderid;
    @SerializedName("newMachineOrderNo")
    @Expose
    private Object newMachineOrderNo;
    @SerializedName("newMachineCartonNo")
    @Expose
    private Object newMachineCartonNo;
    @SerializedName("noOfTimesPrint")
    @Expose
    private Object noOfTimesPrint;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public String getMobileNo() {
        if (mobileNo == null)
            return "";
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public Integer getDistrictId() {
        if (districtId == null)
            return 0;
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Object getNewMachineBiometricSeriallNo() {
        return newMachineBiometricSeriallNo;
    }

    public void setNewMachineBiometricSeriallNo(Object newMachineBiometricSeriallNo) {
        this.newMachineBiometricSeriallNo = newMachineBiometricSeriallNo;
    }

    public Object getNewMachineIMEI2() {
        return newMachineIMEI2;
    }

    public void setNewMachineIMEI2(Object newMachineIMEI2) {
        this.newMachineIMEI2 = newMachineIMEI2;
    }

    public Object getAccessoriesProvided() {
        return accessoriesProvided;
    }

    public void setAccessoriesProvided(Object accessoriesProvided) {
        this.accessoriesProvided = accessoriesProvided;
    }

    public Object getOldMachineVenderid() {
        return oldMachineVenderid;
    }

    public void setOldMachineVenderid(Object oldMachineVenderid) {
        this.oldMachineVenderid = oldMachineVenderid;
    }

    public Object getTranId() {
        return tranId;
    }

    public void setTranId(Object tranId) {
        this.tranId = tranId;
    }

    public Object getTranDate() {
        return tranDate;
    }

    public void setTranDate(Object tranDate) {
        this.tranDate = tranDate;
    }

    public Object getOldVenderName() {
        return oldVenderName;
    }

    public void setOldVenderName(Object oldVenderName) {
        this.oldVenderName = oldVenderName;
    }

    public Object getTranDateStr() {
        return tranDateStr;
    }

    public void setTranDateStr(Object tranDateStr) {
        this.tranDateStr = tranDateStr;
    }

    public Object getModelName() {
        return modelName;
    }

    public void setModelName(Object modelName) {
        this.modelName = modelName;
    }

    public String getDealerName() {
        if (dealerName == null)
            return "";
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getBlockName() {
        if (blockName == null)
            return "";
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public Object getNewVenderName() {
        return newVenderName;
    }

    public void setNewVenderName(Object newVenderName) {
        this.newVenderName = newVenderName;
    }

    public Object getImageContent() {
        return imageContent;
    }

    public void setImageContent(Object imageContent) {
        this.imageContent = imageContent;
    }

    public String getOldMachineBiometricSeriallNo() {
        if (oldMachineBiometricSeriallNo == null)
            return "";
        return oldMachineBiometricSeriallNo;
    }

    public void setOldMachineBiometricSeriallNo(String oldMachineBiometricSeriallNo) {
        this.oldMachineBiometricSeriallNo = oldMachineBiometricSeriallNo;
    }

    public Boolean getWhetherOldMachProvdedForReplacmnt() {
        return whetherOldMachProvdedForReplacmnt;
    }

    public void setWhetherOldMachProvdedForReplacmnt(Boolean whetherOldMachProvdedForReplacmnt) {
        this.whetherOldMachProvdedForReplacmnt = whetherOldMachProvdedForReplacmnt;
    }

    public Object getOldMachineWorkingStatus() {
        return oldMachineWorkingStatus;
    }

    public void setOldMachineWorkingStatus(Object oldMachineWorkingStatus) {
        this.oldMachineWorkingStatus = oldMachineWorkingStatus;
    }

    public Boolean getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(Boolean printStatus) {
        this.printStatus = printStatus;
    }

    public Object getPrintedOn() {
        return printedOn;
    }

    public void setPrintedOn(Object printedOn) {
        this.printedOn = printedOn;
    }

    public Object getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(Object ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Object getNewMachineIMEI1() {
        return newMachineIMEI1;
    }

    public void setNewMachineIMEI1(Object newMachineIMEI1) {
        this.newMachineIMEI1 = newMachineIMEI1;
    }

    public Boolean getIsUploadFilledForm() {
        return isUploadFilledForm;
    }

    public void setIsUploadFilledForm(Boolean isUploadFilledForm) {
        this.isUploadFilledForm = isUploadFilledForm;
    }

    public Object getUploadFilledFormPath() {
        return uploadFilledFormPath;
    }

    public void setUploadFilledFormPath(Object uploadFilledFormPath) {
        this.uploadFilledFormPath = uploadFilledFormPath;
    }

    public Object getUploadFilledFormContentType() {
        return uploadFilledFormContentType;
    }

    public void setUploadFilledFormContentType(Object uploadFilledFormContentType) {
        this.uploadFilledFormContentType = uploadFilledFormContentType;
    }

    public Boolean getIsCompleteWithSatisfactorily() {
        return isCompleteWithSatisfactorily;
    }

    public void setIsCompleteWithSatisfactorily(Boolean isCompleteWithSatisfactorily) {
        this.isCompleteWithSatisfactorily = isCompleteWithSatisfactorily;
    }

    public Object getReceivedRemark() {
        return receivedRemark;
    }

    public void setReceivedRemark(Object receivedRemark) {
        this.receivedRemark = receivedRemark;
    }

    public Object getUploadFilledFormContentSize() {
        return uploadFilledFormContentSize;
    }

    public void setUploadFilledFormContentSize(Object uploadFilledFormContentSize) {
        this.uploadFilledFormContentSize = uploadFilledFormContentSize;
    }

    public Object getNewMachineSerialNo() {
        return newMachineSerialNo;
    }

    public void setNewMachineSerialNo(Object newMachineSerialNo) {
        this.newMachineSerialNo = newMachineSerialNo;
    }

    public String getOldMachineDeviceCode() {
        return oldMachineDeviceCode;
    }

    public void setOldMachineDeviceCode(String oldMachineDeviceCode) {
        this.oldMachineDeviceCode = oldMachineDeviceCode;
    }

    public Object getEquipmentModelId() {
        return equipmentModelId;
    }

    public void setEquipmentModelId(Object equipmentModelId) {
        this.equipmentModelId = equipmentModelId;
    }

    public Object getNewMachineDeviceCode() {
        return newMachineDeviceCode;
    }

    public void setNewMachineDeviceCode(Object newMachineDeviceCode) {
        this.newMachineDeviceCode = newMachineDeviceCode;
    }

    public String getTicketNo() {
        if (ticketNo == null)
            return "";
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getOldMachineSerialNo() {
        if (oldMachineSerialNo == null)
            return "";
        return oldMachineSerialNo;
    }

    public void setOldMachineSerialNo(String oldMachineSerialNo) {
        this.oldMachineSerialNo = oldMachineSerialNo;
    }

    public Object getNewMachineVenderid() {
        return newMachineVenderid;
    }

    public void setNewMachineVenderid(Object newMachineVenderid) {
        this.newMachineVenderid = newMachineVenderid;
    }

    public Object getNewMachineOrderNo() {
        return newMachineOrderNo;
    }

    public void setNewMachineOrderNo(Object newMachineOrderNo) {
        this.newMachineOrderNo = newMachineOrderNo;
    }

    public Object getNewMachineCartonNo() {
        return newMachineCartonNo;
    }

    public void setNewMachineCartonNo(Object newMachineCartonNo) {
        this.newMachineCartonNo = newMachineCartonNo;
    }

    public Object getNoOfTimesPrint() {
        return noOfTimesPrint;
    }

    public void setNoOfTimesPrint(Object noOfTimesPrint) {
        this.noOfTimesPrint = noOfTimesPrint;
    }

}
