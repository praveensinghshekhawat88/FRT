package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelGetSurveyFormDocList {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("Data")
    var list: ArrayList<SurveyFormDocInfo> = ArrayList()
}
