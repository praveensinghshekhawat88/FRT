package com.callmangement.model.fps_repeat_on_service_center

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ModelRepeatFpsComplaints {
    @SerializedName("Parts")
    @Expose
    var parts: List<ModelRepeatFpsComplaintsList>? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null
}
