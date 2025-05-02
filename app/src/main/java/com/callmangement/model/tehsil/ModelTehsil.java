package com.callmangement.model.tehsil;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelTehsil {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("Tehsil_List") private List<ModelTehsilList> Tehsil_List = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ModelTehsilList> getTehsil_List() {
        return Tehsil_List;
    }

    public void setTehsil_List(List<ModelTehsilList> tehsil_List) {
        Tehsil_List = tehsil_List;
    }
}
