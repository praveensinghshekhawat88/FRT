package com.callmangement.EHR.models;

public class SaveFeedbackbyDCRoot {
    public SaveFeedbackbyDCData Response;
    public String message;
    public String status;


    public SaveFeedbackbyDCRoot(SaveFeedbackbyDCData response, String message, String status) {
        Response = response;
        this.message = message;
        this.status = status;
    }


    public SaveFeedbackbyDCData getResponse() {
        return Response;
    }

    public void setResponse(SaveFeedbackbyDCData response) {
        Response = response;
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
