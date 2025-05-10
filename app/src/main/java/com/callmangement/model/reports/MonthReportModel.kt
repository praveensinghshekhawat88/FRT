package com.callmangement.model.reports

import com.callmangement.model.complaints.ModelComplaintList

class MonthReportModel {
    @JvmField
    var date: String? = null
    @JvmField
    var list: MutableList<ModelComplaintList>? = null
}
