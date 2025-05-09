package com.callmangement.ui.reports

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.DailyReportsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDailyReportsBinding
import com.callmangement.model.complaints.ModelComplaint
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.reports.Monthly_Reports_Info
import com.callmangement.report_pdf.ReportPdfActivity
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.DateTimeUtils
import com.callmangement.utils.EqualSpacingItemDecoration
import com.callmangement.utils.PrefManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects
import java.util.StringTokenizer


class DailyReportsActivity : CustomActivity() {

    
    private var binding: ActivityDailyReportsBinding? = null
    private var prefManager: PrefManager? = null
    private var viewModel: ComplaintViewModel? = null
    private var modelComplaintLists: List<ModelComplaintList>? = null
    private val myFormat = "yyyy-MM-dd"
    private var adapter: DailyReportsActivityAdapter? = null
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val complainStatusId = "0"
    private var districtId = "0"
    private var checkDistrict = 0
    private var districtNameEng = ""
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()
    private var toDate: String? = null


    /*   String originalFileName = "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);
    private File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_reports)

        mContext = this
        prefManager = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE

        // binding.actionBar.buttonEXCEL.setVisibility(View.VISIBLE);
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.daily_reports)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        districtNameEng = prefManager!!.useR_District!!
        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) { // for service engineer
            districtId = prefManager!!.useR_DistrictId!!
            binding!!.rlDistrict.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) { // for dso
            districtId = prefManager!!.useR_DistrictId!!
            binding!!.rlDistrict.visibility = View.GONE
        } else {
            districtList()
            binding!!.rlDistrict.visibility = View.VISIBLE
        }

        initView()
        fetchComplaintList()
    }

    private fun initView() {
        adapter = DailyReportsActivityAdapter(this@DailyReportsActivity)
        binding!!.rvDailyReports.setHasFixedSize(true)
        binding!!.rvDailyReports.layoutManager =
            LinearLayoutManager(this@DailyReportsActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvDailyReports.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvDailyReports.adapter = adapter
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val calendarToday = Calendar.getInstance()
        val date = calendarToday.time
        toDate = sdf.format(date)
        binding!!.textToDate.setText(toDate)
        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtNameEng = district_List!![i]!!.districtNameEng!!
                        districtId = district_List!![i]!!.districtId!!
                        if (districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            fetchComplaintListByDistrictWise("0")
                        } else {
                            fetchComplaintListByDistrictWise(districtId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        val dateToDate =
            OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarToDate[Calendar.YEAR] =
                    year
                myCalendarToDate[Calendar.MONTH] = monthOfYear
                myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabelToDate()
            }

        binding!!.textToDate.setOnClickListener { view: View? ->
            val datePickerDialog = DatePickerDialog(
                mContext!!,
                dateToDate,
                myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        binding!!.actionBar.buttonPDF.setOnClickListener { view: View? ->
            if (getResolvedNotResolvedList(modelComplaintLists) != null && getResolvedNotResolvedList(
                    modelComplaintLists
                ).size > 0
            ) {
                val list = getResolvedNotResolvedList(modelComplaintLists)
                val total = list[0].size + list[1].size
                startActivity(
                    Intent(mContext!!, ReportPdfActivity::class.java)
                        .putExtra("resolved", list[0].size.toString())
                        .putExtra("not_resolved", list[1].size.toString())
                        .putExtra("date", toDate.toString())
                        .putExtra("total", total.toString())
                        .putExtra("from_where", "daily")
                        .putExtra("title", "DAILY CALL LOGGED SUMMARY")
                        .putExtra(
                            "district",
                            if (districtNameEng == "") "All District" else districtNameEng
                        )
                        .putExtra("name", prefManager!!.useR_NAME)
                        .putExtra("email", prefManager!!.useR_EMAIL)
                )
            } else {
                Toast.makeText(
                    mContext!!,
                    resources.getString(R.string.no_record_found_to_export_pdf),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        /*     binding.actionBar.buttonEXCEL.setOnClickListener(view -> {
  */
        /*       if (getResolvedNotResolvedList(modelComplaintLists) != null && getResolvedNotResolvedList(modelComplaintLists).size() > 0) {
                List<List<ModelComplaintList>> list = getResolvedNotResolvedList(modelComplaintLists);
                int total = list.get(0).size() + list.get(1).size();
                startActivity(new Intent(mContext!!, ReportPdfActivity.class)
                        .putExtra("resolved",String.valueOf(list.get(0).size()))
                        .putExtra("not_resolved",String.valueOf(list.get(1).size()))
                        .putExtra("date",String.valueOf(toDate))
                        .putExtra("total",String.valueOf(total))
                        .putExtra("from_where", "daily")
                        .putExtra("title", "DAILY CALL LOGGED SUMMARY")
                        .putExtra("district", districtNameEng.equals("") ? "All District":districtNameEng)
                        .putExtra("name", prefManager.getUSER_NAME())
                        .putExtra("email", prefManager.getUSER_EMAIL()));
            } else {
                Toast.makeText(mContext!!, getResources().getString(R.string.no_record_found_to_export_pdf), Toast.LENGTH_SHORT).show();
            }*/
        /*


            if (getResolvedNotResolvedList(modelComplaintLists) != null && getResolvedNotResolvedList(modelComplaintLists).size() > 0) {

                ExcelformTable();

            }
            else {

                Toast.makeText(mContext!!, "No record found to export excel", Toast.LENGTH_SHORT).show();

            }




        });
*/
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }

    private fun updateLabelToDate() {
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        toDate = sdf.format(myCalendarToDate.time)
        fetchComplaintListByDateRange()
    }

    private fun fetchComplaintList() {
        val sdf_in_api = SimpleDateFormat(myFormat, Locale.US)
        val calendarToday = Calendar.getInstance()
        val today = calendarToday.time
        val todayDateInAPI = sdf_in_api.format(today)
        toDate = todayDateInAPI
        isLoading
        viewModel!!.getComplaintsDistStatusDateWise(
            prefManager!!.useR_Id.toString(),
            districtId,
            complainStatusId,
            toDate,
            toDate,
            "",
            "",
            "",
            ""
        ).observe(
            this
        ) { modelComplaint: ModelComplaint? ->
            isLoading
            if (modelComplaint!!.status == "200") {
                modelComplaintLists = modelComplaint.complaint_List
                setUpData()
            }
        }
    }

    private fun fetchComplaintListByDateRange() {
        toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        isLoading
        viewModel!!.getComplaintsDistStatusDateWise(
            prefManager!!.useR_Id.toString(),
            districtId,
            complainStatusId,
            toDate,
            toDate,
            "",
            "",
            "",
            ""
        ).observe(
            this
        ) { modelComplaint: ModelComplaint? ->
            isLoading
            if (modelComplaint!!.status == "200") {
                modelComplaintLists = modelComplaint.complaint_List
                setUpData()
            }
        }
    }

    private fun fetchComplaintListByDistrictWise(districtId: String) {
        isLoading
        viewModel!!.getComplaintsDistStatusDateWise(
            prefManager!!.useR_Id.toString(),
            districtId,
            complainStatusId,
            toDate,
            toDate,
            "",
            "",
            "",
            ""
        ).observe(
            this
        ) { modelComplaint: ModelComplaint? ->
            isLoading
            if (modelComplaint!!.status == "200") {
                modelComplaintLists = modelComplaint.complaint_List
                setUpData()
            }
        }
    }

    private fun setUpData() {
        if (modelComplaintLists!!.size > 0) {
            val list = getResolvedNotResolvedList(modelComplaintLists)
            binding!!.rvDailyReports.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            adapter!!.setData(list, modelComplaintLists, toDate)
        } else {
            binding!!.rvDailyReports.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
        }
    }

    private fun getResolvedNotResolvedList(totalList: List<ModelComplaintList>?): List<List<ModelComplaintList>> {
        val resolvedList: MutableList<ModelComplaintList> = ArrayList()
        val notResolvedList: MutableList<ModelComplaintList> = ArrayList()
        val resolveAndNotResolvedList: MutableList<List<ModelComplaintList>> = ArrayList()
        try {
            if (totalList != null) {
                if (totalList.size > 0) {
                    for (model in totalList) {
                        if (model.complainStatusId == "3" && DateTimeUtils.getTimeStamp(
                                formattedFilterDate()
                            ) == DateTimeUtils.getTimeStamp(model.sermarkDateStr)
                        ) resolvedList.add(model)
                        else notResolvedList.add(model)
                    }
                    resolveAndNotResolvedList.add(resolvedList)
                    resolveAndNotResolvedList.add(notResolvedList)
                }
            }
            return resolveAndNotResolvedList
        } catch (e: Exception) {
            e.printStackTrace()
            return resolveAndNotResolvedList
        }
    }

    private fun formattedFilterDate(): String {
        val tokenizer = StringTokenizer(toDate, "-")
        val year = tokenizer.nextToken()
        val month = tokenizer.nextToken()
        val date = tokenizer.nextToken()
        return "$date-$month-$year"
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

                    val dataAdapter =
                        ArrayAdapter(
                            mContext!!, android.R.layout.simple_spinner_item,
                            district_List!!
                        )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter
                }
            }
        }
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

    inner class CustomComparator : Comparator<Monthly_Reports_Info> {
        override fun compare(o1: Monthly_Reports_Info, o2: Monthly_Reports_Info): Int {
            if (o1.date != null && o2.date != null) {
                return o1.date.compareTo(o2.date)
            }

            return 0
        }
    } //Excel code
    /* private void ExcelformTable() {
        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        // mycomment
        String corStartDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        String corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        //  Log.d("formateddate", "" + corStartDate);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Distributed Photo Upload Status " + corStartDate + " - " + corEndDate);
        // HSSFSheet firstSheet = workbook.createSheet("Distributed Photo Upload Status" );


        HSSFRow rowA = firstSheet.createRow(0);
        rowA.setHeightInPoints(20); // Set row height in points
        HSSFCell cellA = rowA.createCell(0);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);

        RichTextString richText = new HSSFRichTextString("Name");
        richText.applyFont(boldFont);
        cellA.setCellValue(richText);
        firstSheet.setColumnWidth(0, 4000);

        HSSFCell cellB = rowA.createCell(1);
        RichTextString richText1 = new HSSFRichTextString("Email");
        richText1.applyFont(boldFont);
        cellB.setCellValue(richText1);
        firstSheet.setColumnWidth(1, 6000);


        HSSFCell cellC = rowA.createCell(2);
        firstSheet.setColumnWidth(2, 4000);
        RichTextString richText2 = new HSSFRichTextString("District\nName");
        richText2.applyFont(boldFont);
        cellC.setCellValue(richText2);







        HSSFCell cellD = rowA.createCell(3);
        firstSheet.setColumnWidth(3, 5000);
        RichTextString richText21 = new HSSFRichTextString("Date");
        richText21.applyFont(boldFont);
        cellD.setCellValue(richText21);









        HSSFCell cellD2 = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 8000);
        RichTextString richText3 = new HSSFRichTextString("Total");
        richText3.applyFont(boldFont);
        cellD2.setCellValue(richText3);





        HSSFCell cellE = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 4000);
        RichTextString richText4 = new HSSFRichTextString("Resolved");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);








        HSSFCell cellF = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 4000);
        RichTextString richText5 = new HSSFRichTextString("NotResolved");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);



        Log.d("mylist", " -------------- " + getResolvedNotResolvedList(modelComplaintLists));
        if (getResolvedNotResolvedList(modelComplaintLists) != null && getResolvedNotResolvedList(modelComplaintLists).size() > 0) {








            for (int i = 0; i < modelComplaintLists.size(); i++) {

                List<List<ModelComplaintList>> list = getResolvedNotResolvedList(modelComplaintLists);
                int totall = list.get(0).size() + list.get(1).size();


                String resolved = String.valueOf(list.get(0).size());
                String not_resolved = String.valueOf(list.get(1).size());
                String date = String.valueOf(toDate);
                String total = String.valueOf(totall);
                String from_where = String.valueOf("daily");
                String title = "DAILY CALL LOGGED SUMMARY";

                String district = districtNameEng.equals("") ? "All District":districtNameEng;


                String name = prefManager.getUSER_NAME();
                String email = prefManager.getUSER_EMAIL();

                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(name);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(email);
                dataRow.createCell(2).setCellValue(district);
                dataRow.createCell(3).setCellValue(date);
                dataRow.createCell(4).setCellValue(total);
                dataRow.createCell(5).setCellValue(resolved);
                dataRow.createCell(6).setCellValue(not_resolved);

            }
        }


        FileOutputStream fos = null;
        try {
            String str_path = Environment.getExternalStorageDirectory().toString();
            File file;
            file = new File(str_path, getString(R.string.app_name) + ".xls");
            fos = new FileOutputStream(filePathh);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(DailyReportsActivity.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
        }



        try {
            Uri fileUri = FileProvider.getUriForFile(
                    DailyReportsActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", // Replace with your app's provider authority
                    filePathh
            );

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel");
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(DailyReportsActivity.this, "No app found to open Excel files.", Toast.LENGTH_SHORT).show();
        }


    }

    private String generateUniqueFileName(String originalFileName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileExtension = getFileExtension(originalFileName);
        return "excel_" + timeStamp + "." + fileExtension;
    }
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
    private static String convertDateFormat(String dateString, String originalFormat, String desiredFormat) {
        SimpleDateFormat originalDateFormat = new SimpleDateFormat(originalFormat, Locale.US);
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat(desiredFormat, Locale.US);

        try {
            // Parse the original date string into a Date object
            Date date = originalDateFormat.parse(dateString);

            // Format the Date object into the desired format
            return desiredDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return an empty string if there's an error in parsing or formatting
        }
    }



*/
}