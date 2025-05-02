package com.callmangement.ui.complaint

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.callmangement.model.complaints.ModelChallanUploadComplaint
import com.callmangement.model.complaints.ModelComplaint
import com.callmangement.model.complaints.ModelComplaintsCount
import com.callmangement.model.complaints.ModelResolveComplaint
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.reports.ModelSLAReport
import com.callmangement.model.reports.ModelSLAReportDetails
import com.callmangement.model.tehsil.ModelTehsil

class ComplaintViewModel(application: Application) : AndroidViewModel(application) {
    var repository: ComplaintRepository = ComplaintRepository(application.applicationContext)

    val isLoading: LiveData<Boolean>
        get() = repository.isLoading

    fun getAssignComplaintList(
        user_id: String?,
        fromDate: String?,
        toDate: String?
    ): LiveData<ModelComplaint?> {
        return repository.getMutableLiveData(user_id, fromDate, toDate)
    }

    fun getSLAReportList(
        user_id: String?,
        fromDate: String?,
        toDate: String?,
        ResolveInDays: Int?,
        districtId: String?
    ): LiveData<ModelSLAReport?> {
        return repository.getMutableLiveDataForSLAReport(
            user_id,
            fromDate,
            toDate,
            ResolveInDays,
            districtId
        )
    }

    fun getSLAReportDetails(
        user_id: String?,
        fromDate: String?,
        toDate: String?,
        ResolveInDays: Int?,
        districtId: String?
    ): LiveData<ModelSLAReportDetails?> {
        return repository.getMutableLiveDataForSLAReportDetails(
            user_id,
            fromDate,
            toDate,
            ResolveInDays,
            districtId
        )
    }

    fun resolveComplaint(
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
    ): LiveData<ModelResolveComplaint?> {
        return repository.getModelResolveComplaintMutableLiveData(
            user_id,
            IsPhysicalDamage,
            remark,
            complaint_reg_no,
            getStatus,
            replacePartName,
            caurierDetails,
            se_image,
            challanNo,
            seRemarkDate,
            replacePartsIds,
            damageApprovalLetter,
            DSO_LETTER_TYPE
        )
    }

    fun challanUpload(
        userId: String?,
        ComplainId: String?,
        ComplainRegNo: String?,
        ChallanNo: String?,
        chllanImage: String?
    ): LiveData<ModelResolveComplaint?> {
        return repository.getModelChallanUploadMutableLiveData(
            userId,
            ComplainId,
            ComplainRegNo,
            ChallanNo,
            chllanImage
        )
    }

    fun getTehsil(district_id: String?): LiveData<ModelTehsil?> {
        return repository.getModelTehsilMutableLiveData(district_id)
    }

    val district: LiveData<ModelDistrict?>
        get() = repository.getModelDistrictMutableLiveData()

    fun getComplaintsCount(
        user_id: String?,
        district_id: String?,
        fromDate: String?,
        toDate: String?
    ): LiveData<ModelComplaintsCount?> {
        return repository.getModelComplaintsCountMutableLiveData(
            user_id,
            district_id,
            fromDate,
            toDate
        )
    }

    fun getComplaintsDistStatusDateWise(
        user_id: String?,
        district_id: String?,
        complainStatusId: String?,
        fromDate: String?,
        toDate: String?,
        IsPagination: String?,
        PageNo: String?,
        PageSize: String?,
        FPSCode: String?
    ): LiveData<ModelComplaint?> {
        return repository.getModelComplaintMutableLiveData(
            user_id,
            district_id,
            complainStatusId,
            fromDate,
            toDate,
            IsPagination,
            PageNo,
            PageSize,
            FPSCode
        )
    }


    fun getComplaintsDistStatusDateWiseNew(
        user_id: String,
        district_id: String,
        complainStatusId: String,
        fromDate: String,
        toDate: String,
        IsPagination: String?,
        PageNo: String?,
        PageSize: String?,
        FPSCode: String
    ): LiveData<ModelComplaint?> {
        return repository.getModelComplaintMutableLiveDataNew(
            user_id,
            district_id,
            complainStatusId,
            fromDate,
            toDate,
            IsPagination,
            PageNo,
            PageSize,
            FPSCode
        )
    }


    fun getChallanUploadPendingComplaintList(
        user_id: String, district_id: String, ComplainRegNo: String, FPSCode: String?,
        fromDate: String, toDate: String,
        IsPagination: String?, PageNo: String?, PageSize: String?
    ): LiveData<ModelChallanUploadComplaint?> {
        return repository.getModelChallanUploadComplaintMutableLiveData(
            user_id,
            district_id,
            ComplainRegNo,
            FPSCode,
            fromDate,
            toDate,
            IsPagination,
            PageNo,
            PageSize
        )
    }

    fun getModelEditChallanComplaintMutableLiveData(
        user_id: String,
        district_id: String,
        ComplainRegNo: String,
        FPSCode: String?
    ): LiveData<ModelChallanUploadComplaint?> {
        return repository.getModelEditChallanComplaintMutableLiveData(
            user_id,
            district_id,
            ComplainRegNo,
            FPSCode
        )
    }
}
