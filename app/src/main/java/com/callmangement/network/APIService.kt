package com.callmangement.network

import com.callmangement.EHR.models.FeedbackDetailByFpsRoot
import com.callmangement.EHR.models.FeedbackbyDCListRoot
import com.callmangement.EHR.models.SaveFeedbackbyDCRoot
import com.callmangement.model.ModelMobileVersion
import com.callmangement.model.ModelResponse
import com.callmangement.model.attendance.ModelAddLocationList
import com.callmangement.model.attendance.ModelAttendance
import com.callmangement.model.attendance.ModelAttendanceList
import com.callmangement.model.complaints.ModelChallanUploadComplaint
import com.callmangement.model.complaints.ModelComplaint
import com.callmangement.model.complaints.ModelComplaintsCount
import com.callmangement.model.complaints.ModelResolveComplaint
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaint
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWise
import com.callmangement.model.inventrory.ModelDispatchInvoice
import com.callmangement.model.inventrory.ModelDisputeParts
import com.callmangement.model.inventrory.ModelInventoryResponse
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelSEUsers
import com.callmangement.model.inventrory.ModelSavePartsDispatchDetails
import com.callmangement.model.login.ModelLogin
import com.callmangement.model.logout.ModelLogout
import com.callmangement.model.reports.ModelSLAReport
import com.callmangement.model.reports.ModelSLAReportDetails
import com.callmangement.model.reset_device.ModelResetDevice
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.model.training_schedule.ModelCreateTrainingSchedule
import com.callmangement.model.training_schedule.ModelTrainingSchedule
import com.callmangement.model.training_schedule.ModelUpdateTrainingSchedule
import com.callmangement.model.weighingDeliveryDetail.weighingDelieryRoot
import com.callmangement.ui.biometric_delivery.model.BiometricDashboardResponse
import com.callmangement.ui.biometric_delivery.model.DetailsByFPSForSensorRoot
import com.callmangement.ui.biometric_delivery.model.DeviceCodeByFPSResponse
import com.callmangement.ui.biometric_delivery.model.SaveBiometricDeliverResponse
import com.callmangement.ui.biometric_delivery.model.SensorDistributionDetailsListResp
import com.callmangement.ui.biometric_delivery.model.SensorSummaryResponse
import com.callmangement.ui.biometric_delivery.model.UpdateDeviceTypeToChangeFSensorResp
import com.callmangement.ui.closed_guard_delivery.model.ClosedGuardDeliveryListResponse
import com.callmangement.ui.errors.model.GetErrorImagesRoot
import com.callmangement.ui.errors.model.GetErrorTypesRoot
import com.callmangement.ui.errors.model.GetPosDeviceErrorsRoot
import com.callmangement.ui.errors.model.GetRemarkRoot
import com.callmangement.ui.errors.model.SaveErroeReqRoot
import com.callmangement.ui.errors.model.UpdateErroeReqRoot
import com.callmangement.ui.errors.model.UpdateErrorStatusRoot
import com.callmangement.ui.ins_weighing_scale.model.Count.CountRoot
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeightInsRoot
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledRoot
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledDetailedRoot
import com.callmangement.ui.ins_weighing_scale.model.SaveInstall.SaveRoot
import com.callmangement.ui.ins_weighing_scale.model.challan.challanRoot
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.ui.ins_weighing_scale.model.fps.DetailByFpsRoot
import com.callmangement.ui.iris_derivery_installation.Model.CheckIrisSerialNoResponse
import com.callmangement.ui.iris_derivery_installation.Model.InstalledDetailedResponse
import com.callmangement.ui.iris_derivery_installation.Model.IrisDashboardResponse
import com.callmangement.ui.iris_derivery_installation.Model.IrisDeliveryListResponse
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp
import com.callmangement.ui.iris_derivery_installation.Model.ReplacementTypesResponse
import com.callmangement.ui.iris_derivery_installation.Model.SaveIRISDeliverResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface APIService {
    @FormUrlEncoded
    @POST("rest/getUserLoginDetails_V20")
    fun login(
        @Field("emailId") email: String?,
        @Field("password") password: String?,
        @Field("deviceId") deviceId: String?,
        @Field("deviceTocken") deviceTocken: String?
    ): Call<ModelLogin?>?

    @FormUrlEncoded
    @POST("rest/logOutApp")
    fun logOutApp(
        @Field("userId") userId: String?,
        @Field("emailId") emailId: String?
    ): Call<ModelLogout?>?

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
    fun complaint_list(
        @Field("userId") userId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?
    ): Call<ModelComplaint?>?

    @FormUrlEncoded
    @POST("Report/getSLAReportCount")
    fun sla_report_list(
        @Field("userId") userId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?,
        @Field("ResolveInDays") ResolveInDays: Int?,
        @Field("DistrictId") DistrictId: String?
    ): Call<ModelSLAReport?>?

    @FormUrlEncoded
    @POST("Report/getSLAReportCountDetail")
    fun getSLAReportCountDetail(
        @Field("userId") userId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?,
        @Field("ResolveInDays") ResolveInDays: Int?,
        @Field("DistrictId") DistrictId: String?
    ): Call<ModelSLAReportDetails?>?

    @Multipart
    @POST("rest/resolveApiComplaint_V8")
    fun resolveComplaint(
        @Part("userId") userId: RequestBody?,
        @Part("IsPhysicalDamage") IsPhysicalDamage: RequestBody?,
        @Part("seRemark") seRemark: RequestBody?,
        @Part("complaintRegNo") complaintRegNo: RequestBody?,
        @Part("getStatus") getStatus: RequestBody?,
        @Part("replacePartName") replacePartName: RequestBody?,
        @Part("caurierDetails") caurierDetails: RequestBody?,
        @Part("ChallanNo") ChallanNo: RequestBody?,
        @Part("seRemarkDate") seRemarkDate: RequestBody?,
        @Part("ReplacedPartsIds") ReplacedPartsIds: RequestBody?,
        @Part se_image: MultipartBody.Part?,
        @Part damageApprovalLetter: MultipartBody.Part?
    ): Call<ModelResolveComplaint?>?

    @Multipart
    @POST("challan/UploadChallan")
    fun challanUpload(
        @Part("userId") userId: RequestBody?,
        @Part("ComplainId") IsPhysicalDamage: RequestBody?,
        @Part("ComplainRegNo") seRemark: RequestBody?,
        @Part("ChallanNo") complaintRegNo: RequestBody?,
        @Part chllanImage: MultipartBody.Part?
    ): Call<ModelResolveComplaint?>?

    @FormUrlEncoded
    @POST("rest/apiGetTehsilByDistict")
    fun apiGetTehsilByDistict(@Field("districtId") districtId: String?): Call<ModelTehsil?>?

    @GET("rest/apiGetDistictList")
    fun apiGetDistictList(): Call<ModelDistrict?>?

    @FormUrlEncoded
    @POST("rest/getMobileAppVersion")
    fun getMobileAppVersion(@Field("version_code") version_code: Int): Call<ModelMobileVersion?>?

    @FormUrlEncoded
    @POST("rest/getComplaintsCountDateDistrictIdWise")
    fun getComplaintsCountDateDistrictIdWise(
        @Field("userId") userId: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?
    ): Call<ModelComplaintsCount?>?


    //
    @FormUrlEncoded
    @POST("rest/getComplaintListDistStatusDateWise")
    fun getComplaintListDistStatusDateWise(
        @Field("userId") userId: String?,
        @Field("districtId") districtId: String?,
        @Field("complainStatusId") complainStatusId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?,
        @Field("IsPagination") IsPagination: String?,
        @Field("PageNo") PageNo: String?,
        @Field("PageSize") PageSize: String?,
        @Field("FPSCode") FPSCode: String?
    ): Call<ModelComplaint?>?


    @FormUrlEncoded
    @POST("rest/getComplaintListDistStatusDateWise")
    fun getComplaintListDistStatusDateWiseNew(
        @Field("userId") userId: String?,
        @Field("districtId") districtId: String?,
        @Field("complainStatusId") complainStatusId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?,
        @Field("IsPagination") IsPagination: String?,
        @Field("PageNo") PageNo: String?,
        @Field("PageSize") PageSize: String?,
        @Field("FPSCode") FPSCode: String?
    ): Call<ModelComplaint?>?

    @FormUrlEncoded
    @POST("challan/ChallanUploadPendingComplaintList")
    fun getModelChallanUploadComplaintListData(
        @Field("userId") userId: String?,
        @Field("districtId") districtId: String?,
        @Field("ComplainRegNo") ComplainRegNo: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?,
        @Field("IsPagination") IsPagination: String?,
        @Field("PageNo") PageNo: String?,
        @Field("PageSize") PageSize: String?
    ): Call<ModelChallanUploadComplaint?>?


    ///
    @FormUrlEncoded
    @POST("challan/ChallanEditComplaintList")
    fun getModelEditChallanComplaintListData(
        @Field("userId") userId: String?,
        @Field("districtId") districtId: String?,
        @Field("ComplainRegNo") ComplainRegNo: String?,
        @Field("FPSCode") FPSCode: String?
    ): Call<ModelChallanUploadComplaint?>?

    /*attendance and location apis*/
    @FormUrlEncoded
    @POST("addAttendanceRecord")
    fun addAttendanceRecord(
        @Field("User_Id") User_Id: String?,
        @Field("Latitude") Latitude: String?,
        @Field("Longitude") Longitude: String?,
        @Field("Address") Address: String?,
        @Field("Punch_In_Date") Punch_In_Date: String?,
        @Field("Punch_In_Time") Punch_In_Time: String?,
        @Field("Latitude_Out") Latitude_Out: String?,
        @Field("Longitude_Out") Longitude_Out: String?,
        @Field("Address_Out") Address_Out: String?,
        @Field("Punch_Out_Date") Punch_Out_Date: String?,
        @Field("Punch_Out_Time") Punch_Out_Time: String?
    ): Call<ModelAttendance?>?

    @FormUrlEncoded
    @POST("getCheckedAttendance")
    fun getCheckedAttendance(
        @Field("User_Id") User_Id: String?,
        @Field("Punch_In_Date") Punch_In_Date: String?
    ): Call<ModelAttendance?>?

    @FormUrlEncoded
    @POST("getAttendanceRecord")
    fun getAttendanceRecord(
        @Field("User_Id") User_Id: String?,
        @Field("District_Id") District_Id: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?
    ): Call<ModelAttendanceList?>?

    @FormUrlEncoded
    @POST("Location/getLocationRecord")
    fun getLocationRecord(
        @Field("User_Id") User_Id: String?,
        @Field("District_Id") District_Id: String?
    ): Call<ModelAddLocationList?>?

    @POST("Location/saveSELocations")
    fun saveSELocations(
        @Query("userId") userId: String?,
        @Query("districtId") districtId: String?,
        @Query("deviceId") deviceId: String?,
        @Body requestBody: RequestBody?
    ): Call<ResponseBody?>?



    @FormUrlEncoded
    @POST("stock/getSEUserList")
    fun getSEUserList(@Field("districtId") districtId: String?): Call<ModelSEUsers?>?

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
    fun savePartsDispatchDetails(
        @Part("InvoiceId") InvoiceId: RequestBody?,
        @Part("DispatchFrom") DispatchFrom: RequestBody?,
        @Part("DispatchTo") DispatchTo: RequestBody?,
        @Part("DistrictId") DistrictId: RequestBody?,
        @Part("DispatchChallanImage") DispatchChallanImage: RequestBody?,
        @Part("DispatcherRemarks") DispatcherRemarks: RequestBody?,
        @Part("StockStatusId") StockStatusId: RequestBody?,
        @Part("courierName") courierName: RequestBody?,
        @Part("courierTrackingNo") courierTrackingNo: RequestBody?,
        @Part("dispatchItems") dispatchItems: RequestBody?,
        @Part challanImage: MultipartBody.Part?,
        @Part partsImage1: MultipartBody.Part?,
        @Part partsImage2: MultipartBody.Part?
    ): Call<ModelSavePartsDispatchDetails?>?

    @FormUrlEncoded
    @POST("stock/submitDispatchParts")
    fun submitDispatchParts(
        @Field("UserId") UserId: String?,
        @Field("InvoiceId") InvoiceId: String?,
        @Field("DispatcherRemarks") DispatcherRemarks: String?
    ): Call<ModelSavePartsDispatchDetails?>?

    @FormUrlEncoded
    @POST("stock/getPartsDispatcherInvoices")
    fun getPartsDispatcherInvoices(
        @Field("InvoiceId") InvoiceId: String?,
        @Field("UserId") UserId: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?
    ): Call<ModelDispatchInvoice?>?

    @FormUrlEncoded
    @POST("stock/getDispatchInvoiceParts")
    fun getDispatchInvoiceParts(
        @Field("InvoiceId") InvoiceId: String?,
        @Field("UserId") UserId: String?,
        @Field("DispatchId") DispatchId: String?
    ): Call<ModelDispatchInvoice?>?

    @FormUrlEncoded
    @POST("stock/getPartsReciverInvoices")
    fun getPartsReciverInvoices(
        @Field("InvoiceId") invoiceId: String?,
        @Field("UserId") userId: String?,
        @Field("DistrictId") districtId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?
    ): Call<ModelDispatchInvoice?>?

    @FormUrlEncoded
    @POST("stock/getReciveInvoiceParts")
    fun getReciveInvoiceParts(
        @Field("InvoiceId") invoiceId: String?,
        @Field("UserId") userId: String?,
        @Field("DispatchId") dispatchedId: String?
    ): Call<ResponseBody?>?

    /*@POST("stock/saveRecivePartsDetails")
    Call<ResponseBody> savePartsReceive(@Query("InvoiceId") String InvoiceId,
                                        @Query("UserId") String userId,
                                        @Query("IsReceived") boolean isReceived,
                                        @Query("ReciverRemarks") String remark,
                                        @Body RequestBody requestBody);*/
    @Multipart
    @POST("stock/saveRecivePartsDetails")
    fun savePartsReceive(
        @Part("InvoiceId") InvoiceId: RequestBody?,
        @Part("UserId") userId: RequestBody?,
        @Part("IsReceived") isReceived: RequestBody?,
        @Part("ReciverRemarks") remark: RequestBody?,
        @Part("recivedItemsReq") recivedItemsReq: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<ResponseBody?>?

    @GET("mStock/getAvlinStockPartsList")
    fun getAvailableStockPartsList(
        @Query("UserId") UserId: String?,
        @Query("ItemId") ItemId: String?
    ): Call<ModelParts?>?

    @GET("mStock/getPartsCurrentStockList")
    fun getPartsCurrentStockList(
        @Query("UserId") UserId: String?,
        @Query("ItemId") ItemId: String?
    ): Call<ModelParts?>?

    @FormUrlEncoded
    @POST("mStock/getDisputePartsList")
    fun getDisputePartsList(
        @Field("UserId") UserId: String?,
        @Field("InvoiceId") InvoiceId: String?
    ): Call<ModelDisputeParts?>?

    @POST("mStock/updatePartsStock")
    fun updatePartsStock(
        @Query("InvoiceId") InvoiceId: String?,
        @Query("UserId") UserId: String?,
        @Query("ItemStockStatusId") ItemStockStatusId: String?,
        @Query("Remarks") Remarks: String?,
        @Body requestBody: RequestBody?
    ): Call<ResponseBody?>?

    @GET("stock/getSE_AvlStockPartsList")
    fun getSE_AvlStockPartsList(
        @Query("UserId") UserId: String?,
        @Query("ItemId") ItemId: String?
    ): Call<ModelParts?>?

    @GET("stock/getAllSE_AvlStockPartsList")
    fun getAllSE_AvlStockPartsList(
        @Query("UserId") UserId: String?,
        @Query("ItemId") ItemId: String?
    ): Call<ModelParts?>?

    @POST("stock/deleteInvoice")
    fun deleteInvoice(
        @Query("UserId") UserId: String?,
        @Query("InvoiceId") InvoiceId: String?,
        @Query("DeleteRemarks") DeleteRemarks: String?
    ): Call<ModelInventoryResponse?>?

    /*inventory apis*/ /*training schedule*/
    @FormUrlEncoded
    @POST("training/saveTraining")
    fun saveTraining(
        @Field("UserID") UserID: String?,
        @Field("DistrictID") DistrictID: String?,
        @Field("TehsilID") TehsilID: String?,
        @Field("StartedDate") StartedDate: String?,
        @Field("EndDate") EndDate: String?,
        @Field("Address") Address: String?,
        @Field("Description") Description: String?,
        @Field("Title") Title: String?
    ): Call<ModelCreateTrainingSchedule?>?

    @FormUrlEncoded
    @POST("training/getTrainigList")
    fun getTrainigList(
        @Field("UserID") UserID: String?,
        @Field("DistrictID") DistrictID: String?,
        @Field("TehsilID") TehsilID: String?,
        @Field("TrainingNo") TrainingNo: String?,
        @Field("StartedDate") StartedDate: String?,
        @Field("EndDate") EndDate: String?
    ): Call<ModelTrainingSchedule?>?

    @FormUrlEncoded
    @POST("training/updateTraining")
    fun updateTraining(
        @Field("UserID") UserID: String?,
        @Field("DistrictID") DistrictID: String?,
        @Field("TehsilID") TehsilID: String?,
        @Field("TrainingId") TrainingId: String?,
        @Field("StartedDate") StartedDate: String?,
        @Field("EndDate") EndDate: String?,
        @Field("TrainingNo") TrainingNo: String?,
        @Field("Address") Address: String?,
        @Field("Description") Description: String?,
        @Field("Title") Title: String?
    ): Call<ModelUpdateTrainingSchedule?>?

    /*training schedule*/
    @GET("fpsCompHis/getFPSListDisTehWise?")
    fun getFPSListDisTehWise(
        @Query("FPSCode") FPSCode: String?,
        @Query("districtId") districtId: String?,
        @Query("TehsilId") TehsilId: String?
    ): Call<ModelFPSDistTehWise?>?

    @GET("fpsCompHis/getFPSComplainsList?")
    fun getFPSComplainsList(@Query("FPSCode") FPSCode: String?): Call<ModelFPSComplaint?>?

    @FormUrlEncoded
    @POST("rest/resetDeviceIdTokenID")
    fun resetDeviceIdTokenID(
        @Field("seUserId") seUserId: String?,
        @Field("DistrictId") DistrictId: String?
    ): Call<ModelResetDevice?>?

    @FormUrlEncoded
    @POST("Location/updateDToken")
    fun updateDToken(
        @Field("device_id") device_id: String?,
        @Field("device_token") device_token: String?
    ): Call<ResponseBody?>?

    /*pos distribution report apis*/
    @FormUrlEncoded
    @POST("disb/GetOldMachineDetailsByFPSAPI")
    fun GetOldMachineDetailsByFPSAPI(
        @Field("districtId") districtId: String?,
        @Field("userId") userId: String?,
        @Field("FPSCode") FPSCode: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("disb/GetNewMachineDetailsByOrdNoAPI")
    fun GetNewMachineDetailsByOrdNoAPI(
        @Field("districtId") districtId: String?,
        @Field("userId") userId: String?,
        @Field("newMachineOrderNo") newMachineOrderNo: String?
    ): Call<ResponseBody?>?

    @GET("disb/GetDeviceVendorListAPI")
    fun GetDeviceVendorListAPI(): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("disb/getPosDistributionListAPI")
    fun getPosDistributionListAPI(
        @Field("districtId") districtId: String?,
        @Field("userId") userId: String?,
        @Field("tranId") tranId: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("ticketNo") ticketNo: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("disb/getPosDistributionListSEAPI")
    fun getPosDistributionListSEAPI(
        @Field("districtId") districtId: String?,
        @Field("userId") userId: String?,
        @Field("tranId") tranId: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("ticketNo") ticketNo: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("disb/getPosDistributionDetailByTIDAPI")
    fun getPosDistributionDetailByTIDAPI(
        @Field("userId") userId: String?,
        @Field("tranId") tranId: String?
    ): Call<ResponseBody?>?

    @Multipart
    @POST("disb/saveNewPosDistributionAPI")
    fun saveNewPosDistributionAPI(
        @Part("userId") userId: RequestBody?,
        @Part("tranId") tranId: RequestBody?,
        @Part("districtId") districtId: RequestBody?,
        @Part("EquipmentModelId") EquipmentModelId: RequestBody?,
        @Part("oldMachineVenderid") oldMachineVenderid: RequestBody?,
        @Part("newMachineVenderid") newMachineVenderid: RequestBody?,
        @Part("newMachineOrderNo") newMachineOrderNo: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("ticketNo") ticketNo: RequestBody?,
        @Part("tranDateStr") tranDateStr: RequestBody?,
        @Part("oldMachine_SerialNo") oldMachine_SerialNo: RequestBody?,
        @Part("oldMachine_Biometric_SeriallNo") oldMachine_Biometric_SeriallNo: RequestBody?,
        @Part("oldMachineWorkingStatus") oldMachineWorkingStatus: RequestBody?,
        @Part("accessoriesProvided") accessoriesProvided: RequestBody?,
        @Part("remarks") remarks: RequestBody?,
        @Part("dealerName") dealerName: RequestBody?,
        @Part("mobileNo") mobileNo: RequestBody?,
        @Part("blockName") blockName: RequestBody?,
        @Part("ipAddress") ipAddress: RequestBody?,
        @Part("WhetherOldMachProvdedForReplacmnt") WhetherOldMachProvdedForReplacmnt: RequestBody?,
        @Part("isCompleteWithSatisfactorily") isCompleteWithSatisfactorily: RequestBody?,
        @Part dealerImage: MultipartBody.Part?
    ): Call<ResponseBody?>?

    //gagandeep
    @Multipart
    @POST("posError/saveErroeReq")
    fun saveErroeReqApi(
        @Part("UserID") UserID: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("DeviceCode") DeviceCode: RequestBody?,
        @Part("DealerMobileNo") DealerMobileNo: RequestBody?,
        @Part("ErrorTypeId") ErrorTypeId: RequestBody?,
        @Part("ErrorStatusId") ErrorStatusId: RequestBody?,
        @Part("Remark") Remark: RequestBody?,
        @Part campDocumentsParts: Array<MultipartBody.Part?>?
    ): Call<SaveErroeReqRoot?>?

    @Multipart
    @POST("posError/UpdateErroeReq")
    fun UpdateErroeReqApi(
        @Part("ErrorId") ErrorId: RequestBody?,
        @Part("UserID") UserID: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("DeviceCode") DeviceCode: RequestBody?,
        @Part("DealerMobileNo") DealerMobileNo: RequestBody?,
        @Part("ErrorTypeId") ErrorTypeId: RequestBody?,
        @Part("ErrorStatusId") ErrorStatusId: RequestBody?,
        @Part("Remark") Remark: RequestBody?,
        @Part campDocumentsParts: Array<MultipartBody.Part?>?
    ): Call<UpdateErroeReqRoot?>?

    @get:GET("json_parsing.php")
    val jSONString: Call<String?>?

    //gagandeep
    @POST("disb/uploadDistributerPhoto")
    fun uploadDistributerPhoto(@Body requestBody: RequestBody?): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("disb/getPosDistributionListAPIAllUpld")
    fun getPosDistributionListAPIAllUpld(
        @Field("districtId") districtId: String?,
        @Field("userId") userId: String?,
        @Field("tranId") tranId: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("ticketNo") ticketNo: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?
    ): Call<ResponseBody?>?

    /*pos distribution report apis*/
    @FormUrlEncoded
    @POST("Report/RepaetFPSComplainsOnSC")
    fun getFpsRepeatOnServiceCenter(
        @Field("userId") userId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("FPSCode") FPSCode: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("Report/RepaetFPSComplainsOnSCList")
    fun RepaetFPSComplainsOnSCList(
        @Field("userId") userId: String?,
        @Field("fromDate") fromDate: String?,
        @Field("toDate") toDate: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("FPSCode") FPSCode: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("rest/AcceptBySE")
    fun AcceptBySE(
        @Field("userId") userId: String?,
        @Field("complaintRegNo") complaintRegNo: String?
    ): Call<ModelResponse?>?

    @FormUrlEncoded
    @POST("stock/saveReturnPartsFromUserSide")
    fun saveReturnPartsFromUserSide(
        @Field("UserId") UserId: String?,
        @Field("SeUserId") SeUserId: String?,
        @Field("dispatchItems") dispatchItems: String?
    ): Call<ModelResponse?>?

    @Multipart
    @POST("expense/saveExpenses")
    fun saveExpenses(
        @Part("UserId") UserId: RequestBody?,
        @Part("TotalExAmount") TotalExAmount: RequestBody?,
        @Part("Remark") Remark: RequestBody?,
        @Part("DocketNo") DocketNo: RequestBody?,
        @Part("CourierName") CourierName: RequestBody?,
        @Part ExChallanCopy: MultipartBody.Part?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("expense/getExpensesList")
    fun getExpensesList(
        @Field("UserId") UserId: String?,
        @Field("ExpenseStatusID") ExpenseStatusID: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("expense/completeExpenses")
    fun completeExpenses(
        @Field("User" + "Id") UserId: String?,
        @Field("ExpenseId") ExpenseId: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("posError/GetErrorTypes")
    fun GetErrorTypes(
        @Field("UserID") UserID: String?,
        @Field("ErrorTypeId") ErrorTypeId: String?
    ): Call<GetErrorTypesRoot?>?

    @FormUrlEncoded
    @POST("posError/GetPOSDeviceErrors")
    fun GetPOSDeviceErrors(
        @Field("ErrorId") ErrorId: String?,
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("ErrorRegNo") ErrorRegNo: String?,
        @Field("ErrorStatusId") ErrorStatusId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("ErrorTypeId") ErrorTypeId: String?
    ): Call<GetPosDeviceErrorsRoot?>?

    @FormUrlEncoded
    @POST("posError/GetErrorImages")
    fun GetErrorImages(
        @Field("ErrorId") ErrorId: String?,
        @Field("UserID") UserID: String?,
        @Field("ErrorRegNo") ErrorRegNo: String?
    ): Call<GetErrorImagesRoot?>?

    @FormUrlEncoded
    @POST("posError/UpdateErrorStatus")
    fun UpdateErrorStatus(
        @Field("ErrorId") ErrorId: String?,
        @Field("UserID") UserID: String?,
        @Field("ErrorRegNo") ErrorRegNo: String?,
        @Field("ErrorStatusId") ErrorStatusId: String?,
        @Field("Remark") Remark: String?
    ): Call<UpdateErrorStatusRoot?>?

    @FormUrlEncoded
    @POST("posError/GetErrorRemarks")
    fun GetErrorRemarks(
        @Field("ErrorId") ErrorId: String?,
        @Field("UserID") UserID: String?,
        @Field("ErrorRegNo") ErrorRegNo: String?
    ): Call<GetRemarkRoot?>?

    //Ins Weinghing scale
    @FormUrlEncoded
    @POST("WeigIris/getIrisWeighInstallationCnt")
    fun appWeightCountApi(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?,
        @Field("StatusId") StatusId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?
    ): Call<CountRoot?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/WeinghIRISDeliveryInstallCnt")
    fun appWeightCountApiiris(
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("DeviceTypeId") DeviceTypeId: String?,
        @Field("DeviceModelId") DeviceModelId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("ToUserId") ToUserId: String?
    ): Call<CountRoot?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/WeinghIRISDeliveryInstallCnt")
    fun getIrisDashboardData(
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("DeviceTypeId") DeviceTypeId: String?,
        @Field("DeviceModelId") DeviceModelId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("ToUserId") ToUserId: String?
    ): Call<IrisDashboardResponse?>?

    @FormUrlEncoded
    @POST("SensorInstall/SensorDisbnDashbordCnt")
    fun getBiometricSensorDashboardData(
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("ToUserId") ToUserId: String?
    ): Call<BiometricDashboardResponse?>?

    @GET("rest/apiGetDistictList")
    fun apiGetDistictList_w(): Call<ModelDistrict_w?>?

    @FormUrlEncoded
    @POST("WeigIris/GetCustomerDetailsByFPS")
    fun apiDetailsByFPS(
        @Field("FPSCode") FPSCode: String?,
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?
    ): Call<DetailByFpsRoot?>?

    @FormUrlEncoded
    @POST("CloseGuard/GetCustomerDetailsByFPSForCloseGuard")
    fun getCustomerDetailsByFPSForCloseGuard(
        @Field("FPSCode") FPSCode: String?,
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?
    ): Call<DetailByFpsRoot?>?

    @FormUrlEncoded
    @POST("SensorInstall/GetCustomerDetailsByFPSForSensorDistribution")
    fun getCustomerDetailsByFPSForSensorDistribution(
        @Field("FPSCode") FPSCode: String?,
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?
    ): Call<DetailsByFPSForSensorRoot?>?

    @FormUrlEncoded
    @POST("WeigIris/getIrisWeighInstallation")
    fun apiIrisWeighInstallation(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?,
        @Field("StatusId") StatusId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?
    ): Call<WeightInsRoot?>?

    @FormUrlEncoded
    @POST("SensorInstall/SensorDisbnSummaryCnt")
    fun getSensorDisbnSummaryCnt(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("ToUserId") ToUserId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?
    ): Call<SensorSummaryResponse?>?

    @FormUrlEncoded
    @POST("SensorInstall/SensorDistributionDetailsList")
    fun getSensorDistributionDetailsList(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("DeviceCode") DeviceCode: String?,
        @Field("BiometricSerialNo") BiometricSerialNo: String?,
        @Field("FilterTypeId") FilterTypeId: String?,
        @Field("IsPagination") IsPagination: String?,
        @Field("PageNo") PageNo: String?,
        @Field("PageSize") PageSize: String?,
        @Field("ToUserId") ToUserId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?
    ): Call<SensorDistributionDetailsListResp?>?

    @Multipart
    @POST("WeigIrisInstall/saveInstallationReq")
    fun saveInstallationRe(
        @Part("UserID") UserID: RequestBody?,
        @Part("DeliveryId") DeliveryId: RequestBody?,
        @Part("IrisScannerModelId") IrisScannerModelId: RequestBody?,
        @Part("IrisScannerSerialNo") IrisScannerSerialNo: RequestBody?,
        @Part("ShopAddress") ShopAddress: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part("Remarks") Remarks: RequestBody?,
        @Part("DeliveredOn") DeliveredOn: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("DealerMobileNo") DealerMobileNo: RequestBody?,
        @Part InstallationWeinghingImage: MultipartBody.Part?,
        @Part InstallationIRISScannerImage: MultipartBody.Part?,
        @Part InstallationDealerPhoto: MultipartBody.Part?,
        @Part InstallationDealerSignature: MultipartBody.Part?,
        @Part InstallationChallan: MultipartBody.Part?
    ): Call<SaveRoot?>?

    @Multipart
    @POST("WeigIrisInstall/saveIRISDeliverInstallationReq")
    fun saveInstallationRq(
        @Part("UserID") UserID: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("DealerMobileNo") DealerMobileNo: RequestBody?,
        @Part("IrisScannerModelId") IrisScannerModelId: RequestBody?,
        @Part("IrisScannerSerialNo") IrisScannerSerialNo: RequestBody?,
        @Part("ShopAddress") ShopAddress: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part("Remarks") Remark: RequestBody?,
        @Part("DeliveredOn") DeliveredOn: RequestBody?,
        @Part IRISSerialNoImage: MultipartBody.Part?,
        @Part DealerImageWithIRIS: MultipartBody.Part?,
        @Part DealerSignatureIRIS: MultipartBody.Part?,
        @Part DealerPhotIdProof: MultipartBody.Part?
    ): Call<SaveIRISDeliverResponse?>?

    @Multipart
    @POST("SensorInstall/saveSensorDistribution")
    fun saveSensorDistribution(
        @Part("UserID") UserID: RequestBody?,
        @Part("TranId") TranId: RequestBody?,
        @Part("DeviceCode") DeviceCode: RequestBody?,
        @Part("BiometricSerialNo") BiometricSerialNo: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("DealerMobileNo") DealerMobileNo: RequestBody?,
        @Part("ShopAddress") ShopAddress: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part("Remarks") Remark: RequestBody?,
        @Part("DeliveredOn") DeliveredOn: RequestBody?,
        @Part BiometricSerialNoImage: MultipartBody.Part?,
        @Part DealerImageWithBiometric: MultipartBody.Part?,
        @Part DealerSignature: MultipartBody.Part?,
        @Part DealerPhotoIDProof: MultipartBody.Part?
    ): Call<SaveBiometricDeliverResponse?>?

    @Multipart
    @POST("WeigIrisReplacement/saveIRISReplacementReq")
    fun saveIRISReplacementReqpr(
        @Part("UserID") UserID: RequestBody?,
        @Part("DeliveryId") DeliveryId: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("DealerMobileNo") DealerMobileNo: RequestBody?,
        @Part("IrisScannerModelId") IrisScannerModelId: RequestBody?,
        @Part("IrisScannerSerialNo") IrisScannerSerialNo: RequestBody?,
        @Part("ShopAddress") ShopAddress: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part("Remarks") Remark: RequestBody?,
        @Part("DeliveredOn") DeliveredOn: RequestBody?,
        @Part("ReplaceTypeId") ReplaceTypeId: RequestBody?,
        @Part IRISSerialNoImage: MultipartBody.Part?,
        @Part DealerImageWithIRIS: MultipartBody.Part?,
        @Part DealerSignatureIRIS: MultipartBody.Part?,
        @Part DealerPhotIdProof: MultipartBody.Part?
    ): Call<SaveIRISDeliverResponse?>?

    // @Part MultipartBody.Part[] campDocumentsParts);
    @Multipart
    @POST("CloseGuard/saveIRISCloseGuardDelivery")
    fun saveIRISCloseGuardDelivery(
        @Part("UserID") UserID: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("ShopAddress") ShopAddress: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part DealerImageWithIRIS: MultipartBody.Part?,
        @Part DealerSignatureIRIS: MultipartBody.Part?
    ): Call<SaveIRISDeliverResponse?>?

    @FormUrlEncoded
    @POST("WeigIris/IrisWeighDeliveryDtl")
    fun apiWeigDeliveryDetail(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?
    ): Call<weighingDelieryRoot?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisWeighInstallation")
    fun IrisWeighInstallation(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?,
        @Field("StatusId") StatusId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("DeliveryId") DeliveryId: String?
    ): Call<InstalledRoot?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisDeliveryInstallationL")
    fun IrisDeliveryInstallationL(
        @Field("UserID") UserID: String?,
        @Field("ToUserId") ToUserId: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?,
        @Field("BlockId") BlockId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("DeliveryId") DeliveryId: String?,
        @Field("DSerialNo") DSerialNo: String?,
        @Field("DeviceTypeId") DeviceTypeId: String?,
        @Field("DeviceModelId") DeviceModelId: String?,
        @Field("IsPagination") IsPagination: String?,
        @Field("PageNo") PageNo: String?,
        @Field("PageSize") PageSize: String?
    ): Call<IrisDeliveryListResponse?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisDeliveryInstallationLForReplacement")
    fun IrisDeliveryInstallationLForReplacement(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?,
        @Field("BlockId") BlockId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("DeliveryId") DeliveryId: String?,
        @Field("DSerialNo") DSerialNo: String?,
        @Field("DeviceTypeId") DeviceTypeId: String?,
        @Field("DeviceModelId") DeviceModelId: String?,
        @Field("IsPagination") IsPagination: String?,
        @Field("PageNo") PageNo: String?,
        @Field("PageSize") PageSize: String?
    ): Call<IrisDeliveryListResponse?>?

    @FormUrlEncoded
    @POST("CloseGuard/IrisCloseGuardDeliveryList")
    fun getCloseGuardDeliveryList(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("ToUserId") ToUserId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("IsPagination") DeliveryId: String?,
        @Field("PageNo") DSerialNo: String?,
        @Field("PageSize") DeviceTypeId: String?
    ): Call<ClosedGuardDeliveryListResponse?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisWeighInstallationDtl")
    fun IrisWeighInstallationDtl(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?,
        @Field("DeliveryId") DeliveryId: String?
    ): Call<InstalledDetailedRoot?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/IrisDeliveryInstallationDtl")
    fun IrisDeliveryInstallationDtl(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("TicketNo") TicketNo: String?,
        @Field("DeliveryId") DeliveryId: String?
    ): Call<InstalledDetailedResponse?>?

    @FormUrlEncoded
    @POST("WeigIris/DownloadAPIChallanPDF")
    fun DownloadAPIChallanPDF(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DeviceTypeId") DeviceTypeId: String?,
        @Field("ChallanTypeId") ChallanTypeId: String?,
        @Field("DistrictId") DistrictId: String?
    ): Call<challanRoot?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/VerifyDeliveryInst")
    fun verifyDelivery(
        @Field("UserID") userId: String?,
        @Field("DeliveryId") DeliveryId: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DeviceSerialNo") DeviceSerialNo: String?
    ): Call<ModelLogout?>?

    @Multipart
    @POST("WeigIris/updateDelivery")
    fun updateDelivery(
        @Part("UserID") UserID: RequestBody?,
        @Part("DeliveryId") DeliveryId: RequestBody?,
        @Part("WeighingScaleSerialNo") FPSCode: RequestBody?,
        @Part("Remarks") DeviceSerialNo: RequestBody?,
        @Part WeighingImage: MultipartBody.Part?,
        @Part DealerImage: MultipartBody.Part?,
        @Part ChallanImage: MultipartBody.Part?
    ): Call<SaveRoot?>?

    @FormUrlEncoded
    @POST("WeigIrisInstall/checkIRISSerialNoDeliveredOrNot")
    fun checkIrisSerialNo(
        @Field("UserID") UserID: String?,
        @Field("IrisScannerModelId") IrisScannerModelId: String?,
        @Field("IrisScannerSerialNo") IrisScannerSerialNo: String?
    ): Call<CheckIrisSerialNoResponse?>?

    @FormUrlEncoded
    @POST("SensorInstall/getL0DeviceCodeByFPS")
    fun getL0DeviceCodeByFPS(
        @Field("UserID") UserID: String?,
        @Field("FPSCode") FPSCode: String?
    ): Call<DeviceCodeByFPSResponse?>?

    @FormUrlEncoded
    @POST("SensorInstall/UpdateDeviceTypeToChangeFSensor")
    fun updateDeviceTypeToChangeFSensor(
        @Field("UserID") UserID: String?,
        @Field("DeviceCode") DeviceCode: String?
    ): Call<UpdateDeviceTypeToChangeFSensorResp?>?



    @FormUrlEncoded
    @POST("WeigIris/getCombinedIRISWehingDetailReport")
    fun getCombinedIRISWehingDetailReport(
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("DeviceTypeId") DeviceTypeId: String?,
        @Field("DeviceModelId") DeviceModelId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("ToUserId") ToUserId: String?
    ): Call<IrisInstallationPendingListResp?>?

    @Multipart
    @POST("WeigIrisReplacement/saveWeigReplacementReq")
    fun saveWeigReplacementReq(
        @Part("UserID") UserID: RequestBody?,
        @Part("DeliveryId") DeliveryId: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("DealerMobileNo") DealerMobileNo: RequestBody?,
        @Part("DeviceModelId") DeviceModelId: RequestBody?,
        @Part("SerialNo") SerialNo: RequestBody?,
        @Part("ShopAddress") ShopAddress: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part("Remarks") Remark: RequestBody?,
        @Part("DeliveredOn") DeliveredOn: RequestBody?,
        @Part DeviceSerialNoImage: MultipartBody.Part?,
        @Part DealerImageWithDevice: MultipartBody.Part?,
        @Part DealerSignature: MultipartBody.Part?,
        @Part DealerPhotoIdProof: MultipartBody.Part?
    ): Call<SaveIRISDeliverResponse?>?

    //Feedback
    @FormUrlEncoded
    @POST("feedback/GetCustomerDetailsByFPSToFeedback")
    fun apiDetailsByFPSToFeedback(
        @Field("FPSCode") FPSCode: String?,
        @Field("UserID") UserID: String?,
        @Field("DistrictId") DistrictId: String?
    ): Call<FeedbackDetailByFpsRoot?>?

    @Multipart
    @POST("feedback/saveVisitFeedbackbyDC")
    fun saveVisitFeedbackbyDC(
        @Part("UserID") UserID: RequestBody?,
        @Part("FPSCode") FPSCode: RequestBody?,
        @Part("ShopAddress") ShopAddress: RequestBody?,
        @Part("Latitude") Latitude: RequestBody?,
        @Part("Longitude") Longitude: RequestBody?,
        @Part("Remarks") Remark: RequestBody?,
        @Part("LogInTime") LogInTime: RequestBody?,
        @Part SelpiheWithFPSNoAndDealer: MultipartBody.Part?,
        @Part DCVisitingChallan: MultipartBody.Part?,
        @Part DCVisitingFeedbackDealerSignature: MultipartBody.Part?
    ): Call<SaveFeedbackbyDCRoot?>?

    @FormUrlEncoded
    @POST("feedback/getVisitFeedbackbyDCList")
    fun getVisitFeedbackDCList(
        @Field("UserID") UserID: String?,
        @Field("FeedBackId") FeedBackId: String?,
        @Field("FPSCode") FPSCode: String?,
        @Field("DistrictId") DistrictId: String?,
        @Field("DateFrom") DateFrom: String?,
        @Field("DateTo") DateTo: String?,
        @Field("ToUserId") ToUserId: String?
    ): Call<FeedbackbyDCListRoot?>?

    @GET("stock/getPartsList")
    fun getPartsList(): Call<ModelParts>


    @GET("WeigIrisReplacement/getReplacementTypes")
    fun getReplacementTypes(): Call<ReplacementTypesResponse>

}



