package com.callmangement.ehr.ehrActivities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import com.callmangement.R
import com.callmangement.databinding.ActivityCreateNewCampBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.ModelCreateACamp
import com.callmangement.ehr.models.ModelTehsil
import com.callmangement.ehr.models.ModelTehsilList
import com.callmangement.ehr.support.Utils.convertStringToUTF8
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
import java.util.Collections
import java.util.Locale
import java.util.Objects

class CreateNewCampActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivityCreateNewCampBinding? = null
    private var checkTehsil = 0
    private var tehsilNameEng: String? = ""
    private var tehsilId: String? = "0"
    private var tehsil_list: MutableList<ModelTehsilList?> = ArrayList()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private var preference: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityCreateNewCampBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun setUpData() {
        tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"

        Collections.reverse(tehsil_list)
        val l = ModelTehsilList()
        l.tehsilId = (-1).toString()
        l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
        tehsil_list.add(l)
        tehsil_list.reverse()
        val dataAdapter = ArrayAdapter(
            mActivity!!, android.R.layout.simple_spinner_item, tehsil_list
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerTehsil.adapter = dataAdapter
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.create_new_camp)
        binding!!.inputDescription.text = "pos shivir"
        setUpData()
        setClickListener()

        val USER_DISTRICT_Id = preference!!.useR_DistrictId
        tehsilList(USER_DISTRICT_Id)
    }


    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private fun tehsilList(districtId: String?) {
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

            val paramStr = TehsilListByDistictQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = districtId

            val call = apiInterface.callTehsilListApiByDistict(TehsilListByDistict(), paramData)
            call!!.enqueue(object : Callback<ModelTehsil?> {
                override fun onResponse(
                    call: Call<ModelTehsil?>,
                    response: Response<ModelTehsil?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    tehsil_list = response.body()!!.tehsil_List
                                    if (tehsil_list != null && tehsil_list.size > 0) {
                                        Collections.reverse(tehsil_list)
                                        val l = ModelTehsilList()
                                        l.tehsilId = (-1).toString()
                                        l.tehsilNameEng =
                                            "--" + resources.getString(R.string.tehsil) + "--"
                                        tehsil_list.add(l)
                                        tehsil_list.reverse()
                                        val dataAdapter = ArrayAdapter(
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item,
                                            tehsil_list
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        binding!!.spinnerTehsil.adapter = dataAdapter
                                    }
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

                override fun onFailure(call: Call<ModelTehsil?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
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

        binding!!.actionBar.ivThreeDot.setOnClickListener { view: View? ->
            val popupMenu = PopupMenu(
                mActivity!!, binding!!.actionBar.ivThreeDot
            )
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                val builder = AlertDialog.Builder(
                    mActivity!!
                )
                builder.setMessage(resources.getString(R.string.do_you_want_to_logout_from_this_app))
                    .setCancelable(false)
                    .setPositiveButton(
                        resources.getString(R.string.logout)
                    ) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        resources.getString(R.string.cancel)
                    ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
                true
            }
            popupMenu.show()
        }

        //        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (++checkDistrict > 1) {
//                    districtNameEng = district_List.get(i).getDistrictNameEng();
//                    districtId = district_List.get(i).getDistrictId();
//                    if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
//                        tehsil_list.clear();
//                        Collections.reverse(tehsil_list);
//                        ModelTehsilList list = new ModelTehsilList();
//                        list.setTehsilId(String.valueOf(-1));
//                        list.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
//                        tehsil_list.add(list);
//                        Collections.reverse(tehsil_list);
//                        ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, tehsil_list);
//                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        binding.spinnerTehsil.setAdapter(dataAdapter);
//                    } else {
//                        tehsilList(districtId);
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
        binding!!.spinnerTehsil.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkTehsil > 1) {
                        tehsilNameEng = tehsil_list[i]!!.tehsilNameEng
                        tehsilId = tehsil_list[i]!!.tehsilId
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.textStartDateTime.setOnClickListener { view: View? ->
            val dateFromDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarFromDate[Calendar.YEAR] =
                        year
                    myCalendarFromDate[Calendar.MONTH] = monthOfYear
                    myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    timePickerFromDate()
                }
            val datePickerDialog = DatePickerDialog(
                mActivity!!, R.style.DialogThemeTwo, dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding!!.textEndDateTime.setOnClickListener { view: View? ->
            val dateToDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    timePickerToDate()
                }
            val datePickerDialog = DatePickerDialog(
                mActivity!!,
                R.style.DialogThemeTwo,
                dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding!!.buttonCreateTraining.setOnClickListener { view: View? ->
            val startDateTime = Objects.requireNonNull(
                binding!!.textStartDateTime.text
            ).toString().trim { it <= ' ' }
            val endDateTime =
                Objects.requireNonNull(binding!!.textEndDateTime.text)
                    .toString().trim { it <= ' ' }
            val address = binding!!.inputPlace.text.toString().trim { it <= ' ' }
            val description = binding!!.inputDescription.text.toString().trim { it <= ' ' }
            if (tehsilNameEng.equals(
                    "--" + resources.getString(R.string.tehsil) + "--",
                    ignoreCase = true
                )
            ) {
                makeToast(resources.getString(R.string.please_select_tehsil))
            } else if (TextUtils.isEmpty(startDateTime)) {
                makeToast(resources.getString(R.string.please_select_training_start_date))
            } /* else if (TextUtils.isEmpty(endDateTime)) {
                makeToast(getResources().getString(R.string.please_select_training_end_date));
            }*/ else if (TextUtils.isEmpty(address)) {
                makeToast(resources.getString(R.string.please_input_training_address))
            } else if (TextUtils.isEmpty(description)) {
                makeToast(resources.getString(R.string.please_input_training_description))
            } else {
                val USER_Id = preference!!.useR_Id
                val USER_DISTRICT_Id = preference!!.useR_DistrictId

                saveTraining(
                    USER_Id,
                    USER_DISTRICT_Id,
                    tehsilId,
                    startDateTime,
                    endDateTime,
                    convertStringToUTF8(address),
                    convertStringToUTF8(description)
                )
            }
        }
    }

    private fun saveTraining(
        UserID: String,
        DistrictID: String?,
        TehsilID: String?,
        StartedDate: String,
        EndDate: String,
        Address: String,
        Description: String
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
            val paramStr = CreateCampQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = DistrictID
            paramData[splitArray[2]] = TehsilID
            paramData[splitArray[3]] = Description
            paramData[splitArray[4]] = StartedDate
            paramData[splitArray[5]] = EndDate
            paramData[splitArray[6]] = Address

            val call = apiInterface.callCreateACampApi(CreateCamp(), paramData)
            call!!.enqueue(object : Callback<ModelCreateACamp?> {
                override fun onResponse(
                    call: Call<ModelCreateACamp?>,
                    response: Response<ModelCreateACamp?>
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

                override fun onFailure(call: Call<ModelCreateACamp?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun updateLabelFromDate(time: String) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textStartDateTime.setText(sdf.format(myCalendarFromDate.time) + " " + time)
        binding!!.textEndDateTime.text!!.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelToDate(time: String) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textEndDateTime.setText(sdf.format(myCalendarToDate.time) + " " + time)
    }

    private fun timePickerFromDate() {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val second = mcurrentTime[Calendar.SECOND]
        val mTimePicker = TimePickerDialog(
            this,
            { timePicker: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                @SuppressLint("DefaultLocale") val timeStr =
                    String.format("%02d:%02d:%02d", selectedHour, selectedMinute, second)
                updateLabelFromDate(timeStr)
            },
            hour,
            minute,
            false
        )
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    private fun timePickerToDate() {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val second = mcurrentTime[Calendar.SECOND]
        val mTimePicker = TimePickerDialog(
            this,
            { timePicker: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                @SuppressLint("DefaultLocale") val timeStr =
                    String.format("%02d:%02d:%02d", selectedHour, selectedMinute, second)
                updateLabelToDate(timeStr)
            },
            hour,
            minute,
            false
        )
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }
}