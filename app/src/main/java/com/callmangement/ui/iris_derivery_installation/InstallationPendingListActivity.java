package com.callmangement.ui.iris_derivery_installation;

import static java.lang.String.valueOf;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityInstallationPendingListBinding;
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrictList_w;
import com.callmangement.ui.ins_weighing_scale.model.district.ModelDistrict_w;
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp;
import com.callmangement.ui.iris_derivery_installation.adapter.IrisInstalledPendingAdapter;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;


import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstallationPendingListActivity extends CustomActivity {
    String multipleFilePath = "BookList.xls";

    private ActivityInstallationPendingListBinding binding;
    private Context mContext;
    private Activity mActivity;
    private PrefManager prefManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Spinner spinnerDistrict;
    private final List<String> spinnerList = new ArrayList<>();
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private String districtId = "0";
    private final String districtIdd = "0";
    private final String myFormat = "yyyy-MM-dd";
    private String fromDate = "";
    private String toDate = "";
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private List<ModelDistrictList_w> district_List = new ArrayList<>();
    private String dis;
    private int checkFilter = 0;
    private String fpscodee, serialno;
    private String value_selectedDay, value_selectedDis;
    private static ArrayList<IrisInstallationPendingListResp.Datum> irisInsDataArrayList = new ArrayList<>();
    private final ArrayList<IrisInstallationPendingListResp.Datum> irisInsDataArrayListBlock = new ArrayList<>();
    private IrisInstalledPendingAdapter irisInstalledAdapter;
    private final List<String> block_List = new ArrayList<>();
    private IrisInstallationPendingListResp installedRoot = new IrisInstallationPendingListResp();
    private String To_USER_Id = "0";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstallationPendingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang menampilkan data");


        init();
    }

    private void createXlsx(ArrayList<IrisInstallationPendingListResp.Datum> irisInsDataArrayList) {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    showProgress();
                }
            });
            String strDate = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss", Locale.getDefault()).format(new Date());
//            File root = new File(Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ePDSDocuments");
//            if (!root.exists())
//                root.mkdirs();
//            File path = new File(root, "/" + districtNameEng + strDate + ".xlsx");

            File path = new File("/storage/emulated/0/Download/" + districtNameEng + strDate + ".xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream outputStream = new FileOutputStream(path);

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.MEDIUM);
            headerStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerStyle.setBorderRight(BorderStyle.MEDIUM);
            headerStyle.setBorderLeft(BorderStyle.MEDIUM);

            XSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            XSSFSheet sheet = workbook.createSheet("Pending Deliveries");
            XSSFRow row = sheet.createRow(0);

            XSSFCell cell = row.createCell(0);
            cell.setCellValue("Name");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(1);
            cell.setCellValue("FPS Code");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(2);
            cell.setCellValue("Block");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(3);
            cell.setCellValue("District");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(4);
            cell.setCellValue("Mobile Number");
            cell.setCellStyle(headerStyle);

            for (int i = 0; i < irisInsDataArrayList.size(); i++) {
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(irisInsDataArrayList.get(i).getDealerName());
                sheet.setColumnWidth(0, (irisInsDataArrayList.get(i).getDealerName().length() + 30) * 256);

                cell = row.createCell(1);
                cell.setCellValue(irisInsDataArrayList.get(i).getFpscode());
                sheet.setColumnWidth(1, (irisInsDataArrayList.get(i).getFpscode().length() + 10) * 256);

                cell = row.createCell(2);
                cell.setCellValue(irisInsDataArrayList.get(i).getBlockName());
                sheet.setColumnWidth(2, (irisInsDataArrayList.get(i).getBlockName().length() + 10) * 256);

                cell = row.createCell(3);
                cell.setCellValue(irisInsDataArrayList.get(i).getDistrictName());
                sheet.setColumnWidth(3, (irisInsDataArrayList.get(i).getDistrictName().length() + 10) * 256);

                cell = row.createCell(4);
                cell.setCellValue(irisInsDataArrayList.get(i).getDealerMobileNo());
                sheet.setColumnWidth(4, (irisInsDataArrayList.get(i).getDealerMobileNo().length() + 10) * 256);

            }

            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(InstallationPendingListActivity.this, "Data exported successfully!", Toast.LENGTH_SHORT).show();
                }
            });
//            Toast.makeText(InstallationPendingListActivity.this, "Data exported successfully!", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse(path.getPath());
            hideProgress();
           /* Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            try {
                startActivity(Intent.createChooser(intent, "Choose an app to open the XLSX file"));
            } catch (ActivityNotFoundException e) {
                //handle no apps found eg inform the user
                e.printStackTrace();
            }*/

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Filter for Excel files

            try {
                startActivityForResult(intent, 1); // Use startActivityForResult to get the selected file's URI
            } catch (ActivityNotFoundException e) {
                // Handle the case where no app capable of handling this intent is installed
            }
        } catch (IOException e) {
            hideProgress();
            e.printStackTrace();
        }
    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(InstallationPendingListActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(InstallationPendingListActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(InstallationPendingListActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


    private void init() {

        mContext = this;
        mActivity = this;
        prefManager = new PrefManager(mContext);
        swipeRefreshLayout = findViewById(R.id.refresh_list);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.buttonPDF.setImageDrawable(getDrawable(R.drawable.excel));
        mRecyclerView = findViewById(R.id.rv_delivered);
        //     binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.installed_list));
        binding.actionBar.textToolbarTitle.setText("Iris Installation Pending List");
        setUpData();
        setClickListener();
        //  setClickListener();
    }

    private void setUpData() {
        if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE()
                .equalsIgnoreCase("Admin")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();
        } else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE()
                .equalsIgnoreCase("Manager")) {
            binding.rlDistrict.setVisibility(View.VISIBLE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.GONE);
            SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove("districtId_key");
            editor.apply();
        } else if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE()
                .equalsIgnoreCase("ServiceEngineer")) {

            To_USER_Id = prefManager.getUSER_Id();

            binding.rlDistrict.setVisibility(View.GONE);
            binding.spacer.setVisibility(View.VISIBLE);
            binding.seDistrict.setVisibility(View.VISIBLE);
            districtId = getIntent().getStringExtra("districtId");
            districtNameEng = getIntent().getStringExtra("key_districtName");
            if (districtIdd != null && !districtIdd.isEmpty()) {
                try {
                    SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("districtId_key", Integer.parseInt(districtId));
                    editor.commit();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

        String USER_NAME = prefManager.getUSER_NAME();
        String USER_EMAIL = prefManager.getUSER_EMAIL();
        String USER_Mobile = prefManager.getUSER_Mobile();
        String USER_DISTRICT = prefManager.getUSER_District();
        Log.d("USER_NAME", " " + USER_NAME);
        //  getIrisInstallationPendingList();
        //  districtId = getIntent().getStringExtra("districtId");
        binding.seDistrict.setText(USER_DISTRICT);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        districtList();
        districtNameEng = "--" + getResources().getString(R.string.district) + "--";
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String todayDate = sdf.format(today);
        fromDate = todayDate;
        toDate = todayDate;
        //   getIrisInstallationPendingList(districtId,fromDate,toDate,fpscodee);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("clickkked", "refresh");
                getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno);
            }
        });

        Intent intent = getIntent();
        // Get the value from the intent using the key
        if (intent != null) {
            String value_districtId = intent.getStringExtra("key_districtId");
            String value_fromDate = intent.getStringExtra("key_fromDate");
            String value_toDate = intent.getStringExtra("key_toDate");
            value_selectedDay = intent.getStringExtra("key_selectedDate");
            value_selectedDis = intent.getStringExtra("key_selectedDis");
            Log.d("value_selectedDay", value_selectedDay);

            //   binding.spinner.setSelection(2);
            districtId = value_districtId;
            fromDate = value_fromDate;
            toDate = value_toDate;
            Log.d("clickkked", "intent " + districtId);

            if (districtId != null && !districtId.equals("0")) {
                getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno);
            }
        } else {
            //  binding.spinner.setSelection(1);
            getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno);
        }
        // Log.d("useriduserid",""+prefManager.getUSER_TYPE_ID());
        // binding.textDestrict.setText(USER_DISTRICT);
        // getInstallationCntApi();
        setUpDateRangeSpinner();

    }

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(IrisInstalledListActivity.this, WeighingScaleDashboard.class);
                //startActivity(i);
                onBackPressed();
            }
        });

        binding.actionBar.buttonPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (irisInsDataArrayList != null && irisInsDataArrayList.size() > 0) {
                    //           showProgress();

                    Log.d("arrrr_size", "" + irisInsDataArrayList.size());
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            //TODO your background code
                            createXlsx(irisInsDataArrayList);
                        }
                    });
                }

//                if (irisInsDataArrayList != null && irisInsDataArrayList.size() > 0) {
//                    Constants.irisInsPendingArrayList = irisInsDataArrayList;
//                    startActivity(new Intent(mContext, IrisInstallationPendingPdfActivity.class));
//                    finish();
//                }
            }
        });


        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkDistrict > 1) {
                    districtNameEng = district_List.get(i).getDistrictNameEng();
                    dis = district_List.get(i).getDistrictNameEng();
                    Log.d("dfgfd", " " + dis);
                    districtId = district_List.get(i).getDistrictId();
                    Log.d("fggfgh", " " + districtId);
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                    editor.putString("dis", dis);
                    editor.putString("districtId", districtId);
                    editor.apply();
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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
                            //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            fromDate = yesterdayDate;
                            toDate = yesterdayDate;
                            //   getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);

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
                            //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
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
                            //       getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        fromDate = "";
                        toDate = "";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        //     getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        binding.textFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateFromDate = (view1, year, monthOfYear, dayOfMonth) -> {
                    myCalendarFromDate.set(Calendar.YEAR, year);
                    myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                    myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabelFromDate();
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate,
                        myCalendarFromDate.get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                        myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                long minDateInMillis = myCalendarToDate.getTimeInMillis();
                datePickerDialog.getDatePicker().setMaxDate(minDateInMillis);
                datePickerDialog.show();
            }
        });

        binding.textToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener dateToDate = (view1, year, monthOfYear, dayOfMonth) -> {
                    myCalendarToDate.set(Calendar.YEAR, year);
                    myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                    myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabelToDate();
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate,
                        myCalendarToDate.get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                        myCalendarToDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                long minDateInMillis = myCalendarFromDate.getTimeInMillis();
                datePickerDialog.getDatePicker().setMinDate(minDateInMillis);
                datePickerDialog.show();
            }
        });

        binding.btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fpscodee = binding.inputfps.getText().toString();
                serialno = binding.inputserialno.getText().toString();
                Log.d("clickkked", "search");
                getIrisInstallationPendingList(districtId, fromDate, toDate, fpscodee, serialno);
            }
        });
    }

    public static List<Map<String, List<?>>> getListOfObject() {
        List<Map<String, List<?>>> map = new ArrayList<Map<String, List<?>>>();
        Map<String, List<?>> map1 = new HashMap<>();
        map1.put("Report", irisInsDataArrayList);
        map.add(map1);

        return map;
    }

    private void getIrisInstallationPendingList(String districtId, String fromDate, String toDate, String fpscodee,
                                                String serialno) {

        if (Constants.isNetworkAvailable(mContext)) {
            hideKeyboard(mActivity);
            showProgress();
            String USER_Id = prefManager.getUSER_Id();
            Log.d("USER_ID", USER_Id);

            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            //  Call<WeightInsRoot> call = apiInterface.apiIrisWeighInstallation(USER_Id,"",districtId,"","0",fromDate,toDate);
            Call<IrisInstallationPendingListResp> call = apiInterface.getCombinedIRISWehingDetailReport(USER_Id, districtId,
                    "0", "0", fromDate, toDate, To_USER_Id);

            Log.d("response","----"+USER_Id+districtId+fromDate+To_USER_Id);
            call.enqueue(new Callback<IrisInstallationPendingListResp>() {
                @Override
                public void onResponse(@NonNull Call<IrisInstallationPendingListResp> call,
                                       @NonNull Response<IrisInstallationPendingListResp> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                binding.txtNoRecord.setVisibility(View.GONE);
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    installedRoot = response.body();
                                    String message = installedRoot.getMessage();
                                    //  Toast.makeText(InstallationPendingList.this, message, Toast.LENGTH_SHORT).show();
                                    irisInsDataArrayList.clear();
                                    irisInsDataArrayList = installedRoot.getData();

                                    Log.d("iris_array", "" + irisInsDataArrayList.size());
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                    //            mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    //        binding.rlBlock.setVisibility(View.VISIBLE);
                                    irisInstalledAdapter = new IrisInstalledPendingAdapter(irisInsDataArrayList, mContext);
                                    mRecyclerView.setAdapter(irisInstalledAdapter);
                                    // Use 'dataValue' or perform operations with other properties


                                } else {
                                    binding.txtNoRecord.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                    binding.rlBlock.setVisibility(View.GONE);
                                }
                            } else {
                                binding.txtNoRecord.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                                binding.rlBlock.setVisibility(View.GONE);
                                makeToast(String.valueOf(response.body().getMessage()));
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IrisInstallationPendingListResp> call, @NonNull Throwable error) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
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
        //    getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);
    }

    private void districtList() {
        if (Constants.isNetworkAvailable(mActivity)) {
            hideKeyboard(mActivity);
            hideProgress();
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            String USER_Id = prefManager.getUSER_Id();
            APIService apiInterface = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDistrict_w> call = apiInterface.apiGetDistictList_w();
            call.enqueue(new Callback<ModelDistrict_w>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict_w> call, @NonNull Response<ModelDistrict_w> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    district_List = response.body().getDistrict_List();
                                    if (district_List != null && district_List.size() > 0) {
                                        Collections.reverse(district_List);
                                        ModelDistrictList_w l = new ModelDistrictList_w();
                                        l.setDistrictId(valueOf(-1));
                                        l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                                        district_List.add(l);
                                        Collections.reverse(district_List);
                                        ArrayAdapter<ModelDistrictList_w> dataAdapter = new ArrayAdapter<>(mActivity,
                                                android.R.layout.simple_spinner_item, district_List);
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerDistrict.setAdapter(dataAdapter);
                                        binding.spinnerDistrict.setSelection(Integer.parseInt(value_selectedDis));

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
                                    makeToast(response.body().getMessage());
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } else {
                            makeToast(getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelDistrict_w> call, @NonNull Throwable error) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setUpDateRangeSpinner() {
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        spinnerList.add(getResources().getString(R.string.today));
        spinnerList.add(getResources().getString(R.string.yesterday));
        spinnerList.add(getResources().getString(R.string.current_month));
        spinnerList.add(getResources().getString(R.string.previous_month));
        spinnerList.add(getResources().getString(R.string.custom_filter));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);
        binding.spinner.setSelection(Integer.parseInt(value_selectedDay));
        String selectedString = (String) binding.spinner.getSelectedItem();
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //    Intent i = new Intent(IrisInstalledListActivity.this, WeighingScaleDashboard.class);
        //    startActivity(i);
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }


}