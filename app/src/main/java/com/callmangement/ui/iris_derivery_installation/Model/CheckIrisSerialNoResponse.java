package com.callmangement.ui.iris_derivery_installation.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckIrisSerialNoResponse implements Serializable {
    @SerializedName("Response")
    private Response response;
    private String message;
    private String status;

    public Response getResponse() { return response; }
    public void setResponse(Response value) { this.response = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }

}



