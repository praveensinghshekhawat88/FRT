package com.callmangement.model.reports

import com.google.gson.annotations.SerializedName

class ModelSLAReportDetails {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("SLAReport_Detail")
    var sla_reports_infos: MutableList<ModelSLAReportDetailsList>? = ArrayList()
}
