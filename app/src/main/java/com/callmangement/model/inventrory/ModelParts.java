package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelParts {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("Parts") private List<ModelPartsList> Parts = new ArrayList();

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

    public List<ModelPartsList> getParts() {
        return Parts;
    }

    public void setParts(List<ModelPartsList> parts) {
        Parts = parts;
    }
}
