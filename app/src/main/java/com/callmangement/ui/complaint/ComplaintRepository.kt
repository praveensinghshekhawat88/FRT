package com.callmangement.ui.complaint

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.callmangement.Network.APIService
import com.callmangement.Network.MultipartRequester
import com.callmangement.Network.RetrofitInstance
import com.callmangement.R
import com.callmangement.model.complaints.ModelChallanUploadComplaint
import com.callmangement.model.complaints.ModelComplaint
import com.callmangement.model.complaints.ModelComplaintsCount
import com.callmangement.model.complaints.ModelResolveComplaint
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.reports.ModelSLAReport
import com.callmangement.model.reports.ModelSLAReportDetails
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.utils.PrefManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Objects

class ComplaintRepository(private val mContext: Context) {
    private val prefManager = PrefManager(mContext)
    @JvmField
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val mutableLiveData = MutableLiveData<ModelComplaint?>()
    private val modelComplaintMutableLiveData = MutableLiveData<ModelComplaint?>()
    private val modelChallanUploadComplaintMutableLiveData =
        MutableLiveData<ModelChallanUploadComplaint?>()
    private val mutableSLAReportData = MutableLiveData<ModelSLAReport?>()
    private val mutableSLAReportDetailsData = MutableLiveData<ModelSLAReportDetails?>()
    private val modelResolveComplaintMutableLiveData = MutableLiveData<ModelResolveComplaint?>()
    private val modelTehsilMutableLiveData = MutableLiveData<ModelTehsil?>()
    private val modelDistrictMutableLiveData = MutableLiveData<ModelDistrict?>()
    private val modelComplaintsCountMutableLiveData = MutableLiveData<ModelComplaintsCount?>()

    fun getMutableLiveData(
        user_id: String?,
        month: String?,
        year: String?
    ): MutableLiveData<ModelComplaint?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.complaint_list(user_id, month, year)
        call.enqueue(object : Callback<ModelComplaint?> {
            override fun onResponse(
                call: Call<ModelComplaint?>,
                response: Response<ModelComplaint?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        mutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaint?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return mutableLiveData
    }

    fun getModelComplaintMutableLiveData(
        user_id: String?,
        districtId: String?,
        complainStatusId: String?,
        fromDate: String?,
        toDate: String?,
        IsPagination: String?,
        PageNo: String?,
        PageSize: String?,
        FPSCode: String?
    ): MutableLiveData<ModelComplaint?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getComplaintListDistStatusDateWise(
            user_id,
            districtId,
            complainStatusId,
            fromDate,
            toDate,
            IsPagination,
            PageNo,
            PageSize,
            ""
        )
        //    Log.d("--BaseUrl---", "  " + call.request().url());
        //    Log.d("--response---", "  " + user_id + "  " + districtId + "  " + complainStatusId + "  " + fromDate + toDate);
        call.enqueue(object : Callback<ModelComplaint?> {
            override fun onResponse(
                call: Call<ModelComplaint?>,
                response: Response<ModelComplaint?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        val modelComplaintList = model.complaint_List
                        modelComplaintMutableLiveData.setValue(model)


                        //                        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("8")) {
//                            if (prefManager.getUSER_Id().equals("12")) {
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelComplaintList.size(); i++) {
//                                    if (Constants.tehsilNameListFor12.contains(modelComplaintList.get(i).getTehsil())) {
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            } else if (prefManager.getUSER_Id().equals("87")) {
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelComplaintList.size(); i++) {
//                                    if (Constants.tehsilNameListFor87.contains(modelComplaintList.get(i).getTehsil())) {
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            } else {
//                                modelComplaintMutableLiveData.setValue(model);
//                            }
//                        }

//                        else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("25")){
//                            if (prefManager.getUSER_Id().equals("29")){
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i<modelComplaintList.size(); i++){
//                                    if (Constants.tehsilNameListFor29.contains(modelComplaintList.get(i).getTehsil())){
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            }else if (prefManager.getUSER_Id().equals("88")){
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i<modelComplaintList.size(); i++){
//                                    if (Constants.tehsilNameListFor88.contains(modelComplaintList.get(i).getTehsil())){
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            }else {
//                                modelComplaintMutableLiveData.setValue(model);
//                            }
//                        }

//                        else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("33")){
//                            if (prefManager.getUSER_Id().equals("37")){
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i<modelComplaintList.size(); i++){
//                                    if (Constants.tehsilNameListFor37.contains(modelComplaintList.get(i).getTehsil())){
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            }else if (prefManager.getUSER_Id().equals("89")){
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i<modelComplaintList.size(); i++){
//                                    if (Constants.tehsilNameListFor89.contains(modelComplaintList.get(i).getTehsil())){
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            }else {
//                                modelComplaintMutableLiveData.setValue(model);
//                            }
//                        }

//                        else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("17")){
//                            if (prefManager.getUSER_Id().equals("90")){
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i<modelComplaintList.size(); i++){
//                                    if (Constants.tehsilNameListFor90.contains(modelComplaintList.get(i).getTehsil())){
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            }else if (prefManager.getUSER_Id().equals("21")){
//                                List<ModelComplaintList> modelComplaintListsLocal = new ArrayList<>();
//                                for (int i = 0; i<modelComplaintList.size(); i++){
//                                    if (Constants.tehsilNameListFor21.contains(modelComplaintList.get(i).getTehsil())){
//                                        modelComplaintListsLocal.add(modelComplaintList.get(i));
//                                    }
//                                }
//                                model.setComplaint_List(modelComplaintListsLocal);
//                                modelComplaintMutableLiveData.setValue(model);
//                            }else {
//                                modelComplaintMutableLiveData.setValue(model);
//                            }
//                        }

//                        else {
//                            modelComplaintMutableLiveData.setValue(model);
//                        }
                    } else {
                        modelComplaintMutableLiveData.value = model
                        Toast.makeText(mContext, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaint?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("error----", "is" + t.message)
            }
        })
        return modelComplaintMutableLiveData
    }


    fun getModelComplaintMutableLiveDataNew(
        user_id: String,
        districtId: String,
        complainStatusId: String,
        fromDate: String,
        toDate: String,
        IsPagination: String?,
        PageNo: String?,
        PageSize: String?,
        FPSCode: String
    ): MutableLiveData<ModelComplaint?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getComplaintListDistStatusDateWiseNew(
            user_id,
            districtId,
            complainStatusId,
            fromDate,
            toDate,
            IsPagination,
            PageNo,
            PageSize,
            FPSCode
        )
        Log.d("--BaseUrl---", "  " + call.request().url)
        Log.d(
            "--response---",
            "  $user_id  $districtId  $complainStatusId  $fromDate$toDate$FPSCode"
        )
        call.enqueue(object : Callback<ModelComplaint?> {
            override fun onResponse(
                call: Call<ModelComplaint?>,
                response: Response<ModelComplaint?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        val modelComplaintList1 = model.complaint_List
                        modelComplaintMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaint?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("error----", "is" + t.message)
            }
        })
        return modelComplaintMutableLiveData
    }


    fun getModelChallanUploadComplaintMutableLiveData(
        user_id: String, districtId: String, complainRegNo: String,
        FPSCode: String?, fromDate: String, toDate: String,
        IsPagination: String?, PageNo: String?, PageSize: String?
    ): MutableLiveData<ModelChallanUploadComplaint?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getModelChallanUploadComplaintListData(
            user_id,
            districtId,
            complainRegNo,
            FPSCode,
            fromDate,
            toDate,
            IsPagination,
            PageNo,
            PageSize
        )
        Log.d(
            "--response---",
            "  $user_id  $districtId  $complainRegNo  $fromDate$toDate"
        )
        call.enqueue(object : Callback<ModelChallanUploadComplaint?> {
            override fun onResponse(
                call: Call<ModelChallanUploadComplaint?>,
                response: Response<ModelChallanUploadComplaint?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        modelChallanUploadComplaintMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelChallanUploadComplaint?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("error----", "is" + t.message)
            }
        })
        return modelChallanUploadComplaintMutableLiveData
    }


    fun getModelEditChallanComplaintMutableLiveData(
        user_id: String, districtId: String, complainRegNo: String,
        FPSCode: String?
    ): MutableLiveData<ModelChallanUploadComplaint?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getModelEditChallanComplaintListData(
            user_id,
            districtId,
            complainRegNo,
            FPSCode
        )
        Log.d("--response---", "  $user_id  $districtId  $complainRegNo")
        call.enqueue(object : Callback<ModelChallanUploadComplaint?> {
            override fun onResponse(
                call: Call<ModelChallanUploadComplaint?>,
                response: Response<ModelChallanUploadComplaint?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        val modelComplaintList = model.complaint_List
                        modelChallanUploadComplaintMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelChallanUploadComplaint?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("error----", "is" + t.message)
            }
        })
        return modelChallanUploadComplaintMutableLiveData
    }


    fun getMutableLiveDataForSLAReport(
        user_id: String?,
        fromDate: String?,
        toDate: String?,
        ResolveInDays: Int?,
        districtId: String?
    ): MutableLiveData<ModelSLAReport?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.sla_report_list(user_id, fromDate, toDate, ResolveInDays, districtId)
        call.enqueue(object : Callback<ModelSLAReport?> {
            override fun onResponse(
                call: Call<ModelSLAReport?>,
                response: Response<ModelSLAReport?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        mutableSLAReportData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelSLAReport?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return mutableSLAReportData
    }


    fun getMutableLiveDataForSLAReportDetails(
        user_id: String?,
        fromDate: String?,
        toDate: String?,
        ResolveInDays: Int?,
        districtId: String?
    ): MutableLiveData<ModelSLAReportDetails?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call =
            service.getSLAReportCountDetail(user_id, fromDate, toDate, ResolveInDays, districtId)
        call.enqueue(object : Callback<ModelSLAReportDetails?> {
            override fun onResponse(
                call: Call<ModelSLAReportDetails?>,
                response: Response<ModelSLAReportDetails?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        mutableSLAReportDetailsData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelSLAReportDetails?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return mutableSLAReportDetailsData
    }


    fun getModelResolveComplaintMutableLiveData(
        user_id: String?,
        IsPhysicalDamage: String?,
        remark: String?,
        complaint_reg_no: String?,
        getStatus: String?,
        replacePartName: String?,
        caurierDetails: String?,
        se_image: String?,
        challanNo: String?,
        seRemarkDate: String?,
        replacePartsIds: String?,
        damageApprovalLetter: String?,
        DSO_LETTER_TYPE: String
    ): MutableLiveData<ModelResolveComplaint?> {
        isLoading.value = true


        //    RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), damageApprovalLetter);


        //    MultipartBody.Part body = MultipartBody.Part.createFormData("DamageApprovalLetter", damageApprovalLetter.getName(), requestFile);
        var dso_body: MultipartBody.Part? = null
        if (DSO_LETTER_TYPE == "IMAGE") {
            dso_body = MultipartRequester.fromFile("DamageApprovalLetter", damageApprovalLetter)
        } else if (DSO_LETTER_TYPE == "PDF") {
            val file = getFileFromUri(mContext, Uri.parse(damageApprovalLetter))
            val requestFile = RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                file!!
            )
            dso_body = createFormData("DamageApprovalLetter", file.name, requestFile)
        } else {
            dso_body = createFormData("DamageApprovalLetter", "")
        }

        //   MultipartBody.Part.createFormData("DamageApprovalLetter", damageApprovalLetter.getName(), requestFile)
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.resolveComplaint(
            MultipartRequester.fromString(user_id),
            MultipartRequester.fromString(IsPhysicalDamage),
            MultipartRequester.fromString(remark),
            MultipartRequester.fromString(complaint_reg_no),
            MultipartRequester.fromString(getStatus),
            MultipartRequester.fromString(replacePartName),
            MultipartRequester.fromString(caurierDetails),
            MultipartRequester.fromString(challanNo),
            MultipartRequester.fromString(seRemarkDate),
            MultipartRequester.fromString(replacePartsIds),
            MultipartRequester.fromFile("se_image", se_image),
            dso_body
        )

        call.enqueue(object : Callback<ModelResolveComplaint?> {
            override fun onResponse(
                call: Call<ModelResolveComplaint?>,
                response: Response<ModelResolveComplaint?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        modelResolveComplaintMutableLiveData.setValue(model)
                    } else {
                        modelResolveComplaintMutableLiveData.setValue(model)
                        //    Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelResolveComplaint?>, t: Throwable) {
                isLoading.setValue(false)
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelResolveComplaintMutableLiveData
    }

    fun getModelChallanUploadMutableLiveData(
        userId: String?,
        ComplainId: String?,
        ComplainRegNo: String?,
        ChallanNo: String?,
        chllanImage: String?
    ): MutableLiveData<ModelResolveComplaint?> {
        isLoading.value = true

        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.challanUpload(
            MultipartRequester.fromString(userId),
            MultipartRequester.fromString(ComplainId),
            MultipartRequester.fromString(ComplainRegNo),
            MultipartRequester.fromString(ChallanNo),
            MultipartRequester.fromFile("chllanImage", chllanImage)
        )

        call.enqueue(object : Callback<ModelResolveComplaint?> {
            override fun onResponse(
                call: Call<ModelResolveComplaint?>,
                response: Response<ModelResolveComplaint?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        modelResolveComplaintMutableLiveData.setValue(model)
                    } else {
                        modelResolveComplaintMutableLiveData.setValue(model)

                        //    Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelResolveComplaint?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelResolveComplaintMutableLiveData
    }

    fun getModelTehsilMutableLiveData(district_id: String?): MutableLiveData<ModelTehsil?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.apiGetTehsilByDistict(district_id)
        call.enqueue(object : Callback<ModelTehsil?> {
            override fun onResponse(call: Call<ModelTehsil?>, response: Response<ModelTehsil?>) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val modelTehsil = response.body()
                    if (modelTehsil!!.status == "200") {
                        val modelTehsilList = modelTehsil!!.tehsil_List
                        modelTehsilMutableLiveData.setValue(modelTehsil)

                        //                        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("8")) {
//                            if (prefManager.getUSER_Id().equals("12")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor12.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            } else if (prefManager.getUSER_Id().equals("87")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor87.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            } else {
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            }
//                        }

//                        else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("25")){
//                            if (prefManager.getUSER_Id().equals("29")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor29.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            } else if (prefManager.getUSER_Id().equals("88")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor88.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            } else {
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            }
//
//                        }

//                        else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("33")){
//                            if (prefManager.getUSER_Id().equals("37")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor37.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            } else if (prefManager.getUSER_Id().equals("89")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor89.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            } else {
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            }
//                        }

//                        else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_DistrictId().equals("17")){
//                            if (prefManager.getUSER_Id().equals("90")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor90.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//
//                            } else if (prefManager.getUSER_Id().equals("21")) {
//                                List<ModelTehsilList> modelTehsilListsLocal = new ArrayList<>();
//                                for (int i = 0; i < modelTehsilList.size(); i++) {
//                                    if (Constants.tehsilIdListFor21.contains(modelTehsilList.get(i).getTehsilId())) {
//                                        modelTehsilListsLocal.add(modelTehsilList.get(i));
//                                    }
//                                }
//                                modelTehsil.setTehsil_List(modelTehsilListsLocal);
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            } else {
//                                modelTehsilMutableLiveData.setValue(modelTehsil);
//                            }
//                        }

//                        else {
//                            modelTehsilMutableLiveData.setValue(modelTehsil);
//                        }
                    } else {
                        Toast.makeText(mContext, modelTehsil!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelTehsil?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelTehsilMutableLiveData
    }

    fun getModelDistrictMutableLiveData(): MutableLiveData<ModelDistrict?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.apiGetDistictList()
        call.enqueue(object : Callback<ModelDistrict?> {
            override fun onResponse(
                call: Call<ModelDistrict?>,
                response: Response<ModelDistrict?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val modelTehsil = response.body()
                    if (modelTehsil!!.status == "200") {
                        modelDistrictMutableLiveData.setValue(modelTehsil)
                    } else {
                        Toast.makeText(mContext, modelTehsil!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelDistrict?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelDistrictMutableLiveData
    }

    fun getModelComplaintsCountMutableLiveData(
        user_id: String?,
        districtId: String?,
        fromDate: String?,
        toDate: String?
    ): MutableLiveData<ModelComplaintsCount?> {
        isLoading.value = true
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call =
            service.getComplaintsCountDateDistrictIdWise(user_id, districtId, fromDate, toDate)
        call.enqueue(object : Callback<ModelComplaintsCount?> {
            override fun onResponse(
                call: Call<ModelComplaintsCount?>,
                response: Response<ModelComplaintsCount?>
            ) {
                if (response.isSuccessful) {
                    isLoading.setValue(false)
                    val model = response.body()
                    if (model!!.status == "200") {
                        modelComplaintsCountMutableLiveData.setValue(model)
                    } else {
                        Toast.makeText(mContext, model!!.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    isLoading.setValue(false)
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ModelComplaintsCount?>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return modelComplaintsCountMutableLiveData
    }

    companion object {
        private const val TAG = "ComplaintRepository"
        fun getFileFromUri(context: Context, uri: Uri): File? {
            try {
                val documentFile = DocumentFile.fromSingleUri(context, uri)
                if (documentFile != null) {
                    val fileName = documentFile.name
                    val tempFile =
                        File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

                    val inputStream = context.contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(tempFile)

                    var read: Int
                    val buffer = ByteArray(1024)
                    while ((inputStream!!.read(buffer).also { read = it }) != -1) {
                        outputStream.write(buffer, 0, read)
                    }

                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    return tempFile
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}
