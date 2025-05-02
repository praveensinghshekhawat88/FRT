package com.callmangement.model.fps_wise_complaints;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelFPSDistTehWise {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("FPSList") private List<ModelFPSDistTehWiseList> modelFPSDistTehWiseList = new ArrayList<>();

    public String getStatus() {
        if (status == null)
            return "";
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        if (message == null)
            return "";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ModelFPSDistTehWiseList> getModelFPSDistTehWiseList() {
        return modelFPSDistTehWiseList;
    }

    public void setModelFPSDistTehWiseList(List<ModelFPSDistTehWiseList> modelFPSDistTehWiseList) {
        this.modelFPSDistTehWiseList = modelFPSDistTehWiseList;
    }
}
