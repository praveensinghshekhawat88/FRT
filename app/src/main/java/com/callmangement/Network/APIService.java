package com.callmangement.Network;

import com.callmangement.EHR.models.FeedbackDetailByFpsRoot;
import com.callmangement.EHR.models.FeedbackbyDCListRoot;
import com.callmangement.EHR.models.SaveFeedbackbyDCRoot;
import com.callmangement.model.ModelResponse;
import com.callmangement.model.weighingDeliveryDetail.weighingDelieryRoot;
import com.callmangement.model.attendance.ModelAddLocationList;
import com.callmangement.model.attendance.ModelAttendance;
import com.callmangement.model.attendance.ModelAttendanceList;
import com.callmangement.model.complaints.ModelChallanUploadComplaint;
import com.callmangement.model.complaints.ModelComplaintsCount;
import com.callmangement.model.district.ModelDistrict;
import com.callmangement.model.ModelMobileVersion;
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaint;
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWise;
import com.callmangement.model.inventrory.ModelDispatchInvoice;
import com.callmangement.model.inventrory.ModelDisputeParts;
import com.callmangement.model.inventrory.ModelInventoryResponse;
import com.callmangement.model.inventrory.ModelParts;
import com.callmangement.model.inventrory.ModelSEUsers;
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails;
import com.callmangement.model.logout.ModelLogout;
import com.callmangement.model.reports.ModelSLAReport;
import com.callmangement.model.reports.ModelSLAReportDetails;
import com.callmangement.model.reset_device.ModelResetDevice;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.complaints.ModelComplaint;
import com.callmangement.model.complaints.ModelResolveComplaint;
import com.callmangement.model.login.ModelLogin;
import com.callmangement.model.training_schedule.ModelCreateTrainingSchedule;
import com.callmangement.model.training_schedule.ModelTrainingSchedule;
import com.callmangement.model.training_schedule.ModelUpdateTrainingSchedule;
import com.callmangement.ui.biometric_delivery.model.BiometricDashboardResponse;
import com.callmangement.ui.biometric_delivery.model.DetailsByFPSForSensorRoot;
import com.callmangement.ui.biometric_delivery.model.DeviceCodeByFPSResponse;
import com.callmangement.ui.biometric_delivery.model.SaveBiometricDeliverResponse;
import com.callmangement.ui.biometric_delivery.model.SensorDistributionDetailsListResp;
import com.callmangement.ui.biometric_delivery.model.SensorSummaryResponse;
import com.callmangement.ui.biometric_delivery.model.UpdateDeviceTypeToChangeFSensorResp;
import com.callmangement.ui.closed_guard_delivery.model.ClosedGuardDeliveryListResponse;
import com.callmangement.ui.errors.model.GetErrorImagesRoot;
import com.callmangement.ui.errors.model.GetErrorTypesRoot;
import com.callmangement.ui.errors.model.GetPosDeviceErrorsRoot;
import com.callmangement.ui.errors.model.GetRemarkRoot;
import com.callmangement.ui.errors.model.SaveErroeReqRoot;
import com.callmangement.ui.errors.model.UpdateErroeReqRoot;
import com.callmangement.ui.errors.model.UpdateErrorStatusRoot;
import com.callmangement.ui.ins_weighing_scale.model.Count.CountRoot;
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeightInsRoot;
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledRoot;
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledDetailedRoot;
import com.callmangement.ui.ins_weighing_scale.model.SaveInstall.SaveRoot;
import com.callmangement.ui.ins_weighing_scale.model.challan.challanRoot;
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w;
import com.callmangement.ui.ins_weighing_scale.model.fps.DetailByFpsRoot;
import com.callmangement.ui.iris_derivery_installation.Model.CheckIrisSerialNoResponse;
import com.callmangement.ui.iris_derivery_installation.Model.InstalledDetailedResponse;
import com.callmangement.ui.iris_derivery_installation.Model.IrisDashboardResponse;
import com.callmangement.ui.iris_derivery_installation.Model.IrisDeliveryListResponse;
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp;
import com.callmangement.ui.iris_derivery_installation.Model.ReplacementTypesResponse;
import com.callmangement.ui.iris_derivery_installation.Model.SaveIRISDeliverResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIService {
    @FormUrlEncoded
    @POST("rest/getUserLoginDetails_V20")
    Call<ModelLogin> login(@Field("emailId") String email,
                           @Field("password") String password,
                           @Field("deviceId") String deviceId,
                           @Field("deviceTocken") String deviceTocken);

    @FormUrlEncoded
    @POST("rest/logOutApp")
    Call<ModelLogout> logOutApp(@Field("userId") String userId,
                                @Field("emailId") String emailId);

    // login api with any user
    /*@FormUrlEncoded
    @POST("rest/getUserDataLoginAnyUser_V20")
    Call<ModelLogin> login(@Field("emailId") String email,
                           @Field("password") String password,
                           @Field("deviceId") String deviceId,
                           @Field("deviceTocken") String deviceTocken);*/

    // logout api with any user
    /*@FormUrlEncoded
    @POST("rest/logOutAppAnyUser")
    Call<ModelLogout> logOutApp(@Field("userId") String userId,
                                @Field("emailId") String emailId);*/

    @FormUrlEncoded
    @POST("rest/getComplaintListDateWise")
    Call<ModelComplaint> complaint_list(@Field("userId") String userId,
                                        @Field("fromDate") String fromDate,
                                        @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("Report/getSLAReportCount")
    Call<ModelSLAReport> sla_report_list(@Field("userId") String userId,
                                         @Field("fromDate") String fromDate,
                                         @Field("toDate") String toDate,
                                         @Field("ResolveInDays") Integer ResolveInDays,
                                         @Field("DistrictId") String DistrictId);

    @FormUrlEncoded
    @POST("Report/getSLAReportCountDetail")
    Call<ModelSLAReportDetails> getSLAReportCountDetail(@Field("userId") String userId,
                                                        @Field("fromDate") String fromDate,
                                                        @Field("toDate") String toDate,
                                                        @Field("ResolveInDays") Integer ResolveInDays,
                                                        @Field("DistrictId") String DistrictId);

    @Multipart
    @POST("rest/resolveApiComplaint_V8")
    Call<ModelResolveComplaint> resolveComplaint(@Part("userId") RequestBody userId,
                                                 @Part("IsPhysicalDamage") RequestBody IsPhysicalDamage,
                                                 @Part("seRemark") RequestBody seRemark,
                                                 @Part("complaintRegNo") RequestBody complaintRegNo,
                                                 @Part("getStatus") RequestBody getStatus,
                                                 @Part("replacePartName") RequestBody replacePartName,
                                                 @Part("caurierDetails") RequestBody caurierDetails,
                                                 @Part("ChallanNo") RequestBody ChallanNo,
                                                 @Part("seRemarkDate") RequestBody seRemarkDate,
                                                 @Part("ReplacedPartsIds") RequestBody ReplacedPartsIds,
                                                 @Part MultipartBody.Part se_image,
                                                 @Part MultipartBody.Part damageApprovalLetter);

    @Multipart
    @POST("challan/UploadChallan")
    Call<ModelResolveComplaint> challanUpload(@Part("userId") RequestBody userId,
                                              @Part("ComplainId") RequestBody IsPhysicalDamage,
                                              @Part("ComplainRegNo") RequestBody seRemark,
                                              @Part("ChallanNo") RequestBody complaintRegNo,
                                              @Part MultipartBody.Part chllanImage);

    @FormUrlEncoded
    @POST("rest/apiGetTehsilByDistict")
    Call<ModelTehsil> apiGetTehsilByDistict(@Field("districtId") String districtId);

    @GET("rest/apiGetDistictList")
    Call<ModelDistrict> apiGetDistictList();

    @FormUrlEncoded
    @POST("rest/getMobileAppVersion")
    Call<ModelMobileVersion> getMobileAppVersion(@Field("version_code") int version_code);

    @FormUrlEncoded
    @POST("rest/getComplaintsCountDateDistrictIdWise")
    Call<ModelComplaintsCount> getComplaintsCountDateDistrictIdWise(@Field("userId") String userId,
                                                                    @Field("DistrictId") String DistrictId,
                                                                    @Field("fromDate") String fromDate,
                                                                    @Field("toDate") String toDate);



    //


    @FormUrlEncoded
    @POST("rest/getComplaintListDistStatusDateWise")
    Call<ModelComplaint> getComplaintListDistStatusDateWise(@Field("userId") String userId,
                                                            @Field("districtId") String districtId,
                                                            @Field("complainStatusId") String complainStatusId,
                                                            @Field("fromDate") String fromDate,
                                                            @Field("toDate") String toDate,
                                                            @Field("IsPagination") String IsPagination,
                                                            @Field("PageNo") String PageNo,
                                                            @Field("PageSize") String PageSize,
                                                            @Field("FPSCode") String FPSCode);







    @FormUrlEncoded
    @POST("rest/getComplaintListDistStatusDateWise")
    Call<ModelComplaint> getComplaintListDistStatusDateWiseNew(@Field("userId") String userId,
                                                               @Field("districtId") String districtId,
                                                               @Field("complainStatusId") String complainStatusId,
                                                               @Field("fromDate") String fromDate,
                                                               @Field("toDate") String toDate,
                                                               @Field("IsPagination") String IsPagination,
                                                               @Field("PageNo") String PageNo,
                                                               @Field("PageSize") String PageSize,
                                                               @Field("FPSCode") String FPSCode);

    @FormUrlEncoded
    @POST("challan/ChallanUploadPendingComplaintList")
    Call<ModelChallanUploadComplaint> getModelChallanUploadComplaintListData(@Field("userId") String userId,
                                                                             @Field("districtId") String districtId,
                                                                             @Field("ComplainRegNo") String ComplainRegNo,
                                                                             @Field("FPSCode") String FPSCode,
                                                                             @Field("fromDate") String fromDate,
                                                                             @Field("toDate") String toDate,
                                                                             @Field("IsPagination") String IsPagination,
                                                                             @Field("PageNo") String PageNo,
                                                                             @Field("PageSize") String PageSize);



    ///








    @FormUrlEncoded
    @POST("challan/ChallanEditComplaintList")
    Call<ModelChallanUploadComplaint> getModelEditChallanComplaintListData(@Field("userId") String userId,
                                                                           @Field("districtId") String districtId,
                                                                           @Field("ComplainRegNo") String ComplainRegNo,
                                                                           @Field("FPSCode") String FPSCode);
    /*attendance and location apis*/

    @FormUrlEncoded
    @POST("addAttendanceRecord")
    Call<ModelAttendance> addAttendanceRecord(@Field("User_Id") String User_Id,
                                              @Field("Latitude") String Latitude,
                                              @Field("Longitude") String Longitude,
                                              @Field("Address") String Address,
                                              @Field("Punch_In_Date") String Punch_In_Date,
                                              @Field("Punch_In_Time") String Punch_In_Time,
                                              @Field("Latitude_Out") String Latitude_Out,
                                              @Field("Longitude_Out") String Longitude_Out,
                                              @Field("Address_Out") String Address_Out,
                                              @Field("Punch_Out_Date") String Punch_Out_Date,
                                              @Field("Punch_Out_Time") String Punch_Out_Time);

    @FormUrlEncoded
    @POST("getCheckedAttendance")
    Call<ModelAttendance> getCheckedAttendance(@Field("User_Id") String User_Id,
                                               @Field("Punch_In_Date") String Punch_In_Date);

    @FormUrlEncoded
    @POST("getAttendanceRecord")
    Call<ModelAttendanceList> getAttendanceRecord(@Field("User_Id") String User_Id,
                                                  @Field("District_Id") String District_Id,
                                                  @Field("DateFrom") String DateFrom,
                                                  @Field("DateTo") String DateTo);

    @FormUrlEncoded
    @POST("Location/getLocationRecord")
    Call<ModelAddLocationList> getLocationRecord(@Field("User_Id") String User_Id,
                                                 @Field("District_Id") String District_Id);

    @POST("Location/saveSELocations")
    Call<ResponseBody> saveSELocations(@Query("userId") String userId,
                                       @Query("districtId") String districtId,
                                       @Query("deviceId") String deviceId,
                                       @Body RequestBody requestBody);
    /*attendance and location apis*/

    /*inventory apis*/
    @GET("stock/getPartsList")
    Call<ModelParts> getPartsList();

    @FormUrlEncoded
    @POST("stock/getSEUserList")
    Call<ModelSEUsers> getSEUserList(@Field("districtId") String districtId);

    /*@POST("stock/savePartsDispatchDetails")
    Call<ResponseBody> savePartsDispatchDetails(@Query("InvoiceId") String InvoiceId,
                                                @Query("DispatchFrom") String DispatchFrom,
                                                @Query("DispatchTo") String DispatchTo,
                                                @Query("DistrictId") String DistrictId,
                                                @Query("DispatchChallanImage") String DispatchChallanImage,
                                                @Query("DispatcherRemarks") String DispatcherRemarks,
                                                @Query("StockStatusId") String StockStatusId,
                                                @Body RequestBody requestBody);*/

    @Multipart
    @POST("stock/savePartsDispatchDetails")
    Call<ModelSavePartsDispatchDetails> savePartsDispatchDetails(@Part("InvoiceId") RequestBody InvoiceId,
                                                                 @Part("DispatchFrom") RequestBody DispatchFrom,
                                                                 @Part("DispatchTo") RequestBody DispatchTo,
                                                                 @Part("DistrictId") RequestBody DistrictId,
                                                                 @Part("DispatchChallanImage") RequestBody DispatchChallanImage,
                                                                 @Part("DispatcherRemarks") RequestBody DispatcherRemarks,
                                                                 @Part("StockStatusId") RequestBody StockStatusId,
                                                                 @Part("courierName") RequestBody courierName,
                                                                 @Part("courierTrackingNo") RequestBody courierTrackingNo,
                                                                 @Part("dispatchItems") RequestBody dispatchItems,
                                                                 @Part MultipartBody.Part challanImage,
                                                                 @Part MultipartBody.Part partsImage1,
                                                                 @Part MultipartBody.Part partsImage2);

    @FormUrlEncoded
    @POST("stock/submitDispatchParts")
    Call<ModelSavePartsDispatchDetails> submitDispatchParts(@Field("UserId") String UserId,
                                                            @Field("InvoiceId") String InvoiceId,
                                                            @Field("DispatcherRemarks") String DispatcherRemarks);

    @FormUrlEncoded
    @POST("stock/getPartsDispatcherInvoices")
    Call<ModelDispatchInvoice> getPartsDispatcherInvoices(@Field("InvoiceId") String InvoiceId,
                                                          @Field("UserId") String UserId,
                                                          @Field("DistrictId") String DistrictId,
                                                          @Field("fromDate") String fromDate,
                                                          @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("stock/getDispatchInvoiceParts")
    Call<ModelDispatchInvoice> getDispatchInvoiceParts(@Field("InvoiceId") String InvoiceId,
                                                       @Field("UserId") String UserId,
                                                       @Field("DispatchId") String DispatchId);

    @FormUrlEncoded
    @POST("stock/getPartsReciverInvoices")
    Call<ModelDispatchInvoice> getPartsReciverInvoices(@Field("InvoiceId") String invoiceId,
                                                       @Field("UserId") String userId,
                                                       @Field("DistrictId") String districtId,
                                                       @Field("fromDate") String fromDate,
                                                       @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("stock/getReciveInvoiceParts")
    Call<ResponseBody> getReciveInvoiceParts(@Field("InvoiceId") String invoiceId,
                                             @Field("UserId") String userId,
                                             @Field("DispatchId") String dispatchedId);

    /*@POST("stock/saveRecivePartsDetails")
    Call<ResponseBody> savePartsReceive(@Query("InvoiceId") String InvoiceId,
                                        @Query("UserId") String userId,
                                        @Query("IsReceived") boolean isReceived,
                                        @Query("ReciverRemarks") String remark,
                                        @Body RequestBody requestBody);*/

    @Multipart
    @POST("stock/saveRecivePartsDetails")
    Call<ResponseBody> savePartsReceive(@Part("InvoiceId") RequestBody InvoiceId,
                                        @Part("UserId") RequestBody userId,
                                        @Part("IsReceived") RequestBody isReceived,
                                        @Part("ReciverRemarks") RequestBody remark,
                                        @Part("recivedItemsReq") RequestBody recivedItemsReq,
                                        @Part MultipartBody.Part image);

    @GET("mStock/getAvlinStockPartsList")
    Call<ModelParts> getAvailableStockPartsList(@Query("UserId") String UserId,
                                                @Query("ItemId") String ItemId);

    @GET("mStock/getPartsCurrentStockList")
    Call<ModelParts> getPartsCurrentStockList(@Query("UserId") String UserId,
                                              @Query("ItemId") String ItemId);

    @FormUrlEncoded
    @POST("mStock/getDisputePartsList")
    Call<ModelDisputeParts> getDisputePartsList(@Field("UserId") String UserId,
                                                @Field("InvoiceId") String InvoiceId);

    @POST("mStock/updatePartsStock")
    Call<ResponseBody> updatePartsStock(@Query("InvoiceId") String InvoiceId,
                                        @Query("UserId") String UserId,
                                        @Query("ItemStockStatusId") String ItemStockStatusId,
                                        @Query("Remarks") String Remarks,
                                        @Body RequestBody requestBody);

    @GET("stock/getSE_AvlStockPartsList")
    Call<ModelParts> getSE_AvlStockPartsList(@Query("UserId") String UserId,
                                             @Query("ItemId") String ItemId);

    @GET("stock/getAllSE_AvlStockPartsList")
    Call<ModelParts> getAllSE_AvlStockPartsList(@Query("UserId") String UserId,
                                                @Query("ItemId") String ItemId);

    @POST("stock/deleteInvoice")
    Call<ModelInventoryResponse> deleteInvoice(@Query("UserId") String UserId,
                                               @Query("InvoiceId") String InvoiceId,
                                               @Query("DeleteRemarks") String DeleteRemarks);
    /*inventory apis*/

    /*training schedule*/
    @FormUrlEncoded
    @POST("training/saveTraining")
    Call<ModelCreateTrainingSchedule> saveTraining(@Field("UserID") String UserID,
                                                   @Field("DistrictID") String DistrictID,
                                                   @Field("TehsilID") String TehsilID,
                                                   @Field("StartedDate") String StartedDate,
                                                   @Field("EndDate") String EndDate,
                                                   @Field("Address") String Address,
                                                   @Field("Description") String Description,
                                                   @Field("Title") String Title);

    @FormUrlEncoded
    @POST("training/getTrainigList")
    Call<ModelTrainingSchedule> getTrainigList(@Field("UserID") String UserID,
                                               @Field("DistrictID") String DistrictID,
                                               @Field("TehsilID") String TehsilID,
                                               @Field("TrainingNo") String TrainingNo,
                                               @Field("StartedDate") String StartedDate,
                                               @Field("EndDate") String EndDate);

    @FormUrlEncoded
    @POST("training/updateTraining")
    Call<ModelUpdateTrainingSchedule> updateTraining(@Field("UserID") String UserID,
                                                     @Field("DistrictID") String DistrictID,
                                                     @Field("TehsilID") String TehsilID,
                                                     @Field("TrainingId") String TrainingId,
                                                     @Field("StartedDate") String StartedDate,
                                                     @Field("EndDate") String EndDate,
                                                     @Field("TrainingNo") String TrainingNo,
                                                     @Field("Address") String Address,
                                                     @Field("Description") String Description,
                                                     @Field("Title") String Title);
    /*training schedule*/

    @GET("fpsCompHis/getFPSListDisTehWise?")
    Call<ModelFPSDistTehWise> getFPSListDisTehWise(@Query("FPSCode") String FPSCode,
                                                   @Query("districtId") String districtId,
                                                   @Query("TehsilId") String TehsilId);

    @GET("fpsCompHis/getFPSComplainsList?")
    Call<ModelFPSComplaint> getFPSComplainsList(@Query("FPSCode") String FPSCode);

    @FormUrlEncoded
    @POST("rest/resetDeviceIdTokenID")
    Call<ModelResetDevice> resetDeviceIdTokenID(@Field("seUserId") String seUserId,
                                                @Field("DistrictId") String DistrictId);

    @FormUrlEncoded
    @POST("Location/updateDToken")
    Call<ResponseBody> updateDToken(@Field("device_id") String device_id,
                                    @Field("device_token") String device_token);

    /*pos distribution report apis*/
    @FormUrlEncoded
    @POST("disb/GetOldMachineDetailsByFPSAPI")
    Call<ResponseBody> GetOldMachineDetailsByFPSAPI(@Field("districtId") String districtId,
                                                    @Field("userId") String userId,
                                                    @Field("FPSCode") String FPSCode);

    @FormUrlEncoded
    @POST("disb/GetNewMachineDetailsByOrdNoAPI")
    Call<ResponseBody> GetNewMachineDetailsByOrdNoAPI(@Field("districtId") String districtId,
                                                      @Field("userId") String userId,
                                                      @Field("newMachineOrderNo") String newMachineOrderNo);

    @GET("disb/GetDeviceVendorListAPI")
    Call<ResponseBody> GetDeviceVendorListAPI();

    @FormUrlEncoded
    @POST("disb/getPosDistributionListAPI")
    Call<ResponseBody> getPosDistributionListAPI(@Field("districtId") String districtId,
                                                 @Field("userId") String userId,
                                                 @Field("tranId") String tranId,
                                                 @Field("FPSCode") String FPSCode,
                                                 @Field("ticketNo") String ticketNo,
                                                 @Field("fromDate") String fromDate,
                                                 @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("disb/getPosDistributionListSEAPI")
    Call<ResponseBody> getPosDistributionListSEAPI(@Field("districtId") String districtId,
                                                   @Field("userId") String userId,
                                                   @Field("tranId") String tranId,
                                                   @Field("FPSCode") String FPSCode,
                                                   @Field("ticketNo") String ticketNo,
                                                   @Field("fromDate") String fromDate,
                                                   @Field("toDate") String toDate);

    @FormUrlEncoded
    @POST("disb/getPosDistributionDetailByTIDAPI")
    Call<ResponseBody> getPosDistributionDetailByTIDAPI(@Field("userId") String userId,
                                                        @Field("tranId") String tranId);

    @Multipart
    @POST("disb/saveNewPosDistributionAPI")
    Call<ResponseBody> saveNewPosDistributionAPI(@Part("userId") RequestBody userId,
                                                 @Part("tranId") RequestBody tranId,
                                                 @Part("districtId") RequestBody districtId,
                                                 @Part("EquipmentModelId") RequestBody EquipmentModelId,
                                                 @Part("oldMachineVenderid") RequestBody oldMachineVenderid,
                                                 @Part("newMachineVenderid") RequestBody newMachineVenderid,
                                                 @Part("newMachineOrderNo") RequestBody newMachineOrderNo,
                                                 @Part("FPSCode") RequestBody FPSCode,
                                                 @Part("ticketNo") RequestBody ticketNo,
                                                 @Part("tranDateStr") RequestBody tranDateStr,
                                                 @Part("oldMachine_SerialNo") RequestBody oldMachine_SerialNo,
                                                 @Part("oldMachine_Biometric_SeriallNo") RequestBody oldMachine_Biometric_SeriallNo,
                                                 @Part("oldMachineWorkingStatus") RequestBody oldMachineWorkingStatus,
                                                 @Part("accessoriesProvided") RequestBody accessoriesProvided,
                                                 @Part("remarks") RequestBody remarks,
                                                 @Part("dealerName") RequestBody dealerName,
                                                 @Part("mobileNo") RequestBody mobileNo,
                                                 @Part("blockName") RequestBody blockName,
                                                 @Part("ipAddress") RequestBody ipAddress,
                                                 @Part("WhetherOldMachProvdedForReplacmnt") RequestBody WhetherOldMachProvdedForReplacmnt,
                                                 @Part("isCompleteWithSatisfactorily") RequestBody isCompleteWithSatisfactorily,
                                                 @Part MultipartBody.Part dealerImage);

    //gagandeep
    @Multipart
    @POST("posError/saveErroeReq")
    Call<SaveErroeReqRoot> saveErroeReqApi(@Part("UserID") RequestBody UserID,
                                           @Part("FPSCode") RequestBody FPSCode,
                                           @Part("DeviceCode") RequestBody DeviceCode,
                                           @Part("DealerMobileNo") RequestBody DealerMobileNo,
                                           @Part("ErrorTypeId") RequestBody ErrorTypeId,
                                           @Part("ErrorStatusId") RequestBody ErrorStatusId,
                                           @Part("Remark") RequestBody Remark,
                                           @Part MultipartBody.Part[] campDocumentsParts);

    @Multipart
    @POST("posError/UpdateErroeReq")
    Call<UpdateErroeReqRoot> UpdateErroeReqApi(@Part("ErrorId") RequestBody ErrorId,
                                               @Part("UserID") RequestBody UserID,
                                               @Part("FPSCode") RequestBody FPSCode,
                                               @Part("DeviceCode") RequestBody DeviceCode,
                                               @Part("DealerMobileNo") RequestBody DealerMobileNo,
                                               @Part("ErrorTypeId") RequestBody ErrorTypeId,
                                               @Part("ErrorStatusId") RequestBody ErrorStatusId,
                                               @Part("Remark") RequestBody Remark,
                                               @Part MultipartBody.Part[] campDocumentsParts);

    //  String JSONURL = "https://demonuts.com/Demonuts/JsonTest/Tennis/";

    @GET("json_parsing.php")
    Call<String> getJSONString();

//gagandeep

    @POST("disb/uploadDistributerPhoto")
    Call<ResponseBody> uploadDistributerPhoto(@Body RequestBody requestBody);

    @FormUrlEncoded
    @POST("disb/getPosDistributionListAPIAllUpld")
    Call<ResponseBody> getPosDistributionListAPIAllUpld(@Field("districtId") String districtId,
                                                        @Field("userId") String userId,
                                                        @Field("tranId") String tranId,
                                                        @Field("FPSCode") String FPSCode,
                                                        @Field("ticketNo") String ticketNo,
                                                        @Field("fromDate") String fromDate,
                                                        @Field("toDate") String toDate);
    /*pos distribution report apis*/

    @FormUrlEncoded
    @POST("Report/RepaetFPSComplainsOnSC")
    Call<ResponseBody> getFpsRepeatOnServiceCenter(@Field("userId") String userId,
                                                   @Field("fromDate") String fromDate,
                                                   @Field("toDate") String toDate,
                                                   @Field("DistrictId") String DistrictId,
                                                   @Field("FPSCode") String FPSCode);

    @FormUrlEncoded
    @POST("Report/RepaetFPSComplainsOnSCList")
    Call<ResponseBody> RepaetFPSComplainsOnSCList(@Field("userId") String userId,
                                                  @Field("fromDate") String fromDate,
                                                  @Field("toDate") String toDate,
                                                  @Field("DistrictId") String DistrictId,
                                                  @Field("FPSCode") String FPSCode);

    @FormUrlEncoded
    @POST("rest/AcceptBySE")
    Call<ModelResponse> AcceptBySE(@Field("userId") String userId,
                                   @Field("complaintRegNo") String complaintRegNo);

    @FormUrlEncoded
    @POST("stock/saveReturnPartsFromUserSide")
    Call<ModelResponse> saveReturnPartsFromUserSide(@Field("UserId") String UserId,
                                                    @Field("SeUserId") String SeUserId,
                                                    @Field("dispatchItems") String dispatchItems);

    @Multipart
    @POST("expense/saveExpenses")
    Call<ResponseBody> saveExpenses(@Part("UserId") RequestBody UserId,
                                    @Part("TotalExAmount") RequestBody TotalExAmount,
                                    @Part("Remark") RequestBody Remark,
                                    @Part("DocketNo") RequestBody DocketNo,
                                    @Part("CourierName") RequestBody CourierName,
                                    @Part MultipartBody.Part ExChallanCopy);

    @FormUrlEncoded
    @POST("expense/getExpensesList")
    Call<ResponseBody> getExpensesList(@Field("UserId") String UserId,
                                       @Field("ExpenseStatusID") String ExpenseStatusID,
                                       @Field("DistrictId") String DistrictId,
                                       @Field("DateFrom") String DateFrom,
                                       @Field("DateTo") String DateTo);

    @FormUrlEncoded
    @POST("expense/completeExpenses")
    Call<ResponseBody> completeExpenses(@Field("User" + "Id") String UserId,
                                        @Field("ExpenseId") String ExpenseId);

    @FormUrlEncoded
    @POST("posError/GetErrorTypes")
    Call<GetErrorTypesRoot> GetErrorTypes(@Field("UserID") String UserID,
                                          @Field("ErrorTypeId") String ErrorTypeId);

    @FormUrlEncoded
    @POST("posError/GetPOSDeviceErrors")
    Call<GetPosDeviceErrorsRoot> GetPOSDeviceErrors(@Field("ErrorId") String ErrorId,
                                                    @Field("UserID") String UserID,
                                                    @Field("FPSCode") String FPSCode,
                                                    @Field("DistrictId") String DistrictId,
                                                    @Field("ErrorRegNo") String ErrorRegNo,
                                                    @Field("ErrorStatusId") String ErrorStatusId,
                                                    @Field("DateFrom") String DateFrom,
                                                    @Field("DateTo") String DateTo,
                                                    @Field("ErrorTypeId") String ErrorTypeId);

    @FormUrlEncoded
    @POST("posError/GetErrorImages")
    Call<GetErrorImagesRoot> GetErrorImages(@Field("ErrorId") String ErrorId,
                                            @Field("UserID") String UserID,
                                            @Field("ErrorRegNo") String ErrorRegNo);

    @FormUrlEncoded
    @POST("posError/UpdateErrorStatus")
    Call<UpdateErrorStatusRoot> UpdateErrorStatus(@Field("ErrorId") String ErrorId,
                                                  @Field("UserID") String UserID,
                                                  @Field("ErrorRegNo") String ErrorRegNo,
                                                  @Field("ErrorStatusId") String ErrorStatusId,
                                                  @Field("Remark") String Remark);

    @FormUrlEncoded
    @POST("posError/GetErrorRemarks")
    Call<GetRemarkRoot> GetErrorRemarks(@Field("ErrorId") String ErrorId,
                                        @Field("UserID") String UserID,
                                        @Field("ErrorRegNo") String ErrorRegNo);

//Ins Weinghing scale

    @FormUrlEncoded
    @POST("WeigIris/getIrisWeighInstallationCnt")
    Call<CountRoot> appWeightCountApi(@Field("UserID") String UserID,
                                      @Field("FPSCode") String FPSCode,
                                      @Field("DistrictId") String DistrictId,
                                      @Field("TicketNo") String TicketNo,
                                      @Field("StatusId") String StatusId,
                                      @Field("DateFrom") String DateFrom,
                                      @Field("DateTo") String DateTo);

    @FormUrlEncoded
    @POST("WeigIrisInstall/WeinghIRISDeliveryInstallCnt")
    Call<CountRoot> appWeightCountApiiris(@Field("UserID") String UserID,
                                          @Field("DistrictId") String DistrictId,
                                          @Field("DeviceTypeId") String DeviceTypeId,
                                          @Field("DeviceModelId") String DeviceModelId,
                                          @Field("DateFrom") String DateFrom,
                                          @Field("DateTo") String DateTo,
                                          @Field("ToUserId") String ToUserId);

    @FormUrlEncoded
    @POST("WeigIrisInstall/WeinghIRISDeliveryInstallCnt")
    Call<IrisDashboardResponse> getIrisDashboardData(@Field("UserID") String UserID,
                                                     @Field("DistrictId") String DistrictId,
                                                     @Field("DeviceTypeId") String DeviceTypeId,
                                                     @Field("DeviceModelId") String DeviceModelId,
                                                     @Field("DateFrom") String DateFrom,
                                                     @Field("DateTo") String DateTo,
                                                     @Field("ToUserId") String ToUserId);

    @FormUrlEncoded
    @POST("SensorInstall/SensorDisbnDashbordCnt")
    Call<BiometricDashboardResponse> getBiometricSensorDashboardData(@Field("UserID") String UserID,
                                                                     @Field("DistrictId") String DistrictId,
                                                                     @Field("FPSCode") String FPSCode,
                                                                     @Field("DateFrom") String DateFrom,
                                                                     @Field("DateTo") String DateTo,
                                                                     @Field("ToUserId") String ToUserId);

    @GET("rest/apiGetDistictList")
    Call<ModelDistrict_w> apiGetDistictList_w();

    @FormUrlEncoded
    @POST("WeigIris/GetCustomerDetailsByFPS")
    Call<DetailByFpsRoot> apiDetailsByFPS(@Field("FPSCode") String FPSCode,
                                          @Field("UserID") String UserID,
                                          @Field("DistrictId") String DistrictId);

    @FormUrlEncoded
    @POST("CloseGuard/GetCustomerDetailsByFPSForCloseGuard")
    Call<DetailByFpsRoot> getCustomerDetailsByFPSForCloseGuard(@Field("FPSCode") String FPSCode,
                                                               @Field("UserID") String UserID,
                                                               @Field("DistrictId") String DistrictId);

    @FormUrlEncoded
    @POST("SensorInstall/GetCustomerDetailsByFPSForSensorDistribution")
    Call<DetailsByFPSForSensorRoot> getCustomerDetailsByFPSForSensorDistribution(@Field("FPSCode") String FPSCode,
                                                                                 @Field("UserID") String UserID,
                                                                                 @Field("DistrictId") String DistrictId);

    @FormUrlEncoded
    @POST("WeigIris/getIrisWeighInstallation")
    Call<WeightInsRoot> apiIrisWeighInstallation(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("TicketNo") String TicketNo,
            @Field("StatusId") String StatusId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo);

    @FormUrlEncoded
    @POST("SensorInstall/SensorDisbnSummaryCnt")
    Call<SensorSummaryResponse> getSensorDisbnSummaryCnt(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("ToUserId") String ToUserId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo);

    @FormUrlEncoded
    @POST("SensorInstall/SensorDistributionDetailsList")
    Call<SensorDistributionDetailsListResp> getSensorDistributionDetailsList(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("DeviceCode") String DeviceCode,
            @Field("BiometricSerialNo") String BiometricSerialNo,
            @Field("FilterTypeId") String FilterTypeId,
            @Field("IsPagination") String IsPagination,
            @Field("PageNo") String PageNo,
            @Field("PageSize") String PageSize,
            @Field("ToUserId") String ToUserId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo);

    @Multipart
    @POST("WeigIrisInstall/saveInstallationReq")
    Call<SaveRoot> saveInstallationRe(@Part("UserID") RequestBody UserID,
                                      @Part("DeliveryId") RequestBody DeliveryId,
                                      @Part("IrisScannerModelId") RequestBody IrisScannerModelId,
                                      @Part("IrisScannerSerialNo") RequestBody IrisScannerSerialNo,
                                      @Part("ShopAddress") RequestBody ShopAddress,
                                      @Part("Latitude") RequestBody Latitude,
                                      @Part("Longitude") RequestBody Longitude,
                                      @Part("Remarks") RequestBody Remarks,
                                      @Part("DeliveredOn") RequestBody DeliveredOn,
                                      @Part("FPSCode") RequestBody FPSCode,
                                      @Part("DealerMobileNo") RequestBody DealerMobileNo,
                                      @Part MultipartBody.Part InstallationWeinghingImage,
                                      @Part MultipartBody.Part InstallationIRISScannerImage,
                                      @Part MultipartBody.Part InstallationDealerPhoto,
                                      @Part MultipartBody.Part InstallationDealerSignature,
                                      @Part MultipartBody.Part InstallationChallan);

    @Multipart
    @POST("WeigIrisInstall/saveIRISDeliverInstallationReq")
    Call<SaveIRISDeliverResponse> saveInstallationRq(@Part("UserID") RequestBody UserID,
                                                     @Part("FPSCode") RequestBody FPSCode,
                                                     @Part("DealerMobileNo") RequestBody DealerMobileNo,
                                                     @Part("IrisScannerModelId") RequestBody IrisScannerModelId,
                                                     @Part("IrisScannerSerialNo") RequestBody IrisScannerSerialNo,
                                                     @Part("ShopAddress") RequestBody ShopAddress,
                                                     @Part("Latitude") RequestBody Latitude,
                                                     @Part("Longitude") RequestBody Longitude,
                                                     @Part("Remarks") RequestBody Remark,
                                                     @Part("DeliveredOn") RequestBody DeliveredOn,
                                                     @Part MultipartBody.Part IRISSerialNoImage,
                                                     @Part MultipartBody.Part DealerImageWithIRIS,
                                                     @Part MultipartBody.Part DealerSignatureIRIS,
                                                     @Part MultipartBody.Part DealerPhotIdProof);

    @Multipart
    @POST("SensorInstall/saveSensorDistribution")
    Call<SaveBiometricDeliverResponse> saveSensorDistribution(@Part("UserID") RequestBody UserID,
                                                              @Part("TranId") RequestBody TranId,
                                                              @Part("DeviceCode") RequestBody DeviceCode,
                                                              @Part("BiometricSerialNo") RequestBody BiometricSerialNo,
                                                              @Part("FPSCode") RequestBody FPSCode,
                                                              @Part("DealerMobileNo") RequestBody DealerMobileNo,
                                                              @Part("ShopAddress") RequestBody ShopAddress,
                                                              @Part("Latitude") RequestBody Latitude,
                                                              @Part("Longitude") RequestBody Longitude,
                                                              @Part("Remarks") RequestBody Remark,
                                                              @Part("DeliveredOn") RequestBody DeliveredOn,
                                                              @Part MultipartBody.Part BiometricSerialNoImage,
                                                              @Part MultipartBody.Part DealerImageWithBiometric,
                                                              @Part MultipartBody.Part DealerSignature,
                                                              @Part MultipartBody.Part DealerPhotoIDProof);

    @Multipart
    @POST("WeigIrisReplacement/saveIRISReplacementReq")
    Call<SaveIRISDeliverResponse> saveIRISReplacementReqpr(@Part("UserID") RequestBody UserID,
                                                           @Part("DeliveryId") RequestBody DeliveryId,
                                                           @Part("FPSCode") RequestBody FPSCode,
                                                           @Part("DealerMobileNo") RequestBody DealerMobileNo,
                                                           @Part("IrisScannerModelId") RequestBody IrisScannerModelId,
                                                           @Part("IrisScannerSerialNo") RequestBody IrisScannerSerialNo,
                                                           @Part("ShopAddress") RequestBody ShopAddress,
                                                           @Part("Latitude") RequestBody Latitude,
                                                           @Part("Longitude") RequestBody Longitude,
                                                           @Part("Remarks") RequestBody Remark,
                                                           @Part("DeliveredOn") RequestBody DeliveredOn,
                                                           @Part("ReplaceTypeId") RequestBody ReplaceTypeId,
                                                           @Part MultipartBody.Part IRISSerialNoImage,
                                                           @Part MultipartBody.Part DealerImageWithIRIS,
                                                           @Part MultipartBody.Part DealerSignatureIRIS,
                                                           @Part MultipartBody.Part DealerPhotIdProof);

    // @Part MultipartBody.Part[] campDocumentsParts);

    @Multipart
    @POST("CloseGuard/saveIRISCloseGuardDelivery")
    Call<SaveIRISDeliverResponse> saveIRISCloseGuardDelivery(@Part("UserID") RequestBody UserID,
                                                             @Part("FPSCode") RequestBody FPSCode,
                                                             @Part("ShopAddress") RequestBody ShopAddress,
                                                             @Part("Latitude") RequestBody Latitude,
                                                             @Part("Longitude") RequestBody Longitude,
                                                             @Part MultipartBody.Part DealerImageWithIRIS,
                                                             @Part MultipartBody.Part DealerSignatureIRIS);

    @FormUrlEncoded
    @POST("WeigIris/IrisWeighDeliveryDtl")
    Call<weighingDelieryRoot> apiWeigDeliveryDetail(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("TicketNo") String TicketNo);

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisWeighInstallation")
    Call<InstalledRoot> IrisWeighInstallation(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("TicketNo") String TicketNo,
            @Field("StatusId") String StatusId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo,
            @Field("DeliveryId") String DeliveryId);

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisDeliveryInstallationL")
    Call<IrisDeliveryListResponse> IrisDeliveryInstallationL(
            @Field("UserID") String UserID,
            @Field("ToUserId") String ToUserId,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("TicketNo") String TicketNo,
            @Field("BlockId") String BlockId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo,
            @Field("DeliveryId") String DeliveryId,
            @Field("DSerialNo") String DSerialNo,
            @Field("DeviceTypeId") String DeviceTypeId,
            @Field("DeviceModelId") String DeviceModelId,
            @Field("IsPagination") String IsPagination,
            @Field("PageNo") String PageNo,
            @Field("PageSize") String PageSize);

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisDeliveryInstallationLForReplacement")
    Call<IrisDeliveryListResponse> IrisDeliveryInstallationLForReplacement(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("TicketNo") String TicketNo,
            @Field("BlockId") String BlockId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo,
            @Field("DeliveryId") String DeliveryId,
            @Field("DSerialNo") String DSerialNo,
            @Field("DeviceTypeId") String DeviceTypeId,
            @Field("DeviceModelId") String DeviceModelId,
            @Field("IsPagination") String IsPagination,
            @Field("PageNo") String PageNo,
            @Field("PageSize") String PageSize);

    @FormUrlEncoded
    @POST("CloseGuard/IrisCloseGuardDeliveryList")
    Call<ClosedGuardDeliveryListResponse> getCloseGuardDeliveryList(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("ToUserId") String ToUserId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo,
            @Field("IsPagination") String DeliveryId,
            @Field("PageNo") String DSerialNo,
            @Field("PageSize") String DeviceTypeId);

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisWeighInstallationDtl")
    Call<InstalledDetailedRoot> IrisWeighInstallationDtl(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("TicketNo") String TicketNo,
            @Field("DeliveryId") String DeliveryId);

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisDeliveryInstallationDtl")
    Call<InstalledDetailedResponse> IrisDeliveryInstallationDtl(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("TicketNo") String TicketNo,
            @Field("DeliveryId") String DeliveryId);

    @FormUrlEncoded
    @POST("WeigIris/DownloadAPIChallanPDF")
    Call<challanRoot> DownloadAPIChallanPDF(
            @Field("UserID") String UserID,
            @Field("FPSCode") String FPSCode,
            @Field("DeviceTypeId") String DeviceTypeId,
            @Field("ChallanTypeId") String ChallanTypeId,
            @Field("DistrictId") String DistrictId);

    @FormUrlEncoded
    @POST("WeigIrisInstall/VerifyDeliveryInst")
    Call<ModelLogout> verifyDelivery(
            @Field("UserID") String userId,
            @Field("DeliveryId") String DeliveryId,
            @Field("FPSCode") String FPSCode,
            @Field("DeviceSerialNo") String DeviceSerialNo);

    @Multipart
    @POST("WeigIris/updateDelivery")
    Call<SaveRoot> updateDelivery(
            @Part("UserID") RequestBody UserID,
            @Part("DeliveryId") RequestBody DeliveryId,
            @Part("WeighingScaleSerialNo") RequestBody FPSCode,
            @Part("Remarks") RequestBody DeviceSerialNo,
            @Part MultipartBody.Part WeighingImage,
            @Part MultipartBody.Part DealerImage,
            @Part MultipartBody.Part ChallanImage);

    @FormUrlEncoded
    @POST("WeigIrisInstall/checkIRISSerialNoDeliveredOrNot")
    Call<CheckIrisSerialNoResponse> checkIrisSerialNo(@Field("UserID") String UserID,
                                                      @Field("IrisScannerModelId") String IrisScannerModelId,
                                                      @Field("IrisScannerSerialNo") String IrisScannerSerialNo);

    @FormUrlEncoded
    @POST("SensorInstall/getL0DeviceCodeByFPS")
    Call<DeviceCodeByFPSResponse> getL0DeviceCodeByFPS(@Field("UserID") String UserID,
                                                       @Field("FPSCode") String FPSCode);

    @FormUrlEncoded
    @POST("SensorInstall/UpdateDeviceTypeToChangeFSensor")
    Call<UpdateDeviceTypeToChangeFSensorResp> updateDeviceTypeToChangeFSensor(@Field("UserID") String UserID,
                                                                              @Field("DeviceCode") String DeviceCode);

    @GET("WeigIrisReplacement/getReplacementTypes")
    Call<ReplacementTypesResponse> getReplacementTypes();

    @FormUrlEncoded
    @POST("WeigIris/getCombinedIRISWehingDetailReport")
    Call<IrisInstallationPendingListResp> getCombinedIRISWehingDetailReport(
            @Field("UserID") String UserID,
            @Field("DistrictId") String DistrictId,
            @Field("DeviceTypeId") String DeviceTypeId,
            @Field("DeviceModelId") String DeviceModelId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo,
            @Field("ToUserId") String ToUserId);

    @Multipart
    @POST("WeigIrisReplacement/saveWeigReplacementReq")
    Call<SaveIRISDeliverResponse> saveWeigReplacementReq(@Part("UserID") RequestBody UserID,
                                                         @Part("DeliveryId") RequestBody DeliveryId,
                                                         @Part("FPSCode") RequestBody FPSCode,
                                                         @Part("DealerMobileNo") RequestBody DealerMobileNo,
                                                         @Part("DeviceModelId") RequestBody DeviceModelId,
                                                         @Part("SerialNo") RequestBody SerialNo,
                                                         @Part("ShopAddress") RequestBody ShopAddress,
                                                         @Part("Latitude") RequestBody Latitude,
                                                         @Part("Longitude") RequestBody Longitude,
                                                         @Part("Remarks") RequestBody Remark,
                                                         @Part("DeliveredOn") RequestBody DeliveredOn,
                                                         @Part MultipartBody.Part DeviceSerialNoImage,
                                                         @Part MultipartBody.Part DealerImageWithDevice,
                                                         @Part MultipartBody.Part DealerSignature,
                                                         @Part MultipartBody.Part DealerPhotoIdProof);

//Feedback

    @FormUrlEncoded
    @POST("feedback/GetCustomerDetailsByFPSToFeedback")
    Call<FeedbackDetailByFpsRoot> apiDetailsByFPSToFeedback(@Field("FPSCode") String FPSCode,
                                                            @Field("UserID") String UserID,
                                                            @Field("DistrictId") String DistrictId);

    @Multipart
    @POST("feedback/saveVisitFeedbackbyDC")
    Call<SaveFeedbackbyDCRoot> saveVisitFeedbackbyDC(@Part("UserID") RequestBody UserID,
                                                     @Part("FPSCode") RequestBody FPSCode,
                                                     @Part("ShopAddress") RequestBody ShopAddress,
                                                     @Part("Latitude") RequestBody Latitude,
                                                     @Part("Longitude") RequestBody Longitude,
                                                     @Part("Remarks") RequestBody Remark,
                                                     @Part("LogInTime") RequestBody LogInTime,
                                                     @Part MultipartBody.Part SelpiheWithFPSNoAndDealer,
                                                     @Part MultipartBody.Part DCVisitingChallan,
                                                     @Part MultipartBody.Part DCVisitingFeedbackDealerSignature);

    @FormUrlEncoded
    @POST("feedback/getVisitFeedbackbyDCList")
    Call<FeedbackbyDCListRoot> getVisitFeedbackbyDCList(
            @Field("UserID") String UserID,
            @Field("FeedBackId") String FeedBackId,
            @Field("FPSCode") String FPSCode,
            @Field("DistrictId") String DistrictId,
            @Field("DateFrom") String DateFrom,
            @Field("DateTo") String DateTo,
            @Field("ToUserId") String ToUserId);

}



