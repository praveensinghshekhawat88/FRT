package com.callmangement.ui.biometric_delivery.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateDeviceTypeToChangeFSensorResp implements Serializable {

    private String message;
    private String status;
    @SerializedName("Data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

    public class Data implements Serializable{
        private String message;
        private boolean status,isUpdatedDtype;
        private String fpscode,deviceCode,infoMsgEn,infoMsgHi,infoDescMsgHi,infoDescMsgEn;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getFpscode() {
            return fpscode;
        }

        public void setFpscode(String fpscode) {
            this.fpscode = fpscode;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public String getInfoMsgEn() {
            return infoMsgEn;
        }

        public void setInfoMsgEn(String infoMsgEn) {
            this.infoMsgEn = infoMsgEn;
        }

        public String getInfoMsgHi() {
            return infoMsgHi;
        }

        public void setInfoMsgHi(String infoMsgHi) {
            this.infoMsgHi = infoMsgHi;
        }

        public String getInfoDescMsgHi() {
            return infoDescMsgHi;
        }

        public void setInfoDescMsgHi(String infoDescMsgHi) {
            this.infoDescMsgHi = infoDescMsgHi;
        }

        public String getInfoDescMsgEn() {
            return infoDescMsgEn;
        }

        public void setInfoDescMsgEn(String infoDescMsgEn) {
            this.infoDescMsgEn = infoDescMsgEn;
        }

        public boolean isUpdatedDtype() {
            return isUpdatedDtype;
        }

        public void setUpdatedDtype(boolean updatedDtype) {
            isUpdatedDtype = updatedDtype;
        }
    }


}
