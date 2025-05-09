package com.callmangement.ui.distributor.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.BuildConfig
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.adapter.DistributedPosListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityPosDistributionFormListBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.report_pdf.DistributedStatusReportPdfActivity
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.ui.distributor.model.PosDistributionDetail
import com.callmangement.ui.distributor.model.PosDistributionListResponse
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.ResponseBody
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Row
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects

class DistributedPosListActivity : CustomActivity(), View.OnClickListener {

    
    private var binding: ActivityPosDistributionFormListBinding? = null
    private var prefManager: PrefManager? = null
    private var viewModel: ComplaintViewModel? = null
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val spinnerList: MutableList<String> = ArrayList()
    private val myFormat = "yyyy-MM-dd"
    private var checkFilter = 0
    private var checkDistrict = 0
    private var districtId = "0"
    private var fromDate = ""
    private var toDate = ""
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private val userReportList: MutableList<PosDistributionDetail> = ArrayList()

    var originalFileName: String = "Demo.xlsx" // Original file name
    var uniqueFileName: String = generateUniqueFileName(originalFileName)
    private val filePathh = File("/storage/emulated/0/Download/$uniqueFileName")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPosDistributionFormListBinding.inflate(
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
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.buttonEXCEL.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.distributed_pos_list)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        setUpClickListener()
        setUpData()
        districtList()
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
        binding!!.spinner.setSelection(1)
        val selectedItemPosition = binding!!.spinner.selectedItemPosition
        if (selectedItemPosition == 1) {
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val todayDate = sdf.format(today)
            fromDate = todayDate
            toDate = todayDate
        } else {
        }

        //my code
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
                            getReportList(districtId, fromDate, toDate)
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
                            getReportList(districtId, fromDate, toDate)
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
                            getReportList(districtId, fromDate, toDate)
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
                            getReportList(districtId, fromDate, toDate)
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
                        getReportList(districtId, fromDate, toDate)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtId = district_List!![i]!!.districtId!!
                        getReportList(districtId, fromDate, toDate)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.inputFpsCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().isEmpty()) {
                    val filterList: MutableList<PosDistributionDetail> = ArrayList()
                    if (userReportList.size > 0) {
                        for (model in userReportList) {
                            if (model.fpscode!!.contains(charSequence.toString())) filterList.add(
                                model
                            )
                        }
                    }
                    setAdapter(filterList)
                } else setAdapter(userReportList)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        binding!!.inputTicketNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!charSequence.toString().isEmpty()) {
                    val filterList: MutableList<PosDistributionDetail> = ArrayList()
                    if (userReportList.size > 0) {
                        for (model in userReportList) {
                            if (model.ticketNo!!.contains(charSequence.toString())) filterList.add(
                                model
                            )
                        }
                    }
                    setAdapter(filterList)
                } else setAdapter(userReportList)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.actionBar.buttonPDF.setOnClickListener(this)
        binding!!.actionBar.buttonEXCEL.setOnClickListener(this)
        binding!!.textFromDate.setOnClickListener(this)
        binding!!.textToDate.setOnClickListener(this)
    }

    private fun updateLabelFromDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        binding!!.textToDate.text!!.clear()
        fromDate = Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        getReportList(districtId, fromDate, toDate)
    }

    private fun districtList() {
        viewModel!!.district.observe(
            this
        ) { modelDistrict: ModelDistrict? ->
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict.district_List
                if (district_List != null && district_List!!.size > 0) {
                    district_List!!.reverse()
                    val l = ModelDistrictList()
                    l.districtId = (-1).toString()
                    l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                    district_List!!.add(l)
                    district_List!!.reverse()
                    val dataAdapter = ArrayAdapter(
                        mContext!!, android.R.layout.simple_spinner_item,
                        district_List!!
                    )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter
                }
            }
        }
    }

    private fun getReportList(districtId: String, fromDate: String, toDate: String) {
        Log.d("Fromdate..", fromDate + toDate)
        if (Constants.isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getPosDistributionListAPIAllUpld(
                districtId,
                prefManager!!.useR_Id,
                "0",
                "",
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
                                                    Constants.posDistributionDetailsList =
                                                        modelResponse.posDistributionDetailList
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
                        makeToast(resources.getString(R.string.error))
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
            binding!!.recyclerView.adapter = DistributedPosListActivityAdapter(mContext!!, list)
            binding!!.textTotalCount.text = "" + list.size
        } else {
            binding!!.recyclerView.visibility = View.GONE
            binding!!.tvNoDataFound.visibility = View.VISIBLE
            binding!!.textTotalCount.text = "0"
        }
    }

    fun posDistributionFormView(model: PosDistributionDetail?) {
        startActivity(
            Intent(
                mContext!!,
                PosDistributionFormViewActivity::class.java
            ).putExtra("param", model)
        )
    }

    override fun onResume() {
        super.onResume()
        // getReportList(districtId,fromDate,toDate);
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
        } else if (id == R.id.buttonPDF) {
            if (Constants.posDistributionDetailsList != null && Constants.posDistributionDetailsList!!.size > 0) {
                startActivity(Intent(mContext!!, DistributedStatusReportPdfActivity::class.java))
                finish()
            }
        } else if (id == R.id.buttonEXCEL) {
            if (Constants.posDistributionDetailsList != null && Constants.posDistributionDetailsList!!.size > 0) {
                ExcelformTable()
            } else {
                Toast.makeText(mContext!!, "No Data Found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //Excel code
    private fun ExcelformTable() {
        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd-MM-yyyy"
        // mycomment
        val corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat)
        val corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat)

        //  Log.d("formateddate", "" + corStartDate);
        val workbook = HSSFWorkbook()
        val firstSheet =
            workbook.createSheet("Distributed Photo Upload Status $corStartDate - $corEndDate")


        // HSSFSheet firstSheet = workbook.createSheet("Distributed Photo Upload Status" );
        val rowA = firstSheet.createRow(0)
        rowA.heightInPoints = 20f // Set row height in points
        val cellA = rowA.createCell(0)
        val boldFont: Font = workbook.createFont()
        boldFont.bold = true

        val richText: RichTextString = HSSFRichTextString("FPS\nCode")
        richText.applyFont(boldFont)
        cellA.setCellValue(richText)
        firstSheet.setColumnWidth(0, 4000)

        val cellB = rowA.createCell(1)
        val richText1: RichTextString = HSSFRichTextString("TicketNo")
        richText1.applyFont(boldFont)
        cellB.setCellValue(richText1)
        firstSheet.setColumnWidth(1, 6000)


        val cellC = rowA.createCell(2)
        firstSheet.setColumnWidth(2, 4000)
        val richText2: RichTextString = HSSFRichTextString("District\nName")
        richText2.applyFont(boldFont)
        cellC.setCellValue(richText2)


        val cellD = rowA.createCell(3)
        firstSheet.setColumnWidth(3, 5000)
        val richText21: RichTextString = HSSFRichTextString("Distr. Date")
        richText21.applyFont(boldFont)
        cellD.setCellValue(richText21)


        val cellD2 = rowA.createCell(4)
        firstSheet.setColumnWidth(4, 8000)
        val richText3: RichTextString = HSSFRichTextString("DealerName")
        richText3.applyFont(boldFont)
        cellD2.setCellValue(richText3)


        val cellE = rowA.createCell(5)
        firstSheet.setColumnWidth(5, 4000)
        val richText4: RichTextString = HSSFRichTextString("IsPhotoUploaded")
        richText4.applyFont(boldFont)
        cellE.setCellValue(richText4)

        val cellF = rowA.createCell(6)
        firstSheet.setColumnWidth(6, 4000)
        val richText5: RichTextString = HSSFRichTextString("IsFormUploaded")
        richText5.applyFont(boldFont)
        cellF.setCellValue(richText5)


        Log.d("mylist", " -------------- " + Constants.posDistributionDetailsList)
        if (Constants.posDistributionDetailsList != null && Constants.posDistributionDetailsList!!.size > 0) {
            for (i in Constants.posDistributionDetailsList!!.indices) {
                val detailsInfo = Constants.posDistributionDetailsList!![i]
                val fps = detailsInfo.fpscode.toString()
                val ticketNo = detailsInfo.ticketNo.toString()
                val districtName = detailsInfo.districtName.toString()
                val transDistrict = detailsInfo.tranDateStr.toString()
                val dealerName = detailsInfo.dealerName.toString()

                val isPhotoUploaded = detailsInfo.isPhotoUploaded.toString()


                val IsFormUploaded = detailsInfo.isFormUploaded.toString()

                val dataRow: Row = firstSheet.createRow(i + 1) // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(fps)
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(ticketNo)
                dataRow.createCell(2).setCellValue(districtName)
                dataRow.createCell(3).setCellValue(transDistrict)
                dataRow.createCell(4).setCellValue(dealerName)
                dataRow.createCell(5).setCellValue(isPhotoUploaded)
                dataRow.createCell(6).setCellValue(IsFormUploaded)
            }
        }


        var fos: FileOutputStream? = null
        try {
            val str_path = Environment.getExternalStorageDirectory().toString()
            val file = File(str_path, getString(R.string.app_name) + ".xls")
            fos = FileOutputStream(filePathh)
            workbook.write(fos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Toast.makeText(
                this@DistributedPosListActivity,
                "Excel Sheet Download ",
                Toast.LENGTH_SHORT
            ).show()
        }



        try {
            val fileUri = FileProvider.getUriForFile(
                this@DistributedPosListActivity,
                BuildConfig.APPLICATION_ID + ".provider",  // Replace with your app's provider authority
                filePathh
            )

            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel")
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(openIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this@DistributedPosListActivity,
                "No app found to open Excel files.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun generateUniqueFileName(originalFileName: String): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileExtension = getFileExtension(originalFileName)
        return "excel_$timeStamp.$fileExtension"
    }

    private fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf(".")
        if (dotIndex != -1 && dotIndex < fileName.length - 1) {
            return fileName.substring(dotIndex + 1).lowercase(Locale.getDefault())
        }
        return ""
    }

    companion object {
        private fun convertDateFormat(
            dateString: String,
            originalFormat: String,
            desiredFormat: String
        ): String {
            val originalDateFormat = SimpleDateFormat(originalFormat, Locale.US)
            val desiredDateFormat = SimpleDateFormat(desiredFormat, Locale.US)

            try {
                // Parse the original date string into a Date object
                val date = originalDateFormat.parse(dateString)

                // Format the Date object into the desired format
                return desiredDateFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
                return "" // Return an empty string if there's an error in parsing or formatting
            }
        }
    }
}