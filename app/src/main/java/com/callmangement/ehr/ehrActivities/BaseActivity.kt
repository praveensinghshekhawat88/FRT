package com.callmangement.ehr.ehrActivities

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    // Login
    external fun Login(): String?
    external fun LoginQueryParams(): String?

    // Logout
    external fun Logout(): String?
    external fun LogoutQueryParams(): String?

    // CheckVersion
    external fun CheckVersion(): String?
    external fun CheckVersionQueryParams(): String?

    // DistictList
    external fun DistictList(): String?

    // TehsilListByDistict
    external fun TehsilListByDistict(): String?
    external fun TehsilListByDistictQueryParams(): String?

    // SEUserListByDistict
    external fun SEUserListByDistict(): String?
    external fun SEUserListByDistictQueryParams(): String?

    // CreateCamp
    external fun CreateCamp(): String?
    external fun CreateCampQueryParams(): String?

    // GetCampList
    external fun GetCampList(): String?
    external fun GetCampListQueryParams(): String?

    // OrganiseACamp
    external fun OrganiseACamp(): String?
    external fun OrganiseACampQueryParams(): String?

    // GetDealersByBlock
    external fun GetDealersByBlock(): String?
    external fun GetDealersByBlockQueryParams(): String?

    // UploadDocumentOfDailyWorkReport
    external fun UploadDocumentOfDailyWorkReport(): String?
    external fun UploadDocumentOfDailyWorkReportQueryParams(): String?

    // GetCampDocList
    external fun GetCampDocList(): String?
    external fun GetCampDocListQueryParams(): String?

    // DeleteACamp
    external fun DeleteACamp(): String?
    external fun DeleteACampQueryParams(): String?

    // CreateSurveyForm
    external fun CreateSurveyForm(): String?
    external fun CreateSurveyFormQueryParams(): String?

    // EditSurveyForm
    external fun EditSurveyForm(): String?
    external fun EditSurveyFormQueryParams(): String?

    // GetSurveyFormList
    external fun GetSurveyFormList(): String?
    external fun GetSurveyFormListQueryParams(): String?

    // DeleteASurveyForm
    external fun DeleteASurveyForm(): String?
    external fun DeleteASurveyFormQueryParams(): String?

    // UploadDocumentOfSurveyFormReport
    external fun UploadDocumentOfSurveyFormReport(): String?
    external fun UploadDocumentOfSurveyFormReportQueryParams(): String?

    // GetSurveyFormDocList
    external fun GetSurveyFormDocList(): String?
    external fun GetSurveyFormDocListQueryParams(): String?

    // MArkAttendance
    external fun MArkAttendance(): String?
    external fun MArkAttendanceQueryParams(): String?

    companion object {
        // Used to load the 'e_hr' library on application startup.
        init {
            System.loadLibrary("e_hr")
        }

        @JvmStatic
        external fun BaseUrl(): String?

        // GetAttendanceList
        @JvmStatic
        external fun GetAttendanceList(): String?
        @JvmStatic
        external fun GetAttendanceListQueryParams(): String?
    }
}