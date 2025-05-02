package com.callmangement.model.attendance

import com.google.gson.annotations.SerializedName

class ModelAttendanceList {
    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("status")
    var status: String? = null

    @SerializedName("Data")
    var data: List<ModelAttendanceData> = ArrayList()
}
