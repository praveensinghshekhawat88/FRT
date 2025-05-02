package com.callmangement.ui.errors.model;

import java.util.ArrayList;

public class GetErrorImagesRoot {

    public ArrayList<GetErrorImagesDatum> Data;
    public String message;
    public String status;


    public GetErrorImagesRoot(ArrayList<GetErrorImagesDatum> data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public ArrayList<GetErrorImagesDatum> getData() {
        return Data;
    }

    public void setData(ArrayList<GetErrorImagesDatum> data) {
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
