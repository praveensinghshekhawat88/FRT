package com.callmangement.ui.training_schedule

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityUpdateTrainingScheduleBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.model.tehsil.ModelTehsilList
import com.callmangement.model.training_schedule.ModelTrainingScheduleList
import com.callmangement.model.training_schedule.ModelUpdateTrainingSchedule
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.utils.Constants.convertStringToUTF8
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class UpdateTrainingScheduleActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityUpdateTrainingScheduleBinding? = null
    private var viewModel: TrainingScheduleViewModel? = null
    private var prefManager: PrefManager? = null
    private var districtId: String? = "0"
    private var checkDistrict = 0
    private var districtNameEng: String? = ""
    private var checkTehsil = 0
    private var tehsilNameEng: String? = ""
    private var tehsilId: String? = "0"
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var tehsil_list: MutableList<ModelTehsilList?> = ArrayList()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private var model: ModelTrainingScheduleList? = ModelTrainingScheduleList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_training_schedule)
        initView()
    }

    private fun initView() {
        onClickListener()
        intentData
        setUpData()
        districtList()
    }

    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("param") as ModelTrainingScheduleList?
        }

    private fun setUpData() {
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            TrainingScheduleViewModel::class.java
        )
        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"

        Collections.reverse(tehsil_list)
        val l = ModelTehsilList()
        l.tehsilId = (-1).toString()
        l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
        tehsil_list.add(l)
        Collections.reverse(tehsil_list)
        val dataAdapter = ArrayAdapter(
            mContext!!, android.R.layout.simple_spinner_item, tehsil_list
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerTehsil.adapter = dataAdapter

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.update_training_schedule)

        binding!!.textStartDateTime.setText(model!!.startDate)
        binding!!.textEndDateTime.setText(model!!.endDate)
        binding!!.inputPlace.setText(model!!.address)
        binding!!.inputDescription.setText(model!!.description)
        binding!!.inputTitle.setText(model!!.title)
        binding!!.inputTrainingNumber.text = model!!.trainingNo
    }

    private fun onClickListener() {
        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtNameEng = district_List!![i]!!.districtNameEng
                        districtId = district_List!![i]!!.districtId
                        if (districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            tehsil_list.clear()
                            Collections.reverse(tehsil_list)
                            val list =
                                ModelTehsilList()
                            list.tehsilId = (-1).toString()
                            list.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
                            tehsil_list.add(list)
                            Collections.reverse(tehsil_list)
                            val dataAdapter =
                                ArrayAdapter(
                                    mContext!!, android.R.layout.simple_spinner_item, tehsil_list
                                )
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding!!.spinnerTehsil.adapter = dataAdapter
                        } else {
                            tehsilList(districtId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

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

        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonCreateTraining.setOnClickListener(this)
        binding!!.inputTitle.setOnClickListener(this)
        binding!!.inputPlace.setOnClickListener(this)
        binding!!.inputDescription.setOnClickListener(this)
        binding!!.textStartDateTime.setOnClickListener(this)
        binding!!.textEndDateTime.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
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
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mCurrentTime[Calendar.MINUTE]
        val second = mCurrentTime[Calendar.SECOND]
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
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mCurrentTime[Calendar.MINUTE]
        val second = mCurrentTime[Calendar.SECOND]
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

    private fun updateTrainingSchedule() {
        val startDateTime =
            Objects.requireNonNull(binding!!.textStartDateTime.text).toString().trim { it <= ' ' }
        val endDateTime =
            Objects.requireNonNull(binding!!.textEndDateTime.text).toString().trim { it <= ' ' }
        val address =
            Objects.requireNonNull(binding!!.inputPlace.text).toString().trim { it <= ' ' }
        val description =
            Objects.requireNonNull(binding!!.inputDescription.text).toString().trim { it <= ' ' }
        val title = Objects.requireNonNull(binding!!.inputTitle.text).toString().trim { it <= ' ' }

        if (districtNameEng.equals(
                "--" + resources.getString(R.string.district) + "--",
                ignoreCase = true
            )
        ) {
            makeToast(resources.getString(R.string.please_select_district))
        } else if (tehsilNameEng.equals(
                "--" + resources.getString(R.string.tehsil) + "--",
                ignoreCase = true
            )
        ) {
            makeToast(resources.getString(R.string.please_select_tehsil))
        } else if (TextUtils.isEmpty(startDateTime)) {
            makeToast(resources.getString(R.string.please_select_training_start_date))
        } else if (TextUtils.isEmpty(endDateTime)) {
            makeToast(resources.getString(R.string.please_select_training_end_date))
        } else if (TextUtils.isEmpty(title)) {
            makeToast(resources.getString(R.string.please_input_training_title))
        } else if (TextUtils.isEmpty(address)) {
            makeToast(resources.getString(R.string.please_input_training_address))
        } else if (TextUtils.isEmpty(description)) {
            makeToast(resources.getString(R.string.please_input_training_description))
        } else {
            isLoading
            viewModel!!.updateTraining(
                prefManager!!.useR_Id,
                districtId,
                tehsilId,
                model!!.trainingId,
                startDateTime,
                endDateTime,
                model!!.trainingNo,
                convertStringToUTF8(address),
                convertStringToUTF8(description),
                convertStringToUTF8(title)
            ).observe(
                this,
                Observer<ModelUpdateTrainingSchedule?> { modelCreateTrainingSchedule: ModelUpdateTrainingSchedule? ->
                    isLoading
                    binding!!.inputTitle.isCursorVisible = false
                    binding!!.inputPlace.isCursorVisible = false
                    binding!!.inputDescription.isCursorVisible = false
                })
        }
    }

    private fun districtList() {
        isLoading
        viewModel!!.district.observe(
            this,
            Observer<ModelDistrict?> { modelDistrict: ModelDistrict? ->
                if (modelDistrict!!.status == "200") {
                    isLoading
                    district_List = modelDistrict.district_List
                    if (district_List != null && district_List!!.size > 0) {
                        Collections.reverse(district_List)
                        val l = ModelDistrictList()
                        l.districtId = (-1).toString()
                        l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                        district_List!!.add(l)
                        Collections.reverse(district_List)
                        val dataAdapter = ArrayAdapter(
                            mContext!!, android.R.layout.simple_spinner_item,
                            district_List!!
                        )
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding!!.spinnerDistrict.adapter = dataAdapter
                        for (i in district_List!!.indices) {
                            if (district_List!![i]!!.districtId == model!!.districtID) {
                                binding!!.spinnerDistrict.setSelection(i)
                                districtId = district_List!![i]!!.districtId
                                districtNameEng = district_List!![i]!!.districtNameEng
                                tehsilList(district_List!![i]!!.districtId)
                            }
                        }
                    }
                }
            })
    }

    private fun tehsilList(districtId: String?) {
        showProgress()
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.apiGetTehsilByDistict(districtId)
        call.enqueue(object : Callback<ModelTehsil?> {
            override fun onResponse(call: Call<ModelTehsil?>, response: Response<ModelTehsil?>) {
                hideProgress()
                if (response.code() == 200) {
                    if (response.isSuccessful) {
                        val modelTehsil = response.body()
                        if (modelTehsil!!.status == "200") {
                            tehsil_list = modelTehsil!!.tehsil_List
                            if (tehsil_list != null && tehsil_list.size > 0) {
                                Collections.reverse(tehsil_list)
                                val l = ModelTehsilList()
                                l.tehsilId = (-1).toString()
                                l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
                                tehsil_list.add(l)
                                tehsil_list.reverse()
                                val dataAdapter = ArrayAdapter(
                                    mContext!!, android.R.layout.simple_spinner_item, tehsil_list
                                )
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding!!.spinnerTehsil.adapter = dataAdapter
                                for (i in tehsil_list.indices) {
                                    if (tehsil_list[i]!!.tehsilId == model!!.tehsilID) {
                                        tehsilId = tehsil_list[i]!!.tehsilId
                                        tehsilNameEng = tehsil_list[i]!!.tehsilNameEng
                                        binding!!.spinnerTehsil.setSelection(i)
                                    }
                                }
                            }
                        } else {
                            makeToast(modelTehsil.message)
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelTehsil?>, t: Throwable) {
                hideProgress()
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading.observe(
                this
            ) { aBoolean: Boolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonCreateTraining) {
            updateTrainingSchedule()
        } else if (id == R.id.inputTitle) {
            binding!!.inputTitle.isCursorVisible = true
        } else if (id == R.id.inputPlace) {
            binding!!.inputPlace.isCursorVisible = true
        } else if (id == R.id.inputDescription) {
            binding!!.inputDescription.isCursorVisible = true
        } else if (id == R.id.textStartDateTime) {
            val dateFromDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarFromDate[Calendar.YEAR] =
                        year
                    myCalendarFromDate[Calendar.MONTH] = monthOfYear
                    myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    timePickerFromDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        } else if (id == R.id.textEndDateTime) {
            val dateToDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    timePickerToDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateToDate, myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }
}