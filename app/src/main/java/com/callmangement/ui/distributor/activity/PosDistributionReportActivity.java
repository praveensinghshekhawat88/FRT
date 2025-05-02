package com.callmangement.ui.distributor.activity;

import static com.callmangement.utils.Constants.posDistributionDetailsList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.callmangement.BuildConfig;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.PosDistributionReportActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityPosDistributionReportBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.report_pdf.DistributedStatusReportPdfActivity;
import com.callmangement.ui.complaint.ComplaintViewModel;
import com.callmangement.ui.distributor.adapter.UploadImageActivityAdapter;
import com.callmangement.ui.distributor.model.PosDistributionDetail;
import com.callmangement.ui.distributor.model.PosDistributionListResponse;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosDistributionReportActivity extends CustomActivity implements View.OnClickListener {
    private ActivityPosDistributionReportBinding binding;
    private PrefManager prefManager;
    private ComplaintViewModel viewModel;
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final List<String> spinnerList = new ArrayList<>();
    private final String myFormat = "yyyy-MM-dd";
    private int checkFilter = 0;
    private int checkDistrict = 0;
    private String districtId = "0";
    private String fromDate = "";
    private String toDate = "";
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private final List<PosDistributionDetail> userReportList = new ArrayList<>();
    String fpscodee,ticketNumber;
    String  originalFileName= "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);
    private File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosDistributionReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        prefManager = new PrefManager(mContext);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.buttonEXCEL.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.pos_distribution_report));
        fpscodee = binding.inputFpsCode.getText().toString();
        ticketNumber = binding.inputTicketNumber.getText().toString();

        viewModel = ViewModelProviders.of(this).get(ComplaintViewModel.class);

        setUpClickListener();
        setUpData();
        districtList();
    }

    private void setUpData() {
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        spinnerList.add(getResources().getString(R.string.today));
        spinnerList.add(getResources().getString(R.string.yesterday));
        spinnerList.add(getResources().getString(R.string.current_month));
        spinnerList.add(getResources().getString(R.string.previous_month));
        spinnerList.add(getResources().getString(R.string.custom_filter));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);

        //my code
        binding.spinner.setSelection(1);
        int selectedItemPosition = binding.spinner.getSelectedItemPosition();
        if (selectedItemPosition == 1) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String todayDate = sdf.format(today);
            fromDate = todayDate;
            toDate = todayDate;
        } else {
        }
//my code

    }

    private void setUpClickListener(){
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkFilter > 1) {
                    String item = adapterView.getItemAtPosition(i).toString();
                    if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        if (item.equalsIgnoreCase(getResources().getString(R.string.today))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            Date today = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String todayDate = sdf.format(today);
                            fromDate = todayDate;
                            toDate = todayDate;

                            getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fromDate = yesterdayDate;
                            toDate = yesterdayDate;
                            getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date firstDateOfCurrentMonth = calendar.getTime();
                            String date1 = sdf.format(firstDateOfCurrentMonth);
                            Calendar calendar1 = Calendar.getInstance();
                            Date currentDateOfCurrentMonth = calendar1.getTime();
                            String date2 = sdf.format(currentDateOfCurrentMonth);
                            fromDate = date1;
                            toDate = date2;
                            getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            Calendar aCalendar = Calendar.getInstance();
                            aCalendar.add(Calendar.MONTH, -1);
                            aCalendar.set(Calendar.DATE, 1);
                            Date firstDateOfPreviousMonth = aCalendar.getTime();
                            String date1 = sdf.format(firstDateOfPreviousMonth);

                            aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            Date lastDateOfPreviousMonth = aCalendar.getTime();
                            String date2 = sdf.format(lastDateOfPreviousMonth);

                            fromDate = date1;
                            toDate = date2;

                            getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        fromDate = "";
                        toDate = "";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkDistrict > 1) {
                    districtId = district_List.get(i).getDistrictId();
                    getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

     /*   binding.inputFpsCode.addTextChangedListener(new TextWatcher() {
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
        });*/

        binding.actionBar.ivBack.setOnClickListener(this);
        binding.actionBar.buttonPDF.setOnClickListener(this);
        binding.actionBar.buttonEXCEL.setOnClickListener(this);
        binding.textFromDate.setOnClickListener(this);
        binding.textToDate.setOnClickListener(this);


        binding.btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fpscodee = binding.inputFpsCode.getText().toString();
                ticketNumber = binding.inputTicketNumber.getText().toString();
                getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);

                /*if (fpscodee != null && !fpscodee.isEmpty()) {
                    getReportList(districtId,fromDate,toDate,fpscodee);
                }
                else {
                }*/
                //   getIrisWeighInstallation(districtId,fromDate,toDate,fpscodee);
            }
        });

    }

    private void updateLabelFromDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()));
        Objects.requireNonNull(binding.textToDate.getText()).clear();
        fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
    }

    private void updateLabelToDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
       // getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);
    }

    private void districtList() {
        viewModel.getDistrict().observe(this, modelDistrict -> {
            if (modelDistrict.getStatus().equals("200")) {
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

    private void getReportList(String districtId, String fromDate, String toDate, String fpscodee,String ticketNumber) {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress();
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);

            Call<ResponseBody> call = service.getPosDistributionListAPI(districtId, prefManager.getUSER_Id(), "0",fpscodee,ticketNumber,fromDate,toDate);
            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    String responseStr = response.body().string();
                              Log.e("response", responseStr);
                                    JSONObject jsonObject = new JSONObject((responseStr));
                                    String status = jsonObject.optString("status");
                                    JSONArray distributionListJsonArr = jsonObject.optJSONArray("posDistributionDetailList");
                                    if (distributionListJsonArr != null){
                                        PosDistributionListResponse modelResponse = (PosDistributionListResponse) getObject(responseStr, PosDistributionListResponse.class);
                                        if (modelResponse != null){
                                            if (status.equals("200")) {
                                                if (modelResponse.getPosDistributionDetailList().size() > 0) {
                                                    userReportList.clear();
                                                    userReportList.addAll(modelResponse.getPosDistributionDetailList());
                                                    Constants.posDistributionDetailsList = modelResponse.getPosDistributionDetailList();
                                                    setAdapter(userReportList);
                                                } else {
                                                    binding.recyclerView.setVisibility(View.GONE);
                                                    binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                binding.recyclerView.setVisibility(View.GONE);
                                                binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            binding.recyclerView.setVisibility(View.GONE);
                                            binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        binding.recyclerView.setVisibility(View.GONE);
                                        binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    binding.recyclerView.setVisibility(View.GONE);
                                    binding.tvNoDataFound.setVisibility(View.VISIBLE);
                                }
                            } else {
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.tvNoDataFound.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.tvNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setAdapter(List<PosDistributionDetail> list){
        if (list.size() > 0){
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.tvNoDataFound.setVisibility(View.GONE);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
            binding.recyclerView.setAdapter(new PosDistributionReportActivityAdapter(mContext,list));
            binding.textTotalCount.setText(""+list.size());
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.tvNoDataFound.setVisibility(View.VISIBLE);
            binding.textTotalCount.setText("0");
        }
    }

    public void uploadImage(String fpsCode,String tranId,String districtId,String flagType){
        Intent intent = new Intent(mContext, UploadPhotoActivity.class);
        intent.putExtra("fps_code",fpsCode);
        intent.putExtra("tran_id",tranId);
        intent.putExtra("district_id",districtId);
        intent.putExtra("flagType",flagType);
        startActivity(intent);
    }

    public void posDistributionFormView(PosDistributionDetail model){
        startActivity(new Intent(mContext, PosDistributionFormViewActivity.class).putExtra("param", model));
    }

    @Override
    protected void onResume() {
        super.onResume();
       // getReportList(districtId,fromDate,toDate,fpscodee,ticketNumber);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();

        } else if (id == R.id.textFromDate) {
            DatePickerDialog.OnDateSetListener dateFromDate = (view1, year, monthOfYear, dayOfMonth) -> {
                myCalendarFromDate.set(Calendar.YEAR, year);
                myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFromDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        } else if (id == R.id.textToDate) {
            DatePickerDialog.OnDateSetListener dateToDate = (view1, year, monthOfYear, dayOfMonth) -> {
                myCalendarToDate.set(Calendar.YEAR, year);
                myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelToDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        } else if (id == R.id.buttonPDF){
            if (Constants.posDistributionDetailsList != null && Constants.posDistributionDetailsList.size() > 0) {
                startActivity(new Intent(mContext, DistributedStatusReportPdfActivity.class));
                finish();
            }
        }


        else if (id == R.id.buttonEXCEL){
            if (Constants.posDistributionDetailsList != null && Constants.posDistributionDetailsList.size() > 0) {
                ExcelformTable();

            }
            else {

                Toast.makeText(mContext, "No Data Found", Toast.LENGTH_SHORT).show();

            }
        }


    }




    private void ExcelformTable() {
        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        // mycomment
        String corStartDate = convertDateFormat(fromDate, originalFormat, desiredFormat);
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

        RichTextString richText = new HSSFRichTextString("FPS\nCode");
        richText.applyFont(boldFont);
        cellA.setCellValue(richText);
        firstSheet.setColumnWidth(0, 4000);

        HSSFCell cellB = rowA.createCell(1);
        RichTextString richText1 = new HSSFRichTextString("TicketNo");
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
        RichTextString richText21 = new HSSFRichTextString("Distr. Date");
        richText21.applyFont(boldFont);
        cellD.setCellValue(richText21);









        HSSFCell cellD2 = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 8000);
        RichTextString richText3 = new HSSFRichTextString("DealerName");
        richText3.applyFont(boldFont);
        cellD2.setCellValue(richText3);





        HSSFCell cellE = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 4000);
        RichTextString richText4 = new HSSFRichTextString("IsPhotoUploaded");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);








        HSSFCell cellF = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 4000);
        RichTextString richText5 = new HSSFRichTextString("IsFormUploaded");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);



        Log.d("mylist", " -------------- " + posDistributionDetailsList);
        if (posDistributionDetailsList != null && posDistributionDetailsList.size() > 0) {
            for (int i = 0; i < posDistributionDetailsList.size(); i++) {
                PosDistributionDetail detailsInfo = posDistributionDetailsList.get(i);
                String fps = String.valueOf(detailsInfo.getFpscode());
                String ticketNo = String.valueOf(detailsInfo.getTicketNo());
                String districtName = String.valueOf(detailsInfo.getDistrictName());
                String transDistrict = String.valueOf(detailsInfo.getTranDateStr());
                String dealerName = String.valueOf(detailsInfo.getDealerName());

                String isPhotoUploaded = String.valueOf(detailsInfo.getIsPhotoUploaded());


                String IsFormUploaded = String.valueOf(detailsInfo.getIsFormUploaded());

                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(fps);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(ticketNo);
                dataRow.createCell(2).setCellValue(districtName);
                dataRow.createCell(3).setCellValue(transDistrict);
                dataRow.createCell(4).setCellValue(dealerName);
                dataRow.createCell(5).setCellValue(isPhotoUploaded);
                dataRow.createCell(6).setCellValue(IsFormUploaded);

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
            Toast.makeText(PosDistributionReportActivity.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
        }



        try {
            Uri fileUri = FileProvider.getUriForFile(
                    PosDistributionReportActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", // Replace with your app's provider authority
                    filePathh
            );

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel");
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(PosDistributionReportActivity.this, "No app found to open Excel files.", Toast.LENGTH_SHORT).show();
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











}