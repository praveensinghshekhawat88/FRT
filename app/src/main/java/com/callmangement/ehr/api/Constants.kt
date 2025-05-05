package com.callmangement.ehr.api

object Constants {
    const val CONNECTION_TIME_OUT: Int = 240

    const val CAMP_STATUS_ID_INITIATED: String = "1"
    const val CAMP_STATUS_ID_ORGANISED: String = "3"
    const val CAMP_STATUS_ID_COMPLETED: String = "4"

    const val SURVEY_STATUS_ID_INITIATED: String = "1"
    const val SURVEY_STATUS_ID_COMPLETED: String = "2"

    @JvmField
    var saveImagePath: String = "eHr/Images"
}
