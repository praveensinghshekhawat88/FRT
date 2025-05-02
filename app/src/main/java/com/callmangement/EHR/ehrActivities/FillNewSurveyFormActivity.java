package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.databinding.ActivityFillNewSurveyFormBinding;
import com.callmangement.EHR.models.ModelCreateASurveyForm;
import com.callmangement.EHR.support.Preference;
import com.callmangement.EHR.support.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import com.callmangement.R;
import com.callmangement.utils.PrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FillNewSurveyFormActivity extends BaseActivity {

    Activity mActivity;
    private ActivityFillNewSurveyFormBinding binding;

    PrefManager preference;

    private final Calendar myCalendarPODate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityFillNewSurveyFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void setUpData() {

        binding.inputCustomerName.setText("");
        binding.inputBillChallanNo.setText("");
        binding.inputCustomerAddress.setText("");
        binding.inputBillRemarks.setText("");
        binding.inputPointOfContact.setText("");
        binding.inputMobileNumber.setText("");
        binding.inputDateOfInstallation.setText("");
        binding.inputItemDetails.setText("");
        binding.inputPurchaseOrderDetails.setText("");
        binding.inputSpecificationOfMachineInstalled.setText("");
        binding.inputAnyAccessory.setText("");
        binding.inputInstallationDone.setText("");
        binding.inputCustomerRemarks.setText("");
        binding.inputEngineerName.setText("");


    }

    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.fill_new_survey_form));

        setUpData();
        setClickListener();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void setClickListener() {

        binding.actionBar.ivBack.setOnClickListener(view ->
        {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();

        });

        binding.framedateOfInstallation.setOnClickListener(view -> {
            DatePickerDialog.OnDateSetListener dateFromDate = (view1, year, monthOfYear, dayOfMonth) -> {
                myCalendarPODate.set(Calendar.YEAR, year);
                myCalendarPODate.set(Calendar.MONTH, monthOfYear);
                myCalendarPODate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                binding.inputDateOfInstallation.setText(sdf.format(myCalendarPODate.getTime()));

            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, R.style.DialogThemeTwo, dateFromDate, myCalendarPODate
                    .get(Calendar.YEAR), myCalendarPODate.get(Calendar.MONTH),
                    myCalendarPODate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        binding.buttonFillNewSurveyForm.setOnClickListener(view -> {
            String customer_name = Objects.requireNonNull(binding.inputCustomerName.getText()).toString().trim();
            String bill_challan_no = Objects.requireNonNull(binding.inputBillChallanNo.getText()).toString().trim();
            String customer_address = Objects.requireNonNull(binding.inputCustomerAddress.getText()).toString().trim();
            String bill_remarks = Objects.requireNonNull(binding.inputBillRemarks.getText()).toString().trim();
            String point_of_contact = Objects.requireNonNull(binding.inputPointOfContact.getText()).toString().trim();
            String mobile_number = Objects.requireNonNull(binding.inputMobileNumber.getText()).toString().trim();
            String date_of_installation = Objects.requireNonNull(binding.inputDateOfInstallation.getText()).toString().trim();
            String item_details = Objects.requireNonNull(binding.inputItemDetails.getText()).toString().trim();
            String purchase_order_details = Objects.requireNonNull(binding.inputPurchaseOrderDetails.getText()).toString().trim();
            String specification_of_machine_installed = Objects.requireNonNull(binding.inputSpecificationOfMachineInstalled.getText()).toString().trim();
            String any_accessory = Objects.requireNonNull(binding.inputAnyAccessory.getText()).toString().trim();
            String installation_done = Objects.requireNonNull(binding.inputInstallationDone.getText()).toString().trim();
            String customer_remarks = Objects.requireNonNull(binding.inputCustomerRemarks.getText()).toString().trim();
            String engineer_name = Objects.requireNonNull(binding.inputEngineerName.getText()).toString().trim();

            if (TextUtils.isEmpty(customer_name)) {
                makeToast(getResources().getString(R.string.please_input_customer_name));
            } else if (TextUtils.isEmpty(bill_challan_no)) {
                makeToast(getResources().getString(R.string.please_input_bill_challan_no));
            } else if (TextUtils.isEmpty(customer_address)) {
                makeToast(getResources().getString(R.string.please_input_customer_address));
            } else if (TextUtils.isEmpty(bill_remarks)) {
                makeToast(getResources().getString(R.string.please_input_bill_remarks));
            } else if (TextUtils.isEmpty(point_of_contact)) {
                makeToast(getResources().getString(R.string.please_input_point_of_contact));
            } else if (TextUtils.isEmpty(mobile_number)) {
                makeToast(getResources().getString(R.string.please_input_mobile_number));
            } else if (TextUtils.isEmpty(date_of_installation)) {
                makeToast(getResources().getString(R.string.please_input_date_of_installation));
            } else if (!binding.checkBoxSupply.isChecked()
                    && !binding.checkBoxWarranty.isChecked()
                    && !binding.checkBoxInstallation.isChecked()
                    && !binding.checkBoxPayable.isChecked()
                    && !binding.checkBoxCourtesy.isChecked()) {
                makeToast(getResources().getString(R.string.please_select_type_of_call));
            } else if (TextUtils.isEmpty(item_details)) {
                makeToast(getResources().getString(R.string.please_input_item_details));
            } else if (TextUtils.isEmpty(purchase_order_details)) {
                makeToast(getResources().getString(R.string.please_input_purchase_order_details));
            } else if (TextUtils.isEmpty(specification_of_machine_installed)) {
                makeToast(getResources().getString(R.string.please_input_specification_of_machine_installed));
            } else if (TextUtils.isEmpty(any_accessory)) {
                makeToast(getResources().getString(R.string.please_input_any_accessory));
            } else if (TextUtils.isEmpty(installation_done)) {
                makeToast(getResources().getString(R.string.please_input_installation_done));
            } else if (TextUtils.isEmpty(customer_remarks)) {
                makeToast(getResources().getString(R.string.please_input_customer_remarks));
            } else if (TextUtils.isEmpty(customer_name)) {
                makeToast(getResources().getString(R.string.please_input_customer_name));
            } else if (TextUtils.isEmpty(engineer_name)) {
                makeToast(getResources().getString(R.string.please_input_engineer_name));
            } else {

                String type_of_call_combined = "";
                String type_of_call_id_combined = "";

                ArrayList<String> arrayListCheckedName = new ArrayList<>();
                ArrayList<String> arrayListCheckedId = new ArrayList<>();

                if (binding.checkBoxSupply.isChecked()) {
                    arrayListCheckedName.add("Supply");
                    arrayListCheckedId.add("1");
                }

                if (binding.checkBoxWarranty.isChecked()) {
                    arrayListCheckedName.add("Warranty");
                    arrayListCheckedId.add("2");
                }

                if (binding.checkBoxInstallation.isChecked()) {
                    arrayListCheckedName.add("Installation");
                    arrayListCheckedId.add("3");
                }

                if (binding.checkBoxPayable.isChecked()) {
                    arrayListCheckedName.add("Payable");
                    arrayListCheckedId.add("4");
                }

                if (binding.checkBoxCourtesy.isChecked()) {
                    arrayListCheckedName.add("Courtesy");
                    arrayListCheckedId.add("5");
                }

                if (arrayListCheckedName.size() > 1) {

                    for (int i = 0; i < arrayListCheckedName.size(); i++) {
                        if (i == 0) {
                            type_of_call_combined = arrayListCheckedName.get(i);
                            type_of_call_id_combined = arrayListCheckedId.get(i);
                        }
                        else {
                            type_of_call_combined = type_of_call_combined + "," + arrayListCheckedName.get(i);
                            type_of_call_id_combined = type_of_call_id_combined + "," + arrayListCheckedId.get(i);
                        }
                    }

                } else {
                    if (arrayListCheckedName.size() == 1) {
                        type_of_call_combined = arrayListCheckedName.get(0);
                        type_of_call_id_combined = arrayListCheckedId.get(0);
                    }
                }

                String USER_Id = preference.getUSER_Id();
                saveSurveyForm(USER_Id, bill_challan_no, customer_address, point_of_contact, bill_remarks,
                        mobile_number, date_of_installation, type_of_call_id_combined, item_details,
                        purchase_order_details, specification_of_machine_installed, any_accessory,
                        installation_done, customer_remarks, customer_name, engineer_name);
            }

        });
    }

    private void saveSurveyForm(String UserID, String Bill_ChallanNo, String Address, String PointOfContact, String BillRemark,
                                String MobileNumber, String InstallationDateStr, String TypeOfCallId, String ItemDetail,
                                String PurchaseOrderDtl, String InstalledMachineSpecification, String Accessesory, String InstallationDone,
                                String Customer_Remark, String CustomerName, String EngineerName) {

        if (Utils.isNetworkAvailable(mActivity)) {

            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);

            String paramStr = CreateSurveyFormQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], UserID);
            paramData.put(splitArray[1], Bill_ChallanNo);
            paramData.put(splitArray[2], Address);
            paramData.put(splitArray[3], PointOfContact);
            paramData.put(splitArray[4], BillRemark);
            paramData.put(splitArray[5], MobileNumber);
            paramData.put(splitArray[6], InstallationDateStr);
            paramData.put(splitArray[7], TypeOfCallId);
            paramData.put(splitArray[8], ItemDetail);
            paramData.put(splitArray[9], PurchaseOrderDtl);
            paramData.put(splitArray[10], InstalledMachineSpecification);
            paramData.put(splitArray[11], Accessesory);
            paramData.put(splitArray[12], InstallationDone);
            paramData.put(splitArray[13], Customer_Remark);
            paramData.put(splitArray[14], CustomerName);
            paramData.put(splitArray[15], EngineerName);

            Call<ModelCreateASurveyForm> call = apiInterface.callCreateSurveyFormApi(CreateSurveyForm(), paramData);
            call.enqueue(new Callback<ModelCreateASurveyForm>() {
                @Override
                public void onResponse(@NonNull Call<ModelCreateASurveyForm> call, @NonNull Response<ModelCreateASurveyForm> response) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    makeToast(response.body().getMessage());

                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
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
                public void onFailure(@NonNull Call<ModelCreateASurveyForm> call, @NonNull Throwable error) {
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


}