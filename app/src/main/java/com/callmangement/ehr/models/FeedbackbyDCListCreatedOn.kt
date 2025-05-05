package com.callmangement.ehr.models

import java.io.Serializable

class FeedbackbyDCListCreatedOn(
    var year: Int,
    var monthValue: Int,
    var dayOfMonth: Int,
    var hour: Int,
    var minute: Int,
    var second: Int,
    var nano: Int,
    var dayOfWeek: String,
    var dayOfYear: Int,
    var month: String,
    var chronology: FeedbackbyDCListChronology
) :
    Serializable
