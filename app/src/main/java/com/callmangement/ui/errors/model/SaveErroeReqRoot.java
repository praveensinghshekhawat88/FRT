package com.callmangement.ui.errors.model;

public class SaveErroeReqRoot {


    public SaveErrorReqResponse Response;
    public String status;


    public SaveErroeReqRoot(SaveErrorReqResponse response, String status) {
        Response = response;
        this.status = status;
    }

    public SaveErrorReqResponse getResponse() {
        return Response;
    }

    public void setResponse(SaveErrorReqResponse response) {
        Response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
