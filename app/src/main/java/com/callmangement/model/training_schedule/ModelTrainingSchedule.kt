package com.callmangement.model.training_schedule

import com.google.gson.annotations.SerializedName

class ModelTrainingSchedule {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("Data")
    var list: List<ModelTrainingScheduleList> = ArrayList()
}
