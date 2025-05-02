package com.callmangement.ui.errors.model;

import java.util.ArrayList;

public class GetPosDeviceErrorsRoot {
    public ArrayList<GetPosDeviceErrorDatum> Data;
    public String message;
    public String status;


    public GetPosDeviceErrorsRoot(ArrayList<GetPosDeviceErrorDatum> data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public ArrayList<GetPosDeviceErrorDatum> getData() {
        return Data;
    }

    public void setData(ArrayList<GetPosDeviceErrorDatum> data) {
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
