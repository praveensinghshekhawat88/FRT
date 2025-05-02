package com.callmangement.ui.ins_weighing_scale.model.SaveInstall;

public class SaveRoot {
    public SaveResponse Response;
    public String status;

    public SaveRoot(SaveResponse response, String status) {
        Response = response;
        this.status = status;
    }

    public SaveResponse getResponse() {
        return Response;
    }

    public void setResponse(SaveResponse response) {
        Response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
