package com.callmangement.ui.biometric_delivery.model;

public class DetailsByFPSForSensorRoot {

    public DetailByFpsForSensorData Data;
    public String message;
    public String status;


    public DetailByFpsForSensorData getData() {
        return Data;
    }

    public void setData(DetailByFpsForSensorData data) {
        Data = data;
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
}
