package com.callmangement.ehr.models

class DashboardUserDetails(
    var message: Any,
    var year: Int,
    var month: Int,
    var isStatus: Boolean,
    @JvmField var tour: Int,
    @JvmField var absent: Int,
    @JvmField var attendanceCount: String,
    @JvmField var leave: Int,
    @JvmField var halfDay: Int,
    @JvmField var persent: Int,
    @JvmField var createdCampCount: String,
    @JvmField var organizedCampCount: String,
    var userId: Any
)
