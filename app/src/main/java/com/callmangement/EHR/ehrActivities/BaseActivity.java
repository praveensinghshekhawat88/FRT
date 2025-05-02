package com.callmangement.EHR.ehrActivities;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    // Used to load the 'e_hr' library on application startup.
    static {
        System.loadLibrary("e_hr");
    }
     public static native String BaseUrl();
    // Login
    public native String Login();
    public native String LoginQueryParams();

    // Logout
    public native String Logout();
    public native String LogoutQueryParams();

    // CheckVersion
    public native String CheckVersion();
    public native String CheckVersionQueryParams();

    // DistictList
    public native String DistictList();

    // TehsilListByDistict
    public native String TehsilListByDistict();
    public native String TehsilListByDistictQueryParams();

    // SEUserListByDistict
    public native String SEUserListByDistict();
    public native String SEUserListByDistictQueryParams();

    // CreateCamp
    public native String CreateCamp();
    public native String CreateCampQueryParams();

    // GetCampList
    public native String GetCampList();
    public native String GetCampListQueryParams();

    // OrganiseACamp
    public native String OrganiseACamp();
    public native String OrganiseACampQueryParams();

    // GetDealersByBlock
    public native String GetDealersByBlock();
    public native String GetDealersByBlockQueryParams();

    // UploadDocumentOfDailyWorkReport
    public native String UploadDocumentOfDailyWorkReport();
    public native String UploadDocumentOfDailyWorkReportQueryParams();

    // GetCampDocList
    public native String GetCampDocList();
    public native String GetCampDocListQueryParams();

    // DeleteACamp
    public native String DeleteACamp();
    public native String DeleteACampQueryParams();

    // CreateSurveyForm
    public native String CreateSurveyForm();
    public native String CreateSurveyFormQueryParams();

    // EditSurveyForm
    public native String EditSurveyForm();
    public native String EditSurveyFormQueryParams();

    // GetSurveyFormList
    public native String GetSurveyFormList();
    public native String GetSurveyFormListQueryParams();

    // DeleteASurveyForm
    public native String DeleteASurveyForm();
    public native String DeleteASurveyFormQueryParams();

    // UploadDocumentOfSurveyFormReport
    public native String UploadDocumentOfSurveyFormReport();
    public native String UploadDocumentOfSurveyFormReportQueryParams();

    // GetSurveyFormDocList
    public native String GetSurveyFormDocList();
    public native String GetSurveyFormDocListQueryParams();

    // GetAttendanceList
    public static native String GetAttendanceList();
    public static native String GetAttendanceListQueryParams();

    // MArkAttendance
    public native String MArkAttendance();
    public native String MArkAttendanceQueryParams();
}