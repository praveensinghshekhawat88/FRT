package com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal;

import java.util.ArrayList;

public class WeightInsRoot {
    public ArrayList<WeighInsData> Data;
    public String message;
    public String status;


    public WeightInsRoot(ArrayList<WeighInsData> data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public ArrayList<WeighInsData> getData() {
        return Data;
    }

    public void setData(ArrayList<WeighInsData> data) {
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
