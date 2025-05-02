package com.callmangement.ui.ins_weighing_scale.model.district;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ModelDistrict_w {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("District_List") private List<ModelDistrictList_w> district_List = new ArrayList<>();

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

    public List<ModelDistrictList_w> getDistrict_List() {
        return district_List;
    }

    public void setDistrict_List(List<ModelDistrictList_w> district_List) {
        this.district_List = district_List;
    }
}
