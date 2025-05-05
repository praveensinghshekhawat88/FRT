package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SurveyFormDocInfo : Serializable {
    @JvmField
    @SerializedName("documentPath")
    var documentPath: String? = null

    @SerializedName("documentName")
    var documentName: String? = null
}

