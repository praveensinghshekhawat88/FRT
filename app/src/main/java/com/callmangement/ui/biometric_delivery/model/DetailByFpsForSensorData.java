package com.callmangement.ui.biometric_delivery.model;

public class DetailByFpsForSensorData {

    public boolean status;
    public String message;
    public String biometricSerialNo,deviceCode,dealerMobileNo,districtName,blockName,dealerName,fpsdeviceCode,fpscode,districtId;


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

    public String getBiometricSerialNo() {
        return biometricSerialNo;
    }

    public void setBiometricSerialNo(String biometricSerialNo) {
        this.biometricSerialNo = biometricSerialNo;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
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

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
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

    public String getFpscode() {
        return fpscode;
    }

    public void setFpscode(String fpscode) {
        this.fpscode = fpscode;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }
}
