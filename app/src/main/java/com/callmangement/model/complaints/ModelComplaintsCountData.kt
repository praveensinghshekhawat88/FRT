package com.callmangement.model.complaints;

import com.google.gson.annotations.SerializedName;

public class ModelComplaintsCountData {
    @SerializedName("total") private Integer total;
    @SerializedName("resolved") private Integer resolved;
    @SerializedName("notResolve") private Integer notResolve;
    @SerializedName("sendToSECenter") private Integer sendToSECenter;
    @SerializedName("uploadPendingChallan") private Integer uploadPendingChallan;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getResolved() {
        return resolved;
    }

    public void setResolved(Integer resolved) {
        this.resolved = resolved;
    }

    public Integer getNotResolve() {
        return notResolve;
    }

    public void setNotResolve(Integer notResolve) {
        this.notResolve = notResolve;
    }

    public Integer getSendToSECenter() {
        return sendToSECenter;
    }

    public void setSendToSECenter(Integer sendToSECenter) {
        this.sendToSECenter = sendToSECenter;
    }


    public Integer getUploadPendingChallan() {
        return uploadPendingChallan;
    }

    public void setUploadPendingChallan(Integer uploadPendingChallan) {
        this.uploadPendingChallan = uploadPendingChallan;
    }
}
