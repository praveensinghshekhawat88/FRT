package com.callmangement.ui.ins_weighing_scale.model.Count;

import java.util.ArrayList;

public class CountRoot {

    public ArrayList<CountDatum> Data;
    public String message;
    public String status;


    public CountRoot(ArrayList<CountDatum> data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public ArrayList<CountDatum> getData() {
        return Data;
    }

    public void setData(ArrayList<CountDatum> data) {
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
