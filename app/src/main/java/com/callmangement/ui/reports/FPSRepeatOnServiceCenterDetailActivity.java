package com.callmangement.ui.reports;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.FPSRepeatOnServiceCenterDetailActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityFpsrepeatOnServiceCenterDetailBinding;
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList;
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaints;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FPSRepeatOnServiceCenterDetailActivity extends CustomActivity implements View.OnClickListener {
    private ActivityFpsrepeatOnServiceCenterDetailBinding binding;
    private PrefManager prefManager;
    private ModelRepeatFpsComplaintsList model;
    private List<ModelRepeatFpsComplaintsList> list = new ArrayList<>();
    private String districtId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFpsrepeatOnServiceCenterDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.count_on_service_center));
        prefManager = new PrefManager(mContext);
        model = (ModelRepeatFpsComplaintsList) getIntent().getSerializableExtra("param");
        districtId = prefManager.getUSER_DistrictId();
        setUpOnClickListener();
        fetchData();
    }

    private void setUpOnClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
//        binding.actionBar.buttonPDF.setOnClickListener(this);
    }

    private void fetchData(){
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ResponseBody> call = service.RepaetFPSComplainsOnSCList(prefManager.getUSER_Id(),"","",districtId, model.getFpscode());
            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    String responseStr = response.body().string();
                                    ModelRepeatFpsComplaints modelResponse = (ModelRepeatFpsComplaints) getObject(responseStr, ModelRepeatFpsComplaints.class);
                                    if (modelResponse != null){
                                        if (modelResponse.status.equals("200")) {
                                            if (modelResponse.parts.size() > 0) {
                                                list = modelResponse.parts;
//                                                Constants.modelRepeatFpsComplaintsList = list;
                                                setUpAdapter(list);
                                            } else {
                                                binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                                                binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                                            binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                                        binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                                    binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                }
                            } else {
                                binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                                binding.tvNoDataFound.setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                            binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                        binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                        binding.tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                    binding.rvFpsRepeatDetail.setVisibility(View.GONE);
                    binding.tvNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpAdapter(List<ModelRepeatFpsComplaintsList> list){
        if (list.size() > 0) {
            binding.rvFpsRepeatDetail.setVisibility(View.VISIBLE);
            binding.tvNoDataFound.setVisibility(View.GONE);
            binding.rvFpsRepeatDetail.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            binding.rvFpsRepeatDetail.setAdapter(new FPSRepeatOnServiceCenterDetailActivityAdapter(mContext, list));
            binding.textTotalCount.setText(String.valueOf(list.size()));
        } else {
            binding.rvFpsRepeatDetail.setVisibility(View.GONE);
            binding.tvNoDataFound.setVisibility(View.VISIBLE);
            binding.textTotalCount.setText("0");
        }
    }

    public void repeatFpsComplaintDetail(ModelRepeatFpsComplaintsList model){
        Intent intent = new Intent(mContext, RepeatFpsComplaintDetailActivity.class);
        intent.putExtra("param", model);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        } /*else if (id == R.id.buttonPDF){
            if (Constants.modelRepeatFpsComplaintsList != null && Constants.modelRepeatFpsComplaintsList.size() > 0){
                startActivity(new Intent(mContext, FPSRepeatOnServiceCenterPDFActivity.class));
                finish();
            }
        }*/
    }

}