package com.callmangement.model.fps_wise_complaints

import com.google.gson.annotations.SerializedName

class ModelFPSComplaint {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("fPSComplainHistoryReqList")
    private var fPSComplainHistoryReqList: List<ModelFPSComplaintList> = ArrayList()

    fun getfPSComplainHistoryReqList(): List<ModelFPSComplaintList> {
        return fPSComplainHistoryReqList
    }

    fun setfPSComplainHistoryReqList(fPSComplainHistoryReqList: List<ModelFPSComplaintList>) {
        this.fPSComplainHistoryReqList = fPSComplainHistoryReqList
    }
}
