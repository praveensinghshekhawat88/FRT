// IrisDeliveryListResponse.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.callmangement.ui.closed_guard_delivery.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ClosedGuardDeliveryListResponse implements Serializable {

    @SerializedName("Data")
    private ArrayList<Datum> data;
    private String message;
    private String status;

    private String totalItems,totalPages,currentPage;

    public ArrayList<Datum> getData() { return data; }
    public void setData(ArrayList<Datum> value) { this.data = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

    public String getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public class Datum implements Serializable{

        private String message,deviceModelId,deviceTypeId,fpscode,status,districtId,districtName,blockName,dealerName,dealerMobileNo,posdeviceCode,deliveryId,isActive,
                deviceType,closeGuardDeliveryAddress,closeGuardDeliverdByName,closeGuardLatitude,closeGuardLongitude,closeGuardDeliverdBy,closeGuardDeliverdOnStr,
                isCloseGuardDeliverd,closeGuardDeliverdOn,serialNo,deviceModelName,cgphotoPath,cgsignaturePath;


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDeviceModelId() {
            return deviceModelId;
        }

        public void setDeviceModelId(String deviceModelId) {
            this.deviceModelId = deviceModelId;
        }

        public String getDeviceTypeId() {
            return deviceTypeId;
        }

        public void setDeviceTypeId(String deviceTypeId) {
            this.deviceTypeId = deviceTypeId;
        }

        public String getFpscode() {
            return fpscode;
        }

        public void setFpscode(String fpscode) {
            this.fpscode = fpscode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDistrictId() {
            return districtId;
        }

        public void setDistrictId(String districtId) {
            this.districtId = districtId;
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

        public String getDealerMobileNo() {
            return dealerMobileNo;
        }

        public void setDealerMobileNo(String dealerMobileNo) {
            this.dealerMobileNo = dealerMobileNo;
        }

        public String getPosdeviceCode() {
            return posdeviceCode;
        }

        public void setPosdeviceCode(String posdeviceCode) {
            this.posdeviceCode = posdeviceCode;
        }

        public String getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(String deliveryId) {
            this.deliveryId = deliveryId;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getCloseGuardDeliveryAddress() {
            return closeGuardDeliveryAddress;
        }

        public void setCloseGuardDeliveryAddress(String closeGuardDeliveryAddress) {
            this.closeGuardDeliveryAddress = closeGuardDeliveryAddress;
        }

        public String getCloseGuardDeliverdByName() {
            return closeGuardDeliverdByName;
        }

        public void setCloseGuardDeliverdByName(String closeGuardDeliverdByName) {
            this.closeGuardDeliverdByName = closeGuardDeliverdByName;
        }

        public String getCloseGuardLatitude() {
            return closeGuardLatitude;
        }

        public void setCloseGuardLatitude(String closeGuardLatitude) {
            this.closeGuardLatitude = closeGuardLatitude;
        }

        public String getCloseGuardLongitude() {
            return closeGuardLongitude;
        }

        public void setCloseGuardLongitude(String closeGuardLongitude) {
            this.closeGuardLongitude = closeGuardLongitude;
        }

        public String getCloseGuardDeliverdBy() {
            return closeGuardDeliverdBy;
        }

        public void setCloseGuardDeliverdBy(String closeGuardDeliverdBy) {
            this.closeGuardDeliverdBy = closeGuardDeliverdBy;
        }

        public String getCloseGuardDeliverdOnStr() {
            return closeGuardDeliverdOnStr;
        }

        public void setCloseGuardDeliverdOnStr(String closeGuardDeliverdOnStr) {
            this.closeGuardDeliverdOnStr = closeGuardDeliverdOnStr;
        }

        public String getIsCloseGuardDeliverd() {
            return isCloseGuardDeliverd;
        }

        public void setIsCloseGuardDeliverd(String isCloseGuardDeliverd) {
            this.isCloseGuardDeliverd = isCloseGuardDeliverd;
        }

        public String getCloseGuardDeliverdOn() {
            return closeGuardDeliverdOn;
        }

        public void setCloseGuardDeliverdOn(String closeGuardDeliverdOn) {
            this.closeGuardDeliverdOn = closeGuardDeliverdOn;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getDeviceModelName() {
            return deviceModelName;
        }

        public void setDeviceModelName(String deviceModelName) {
            this.deviceModelName = deviceModelName;
        }

        public String getCgphotoPath() {
            return cgphotoPath;
        }

        public void setCgphotoPath(String cgphotoPath) {
            this.cgphotoPath = cgphotoPath;
        }

        public String getCgsignaturePath() {
            return cgsignaturePath;
        }

        public void setCgsignaturePath(String cgsignaturePath) {
            this.cgsignaturePath = cgsignaturePath;
        }
    }

}

// Datum.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

