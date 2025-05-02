package com.callmangement.EHR.ehrActivities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.lang.String.valueOf;
import com.callmangement.BuildConfig;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callmangement.EHR.adapter.AttendanceListingAdapter;
import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.models.AttStatusDatum;
import com.callmangement.EHR.models.AttStatusRoot;
import com.callmangement.EHR.models.AttendanceDetailsInfo;
import com.callmangement.EHR.models.DashboardUserDetails;
import com.callmangement.EHR.models.DashbordRoot;
import com.callmangement.EHR.models.ModelAttendanceListing;
import com.callmangement.EHR.models.ModelDistrict;
import com.callmangement.EHR.models.ModelDistrictList;
import com.callmangement.EHR.models.ModelSEUser;
import com.callmangement.EHR.models.ModelSEUserList;
import com.callmangement.EHR.support.EqualSpacingItemDecoration;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Utils;
import com.callmangement.R;
import com.callmangement.databinding.ActivityAttendanceListingBinding;
import com.callmangement.utils.PrefManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AttendanceListingActivity extends BaseActivity {
    String targetPdf = "Camp Daily Work Report.pdf";
    private Activity mActivity;
    private Context mContext;
    private ActivityAttendanceListingBinding binding;
    PrefManager preference;
    private String dis;
    private List<AttendanceDetailsInfo> attendanceDetailsInfoList = new ArrayList<>();
    private AttendanceListingAdapter adapter;
    public final int LAUNCH_MARK_ATTENDANCE_ACTIVITY = 333;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private Spinner spinnerDistrict;
    private Spinner spinner, se_date_spinner;
    private Spinner spinnerSEUsers;
    private EditText inputStartDate;
    private EditText inputEndDate;
    private String startDate = "";
    private String endDate = "";
    private String SDate = "";
    private String EDate = "";
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private int checkSEUser = 0;
    private String SEUserNameEng = "";
    private String SEUserId = "0";
    private String s1, d1, d2, selecteduserid;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private List<ModelSEUserList> SEUser_list = new ArrayList<>();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private int checkFilter = 0;
    LinearLayoutCompat layoutDateRange;
    private final String myFormat = "yyyy-MM-dd";
    String USER_Id;
    int totelPrestAtt;
    String outQty, otQty;
    private Vibrator vibrator;
    long daysDifference;
    String todayDate;
    SharedPreferences sharedPreferences;
    String originalFileName = "Demo.xlsx"; // Original file name
    String uniqueFileName = generateUniqueFileName(originalFileName);
    //  File filePathh = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), uniqueFileName);
    private File filePathh = new File("/storage/emulated/0/Download/" + uniqueFileName);
    //  private File filePathh = new File("/storage/emulated/0/Download" + "/Demo.xlsx");
//  ArrayList<AttStatusDatum>  attStatusData;
    private List<AttStatusDatum> attStatusData = new ArrayList<>();

    private String Month = "";
    private String Year = "";
    String curMonth = "";
    SwipeRefreshLayout swipeRefreshLayout;

    int date_selected_year = 0;
    int date_selected_month = 0;
    int date_selected_day = 0;

    int currentPage =1;
    int  pageSize =200;
    String pagination = String.valueOf(1);

    private boolean isLoading = false;
    private boolean isLastPage = false;
    int totalPages; // Adjust accordingly based on response
    private boolean isExcelRequest = false;
    private boolean isPdfRequest = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityAttendanceListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mActivity = this;
        mContext = this;

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        init();

    }

    private void init() {
        clearSharePreference();
        preference = new PrefManager(mActivity);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.manage_attendance));
        date_selected_year = getIntent().getIntExtra("YEAR",0);
        date_selected_month = getIntent().getIntExtra("MONTH",0);
        date_selected_day =getIntent().getIntExtra("DATE",0);

        setNormalPicker();
        //   filterDialog();

        SEdatesppiner();

        getSpinnerStatus();

        attendanceDetailsInfoList = new ArrayList<>();
        attStatusData = new ArrayList<>();

      /*  adapter = new AttendanceListingAdapter(mActivity, attendanceDetailsInfoList, attStatusData, onItemViewClickListener, preference.getUSER_TYPE_ID());
        binding.rvAllAttendance.setHasFixedSize(true);
        binding.rvAllAttendance.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        binding.rvAllAttendance.addItemDecoration(new EqualSpacingItemDecoration(0, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAllAttendance.setAdapter(adapter);*/
        // adapter.refreshItem(attendanceDetailsInfoList);

        setClickListener();
        binding.linCreateItem.setVisibility(View.GONE);
        binding.actionBar.buttonFilter.setVisibility(View.GONE);
        binding.buttonprint.setVisibility(View.GONE);


        binding.buttonprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);


                //  Pdfform();
                clearSharePreference();
                pagination = String.valueOf(0);
                currentPage =0;
                pageSize=0;
                isPdfRequest = true;

                //  getAttendanceList(USER_Id, SDate, EDate);
                getAttendanceList(USER_Id, startDate, endDate);

                // Delay execution to ensure data is loaded
             /*   new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Pdfform();
                    }
                }, 8000);

*/
            }
        });
        binding.buttonexcel.setVisibility(View.GONE);
        binding.buttonexcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(100);
                clearSharePreference();
                pagination = String.valueOf(0);
                currentPage =0;
                pageSize=0;
                // getAttendanceList(USER_Id, SDate, EDate);
                isExcelRequest=true;

                sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                // creating a variable for gson.
                Gson gson = new Gson();

                // below line is to get to string present from our
                // shared prefs if not present setting it as null.
                String fetchUserid = sharedPreferences.getString("fetchUserid", null);
                String fetchSDate = sharedPreferences.getString("fetchSDate", null);
                String fetchEDate = sharedPreferences.getString("fetchEDate", null);


                getAttendanceList(fetchUserid, fetchSDate, fetchEDate);
                Log.d("StartDate","M---------"+SDate+"  "+EDate);


                Log.d("startdate", "M---------"+startDate+"  "+endDate);

               /* // Delay execution to ensure data is loaded
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ExcelformTable();
                    }
                }, 8000);
*/













            }
        });
       /* binding.totelAttandance.setVisibility(View.GONE);
        binding.inAttandance.setVisibility(View.GONE);
        binding.outAttandance.setVisibility(View.GONE);*/
        //   Log.d("jgggkgjll", "hkjkbb" + attendanceDetailsInfoList);
        String USER_TYPE = preference.getUSER_TYPE();
        String USER_TYPE_ID = preference.getUSER_TYPE_ID();
        if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
            binding.linCreateItem.setVisibility(View.GONE);
            binding.buttonprint.setVisibility(View.VISIBLE);
            binding.buttonexcel.setVisibility(View.VISIBLE);
            binding.actionBar.buttonFilter.setVisibility(View.GONE);
            binding.llDisUser.setVisibility(View.VISIBLE);
            binding.llAttendanceCount.setVisibility(View.GONE);
            binding.filter.setVisibility(View.VISIBLE);
            binding.SEsearch.setVisibility(View.GONE);

            //  binding.llAttendanceCount.setVisibility(View.VISIBLE);

        } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager") || USER_TYPE_ID.equals("16") && USER_TYPE.equalsIgnoreCase("HRManager")) {
            binding.linCreateItem.setVisibility(View.GONE);
            binding.buttonprint.setVisibility(View.VISIBLE);
            binding.buttonexcel.setVisibility(View.VISIBLE);
            binding.actionBar.buttonFilter.setVisibility(View.GONE);
            binding.llDisUser.setVisibility(View.VISIBLE);
            binding.llAttendanceCount.setVisibility(View.GONE);
            binding.filter.setVisibility(View.VISIBLE);
            binding.SEsearch.setVisibility(View.GONE);
            // binding.llAttendanceCount.setVisibility(View.VISIBLE);

        } else if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")
                || USER_TYPE_ID.equals("3") && USER_TYPE.equalsIgnoreCase("Support")) {
            binding.linCreateItem.setVisibility(View.GONE);
            binding.actionBar.buttonFilter.setVisibility(View.GONE);
            binding.rlSedateSpinner.setVisibility(View.VISIBLE);
            binding.llDisUser.setVisibility(View.GONE);
            binding.llAttendanceCount.setVisibility(View.VISIBLE);
            binding.filter.setVisibility(View.GONE);
            binding.SEsearch.setVisibility(View.VISIBLE);
            binding.buttonprint.setVisibility(View.VISIBLE);
            binding.buttonexcel.setVisibility(View.VISIBLE);

        }
        USER_Id = preference.getUSER_Id();
        //   Log.d("userid", "" + USER_Id);
        if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
          /* Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH)+1);
            Month = curMonth;
            Year = year;
            SEUserId = USER_Id;

            dashboardApi(  USER_Id,  Month ,  Year,SEUserId);*/
            //  SDate =Year+"-"+Month+"-"+"1";

            SEUserId = USER_Id;

            Integer currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String CurrentDay = sdf.format(today);
            //   Log.d("CurrentDay", " " + CurrentDay);
            SDate = CurrentDay;

            EDate = CurrentDay;
            currentPage = 1;

            getAttendanceList(USER_Id, SDate, EDate);


            filterDialog();
        } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager") || USER_TYPE_ID.equals("16") && USER_TYPE.equalsIgnoreCase("HRManager")) {

            filterDialog();



/*
            Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH)+1);
            Month = curMonth;
            Year = year;
            SEUserId = USER_Id;

            dashboardApi(  USER_Id,  Month ,  Year,SEUserId);



            SDate =Year+"-"+Month+"-"+"1";

            Integer currentMonth =   Calendar.getInstance().get(Calendar.MONTH)+1;
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String   CurrentDay = sdf.format(today);
            Log.d("CurrentDay"," "+CurrentDay);
            EDate =CurrentDay;

            getAttendanceList(USER_Id, SDate, EDate);*/


            //  SDate =Year+"-"+Month+"-"+"1";

            SEUserId = USER_Id;

            Integer currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String CurrentDay = sdf.format(today);
            //   Log.d("CurrentDay", " " + CurrentDay);
            SDate = CurrentDay;
            EDate = CurrentDay;
            currentPage = 1;
            pagination = String.valueOf(1);


            getAttendanceList(SEUserId, SDate, EDate);

        } else if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")
                || USER_TYPE_ID.equals("3") && USER_TYPE.equalsIgnoreCase("Support")) {


            Calendar c = Calendar.getInstance();
            //    String year = String.valueOf(c.get(Calendar.YEAR));
            //    curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            curMonth = String.valueOf(date_selected_month+1);
            Month = curMonth;
            //    Year = year;
            Year = String.valueOf(date_selected_year);
            SEUserId = USER_Id;

            dashboardApi(USER_Id, Month, Year, SEUserId);


            SDate = Year + "-" + Month + "-" + "1";

            Integer currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String CurrentDay = sdf.format(today);
            //  Log.d("CurrentDay", " " + CurrentDay);
            EDate = CurrentDay;

            if (Month.equals(String.valueOf(currentMonth))) {

                EDate = CurrentDay;
                //    Log.d("jkkhjk", "" + EDate);

            } else {

// Example month and year
                int month = Integer.parseInt(Month); // June (months are 0-based, so 5 represents June)
                int year = Integer.parseInt(Year);

                // Get the last day of the month
                int lastDay = getLastDayOfMonth(year, month);
                EDate = Year + "-" + Month + "-" + lastDay;
                //  Log.d("itiiuiy", "" + EDate);
                //  Log.d("MyMonth", "" + Month);
                //  Log.d("lastDay", "" + lastDay);

            }
            currentPage = 1;
            pagination = String.valueOf(1);


            //starting#
           Log.d("checkDate", ""+SDate+" "+EDate);
              startDate = SDate;
              endDate = EDate;

            getAttendanceList(USER_Id, SDate, EDate);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            int monthnum = date_selected_month;
            cal.set(Calendar.MONTH, monthnum);
            String month_name = month_date.format(cal.getTime());

            // Log.e("", "" + month_name);

            String ed_monthYear = "--" + month_name + "  " + date_selected_year + "--";
            binding.dateSelt.setText(ed_monthYear);

        }

        Log.d("attStatusData..", "recyyyyyyy" + attStatusData);
        Log.d("attStatusData..", "rec" + attendanceDetailsInfoList);
        adapter = new AttendanceListingAdapter(mActivity, attendanceDetailsInfoList, attStatusData, onItemViewClickListener, preference.getUSER_TYPE_ID());

        binding.rvAllAttendance.setHasFixedSize(true);
        binding.rvAllAttendance.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        binding.rvAllAttendance.addItemDecoration(new EqualSpacingItemDecoration(0, EqualSpacingItemDecoration.VERTICAL));
        binding.rvAllAttendance.setAdapter(adapter);

        binding.rvAllAttendance.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Log the values to verify scroll position and total item count
                    Log.d("ScrollListener", "VisibleItemCount: " + visibleItemCount);
                    Log.d("ScrollListener", "TotalItemCount: " + totalItemCount);
                    Log.d("ScrollListener", "FirstVisibleItemPosition: " + firstVisibleItemPosition);


                    Log.d("loggggggg", " AA    "+currentPage);
                    Log.d("loggggggg", " VV    "+!isLoading +" ----"+ !isLastPage );
                    Log.d("check--", " VV    "+!isLoading +" ----"+ !isLastPage );
                    // Check if more data needs to be loaded
                    if (!isLoading && !isLastPage) {

                        Log.d("loggggggg", "BB     "+currentPage);
                        // Load more data when 5 items are left to reach the end
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                            Log.d("ScrollListener", "End of list reached. Loading more data...");
                            isLoading = true;  // Set loading flag to true
                            Log.d("loggggggg", "CC     "+currentPage);
                            currentPage++; // Increment currentPage
                            Log.d("ScrollListener", "Loading page: " + currentPage);
                            // getAttendanceList(USER_Id, SDate, EDate);
                            Log.d("sDateSDate", "" + SDate);
                            Log.d("sDateSDate--", "" + startDate);


                            getAttendanceList(SEUserId, startDate, endDate);

                            // Fetch more data
                        }
                    }
                }
            }
        });




        //   getSpinnerStatus();













    }


    public void clearSharePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
// Clearing the value associated with the "camp" key
        editor.remove("courses");
// Applying the changes to save the updated SharedPreferences
        editor.apply();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context mContext, Intent intent) {
            // Get extra data included in the Intent
            outQty = intent.getStringExtra("quantity");
            otQty = outQty;

            //   Log.d("otqty", "" + otQty);
            //    binding.totelAttandance.setText("Total Attn.\n "+totelPrestAtt+"/"+daysDifference);

            //   binding.inAttandance.setText("In Attn.\n"+totelPrestAtt+"/"+totelPrestAtt);

            //     binding.outAttandance.setText("Out Attn.\n"+ otQty+"/"+totelPrestAtt);


        }
    };

    public void attendencecount() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        // Parse the date strings into Date objects
        try {
            Date startDatee = sdf.parse(startDate);
            Date endDatee = sdf.parse(endDate);
            // Get the time difference in milliseconds
            long timeDifferenceMillis = endDatee.getTime() - startDatee.getTime();
            // Convert milliseconds to days
            daysDifference = TimeUnit.DAYS.convert(timeDifferenceMillis, TimeUnit.MILLISECONDS);
            daysDifference += 1;
            //Log.d("time_diff", "" + daysDifference);
            //  Log.d("sjdbsdFB", "" + totelPrestAtt + daysDifference + otQty);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    AttendanceListingAdapter.OnItemViewClickListener onItemViewClickListener = new AttendanceListingAdapter.OnItemViewClickListener() {
        @Override
        public void onItemClick(AttendanceDetailsInfo attendanceDetailsInfo, int position) {

        }
    };


    private void getAttendanceList(final String USER_Id, final String SDate, final String EDate) {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            binding.progressBar.setVisibility(View.VISIBLE);

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String paramStr = GetAttendanceListQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], USER_Id);
            paramData.put(splitArray[1], districtId);





            paramData.put(splitArray[2], SDate);
            paramData.put(splitArray[3], EDate);
          //  paramData.put(splitArray[4], pagination);
            if (splitArray.length > 4) {
                paramData.put(splitArray[4], pagination);
            } else {
                Log.e("ArrayIndexError", "splitArray has only " + splitArray.length + " elements. paramStr: " + paramStr);
            }
            if (splitArray.length >5) {
                paramData.put(splitArray[5], String.valueOf(currentPage));
            } else {
                Log.e("ArrayIndexError", "splitArray has only " + splitArray.length + " elements. paramStr: " + paramStr);
            }
            if (splitArray.length >6) {
                paramData.put(splitArray[6], String.valueOf(pageSize));
            } else {
                Log.e("ArrayIndexError", "splitArray has only " + splitArray.length + " elements. paramStr: " + paramStr);
            }

            Log.d("Debug", "paramStr: " + paramStr);
            Log.d("Debug", "splitArray length: " + splitArray.length);
            Log.d("Debug", "splitArray contents: " + Arrays.toString(splitArray));



            Call<ModelAttendanceListing> call = apiInterface.callAttendanceListApi(GetAttendanceList(), paramData);
            Log.d("-response-", "  " + GetAttendanceList()+ paramData);
            Log.d("Attendance_Pra", " ----" + USER_Id + "   " + districtId + "   " + SDate + "   " + EDate+ "   " + pagination + "   " + String.valueOf(currentPage) + "   " + String.valueOf(pageSize));

            isLoading = true;
            call.enqueue(new Callback<ModelAttendanceListing>() {


                @Override
                public void onResponse(@NonNull Call<ModelAttendanceListing> call, @NonNull Response<ModelAttendanceListing> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    isLoading = false;

                    if (response.isSuccessful() && response.body() != null) {
                        if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                            List<AttendanceDetailsInfo> newAttendanceList = response.body().getData();

                            totalPages = response.body().getTotalPages();
                            Log.d("totalPages", ""+totalPages);
                            //  currentPage = response.body().getCurrentPage(); // Use the currentPage from the response

                            if (newAttendanceList != null && !newAttendanceList.isEmpty()) {
                                binding.textNoCampSchedule.setVisibility(View.GONE);
                                binding.rvAllAttendance.setVisibility(View.VISIBLE);
                                Log.d("USER_Id", "     "+USER_Id);




                                if (currentPage == 1) {
                                    // First Page: Set New Data
                                    adapter.setData(newAttendanceList, attStatusData);
                                    Log.d("loggggggg", "     "+currentPage);
                                } else {
                                    // Next Page: Append Data
                                    Log.d("loggggggg", "     "+currentPage);
                                    adapter.addData(newAttendanceList, attStatusData);
                                }

                                // Check if it's the last page
                                if (currentPage == totalPages) {
                                    isLastPage = true;
                                }
                            } else {
                                if (currentPage == 1) {
                                    binding.textNoCampSchedule.setVisibility(View.VISIBLE);
                                    binding.rvAllAttendance.setVisibility(View.GONE);
                                }
                                isLastPage = true; // No more pages to load
                            }



                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);


                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            Gson gson = new Gson();

                            String json = gson.toJson(response.body().getData());


                            editor.putString("courses", json);
                            editor.putString("fetchUserid", USER_Id);
                            editor.putString("fetchSDate", SDate);
                            editor.putString("fetchEDate", EDate);


                            editor.apply();


                            if (isExcelRequest){
                                ExcelformTable();
                                isExcelRequest = false; // Reset flag after generating Excel

                            }
                            else{

                            }



                            if (isPdfRequest){
                                Pdfform();

                                isPdfRequest = false; // Reset flag after generating Excel

                            }
                            else{

                            }




                        } else {
                            makeToast(response.body().getMessage());
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelAttendanceListing> call, @NonNull Throwable error) {
                    binding.progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    makeToast(getResources().getString(R.string.error));
                    Log.e("API Error", error.getMessage(), error);
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }





    private void getSpinnerStatus() {
        if (Utils.isNetworkAvailable(mActivity)) {
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            Call<AttStatusRoot> call = apiInterface.GetAttStatus(USER_Id);
            call.enqueue(new Callback<AttStatusRoot>() {
                @Override
                public void onResponse(@NonNull Call<AttStatusRoot> call, @NonNull Response<AttStatusRoot> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    Log.d("USER_Id..", "USER_Id.." + USER_Id);

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    Utils.hideCustomProgressDialogCommonForAll();

                                    AttStatusRoot getErrorTypesRoot = response.body();
                                    Log.d("getErrorTypesRoot..", "getErrorTypesRoot.." + getErrorTypesRoot);

                                    attStatusData =
                                            getErrorTypesRoot.getData();

                                    Log.d("attStatusData..", "attStatusData.." + attStatusData);

                                    for (int i = 0; i < attStatusData.size(); i++) {
                                        //  playerNames.add(attStatusData.get(i).getAttendanceStatus().toString());
                                        String attendancestatus = attStatusData.get(i).getAttendanceStatus().toString();

                                        Log.d("attStatusData..", "attStatusData.." + attendancestatus);

                                    }

                                } else {
                                    makeToast(String.valueOf(response.body().getMessage()));
                                }

                            } else {
                                makeToast(mActivity.getResources().getString(R.string.error));
                            }
                        } else {
                            makeToast(mActivity.getResources().getString(R.string.error));
                        }
                    } else {
                        makeToast(mActivity.getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AttStatusRoot> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(mActivity.getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(mActivity.getResources().getString(R.string.no_internet_connection));

        }
    }


    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            //    Intent intent = new Intent(mActivity, MainActivity.class);
                                                            //    startActivity(intent);
                                                            finish();
                                                        }
                                                    }
        );


        binding.actionBar.buttonFilter.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Utils.hideCustomProgressDialogCommonForAll();
                SharedPreferences sh = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                d1 = sh.getString("dis", "");
                d2 = sh.getString("districtId", "");
                SharedPreferences sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE);
                selecteduserid = sh2.getString("UserId", "");
                Log.d("d2IDDD", "" + d2);
                Log.d("selecteduserid", "" + selecteduserid);



                filterDialog();
            }
        });
        binding.buttonMarkAttendance.setOnClickListener(view -> {
            permissionsToRequest = findUnAskedPermissions(permissions);
            //get the permissions we have asked for before but are not granted..
            //we will store this in a global list to access later.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    Log.d("checkcheck", "1");
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                } else {
                    Intent intent = new Intent(mActivity, MarkAttendanceActivity.class);
                    startActivityForResult(intent, LAUNCH_MARK_ATTENDANCE_ACTIVITY);
                    Log.d("checkcheck", "2");

                }
            } else {
                Intent intent = new Intent(mActivity, MarkAttendanceActivity.class);
                startActivityForResult(intent, LAUNCH_MARK_ATTENDANCE_ACTIVITY);
                Log.d("checkcheck", "3");
            }
        });


        binding.btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String fps = binding.inputSearch.getText().toString();
                //  fpscodee = binding.inputSearch.getText().toString();
                //. getPosError(expenseStatusId,districtId,fromDate,toDate,fpscodee);


                //   filterDialog();

                USER_Id = SEUserId;
                Log.d("USER_IdUSER_Id", "ioio" + USER_Id);

                Log.d("districtList", "" + districtNameEng + "  " + SEUserNameEng);

                //   Log.d("startDate_endDate",""+startDate + "  "+ endDate);
                Log.d("startDate_endDate", "" + Month + "  " + Year);

                dashboardApi(USER_Id, Month, Year, SEUserId);

                Utils.hideCustomProgressDialogCommonForAll();
                SharedPreferences sh = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                d1 = sh.getString("dis", "");
                d2 = sh.getString("districtId", "");
                SharedPreferences sh2 = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE);
                selecteduserid = sh2.getString("UserId", "");
                Log.d("d2IDDD", "" + d2);
                Log.d("selecteduserid", "" + selecteduserid);


                SDate = Year + "-" + Month + "-" + "1";

                Integer currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String CurrentDay = sdf.format(today);
                Log.d("CurrentDay", " " + CurrentDay);
                //  curMonth = String.valueOf(c.get(Calendar.MONTH)+1);


                Log.d("bmvbkhkuk", "" + USER_Id);
                Log.d("ytuyu", "" + Month);
                Log.d("jyjjh", "" + currentMonth);


                if (Month.equals(String.valueOf(currentMonth))) {

                    EDate = CurrentDay;
                    Log.d("jkkhjk", "" + EDate);

                } else {

// Example month and year
                    int month = Integer.parseInt(Month); // June (months are 0-based, so 5 represents June)
                    int year = Integer.parseInt(Year);

                    // Get the last day of the month
                    int lastDay = getLastDayOfMonth(year, month);
                    EDate = Year + "-" + Month + "-" + lastDay;
                    Log.d("itiiuiy", "" + EDate);
                    Log.d("MyMonth", "" + Month);
                    Log.d("lastDay", "" + lastDay);

                }

                Log.d("jfddkjv", "" + EDate);
                currentPage = 1;
                pageSize=10;
                isLoading = false;

                startDate = SDate;
                endDate = EDate;

                Log.d("startDatestartDate", "" + startDate);
                // Reset to first page
                isLastPage = false;











                getAttendanceList(USER_Id, SDate, EDate);

            }
        });


    }


    public static int getLastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        // Set the date to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Get the last day of the month
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        return lastDay;
    }


    private void filterDialog() {



        try {

            spinnerDistrict = findViewById(R.id.spinnerDistrict);
            spinnerSEUsers = findViewById(R.id.spinnerSEUsers);
            layoutDateRange = findViewById(R.id.layout_date_range);

            EditText inputTrainingNumber = findViewById(R.id.inputTrainingNumber);
            Button buttonTrainingFilter = findViewById(R.id.buttonTrainingFilter);
            Log.d("jgjgk", "" + d1);
            Log.d("kjkkgk", "" + d2);
            districtNameEng = "--" + getResources().getString(R.string.district) + "--";
            SEUserNameEng = "--" + getResources().getString(R.string.se_user) + "--";
            districtList();
            Collections.reverse(SEUser_list);
            ModelSEUserList l = new ModelSEUserList();
            l.setUserId(valueOf(-1));
            l.setUserName("--" + getResources().getString(R.string.se_user) + "--");
            SEUser_list.add(l);
            Collections.reverse(SEUser_list);
            ArrayAdapter<ModelSEUserList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, SEUser_list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSEUsers.setAdapter(dataAdapter);


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
                        if (districtNameEng.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--")) {
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
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            spinnerSEUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (++checkSEUser > 1) {
                        SEUserNameEng = SEUser_list.get(i).getUserName();
                        SEUserId = SEUser_list.get(i).getUserId();
                        if (SEUserId.equals("-1")) {

                        } else {

                            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsMyPrefs", MODE_PRIVATE).edit();
                            editor.putString("UserId", SEUserId);
                            editor.apply();
                            Log.d("ghgfh", "fghgh" + SEUserNameEng + "" + SEUserId);

                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            binding.buttonTrainingFilter.setOnClickListener(view -> {





                int UserIdd = Integer.parseInt(SEUserId);
                Log.d("userid", "" + UserIdd);




                //   districtId = district_List.get(i).getDistrictId();
                Log.d("districtList", "" + districtNameEng + "  " + SEUserNameEng);
                //   startDate = inputStartDate.getText().toString().trim();
                //  endDate = inputEndDate.getText().toString().trim();
                Log.d("startDate_endDate", "" + startDate + "  " + endDate);
                layoutDateRange.getVisibility();


                if (layoutDateRange.getVisibility() == View.VISIBLE && startDate.isEmpty()) {
                    Toast.makeText(mActivity, "Select Start date", Toast.LENGTH_SHORT).show();


                } else if (layoutDateRange.getVisibility() == View.VISIBLE && endDate.isEmpty()) {
                    Toast.makeText(mActivity, "Select End date", Toast.LENGTH_SHORT).show();


                } else {

                    isLoading = false;  // Start loading
                    currentPage = 1;
                    pagination = String.valueOf(1);
                    pageSize=10;
                    // Reset to first page
                    isLastPage = false;
                    getAttendanceList(SEUserId, startDate, endDate);

                }
                //   }
            });


















        /*    binding. btnsearch.setOnClickListener(view -> {

                int UserIdd = Integer.parseInt(SEUserId);
                Log.d("userid",""  +UserIdd);

                Log.d("districtList",""+districtNameEng + "  "+ SEUserNameEng);

                Log.d("startDate_endDate",""+startDate + "  "+ endDate);

                dashboardApi(  USER_Id,  Month ,  Year,SEUserId);




            });*/
            // dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void SEdatesppiner() {

        //SE Date Spinner


        List<String> SEspinnerList = new ArrayList<>();
        SEspinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        SEspinnerList.add(getResources().getString(R.string.today));
        SEspinnerList.add(getResources().getString(R.string.yesterday));
        SEspinnerList.add(getResources().getString(R.string.current_month));
        SEspinnerList.add(getResources().getString(R.string.previous_month));
        SEspinnerList.add(getResources().getString(R.string.custom_filter));
        ArrayAdapter<String> SEdataAdapterr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SEspinnerList);
        SEdataAdapterr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.seDateSpinner.setAdapter(SEdataAdapterr);
        binding.seDateSpinner.setSelection(1);

        //  String SEselectedString = (String)  binding. seDateSpinner.getSelectedItem();
        int SEselectedItemPosition = binding.seDateSpinner.getSelectedItemPosition();
        if (SEselectedItemPosition == 1) {
            binding.layoutDateRange.setVisibility(View.GONE);
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            todayDate = sdf.format(today);
            startDate = todayDate;
            endDate = todayDate;
        } else {

        }

        binding.seDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkFilter > 1) {
                    String item = adapterView.getItemAtPosition(i).toString();
                    if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                     /*   Objects.requireNonNull(inputStartDate.getText()).clear();
                        Objects.requireNonNull(inputEndDate.getText()).clear();*/
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        if (item.equalsIgnoreCase(getResources().getString(R.string.today))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            Date today = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            todayDate = sdf.format(today);
                            startDate = todayDate;
                            endDate = todayDate;
                            //  getAttendanceList(USER_Id,startDate,endDate);
                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            startDate = yesterdayDate;
                            endDate = yesterdayDate;
                            // getReportList(districtId,startDate,endDate);
                            //  getAttendanceList(USER_Id,startDate,endDate);
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
                            startDate = date1;
                            endDate = date2;
                            //   getAttendanceList(USER_Id,startDate,endDate);
                            //   getReportList(districtId,startDate,endDate);
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
                            startDate = date1;
                            endDate = date2;
                            //  getAttendanceList(USER_Id,startDate,endDate);
                            //  getReportList(districtId,startDate,endDate);
                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            startDate = "";
                            endDate = "";
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        startDate = "";
                        endDate = "";
                        //   Objects.requireNonNull(inputStartDate.getText()).clear();
                        //  Objects.requireNonNull(inputEndDate.getText()).clear();

                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        Log.d("yoyo", "yoyo");
                        // getAttendanceList(USER_Id,startDate,endDate);
                        // getReportList(districtId,startDate,endDate);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        /*from date*/
        DatePickerDialog.OnDateSetListener dateFromDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarFromDate.set(Calendar.YEAR, year);
            myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
            myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelFromDateSE();
        };
        Date date = new Date();  // to get the date
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // getting date in this format
        String formattedDate = df.format(date.getTime());
        binding.textFromDate.setText(formattedDate);
        binding.textFromDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, R.style.DialogThemeTwo, dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            long minDateInMillis = myCalendarToDate.getTimeInMillis();
            datePickerDialog.getDatePicker().setMaxDate(minDateInMillis);
            datePickerDialog.show();

        });
        /*from date*/

        /*to date*/
        DatePickerDialog.OnDateSetListener dateToDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarToDate.set(Calendar.YEAR, year);
            myCalendarToDate.set(Calendar.MONTH, monthOfYear);
            myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelToDateSE();
        };

        binding.textToDate.setText(formattedDate);
        binding.textToDate.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, R.style.DialogThemeTwo, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            long minDateInMillis = myCalendarFromDate.getTimeInMillis();
            datePickerDialog.getDatePicker().setMinDate(minDateInMillis);
                /*if (myCalendarToDate.before(myCalendarFromDate)) {
                    // "To" date is smaller than "from" date, show an error message or take appropriate action
                    Toast.makeText(mActivity, "Invalid date selection", Toast.LENGTH_SHORT).show();
                    // Reset the "to" date to the previous valid selection
                }*/
            datePickerDialog.show();
        });
        /*to date*/


        //end SE date spinner


    }


    @Override
    public void onBackPressed() {
        clearSharePreference();

//        Intent intent = new Intent(mActivity, MainActivity.class);
//        startActivity(intent);
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelFromDate() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        inputStartDate.setText(sdf.format(myCalendarFromDate.getTime()));
        Log.d("yoyoyo", "hohoho" + sdf);

        //  Objects.requireNonNull(binding.textToDate.getText()).clear();
        //  fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        //   startDate = inputStartDate.getText().toString().trim();

        String calstartDate = inputStartDate.getText().toString().trim();
        String originalFormat = "dd-MM-yyyy";
        String desiredFormat = "yyyy-MM-dd";

        startDate = convertDateFormat(calstartDate, originalFormat, desiredFormat);
        Log.d("formateddate", "" + startDate);


        // inputEndDate.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelToDate() {
        String myFormat = "dd-MM-yyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        inputEndDate.setText(sdf.format(myCalendarToDate.getTime()));
        //  endDate = inputEndDate.getText().toString().trim();
        String calendDate = inputEndDate.getText().toString().trim();
        String originalFormat = "dd-MM-yyyy";
        String desiredFormat = "yyyy-MM-dd";

        endDate = convertDateFormat(calendDate, originalFormat, desiredFormat);
        Log.d("formateddate", "" + startDate);


    }


    @SuppressLint("SetTextI18n")
    private void updateLabelFromDateSE() {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        binding.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()));
        Log.d("yoyoyo", "hohoho" + sdf);

        //  Objects.requireNonNull(binding.textToDate.getText()).clear();
        //  fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
        //   startDate = inputStartDate.getText().toString().trim();

        String calstartDate = binding.textFromDate.getText().toString().trim();
        String originalFormat = "dd-MM-yyyy";
        String desiredFormat = "yyyy-MM-dd";

        startDate = convertDateFormat(calstartDate, originalFormat, desiredFormat);
        Log.d("formateddate", "" + startDate);


        // inputEndDate.getText().clear();
    }

    @SuppressLint("SetTextI18n")
    private void updateLabelToDateSE() {
        String myFormat = "dd-MM-yyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        //  endDate = inputEndDate.getText().toString().trim();
        String calendDate = binding.textToDate.getText().toString().trim();
        String originalFormat = "dd-MM-yyyy";
        String desiredFormat = "yyyy-MM-dd";

        endDate = convertDateFormat(calendDate, originalFormat, desiredFormat);
        Log.d("formateddate", "" + startDate);


    }


    private void SEUsersList(String districtId) {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            binding.progressBar.setVisibility(View.INVISIBLE);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String paramStr = SEUserListByDistictQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], districtId);
            Call<ModelSEUser> call = apiInterface.callSEUserListApiByDistict(SEUserListByDistict(), paramData);
            call.enqueue(new Callback<ModelSEUser>() {
                @Override
                public void onResponse(@NonNull Call<ModelSEUser> call, @NonNull Response<ModelSEUser> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    SEUser_list = response.body().getSEUsersList();
                                    if (SEUser_list != null && SEUser_list.size() > 0) {
                                        Collections.reverse(SEUser_list);
                                        ModelSEUserList l = new ModelSEUserList();
                                        l.setUserId(valueOf(-1));
                                        l.setUserName("--" + getResources().getString(R.string.se_user) + "--");
                                        SEUser_list.add(l);
                                        Collections.reverse(SEUser_list);
                                        ArrayAdapter<ModelSEUserList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, SEUser_list);
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerSEUsers.setAdapter(dataAdapter);
                                        Log.d("userid", "" + selecteduserid);
                                        if (selecteduserid != null && !selecteduserid.isEmpty()) {
                                            Log.d("ghjh", "" + selecteduserid);
                                            int userId = Integer.parseInt(selecteduserid); // replace with the user ID you want to select
                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelSEUserList user = dataAdapter.getItem(i);
                                                if (String.valueOf(user.getUserId()).equals(String.valueOf(userId))) {
                                                    spinnerSEUsers.setSelection(i);
                                                    break;
                                                }
                                            }

                                        }
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
                public void onFailure(@NonNull Call<ModelSEUser> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }


    private void districtList() {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);

            binding.progressBar.setVisibility(View.VISIBLE);

            //   Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            Call<ModelDistrict> call = apiInterface.callDistrictListApi(DistictList());
            call.enqueue(new Callback<ModelDistrict>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict> call, @NonNull Response<ModelDistrict> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    district_List = response.body().getDistrict_List();
                                    if (district_List != null && district_List.size() > 0) {
                                        Collections.reverse(district_List);
                                        ModelDistrictList l = new ModelDistrictList();
                                        l.setDistrictId(valueOf(-1));
                                        l.setDistrictNameEng("--" + getResources().getString(R.string.district) + "--");
                                        district_List.add(l);
                                        Collections.reverse(district_List);
                                        ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, district_List);
                                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerDistrict.setAdapter(dataAdapter);
                                        //   spinnerDistrict.setSelection(1);
                                        /*if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));
                                        }*/
                                        if (d2 != null && !d2.isEmpty()) {
                                            Log.d("ghjh", "" + d2);
                                            int userId = Integer.parseInt(d2); // replace with the user ID you want to select
                                            for (int i = 0; i < dataAdapter.getCount(); i++) {
                                                ModelDistrictList user = dataAdapter.getItem(i);
                                                if (String.valueOf(user.getDistrictId()).equals(String.valueOf(userId))) {
                                                    spinnerDistrict.setSelection(i);
                                                    break;
                                                }
                                            }
                                        }
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
                public void onFailure(@NonNull Call<ModelDistrict> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });

                            return;
                        }
                    }

                } else {
                    Intent intent = new Intent(mActivity, MarkAttendanceActivity.class);
                    startActivityForResult(intent, LAUNCH_MARK_ATTENDANCE_ACTIVITY);
                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_MARK_ATTENDANCE_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                attendanceDetailsInfoList = new ArrayList<>();
                attStatusData = new ArrayList<>();

                Log.d("cccccc", ">>>>");
                adapter.setData(attendanceDetailsInfoList, attStatusData);

                String USER_TYPE = preference.getUSER_TYPE();
                String USER_TYPE_ID = preference.getUSER_TYPE_ID();
                String USER_Id = preference.getUSER_Id();
                if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")
                        || USER_TYPE_ID.equals("3") && USER_TYPE.equalsIgnoreCase("Support")) {
                    //  getAttendanceList(USER_Id, "", "");
                    isLoading = false;

                    currentPage =1;
                    getAttendanceList(USER_Id, SDate, EDate);
                }
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                Intent wpsIntent = new Intent(Intent.ACTION_VIEW);
                wpsIntent.setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                wpsIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(wpsIntent);
                } catch (ActivityNotFoundException e) {
                    Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=cn.wps.moffice_eng"));
                    startActivity(playStoreIntent);
                    // Handle the case where WPS Office or a compatible app is not installed
                }
            }
        }


    }


    private void Pdfform() {
        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd/MM/yyyy";
        String corStartDate = convertDateFormat(startDate, originalFormat, desiredFormat);
        String corEndDate = convertDateFormat(endDate, originalFormat, desiredFormat);
        Log.d("formateddate", "" + corStartDate);
        Intent intent = new Intent(mActivity, Excelfortable.class);
        intent.putExtra("startDateKey", corStartDate);
        intent.putExtra("endDateKey", corEndDate);
        startActivity(intent);
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


    private void ExcelformTable() {


        mActivity = this;
        preference =new PrefManager(mActivity);;
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("courses", null);
        if (json == null) {
            Log.e("ExcelGeneration", "No data found in SharedPreferences.");
            Toast.makeText(this, "No data available to generate Excel", Toast.LENGTH_SHORT).show();
            return;
        }

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<AttendanceDetailsInfo>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        attendanceDetailsInfoList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
       /* if (attendanceDetailsInfoList == null) {
            // if the array list is empty
            // creating a new array list.
            attendanceDetailsInfoList = new ArrayList<>();
            Log.d("nbb", "" + attendanceDetailsInfoList);
        }*/


        if (attendanceDetailsInfoList == null || attendanceDetailsInfoList.isEmpty()) {
            attendanceDetailsInfoList = new ArrayList<>();

            Log.e("ExcelGeneration", "Attendance list is empty.");
            Toast.makeText(this, "No attendance data found", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("gfhvbb", "" + attendanceDetailsInfoList);
        String originalFormat = "yyyy-MM-dd";
        String desiredFormat = "dd-MM-yyyy";
        String corStartDate = convertDateFormat(startDate, originalFormat, desiredFormat);
        String corEndDate = convertDateFormat(endDate, originalFormat, desiredFormat);
        Log.d("formateddate", "" + corStartDate);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("Record " + corStartDate + " - " + corEndDate);

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
        RichTextString richText1 = new HSSFRichTextString("User\nName");
        richText1.applyFont(boldFont);
        // Set the RichTextString to the cell
        cellB.setCellValue(richText1);
        firstSheet.setColumnWidth(1, 4000);
        //   cellB.setCellStyle(cellStyle);
        HSSFCell cellC = rowA.createCell(2);
        firstSheet.setColumnWidth(2, 4000);
        RichTextString richText2 = new HSSFRichTextString("Punch\nDate");
        richText2.applyFont(boldFont);
        cellC.setCellValue(richText2);
        //  cellC.setCellValue(new HSSFRichTextString("Punch\nDate"));
        HSSFCell cellD = rowA.createCell(3);
        firstSheet.setColumnWidth(3, 4000);
        RichTextString richText3 = new HSSFRichTextString("Punch\nDay");
        richText3.applyFont(boldFont);
        cellD.setCellValue(richText3);
        // cellD.setCellValue(new HSSFRichTextString("Punch\nDay"));
        HSSFCell cellE = rowA.createCell(4);
        firstSheet.setColumnWidth(4, 4000);
        RichTextString richText4 = new HSSFRichTextString("PunchInTime");
        richText4.applyFont(boldFont);
        cellE.setCellValue(richText4);
        //  cellE.setCellValue(new HSSFRichTextString("PunchIn\nTime"));
        HSSFCell cellF = rowA.createCell(5);
        firstSheet.setColumnWidth(5, 4000);
        RichTextString richText5 = new HSSFRichTextString("PunchOutTime");
        richText5.applyFont(boldFont);
        cellF.setCellValue(richText5);
        // cellF.setCellValue(new HSSFRichTextString("Punch\nOut\nTime"));
        //    cellF.setCellStyle(cellStyle);
        HSSFCell cellG = rowA.createCell(6);
        firstSheet.setColumnWidth(6, 20000);
        RichTextString richText6 = new HSSFRichTextString("AddressIn");
        richText6.applyFont(boldFont);
        cellG.setCellValue(richText6);
        //  cellG.setCellValue(new HSSFRichTextString("AddressIn"));
        //    cellG.setCellStyle(cellStyle);
        HSSFCell cellH = rowA.createCell(7);
        firstSheet.setColumnWidth(7, 20000);
        //cellH.setCellValue(new HSSFRichTextString("AddressOut"));
        RichTextString richText7 = new HSSFRichTextString("AddressOut");
        richText7.applyFont(boldFont);
        cellH.setCellValue(richText7);


        Log.d("mylist", " -------------- " + attendanceDetailsInfoList);
        if (attendanceDetailsInfoList != null && attendanceDetailsInfoList.size() > 0) {
            for (int i = 0; i < attendanceDetailsInfoList.size(); i++) {
                AttendanceDetailsInfo detailsInfo = attendanceDetailsInfoList.get(i);
                String districtName = String.valueOf(detailsInfo.getDistrictName());
                String attendanceValue = String.valueOf(detailsInfo.getUsername());
                String date = String.valueOf(detailsInfo.getPunchInDate());
                String day = String.valueOf(detailsInfo.getDayName());
                String intime = String.valueOf(detailsInfo.getPunchInTime());
                String outtime = String.valueOf(detailsInfo.getPunchOutTime());
                String inadd = String.valueOf(detailsInfo.getAddress_In());
                String outadd = String.valueOf(detailsInfo.getAddress_Out());
                Row dataRow = firstSheet.createRow(i + 1); // Start from row 1 for data
                // Column 1: District Name
                dataRow.createCell(0).setCellValue(districtName);
                // Column 2: Attendance Value
                dataRow.createCell(1).setCellValue(attendanceValue);
                dataRow.createCell(2).setCellValue(date);
                dataRow.createCell(3).setCellValue(day);
                dataRow.createCell(4).setCellValue(intime);
                dataRow.createCell(5).setCellValue(outtime);
                dataRow.createCell(6).setCellValue(inadd);
                dataRow.createCell(7).setCellValue(outadd);
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
            Toast.makeText(AttendanceListingActivity.this, "Excel Sheet Download ", Toast.LENGTH_SHORT).show();
        }



        try {
            Uri fileUri = FileProvider.getUriForFile(
                    AttendanceListingActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", // Replace with your app's provider authority
                    filePathh
            );

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(fileUri, "application/vnd.ms-excel");
            openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(openIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(AttendanceListingActivity.this, "No app found to open Excel files.", Toast.LENGTH_SHORT).show();
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

    private void dashboardApi(String USER_Id, String Month, String Year, String SEUserId) {
//Progress Bar while connection establishes
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            binding.progressBar.setVisibility(View.VISIBLE);
            USER_Id = preference.getUSER_Id();

            Log.d("dashboardAPI", " " + USER_Id + "   " + Month + "   " + Year + "   " + SEUserId);

            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            Call<DashbordRoot> call = apiInterface.dashboardApi(USER_Id, Month, Year, SEUserId);
            call.enqueue(new Callback<DashbordRoot>() {
                @Override
                public void onResponse(@NonNull Call<DashbordRoot> call, @NonNull Response<DashbordRoot> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);


                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    DashbordRoot dashbordRoot = response.body();
                                    DashboardUserDetails dashboardUserDetails =
                                            dashbordRoot.getUser_details();
                                    String CreatedCampCount = dashboardUserDetails.getCreatedCampCount();
                                    String OrganizedCampCount = dashboardUserDetails.getOrganizedCampCount();
                                    String AttendanceCount = dashboardUserDetails.getAttendanceCount();
                                    String presentCount = String.valueOf(dashboardUserDetails.getPersent());
                                    String abstCount = String.valueOf(dashboardUserDetails.getAbsent());
                                    String halfdayCount = String.valueOf(dashboardUserDetails.getHalfDay());
                                    String leaveCount = String.valueOf(dashboardUserDetails.getLeave());
                                    String tourCount = String.valueOf(dashboardUserDetails.getTour());
                                    // binding.totelAttandance.setText(CreatedCampCount);
                                    // binding.preAttandance.setText(OrganizedCampCount);
                                    binding.totelAttandance.setText("Total Attn.\n" + AttendanceCount);
                                    binding.preAttandance.setText("Present.\n" + presentCount);
                                    binding.aabAttandance.setText("Absent.\n" + abstCount);
                                    binding.halfday.setText("HalfDay.\n" + halfdayCount);
                                    binding.leave.setText("Leave.\n" + leaveCount);
                                    binding.tour.setText("Tour.\n" + tourCount);


                                    Log.d("ghmn", "asmbnmldjfjas" + CreatedCampCount + "  " + OrganizedCampCount + " " + AttendanceCount);
                                } else {
                                    makeToast(String.valueOf(response.body().getMessage()));
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
                public void onFailure(@NonNull Call<DashbordRoot> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    binding.progressBar.setVisibility(View.GONE);


                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
            Utils.hideCustomProgressDialogCommonForAll();
            binding.progressBar.setVisibility(View.GONE);
        }
    }


    private void setNormalPicker() {
        //  setContentView(R.layout.activity_main);
        final Calendar today = Calendar.getInstance();
        findViewById(R.id.date_selt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long minDate = Long.parseLong("1609439400000");
                Calendar c = Calendar.getInstance();
                long maxDate = c.getTimeInMillis();
                DatePickerDialog(mContext, binding.dateSelt, minDate, maxDate);

            }
        });

    }


    private void DatePickerDialog(Context con, final View v, long mindate, long maxDate) {

        //    DatePickerDialog dpd = new DatePickerDialog(con, new DatePickerDialog.OnDateSetListener() {
        DatePickerDialog dpd = new DatePickerDialog(con,
                android.app.AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date_selected_year = year;
                date_selected_month = monthOfYear;
                date_selected_day = dayOfMonth;
                // TODO Auto-generated method stub
                DecimalFormat formatter = new DecimalFormat("00");
                String date = formatter.format(dayOfMonth);
                String month = formatter.format(monthOfYear + 1);
                Month = month;
                Year = String.valueOf(year);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                int monthnum = Integer.parseInt(Month) - 1;
                cal.set(Calendar.MONTH, monthnum);
                String month_name = month_date.format(cal.getTime());

                //   Log.e(">>>>>", "" + monthnum);
                String ed_monthYear = "--" + month_name + "  " + Year + "--";

                if (v instanceof Button) {
                    Button btn = (Button) v;
//                    btn.setText(date + "/" + month + "/" + year);
//                    btn.setTag(date + "/" + month + "/" + year);
                    btn.setText(ed_monthYear);
                } else if (v instanceof EditText) {
                    EditText txt = (EditText) v;
//                    txt.setText(date + "/" + month + "/" + year);
//                    txt.setTag(date + "/" + month + "/" + year);
                    txt.setText(ed_monthYear);
                }

                //    binding.buttonTrainingFilter.performClick();

            }
        }, date_selected_year, date_selected_month, date_selected_day) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            }
        };
        if (mindate > 0)
            dpd.getDatePicker().setMinDate(mindate);
        if (maxDate > 0)
            dpd.getDatePicker().setMaxDate(maxDate);
//        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        dpd.show();
        //   dpd.findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
    }
}

