package com.callmangement.EHR.api;



import com.callmangement.EHR.models.AttStatusRoot;
import com.callmangement.EHR.models.DashbordRoot;
import com.callmangement.EHR.models.ModelAllDealersByBlock;
import com.callmangement.EHR.models.ModelAttendanceListing;
import com.callmangement.EHR.models.ModelCampSchedule;
import com.callmangement.EHR.models.ModelCreateACamp;
import com.callmangement.EHR.models.ModelCreateASurveyForm;
import com.callmangement.EHR.models.ModelDeleteACamp;
import com.callmangement.EHR.models.ModelDeleteASurveyForm;
import com.callmangement.EHR.models.ModelDistrict;
import com.callmangement.EHR.models.ModelEditASurveyForm;
import com.callmangement.EHR.models.ModelGetCampDocList;
import com.callmangement.EHR.models.ModelGetSurveyFormDocList;
import com.callmangement.EHR.models.ModelLogin;
import com.callmangement.EHR.models.ModelLogout;
import com.callmangement.EHR.models.ModelMarkAttendance;
import com.callmangement.EHR.models.ModelMobileVersion;
import com.callmangement.EHR.models.ModelOrganiseACamp;
import com.callmangement.EHR.models.ModelSEUser;
import com.callmangement.EHR.models.ModelSurveyFormListing;
import com.callmangement.EHR.models.ModelTehsil;
import com.callmangement.EHR.models.ModelUploadCampDailyReport;
import com.callmangement.EHR.models.ModelUploadSurveyFormReport;
import com.callmangement.EHR.models.UpdateAttendanceStatusRoot;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface APIInterface {

    @POST("{full_api_path}")
    Call<ModelLogin> callLoginApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                  @QueryMap Map<String,String> params);
    @POST("{full_api_path}")
    Call<ModelLogout> callLogoutApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                    @QueryMap Map<String,String> params);


    @POST("{full_api_path}")
    Call<ModelMobileVersion> callCheckVersionApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                 @QueryMap Map<String,String> params);

    @GET("{full_api_path}")
    Call<ModelDistrict> callDistrictListApi(@Path(value = "full_api_path", encoded = true) String full_api_path);

    @POST("{full_api_path}")
    Call<ModelTehsil> callTehsilListApiByDistict(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                 @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelSEUser> callSEUserListApiByDistict(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                 @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelCreateACamp> callCreateACampApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                              @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelCampSchedule> callCampListApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                            @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelOrganiseACamp> callOrganiseACampApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                  @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelDeleteACamp> callDeleteACampApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                              @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelAllDealersByBlock> callGetDealersByBlockApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                          @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelGetCampDocList> callGetCampDocListApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                    @QueryMap Map<String,String> params);

    @Multipart
    @POST("{full_api_path}")
    Call<ModelUploadCampDailyReport> callUploadCampDailyReportImagesApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                                        @Part("UserID") RequestBody UserID,
                                                                        @Part("TrainingId") RequestBody TrainingId,
                                                                        @Part("TrainingNo") RequestBody TrainingNo,
                                                                        @Part("FlagTypeId") RequestBody FlagTypeId,
                                                                        @Part MultipartBody.Part[] campDocumentsParts);

    @POST("{full_api_path}")
    Call<ModelCreateASurveyForm> callCreateSurveyFormApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                         @QueryMap Map<String,String> params);
    @POST("{full_api_path}")
    Call<ModelEditASurveyForm> callEditSurveyFormApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                     @QueryMap Map<String,String> params);
    @POST("{full_api_path}")
    Call<ModelSurveyFormListing> callSurveyFormListApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                       @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelDeleteASurveyForm> callDeleteASurveyFormApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                          @QueryMap Map<String,String> params);

    @Multipart
    @POST("{full_api_path}")
    Call<ModelUploadSurveyFormReport> callUploadSurveyFormReportImagesApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                                          @Part("UserID") RequestBody UserID,
                                                                          @Part("ServeyFormId") RequestBody ServeyFormId,
                                                                          @Part("TicketNo") RequestBody TicketNo,
                                                                          @Part MultipartBody.Part[] campDocumentsParts);

    @POST("{full_api_path}")
    Call<ModelGetSurveyFormDocList> callGetSurveyFormDocListApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                                @QueryMap Map<String,String> params);

    @POST("{full_api_path}")
    Call<ModelAttendanceListing> callAttendanceListApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                       @QueryMap Map<String,String> params);


    @Multipart
    @POST("{full_api_path}")
    Call<ModelMarkAttendance> callMarkAttendanceApi(@Path(value = "full_api_path", encoded = true) String full_api_path,
                                                    @Part("UserId") RequestBody UserId,
                                                    @Part("Latitude") RequestBody Latitude,
                                                    @Part("Longitude") RequestBody Longitude,
                                                    @Part("Address") RequestBody Address,
                                                    @Part("AddressLocPin") RequestBody AddressLocPin,
                                                    @Part MultipartBody.Part image);
    @FormUrlEncoded
    @POST("ehr/dashboard")
    Call<DashbordRoot> dashboardApi(@Field("UserId") String UserId,
                                    @Field("Month") String Month,
                                    @Field("Year") String Year,
                                    @Field("ToUserId") String ToUserId
                                    );
    @FormUrlEncoded
    @POST("attendance/AttendanceStatus")
    Call<AttStatusRoot> GetAttStatus(@Field("UserId") String UserID);
    @FormUrlEncoded
    @POST("attendance/UpdateAttandanceStatus")
    Call<UpdateAttendanceStatusRoot> UpdateAttStatus(@Field("AttendanceId") String AttendanceId,
                                                     @Field("UserId") String UserId,
                                                     @Field("ToUserId") String ToUserId,
                                                     @Field("AttendanceStatusId") String AttendanceStatusId,
                                                     @Field("Remark") String Remark
                                                     );
}
