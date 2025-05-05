package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class SaveFeedbackbyDCRoot(
    @SerializedName("Response")
    var response: SaveFeedbackbyDCData,
    var message: String,
    @JvmField var status: String
)
