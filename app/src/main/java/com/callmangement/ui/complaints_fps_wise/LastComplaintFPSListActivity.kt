package com.callmangement.ui.complaints_fps_wise

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.BuildConfig
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.adapter.LastComplaintFPSListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityLastComplaintFpslistBinding
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWise
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWiseList
import com.callmangement.model.tehsil.ModelTehsil
import com.callmangement.model.tehsil.ModelTehsilList
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Row
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class LastComplaintFPSListActivity : CustomActivity(), View.OnClickListener {


    var binding: ActivityLastComplaintFpslistBinding? = null
    
    private var prefManager: PrefManager? = null
    private var districtId = "0"
    private var tehsilId = "0"
    private var fpsCode = ""
    private var tehsilList: MutableList<ModelTehsilList>? = ArrayList()
    private var districtList: MutableList<ModelDistrictList>? = ArrayList()
    private var modelFPSDistTehWiseList: MutableList<ModelFPSDistTehWiseList>? = ArrayList()
    private val checkTehsil = 0
    private val checkDistrict = 0
    private var tehsilNameEng = ""
    private var districtNameEng = ""
    private var viewModel: ComplaintViewModel? = null
    var mActivity: Activity? = null
    private var vibrator: Vibrator? = null
    var originalFileName: String = "Demo.xlsx" // Original file name
    var uniqueFileName: String = generateUniqueFileName(originalFileName)
    private val filePathh = File("/storage/emulated/0/Download/$uniqueFileName")
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLastComplaintFpslistBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)

        mContext = this

        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.last_complain_list)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        binding!!.actionBar.buttonEXCEL.visibility = View.VISIBLE
        prefManager = PrefManager(mContext!!)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        initView()
    }

    private fun initView() {
        mActivity = this
        setUpOnclickListener()
        clearSharePreference()
        setUpData()
        districtList()
    }

    private fun setUpOnclickListener() {
        binding!!.buttonGetDetails.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.actionBar.buttonEXCEL.setOnClickListener(this)
    }

    private fun setUpData() {
        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (districtList!![i].districtId != "0") {
                        binding!!.rvFpsList.visibility = View.GONE
                        binding!!.textNoRecordFound.visibility = View.VISIBLE
                        districtNameEng = districtList!![i].districtNameEng!!
                        districtId = districtList!![i].districtId!!
                        tehsilList(districtId)
                    } else {
                        districtNameEng = ""
                        districtId = "0"
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
                    if (tehsilList!![i].tehsilId != "0") {
                        tehsilNameEng = tehsilList!![i].tehsilNameEng!!
                        tehsilId = tehsilList!![i].tehsilId!!
                        getFPSList()
                    } else {
                        tehsilNameEng = ""
                        tehsilId = "0"
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }
    }

    private fun districtList() {
        isLoading
        viewModel!!.district.observe(this) { modelDistrict: ModelDistrict? ->
            isLoading
            if (modelDistrict!!.status == "200") {
                districtList!!.clear()
                districtList = modelDistrict.district_List
                if (districtList != null && districtList!!.size > 0) {
                    val modelDistrictList = ModelDistrictList()
                    modelDistrictList.districtNameEng =
                        "--" + resources.getString(R.string.district) + "--"
                    districtList!!.add(0, modelDistrictList)
                    val dataAdapter = ArrayAdapter(
                        mContext!!, android.R.layout.simple_spinner_item,
                        districtList!!
                    )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter

                    tehsilList!!.clear()
                    val modelTehsilList = ModelTehsilList()
                    modelTehsilList.tehsilNameEng =
                        "--" + resources.getString(R.string.tehsil) + "--"
                    tehsilList!!.add(0, modelTehsilList)
                    val dataAdapter1 =
                        ArrayAdapter(mContext!!, android.R.layout.simple_spinner_item, tehsilList!!)
                    dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerTehsil.adapter = dataAdapter1
                }
            }
        }
    }

    private fun tehsilList(districtId: String) {
        if (Constants.isNetworkAvailable(mActivity!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.apiGetTehsilByDistict(districtId)
            call.enqueue(object : Callback<ModelTehsil?> {
                override fun onResponse(
                    call: Call<ModelTehsil?>,
                    response: Response<ModelTehsil?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            tehsilList!!.clear()
                            tehsilList = model!!.tehsil_List
                            if (tehsilList != null && tehsilList!!.size > 0) {
                                val modelTehsilList = ModelTehsilList()
                                modelTehsilList.tehsilNameEng =
                                    "--" + resources.getString(R.string.tehsil) + "--"
                                tehsilList!!.add(0, modelTehsilList)
                                val dataAdapter = ArrayAdapter(
                                    mContext!!, android.R.layout.simple_spinner_item,
                                    tehsilList!!
                                )
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding!!.spinnerTehsil.adapter = dataAdapter
                            }
                        } else {
                            Toast.makeText(mActivity, model!!.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            mActivity,
                            resources.getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ModelTehsil?>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(
                        mActivity,
                        resources.getString(R.string.error_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(
                mActivity,
                resources.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading.observe(this) { aBoolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    private fun getFPSList() {
            if (Constants.isNetworkAvailable(mContext!!)) {
                modelFPSDistTehWiseList!!.clear()

                fpsCode = Objects.requireNonNull(binding!!.inputFpsCode.text).toString()
                    .trim { it <= ' ' }

                val service = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )
                //Toast.makeText(LastComplaintFPSListActivity.this, "fpsCode:"+fpsCode+" Tehsil:"+tehsilId+" disrictId:"+districtId, Toast.LENGTH_SHORT).show();
                val call = service.getFPSListDisTehWise(fpsCode, districtId, tehsilId)
                showProgress()
                call.enqueue(object : Callback<ModelFPSDistTehWise> {
                    override fun onResponse(
                        call: Call<ModelFPSDistTehWise>,
                        response: Response<ModelFPSDistTehWise>
                    ) {
                        hideProgress()
                        if (response.isSuccessful) {
                            val model = response.body()
                            if (model!!.status == "200") {
                                modelFPSDistTehWiseList = model!!.modelFPSDistTehWiseList
                                if (modelFPSDistTehWiseList!!.size > 0) {
                                    binding!!.rvFpsList.visibility = View.VISIBLE
                                    binding!!.textNoRecordFound.visibility = View.GONE
                                    setUpFPSListAdapter(modelFPSDistTehWiseList)
                                    val sharedPreferences =
                                        getSharedPreferences("shared preferences", MODE_PRIVATE)
                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    val editor = sharedPreferences.edit()
                                    // creating a new variable for gson.
                                    val gson = Gson()
                                    // getting data from gson and storing it in a string.
                                    val json =
                                        gson.toJson(response.body()!!.modelFPSDistTehWiseList)
                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("LastComp", json)
                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply()
                                } else {
                                    binding!!.rvFpsList.visibility = View.GONE
                                    binding!!.textNoRecordFound.visibility = View.VISIBLE
                                    clearSharePreference()
                                }
                            } else {
                                binding!!.rvFpsList.visibility = View.GONE
                                binding!!.textNoRecordFound.visibility = View.VISIBLE
                                clearSharePreference()

                                //makeToastLong(model.getMessage());
                            }
                        } else {
                            clearSharePreference()

                            makeToast(resources.getString(R.string.error))
                        }
                    }

                    override fun onFailure(call: Call<ModelFPSDistTehWise>, t: Throwable) {
                        hideProgress()
                        makeToast(resources.getString(R.string.error))
                    }
                })
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        }

    private fun setUpFPSListAdapter(modelFPSDistTehWiseList: List<ModelFPSDistTehWiseList>?) {
        binding!!.rvFpsList.layoutManager =
            LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
        binding!!.rvFpsList.adapter =
            LastComplaintFPSListActivityAdapter(mContext!!, modelFPSDistTehWiseList)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonGetDetails) {
            getFPSList()
        } else if (id == R.id.buttonEXCEL) {
            vibrator!!.vibrate(100)

            Log.d("ListSize", "" + modelFPSDistTehWiseList!!.size)

            if (modelFPSDistTehWiseList != null && modelFPSDistTehWiseList!!.size > 0) {
                ExcelformTable()
            } else {
                Toast.makeText(mContext!!, "No Data Found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun ExcelformTable() {
        mActivity = this
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        // creating a variable for gson.
        val gson = Gson()
        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences!!.getString("LastComp", null)
        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<ModelFPSDistTehWiseList?>?>() {}.type
        // in below line we are getting data from gson
        // and saving it to our array list
        modelFPSDistTehWiseList = gson.fromJson(json, type)
        if (modelFPSDistTehWiseList == null) {
            // if the array list is empty
            // creating a new array list.
            modelFPSDistTehWiseList = ArrayList()
            Log.d("nbb", "" + modelFPSDistTehWiseList)
        }
        Log.d("gfhvbb", "" + modelFPSDistTehWiseList)
        val originalFormat = "yyyy-MM-dd"
        val desiredFormat = "dd-MM-yyyy"
        // String corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat);
        // String corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        //  Log.d("formateddate", "" + corStartDate);
        val workbook = HSSFWorkbook()
        //   HSSFSheet firstSheet = workbook.createSheet("Iris Delivered & Installed " + corStartDate + " - " + corEndDate);
        val firstSheet = workbook.createSheet("Last Complaint List")
        // Create cell style for center alignment
        val rowA = firstSheet.createRow(0)
        rowA.heightInPoints = 20f // Set row height in points
        val cellA = rowA.createCell(0)
        /*  cellA.setCellValue(new HSSFRichTextString("District\n name"));
        // Create a bold font
        HSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);
        // Apply the bold font to a new cell style
        HSSFCellStyle boldCellStyle = workbook.createCellStyle();
        boldCellStyle.setFont(boldFont);
        cellA.setCellStyle(boldCellStyle);
        firstSheet.setColumnWidth(0, 3000);
*/
        val boldFont: Font = workbook.createFont()
        boldFont.bold = true
        // Create a RichTextString with bold formatting
        val richText: RichTextString = HSSFRichTextString("District\nName")
        richText.applyFont(boldFont)
        // Set the RichTextString to the cell
        cellA.setCellValue(richText)
        firstSheet.setColumnWidth(0, 4000)
        val cellB = rowA.createCell(1)
        val richText1: RichTextString = HSSFRichTextString("Tehsil")
        richText1.applyFont(boldFont)
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1)
        firstSheet.setColumnWidth(1, 4000)
        //   cellB.setCellStyle(cellStyle);
        val cellC = rowA.createCell(2)
        firstSheet.setColumnWidth(2, 4000)
        val richText2: RichTextString = HSSFRichTextString("FPS\nCode")
        richText2.applyFont(boldFont)
        cellC.setCellValue(richText2)
        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        val cellD = rowA.createCell(3)
        firstSheet.setColumnWidth(3, 9000)
        val richText3: RichTextString = HSSFRichTextString("Name")
        richText3.applyFont(boldFont)
        cellD.setCellValue(richText3)
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        val cellE = rowA.createCell(4)
        firstSheet.setColumnWidth(4, 4000)
        val richText4: RichTextString = HSSFRichTextString("Mobile.N0")
        richText4.applyFont(boldFont)
        cellE.setCellValue(richText4)


        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        val cellF = rowA.createCell(5)
        firstSheet.setColumnWidth(5, 4000)
        val richText5: RichTextString = HSSFRichTextString("ComplainStatus")
        richText5.applyFont(boldFont)
        cellF.setCellValue(richText5)
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        val cellG = rowA.createCell(6)
        firstSheet.setColumnWidth(6, 9000)
        val richText6: RichTextString = HSSFRichTextString("Complain\nDate")
        richText6.applyFont(boldFont)
        cellG.setCellValue(richText6)
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        val cellH = rowA.createCell(7)
        firstSheet.setColumnWidth(7, 9000)
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        val richText7: RichTextString = HSSFRichTextString("Complain\nDesc")
        richText7.applyFont(boldFont)
        cellH.setCellValue(richText7)




        Log.d("mylist", " -------------- $modelFPSDistTehWiseList")
        if (modelFPSDistTehWiseList != null && modelFPSDistTehWiseList!!.size > 0) {
            for (i in modelFPSDistTehWiseList!!.indices) {
                val detailsInfo = modelFPSDistTehWiseList!![i]
                val districtName = detailsInfo.districtName.toString()
                val teh = detailsInfo.tehsilName.toString()
                val fpsCode = detailsInfo.fpscode.toString()
                val customerName = detailsInfo.customerNameEng.toString()
                val mobileNo = detailsInfo.mobileNo.toString()
                val complaintStatus = detailsInfo.complainStatus.toString()
                val date = detailsInfo.complainRegDate.toString()
                val dis = detailsInfo.complainDesc.toString()
                val dataRow: Row = firstSheet.createRow(i + 1) // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName)
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(teh)
                dataRow.createCell(2).setCellValue(fpsCode)
                dataRow.createCell(3).setCellValue(customerName)
                dataRow.createCell(4).setCellValue(mobileNo)
                dataRow.createCell(5).setCellValue(complaintStatus)
                dataRow.createCell(6).setCellValue(date)
                dataRow.createCell(7).setCellValue(dis)
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
                this@LastComplaintFPSListActivity,
                "Excel Sheet Download ",
                Toast.LENGTH_SHORT
            ).show()
        }


        /*  long timeMillis = System.currentTimeMillis();

        // Generate a random number.
     Random random = new Random();
        int randomNumber = random.nextInt(100000);

        // Combine the current date and time with the random number to generate a unique string.
        String fileName = String.format("excel_%d_%d", timeMillis, randomNumber);
        Log.d("fkddv", "fh" + fileName);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Filter for Excel files

        try {
            startActivityForResult(intent, 1); // Use startActivityForResult to get the selected file's URI
        } catch (ActivityNotFoundException e) {
            // Handle the case where no app capable of handling this intent is installed
        }*/
        try {
            val fileUri = FileProvider.getUriForFile(
                this@LastComplaintFPSListActivity,
                BuildConfig.APPLICATION_ID + ".provider",  // Replace with your app's provider authority
                filePathh
            )

            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel")
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(openIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this@LastComplaintFPSListActivity,
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


    fun clearSharePreference() {
        // super.onBackPressed();
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Clearing the value associated with the "camp" key
        editor.remove("LastComp")
        // Applying the changes to save the updated SharedPreferences
        editor.apply()
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