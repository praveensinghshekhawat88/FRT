package com.callmangement.ui.ins_weighing_scale.model.Count;

public class CountDatum {

    public boolean status;
    public String message;
    public int totalDelivered;
    public int deliveryPending;
    public int installationPending;
    public int totalDeliveryPending;
    public int totalDispatched;
    public int totalInstalled;
    public int totalInstallationPending;
    public int districtId;
    public Object districtName;
    public int totalFPS;
    public int dispatched;
    public int delivered;
    public int installed;


    public CountDatum(boolean status, String message, int totalDelivered, int deliveryPending, int installationPending, int totalDeliveryPending, int totalDispatched, int totalInstalled, int totalInstallationPending, int districtId, Object districtName, int totalFPS, int dispatched, int delivered, int installed) {
        this.status = status;
        this.message = message;
        this.totalDelivered = totalDelivered;
        this.deliveryPending = deliveryPending;
        this.installationPending = installationPending;
        this.totalDeliveryPending = totalDeliveryPending;
        this.totalDispatched = totalDispatched;
        this.totalInstalled = totalInstalled;
        this.totalInstallationPending = totalInstallationPending;
        this.districtId = districtId;
        this.districtName = districtName;
        this.totalFPS = totalFPS;
        this.dispatched = dispatched;
        this.delivered = delivered;
        this.installed = installed;
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

    public int getTotalDelivered() {
        return totalDelivered;
    }

    public void setTotalDelivered(int totalDelivered) {
        this.totalDelivered = totalDelivered;
    }

    public int getDeliveryPending() {
        return deliveryPending;
    }

    public void setDeliveryPending(int deliveryPending) {
        this.deliveryPending = deliveryPending;
    }

    public int getInstallationPending() {
        return installationPending;
    }

    public void setInstallationPending(int installationPending) {
        this.installationPending = installationPending;
    }

    public int getTotalDeliveryPending() {
        return totalDeliveryPending;
    }

    public void setTotalDeliveryPending(int totalDeliveryPending) {
        this.totalDeliveryPending = totalDeliveryPending;
    }

    public int getTotalDispatched() {
        return totalDispatched;
    }

    public void setTotalDispatched(int totalDispatched) {
        this.totalDispatched = totalDispatched;
    }

    public int getTotalInstalled() {
        return totalInstalled;
    }

    public void setTotalInstalled(int totalInstalled) {
        this.totalInstalled = totalInstalled;
    }

    public int getTotalInstallationPending() {
        return totalInstallationPending;
    }

    public void setTotalInstallationPending(int totalInstallationPending) {
        this.totalInstallationPending = totalInstallationPending;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public Object getDistrictName() {
        return districtName;
    }

    public void setDistrictName(Object districtName) {
        this.districtName = districtName;
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

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getInstalled() {
        return installed;
    }

    public void setInstalled(int installed) {
        this.installed = installed;
    }
}
