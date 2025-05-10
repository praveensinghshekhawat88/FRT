package com.callmangement.ui.training_schedule

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.TrainingScheduleListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityTrainingScheduleListBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.model.tehsil.ModelTehsilList
import com.callmangement.model.training_schedule.ModelTrainingSchedule
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class TrainingScheduleListActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityTrainingScheduleListBinding? = null
    private var viewModel: TrainingScheduleViewModel? = null
    private var prefManager: PrefManager? = null
    private var adapter: TrainingScheduleListActivityAdapter? = null
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
    private var spinnerDistrict: Spinner? = null
    private var spinnerTehsil: Spinner? = null
    private var inputStartDate: EditText? = null
    private var inputEndDate: EditText? = null
    private var startDate = ""
    private var endDate = ""
    private var trainingNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_schedule_list)
        initView()
    }

    private fun initView() {
        onClickListener()
        setUpData()
    }

    override fun onResume() {
        super.onResume()
        training
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpData() {
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            TrainingScheduleViewModel::class.java
        )

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.buttonFilter.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.training_schedule)

        adapter = TrainingScheduleListActivityAdapter(mContext!!)
        adapter!!.notifyDataSetChanged()
        binding!!.rvTrainingScheduleList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.rvTrainingScheduleList.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvTrainingScheduleList.adapter = adapter
    }

    private fun onClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonCreateTrainingSchedule.setOnClickListener(this)
        binding!!.actionBar.buttonFilter.setOnClickListener(this)
    }

    private val training: Unit
        get() {
            isLoading
            viewModel!!.getTraining(
                prefManager!!.useR_Id,
                districtId,
                tehsilId,
                trainingNumber,
                startDate,
                endDate
            ).observe(
                this,
                Observer<ModelTrainingSchedule?> { modelTrainingSchedule: ModelTrainingSchedule? ->
                    isLoading
                    if (modelTrainingSchedule!!.list.size > 0) {
                        binding!!.textNoTrainingSchedule.visibility = View.GONE
                        binding!!.rvTrainingScheduleList.visibility = View.VISIBLE
                        adapter!!.setData(modelTrainingSchedule.list)
                    } else {
                        binding!!.textNoTrainingSchedule.visibility = View.VISIBLE
                        binding!!.rvTrainingScheduleList.visibility = View.GONE
                    }
                })
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
                        spinnerDistrict!!.adapter = dataAdapter
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
                                Collections.reverse(tehsil_list)
                                val dataAdapter = ArrayAdapter(
                                    mContext!!, android.R.layout.simple_spinner_item, tehsil_list
                                )
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerTehsil!!.adapter = dataAdapter
                            }
                        } else {
                            makeToast(modelTehsil!!.message)
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

    private fun filterDialog() {
        try {
            val dialog = Dialog(mContext!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_filter)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCanceledOnTouchOutside(true)

            spinnerDistrict = dialog.findViewById(R.id.spinnerDistrict)
            spinnerTehsil = dialog.findViewById(R.id.spinnerTehsil)
            inputStartDate = dialog.findViewById(R.id.inputStartDate)
            inputEndDate = dialog.findViewById(R.id.inputEndDate)
            val inputTrainingNumber = dialog.findViewById<EditText>(R.id.inputTrainingNumber)
            val buttonTrainingFilter = dialog.findViewById<Button>(R.id.buttonTrainingFilter)

            districtNameEng = "--" + resources.getString(R.string.district) + "--"
            tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"

            districtList()

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
            spinnerTehsil!!.setAdapter(dataAdapter)

            spinnerDistrict!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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
                            val list = ModelTehsilList()
                            list.tehsilId = (-1).toString()
                            list.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
                            tehsil_list.add(list)
                            Collections.reverse(tehsil_list)
                            val dataAdapter = ArrayAdapter(
                                mContext!!, android.R.layout.simple_spinner_item, tehsil_list
                            )
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerTehsil!!.setAdapter(dataAdapter)
                        } else {
                            tehsilList(districtId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            })

            spinnerTehsil!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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
            })

            /*from date*/
            val dateFromDate =
                OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarFromDate[Calendar.YEAR] =
                        year
                    myCalendarFromDate[Calendar.MONTH] = monthOfYear
                    myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelFromDate()
                }
            inputStartDate!!.setOnClickListener(View.OnClickListener { view: View? ->
                val datePickerDialog = DatePickerDialog(
                    mContext!!, dateFromDate,
                    myCalendarFromDate[Calendar.YEAR],
                    myCalendarFromDate[Calendar.MONTH],
                    myCalendarFromDate[Calendar.DAY_OF_MONTH]
                )
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show()
            })

            /*from date*/

            /*to date*/
            val dateToDate =
                OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelToDate()
                }
            inputEndDate!!.setOnClickListener(View.OnClickListener { view: View? ->
                val datePickerDialog = DatePickerDialog(
                    mContext!!, dateToDate, myCalendarToDate[Calendar.YEAR],
                    myCalendarToDate[Calendar.MONTH],
                    myCalendarToDate[Calendar.DAY_OF_MONTH]
                )
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show()
            })

            /*to date*/
            buttonTrainingFilter.setOnClickListener { view: View? ->
                startDate = inputStartDate!!.getText().toString().trim { it <= ' ' }
                endDate = inputEndDate!!.getText().toString().trim { it <= ' ' }
                trainingNumber = inputTrainingNumber.text.toString().trim { it <= ' ' }
                training
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        inputStartDate!!.setText(sdf.format(myCalendarFromDate.time))
        inputEndDate!!.text.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        inputEndDate!!.setText(sdf.format(myCalendarToDate.time))
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) onBackPressed()
        else if (id == R.id.buttonCreateTrainingSchedule) startActivity(
            CreateTrainingScheduleActivity::class.java
        )
        else if (id == R.id.buttonFilter) filterDialog()
    }
}