package com.callmangement.ui.complaints_fps_wise;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.callmangement.BuildConfig;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.ComplaintsListAccordingToFPSActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityComplaintsListByFpsactivityBinding;
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaint;
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaintList;
import com.callmangement.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintsListAccordingToFPSActivity extends CustomActivity implements View.OnClickListener {
    ActivityComplaintsListByFpsactivityBinding binding;
    private String fpsCode = "";
    private Vibrator vibrator;
    String originalFileName = "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);
    private File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    SharedPreferences sharedPreferences;
    ModelFPSComplaint model;
    Activity mActivity;
    private List<ModelFPSComplaintList> fPSComplainHistoryReqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComplaintsListByFpsactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.buttonEXCEL.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.complaints_by_fps));
        initView();
    }

    private void initView() {
        setUpOnclickListener();
        clearSharePreference();

        getIntentData();
        getComplaintsListByFPS();
    }

    private void getIntentData(){
        fpsCode = getIntent().getStringExtra("fps_code");
    }

    private void setUpOnclickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.actionBar.buttonEXCEL.setOnClickListener(this);
    }

    private void getComplaintsListByFPS(){
        if (Constants.isNetworkAvailable(mContext)){
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelFPSComplaint> call = service.getFPSComplainsList(fpsCode);
            showProgress();
            call.enqueue(new Callback<ModelFPSComplaint>() {
                @Override
                public void onResponse(@NonNull Call<ModelFPSComplaint> call, @NonNull Response<ModelFPSComplaint> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        model = response.body();
                        if (Objects.requireNonNull(model).status.equals("200")){

                            fPSComplainHistoryReqList = model.getfPSComplainHistoryReqList();


                            if (fPSComplainHistoryReqList.size() > 0) {
                                binding.rvComplaintsListByFps.setVisibility(View.VISIBLE);
                                binding.textNoRecordFound.setVisibility(View.GONE);
                                setUpComplaintsListAdapter(fPSComplainHistoryReqList);



                                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                                // creating a variable for editor to
                                // store data in shared preferences.
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                // creating a new variable for gson.
                                Gson gson = new Gson();

                                // getting data from gson and storing it in a string.
                                String json = gson.toJson(  response.body().getfPSComplainHistoryReqList());

                                // below line is to save data in shared
                                // prefs in the form of string.
                                editor.putString("DtlComp", json);

                                // below line is to apply changes
                                // and save data in shared prefs.
                                editor.apply();





                            } else {
                                binding.rvComplaintsListByFps.setVisibility(View.GONE);
                                binding.textNoRecordFound.setVisibility(View.VISIBLE);
                                clearSharePreference();
                            }
                        }else {
                            binding.rvComplaintsListByFps.setVisibility(View.GONE);
                            binding.textNoRecordFound.setVisibility(View.VISIBLE);
                            //makeToast(model.getMessage());
                            clearSharePreference();

                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                        clearSharePreference();

                    }
                }
                @Override
                public void onFailure(@NonNull Call<ModelFPSComplaint> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpComplaintsListAdapter(List<ModelFPSComplaintList> modelFPSComplaintLists){
        binding.rvComplaintsListByFps.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvComplaintsListByFps.setAdapter(new ComplaintsListAccordingToFPSActivityAdapter(mContext, modelFPSComplaintLists));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        } else if (id==R.id.buttonEXCEL) {

            vibrator.vibrate(100);

            Log.d("ListSize", ""+fPSComplainHistoryReqList.size());

            if (fPSComplainHistoryReqList != null && fPSComplainHistoryReqList.size() > 0) {
                ExcelformTable();

            }
            else {

                Toast.makeText(mContext, "No Data Found", Toast.LENGTH_SHORT).show();

            }

        }
    }



    private void ExcelformTable() {

        mActivity = this;
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // creating a variable for gson.
        Gson gson = new Gson();
        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("DtlComp", null);
        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<ModelFPSComplaintList>>() {}.getType();
        // in below line we are getting data from gson
        // and saving it to our array list
        fPSComplainHistoryReqList = gson.fromJson(json, type);
        if (fPSComplainHistoryReqList == null) {
            // if the array list is empty
            // creating a new array list.
            fPSComplainHistoryReqList = new ArrayList<>();
            Log.d("nbb", "" + fPSComplainHistoryReqList);
        }
        Log.d("gfhvbb", "" + fPSComplainHistoryReqList);
        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        // String corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat);
        // String corEndDate = convertDateFormat(toDate, originalFormat, desiredFormat);
        //  Log.d("formateddate", "" + corStartDate);
        HSSFWorkbook workbook = new HSSFWorkbook();
        //   HSSFSheet firstSheet = workbook.createSheet("Iris Delivered & Installed " + corStartDate + " - " + corEndDate);
        HSSFSheet firstSheet = workbook.createSheet("Last Complaint List");
        // Create cell style for center alignment
        HSSFRow rowA = firstSheet.createRow(0);
        rowA.setHeightInPoints(20); // Set row height in points
        HSSFCell cellA = rowA.createCell(0);
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
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        // Create a RichTextString with bold formatting
        RichTextString richText = new HSSFRichTextString("District\nName");
        richText.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellA.setCellValue(richText);
        firstSheet.setColumnWidth(0, 4000);
        HSSFCell cellB = rowA.createCell(1);
        RichTextString richText1 = new HSSFRichTextString("Tehsil");
        richText1.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1);
        firstSheet.setColumnWidth(1, 4000);
        //   cellB.setCellStyle(cellStyle);
        HSSFCell cellC = rowA.createCell(2);
        firstSheet.setColumnWidth(2, 4000);
        RichTextString richText2 = new HSSFRichTextString("FPS\nCode");
        richText2.applyFont(boldFont);
        cellC.setCellValue(richText2);
        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        HSSFCell cellD = rowA.createCell(3);
        firstSheet.setColumnWidth(3, 9000);
        RichTextString richText3 = new HSSFRichTextString("Name");
        richText3.applyFont(boldFont);
        cellD.setCellValue(richText3);
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        HSSFCell cellE = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 4000);
        RichTextString richText4 = new HSSFRichTextString("Mobile.N0");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);




        HSSFCell cellF = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 5000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText5 = new HSSFRichTextString("Complain\nRegNo.");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);








        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        HSSFCell cellG = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 4000);
        RichTextString richText6 = new HSSFRichTextString("ComplainStatus");
        richText6.applyFont(boldFont);
        cellG.setCellValue(richText6);
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        HSSFCell cellH = rowA.createCell(7);
        firstSheet.setColumnWidth(7, 9000);
        RichTextString richText7 = new HSSFRichTextString("Complain\nDate");
        richText7.applyFont(boldFont);
        cellH.setCellValue(richText7);
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        HSSFCell cellI = rowA.createCell(8);
        firstSheet.setColumnWidth(8, 9000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText8 = new HSSFRichTextString("Complain\nDesc");
        richText8.applyFont(boldFont);
        cellI.setCellValue(richText8);






        Log.d("mylist", " -------------- " + fPSComplainHistoryReqList);
        if (fPSComplainHistoryReqList != null && fPSComplainHistoryReqList.size() > 0) {
            for (int i = 0; i < fPSComplainHistoryReqList.size(); i++) {
                ModelFPSComplaintList detailsInfo = fPSComplainHistoryReqList.get(i);
                String districtName = String.valueOf(detailsInfo.getDistrict());
                String teh = String.valueOf(detailsInfo.getTehsil());
                String fpsCode = String.valueOf(detailsInfo.getFpscode());
                String customerName = String.valueOf(detailsInfo.getCustomerName());
                String mobileNo = String.valueOf(detailsInfo.getMobileNo());
                String cRegNo = String.valueOf(detailsInfo.getComplainRegNo());


                String complaintStatus = String.valueOf(detailsInfo.getComplainStatus());
                String date = String.valueOf(detailsInfo.getComplainRegDateStr());
                String dis = String.valueOf(detailsInfo.getComplainDesc());
                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(teh);
                dataRow.createCell(2).setCellValue(fpsCode);
                dataRow.createCell(3).setCellValue(customerName);
                dataRow.createCell(4).setCellValue(mobileNo);
                dataRow.createCell(5).setCellValue(cRegNo);
                dataRow.createCell(6).setCellValue(complaintStatus);
                dataRow.createCell(7).setCellValue(date);
                dataRow.createCell(8).setCellValue(dis);
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
            Toast.makeText(ComplaintsListAccordingToFPSActivity.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
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
            Uri fileUri = FileProvider.getUriForFile(
                    ComplaintsListAccordingToFPSActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", // Replace with your app's provider authority
                    filePathh
            );

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel");
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ComplaintsListAccordingToFPSActivity.this, "No app found to open Excel files.", Toast.LENGTH_SHORT).show();
        }



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




    public void clearSharePreference(){
        // super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
// Clearing the value associated with the "camp" key
        editor.remove("DtlComp");
// Applying the changes to save the updated SharedPreferences
        editor.apply();
    }




}