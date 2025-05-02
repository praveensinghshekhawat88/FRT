package com.callmangement.ui.complaints_fps_wise;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.callmangement.BuildConfig;
import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.LastComplaintFPSListActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityLastComplaintFpslistBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWise;
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWiseList;
import com.callmangement.model.tehsil.ModelTehsil;
import com.callmangement.model.tehsil.ModelTehsilList;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.ui.iris_derivery_installation.IrisInstalledListActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
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
public class LastComplaintFPSListActivity extends CustomActivity implements View.OnClickListener {
    ActivityLastComplaintFpslistBinding binding;
    private PrefManager prefManager;
    private String districtId = "0";
    private String tehsilId = "0";
    private String fpsCode = "";
    private List<ModelTehsilList> tehsilList = new ArrayList<>();
    private List<ModelDistrictList> districtList = new ArrayList<>();
    private List<ModelFPSDistTehWiseList> modelFPSDistTehWiseList = new ArrayList<>();
    private final int checkTehsil = 0;
    private final int checkDistrict = 0;
    private String tehsilNameEng = "";
    private String districtNameEng = "";
    private ComplaintViewModel viewModel;
    Activity mActivity;
    private Vibrator vibrator;
    String originalFileName = "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);
    private File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLastComplaintFpslistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.last_complain_list));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        binding.actionBar.buttonEXCEL.setVisibility(View.VISIBLE);
        prefManager = new PrefManager(mContext);
        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);
        initView();
    }

    private void initView() {
        mActivity = this;
        setUpOnclickListener();
        clearSharePreference();
        setUpData();
        districtList();
    }

    private void setUpOnclickListener(){
        binding.buttonGetDetails.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.actionBar.buttonEXCEL.setOnClickListener(this);
    }

    private void setUpData() {
        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!districtList.get(i).getDistrictId().equals("0")){
                    binding.rvFpsList.setVisibility(View.GONE);
                    binding.textNoRecordFound.setVisibility(View.VISIBLE);
                    districtNameEng = districtList.get(i).getDistrictNameEng();
                    districtId = districtList.get(i).getDistrictId();
                    tehsilList(districtId);
                }else{
                    districtNameEng = "";
                    districtId = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!tehsilList.get(i).getTehsilId().equals("0")){
                    tehsilNameEng = tehsilList.get(i).getTehsilNameEng();
                    tehsilId = tehsilList.get(i).getTehsilId();
                    getFpsList();
                }else {
                    tehsilNameEng = "";
                    tehsilId = "0";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void districtList() {
        isLoading();
        viewModel.getDistrict().observe(this, modelDistrict -> {
            isLoading();
            if (modelDistrict.getStatus().equals("200")) {
                districtList.clear();
                districtList = modelDistrict.getDistrict_List();
                if (districtList != null && districtList.size() > 0) {
                    ModelDistrictList modelDistrictList = new ModelDistrictList();
                    modelDistrictList.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                    districtList.add(0,modelDistrictList);
                    ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, districtList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerDistrict.setAdapter(dataAdapter);

                    tehsilList.clear();
                    ModelTehsilList modelTehsilList = new ModelTehsilList();
                    modelTehsilList.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
                    tehsilList.add(0,modelTehsilList);
                    ArrayAdapter<ModelTehsilList> dataAdapter1 = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, tehsilList);
                    dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerTehsil.setAdapter(dataAdapter1);

                }
            }
        });
    }

    private void tehsilList(String districtId) {
        if (Constants.isNetworkAvailable(mActivity)){
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelTehsil> call = service.apiGetTehsilByDistict(districtId);
            call.enqueue(new Callback<ModelTehsil>() {
                @Override
                public void onResponse(@NonNull Call<ModelTehsil> call, @NonNull Response<ModelTehsil> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        ModelTehsil model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")) {
                            tehsilList.clear();
                            tehsilList = model.getTehsil_List();
                            if (tehsilList != null && tehsilList.size() > 0) {
                                ModelTehsilList modelTehsilList = new ModelTehsilList();
                                modelTehsilList.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
                                tehsilList.add(0,modelTehsilList);
                                ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, tehsilList);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                binding.spinnerTehsil.setAdapter(dataAdapter);
                            }

                        }else {
                            Toast.makeText(mActivity, model.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(mActivity, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelTehsil> call, @NonNull Throwable t) {
                    hideProgress();
                    Toast.makeText(mActivity, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(mActivity, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }


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

    private void getFpsList(){
        if (Constants.isNetworkAvailable(mContext)) {
            modelFPSDistTehWiseList.clear();

            fpsCode = Objects.requireNonNull(binding.inputFpsCode.getText()).toString().trim();

            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            //Toast.makeText(LastComplaintFPSListActivity.this, "fpsCode:"+fpsCode+" Tehsil:"+tehsilId+" disrictId:"+districtId, Toast.LENGTH_SHORT).show();
            Call<ModelFPSDistTehWise> call = service.getFPSListDisTehWise(fpsCode, districtId, tehsilId);
            showProgress();
            call.enqueue(new Callback<ModelFPSDistTehWise>() {
                @Override
                public void onResponse(@NonNull Call<ModelFPSDistTehWise> call, @NonNull Response<ModelFPSDistTehWise> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        ModelFPSDistTehWise model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")){
                            modelFPSDistTehWiseList = model.getModelFPSDistTehWiseList();
                            if (modelFPSDistTehWiseList.size() > 0) {
                                binding.rvFpsList.setVisibility(View.VISIBLE);
                                binding.textNoRecordFound.setVisibility(View.GONE);
                                setUpFPSListAdapter(modelFPSDistTehWiseList);
                                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                                // creating a variable for editor to
                                // store data in shared preferences.
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                // creating a new variable for gson.
                                Gson gson = new Gson();
                                // getting data from gson and storing it in a string.
                                String json = gson.toJson(  response.body().getModelFPSDistTehWiseList());
                                // below line is to save data in shared
                                // prefs in the form of string.
                                editor.putString("LastComp", json);
                                // below line is to apply changes
                                // and save data in shared prefs.
                                editor.apply();
                            } else {
                                binding.rvFpsList.setVisibility(View.GONE);
                                binding.textNoRecordFound.setVisibility(View.VISIBLE);
                                clearSharePreference();

                            }
                        } else {
                            binding.rvFpsList.setVisibility(View.GONE);
                            binding.textNoRecordFound.setVisibility(View.VISIBLE);
                            clearSharePreference();

                            //makeToastLong(model.getMessage());
                        }
                    }else {
                        clearSharePreference();

                        makeToast(getResources().getString(R.string.error));
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ModelFPSDistTehWise> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error));
                }
            });
        }else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpFPSListAdapter(List<ModelFPSDistTehWiseList> modelFPSDistTehWiseList){
        binding.rvFpsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.rvFpsList.setAdapter(new LastComplaintFPSListActivityAdapter(mContext, modelFPSDistTehWiseList));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        }else if (id == R.id.buttonGetDetails){
            getFpsList();
        } else if (id==R.id.buttonEXCEL) {


            vibrator.vibrate(100);

            Log.d("ListSize", ""+modelFPSDistTehWiseList.size());

            if (modelFPSDistTehWiseList != null && modelFPSDistTehWiseList.size() > 0) {
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
        String json = sharedPreferences.getString("LastComp", null);
        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<ModelFPSDistTehWiseList>>() {}.getType();
        // in below line we are getting data from gson
        // and saving it to our array list
        modelFPSDistTehWiseList = gson.fromJson(json, type);
        if (modelFPSDistTehWiseList == null) {
            // if the array list is empty
            // creating a new array list.
            modelFPSDistTehWiseList = new ArrayList<>();
            Log.d("nbb", "" + modelFPSDistTehWiseList);
        }
        Log.d("gfhvbb", "" + modelFPSDistTehWiseList);
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


        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        HSSFCell cellF = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 4000);
        RichTextString richText5 = new HSSFRichTextString("ComplainStatus");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        HSSFCell cellG = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 9000);
        RichTextString richText6 = new HSSFRichTextString("Complain\nDate");
        richText6.applyFont(boldFont);
        cellG.setCellValue(richText6);
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        HSSFCell cellH = rowA.createCell(7);
        firstSheet.setColumnWidth(7, 9000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText7 = new HSSFRichTextString("Complain\nDesc");
        richText7.applyFont(boldFont);
        cellH.setCellValue(richText7);




        Log.d("mylist", " -------------- " + modelFPSDistTehWiseList);
        if (modelFPSDistTehWiseList != null && modelFPSDistTehWiseList.size() > 0) {
            for (int i = 0; i < modelFPSDistTehWiseList.size(); i++) {
                ModelFPSDistTehWiseList detailsInfo = modelFPSDistTehWiseList.get(i);
                String districtName = String.valueOf(detailsInfo.getDistrictName());
                String teh = String.valueOf(detailsInfo.getTehsilName());
                String fpsCode = String.valueOf(detailsInfo.getFpscode());
                String customerName = String.valueOf(detailsInfo.getCustomerNameEng());
                String mobileNo = String.valueOf(detailsInfo.getMobileNo());
                String complaintStatus = String.valueOf(detailsInfo.getComplainStatus());
                String date = String.valueOf(detailsInfo.getComplainRegDate());
                String dis = String.valueOf(detailsInfo.getComplainDesc());
                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(teh);
                dataRow.createCell(2).setCellValue(fpsCode);
                dataRow.createCell(3).setCellValue(customerName);
                dataRow.createCell(4).setCellValue(mobileNo);
                dataRow.createCell(5).setCellValue(complaintStatus);
                dataRow.createCell(6).setCellValue(date);
                dataRow.createCell(7).setCellValue(dis);
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
            Toast.makeText(LastComplaintFPSListActivity.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
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
                    LastComplaintFPSListActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", // Replace with your app's provider authority
                    filePathh
            );

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel");
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(LastComplaintFPSListActivity.this, "No app found to open Excel files.", Toast.LENGTH_SHORT).show();
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
        editor.remove("LastComp");
// Applying the changes to save the updated SharedPreferences
        editor.apply();
    }




















}