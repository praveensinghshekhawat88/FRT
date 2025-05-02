package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.callmangement.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.EHR.adapter.SurveyFormListingAdapter;
import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.databinding.ActivitySurveyFormListingBinding;
import com.callmangement.EHR.models.ModelDeleteASurveyForm;
import com.callmangement.EHR.models.ModelGetSurveyFormDocList;
import com.callmangement.EHR.models.ModelSurveyFormListing;
import com.callmangement.EHR.models.SurveyFormDetailsInfo;
import com.callmangement.EHR.models.SurveyFormDocInfo;
import com.callmangement.EHR.support.EqualSpacingItemDecoration;
import com.callmangement.EHR.support.Preference;
import com.callmangement.EHR.support.Utils;
import com.callmangement.utils.PrefManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyFormsListingActivity extends BaseActivity {

    Activity mActivity;
    private ActivitySurveyFormListingBinding binding;

    PrefManager preference;

    private List<SurveyFormDetailsInfo> campDetails_infoArrayList = new ArrayList<>();
    private SurveyFormListingAdapter adapter;

    public final int LAUNCH_SURVEY_FORM_FILLING_ACTIVITY = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySurveyFormListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.manage_survey_forms));

        campDetails_infoArrayList = new ArrayList<>();
        adapter = new SurveyFormListingAdapter(mActivity, campDetails_infoArrayList, onItemUploadClickListener,
                onItemDownloadClickListener, onItemViewClickListener, onItemDeleteClickListener, onItemEditClickListener);
        binding.rvAllSurveyForms.setHasFixedSize(true);
        binding.rvAllSurveyForms.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        binding.rvAllSurveyForms.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAllSurveyForms.setAdapter(adapter);

        setClickListener();

        getSurveyForms("0", "", "", "", "0");
    }


    SurveyFormListingAdapter.OnItemViewClickListener onItemViewClickListener = new SurveyFormListingAdapter.OnItemViewClickListener() {
        @Override
        public void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position) {
            String USER_Id = preference.getUSER_Id();
            viewUploadedImages(USER_Id, surveyFormDetailsInfo);
        }
    };

    private void viewUploadedImages(String UserID, SurveyFormDetailsInfo surveyFormDetailsInfo) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = GetSurveyFormDocListQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], surveyFormDetailsInfo.getServeyFormId());

            Call<ModelGetSurveyFormDocList> call = apiInterface.callGetSurveyFormDocListApi(GetSurveyFormDocList(), paramData);
            call.enqueue(new Callback<ModelGetSurveyFormDocList>() {
                @Override
                public void onResponse(@NonNull Call<ModelGetSurveyFormDocList> call, @NonNull Response<ModelGetSurveyFormDocList> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    ArrayList<SurveyFormDocInfo> campDocInfoArrayList = new ArrayList<>();

                                    if(response.body().getList() != null && response.body().getList().size() > 0) {

                                        for (int i =0; i < response.body().getList().size(); i++) {

                                            SurveyFormDocInfo surveyFormDocInfo = response.body().getList().get(i);

                                            String imagePath = BaseUrl() + surveyFormDocInfo.getDocumentPath();
                                            surveyFormDocInfo.setDocumentPath(imagePath);

                                            campDocInfoArrayList.add(surveyFormDocInfo);
                                        }

                                        Intent intent = new Intent(mActivity, ViewSurveyFormDetailsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("mylist", campDocInfoArrayList);
                                        bundle.putSerializable("surveyFormDetails", surveyFormDetailsInfo);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }
                                    else {

                                        Intent intent = new Intent(mActivity, ViewSurveyFormDetailsActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("mylist", campDocInfoArrayList);
                                        bundle.putSerializable("surveyFormDetails", surveyFormDetailsInfo);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }

                                } else {
                                    makeToast(response.body().getMessage());
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelGetSurveyFormDocList> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }

    SurveyFormListingAdapter.OnItemEditClickListener onItemEditClickListener = new SurveyFormListingAdapter.OnItemEditClickListener() {
        @Override
        public void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position) {
            Intent intent = new Intent(mActivity, EditSurveyFormActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("surveyFormDetails", surveyFormDetailsInfo);
            intent.putExtras(bundle);
            startActivityForResult(intent, LAUNCH_SURVEY_FORM_FILLING_ACTIVITY);
        }
    };

    SurveyFormListingAdapter.OnItemDeleteClickListener onItemDeleteClickListener = new SurveyFormListingAdapter.OnItemDeleteClickListener() {
        @Override
        public void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.delete_survey_form_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        String USER_Id = preference.getUSER_Id();
                        deleteASurveyForm(USER_Id, surveyFormDetailsInfo.getServeyFormId(), surveyFormDetailsInfo.getTicketNo());

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    };

    private void deleteASurveyForm(String UserID, String ServeyFormId, String TicketNo) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = DeleteASurveyFormQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], ServeyFormId);
            paramData.put(splitArray[2], TicketNo);

            Call<ModelDeleteASurveyForm> call = apiInterface.callDeleteASurveyFormApi(DeleteASurveyForm(), paramData);
            call.enqueue(new Callback<ModelDeleteASurveyForm>() {
                @Override
                public void onResponse(@NonNull Call<ModelDeleteASurveyForm> call, @NonNull Response<ModelDeleteASurveyForm> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(response.body().getMessage());

                                    campDetails_infoArrayList = new ArrayList<>();
                                    adapter.setData(campDetails_infoArrayList);

                                    getSurveyForms("0", "", "", "", "0");

                                } else {
                                    makeToast(response.body().getMessage());
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelDeleteASurveyForm> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }


    SurveyFormListingAdapter.OnItemUploadClickListener onItemUploadClickListener = new SurveyFormListingAdapter.OnItemUploadClickListener() {
        @Override
        public void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.upload_survey_form_sheet_for_survey_form_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        Intent intent = new Intent(mActivity, UploadSurveyFormReportActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ServeyFormId", surveyFormDetailsInfo.getServeyFormId());
                        bundle.putString("TicketNo", surveyFormDetailsInfo.getTicketNo());
                        intent.putExtras(bundle);
                        startActivity(intent);

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }
    };

    SurveyFormListingAdapter.OnItemDownloadClickListener onItemDownloadClickListener = new SurveyFormListingAdapter.OnItemDownloadClickListener() {
        @Override
        public void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getResources().getString(R.string.download_survey_form_now))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                        dialog.cancel();

                        Intent intent = new Intent(mActivity, SurveyFormPdfActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("surveyFormDetailsInfo", surveyFormDetailsInfo);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, i) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();

        }
    };

    private void getSurveyForms(final String ServeyFormId, final String TicketNo, final String StartDate, final String EndDate, final String StatusId) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String USER_Id = preference.getUSER_Id();

            String paramStr = GetSurveyFormListQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], USER_Id);
            paramData.put(splitArray[1], ServeyFormId);
            paramData.put(splitArray[2], TicketNo);
            paramData.put(splitArray[3], StartDate);
            paramData.put(splitArray[4], EndDate);
            paramData.put(splitArray[5], StatusId);

            Call<ModelSurveyFormListing> call = apiInterface.callSurveyFormListApi(GetSurveyFormList(), paramData);
            call.enqueue(new Callback<ModelSurveyFormListing>() {
                @Override
                public void onResponse(@NonNull Call<ModelSurveyFormListing> call, @NonNull Response<ModelSurveyFormListing> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    if (response.body().getList().size() > 0) {
                                        binding.textNoCampSchedule.setVisibility(View.GONE);
                                        binding.rvAllSurveyForms.setVisibility(View.VISIBLE);
                                        adapter.setData(response.body().getList());
                                    } else {
                                        binding.textNoCampSchedule.setVisibility(View.VISIBLE);
                                        binding.rvAllSurveyForms.setVisibility(View.GONE);
                                    }

                                } else {
                                    makeToast(response.body().getMessage());
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSurveyFormListing> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    private void setClickListener() {

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());

        binding.actionBar.ivThreeDot.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(SurveyFormsListingActivity.this, binding.actionBar.ivThreeDot);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(getResources().getString(R.string.do_you_want_to_logout_from_this_app))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.logout), (dialog, id) -> {
                            dialog.cancel();
                            //logout();
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
                return true;
            });
            popupMenu.show();
        });

        binding.buttonFillNewSurveyForm.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, FillNewSurveyFormActivity.class);
            startActivityForResult(intent, LAUNCH_SURVEY_FORM_FILLING_ACTIVITY);
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SURVEY_FORM_FILLING_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                campDetails_infoArrayList = new ArrayList<>();
                adapter.setData(campDetails_infoArrayList);

                getSurveyForms("0", "", "", "", "0");
            }
        }
    }

}