package com.callmangement.model.complaints

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateResponse {
    @SerializedName("id")
    @Expose
    @Transient
    var id: String? = null

    @SerializedName("seRemark")
    @Expose
    var seRemark: String? = null

    @SerializedName("complaintRegNo")
    @Expose
    var complaintRegNo: String? = null
}
