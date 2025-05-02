package com.callmangement.ui.biometric_delivery.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SensorDistributionDetailsListResp implements Serializable {

    public int totalItems;
    public int totalPages;
    public ArrayList<Data> Data;
    public int currentPage;
    public String message;
    public String status;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public ArrayList<Data> getData() {
        return Data;
    }

    public void setData(ArrayList<Data> data) {
        Data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Data implements Serializable {
        public boolean status;
        public String message;
        public int totalItems;
        public int totalPages;
        public int districtId;
        public String fpscode;
        public String district;
        public String deviceCode;
        public boolean isBiometricMapped;
        public boolean isBiometrictDistributed;
        public String biometricSensorStatus;
        public String biometrictMappedOnStr;
        public String biometricSensorImage;


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

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
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

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public boolean isBiometricMapped() {
            return isBiometricMapped;
        }

        public void setBiometricMapped(boolean biometricMapped) {
            isBiometricMapped = biometricMapped;
        }

        public boolean isBiometrictDistributed() {
            return isBiometrictDistributed;
        }

        public void setBiometrictDistributed(boolean biometrictDistributed) {
            isBiometrictDistributed = biometrictDistributed;
        }

        public String getBiometricSensorStatus() {
            return biometricSensorStatus;
        }

        public void setBiometricSensorStatus(String biometricSensorStatus) {
            this.biometricSensorStatus = biometricSensorStatus;
        }

        public String getBiometrictMappedOnStr() {
            return biometrictMappedOnStr;
        }

        public void setBiometrictMappedOnStr(String biometrictMappedOnStr) {
            this.biometrictMappedOnStr = biometrictMappedOnStr;
        }

        public String getBiometricSensorImage() {
            return biometricSensorImage;
        }

        public void setBiometricSensorImage(String biometricSensorImage) {
            this.biometricSensorImage = biometricSensorImage;
        }
    }
}
