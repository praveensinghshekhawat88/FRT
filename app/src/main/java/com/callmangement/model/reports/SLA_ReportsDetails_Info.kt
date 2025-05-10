package com.callmangement.model.reports

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SLA_ReportsDetails_Info : Serializable {
    @SerializedName("district")
    var district: String? = null

    @SerializedName("districtId")
    var districtId: Int? = null

    @SerializedName("complaintCount")
    var complaintCount: Int? = null
}
