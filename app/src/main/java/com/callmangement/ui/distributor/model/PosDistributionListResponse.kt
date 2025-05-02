package com.callmangement.ui.distributor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PosDistributionListResponse {
    @SerializedName("posDistributionDetailList")
    @Expose
    var posDistributionDetailList: List<PosDistributionDetail>? = null
        get() {
            if (field == null) return ArrayList()
            return field
        }

    @SerializedName("message")
    @Expose
    var message: Any? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
}
