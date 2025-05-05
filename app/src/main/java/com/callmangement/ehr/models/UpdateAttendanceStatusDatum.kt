package com.callmangement.ehr.models

class UpdateAttendanceStatusDatum(
    var userId: Any,
    @JvmField var remark: String,
    var toUserId: Any,
    var mesg: String,
    var attendanceId: Any,
    var isFlagStatus: Boolean,
    var attendanceStatusDes: Any,
    @JvmField var attendanceStatus: String,
    @JvmField var statusID: Int
)
