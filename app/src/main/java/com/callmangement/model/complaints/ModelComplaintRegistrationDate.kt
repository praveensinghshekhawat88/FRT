package com.callmangement.model.complaints

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelComplaintRegistrationDate : Serializable {
    @SerializedName("year")
    var year: String? = null

    @SerializedName("month")
    var month: String? = null
        get() {
            if (field == null) return ""
            return field
        }

    @SerializedName("era")
    var era: String? = null

    @SerializedName("leapYear")
    var leapYear: String? = null

    @SerializedName("dayOfMonth")
    var dayOfMonth: String? = null

    @SerializedName("monthValue")
    var monthValue: Int? = null

    @SerializedName("dayOfWeek")
    var dayOfWeek: String? = null

    @SerializedName("dayOfYear")
    var dayOfYear: String? = null
}
