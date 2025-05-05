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
import com.callmangement.databinding.ActivityEditSurveyFormBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.ModelEditASurveyForm
import com.callmangement.ehr.models.SurveyFormDetailsInfo
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class EditSurveyFormActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivityEditSurveyFormBinding? = null
    private var preference: PrefManager? = null
    private val myCalendarPODate: Calendar = Calendar.getInstance()
    private var surveyFormDetailsInfo: SurveyFormDetailsInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityEditSurveyFormBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun setUpData() {
        if (surveyFormDetailsInfo!!.customerName != null) binding!!.inputCustomerName.setText(
            surveyFormDetailsInfo!!.customerName
        )

        if (surveyFormDetailsInfo!!.bill_ChallanNo != null) binding!!.inputBillChallanNo.setText(
            surveyFormDetailsInfo!!.bill_ChallanNo
        )

        if (surveyFormDetailsInfo!!.address != null) binding!!.inputCustomerAddress.setText(
            surveyFormDetailsInfo!!.address
        )

        if (surveyFormDetailsInfo!!.billRemark != null) binding!!.inputBillRemarks.setText(
            surveyFormDetailsInfo!!.billRemark
        )

        if (surveyFormDetailsInfo!!.pointOfContact != null) binding!!.inputPointOfContact.setText(
            surveyFormDetailsInfo!!.pointOfContact
        )

        if (surveyFormDetailsInfo!!.mobileNumber != null) binding!!.inputMobileNumber.setText(
            surveyFormDetailsInfo!!.mobileNumber
        )

        if (surveyFormDetailsInfo!!.installationDateStr != null) {
            val input = SimpleDateFormat("dd-MM-yyyy")
            val output = SimpleDateFormat("yyyy-MM-dd")
            try {
                val oneWayTripDate =
                    input.parse(surveyFormDetailsInfo!!.installationDateStr) // parse input
                binding!!.inputDateOfInstallation.setText(output.format(oneWayTripDate)) // format output
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        if (surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.typeOfCallId != null) {
            if (surveyFormDetailsInfo!!.typeOfCallId!!.contains(",")) {
                val result = surveyFormDetailsInfo!!.typeOfCallId!!.split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                val typeOfCallIdList = ArrayList(Arrays.asList(*result))

                binding!!.checkBoxSupply.isChecked = typeOfCallIdList.contains("1")

                if (typeOfCallIdList.contains("2")) {
                    binding!!.checkBoxWarranty.isChecked = true
                } else {
                    binding!!.checkBoxSupply.isChecked = false
                }

                binding!!.checkBoxInstallation.isChecked = typeOfCallIdList.contains("3")

                binding!!.checkBoxPayable.isChecked = typeOfCallIdList.contains("4")

                binding!!.checkBoxCourtesy.isChecked = typeOfCallIdList.contains("5")
            } else {
                if (surveyFormDetailsInfo!!.typeOfCallId!!.length > 0) {
                    binding!!.checkBoxSupply.isChecked =
                        surveyFormDetailsInfo!!.typeOfCallId.equals("1", ignoreCase = true)

                    if (surveyFormDetailsInfo!!.typeOfCallId.equals("2", ignoreCase = true)) {
                        binding!!.checkBoxWarranty.isChecked = true
                    } else {
                        binding!!.checkBoxSupply.isChecked = false
                    }

                    binding!!.checkBoxInstallation.isChecked =
                        surveyFormDetailsInfo!!.typeOfCallId.equals("3", ignoreCase = true)

                    binding!!.checkBoxPayable.isChecked =
                        surveyFormDetailsInfo!!.typeOfCallId.equals("4", ignoreCase = true)

                    binding!!.checkBoxCourtesy.isChecked =
                        surveyFormDetailsInfo!!.typeOfCallId.equals("5", ignoreCase = true)
                } else {
                    binding!!.checkBoxSupply.isChecked = false
                    binding!!.checkBoxSupply.isChecked = false
                    binding!!.checkBoxInstallation.isChecked = false
                    binding!!.checkBoxPayable.isChecked = false
                    binding!!.checkBoxCourtesy.isChecked = false
                }
            }
        }

        if (surveyFormDetailsInfo!!.itemDetail != null) binding!!.inputItemDetails.setText(
            surveyFormDetailsInfo!!.itemDetail
        )

        if (surveyFormDetailsInfo!!.purchaseOrderDtl != null) binding!!.inputPurchaseOrderDetails.setText(
            surveyFormDetailsInfo!!.purchaseOrderDtl
        )

        if (surveyFormDetailsInfo!!.installedMachineSpecification != null) binding!!.inputSpecificationOfMachineInstalled.setText(
            surveyFormDetailsInfo!!.installedMachineSpecification
        )

        if (surveyFormDetailsInfo!!.accessesory != null) binding!!.inputAnyAccessory.setText(
            surveyFormDetailsInfo!!.accessesory
        )

        if (surveyFormDetailsInfo!!.installationDone != null) binding!!.inputInstallationDone.setText(
            surveyFormDetailsInfo!!.installationDone
        )

        if (surveyFormDetailsInfo!!.customer_Remark != null) binding!!.inputCustomerRemarks.setText(
            surveyFormDetailsInfo!!.customer_Remark
        )

        if (surveyFormDetailsInfo!!.engineerName != null) binding!!.inputEngineerName.setText(
            surveyFormDetailsInfo!!.engineerName
        )
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        val bundle = intent.extras
        surveyFormDetailsInfo =
            bundle!!.getSerializable("surveyFormDetails") as SurveyFormDetailsInfo?

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.fill_edit_survey_form)

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
                mActivity!!, dateFromDate, myCalendarPODate[Calendar.YEAR],
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
                editSurveyForm(
                    USER_Id, surveyFormDetailsInfo!!.serveyFormId, surveyFormDetailsInfo!!.ticketNo,
                    bill_challan_no, customer_address, point_of_contact, bill_remarks,
                    mobile_number, date_of_installation, type_of_call_id_combined, item_details,
                    purchase_order_details, specification_of_machine_installed, any_accessory,
                    installation_done, customer_remarks, customer_name, engineer_name
                )
            }
        }
    }

    private fun editSurveyForm(
        UserID: String,
        ServeyFormId: String?,
        TicketNo: String?,
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

            val paramStr = EditSurveyFormQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = ServeyFormId
            paramData[splitArray[2]] = TicketNo
            paramData[splitArray[3]] = Bill_ChallanNo
            paramData[splitArray[4]] = Address
            paramData[splitArray[5]] = PointOfContact
            paramData[splitArray[6]] = BillRemark
            paramData[splitArray[7]] = MobileNumber
            paramData[splitArray[8]] = InstallationDateStr
            paramData[splitArray[9]] = TypeOfCallId
            paramData[splitArray[10]] = ItemDetail
            paramData[splitArray[11]] = PurchaseOrderDtl
            paramData[splitArray[12]] = InstalledMachineSpecification
            paramData[splitArray[13]] = Accessesory
            paramData[splitArray[14]] = InstallationDone
            paramData[splitArray[15]] = Customer_Remark
            paramData[splitArray[16]] = CustomerName
            paramData[splitArray[17]] = EngineerName

            val call = apiInterface.callEditSurveyFormApi(EditSurveyForm(), paramData)
            call!!.enqueue(object : Callback<ModelEditASurveyForm?> {
                override fun onResponse(
                    call: Call<ModelEditASurveyForm?>,
                    response: Response<ModelEditASurveyForm?>
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

                override fun onFailure(call: Call<ModelEditASurveyForm?>, error: Throwable) {
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