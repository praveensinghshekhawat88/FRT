package com.callmangement.ui.distributor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PosDistributionFormDetailResponse {
    @JvmField
    @SerializedName("posDistributionDetail")
    @Expose
    var posDistributionDetail: PosDistributionDetail? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @JvmField
    @SerializedName("status")
    @Expose
    var status: String? = null
}
