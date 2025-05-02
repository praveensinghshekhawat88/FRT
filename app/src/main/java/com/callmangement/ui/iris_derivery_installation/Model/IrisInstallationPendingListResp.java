package com.callmangement.ui.iris_derivery_installation.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class IrisInstallationPendingListResp implements Serializable {

    @SerializedName("Data")
    private ArrayList<Datum> data;
    private String message;
    private String status;

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> value) {
        this.data = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public class Datum implements Serializable {

        private boolean status;
        private String message, deliveredBy, serialNo, dealerMobileNo, deviceSerialNo, deliveredOnStr, installedOnStr, remark,
                districtId, fpscode, blockName, deviceModel, dealerName, districtName, ticketNo, fpsdeviceCode, shopAddress,
                latitude, longitude, deviceType, lastStatus, installedBy, installedOn, deliveredOn;

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

        public String getDeliveredBy() {
            return deliveredBy;
        }

        public void setDeliveredBy(String deliveredBy) {
            this.deliveredBy = deliveredBy;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getDealerMobileNo() {
            return dealerMobileNo;
        }

        public void setDealerMobileNo(String dealerMobileNo) {
            this.dealerMobileNo = dealerMobileNo;
        }

        public String getDeviceSerialNo() {
            return deviceSerialNo;
        }

        public void setDeviceSerialNo(String deviceSerialNo) {
            this.deviceSerialNo = deviceSerialNo;
        }

        public String getDeliveredOnStr() {
            return deliveredOnStr;
        }

        public void setDeliveredOnStr(String deliveredOnStr) {
            this.deliveredOnStr = deliveredOnStr;
        }

        public String getInstalledOnStr() {
            return installedOnStr;
        }

        public void setInstalledOnStr(String installedOnStr) {
            this.installedOnStr = installedOnStr;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getDistrictId() {
            return districtId;
        }

        public void setDistrictId(String districtId) {
            this.districtId = districtId;
        }

        public String getFpscode() {
            return fpscode;
        }

        public void setFpscode(String fpscode) {
            this.fpscode = fpscode;
        }

        public String getBlockName() {
            return blockName;
        }

        public void setBlockName(String blockName) {
            this.blockName = blockName;
        }

        public String getDeviceModel() {
            return deviceModel;
        }

        public void setDeviceModel(String deviceModel) {
            this.deviceModel = deviceModel;
        }

        public String getDealerName() {
            return dealerName;
        }

        public void setDealerName(String dealerName) {
            this.dealerName = dealerName;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public String getTicketNo() {
            return ticketNo;
        }

        public void setTicketNo(String ticketNo) {
            this.ticketNo = ticketNo;
        }

        public String getFpsdeviceCode() {
            return fpsdeviceCode;
        }

        public void setFpsdeviceCode(String fpsdeviceCode) {
            this.fpsdeviceCode = fpsdeviceCode;
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

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getLastStatus() {
            return lastStatus;
        }

        public void setLastStatus(String lastStatus) {
            this.lastStatus = lastStatus;
        }

        public String getInstalledBy() {
            return installedBy;
        }

        public void setInstalledBy(String installedBy) {
            this.installedBy = installedBy;
        }

        public String getInstalledOn() {
            return installedOn;
        }

        public void setInstalledOn(String installedOn) {
            this.installedOn = installedOn;
        }

        public String getDeliveredOn() {
            return deliveredOn;
        }

        public void setDeliveredOn(String deliveredOn) {
            this.deliveredOn = deliveredOn;
        }
    }

}


