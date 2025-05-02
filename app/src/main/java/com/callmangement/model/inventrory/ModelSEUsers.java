package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelSEUsers {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("SEUsersList") private List<ModelSEUsersList> SEUsersList = new ArrayList<>();

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

    public List<ModelSEUsersList> getSEUsersList() {
        return SEUsersList;
    }

    public void setSEUsersList(List<ModelSEUsersList> SEUsersList) {
        this.SEUsersList = SEUsersList;
    }
}
