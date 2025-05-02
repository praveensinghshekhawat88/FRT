package com.callmangement.ui.iris_derivery_installation.Model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class IrisDashboardResponse implements Serializable {

    @SerializedName("Data")
    public ArrayList<Data> data;
    public String message;
    public String status;


    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
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

    public class Data {

        public boolean status;
        public String message;
        public int totalDispatched;
        public int totalInstallationPending;
        public int totalIrisDevice;
        public int totalDeliveryPending;
        public int totalIRISDeliverdInstalled;
        public int totalIRISDispatched;
        public int totalIRISDeliveryPending;
        public int totalWeinghingScale;
        public int totalDelivered;
        public int totalInstalled;
        public int installationPending;
        public int deliveryPending;
        public String districtName;
        public int districtId;
        public int delivered;
        public int totalGoldTek;
        public int totalFPS;
        public int dispatched;
        public int installed;
        public int totalSipTek;

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

        public int getTotalDispatched() {
            return totalDispatched;
        }

        public void setTotalDispatched(int totalDispatched) {
            this.totalDispatched = totalDispatched;
        }

        public int getTotalInstallationPending() {
            return totalInstallationPending;
        }

        public void setTotalInstallationPending(int totalInstallationPending) {
            this.totalInstallationPending = totalInstallationPending;
        }

        public int getTotalIrisDevice() {
            return totalIrisDevice;
        }

        public void setTotalIrisDevice(int totalIrisDevice) {
            this.totalIrisDevice = totalIrisDevice;
        }

        public int getTotalDeliveryPending() {
            return totalDeliveryPending;
        }

        public void setTotalDeliveryPending(int totalDeliveryPending) {
            this.totalDeliveryPending = totalDeliveryPending;
        }

        public int getTotalIRISDeliverdInstalled() {
            return totalIRISDeliverdInstalled;
        }

        public void setTotalIRISDeliverdInstalled(int totalIRISDeliverdInstalled) {
            this.totalIRISDeliverdInstalled = totalIRISDeliverdInstalled;
        }

        public int getTotalIRISDispatched() {
            return totalIRISDispatched;
        }

        public void setTotalIRISDispatched(int totalIRISDispatched) {
            this.totalIRISDispatched = totalIRISDispatched;
        }

        public int getTotalIRISDeliveryPending() {
            return totalIRISDeliveryPending;
        }

        public void setTotalIRISDeliveryPending(int totalIRISDeliveryPending) {
            this.totalIRISDeliveryPending = totalIRISDeliveryPending;
        }

        public int getTotalWeinghingScale() {
            return totalWeinghingScale;
        }

        public void setTotalWeinghingScale(int totalWeinghingScale) {
            this.totalWeinghingScale = totalWeinghingScale;
        }

        public int getTotalDelivered() {
            return totalDelivered;
        }

        public void setTotalDelivered(int totalDelivered) {
            this.totalDelivered = totalDelivered;
        }

        public int getTotalInstalled() {
            return totalInstalled;
        }

        public void setTotalInstalled(int totalInstalled) {
            this.totalInstalled = totalInstalled;
        }

        public int getInstallationPending() {
            return installationPending;
        }

        public void setInstallationPending(int installationPending) {
            this.installationPending = installationPending;
        }

        public int getDeliveryPending() {
            return deliveryPending;
        }

        public void setDeliveryPending(int deliveryPending) {
            this.deliveryPending = deliveryPending;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public int getDistrictId() {
            return districtId;
        }

        public void setDistrictId(int districtId) {
            this.districtId = districtId;
        }

        public int getDelivered() {
            return delivered;
        }

        public void setDelivered(int delivered) {
            this.delivered = delivered;
        }

        public int getTotalGoldTek() {
            return totalGoldTek;
        }

        public void setTotalGoldTek(int totalGoldTek) {
            this.totalGoldTek = totalGoldTek;
        }

        public int getTotalFPS() {
            return totalFPS;
        }

        public void setTotalFPS(int totalFPS) {
            this.totalFPS = totalFPS;
        }

        public int getDispatched() {
            return dispatched;
        }

        public void setDispatched(int dispatched) {
            this.dispatched = dispatched;
        }

        public int getInstalled() {
            return installed;
        }

        public void setInstalled(int installed) {
            this.installed = installed;
        }

        public int getTotalSipTek() {
            return totalSipTek;
        }

        public void setTotalSipTek(int totalSipTek) {
            this.totalSipTek = totalSipTek;
        }
    }
}

