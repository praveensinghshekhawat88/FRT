package com.callmangement.ui.ins_weighing_scale.model.challan;

public class challanRoot {

    public challanData Data;
    public String message;
    public String status;

    public challanRoot(challanData data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public challanData getData() {
        return Data;
    }

    public void setData(challanData data) {
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
