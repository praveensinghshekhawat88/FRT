package com.callmangement.ui.distributor.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDistributorPosReportForSeactivityBinding
import com.callmangement.ui.distributor.adapter.DistributorPosReportForSEActivityAdapter
import com.callmangement.ui.distributor.model.PosDistributionDetail
import com.callmangement.ui.distributor.model.PosDistributionListResponse
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DistributorPosReportForSEActivity : CustomActivity(), View.OnClickListener {

    
    private var binding: ActivityDistributorPosReportForSeactivityBinding? = null
    private var prefManager: PrefManager? = null
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val spinnerList: MutableList<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private var checkFilter = 0
    private var fromDate = ""
    private var toDate = ""
    private val userReportList: MutableList<PosDistributionDetail> = ArrayList()
    var fpscodee: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistributorPosReportForSeactivityBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.pos_reports)
        setUpClickListener()
        setUpData()
    }

    private fun setUpData() {
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinner.adapter = dataAdapter

        //my code
        /*  binding.spinner.setSelection(1);

        int selectedItemPosition = binding.spinner.getSelectedItemPosition();
        if (selectedItemPosition == 1) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String todayDate = sdf.format(today);
            fromDate = todayDate;
            toDate = todayDate;
        } else {
        }*/

        //
    }

    private fun setUpClickListener() {
        binding!!.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                i: Int,
                l: Long
            ) {
                if (++checkFilter > 1) {
                    val item = adapterView.getItemAtPosition(i).toString()
                    if (!item.equals(
                            "--" + resources.getString(R.string.select_filter) + "--",
                            ignoreCase = true
                        )
                    ) {
                        binding!!.textFromDate.text!!.clear()
                        binding!!.textToDate.text!!.clear()
                        if (item.equals(resources.getString(R.string.today), ignoreCase = true)) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            val today = calendar.time
                            val sdf =
                                SimpleDateFormat(myFormat, Locale.US)
                            val todayDate = sdf.format(today)
                            fromDate = todayDate
                            toDate = todayDate
                            getReportList(fromDate, toDate, fpscodee)
                        } else if (item.equals(
                                resources.getString(R.string.yesterday),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            val yesterday = calendar.time
                            val sdf =
                                SimpleDateFormat(myFormat, Locale.US)
                            val yesterdayDate = sdf.format(yesterday)
                            fromDate = yesterdayDate
                            toDate = yesterdayDate
                            getReportList(fromDate, toDate, fpscodee)
                        } else if (item.equals(
                                resources.getString(R.string.current_month),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val sdf =
                                SimpleDateFormat(myFormat, Locale.US)
                            val calendar = Calendar.getInstance()
                            calendar[Calendar.DAY_OF_MONTH] =
                                calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                            calendar[Calendar.HOUR_OF_DAY] = 0
                            calendar[Calendar.MINUTE] = 0
                            calendar[Calendar.SECOND] = 0
                            calendar[Calendar.MILLISECOND] = 0
                            val firstDateOfCurrentMonth = calendar.time
                            val date1 = sdf.format(firstDateOfCurrentMonth)
                            val calendar1 = Calendar.getInstance()
                            val currentDateOfCurrentMonth = calendar1.time
                            val date2 = sdf.format(currentDateOfCurrentMonth)
                            fromDate = date1
                            toDate = date2
                            getReportList(fromDate, toDate, fpscodee)
                        } else if (item.equals(
                                resources.getString(R.string.previous_month),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val sdf =
                                SimpleDateFormat(myFormat, Locale.US)
                            val aCalendar = Calendar.getInstance()
                            aCalendar.add(Calendar.MONTH, -1)
                            aCalendar[Calendar.DATE] = 1
                            val firstDateOfPreviousMonth = aCalendar.time
                            val date1 = sdf.format(firstDateOfPreviousMonth)
                            aCalendar[Calendar.DATE] =
                                aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                            val lastDateOfPreviousMonth = aCalendar.time
                            val date2 = sdf.format(lastDateOfPreviousMonth)
                            fromDate = date1
                            toDate = date2
                            getReportList(fromDate, toDate, fpscodee)
                        } else if (item.equals(
                                resources.getString(R.string.custom_filter),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.VISIBLE
                        }
                    } else {
                        fromDate = ""
                        toDate = ""
                        binding!!.textFromDate.text!!.clear()
                        binding!!.textToDate.text!!.clear()
                        binding!!.layoutDateRange.visibility = View.GONE
                        getReportList(fromDate, toDate, fpscodee)
                        Log.d("yesyes", "hiiii")
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        /*     binding.inputFpsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    List<PosDistributionDetail> filterList = new ArrayList<>();
                    if (userReportList.size() > 0){
                        for (PosDistributionDetail model : userReportList){
                            if (model.getFpscode().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                    setAdapter(filterList);
                }else setAdapter(userReportList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputTicketNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    List<PosDistributionDetail> filterList = new ArrayList<>();
                    if (userReportList.size() > 0){
                        for (PosDistributionDetail model : userReportList){
                            if (model.getTicketNo().contains(charSequence.toString()))
                                filterList.add(model);
                        }
                    }
                    setAdapter(filterList);
                }else setAdapter(userReportList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

      */
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.textFromDate.setOnClickListener(this)
        binding!!.textToDate.setOnClickListener(this)



        binding!!.btnsearch.setOnClickListener {
            fpscodee = binding!!.inputFpsCode.text.toString()
            if (fpscodee != null && !fpscodee!!.isEmpty()) {
                getReportList(fromDate, toDate, fpscodee)
            } else {
            }
            //   getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
        }
    }

    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        binding!!.textToDate.text!!.clear()
        fromDate = binding!!.textFromDate.text.toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        toDate = binding!!.textToDate.text.toString().trim { it <= ' ' }
        getReportList(fromDate, toDate, fpscodee)
    }

    private fun getReportList(fromDate: String, toDate: String, fpscodee: String?) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        Log.d("checkdate", fromDate + toDate)
        Log.d("userid", prefManager!!.useR_Id)
        Log.d("disid", prefManager!!.useR_DistrictId!!)
        if (Constants.isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getPosDistributionListSEAPI(
                prefManager!!.useR_DistrictId!!,
                prefManager!!.useR_Id,
                "0",
                fpscodee,
                "",
                fromDate,
                toDate
            )
            call.enqueue(object : Callback<ResponseBody?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val responseStr = response.body()!!.string()
                                    val jsonObject = JSONObject((responseStr))
                                    val status = jsonObject.optString("status")
                                    val distributionListJsonArr =
                                        jsonObject.optJSONArray("posDistributionDetailList")
                                    if (distributionListJsonArr != null) {
                                        val modelResponse = getObject(
                                            responseStr,
                                            PosDistributionListResponse::class.java
                                        ) as PosDistributionListResponse
                                        if (modelResponse != null) {
                                            if (status == "200") {
                                                if (modelResponse.posDistributionDetailList!!.size > 0) {
                                                    userReportList.clear()
                                                    userReportList.addAll(modelResponse.posDistributionDetailList!!)
                                                    setAdapter(userReportList)
                                                } else {
                                                    binding!!.recyclerView.visibility = View.GONE
                                                    binding!!.tvNoDataFound.visibility =
                                                        View.VISIBLE
                                                }
                                            } else {
                                                binding!!.recyclerView.visibility = View.GONE
                                                binding!!.tvNoDataFound.visibility = View.VISIBLE
                                            }
                                        } else {
                                            binding!!.recyclerView.visibility = View.GONE
                                            binding!!.tvNoDataFound.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding!!.recyclerView.visibility = View.GONE
                                        binding!!.tvNoDataFound.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding!!.recyclerView.visibility = View.GONE
                                    binding!!.tvNoDataFound.visibility = View.VISIBLE
                                }
                            } else {
                                binding!!.recyclerView.visibility = View.GONE
                                binding!!.tvNoDataFound.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.recyclerView.visibility = View.GONE
                            binding!!.tvNoDataFound.visibility = View.VISIBLE
                        }
                    } else {
                        //  makeToast(getResources().getString(R.string.error));
                        binding!!.recyclerView.visibility = View.GONE
                        binding!!.tvNoDataFound.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.tvNoDataFound.visibility = View.VISIBLE
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAdapter(list: List<PosDistributionDetail>) {
        if (list.size > 0) {
            binding!!.recyclerView.visibility = View.VISIBLE
            binding!!.tvNoDataFound.visibility = View.GONE
            binding!!.recyclerView.layoutManager =
                LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
            binding!!.recyclerView.adapter =
                DistributorPosReportForSEActivityAdapter(mContext!!, list)
            binding!!.textTotalCount.text = "" + list.size
        } else {
            binding!!.recyclerView.visibility = View.GONE
            binding!!.tvNoDataFound.visibility = View.VISIBLE
            binding!!.textTotalCount.text = "0"
        }
    }

    fun uploadImage(fpsCode: String?, tranId: String?, districtId: String?, flagType: String?) {
        val intent = Intent(mContext!!, UploadPhotoActivity::class.java)
        intent.putExtra("fps_code", fpsCode)
        intent.putExtra("tran_id", tranId)
        intent.putExtra("district_id", districtId)
        intent.putExtra("flagType", flagType)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        /*    binding.spinner.setSelection(1);
        int selectedItemPosition = binding.spinner.getSelectedItemPosition();
        if (selectedItemPosition == 1) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String todayDate = sdf.format(today);
            fromDate = todayDate;
            toDate = todayDate;

        } else {
        }*/
        if (fpscodee != null && !fpscodee!!.isEmpty()) {
            getReportList(fromDate, toDate, fpscodee)
        } else {
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.textFromDate) {
            val dateFromDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarFromDate[Calendar.YEAR] =
                        year
                    myCalendarFromDate[Calendar.MONTH] = monthOfYear
                    myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelFromDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        } else if (id == R.id.textToDate) {
            val dateToDate =
                OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelToDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }
}