package com.callmangement.ui.complaint;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.MutableLiveData;

import com.callmangement.Network.APIService;
import com.callmangement.Network.MultipartRequester;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.model.complaints.ModelChallanUploadComplaint;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.complaints.ModelComplaintsCount;
import com.callmangement.model.district.ModelDistrict;
import com.callmangement.model.reports.ModelSLAReport;
import com.callmangement.model.reports.ModelSLAReportDetails;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.complaints.ModelComplaint;
import com.callmangement.model.complaints.ModelResolveComplaint;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintRepository {
    private static final String TAG = "ComplaintRepository";
    private final Context mContext;
    private final PrefManager prefManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<ModelComplaint> mutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelComplaint> modelComplaintMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelChallanUploadComplaint> modelChallanUploadComplaintMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelSLAReport> mutableSLAReportData = new MutableLiveData<>();
    private final MutableLiveData<ModelSLAReportDetails> mutableSLAReportDetailsData = new MutableLiveData<>();
    private final MutableLiveData<ModelResolveComplaint> modelResolveComplaintMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelTehsil> modelTehsilMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelDistrict> modelDistrictMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ModelComplaintsCount> modelComplaintsCountMutableLiveData = new MutableLiveData<>();

    public ComplaintRepository(Context mContext) {
        this.mContext = mContext;
        prefManager = new PrefManager(mContext);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<ModelComplaint> getMutableLiveData(String user_id, String month, String year) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaint> call = service.complaint_list(user_id, month, year);
        call.enqueue(new Callback<ModelComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelComplaint> call, @NonNull Response<ModelComplaint> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelComplaint model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        mutableLiveData.setValue(model);
                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaint> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<ModelComplaint> getModelComplaintMutableLiveData(String user_id, String districtId, String complainStatusId, String fromDate, String toDate, String IsPagination, String PageNo, String PageSize,String FPSCode) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaint> call = service.getComplaintListDistStatusDateWise(user_id, districtId, complainStatusId, fromDate, toDate,IsPagination,PageNo,PageSize,"");
    //    Log.d("--BaseUrl---", "  " + call.request().url());
    //    Log.d("--response---", "  " + user_id + "  " + districtId + "  " + complainStatusId + "  " + fromDate + toDate);
        call.enqueue(new Callback<ModelComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelComplaint> call, @NonNull Response<ModelComplaint> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelComplaint model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        List<ModelComplaintList> modelComplaintList = model.getComplaint_List();
                        modelComplaintMutableLiveData.setValue(model);


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
                        modelComplaintMutableLiveData.setValue(model);
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaint> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                Log.d("error----", "is" + t.getMessage());
            }
        });
        return modelComplaintMutableLiveData;
    }





    public MutableLiveData<ModelComplaint> getModelComplaintMutableLiveDataNew(String user_id, String districtId, String complainStatusId, String fromDate, String toDate,
                                                                               String IsPagination, String PageNo, String PageSize, String FPSCode) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaint> call = service.getComplaintListDistStatusDateWiseNew(user_id, districtId, complainStatusId, fromDate, toDate,IsPagination,PageNo,PageSize,FPSCode);
        Log.d("--BaseUrl---", "  " + call.request().url());
        Log.d("--response---", "  " + user_id + "  " + districtId + "  " + complainStatusId + "  " + fromDate + toDate+FPSCode);
        call.enqueue(new Callback<ModelComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelComplaint> call, @NonNull Response<ModelComplaint> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelComplaint model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        List<ModelComplaintList> modelComplaintList1 = model.getComplaint_List();
                        modelComplaintMutableLiveData.setValue(model);

                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaint> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                Log.d("error----", "is" + t.getMessage());
            }
        });
        return modelComplaintMutableLiveData;
    }






    public MutableLiveData<ModelChallanUploadComplaint> getModelChallanUploadComplaintMutableLiveData(String user_id, String districtId, String complainRegNo,
                                                                                                      String FPSCode, String fromDate, String toDate,
                                                                                                      String IsPagination, String PageNo, String PageSize) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelChallanUploadComplaint> call = service.getModelChallanUploadComplaintListData(user_id, districtId, complainRegNo, FPSCode, fromDate, toDate,IsPagination,PageNo,PageSize);
        Log.d("--response---", "  " + user_id + "  " + districtId + "  " + complainRegNo + "  " + fromDate + toDate);
        call.enqueue(new Callback<ModelChallanUploadComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelChallanUploadComplaint> call, @NonNull Response<ModelChallanUploadComplaint> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelChallanUploadComplaint model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelChallanUploadComplaintMutableLiveData.setValue(model);

                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelChallanUploadComplaint> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                Log.d("error----", "is" + t.getMessage());
            }
        });
        return modelChallanUploadComplaintMutableLiveData;
    }



    public MutableLiveData<ModelChallanUploadComplaint> getModelEditChallanComplaintMutableLiveData(String user_id, String districtId, String complainRegNo,
                                                                                                      String FPSCode) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelChallanUploadComplaint> call = service.getModelEditChallanComplaintListData(user_id, districtId, complainRegNo, FPSCode);
        Log.d("--response---", "  " + user_id + "  " + districtId + "  " + complainRegNo );
        call.enqueue(new Callback<ModelChallanUploadComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelChallanUploadComplaint> call, @NonNull Response<ModelChallanUploadComplaint> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelChallanUploadComplaint model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        List<ModelComplaintList> modelComplaintList = model.getComplaint_List();
                        modelChallanUploadComplaintMutableLiveData.setValue(model);

                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelChallanUploadComplaint> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                Log.d("error----", "is" + t.getMessage());
            }
        });
        return modelChallanUploadComplaintMutableLiveData;
    }


    public MutableLiveData<ModelSLAReport> getMutableLiveDataForSLAReport(String user_id, String fromDate, String toDate, Integer ResolveInDays, String districtId) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelSLAReport> call = service.sla_report_list(user_id, fromDate, toDate, ResolveInDays, districtId);
        call.enqueue(new Callback<ModelSLAReport>() {
            @Override
            public void onResponse(@NonNull Call<ModelSLAReport> call, @NonNull Response<ModelSLAReport> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelSLAReport model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        mutableSLAReportData.setValue(model);
                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSLAReport> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return mutableSLAReportData;
    }


    public MutableLiveData<ModelSLAReportDetails> getMutableLiveDataForSLAReportDetails(String user_id, String fromDate, String toDate, Integer ResolveInDays, String districtId) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelSLAReportDetails> call = service.getSLAReportCountDetail(user_id, fromDate, toDate, ResolveInDays, districtId);
        call.enqueue(new Callback<ModelSLAReportDetails>() {
            @Override
            public void onResponse(@NonNull Call<ModelSLAReportDetails> call, @NonNull Response<ModelSLAReportDetails> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelSLAReportDetails model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        mutableSLAReportDetailsData.setValue(model);
                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSLAReportDetails> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return mutableSLAReportDetailsData;
    }


    public MutableLiveData<ModelResolveComplaint> getModelResolveComplaintMutableLiveData(String user_id, String IsPhysicalDamage, String remark, String complaint_reg_no, String getStatus, String replacePartName, String caurierDetails, String se_image, String challanNo, String seRemarkDate, String replacePartsIds, String damageApprovalLetter, String DSO_LETTER_TYPE) {
        isLoading.setValue(true);

        //    RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), damageApprovalLetter);


        //    MultipartBody.Part body = MultipartBody.Part.createFormData("DamageApprovalLetter", damageApprovalLetter.getName(), requestFile);

        MultipartBody.Part dso_body = null;
        if (DSO_LETTER_TYPE.equals("IMAGE")) {
            dso_body = MultipartRequester.fromFile("DamageApprovalLetter", damageApprovalLetter);
        } else if (DSO_LETTER_TYPE.equals("PDF")) {
            File file = getFileFromUri(mContext, Uri.parse(damageApprovalLetter));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            dso_body = MultipartBody.Part.createFormData("DamageApprovalLetter", file.getName(), requestFile);
        } else {
            dso_body = MultipartBody.Part.createFormData("DamageApprovalLetter", "");
        }

        //   MultipartBody.Part.createFormData("DamageApprovalLetter", damageApprovalLetter.getName(), requestFile)
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelResolveComplaint> call = service.resolveComplaint(MultipartRequester.fromString(user_id),
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
                dso_body);

        call.enqueue(new Callback<ModelResolveComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelResolveComplaint> call, @NonNull Response<ModelResolveComplaint> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelResolveComplaint model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelResolveComplaintMutableLiveData.setValue(model);
                    } else {
                        modelResolveComplaintMutableLiveData.setValue(model);
                        //    Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelResolveComplaint> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelResolveComplaintMutableLiveData;
    }

    public MutableLiveData<ModelResolveComplaint> getModelChallanUploadMutableLiveData(String userId, String ComplainId, String ComplainRegNo, String ChallanNo, String chllanImage) {
        isLoading.setValue(true);

        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelResolveComplaint> call = service.challanUpload(MultipartRequester.fromString(userId),
                MultipartRequester.fromString(ComplainId),
                MultipartRequester.fromString(ComplainRegNo),
                MultipartRequester.fromString(ChallanNo),
                MultipartRequester.fromFile("chllanImage", chllanImage));

        call.enqueue(new Callback<ModelResolveComplaint>() {
            @Override
            public void onResponse(@NonNull Call<ModelResolveComplaint> call, @NonNull Response<ModelResolveComplaint> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelResolveComplaint model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelResolveComplaintMutableLiveData.setValue(model);
                    } else {
                        modelResolveComplaintMutableLiveData.setValue(model);
                        //    Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelResolveComplaint> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelResolveComplaintMutableLiveData;
    }

    public MutableLiveData<ModelTehsil> getModelTehsilMutableLiveData(String district_id) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelTehsil> call = service.apiGetTehsilByDistict(district_id);
        call.enqueue(new Callback<ModelTehsil>() {
            @Override
            public void onResponse(@NonNull Call<ModelTehsil> call, @NonNull Response<ModelTehsil> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelTehsil modelTehsil = response.body();
                    if (Objects.requireNonNull(modelTehsil).getStatus().equals("200")) {
                        List<ModelTehsilList> modelTehsilList = modelTehsil.getTehsil_List();
                        modelTehsilMutableLiveData.setValue(modelTehsil);
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
                        Toast.makeText(mContext, modelTehsil.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelTehsil> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelTehsilMutableLiveData;
    }

    public MutableLiveData<ModelDistrict> getModelDistrictMutableLiveData() {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelDistrict> call = service.apiGetDistictList();
        call.enqueue(new Callback<ModelDistrict>() {
            @Override
            public void onResponse(@NonNull Call<ModelDistrict> call, @NonNull Response<ModelDistrict> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelDistrict modelTehsil = response.body();
                    if (Objects.requireNonNull(modelTehsil).getStatus().equals("200")) {
                        modelDistrictMutableLiveData.setValue(modelTehsil);
                    } else {
                        Toast.makeText(mContext, modelTehsil.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelDistrict> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelDistrictMutableLiveData;
    }

    public MutableLiveData<ModelComplaintsCount> getModelComplaintsCountMutableLiveData(String user_id, String districtId, String fromDate, String toDate) {
        isLoading.setValue(true);
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ModelComplaintsCount> call = service.getComplaintsCountDateDistrictIdWise(user_id, districtId, fromDate, toDate);
        call.enqueue(new Callback<ModelComplaintsCount>() {
            @Override
            public void onResponse(@NonNull Call<ModelComplaintsCount> call, @NonNull Response<ModelComplaintsCount> response) {
                if (response.isSuccessful()) {
                    isLoading.setValue(false);
                    ModelComplaintsCount model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")) {
                        modelComplaintsCountMutableLiveData.setValue(model);
                    } else {
                        Toast.makeText(mContext, model.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    isLoading.setValue(false);
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComplaintsCount> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });
        return modelComplaintsCountMutableLiveData;
    }

    public static File getFileFromUri(Context context, Uri uri) {
        try {
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
            if (documentFile != null) {
                String fileName = documentFile.getName();
                File tempFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(tempFile);

                int read;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                return tempFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
