package com.callmangement.ui.errors.model;

import java.util.ArrayList;

public class GetRemarkRoot {

    public ArrayList<GetRemarkDatum> Data;
    public String message;
    public String status;

    public GetRemarkRoot(ArrayList<GetRemarkDatum> data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public ArrayList<GetRemarkDatum> getData() {
        return Data;
    }

    public void setData(ArrayList<GetRemarkDatum> data) {
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
