package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.Toast
import com.callmangement.R
import com.callmangement.databinding.ActivityFillNewSurveyFormBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.ModelCreateASurveyForm
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class FillNewSurveyFormActivity : BaseActivity() {

    private var mActivity: Activity? = null
    private var binding: ActivityFillNewSurveyFormBinding? = null
    private var preference: PrefManager? = null
    private val myCalendarPODate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityFillNewSurveyFormBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun setUpData() {
        binding!!.inputCustomerName.setText("")
        binding!!.inputBillChallanNo.setText("")
        binding!!.inputCustomerAddress.setText("")
        binding!!.inputBillRemarks.setText("")
        binding!!.inputPointOfContact.setText("")
        binding!!.inputMobileNumber.setText("")
        binding!!.inputDateOfInstallation.setText("")
        binding!!.inputItemDetails.setText("")
        binding!!.inputPurchaseOrderDetails.setText("")
        binding!!.inputSpecificationOfMachineInstalled.setText("")
        binding!!.inputAnyAccessory.setText("")
        binding!!.inputInstallationDone.setText("")
        binding!!.inputCustomerRemarks.setText("")
        binding!!.inputEngineerName.setText("")
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.fill_new_survey_form)

        setUpData()
        setClickListener()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { view: View? ->
            val returnIntent = Intent()
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }

        binding!!.framedateOfInstallation.setOnClickListener { view: View? ->
            val dateFromDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarPODate[Calendar.YEAR] =
                        year
                    myCalendarPODate[Calendar.MONTH] = monthOfYear
                    myCalendarPODate[Calendar.DAY_OF_MONTH] = dayOfMonth

                    val myFormat = "yyyy-MM-dd"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    binding!!.inputDateOfInstallation.setText(sdf.format(myCalendarPODate.time))
                }
            val datePickerDialog = DatePickerDialog(
                mActivity!!,
                R.style.DialogThemeTwo,
                dateFromDate,
                myCalendarPODate[Calendar.YEAR],
                myCalendarPODate[Calendar.MONTH],
                myCalendarPODate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding!!.buttonFillNewSurveyForm.setOnClickListener { view: View? ->
            val customer_name = Objects.requireNonNull(
                binding!!.inputCustomerName.text
            ).toString().trim { it <= ' ' }
            val bill_challan_no =
                Objects.requireNonNull(binding!!.inputBillChallanNo.text)
                    .toString().trim { it <= ' ' }
            val customer_address =
                Objects.requireNonNull(binding!!.inputCustomerAddress.text)
                    .toString().trim { it <= ' ' }
            val bill_remarks =
                Objects.requireNonNull(binding!!.inputBillRemarks.text)
                    .toString().trim { it <= ' ' }
            val point_of_contact =
                Objects.requireNonNull(binding!!.inputPointOfContact.text)
                    .toString().trim { it <= ' ' }
            val mobile_number =
                Objects.requireNonNull(binding!!.inputMobileNumber.text)
                    .toString().trim { it <= ' ' }
            val date_of_installation =
                Objects.requireNonNull(binding!!.inputDateOfInstallation.text)
                    .toString().trim { it <= ' ' }
            val item_details =
                Objects.requireNonNull(binding!!.inputItemDetails.text)
                    .toString().trim { it <= ' ' }
            val purchase_order_details = Objects.requireNonNull(
                binding!!.inputPurchaseOrderDetails.text
            ).toString().trim { it <= ' ' }
            val specification_of_machine_installed = Objects.requireNonNull(
                binding!!.inputSpecificationOfMachineInstalled.text
            ).toString().trim { it <= ' ' }
            val any_accessory =
                Objects.requireNonNull(binding!!.inputAnyAccessory.text)
                    .toString().trim { it <= ' ' }
            val installation_done =
                Objects.requireNonNull(binding!!.inputInstallationDone.text)
                    .toString().trim { it <= ' ' }
            val customer_remarks =
                Objects.requireNonNull(binding!!.inputCustomerRemarks.text)
                    .toString().trim { it <= ' ' }
            val engineer_name =
                Objects.requireNonNull(binding!!.inputEngineerName.text)
                    .toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(customer_name)) {
                makeToast(resources.getString(R.string.please_input_customer_name))
            } else if (TextUtils.isEmpty(bill_challan_no)) {
                makeToast(resources.getString(R.string.please_input_bill_challan_no))
            } else if (TextUtils.isEmpty(customer_address)) {
                makeToast(resources.getString(R.string.please_input_customer_address))
            } else if (TextUtils.isEmpty(bill_remarks)) {
                makeToast(resources.getString(R.string.please_input_bill_remarks))
            } else if (TextUtils.isEmpty(point_of_contact)) {
                makeToast(resources.getString(R.string.please_input_point_of_contact))
            } else if (TextUtils.isEmpty(mobile_number)) {
                makeToast(resources.getString(R.string.please_input_mobile_number))
            } else if (TextUtils.isEmpty(date_of_installation)) {
                makeToast(resources.getString(R.string.please_input_date_of_installation))
            } else if (!binding!!.checkBoxSupply.isChecked
                && !binding!!.checkBoxWarranty.isChecked
                && !binding!!.checkBoxInstallation.isChecked
                && !binding!!.checkBoxPayable.isChecked
                && !binding!!.checkBoxCourtesy.isChecked
            ) {
                makeToast(resources.getString(R.string.please_select_type_of_call))
            } else if (TextUtils.isEmpty(item_details)) {
                makeToast(resources.getString(R.string.please_input_item_details))
            } else if (TextUtils.isEmpty(purchase_order_details)) {
                makeToast(resources.getString(R.string.please_input_purchase_order_details))
            } else if (TextUtils.isEmpty(specification_of_machine_installed)) {
                makeToast(resources.getString(R.string.please_input_specification_of_machine_installed))
            } else if (TextUtils.isEmpty(any_accessory)) {
                makeToast(resources.getString(R.string.please_input_any_accessory))
            } else if (TextUtils.isEmpty(installation_done)) {
                makeToast(resources.getString(R.string.please_input_installation_done))
            } else if (TextUtils.isEmpty(customer_remarks)) {
                makeToast(resources.getString(R.string.please_input_customer_remarks))
            } else if (TextUtils.isEmpty(customer_name)) {
                makeToast(resources.getString(R.string.please_input_customer_name))
            } else if (TextUtils.isEmpty(engineer_name)) {
                makeToast(resources.getString(R.string.please_input_engineer_name))
            } else {
                var type_of_call_combined = ""
                var type_of_call_id_combined = ""

                val arrayListCheckedName = ArrayList<String>()
                val arrayListCheckedId = ArrayList<String>()

                if (binding!!.checkBoxSupply.isChecked) {
                    arrayListCheckedName.add("Supply")
                    arrayListCheckedId.add("1")
                }

                if (binding!!.checkBoxWarranty.isChecked) {
                    arrayListCheckedName.add("Warranty")
                    arrayListCheckedId.add("2")
                }

                if (binding!!.checkBoxInstallation.isChecked) {
                    arrayListCheckedName.add("Installation")
                    arrayListCheckedId.add("3")
                }

                if (binding!!.checkBoxPayable.isChecked) {
                    arrayListCheckedName.add("Payable")
                    arrayListCheckedId.add("4")
                }

                if (binding!!.checkBoxCourtesy.isChecked) {
                    arrayListCheckedName.add("Courtesy")
                    arrayListCheckedId.add("5")
                }

                if (arrayListCheckedName.size > 1) {
                    for (i in arrayListCheckedName.indices) {
                        if (i == 0) {
                            type_of_call_combined = arrayListCheckedName[i]
                            type_of_call_id_combined = arrayListCheckedId[i]
                        } else {
                            type_of_call_combined =
                                type_of_call_combined + "," + arrayListCheckedName[i]
                            type_of_call_id_combined =
                                type_of_call_id_combined + "," + arrayListCheckedId[i]
                        }
                    }
                } else {
                    if (arrayListCheckedName.size == 1) {
                        type_of_call_combined = arrayListCheckedName[0]
                        type_of_call_id_combined = arrayListCheckedId[0]
                    }
                }

                val USER_Id = preference!!.useR_Id
                saveSurveyForm(
                    USER_Id, bill_challan_no, customer_address, point_of_contact, bill_remarks,
                    mobile_number, date_of_installation, type_of_call_id_combined, item_details,
                    purchase_order_details, specification_of_machine_installed, any_accessory,
                    installation_done, customer_remarks, customer_name, engineer_name
                )
            }
        }
    }

    private fun saveSurveyForm(
        UserID: String,
        Bill_ChallanNo: String,
        Address: String,
        PointOfContact: String,
        BillRemark: String,
        MobileNumber: String,
        InstallationDateStr: String,
        TypeOfCallId: String,
        ItemDetail: String,
        PurchaseOrderDtl: String,
        InstalledMachineSpecification: String,
        Accessesory: String,
        InstallationDone: String,
        Customer_Remark: String,
        CustomerName: String,
        EngineerName: String
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )

            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )

            val paramStr = CreateSurveyFormQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = Bill_ChallanNo
            paramData[splitArray[2]] = Address
            paramData[splitArray[3]] = PointOfContact
            paramData[splitArray[4]] = BillRemark
            paramData[splitArray[5]] = MobileNumber
            paramData[splitArray[6]] = InstallationDateStr
            paramData[splitArray[7]] = TypeOfCallId
            paramData[splitArray[8]] = ItemDetail
            paramData[splitArray[9]] = PurchaseOrderDtl
            paramData[splitArray[10]] = InstalledMachineSpecification
            paramData[splitArray[11]] = Accessesory
            paramData[splitArray[12]] = InstallationDone
            paramData[splitArray[13]] = Customer_Remark
            paramData[splitArray[14]] = CustomerName
            paramData[splitArray[15]] = EngineerName

            val call = apiInterface.callCreateSurveyFormApi(CreateSurveyForm(), paramData)
            call!!.enqueue(object : Callback<ModelCreateASurveyForm?> {
                override fun onResponse(
                    call: Call<ModelCreateASurveyForm?>,
                    response: Response<ModelCreateASurveyForm?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.message)

                                    val returnIntent = Intent()
                                    setResult(RESULT_OK, returnIntent)
                                    finish()
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelCreateASurveyForm?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }
}