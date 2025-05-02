
package com.callmangement.ui.distributor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PosDistributionListResponse {
    @SerializedName("posDistributionDetailList")
    @Expose
    private List<PosDistributionDetail> posDistributionDetailList = null;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<PosDistributionDetail> getPosDistributionDetailList() {
        if (posDistributionDetailList == null)
            return new ArrayList<>();
        return posDistributionDetailList;
    }

    public void setPosDistributionDetailList(List<PosDistributionDetail> posDistributionDetailList) {
        this.posDistributionDetailList = posDistributionDetailList;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
