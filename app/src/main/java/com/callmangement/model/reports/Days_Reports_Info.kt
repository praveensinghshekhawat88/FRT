package com.callmangement.model.reports

import java.io.Serializable

class Days_Reports_Info : Serializable {
    @JvmField
    var day: String? = null
    @JvmField
    var days_count: Int = 0

    override fun toString(): String {
        return day!!
    }
}
