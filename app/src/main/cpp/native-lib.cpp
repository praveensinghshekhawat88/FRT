#include <jni.h>
#include <string>

//bool IS_TEST_MODE_ON = false;  //live
bool IS_TEST_MODE_ON = true;  //staging


extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_BaseUrl(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "https://rajepds.com/";
    if(IS_TEST_MODE_ON) {
        //  hello = "https://rajepds.com/CallManagement/";
        hello = "http://198.38.88.124/CallManagement/";
    }
    return env->NewStringUTF(hello.c_str());
}

//Login
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_Login(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "ehr/LogIn";
    if (IS_TEST_MODE_ON) {
        hello = "ehr/LogIn";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_LoginQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "emailId,password,deviceId";
    return env->NewStringUTF(hello.c_str());
}

//Logout
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_Logout(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "rest/logOutApp";
    if (IS_TEST_MODE_ON) {
        hello = "rest/logOutApp";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_LogoutQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "userId,emailId";
    return env->NewStringUTF(hello.c_str());
}

//CheckVersion
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_CheckVersion(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "ehr/getEhrAppVersion";
    if (IS_TEST_MODE_ON) {
        hello = "ehr/getEhrAppVersion";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_CheckVersionQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "version_code";
    return env->NewStringUTF(hello.c_str());
}

//DistictList
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_DistictList(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "rest/apiGetDistictList";
    if (IS_TEST_MODE_ON) {
        hello = "rest/apiGetDistictList";
    }
    return env->NewStringUTF(hello.c_str());
}

//TehsilListByDistict
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_TehsilListByDistict(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "rest/apiGetTehsilByDistict";
    if (IS_TEST_MODE_ON) {
        hello = "rest/apiGetTehsilByDistict";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_TehsilListByDistictQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "districtId";
    return env->NewStringUTF(hello.c_str());
}

//SEUserListByDistict
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_SEUserListByDistict(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "stock/getSEUserList";
    if (IS_TEST_MODE_ON) {
        hello = "stock/getSEUserList";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_SEUserListByDistictQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "districtId";
    return env->NewStringUTF(hello.c_str());
}

//GetCampList
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetCampList(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "camp/getCampList";

    if (IS_TEST_MODE_ON) {
        hello = "camp/getCampList";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetCampListQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,DistrictID,TehsilID,TrainingNo,StartedDate,EndDate,TStatusId";
    return env->NewStringUTF(hello.c_str());
}

//CreateCamp
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_CreateCamp(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "camp/save";
    if (IS_TEST_MODE_ON) {
        hello = "camp/save";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_CreateCampQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,DistrictID,TehsilID,Description,StartedDate,EndDate,Address";
    return env->NewStringUTF(hello.c_str());
}

//OrganiseACamp
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_OrganiseACamp(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "camp/updateCampStatus";
    if (IS_TEST_MODE_ON) {
        hello = "camp/updateCampStatus";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_OrganiseACampQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,TrainingId,TrainingNo,TStatusId";
    return env->NewStringUTF(hello.c_str());
}

//GetDealersByBlock
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetDealersByBlock(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "camp/getDealersByBlock";
    if (IS_TEST_MODE_ON) {
        hello = "camp/getDealersByBlock";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetDealersByBlockQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,DistrictID,TehsilID,BlockId";
    return env->NewStringUTF(hello.c_str());
}

//UploadDocumentOfDailyWorkReport
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_UploadDocumentOfDailyWorkReport(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "camp/uploadDocument";
    if (IS_TEST_MODE_ON) {
        hello = "camp/uploadDocument";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_UploadDocumentOfDailyWorkReportQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,TrainingId,TrainingNo,CampDocuments";
    return env->NewStringUTF(hello.c_str());
}

//GetCampDocList
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetCampDocList(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "camp/getCampDocList";
    if (IS_TEST_MODE_ON) {
        hello = "camp/getCampDocList";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetCampDocListQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,TrainingId";
    return env->NewStringUTF(hello.c_str());
}

//DeleteACamp
extern "C" JNIEXPORT jstring JNICALL


Java_com_callmangement_ehr_ehrActivities_BaseActivity_DeleteACamp(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "camp/deleteCamp";
    if (IS_TEST_MODE_ON) {
        hello = "camp/deleteCamp";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_DeleteACampQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,TrainingId,TrainingNo";
    return env->NewStringUTF(hello.c_str());
}


//CreateSurveyForm
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_CreateSurveyForm(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "survey/save";
    if (IS_TEST_MODE_ON) {
        hello = "survey/save";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_CreateSurveyFormQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,Bill_ChallanNo,Address,PointOfContact,BillRemark,MobileNumber,InstallationDateStr,TypeOfCallId,ItemDetail,PurchaseOrderDtl,InstalledMachineSpecification,Accessesory,InstallationDone,Customer_Remark,CustomerName,EngineerName";
    return env->NewStringUTF(hello.c_str());
}

//EditSurveyForm
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_EditSurveyForm(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "survey/update";
    if (IS_TEST_MODE_ON) {
        hello = "survey/update";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_EditSurveyFormQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,ServeyFormId,TicketNo,Bill_ChallanNo,Address,PointOfContact,BillRemark,MobileNumber,InstallationDateStr,TypeOfCallId,ItemDetail,PurchaseOrderDtl,InstalledMachineSpecification,Accessesory,InstallationDone,Customer_Remark,CustomerName,EngineerName";
    return env->NewStringUTF(hello.c_str());
}

//GetSurveyFormList
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetSurveyFormList(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "survey/surveyFormList";
    if (IS_TEST_MODE_ON) {
        hello = "survey/surveyFormList";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetSurveyFormListQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,ServeyFormId,TicketNo,StartDate,EndDate,StatusId";
    return env->NewStringUTF(hello.c_str());
}

//DeleteASurveyForm
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_DeleteASurveyForm(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "survey/deleteSurveyForm";
    if (IS_TEST_MODE_ON) {
        hello = "survey/deleteSurveyForm";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_DeleteASurveyFormQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,ServeyFormId,TicketNo";
    return env->NewStringUTF(hello.c_str());
}

//UploadDocumentOfSurveyFormReport
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_UploadDocumentOfSurveyFormReport(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "survey/uploadSurveyForm";
    if (IS_TEST_MODE_ON) {
        hello = "survey/uploadSurveyForm";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_UploadDocumentOfSurveyFormReportQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,ServeyFormId,TicketNo,CampDocuments";
    return env->NewStringUTF(hello.c_str());
}

//GetSurveyFormDocList
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetSurveyFormDocList(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "survey/serveyFormDocList";
    if (IS_TEST_MODE_ON) {
        hello = "survey/serveyFormDocList";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetSurveyFormDocListQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserID,ServeyFormId";
    return env->NewStringUTF(hello.c_str());
}

//GetSurveyFormList
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetAttendanceList(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "attendance/attendanceList";
    if (IS_TEST_MODE_ON) {
        hello = "attendance/attendanceList";
    }
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_GetAttendanceListQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserId,DistrictId,StartDate,EndDate,IsPagination,PageNo,PageSize";
    return env->NewStringUTF(hello.c_str());
}

//MArkAttendance
extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_MArkAttendance(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "attendance/save";
    if (IS_TEST_MODE_ON) {
        hello = "attendance/save";
    }
    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_callmangement_ehr_ehrActivities_BaseActivity_MArkAttendanceQueryParams(
        JNIEnv* env,
        jobject) {
    std::string hello = "UserId,Latitude,Longitude,Address,image";
    return env->NewStringUTF(hello.c_str());
}
