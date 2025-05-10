package com.callmangement.model.training_schedule

import com.google.gson.annotations.SerializedName

class ModelUpdateTrainingSchedule {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null
}
