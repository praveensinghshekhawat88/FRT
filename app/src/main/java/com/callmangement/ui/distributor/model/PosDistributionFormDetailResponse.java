
package com.callmangement.ui.distributor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PosDistributionFormDetailResponse {

    @SerializedName("posDistributionDetail")
    @Expose
    private PosDistributionDetail posDistributionDetail;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public PosDistributionDetail getPosDistributionDetail() {
        return posDistributionDetail;
    }

    public void setPosDistributionDetail(PosDistributionDetail posDistributionDetail) {
        this.posDistributionDetail = posDistributionDetail;
    }

    public String getMessage() {
        if (message == null)
            return "";
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
