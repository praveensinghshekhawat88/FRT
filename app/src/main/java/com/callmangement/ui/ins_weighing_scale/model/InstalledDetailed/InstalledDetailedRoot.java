package com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed;

public class InstalledDetailedRoot {

    public InstalledDetailedData Data;
    public String message;
    public String status;


    public InstalledDetailedRoot(InstalledDetailedData data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public InstalledDetailedData getData() {
        return Data;
    }

    public void setData(InstalledDetailedData data) {
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
