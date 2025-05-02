package com.callmangement.ui.iris_derivery_installation.Model;



public class InstalledDetailedResponse {

    public InstalledDtlDataIris Data;
    public String message;
    public String status;


    public InstalledDetailedResponse(InstalledDtlDataIris data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public InstalledDtlDataIris getData() {
        return Data;
    }

    public void setData(InstalledDtlDataIris data) {
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
