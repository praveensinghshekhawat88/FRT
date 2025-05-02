package com.callmangement.ui.errors.model;

public class UpdateErrorStatusRoot {
    public UpdateErrorStatusResponse Response;
    public String status;


    public UpdateErrorStatusRoot(UpdateErrorStatusResponse response, String status) {
        Response = response;
        this.status = status;
    }


    public UpdateErrorStatusResponse getResponse() {
        return Response;
    }

    public void setResponse(UpdateErrorStatusResponse response) {
        Response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
