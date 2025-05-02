package com.callmangement.ui.reports;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.R;
import com.callmangement.adapter.DailyReportsActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDailyReportsBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.reports.Monthly_Reports_Info;
import com.callmangement.report_pdf.ReportPdfActivity;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.utils.EqualSpacingItemDecoration;
import com.callmangement.utils.PrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

public class DailyReportsActivity extends CustomActivity {
    ActivityDailyReportsBinding binding;
    private PrefManager prefManager;
    private ComplaintViewModel viewModel;
    private List<ModelComplaintList> modelComplaintLists;
    private final String myFormat = "yyyy-MM-dd";
    private DailyReportsActivityAdapter adapter;
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final String complainStatusId = "0";
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private String toDate;


 /*   String originalFileName = "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);
    private File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_reports);
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
       // binding.actionBar.buttonEXCEL.setVisibility(View.VISIBLE);

        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.daily_reports));
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        districtNameEng = prefManager.getUSER_District();
        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
            districtId = prefManager.getUSER_DistrictId();
            binding.rlDistrict.setVisibility(View.GONE);
        } else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){ // for dso
            districtId = prefManager.getUSER_DistrictId();
            binding.rlDistrict.setVisibility(View.GONE);
        } else{
            districtList();
            binding.rlDistrict.setVisibility(View.VISIBLE);
        }

        initView();
        fetchComplaintList();
    }

    private void initView() {

        adapter = new DailyReportsActivityAdapter(DailyReportsActivity.this);
        binding.rvDailyReports.setHasFixedSize(true);
        binding.rvDailyReports.setLayoutManager(new LinearLayoutManager(DailyReportsActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvDailyReports.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvDailyReports.setAdapter(adapter);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Calendar calendarToday = Calendar.getInstance();
        Date date = calendarToday.getTime();
        toDate = sdf.format(date);
        binding.textToDate.setText(toDate);
        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    districtId = district_List.get(i).getDistrictId();
                    if (districtNameEng.equalsIgnoreCase("--"+getResources().getString(R.string.district)+"--")) {
                        fetchComplaintListByDistrictWise("0");
                    } else {
                        fetchComplaintListByDistrictWise(districtId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatePickerDialog.OnDateSetListener dateToDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarToDate.set(Calendar.YEAR, year);
            myCalendarToDate.set(Calendar.MONTH, monthOfYear);
            myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelToDate();
        };

        binding.textToDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        binding.actionBar.buttonPDF.setOnClickListener(view -> {
            if (getResolvedNotResolvedList(modelComplaintLists) != null && getResolvedNotResolvedList(modelComplaintLists).size() > 0) {
                List<List<ModelComplaintList>> list = getResolvedNotResolvedList(modelComplaintLists);
                int total = list.get(0).size() + list.get(1).size();
                startActivity(new Intent(mContext, ReportPdfActivity.class)
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
                Toast.makeText(mContext, getResources().getString(R.string.no_record_found_to_export_pdf), Toast.LENGTH_SHORT).show();
            }
        });



   /*     binding.actionBar.buttonEXCEL.setOnClickListener(view -> {
     *//*       if (getResolvedNotResolvedList(modelComplaintLists) != null && getResolvedNotResolvedList(modelComplaintLists).size() > 0) {
                List<List<ModelComplaintList>> list = getResolvedNotResolvedList(modelComplaintLists);
                int total = list.get(0).size() + list.get(1).size();
                startActivity(new Intent(mContext, ReportPdfActivity.class)
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
                Toast.makeText(mContext, getResources().getString(R.string.no_record_found_to_export_pdf), Toast.LENGTH_SHORT).show();
            }*//*


            if (getResolvedNotResolvedList(modelComplaintLists) != null && getResolvedNotResolvedList(modelComplaintLists).size() > 0) {

                ExcelformTable();

            }
            else {

                Toast.makeText(mContext, "No record found to export excel", Toast.LENGTH_SHORT).show();

            }




        });
*/

        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
    }

    private void updateLabelToDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        toDate = sdf.format(myCalendarToDate.getTime());
        fetchComplaintListByDateRange();
    }

    private void fetchComplaintList() {
        SimpleDateFormat sdf_in_api = new SimpleDateFormat(myFormat, Locale.US);
        Calendar calendarToday = Calendar.getInstance();
        Date today = calendarToday.getTime();
        String todayDateInAPI = sdf_in_api.format(today);
        toDate = todayDateInAPI;
        isLoading();
        viewModel.getComplaintsDistStatusDateWise(String.valueOf(prefManager.getUSER_Id()), districtId, complainStatusId, toDate, toDate,"","","","").observe(this, modelComplaint -> {
            isLoading();
            if (modelComplaint.getStatus().equals("200")) {
                modelComplaintLists = modelComplaint.getComplaint_List();
                setUpData();
            }
        });
    }

    private void fetchComplaintListByDateRange() {
        toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
        isLoading();
        viewModel.getComplaintsDistStatusDateWise(String.valueOf(prefManager.getUSER_Id()), districtId, complainStatusId, toDate, toDate,"","","","").observe(this, modelComplaint -> {
            isLoading();
            if (modelComplaint.getStatus().equals("200")) {
                modelComplaintLists = modelComplaint.getComplaint_List();
                setUpData();
            }
        });
    }

    private void fetchComplaintListByDistrictWise(String districtId) {
        isLoading();
        viewModel.getComplaintsDistStatusDateWise(String.valueOf(prefManager.getUSER_Id()), districtId, complainStatusId, toDate, toDate,"","","","").observe(this, modelComplaint -> {
            isLoading();
            if (modelComplaint.getStatus().equals("200")) {
                modelComplaintLists = modelComplaint.getComplaint_List();
                setUpData();
            }
        });
    }

    private void setUpData() {
        if (modelComplaintLists.size() > 0) {
            List<List<ModelComplaintList>> list = getResolvedNotResolvedList(modelComplaintLists);
            binding.rvDailyReports.setVisibility(View.VISIBLE);
            binding.textNoComplaint.setVisibility(View.GONE);
            adapter.setData(list,modelComplaintLists, toDate);
        } else {
            binding.rvDailyReports.setVisibility(View.GONE);
            binding.textNoComplaint.setVisibility(View.VISIBLE);
        }
    }

    private List<List<ModelComplaintList>> getResolvedNotResolvedList(List<ModelComplaintList> totalList) {
        List<ModelComplaintList> resolvedList = new ArrayList<>();
        List<ModelComplaintList> notResolvedList = new ArrayList<>();
        List<List<ModelComplaintList>> resolveAndNotResolvedList = new ArrayList<>();
        try {
            if (totalList != null) {
                if (totalList.size() > 0) {
                    for (ModelComplaintList model : totalList) {
                        if (model.getComplainStatusId().equals("3") && DateTimeUtils.getTimeStamp(formattedFilterDate()) == DateTimeUtils.getTimeStamp(model.getSermarkDateStr()))
                            resolvedList.add(model);
                        else notResolvedList.add(model);
                    }
                    resolveAndNotResolvedList.add(resolvedList);
                    resolveAndNotResolvedList.add(notResolvedList);
                }
            }
            return resolveAndNotResolvedList;
        } catch (Exception e) {
            e.printStackTrace();
            return resolveAndNotResolvedList;
        }
    }

    private String formattedFilterDate(){
        StringTokenizer tokenizer = new StringTokenizer(toDate,"-");
        String year = tokenizer.nextToken();
        String month = tokenizer.nextToken();
        String date = tokenizer.nextToken();
        return date+"-"+month+"-"+year;
    }

    private void districtList(){
        viewModel.getDistrict().observe(this, modelDistrict -> {
            if (modelDistrict.getStatus().equals("200")){
                district_List = modelDistrict.getDistrict_List();

                if (district_List != null && district_List.size() > 0) {
                    Collections.reverse(district_List);
                    ModelDistrictList l = new ModelDistrictList();
                    l.setDistrictId(String.valueOf(-1));
                    l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                    district_List.add(l);
                    Collections.reverse(district_List);

                    ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, district_List);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerDistrict.setAdapter(dataAdapter);
                }
            }
        });
    }

    private void isLoading() {
        viewModel.getIsLoading().observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress(getResources().getString(R.string.please_wait));
            } else {
                hideProgress();
            }
        });
    }

    public class CustomComparator implements Comparator<Monthly_Reports_Info> {
        @Override
        public int compare(Monthly_Reports_Info o1, Monthly_Reports_Info o2) {

            if (o1.getDate() != null && o2.getDate() != null) {
                return o1.getDate().compareTo(o2.getDate());
            }

            return 0;
        }
    }



    //Excel code



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