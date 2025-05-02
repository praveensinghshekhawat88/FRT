package com.callmangement.EHR.models;

public class FeedbackDetailByFpsRoot {
    public FeedbackDetailByFpsData Data;
    public String message;
    public String status;


    public FeedbackDetailByFpsRoot(FeedbackDetailByFpsData data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public FeedbackDetailByFpsData getData() {
        return Data;
    }

    public void setData(FeedbackDetailByFpsData data) {
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
