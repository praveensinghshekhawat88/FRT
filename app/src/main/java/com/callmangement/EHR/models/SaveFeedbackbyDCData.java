package com.callmangement.EHR.models;

public class SaveFeedbackbyDCData {

    public String Message;
    public boolean status;

    public SaveFeedbackbyDCData(String message, boolean status) {
        Message = message;
        this.status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
