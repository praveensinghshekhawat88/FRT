package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelEditASurveyForm {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null
}
