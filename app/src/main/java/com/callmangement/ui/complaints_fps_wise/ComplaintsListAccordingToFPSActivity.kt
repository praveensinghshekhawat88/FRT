package com.callmangement.ui.complaints_fps_wise

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.BuildConfig
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.adapter.ComplaintsListAccordingToFPSActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityComplaintsListByFpsactivityBinding
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaint
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaintList
import com.callmangement.utils.Constants
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

class ComplaintsListAccordingToFPSActivity : CustomActivity(), View.OnClickListener {
    var binding: ActivityComplaintsListByFpsactivityBinding? = null
    private var fpsCode: String? = ""
    private var vibrator: Vibrator? = null
    var originalFileName: String = "Demo.xlsx" // Original file name
    var uniqueFileName: String = generateUniqueFileName(originalFileName)
    private val filePathh = File("/storage/emulated/0/Download/$uniqueFileName")
    var sharedPreferences: SharedPreferences? = null
    var model: ModelFPSComplaint? = null
    var mActivity: Activity? = null
    private var fPSComplainHistoryReqList: List<ModelFPSComplaintList>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintsListByFpsactivityBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.buttonEXCEL.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.complaints_by_fps)
        initView()
    }

    private fun initView() {
        setUpOnclickListener()
        clearSharePreference()

        intentData
        complaintsListByFPS
    }

    private val intentData: Unit
        get() {
            fpsCode = intent.getStringExtra("fps_code")
        }

    private fun setUpOnclickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.actionBar.buttonEXCEL.setOnClickListener(this)
    }

    private val complaintsListByFPS: Unit
        get() {
            if (Constants.isNetworkAvailable(mContext)) {
                val service = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )
                val call = service.getFPSComplainsList(fpsCode)
                showProgress()
                call.enqueue(object : Callback<ModelFPSComplaint> {
                    override fun onResponse(
                        call: Call<ModelFPSComplaint>,
                        response: Response<ModelFPSComplaint>
                    ) {
                        hideProgress()
                        if (response.isSuccessful) {
                            model = response.body()
                            if (model!!.status == "200"
                            ) {
                                fPSComplainHistoryReqList = model!!.getfPSComplainHistoryReqList()


                                if (fPSComplainHistoryReqList!!.size > 0) {
                                    binding!!.rvComplaintsListByFps.visibility = View.VISIBLE
                                    binding!!.textNoRecordFound.visibility = View.GONE
                                    setUpComplaintsListAdapter(fPSComplainHistoryReqList)


                                    val sharedPreferences = getSharedPreferences(
                                        "shared preferences",
                                        MODE_PRIVATE
                                    )
                                    // creating a variable for editor to
                                    // store data in shared preferences.
                                    val editor = sharedPreferences.edit()

                                    // creating a new variable for gson.
                                    val gson = Gson()

                                    // getting data from gson and storing it in a string.
                                    val json =
                                        gson.toJson(
                                            response.body()!!.getfPSComplainHistoryReqList()
                                        )

                                    // below line is to save data in shared
                                    // prefs in the form of string.
                                    editor.putString("DtlComp", json)

                                    // below line is to apply changes
                                    // and save data in shared prefs.
                                    editor.apply()
                                } else {
                                    binding!!.rvComplaintsListByFps.visibility = View.GONE
                                    binding!!.textNoRecordFound.visibility = View.VISIBLE
                                    clearSharePreference()
                                }
                            } else {
                                binding!!.rvComplaintsListByFps.visibility = View.GONE
                                binding!!.textNoRecordFound.visibility = View.VISIBLE
                                //makeToast(model.getMessage());
                                clearSharePreference()
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                            clearSharePreference()
                        }
                    }

                    override fun onFailure(call: Call<ModelFPSComplaint>, t: Throwable) {
                        hideProgress()
                        makeToast(resources.getString(R.string.error))
                    }
                })
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        }

    private fun setUpComplaintsListAdapter(modelFPSComplaintLists: List<ModelFPSComplaintList>?) {
        binding!!.rvComplaintsListByFps.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvComplaintsListByFps.adapter =
            ComplaintsListAccordingToFPSActivityAdapter(mContext, modelFPSComplaintLists)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonEXCEL) {
            vibrator!!.vibrate(100)

            Log.d("ListSize", "" + fPSComplainHistoryReqList!!.size)

            if (fPSComplainHistoryReqList != null && fPSComplainHistoryReqList!!.size > 0) {
                ExcelformTable()
            } else {
                Toast.makeText(mContext, "No Data Found", Toast.LENGTH_SHORT).show()
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
        val json = sharedPreferences!!.getString("DtlComp", null)
        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<ModelFPSComplaintList?>?>() {}.type
        // in below line we are getting data from gson
        // and saving it to our array list
        fPSComplainHistoryReqList = gson.fromJson(json, type)
        if (fPSComplainHistoryReqList == null) {
            // if the array list is empty
            // creating a new array list.
            fPSComplainHistoryReqList = ArrayList()
            Log.d("nbb", "" + fPSComplainHistoryReqList)
        }
        Log.d("gfhvbb", "" + fPSComplainHistoryReqList)
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


        val cellF = rowA.createCell(5)
        firstSheet.setColumnWidth(5, 5000)
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        val richText5: RichTextString = HSSFRichTextString("Complain\nRegNo.")
        richText5.applyFont(boldFont)
        cellF.setCellValue(richText5)


        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        val cellG = rowA.createCell(6)
        firstSheet.setColumnWidth(6, 4000)
        val richText6: RichTextString = HSSFRichTextString("ComplainStatus")
        richText6.applyFont(boldFont)
        cellG.setCellValue(richText6)
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        val cellH = rowA.createCell(7)
        firstSheet.setColumnWidth(7, 9000)
        val richText7: RichTextString = HSSFRichTextString("Complain\nDate")
        richText7.applyFont(boldFont)
        cellH.setCellValue(richText7)
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        val cellI = rowA.createCell(8)
        firstSheet.setColumnWidth(8, 9000)
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        val richText8: RichTextString = HSSFRichTextString("Complain\nDesc")
        richText8.applyFont(boldFont)
        cellI.setCellValue(richText8)






        Log.d("mylist", " -------------- $fPSComplainHistoryReqList")
        if (fPSComplainHistoryReqList != null && fPSComplainHistoryReqList!!.size > 0) {
            for (i in fPSComplainHistoryReqList!!.indices) {
                val detailsInfo = fPSComplainHistoryReqList!![i]
                val districtName = detailsInfo.district.toString()
                val teh = detailsInfo.tehsil.toString()
                val fpsCode = detailsInfo.fpscode.toString()
                val customerName = detailsInfo.customerName.toString()
                val mobileNo = detailsInfo.mobileNo.toString()
                val cRegNo = detailsInfo.complainRegNo.toString()


                val complaintStatus = detailsInfo.complainStatus.toString()
                val date = detailsInfo.complainRegDateStr.toString()
                val dis = detailsInfo.complainDesc.toString()
                val dataRow: Row = firstSheet.createRow(i + 1) // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName)
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(teh)
                dataRow.createCell(2).setCellValue(fpsCode)
                dataRow.createCell(3).setCellValue(customerName)
                dataRow.createCell(4).setCellValue(mobileNo)
                dataRow.createCell(5).setCellValue(cRegNo)
                dataRow.createCell(6).setCellValue(complaintStatus)
                dataRow.createCell(7).setCellValue(date)
                dataRow.createCell(8).setCellValue(dis)
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
                this@ComplaintsListAccordingToFPSActivity,
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
                this@ComplaintsListAccordingToFPSActivity,
                BuildConfig.APPLICATION_ID + ".provider",  // Replace with your app's provider authority
                filePathh
            )

            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel")
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(openIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this@ComplaintsListAccordingToFPSActivity,
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
        editor.remove("DtlComp")
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