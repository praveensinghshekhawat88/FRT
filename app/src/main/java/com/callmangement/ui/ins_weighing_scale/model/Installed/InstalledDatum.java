package com.callmangement.ui.ins_weighing_scale.model.Installed;

import java.io.Serializable;

public class InstalledDatum implements Serializable {

    public boolean status;
    public String message;
    public boolean isDeliverdIRIS;
    public String otp;
    public String images;
    public String imagesDetail;
    public String dealerMobileNo;
    public String irisScannerSerialNo;
    public String irisScannerModelId;
    public String winghingScaleDeliveredOn;
    public boolean isDeliverdWeighingScale;
    public String weighingScaleModelName;
    public String irisScannerModelName;
    public String weighingScaleSerialNo;
    public String irisDeviceType;
    public String irisDeviceModel;
    public String deliveryRemark;
    public int installationBy;
    public String winghingScaleDeliveredOnStr;
    public int irisDeviceModelId;
    public int last_TicketStatusId;
    public String installationOnStr;
    public int weighingScaleDeliveredBy;
    public int irisDeviceTypeId;
    public String last_TicketStatus;
    public String installationRemark;
    public String irisdeliveredOnStr;
    public String deliveredByName;
    public boolean isActive;
    public String weighingScaleModelId;
    public String irisdeliveredOn;
    public String installationOn;
    public String last_Remark;
    public String dealerName;
    public String ticketNo;
    public String remarks;
    public String blockName;
    public int districtId;
    public int deviceTypeId;
    public String fpscode;
    public String userID;
    public String blockId;
    public String fpsdeviceCode;
    public String districtName;
    public int updatedBy;
    public int deviceModelId;
    public String deviceModel;
    public String tranDate;
    public String shopAddress;
    public String deviceType;
    public int deliveryId;
    public String tranDateStr;
    public String latitude;
    public String longitude;
    public String createdOn;
    public String updatedOn;


    public InstalledDatum(boolean status, String message, boolean isDeliverdIRIS, String otp, String images, String imagesDetail, String dealerMobileNo, String irisScannerSerialNo, String irisScannerModelId, String winghingScaleDeliveredOn, boolean isDeliverdWeighingScale, String weighingScaleModelName, String irisScannerModelName, String weighingScaleSerialNo, String irisDeviceType, String irisDeviceModel, String deliveryRemark, int installationBy, String winghingScaleDeliveredOnStr, int irisDeviceModelId, int last_TicketStatusId, String installationOnStr, int weighingScaleDeliveredBy, int irisDeviceTypeId, String last_TicketStatus, String installationRemark, String irisdeliveredOnStr, String deliveredByName, boolean isActive, String weighingScaleModelId, String irisdeliveredOn, String installationOn, String last_Remark, String dealerName, String ticketNo, String remarks, String blockName, int districtId, int deviceTypeId, String fpscode, String userID, String blockId, String fpsdeviceCode, String districtName, int updatedBy, int deviceModelId, String deviceModel, String tranDate, String shopAddress, String deviceType, int deliveryId, String tranDateStr, String latitude, String longitude, String createdOn, String updatedOn) {
        this.status = status;
        this.message = message;
        this.isDeliverdIRIS = isDeliverdIRIS;
        this.otp = otp;
        this.images = images;
        this.imagesDetail = imagesDetail;
        this.dealerMobileNo = dealerMobileNo;
        this.irisScannerSerialNo = irisScannerSerialNo;
        this.irisScannerModelId = irisScannerModelId;
        this.winghingScaleDeliveredOn = winghingScaleDeliveredOn;
        this.isDeliverdWeighingScale = isDeliverdWeighingScale;
        this.weighingScaleModelName = weighingScaleModelName;
        this.irisScannerModelName = irisScannerModelName;
        this.weighingScaleSerialNo = weighingScaleSerialNo;
        this.irisDeviceType = irisDeviceType;
        this.irisDeviceModel = irisDeviceModel;
        this.deliveryRemark = deliveryRemark;
        this.installationBy = installationBy;
        this.winghingScaleDeliveredOnStr = winghingScaleDeliveredOnStr;
        this.irisDeviceModelId = irisDeviceModelId;
        this.last_TicketStatusId = last_TicketStatusId;
        this.installationOnStr = installationOnStr;
        this.weighingScaleDeliveredBy = weighingScaleDeliveredBy;
        this.irisDeviceTypeId = irisDeviceTypeId;
        this.last_TicketStatus = last_TicketStatus;
        this.installationRemark = installationRemark;
        this.irisdeliveredOnStr = irisdeliveredOnStr;
        this.deliveredByName = deliveredByName;
        this.isActive = isActive;
        this.weighingScaleModelId = weighingScaleModelId;
        this.irisdeliveredOn = irisdeliveredOn;
        this.installationOn = installationOn;
        this.last_Remark = last_Remark;
        this.dealerName = dealerName;
        this.ticketNo = ticketNo;
        this.remarks = remarks;
        this.blockName = blockName;
        this.districtId = districtId;
        this.deviceTypeId = deviceTypeId;
        this.fpscode = fpscode;
        this.userID = userID;
        this.blockId = blockId;
        this.fpsdeviceCode = fpsdeviceCode;
        this.districtName = districtName;
        this.updatedBy = updatedBy;
        this.deviceModelId = deviceModelId;
        this.deviceModel = deviceModel;
        this.tranDate = tranDate;
        this.shopAddress = shopAddress;
        this.deviceType = deviceType;
        this.deliveryId = deliveryId;
        this.tranDateStr = tranDateStr;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDeliverdIRIS() {
        return isDeliverdIRIS;
    }

    public void setDeliverdIRIS(boolean deliverdIRIS) {
        isDeliverdIRIS = deliverdIRIS;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImagesDetail() {
        return imagesDetail;
    }

    public void setImagesDetail(String imagesDetail) {
        this.imagesDetail = imagesDetail;
    }

    public String getDealerMobileNo() {
        return dealerMobileNo;
    }

    public void setDealerMobileNo(String dealerMobileNo) {
        this.dealerMobileNo = dealerMobileNo;
    }

    public String getIrisScannerSerialNo() {
        return irisScannerSerialNo;
    }

    public void setIrisScannerSerialNo(String irisScannerSerialNo) {
        this.irisScannerSerialNo = irisScannerSerialNo;
    }

    public String getIrisScannerModelId() {
        return irisScannerModelId;
    }

    public void setIrisScannerModelId(String irisScannerModelId) {
        this.irisScannerModelId = irisScannerModelId;
    }

    public String getWinghingScaleDeliveredOn() {
        return winghingScaleDeliveredOn;
    }

    public void setWinghingScaleDeliveredOn(String winghingScaleDeliveredOn) {
        this.winghingScaleDeliveredOn = winghingScaleDeliveredOn;
    }

    public boolean isDeliverdWeighingScale() {
        return isDeliverdWeighingScale;
    }

    public void setDeliverdWeighingScale(boolean deliverdWeighingScale) {
        isDeliverdWeighingScale = deliverdWeighingScale;
    }

    public String getWeighingScaleModelName() {
        return weighingScaleModelName;
    }

    public void setWeighingScaleModelName(String weighingScaleModelName) {
        this.weighingScaleModelName = weighingScaleModelName;
    }

    public String getIrisScannerModelName() {
        return irisScannerModelName;
    }

    public void setIrisScannerModelName(String irisScannerModelName) {
        this.irisScannerModelName = irisScannerModelName;
    }

    public String getWeighingScaleSerialNo() {
        return weighingScaleSerialNo;
    }

    public void setWeighingScaleSerialNo(String weighingScaleSerialNo) {
        this.weighingScaleSerialNo = weighingScaleSerialNo;
    }

    public String getIrisDeviceType() {
        return irisDeviceType;
    }

    public void setIrisDeviceType(String irisDeviceType) {
        this.irisDeviceType = irisDeviceType;
    }

    public String getIrisDeviceModel() {
        return irisDeviceModel;
    }

    public void setIrisDeviceModel(String irisDeviceModel) {
        this.irisDeviceModel = irisDeviceModel;
    }

    public String getDeliveryRemark() {
        return deliveryRemark;
    }

    public void setDeliveryRemark(String deliveryRemark) {
        this.deliveryRemark = deliveryRemark;
    }

    public int getInstallationBy() {
        return installationBy;
    }

    public void setInstallationBy(int installationBy) {
        this.installationBy = installationBy;
    }

    public String getWinghingScaleDeliveredOnStr() {
        return winghingScaleDeliveredOnStr;
    }

    public void setWinghingScaleDeliveredOnStr(String winghingScaleDeliveredOnStr) {
        this.winghingScaleDeliveredOnStr = winghingScaleDeliveredOnStr;
    }

    public int getIrisDeviceModelId() {
        return irisDeviceModelId;
    }

    public void setIrisDeviceModelId(int irisDeviceModelId) {
        this.irisDeviceModelId = irisDeviceModelId;
    }

    public int getLast_TicketStatusId() {
        return last_TicketStatusId;
    }

    public void setLast_TicketStatusId(int last_TicketStatusId) {
        this.last_TicketStatusId = last_TicketStatusId;
    }

    public String getInstallationOnStr() {
        return installationOnStr;
    }

    public void setInstallationOnStr(String installationOnStr) {
        this.installationOnStr = installationOnStr;
    }

    public int getWeighingScaleDeliveredBy() {
        return weighingScaleDeliveredBy;
    }

    public void setWeighingScaleDeliveredBy(int weighingScaleDeliveredBy) {
        this.weighingScaleDeliveredBy = weighingScaleDeliveredBy;
    }

    public int getIrisDeviceTypeId() {
        return irisDeviceTypeId;
    }

    public void setIrisDeviceTypeId(int irisDeviceTypeId) {
        this.irisDeviceTypeId = irisDeviceTypeId;
    }

    public String getLast_TicketStatus() {
        return last_TicketStatus;
    }

    public void setLast_TicketStatus(String last_TicketStatus) {
        this.last_TicketStatus = last_TicketStatus;
    }

    public String getInstallationRemark() {
        return installationRemark;
    }

    public void setInstallationRemark(String installationRemark) {
        this.installationRemark = installationRemark;
    }

    public String getIrisdeliveredOnStr() {
        return irisdeliveredOnStr;
    }

    public void setIrisdeliveredOnStr(String irisdeliveredOnStr) {
        this.irisdeliveredOnStr = irisdeliveredOnStr;
    }

    public String getDeliveredByName() {
        return deliveredByName;
    }

    public void setDeliveredByName(String deliveredByName) {
        this.deliveredByName = deliveredByName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getWeighingScaleModelId() {
        return weighingScaleModelId;
    }

    public void setWeighingScaleModelId(String weighingScaleModelId) {
        this.weighingScaleModelId = weighingScaleModelId;
    }

    public String getIrisdeliveredOn() {
        return irisdeliveredOn;
    }

    public void setIrisdeliveredOn(String irisdeliveredOn) {
        this.irisdeliveredOn = irisdeliveredOn;
    }

    public String getInstallationOn() {
        return installationOn;
    }

    public void setInstallationOn(String installationOn) {
        this.installationOn = installationOn;
    }

    public String getLast_Remark() {
        return last_Remark;
    }

    public void setLast_Remark(String last_Remark) {
        this.last_Remark = last_Remark;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getFpsdeviceCode() {
        return fpsdeviceCode;
    }

    public void setFpsdeviceCode(String fpsdeviceCode) {
        this.fpsdeviceCode = fpsdeviceCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public int getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(int deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getTranDateStr() {
        return tranDateStr;
    }

    public void setTranDateStr(String tranDateStr) {
        this.tranDateStr = tranDateStr;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }
}
