package com.callmangement.model.reports

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SLA_Reports_Info : Serializable {
    @JvmField
    @SerializedName("district")
    var district: String? = null

    @JvmField
    @SerializedName("districtId")
    var districtId: Int? = null

    @JvmField
    @SerializedName("complaintCount")
    var complaintCount: Int? = null
}
