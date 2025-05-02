package com.callmangement.model.reports

import com.google.gson.annotations.SerializedName

class ModelSLAReportDetails {
    @JvmField
    @SerializedName("status")
    var status: String? = null

    @JvmField
    @SerializedName("message")
    var message: String? = null

    @JvmField
    @SerializedName("SLAReport_Detail")
    var sla_reports_infos: List<ModelSLAReportDetailsList> = ArrayList()
}
