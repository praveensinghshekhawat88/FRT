package com.callmangement.model.WeighingDeliveryDetail;

import java.util.ArrayList;

public class weighingDeliveryData {

    public boolean status;
    public String message;
    public int districtId;
    public String fpscode;
    public String ticketNo;
    public String districtName;
    public Object userID;
    public Object blockId;
    public String fpsdeviceCode;
    public String dealerName;
    public String blockName;
    public int updatedBy;
    public String last_Remark;
    public boolean isActive;
    public Object remarks;
    public Object tranDateStr;
    public String shopAddress;
    public String latitude;
    public String longitude;
    public Object images;
    public Object otp;
    public int deliveryId;
    public Object createdOn;
    public Object updatedOn;
    public Object installationOnStr;
    public Object installationOn;
    public String last_TicketStatus;
    public int installationBy;
    public Object irisdeliveredOn;
    public String deliveryRemark;
    public Object installationRemark;
    public int weighingScaleDeliveredBy;
    public String weighingScaleModelName;
    public String dealerMobileNo;
    public Object irisdeliveredOnStr;
    public String weighingScaleSerialNo;
    public String winghingScaleDeliveredOnStr;
    public Object tranDate;
    public ArrayList<weighingDeliveryImagesDetail> imagesDetail;
    public boolean isDeliverdIRIS;
    public boolean isDeliverdWeighingScale;
    public Object irisScannerSerialNo;
    public Object irisScannerModelName;
    public Object winghingScaleDeliveredOn;
    public int last_TicketStatusId;

    public weighingDeliveryData(boolean status, String message, int districtId, String fpscode, String ticketNo, String districtName, Object userID, Object blockId, String fpsdeviceCode, String dealerName, String blockName, int updatedBy, String last_Remark, boolean isActive, Object remarks, Object tranDateStr, String shopAddress, String latitude, String longitude, Object images, Object otp, int deliveryId, Object createdOn, Object updatedOn, Object installationOnStr, Object installationOn, String last_TicketStatus, int installationBy, Object irisdeliveredOn, String deliveryRemark, Object installationRemark, int weighingScaleDeliveredBy, String weighingScaleModelName, String dealerMobileNo, Object irisdeliveredOnStr, String weighingScaleSerialNo, String winghingScaleDeliveredOnStr, Object tranDate, ArrayList<weighingDeliveryImagesDetail> imagesDetail, boolean isDeliverdIRIS, boolean isDeliverdWeighingScale, Object irisScannerSerialNo, Object irisScannerModelName, Object winghingScaleDeliveredOn, int last_TicketStatusId) {
        this.status = status;
        this.message = message;
        this.districtId = districtId;
        this.fpscode = fpscode;
        this.ticketNo = ticketNo;
        this.districtName = districtName;
        this.userID = userID;
        this.blockId = blockId;
        this.fpsdeviceCode = fpsdeviceCode;
        this.dealerName = dealerName;
        this.blockName = blockName;
        this.updatedBy = updatedBy;
        this.last_Remark = last_Remark;
        this.isActive = isActive;
        this.remarks = remarks;
        this.tranDateStr = tranDateStr;
        this.shopAddress = shopAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = images;
        this.otp = otp;
        this.deliveryId = deliveryId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.installationOnStr = installationOnStr;
        this.installationOn = installationOn;
        this.last_TicketStatus = last_TicketStatus;
        this.installationBy = installationBy;
        this.irisdeliveredOn = irisdeliveredOn;
        this.deliveryRemark = deliveryRemark;
        this.installationRemark = installationRemark;
        this.weighingScaleDeliveredBy = weighingScaleDeliveredBy;
        this.weighingScaleModelName = weighingScaleModelName;
        this.dealerMobileNo = dealerMobileNo;
        this.irisdeliveredOnStr = irisdeliveredOnStr;
        this.weighingScaleSerialNo = weighingScaleSerialNo;
        this.winghingScaleDeliveredOnStr = winghingScaleDeliveredOnStr;
        this.tranDate = tranDate;
        this.imagesDetail = imagesDetail;
        this.isDeliverdIRIS = isDeliverdIRIS;
        this.isDeliverdWeighingScale = isDeliverdWeighingScale;
        this.irisScannerSerialNo = irisScannerSerialNo;
        this.irisScannerModelName = irisScannerModelName;
        this.winghingScaleDeliveredOn = winghingScaleDeliveredOn;
        this.last_TicketStatusId = last_TicketStatusId;
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

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Object getUserID() {
        return userID;
    }

    public void setUserID(Object userID) {
        this.userID = userID;
    }

    public Object getBlockId() {
        return blockId;
    }

    public void setBlockId(Object blockId) {
        this.blockId = blockId;
    }

    public String getFpsdeviceCode() {
        return fpsdeviceCode;
    }

    public void setFpsdeviceCode(String fpsdeviceCode) {
        this.fpsdeviceCode = fpsdeviceCode;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLast_Remark() {
        return last_Remark;
    }

    public void setLast_Remark(String last_Remark) {
        this.last_Remark = last_Remark;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public Object getTranDateStr() {
        return tranDateStr;
    }

    public void setTranDateStr(Object tranDateStr) {
        this.tranDateStr = tranDateStr;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
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

    public Object getImages() {
        return images;
    }

    public void setImages(Object images) {
        this.images = images;
    }

    public Object getOtp() {
        return otp;
    }

    public void setOtp(Object otp) {
        this.otp = otp;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
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

    public Object getInstallationOnStr() {
        return installationOnStr;
    }

    public void setInstallationOnStr(Object installationOnStr) {
        this.installationOnStr = installationOnStr;
    }

    public Object getInstallationOn() {
        return installationOn;
    }

    public void setInstallationOn(Object installationOn) {
        this.installationOn = installationOn;
    }

    public String getLast_TicketStatus() {
        return last_TicketStatus;
    }

    public void setLast_TicketStatus(String last_TicketStatus) {
        this.last_TicketStatus = last_TicketStatus;
    }

    public int getInstallationBy() {
        return installationBy;
    }

    public void setInstallationBy(int installationBy) {
        this.installationBy = installationBy;
    }

    public Object getIrisdeliveredOn() {
        return irisdeliveredOn;
    }

    public void setIrisdeliveredOn(Object irisdeliveredOn) {
        this.irisdeliveredOn = irisdeliveredOn;
    }

    public String getDeliveryRemark() {
        return deliveryRemark;
    }

    public void setDeliveryRemark(String deliveryRemark) {
        this.deliveryRemark = deliveryRemark;
    }

    public Object getInstallationRemark() {
        return installationRemark;
    }

    public void setInstallationRemark(Object installationRemark) {
        this.installationRemark = installationRemark;
    }

    public int getWeighingScaleDeliveredBy() {
        return weighingScaleDeliveredBy;
    }

    public void setWeighingScaleDeliveredBy(int weighingScaleDeliveredBy) {
        this.weighingScaleDeliveredBy = weighingScaleDeliveredBy;
    }

    public String getWeighingScaleModelName() {
        return weighingScaleModelName;
    }

    public void setWeighingScaleModelName(String weighingScaleModelName) {
        this.weighingScaleModelName = weighingScaleModelName;
    }

    public String getDealerMobileNo() {
        return dealerMobileNo;
    }

    public void setDealerMobileNo(String dealerMobileNo) {
        this.dealerMobileNo = dealerMobileNo;
    }

    public Object getIrisdeliveredOnStr() {
        return irisdeliveredOnStr;
    }

    public void setIrisdeliveredOnStr(Object irisdeliveredOnStr) {
        this.irisdeliveredOnStr = irisdeliveredOnStr;
    }

    public String getWeighingScaleSerialNo() {
        return weighingScaleSerialNo;
    }

    public void setWeighingScaleSerialNo(String weighingScaleSerialNo) {
        this.weighingScaleSerialNo = weighingScaleSerialNo;
    }

    public String getWinghingScaleDeliveredOnStr() {
        return winghingScaleDeliveredOnStr;
    }

    public void setWinghingScaleDeliveredOnStr(String winghingScaleDeliveredOnStr) {
        this.winghingScaleDeliveredOnStr = winghingScaleDeliveredOnStr;
    }

    public Object getTranDate() {
        return tranDate;
    }

    public void setTranDate(Object tranDate) {
        this.tranDate = tranDate;
    }

    public ArrayList<weighingDeliveryImagesDetail> getImagesDetail() {
        return imagesDetail;
    }

    public void setImagesDetail(ArrayList<weighingDeliveryImagesDetail> imagesDetail) {
        this.imagesDetail = imagesDetail;
    }

    public boolean isDeliverdIRIS() {
        return isDeliverdIRIS;
    }

    public void setDeliverdIRIS(boolean deliverdIRIS) {
        isDeliverdIRIS = deliverdIRIS;
    }

    public boolean isDeliverdWeighingScale() {
        return isDeliverdWeighingScale;
    }

    public void setDeliverdWeighingScale(boolean deliverdWeighingScale) {
        isDeliverdWeighingScale = deliverdWeighingScale;
    }

    public Object getIrisScannerSerialNo() {
        return irisScannerSerialNo;
    }

    public void setIrisScannerSerialNo(Object irisScannerSerialNo) {
        this.irisScannerSerialNo = irisScannerSerialNo;
    }

    public Object getIrisScannerModelName() {
        return irisScannerModelName;
    }

    public void setIrisScannerModelName(Object irisScannerModelName) {
        this.irisScannerModelName = irisScannerModelName;
    }

    public Object getWinghingScaleDeliveredOn() {
        return winghingScaleDeliveredOn;
    }

    public void setWinghingScaleDeliveredOn(Object winghingScaleDeliveredOn) {
        this.winghingScaleDeliveredOn = winghingScaleDeliveredOn;
    }

    public int getLast_TicketStatusId() {
        return last_TicketStatusId;
    }

    public void setLast_TicketStatusId(int last_TicketStatusId) {
        this.last_TicketStatusId = last_TicketStatusId;
    }
}
