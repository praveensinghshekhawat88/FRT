package com.callmangement.ui.pos_help.model

import com.google.gson.annotations.SerializedName

class ModelFAQList {
    @SerializedName("id")
    var id: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("question")
    var question: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("answer")
    var answer: String? = null
        get() {
            if (field == null) return ""
            return field
        }
    var isSelectFlag: Boolean = false
    var isVisibleItemFlag: Boolean = false
}
