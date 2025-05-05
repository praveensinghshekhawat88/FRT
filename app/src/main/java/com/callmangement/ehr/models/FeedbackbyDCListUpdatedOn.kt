package com.callmangement.ehr.models

import java.io.Serializable

class FeedbackbyDCListUpdatedOn(
    @JvmField var year: Int,
    @JvmField var monthValue: Int,
    @JvmField var dayOfMonth: Int,
    var hour: Int,
    var minute: Int,
    var second: Int,
    var nano: Int,
    @JvmField var dayOfWeek: String,
    var dayOfYear: Int,
    var month: String,
    var chronology: FeedbackbyDCListChronology
) :
    Serializable
