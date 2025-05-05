package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class FeedbackDetailByFpsRoot(
    @SerializedName("Data")
    var data: FeedbackDetailByFpsData,
    @JvmField var message: String,
    @JvmField var status: String
)
