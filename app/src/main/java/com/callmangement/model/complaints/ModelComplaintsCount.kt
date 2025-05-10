package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName

class ModelComplaintsCount {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("Complaints_Count")
    var complaints_Count: ModelComplaintsCountData = ModelComplaintsCountData()
}
