package com.callmangement.ui.distributor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TranDate : Serializable {
    @SerializedName("dayOfWeek")
    @Expose
    var dayOfWeek: String? = null

    @SerializedName("dayOfYear")
    @Expose
    var dayOfYear: Int? = null

    @SerializedName("dayOfMonth")
    @Expose
    var dayOfMonth: Int? = null

    @SerializedName("hour")
    @Expose
    var hour: Int? = null

    @SerializedName("minute")
    @Expose
    var minute: Int? = null

    @SerializedName("month")
    @Expose
    var month: String? = null

    @SerializedName("monthValue")
    @Expose
    var monthValue: Int? = null

    @SerializedName("nano")
    @Expose
    var nano: Int? = null

    @SerializedName("second")
    @Expose
    var second: Int? = null

    @SerializedName("year")
    @Expose
    var year: Int? = null

    @SerializedName("chronology")
    @Expose
    var chronology: Chronology? = null
}
