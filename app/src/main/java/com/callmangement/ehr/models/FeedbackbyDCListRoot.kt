package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class FeedbackbyDCListRoot(
    @SerializedName("Data")
    var data: ArrayList<FeedbackbyDCListDatum>,
    @JvmField var message: String,
    @JvmField var status: String
)
