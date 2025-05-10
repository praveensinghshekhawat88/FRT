package com.callmangement.ui.iris_derivery_installation

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityInstallationPendingListBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp
import com.callmangement.ui.iris_derivery_installation.adapter.IrisInstalledPendingAdapter
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Objects

class InstallationPendingListActivity : CustomActivity() {
    var multipleFilePath: String = "BookList.xls"

    private var binding: ActivityInstallationPendingListBinding? = null
    
    private var mActivity: Activity? = null
    private var prefManager: PrefManager? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mRecyclerView: RecyclerView? = null
    private var spinnerDistrict: Spinner? = null
    private val spinnerList: MutableList<String> = ArrayList()
    private var checkDistrict = 0
    private var districtNameEng: String? = ""
    private var districtId: String? = "0"
    private val districtIdd = "0"
    private val myFormat = "yyyy-MM-dd"
    private var fromDate: String? = ""
    private var toDate: String? = ""
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private var district_List: MutableList<ModelDistrictList_w?>? = ArrayList()
    private var dis: String? = null
    private var checkFilter = 0
    private var fpscodee: String? = null
    private var serialno: String? = null
    private var value_selectedDay: String? = null
    private var value_selectedDis: String? = null
    private val irisInsDataArrayListBlock = ArrayList<IrisInstallationPendingListResp.Datum>()
    private var irisInstalledAdapter: IrisInstalledPendingAdapter? = null
    private val block_List: List<String> = ArrayList()
    private var installedRoot = IrisInstallationPendingListResp()
    private var To_USER_Id = "0"
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstallationPendingListBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data")


        init()
    }

    private fun createXlsx(irisInsDataArrayList: ArrayList<IrisInstallationPendingListResp.Datum>) {
        try {
            runOnUiThread { showProgress() }
            val strDate =
                SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(Date())

            //            File root = new File(Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ePDSDocuments");
//            if (!root.exists())
//                root.mkdirs();
//            File path = new File(root, "/" + districtNameEng + strDate + ".xlsx");
            val path = File("/storage/emulated/0/Download/$districtNameEng$strDate.xlsx")

            val workbook = XSSFWorkbook()
            val outputStream = FileOutputStream(path)

            val headerStyle = workbook.createCellStyle()
            headerStyle.setAlignment(HorizontalAlignment.CENTER)
            headerStyle.fillForegroundColor = IndexedColors.BLUE_GREY.getIndex()
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
            headerStyle.setBorderTop(BorderStyle.MEDIUM)
            headerStyle.setBorderBottom(BorderStyle.MEDIUM)
            headerStyle.setBorderRight(BorderStyle.MEDIUM)
            headerStyle.setBorderLeft(BorderStyle.MEDIUM)

            val font = workbook.createFont()
            font.fontHeightInPoints = 12.toShort()
            font.color = IndexedColors.WHITE.getIndex()
            font.bold = true
            headerStyle.setFont(font)

            val sheet = workbook.createSheet("Pending Deliveries")
            var row = sheet.createRow(0)

            var cell = row.createCell(0)
            cell.setCellValue("Name")
            cell.cellStyle = headerStyle

            cell = row.createCell(1)
            cell.setCellValue("FPS Code")
            cell.cellStyle = headerStyle

            cell = row.createCell(2)
            cell.setCellValue("Block")
            cell.cellStyle = headerStyle

            cell = row.createCell(3)
            cell.setCellValue("District")
            cell.cellStyle = headerStyle

            cell = row.createCell(4)
            cell.setCellValue("Mobile Number")
            cell.cellStyle = headerStyle

            for (i in irisInsDataArrayList.indices) {
                row = sheet.createRow(i + 1)

                cell = row.createCell(0)
                cell.setCellValue(irisInsDataArrayList[i].dealerName)
                sheet.setColumnWidth(0, (irisInsDataArrayList[i].dealerName!!.length + 30) * 256)

                cell = row.createCell(1)
                cell.setCellValue(irisInsDataArrayList[i].fpscode)
                sheet.setColumnWidth(1, (irisInsDataArrayList[i].fpscode!!.length + 10) * 256)

                cell = row.createCell(2)
                cell.setCellValue(irisInsDataArrayList[i].blockName)
                sheet.setColumnWidth(2, (irisInsDataArrayList[i].blockName!!.length + 10) * 256)

                cell = row.createCell(3)
                cell.setCellValue(irisInsDataArrayList[i].districtName)
                sheet.setColumnWidth(3, (irisInsDataArrayList[i].districtName!!.length + 10) * 256)

                cell = row.createCell(4)
                cell.setCellValue(irisInsDataArrayList[i].dealerMobileNo)
                sheet.setColumnWidth(
                    4,
                    (irisInsDataArrayList[i].dealerMobileNo!!.length + 10) * 256
                )
            }

            workbook.write(outputStream)
            outputStream.close()
            workbook.close()
            runOnUiThread {
                Toast.makeText(
                    this@InstallationPendingListActivity,
                    "Data exported successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //            Toast.makeText(InstallationPendingListActivity.this, "Data exported successfully!", Toast.LENGTH_SHORT).show();
            val uri = Uri.parse(path.path)
            hideProgress()

            /* Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            try {
                startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"));
            } catch (ActivityNotFoundException e) {
                //handle no apps found eg inform the user
                e.printStackTrace();
            }*/
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") // Filter for Excel files

            try {
                startActivityForResult(
                    intent,
                    1
                ) // Use startActivityForResult to get the selected file's URI
            } catch (e: ActivityNotFoundException) {
                // Handle the case where no app capable of handling this intent is installed
            }
        } catch (e: IOException) {
            hideProgress()
            e.printStackTrace()
        }
    }


    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@InstallationPendingListActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@InstallationPendingListActivity,
                arrayOf(permission),
                requestCode
            )
        } else {
            Toast.makeText(
                this@InstallationPendingListActivity,
                "Permission already granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun init() {
        mContext = this
        mActivity = this
        prefManager = PrefManager(mContext!!)
        swipeRefreshLayout = findViewById(R.id.refresh_list)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel))
        mRecyclerView = findViewById(R.id.rv_delivered)
        //     binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.installed_list));
        binding!!.actionBar.textToolbarTitle.text = "Iris Installation Pending List"
        setUpData()
        setClickListener()
        //  setClickListener();
    }

    private fun setUpData() {
        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE
                .equals("Admin", ignoreCase = true)
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.VISIBLE
            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE
                .equals("Manager", ignoreCase = true)
        ) {
            binding!!.rlDistrict.visibility = View.VISIBLE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.GONE
            val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("districtId_key")
            editor.apply()
        } else if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE
                .equals("ServiceEngineer", ignoreCase = true)
        ) {
            To_USER_Id = prefManager!!.useR_Id

            binding!!.rlDistrict.visibility = View.GONE
            binding!!.spacer.visibility = View.VISIBLE
            binding!!.seDistrict.visibility = View.VISIBLE
            districtId = intent.getStringExtra("districtId")
            districtNameEng = intent.getStringExtra("key_districtName")
            if (districtIdd != null && !districtIdd.isEmpty()) {
                try {
                    val sp = getSharedPreferences("your_prefs", MODE_PRIVATE)
                    val editor = sp.edit()
                    editor.putInt("districtId_key", districtId!!.toInt())
                    editor.commit()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            } else {
            }
        }

        val USER_NAME = prefManager!!.useR_NAME
        val USER_EMAIL = prefManager!!.useR_EMAIL
        val USER_Mobile = prefManager!!.useR_Mobile
        val USER_DISTRICT = prefManager!!.useR_District
        Log.d("USER_NAME", " $USER_NAME")
        //  getIrisInstallationPendingList();
        //  districtId = getIntent().getStringExtra("districtId");
        binding!!.seDistrict.text = USER_DISTRICT
        spinnerDistrict = findViewById(R.id.spinnerDistrict)
        districtList()
        districtNameEng = "--" + resources.getString(R.string.district) + "--"
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val todayDate = sdf.format(today)
        fromDate = todayDate
        toDate = todayDate
        //   getIrisInstallationPendingList(districtId,fromDate,toDate,fpscodee);
        swipeRefreshLayout!!.setOnRefreshListener {
            swipeRefreshLayout!!.isRefreshing = false
            Log.d("clickkked", "refresh")
            getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno)
        }

        val intent = intent
        // Get the value from the intent using the key
        if (intent != null) {
            val value_districtId = intent.getStringExtra("key_districtId")
            val value_fromDate = intent.getStringExtra("key_fromDate")
            val value_toDate = intent.getStringExtra("key_toDate")
            value_selectedDay = intent.getStringExtra("key_selectedDate")
            value_selectedDis = intent.getStringExtra("key_selectedDis")
            Log.d("value_selectedDay", value_selectedDay!!)

            //   binding.spinner.setSelection(2);
            districtId = value_districtId
            fromDate = value_fromDate
            toDate = value_toDate
            Log.d("clickkked", "intent $districtId")

            if (districtId != null && districtId != "0") {
                getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno)
            }
        } else {
            //  binding.spinner.setSelection(1);
            getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno)
        }
        // Log.d("useriduserid",""+prefManager.getUseR_TYPE_ID());
        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { //Intent i = new Intent(IrisInstalledListActivity.this, WeighingScaleDashboard.class);
            //startActivity(i);
            onBackPressed()
        }

        binding!!.actionBar.buttonPDF.setOnClickListener {
            if (irisInsDataArrayList != null && irisInsDataArrayList!!.size > 0) {
                //           showProgress();

                Log.d(
                    "arrrr_size",
                    "" + irisInsDataArrayList!!.size
                )
                AsyncTask.execute { //TODO your background code
                    createXlsx(irisInsDataArrayList!!)
                }
            }
            //                if (irisInsDataArrayList != null && irisInsDataArrayList.size() > 0) {
            //                    Constants.irisInsPendingArrayList = irisInsDataArrayList;
            //                    startActivity(new Intent(mContext, IrisInstallationPendingPdfActivity.class));
            //                    finish();
            //                }
        }


        spinnerDistrict!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List!![i]!!.districtNameEng
                    dis = district_List!![i]!!.districtNameEng
                    Log.d("dfgfd", " $dis")
                    districtId = district_List!![i]!!.districtId
                    Log.d("fggfgh", " $districtId")
                    val editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    editor.putString("dis", dis)
                    editor.putString("districtId", districtId)
                    editor.apply()
                    /*  if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
                              SEUser_list.clear();
                              Collections.reverse(SEUser_list);
                              ModelSEUserList list = new ModelSEUserList();
                              list.setUserId(valueOf(-1));
                              list.setUserName("--" + getResources().getString(R.string.se_user) + "--");
                              SEUser_list.add(list);
                              Collections.reverse(SEUser_list);
                              ArrayAdapter<ModelSEUserList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, SEUser_list);
                              dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                              spinnerSEUsers.setAdapter(dataAdapter);
                          } else {
                              SEUsersList(districtId);
                          }*/
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

        binding!!.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
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
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            val todayDate = sdf.format(today)
                            fromDate = todayDate
                            toDate = todayDate

                            //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equals(
                                resources.getString(R.string.yesterday),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            val yesterday = calendar.time
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            val yesterdayDate = sdf.format(yesterday)
                            fromDate = yesterdayDate
                            toDate = yesterdayDate

                            //   getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equals(
                                resources.getString(R.string.current_month),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
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
                            //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equals(
                                resources.getString(R.string.previous_month),
                                ignoreCase = true
                            )
                        ) {
                            binding!!.layoutDateRange.visibility = View.GONE
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
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
                            //       getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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
                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }


        binding!!.textFromDate.setOnClickListener {
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
            val minDateInMillis = myCalendarToDate.timeInMillis
            datePickerDialog.datePicker.maxDate = minDateInMillis
            datePickerDialog.show()
        }

        binding!!.textToDate.setOnClickListener {
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
            val minDateInMillis = myCalendarFromDate.timeInMillis
            datePickerDialog.datePicker.minDate = minDateInMillis
            datePickerDialog.show()
        }

        binding!!.btnsearch.setOnClickListener {
            fpscodee = binding!!.inputfps.text.toString()
            serialno = binding!!.inputserialno.text.toString()
            Log.d("clickkked", "search")
            getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno)
        }
    }

    private fun getIrisInstallationPendingList(
        districtId: String?, fromDate: String?, toDate: String?, fpscodee: String?,
        serialno: String?
    ) {
        if (isNetworkAvailable(mContext!!)) {
            hideKeyboard(mActivity)
            showProgress()
            val USER_Id = prefManager!!.useR_Id
            Log.d("USER_ID", USER_Id)

            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            val call = apiInterface.getCombinedIRISWehingDetailReport(
                USER_Id, districtId,
                "0", "0", fromDate, toDate, To_USER_Id
            )

            Log.d("response", "----$USER_Id$districtId$fromDate$To_USER_Id")
            call.enqueue(object : Callback<IrisInstallationPendingListResp?> {
                override fun onResponse(
                    call: Call<IrisInstallationPendingListResp?>,
                    response: Response<IrisInstallationPendingListResp?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding!!.txtNoRecord.visibility = View.GONE
                                if (response.body()!!.status == "200") {
                                    installedRoot = response.body()!!
                                    val message = installedRoot.message
                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();
                                    irisInsDataArrayList!!.clear()
                                    irisInsDataArrayList = installedRoot.data

                                    Log.d("iris_array", "" + irisInsDataArrayList!!.size)
                                    mRecyclerView!!.layoutManager = LinearLayoutManager(mContext)
                                    //            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                                    mRecyclerView!!.visibility = View.VISIBLE
                                    //        binding.rlBlock.setVisibility(View.VISIBLE);
                                    irisInstalledAdapter = IrisInstalledPendingAdapter(
                                        irisInsDataArrayList!!,
                                        mContext!!
                                    )
                                    mRecyclerView!!.adapter = irisInstalledAdapter


                                    // Use 'dataValue' or perform operations with other properties
                                } else {
                                    binding!!.txtNoRecord.visibility = View.VISIBLE
                                    mRecyclerView!!.visibility = View.GONE
                                    binding!!.rlBlock.visibility = View.GONE
                                }
                            } else {
                                binding!!.txtNoRecord.visibility = View.VISIBLE
                                mRecyclerView!!.visibility = View.GONE
                                binding!!.rlBlock.visibility = View.GONE
                                makeToast(response.body()!!.message.toString())
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(
                    call: Call<IrisInstallationPendingListResp?>,
                    error: Throwable
                ) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
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
        //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
    }

    private fun districtList() {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity)
            hideProgress()
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            val USER_Id = prefManager!!.useR_Id
            val apiInterface = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = apiInterface.apiGetDistictList_w()
            call.enqueue(object : Callback<ModelDistrict_w?> {
                override fun onResponse(
                    call: Call<ModelDistrict_w?>,
                    response: Response<ModelDistrict_w?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    district_List = response.body()!!.district_List
                                    if (district_List != null && district_List!!.size > 0) {
                                        Collections.reverse(district_List)
                                        val l = ModelDistrictList_w()
                                        l.districtId = (-1).toString()
                                        l.districtNameEng =
                                            "--" + resources.getString(R.string.district) + "--"
                                        district_List!!.add(l)
                                        Collections.reverse(district_List)
                                        val dataAdapter = ArrayAdapter(
                                            mActivity!!,
                                            android.R.layout.simple_spinner_item, district_List!!
                                        )
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerDistrict!!.adapter = dataAdapter
                                        binding!!.spinnerDistrict.setSelection(value_selectedDis!!.toInt())

                                        /* if(d2!=null)
                                          {
                                              spinnerDistrict.setSelection(Integer.parseInt(d2));

                                          }*/

                                        /*  if(d2!=null && !d2.isEmpty())
                                          {
                                              Log.d("ghjh",""+d2);
                                     int userId = Integer.parseInt(d2); // replace with the user ID you want to select

                                              for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                  ModelDistrictList user = dataAdapter.getItem(i);
                                                  if (String.valueOf(user.getDistrictId()).equals(String.valueOf(userId))) {
                                                      spinnerDistrict.setSelection(i);
                                                      break;
                                                  }
                                              }
                                          }*/
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

                override fun onFailure(call: Call<ModelDistrict_w?>, error: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setUpDateRangeSpinner() {
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))
        val dataAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            spinnerList
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinner.adapter = dataAdapter
        binding!!.spinner.setSelection(value_selectedDay!!.toInt())
        val selectedString = binding!!.spinner.selectedItem as String
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //    Intent i = new Intent(IrisInstalledListActivity.this, WeighingScaleDashboard.class);
        //    startActivity(i);
    }

    override fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private var irisInsDataArrayList: ArrayList<IrisInstallationPendingListResp.Datum>? =
            ArrayList()
        val listOfObject: List<Map<String, List<*>?>>
            get() {
                val map: MutableList<Map<String, List<*>?>> =
                    ArrayList()
                val map1: MutableMap<String, List<*>?> =
                    HashMap()
                map1["Report"] = irisInsDataArrayList
                map.add(map1)

                return map
            }
    }
}