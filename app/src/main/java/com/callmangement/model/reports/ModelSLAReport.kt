package com.callmangement.model.reports

import com.google.gson.annotations.SerializedName

class ModelSLAReport {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("SLAReport_Count")
    var sla_reports_infos: List<SLA_Reports_Info> = ArrayList()
}
