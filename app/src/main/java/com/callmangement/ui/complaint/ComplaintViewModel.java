package com.callmangement.ui.complaint;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.callmangement.model.complaints.ModelChallanUploadComplaint;
import com.callmangement.model.complaints.ModelComplaintsCount;
import com.callmangement.model.district.ModelDistrict;
import com.callmangement.model.reports.ModelSLAReport;
import com.callmangement.model.reports.ModelSLAReportDetails;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.complaints.ModelComplaint;
import com.callmangement.model.complaints.ModelResolveComplaint;

import java.io.File;

public class ComplaintViewModel extends AndroidViewModel {
    public ComplaintRepository repository;

    public ComplaintViewModel(@NonNull Application application) {
        super(application);
        repository = new ComplaintRepository(application.getApplicationContext());
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }

    public LiveData<ModelComplaint> getAssignComplaintList(String user_id, String fromDate, String toDate) {
        return repository.getMutableLiveData(user_id, fromDate, toDate);
    }

    public LiveData<ModelSLAReport> getSLAReportList(String user_id, String fromDate, String toDate, Integer ResolveInDays, String districtId) {
        return repository.getMutableLiveDataForSLAReport(user_id, fromDate, toDate, ResolveInDays, districtId);
    }

    public LiveData<ModelSLAReportDetails> getSLAReportDetails(String user_id, String fromDate, String toDate, Integer ResolveInDays, String districtId) {
        return repository.getMutableLiveDataForSLAReportDetails(user_id, fromDate, toDate, ResolveInDays, districtId);
    }

    public LiveData<ModelResolveComplaint> resolveComplaint(String user_id, String IsPhysicalDamage, String remark, String complaint_reg_no, String getStatus, String replacePartName, String caurierDetails, String se_image, String challanNo, String seRemarkDate, String replacePartsIds, String damageApprovalLetter,String DSO_LETTER_TYPE) {
        return repository.getModelResolveComplaintMutableLiveData(user_id, IsPhysicalDamage, remark, complaint_reg_no, getStatus, replacePartName, caurierDetails, se_image, challanNo, seRemarkDate, replacePartsIds,damageApprovalLetter,DSO_LETTER_TYPE);
    }

    public LiveData<ModelResolveComplaint> challanUpload(String userId, String ComplainId, String ComplainRegNo, String ChallanNo, String chllanImage) {
        return repository.getModelChallanUploadMutableLiveData(userId, ComplainId, ComplainRegNo, ChallanNo, chllanImage);
    }

    public LiveData<ModelTehsil> getTehsil(String district_id) {
        return repository.getModelTehsilMutableLiveData(district_id);
    }

    public LiveData<ModelDistrict> getDistrict() {
        return repository.getModelDistrictMutableLiveData();
    }

    public LiveData<ModelComplaintsCount> getComplaintsCount(String user_id, String district_id, String fromDate, String toDate) {
        return repository.getModelComplaintsCountMutableLiveData(user_id, district_id, fromDate, toDate);
    }

    public LiveData<ModelComplaint> getComplaintsDistStatusDateWise(String user_id, String district_id, String complainStatusId, String fromDate, String toDate,
                                                                    String IsPagination, String PageNo, String PageSize,String FPSCode) {
        return repository.getModelComplaintMutableLiveData(user_id, district_id, complainStatusId, fromDate, toDate,IsPagination,PageNo,PageSize,FPSCode);
    }


    public LiveData<ModelComplaint> getComplaintsDistStatusDateWiseNew(String user_id, String district_id, String complainStatusId, String fromDate, String toDate,
                                                                       String IsPagination, String PageNo, String PageSize,String FPSCode) {
        return repository.getModelComplaintMutableLiveDataNew(user_id, district_id, complainStatusId, fromDate, toDate,IsPagination,PageNo,PageSize,FPSCode);
    }



    public LiveData<ModelChallanUploadComplaint> getChallanUploadPendingComplaintList(String user_id, String district_id, String ComplainRegNo, String FPSCode,
                                                                                      String fromDate, String toDate,
                                                                                      String IsPagination, String PageNo, String PageSize) {
        return repository.getModelChallanUploadComplaintMutableLiveData(user_id, district_id, ComplainRegNo, FPSCode, fromDate, toDate,IsPagination,PageNo,PageSize);
    }

    public LiveData<ModelChallanUploadComplaint> getModelEditChallanComplaintMutableLiveData(String user_id, String district_id, String ComplainRegNo, String FPSCode) {
        return repository.getModelEditChallanComplaintMutableLiveData(user_id, district_id, ComplainRegNo, FPSCode);
    }

}
