package com.callmangement.model.inventrory;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelReceiveMaterialListData implements Serializable {
    @SerializedName("date") private String date;
    @SerializedName("quantity") private String quantity;
    @SerializedName("status") private String status;
    @SerializedName("materialList") private List<ModelReceiveMaterialListDataList> materialList = new ArrayList<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ModelReceiveMaterialListDataList> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<ModelReceiveMaterialListDataList> materialList) {
        this.materialList = materialList;
    }
}
