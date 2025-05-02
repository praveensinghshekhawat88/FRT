// IrisDeliveryListResponse.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package com.callmangement.ui.iris_derivery_installation.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class IrisDeliveryListResponse implements Serializable {

    @SerializedName("Data")
    private ArrayList<Datum> data;
    private String message;
    private String status;

    private String totalItems,totalPages,currentPage;

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

    public ArrayList<Datum> getData() { return data; }
    public void setData(ArrayList<Datum> value) { this.data = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

    public class Datum implements Serializable{
        private long deviceTypeId;
        private String blockName;
        private String latitude;
        private String fpscode;
        private boolean isActive;
        private String shopAddress;
        private String deliveryVerifyStatus;
        private String deliverdByName;
        private long deliveryId;
        private String ticketNo;
        private long deviceModelId;
        private String irisdeliveredOnStr;
        private String longitude;
        private String deviceType;
        private boolean isDeliveryVerify;
        private String deliveryRemark;
        private String dealerName;
        private long updatedBy;
        private String districtName;
        private String deliverdStatus;
        private String deviceModelName;
        private String dealerMobileNo;
        private boolean isDeliverdIRIS;
        private String message;
        private String serialNo;
        private long districtId;
        private long deliveryVerifyBy;
        private String fpsdeviceCode;
        private boolean status;
        private String deliveredBy,createdBy,tranDateStr,images,createdOn,updatedOn,imagesDetail,tranDate,deliveryVerifyOnStr,deliveryVerifyByName;

        public long getDeviceTypeId() { return deviceTypeId; }
        public void setDeviceTypeId(long value) { this.deviceTypeId = value; }

        public String getBlockName() { return blockName; }
        public void setBlockName(String value) { this.blockName = value; }

        public String getLatitude() { return latitude; }
        public void setLatitude(String value) { this.latitude = value; }

        public String getFpscode() { return fpscode; }
        public void setFpscode(String value) { this.fpscode = value; }

        public boolean getIsActive() { return isActive; }
        public void setIsActive(boolean value) { this.isActive = value; }

        public String getShopAddress() { return shopAddress; }
        public void setShopAddress(String value) { this.shopAddress = value; }

        public String getDeliveryVerifyStatus() { return deliveryVerifyStatus; }
        public void setDeliveryVerifyStatus(String value) { this.deliveryVerifyStatus = value; }

        public String getDeliverdByName() { return deliverdByName; }
        public void setDeliverdByName(String value) { this.deliverdByName = value; }

        public long getDeliveryId() { return deliveryId; }
        public void setDeliveryId(long value) { this.deliveryId = value; }

        public String getTicketNo() { return ticketNo; }
        public void setTicketNo(String value) { this.ticketNo = value; }

        public long getDeviceModelId() { return deviceModelId; }
        public void setDeviceModelId(long value) { this.deviceModelId = value; }

        public String getIrisdeliveredOnStr() { return irisdeliveredOnStr; }
        public void setIrisdeliveredOnStr(String value) { this.irisdeliveredOnStr = value; }

        public String getLongitude() { return longitude; }
        public void setLongitude(String value) { this.longitude = value; }

        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String value) { this.deviceType = value; }

        public boolean getIsDeliveryVerify() { return isDeliveryVerify; }
        public void setIsDeliveryVerify(boolean value) { this.isDeliveryVerify = value; }

        public String getDeliveryRemark() { return deliveryRemark; }
        public void setDeliveryRemark(String value) { this.deliveryRemark = value; }

        public String getDealerName() { return dealerName; }
        public void setDealerName(String value) { this.dealerName = value; }

        public long getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(long value) { this.updatedBy = value; }

        public String getDistrictName() { return districtName; }
        public void setDistrictName(String value) { this.districtName = value; }

        public String getDeliverdStatus() { return deliverdStatus; }
        public void setDeliverdStatus(String value) { this.deliverdStatus = value; }

        public String getDeviceModelName() { return deviceModelName; }
        public void setDeviceModelName(String value) { this.deviceModelName = value; }

        public String getDealerMobileNo() { return dealerMobileNo; }
        public void setDealerMobileNo(String value) { this.dealerMobileNo = value; }

        public boolean getIsDeliverdIRIS() { return isDeliverdIRIS; }
        public void setIsDeliverdIRIS(boolean value) { this.isDeliverdIRIS = value; }

        public String getMessage() { return message; }
        public void setMessage(String value) { this.message = value; }

        public String getSerialNo() { return serialNo; }
        public void setSerialNo(String value) { this.serialNo = value; }

        public long getDistrictId() { return districtId; }
        public void setDistrictId(long value) { this.districtId = value; }

        public long getDeliveryVerifyBy() { return deliveryVerifyBy; }
        public void setDeliveryVerifyBy(long value) { this.deliveryVerifyBy = value; }

        public String getFpsdeviceCode() { return fpsdeviceCode; }
        public void setFpsdeviceCode(String value) { this.fpsdeviceCode = value; }

        public boolean getStatus() { return status; }
        public void setStatus(boolean value) { this.status = value; }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public boolean isDeliveryVerify() {
            return isDeliveryVerify;
        }

        public void setDeliveryVerify(boolean deliveryVerify) {
            isDeliveryVerify = deliveryVerify;
        }

        public boolean isDeliverdIRIS() {
            return isDeliverdIRIS;
        }

        public void setDeliverdIRIS(boolean deliverdIRIS) {
            isDeliverdIRIS = deliverdIRIS;
        }

        public boolean isStatus() {
            return status;
        }

        public String getDeliveredBy() {
            return deliveredBy;
        }

        public void setDeliveredBy(String deliveredBy) {
            this.deliveredBy = deliveredBy;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getTranDateStr() {
            return tranDateStr;
        }

        public void setTranDateStr(String tranDateStr) {
            this.tranDateStr = tranDateStr;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
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

        public String getImagesDetail() {
            return imagesDetail;
        }

        public void setImagesDetail(String imagesDetail) {
            this.imagesDetail = imagesDetail;
        }

        public String getTranDate() {
            return tranDate;
        }

        public void setTranDate(String tranDate) {
            this.tranDate = tranDate;
        }

        public String getDeliveryVerifyOnStr() {
            return deliveryVerifyOnStr;
        }

        public void setDeliveryVerifyOnStr(String deliveryVerifyOnStr) {
            this.deliveryVerifyOnStr = deliveryVerifyOnStr;
        }

        public String getDeliveryVerifyByName() {
            return deliveryVerifyByName;
        }

        public void setDeliveryVerifyByName(String deliveryVerifyByName) {
            this.deliveryVerifyByName = deliveryVerifyByName;
        }
    }

}

// Datum.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

