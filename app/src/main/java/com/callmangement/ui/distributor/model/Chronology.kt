package com.callmangement.ui.distributor.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Chronology : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("calendarType")
    @Expose
    var calendarType: String? = null
}
