package com.callmangement.ui.errors.model;

public class UpdateErroeReqRoot {

    public UpdateErrorReqResponse Response;
    public String status;


    public UpdateErroeReqRoot(UpdateErrorReqResponse response, String status) {
        Response = response;
        this.status = status;
    }


    public UpdateErrorReqResponse getResponse() {
        return Response;
    }

    public void setResponse(UpdateErrorReqResponse response) {
        Response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
