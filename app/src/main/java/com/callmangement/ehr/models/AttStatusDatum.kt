package com.callmangement.ehr.models

import com.google.gson.annotations.SerializedName

//data class AttStatusDatum(
//    val createdBy: Any?,
//    val createdOn: Any?,
//    val isActive: Boolean,
//    val attendanceStatusDes: String,
//    val mesg: Any?,
//    @SerializedName("flagStatus") val flagStatus: Boolean,
//    val statusID: Int,
//    val attendanceStatus: String
//)


data class AttStatusDatum(
    val createdBy: Any?,
    val createdOn: Any?,
    val isActive: Boolean,
    val attendanceStatusDes: String,
    val mesg: Any?,
    @SerializedName("flagStatus") val flagStatus: Boolean,
    val statusID: Int,
    val attendanceStatus: String
)
