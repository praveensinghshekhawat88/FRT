
package com.callmangement.ui.distributor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PosDistributionDetail implements Serializable {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("districtName")
    @Expose
    private String districtName;
    @SerializedName("isFormUploaded")
    @Expose
    private String isFormUploaded;
    @SerializedName("isPhotoUploaded")
    @Expose
    private String isPhotoUploaded;
    @SerializedName("newMachineOrderNo")
    @Expose
    private Integer newMachineOrderNo;
    @SerializedName("newMachineCartonNo")
    @Expose
    private Object newMachineCartonNo;
    @SerializedName("accessoriesProvided")
    @Expose
    private Object accessoriesProvided;
    @SerializedName("oldMachineWorkingStatus")
    @Expose
    private String oldMachineWorkingStatus;
    @SerializedName("equipmentModelId")
    @Expose
    private Integer equipmentModelId;
    @SerializedName("isCompleteWithSatisfactorily")
    @Expose
    private Boolean isCompleteWithSatisfactorily;
    @SerializedName("oldMachineVenderid")
    @Expose
    private Integer oldMachineVenderid;
    @SerializedName("newMachineVenderid")
    @Expose
    private Integer newMachineVenderid;
    @SerializedName("oldMachine_SerialNo")
    @Expose
    private String oldMachineSerialNo;
    @SerializedName("newMachine_SerialNo")
    @Expose
    private String newMachineSerialNo;
    @SerializedName("receivedRemark")
    @Expose
    private String receivedRemark;
    @SerializedName("newMachine_IMEI_1")
    @Expose
    private String newMachineIMEI1;
    @SerializedName("newMachine_IMEI_2")
    @Expose
    private String newMachineIMEI2;
    @SerializedName("uploadFilledFormContentType")
    @Expose
    private Object uploadFilledFormContentType;
    @SerializedName("isUploadFilledForm")
    @Expose
    private Boolean isUploadFilledForm;
    @SerializedName("newMachine_DeviceCode")
    @Expose
    private String newMachineDeviceCode;
    @SerializedName("oldMachine_DeviceCode")
    @Expose
    private String oldMachineDeviceCode;
    @SerializedName("uploadFilledFormPath")
    @Expose
    private Object uploadFilledFormPath;
    @SerializedName("noOfTimesPrint")
    @Expose
    private Integer noOfTimesPrint;
    @SerializedName("createdBy")
    @Expose
    private Integer createdBy;
    @SerializedName("newMachine_Biometric_SeriallNo")
    @Expose
    private String newMachineBiometricSeriallNo;
    @SerializedName("tranDateStr")
    @Expose
    private String tranDateStr;
    @SerializedName("modelName")
    @Expose
    private String modelName;
    @SerializedName("oldVenderName")
    @Expose
    private String oldVenderName;
    @SerializedName("ticketNo")
    @Expose
    private String ticketNo;
    @SerializedName("newVenderName")
    @Expose
    private String newVenderName;
    @SerializedName("tranDate")
    @Expose
    private TranDate tranDate;
    @SerializedName("printStatus")
    @Expose
    private Boolean printStatus;
    @SerializedName("printedOn")
    @Expose
    private String printedOn;
    @SerializedName("ipAddress")
    @Expose
    private String ipAddress;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("createdOn")
    @Expose
    private Object createdOn;
    @SerializedName("imageContent")
    @Expose
    private Object imageContent;
    @SerializedName("whetherOldMachProvdedForReplacmnt")
    @Expose
    private Boolean whetherOldMachProvdedForReplacmnt;
    @SerializedName("oldMachine_Biometric_SeriallNo")
    @Expose
    private String oldMachineBiometricSeriallNo;
    @SerializedName("uploadFilledFormContentSize")
    @Expose
    private Object uploadFilledFormContentSize;
    @SerializedName("tranId")
    @Expose
    private Integer tranId;
    @SerializedName("dealerName")
    @Expose
    private String dealerName;
    @SerializedName("remarks")
    @Expose
    private Object remarks;
    @SerializedName("blockName")
    @Expose
    private String blockName;
    @SerializedName("districtId")
    @Expose
    private Integer districtId;
    @SerializedName("msg")
    @Expose
    private Object msg;
    @SerializedName("updatedBy")
    @Expose
    private Integer updatedBy;
    @SerializedName("updatedOn")
    @Expose
    private Object updatedOn;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;

    @SerializedName("fpscode")
    @Expose
    private String fpscode;

    @SerializedName("upPhotoPath")
    @Expose
    private String upPhotoPath;

    @SerializedName("upFormPath")
    @Expose
    private String upFormPath;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDistrictName() {
        if (districtName == null)
            return "";
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getNewMachineOrderNo() {
        if (newMachineOrderNo == null)
            return 0;
        return newMachineOrderNo;
    }

    public void setNewMachineOrderNo(Integer newMachineOrderNo) {
        this.newMachineOrderNo = newMachineOrderNo;
    }

    public Object getNewMachineCartonNo() {
        return newMachineCartonNo;
    }

    public void setNewMachineCartonNo(Object newMachineCartonNo) {
        this.newMachineCartonNo = newMachineCartonNo;
    }

    public Object getAccessoriesProvided() {
        return accessoriesProvided;
    }

    public void setAccessoriesProvided(Object accessoriesProvided) {
        this.accessoriesProvided = accessoriesProvided;
    }

    public String getOldMachineWorkingStatus() {
        if (oldMachineWorkingStatus == null)
            return "";
        return oldMachineWorkingStatus;
    }

    public void setOldMachineWorkingStatus(String oldMachineWorkingStatus) {
        this.oldMachineWorkingStatus = oldMachineWorkingStatus;
    }

    public Integer getEquipmentModelId() {
        return equipmentModelId;
    }

    public void setEquipmentModelId(Integer equipmentModelId) {
        this.equipmentModelId = equipmentModelId;
    }

    public Boolean getIsCompleteWithSatisfactorily() {
        return isCompleteWithSatisfactorily;
    }

    public void setIsCompleteWithSatisfactorily(Boolean isCompleteWithSatisfactorily) {
        this.isCompleteWithSatisfactorily = isCompleteWithSatisfactorily;
    }

    public Integer getOldMachineVenderid() {
        return oldMachineVenderid;
    }

    public void setOldMachineVenderid(Integer oldMachineVenderid) {
        this.oldMachineVenderid = oldMachineVenderid;
    }

    public Integer getNewMachineVenderid() {
        return newMachineVenderid;
    }

    public void setNewMachineVenderid(Integer newMachineVenderid) {
        this.newMachineVenderid = newMachineVenderid;
    }

    public String getOldMachineSerialNo() {
        return oldMachineSerialNo;
    }

    public void setOldMachineSerialNo(String oldMachineSerialNo) {
        this.oldMachineSerialNo = oldMachineSerialNo;
    }

    public String getNewMachineSerialNo() {
        if (newMachineSerialNo == null)
            return "";
        return newMachineSerialNo;
    }

    public void setNewMachineSerialNo(String newMachineSerialNo) {
        this.newMachineSerialNo = newMachineSerialNo;
    }

    public String getReceivedRemark() {
        return receivedRemark;
    }

    public void setReceivedRemark(String receivedRemark) {
        this.receivedRemark = receivedRemark;
    }

    public String getNewMachineIMEI1() {
        if (newMachineIMEI1 == null)
            return "";
        return newMachineIMEI1;
    }

    public void setNewMachineIMEI1(String newMachineIMEI1) {
        this.newMachineIMEI1 = newMachineIMEI1;
    }

    public String getNewMachineIMEI2() {
        if (newMachineIMEI2 == null)
            return "";
        return newMachineIMEI2;
    }

    public void setNewMachineIMEI2(String newMachineIMEI2) {
        this.newMachineIMEI2 = newMachineIMEI2;
    }

    public Object getUploadFilledFormContentType() {
        return uploadFilledFormContentType;
    }

    public void setUploadFilledFormContentType(Object uploadFilledFormContentType) {
        this.uploadFilledFormContentType = uploadFilledFormContentType;
    }

    public Boolean getIsUploadFilledForm() {
        return isUploadFilledForm;
    }

    public void setIsUploadFilledForm(Boolean isUploadFilledForm) {
        this.isUploadFilledForm = isUploadFilledForm;
    }

    public String getNewMachineDeviceCode() {
        return newMachineDeviceCode;
    }

    public void setNewMachineDeviceCode(String newMachineDeviceCode) {
        this.newMachineDeviceCode = newMachineDeviceCode;
    }

    public String getOldMachineDeviceCode() {
        return oldMachineDeviceCode;
    }

    public void setOldMachineDeviceCode(String oldMachineDeviceCode) {
        this.oldMachineDeviceCode = oldMachineDeviceCode;
    }

    public Object getUploadFilledFormPath() {
        return uploadFilledFormPath;
    }

    public void setUploadFilledFormPath(Object uploadFilledFormPath) {
        this.uploadFilledFormPath = uploadFilledFormPath;
    }

    public Integer getNoOfTimesPrint() {
        return noOfTimesPrint;
    }

    public void setNoOfTimesPrint(Integer noOfTimesPrint) {
        this.noOfTimesPrint = noOfTimesPrint;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getNewMachineBiometricSeriallNo() {
        if (newMachineBiometricSeriallNo == null)
            return "";
        return newMachineBiometricSeriallNo;
    }

    public void setNewMachineBiometricSeriallNo(String newMachineBiometricSeriallNo) {
        this.newMachineBiometricSeriallNo = newMachineBiometricSeriallNo;
    }

    public String getTranDateStr() {
        return tranDateStr;
    }

    public void setTranDateStr(String tranDateStr) {
        this.tranDateStr = tranDateStr;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getOldVenderName() {
        return oldVenderName;
    }

    public void setOldVenderName(String oldVenderName) {
        this.oldVenderName = oldVenderName;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getNewVenderName() {
        return newVenderName;
    }

    public void setNewVenderName(String newVenderName) {
        this.newVenderName = newVenderName;
    }

    public TranDate getTranDate() {
        return tranDate;
    }

    public void setTranDate(TranDate tranDate) {
        this.tranDate = tranDate;
    }

    public Boolean getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(Boolean printStatus) {
        this.printStatus = printStatus;
    }

    public String getPrintedOn() {
        return printedOn;
    }

    public void setPrintedOn(String printedOn) {
        this.printedOn = printedOn;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public Object getImageContent() {
        return imageContent;
    }

    public void setImageContent(Object imageContent) {
        this.imageContent = imageContent;
    }

    public Boolean getWhetherOldMachProvdedForReplacmnt() {
        return whetherOldMachProvdedForReplacmnt;
    }

    public void setWhetherOldMachProvdedForReplacmnt(Boolean whetherOldMachProvdedForReplacmnt) {
        this.whetherOldMachProvdedForReplacmnt = whetherOldMachProvdedForReplacmnt;
    }

    public String getOldMachineBiometricSeriallNo() {
        return oldMachineBiometricSeriallNo;
    }

    public void setOldMachineBiometricSeriallNo(String oldMachineBiometricSeriallNo) {
        this.oldMachineBiometricSeriallNo = oldMachineBiometricSeriallNo;
    }

    public Object getUploadFilledFormContentSize() {
        return uploadFilledFormContentSize;
    }

    public void setUploadFilledFormContentSize(Object uploadFilledFormContentSize) {
        this.uploadFilledFormContentSize = uploadFilledFormContentSize;
    }

    public Integer getTranId() {
        if (tranId == null)
            return 0;
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    public String getDealerName() {
        if (dealerName == null)
            return "";
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public Integer getDistrictId() {
        if (districtId == null)
            return 0;
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFpscode() {
        if (fpscode == null)
            return "";
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getIsFormUploaded() {
        if (isFormUploaded == null)
            return "0";
        return isFormUploaded;
    }

    public void setIsFormUploaded(String isFormUploaded) {
        this.isFormUploaded = isFormUploaded;
    }

    public String getIsPhotoUploaded() {
        if (isPhotoUploaded == null)
            return "0";
        return isPhotoUploaded;
    }

    public void setIsPhotoUploaded(String isPhotoUploaded) {
        this.isPhotoUploaded = isPhotoUploaded;
    }

    public String getUpPhotoPath() {
        if (upPhotoPath == null)
            return "";
        return upPhotoPath;
    }

    public void setUpPhotoPath(String upPhotoPath) {
        this.upPhotoPath = upPhotoPath;
    }

    public String getUpFormPath() {
        if (upFormPath == null)
            return "";
        return upFormPath;
    }

    public void setUpFormPath(String upFormPath) {
        this.upFormPath = upFormPath;
    }
}
