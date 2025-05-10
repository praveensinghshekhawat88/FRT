package com.callmangement.ui.pos_issue.model

import com.google.gson.annotations.SerializedName

class ModelIssuesList {
    @SerializedName("id")
    var id: String? = null
        get() {
            if (field == null) return "0"
            return field
        }

    @SerializedName("issueName")
    var issueName: String? = null ?: ""

    override fun toString(): String {
        return issueName!!
    }
}
