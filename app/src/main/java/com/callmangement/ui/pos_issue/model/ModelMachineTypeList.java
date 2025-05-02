package com.callmangement.ui.pos_issue.model;

import com.google.gson.annotations.SerializedName;

public class ModelMachineTypeList {
    @SerializedName("id") private String id;
    @SerializedName("machineType") private String machineType;

    public String getId() {
        if (id == null)
            return "0";
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMachineType() {
        if (machineType == null)
            return "";
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    @Override
    public String toString() {
        return machineType;
    }
}
