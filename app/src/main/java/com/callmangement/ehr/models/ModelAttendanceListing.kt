package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

class ModelAttendanceListing(
    var totalItems: Int,
    @JvmField var totalPages: Int,
    @SerializedName("Data")
    var data: MutableList<AttendanceDetailsInfo>,
    var currentPage: Int,
    @JvmField var message: String,
    @JvmField var status: String
)

