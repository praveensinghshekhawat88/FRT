package com.callmangement.model.reports

import com.google.gson.annotations.SerializedName

class ModelSLAReport {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("SLAReport_Count")
    var sla_reports_infos: MutableList<SLA_Reports_Info>? = ArrayList()
}
