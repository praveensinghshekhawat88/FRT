package com.callmangement.model.attendance

import com.google.gson.annotations.SerializedName

class ModelAttendanceList {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("Data")
    var data: List<ModelAttendanceData> = ArrayList()
}
