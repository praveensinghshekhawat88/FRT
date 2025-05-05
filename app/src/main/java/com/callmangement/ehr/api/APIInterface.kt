package com.callmangement.ehr.api

import com.callmangement.ehr.models.AttStatusRoot
import com.callmangement.ehr.models.DashbordRoot
import com.callmangement.ehr.models.ModelAllDealersByBlock
import com.callmangement.ehr.models.ModelAttendanceListing
import com.callmangement.ehr.models.ModelCampSchedule
import com.callmangement.ehr.models.ModelCreateACamp
import com.callmangement.ehr.models.ModelCreateASurveyForm
import com.callmangement.ehr.models.ModelDeleteACamp
import com.callmangement.ehr.models.ModelDeleteASurveyForm
import com.callmangement.ehr.models.ModelDistrict
import com.callmangement.ehr.models.ModelEditASurveyForm
import com.callmangement.ehr.models.ModelGetCampDocList
import com.callmangement.ehr.models.ModelGetSurveyFormDocList
import com.callmangement.ehr.models.ModelLogin
import com.callmangement.ehr.models.ModelLogout
import com.callmangement.ehr.models.ModelMarkAttendance
import com.callmangement.ehr.models.ModelMobileVersion
import com.callmangement.ehr.models.ModelOrganiseACamp
import com.callmangement.ehr.models.ModelSEUser
import com.callmangement.ehr.models.ModelSurveyFormListing
import com.callmangement.ehr.models.ModelTehsil
import com.callmangement.ehr.models.ModelUploadCampDailyReport
import com.callmangement.ehr.models.ModelUploadSurveyFormReport
import com.callmangement.ehr.models.UpdateAttendanceStatusRoot
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.QueryMap


interface APIInterface {
    @POST("{full_api_path}")
    fun callLoginApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelLogin?>?

    @POST("{full_api_path}")
    fun callLogoutApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelLogout?>?


    @POST("{full_api_path}")
    fun callCheckVersionApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelMobileVersion?>?

    @GET("{full_api_path}")
    fun callDistrictListApi(
        @Path(
            value = "full_api_path",
            encoded = true
        ) full_api_path: String?
    ): Call<ModelDistrict?>?

    @POST("{full_api_path}")
    fun callTehsilListApiByDistict(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelTehsil?>?

    @POST("{full_api_path}")
    fun callSEUserListApiByDistict(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: MutableMap<String?, String?>?
    ): Call<ModelSEUser?>?

    @POST("{full_api_path}")
    fun callCreateACampApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelCreateACamp?>?

    @POST("{full_api_path}")
    fun callCampListApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelCampSchedule?>?

    @POST("{full_api_path}")
    fun callOrganiseACampApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelOrganiseACamp?>?

    @POST("{full_api_path}")
    fun callDeleteACampApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelDeleteACamp?>?

    @POST("{full_api_path}")
    fun callGetDealersByBlockApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelAllDealersByBlock?>?

    @POST("{full_api_path}")
    fun callGetCampDocListApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelGetCampDocList?>?

    @Multipart
    @POST("{full_api_path}")
    fun callUploadCampDailyReportImagesApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @Part("UserID") UserID: RequestBody?,
        @Part("TrainingId") TrainingId: RequestBody?,
        @Part("TrainingNo") TrainingNo: RequestBody?,
        @Part("FlagTypeId") FlagTypeId: RequestBody?,
        @Part campDocumentsParts: Array<MultipartBody.Part?>?
    ): Call<ModelUploadCampDailyReport?>?

    @POST("{full_api_path}")
    fun callCreateSurveyFormApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelCreateASurveyForm?>?

    @POST("{full_api_path}")
    fun callEditSurveyFormApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelEditASurveyForm?>?

    @POST("{full_api_path}")
    fun callSurveyFormListApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelSurveyFormListing?>?

    @POST("{full_api_path}")
    fun callDeleteASurveyFormApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelDeleteASurveyForm?>?

    @Multipart
    @POST("{full_api_path}")
    fun callUploadSurveyFormReportImagesApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @Part("UserID") UserID: RequestBody?,
        @Part("ServeyFormId") ServeyFormId: RequestBody?,
        @Part("TicketNo") TicketNo: RequestBody?,
        @Part campDocumentsParts: Array<MultipartBody.Part?>?
    ): Call<ModelUploadSurveyFormReport?>?

    @POST("{full_api_path}")
    fun callGetSurveyFormDocListApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: Map<String?, String?>?
    ): Call<ModelGetSurveyFormDocList?>?

    @POST("{full_api_path}")
    fun callAttendanceListApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @QueryMap params: MutableMap<String?, String?>?
    ): Call<ModelAttendanceListing?>?


    @Multipart
    @POST("{full_api_path}")
    fun callMarkAttendanceApi(
        @Path(value = "full_api_path", encoded = true) full_api_path: String?,
        @Part("UserId") UserId: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part("Address") Address: RequestBody?,
        @Part("AddressLocPin") AddressLocPin: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<ModelMarkAttendance?>?

    @FormUrlEncoded
    @POST("ehr/dashboard")
    fun dashboardApi(
        @Field("UserId") UserId: String?,
        @Field("Month") Month: String?,
        @Field("Year") Year: String?,
        @Field("ToUserId") ToUserId: String?
    ): Call<DashbordRoot?>?

    @FormUrlEncoded
    @POST("attendance/AttendanceStatus")
    fun GetAttStatus(@Field("UserId") UserID: String?): Call<AttStatusRoot?>?

    @FormUrlEncoded
    @POST("attendance/UpdateAttandanceStatus")
    fun UpdateAttStatus(
        @Field("AttendanceId") AttendanceId: String?,
        @Field("UserId") UserId: String?,
        @Field("ToUserId") ToUserId: String?,
        @Field("AttendanceStatusId") AttendanceStatusId: String?,
        @Field("Remark") Remark: String?
    ): Call<UpdateAttendanceStatusRoot?>?
}
