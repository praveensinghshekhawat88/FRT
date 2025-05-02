package com.callmangement.EHR.models;

import java.util.ArrayList;

public class FeedbackbyDCListRoot {
    public ArrayList<FeedbackbyDCListDatum> Data;
    public String message;
    public String status;


    public FeedbackbyDCListRoot(ArrayList<FeedbackbyDCListDatum> data, String message, String status) {
        Data = data;
        this.message = message;
        this.status = status;
    }


    public ArrayList<FeedbackbyDCListDatum> getData() {
        return Data;
    }

    public void setData(ArrayList<FeedbackbyDCListDatum> data) {
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
