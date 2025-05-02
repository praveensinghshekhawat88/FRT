package com.callmangement.ui.ins_weighing_scale.model.fps;

public class DetailByFpsRoot {
    public DetailByFpsData Data;
    public String message;
    public String status;


    public DetailByFpsRoot(DetailByFpsData data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public DetailByFpsData getData() {
        return Data;
    }

    public void setData(DetailByFpsData data) {
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
