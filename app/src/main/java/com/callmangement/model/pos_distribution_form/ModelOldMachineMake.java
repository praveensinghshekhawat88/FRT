package com.callmangement.model.pos_distribution_form;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModelOldMachineMake implements Serializable {
    @SerializedName("id") private String id;
    @SerializedName("name") private String name;

    public String getId() {
        if (id == null)
            return "0";
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if (name == null)
            return "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
