package com.callmangement.EHR.models;

public class FeedbackDetailByFpsData {
    public boolean status;
    public String message;

    public Object otp;

    public Object updatedBy;
    public Object tranDateStr;
    public Object latitude;
    public Object images;
    public Object longitude;

    public Object deliveryId;
    public Object updatedOn;
    public Object createdOn;
    public int districtId;
    public Object blockId;
    public String fpscode;
    public Object ticketNo;
    public String dealerMobileNo;
    public String districtName;
    public String dealerName;
    public String fpsdeviceCode;
    public String blockName;
    public boolean isActive;

    public Object remarks;

    public Object tranDate;


    public FeedbackDetailByFpsData(boolean status, String message, Object otp, Object updatedBy, Object tranDateStr, Object latitude, Object images, Object longitude, Object deliveryId, Object updatedOn, Object createdOn, int districtId, Object blockId, String fpscode, Object ticketNo, String dealerMobileNo, String districtName, String dealerName, String fpsdeviceCode, String blockName, boolean isActive, Object remarks, Object tranDate) {
        this.status = status;
        this.message = message;
        this.otp = otp;
        this.updatedBy = updatedBy;
        this.tranDateStr = tranDateStr;
        this.latitude = latitude;
        this.images = images;
        this.longitude = longitude;
        this.deliveryId = deliveryId;
        this.updatedOn = updatedOn;
        this.createdOn = createdOn;
        this.districtId = districtId;
        this.blockId = blockId;
        this.fpscode = fpscode;
        this.ticketNo = ticketNo;
        this.dealerMobileNo = dealerMobileNo;
        this.districtName = districtName;
        this.dealerName = dealerName;
        this.fpsdeviceCode = fpsdeviceCode;
        this.blockName = blockName;
        this.isActive = isActive;
        this.remarks = remarks;
        this.tranDate = tranDate;
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

    public Object getOtp() {
        return otp;
    }

    public void setOtp(Object otp) {
        this.otp = otp;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getTranDateStr() {
        return tranDateStr;
    }

    public void setTranDateStr(Object tranDateStr) {
        this.tranDateStr = tranDateStr;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Object getImages() {
        return images;
    }

    public void setImages(Object images) {
        this.images = images;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public Object getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Object deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Object getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Object createdOn) {
        this.createdOn = createdOn;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public Object getBlockId() {
        return blockId;
    }

    public void setBlockId(Object blockId) {
        this.blockId = blockId;
    }

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public Object getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(Object ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getDealerMobileNo() {
        return dealerMobileNo;
    }

    public void setDealerMobileNo(String dealerMobileNo) {
        this.dealerMobileNo = dealerMobileNo;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getFpsdeviceCode() {
        return fpsdeviceCode;
    }

    public void setFpsdeviceCode(String fpsdeviceCode) {
        this.fpsdeviceCode = fpsdeviceCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
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

    public Object getTranDate() {
        return tranDate;
    }

    public void setTranDate(Object tranDate) {
        this.tranDate = tranDate;
    }
}
