package com.callmangement.model.reports

import java.io.Serializable

class Monthly_Reports_Info : Serializable {
    @JvmField
    var date: String? = null
    @JvmField
    var not_resolved: Int = 0
    @JvmField
    var resolved: Int = 0
    @JvmField
    var total: Int = 0
}
