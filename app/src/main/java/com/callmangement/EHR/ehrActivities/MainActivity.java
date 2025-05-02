package com.callmangement.EHR.ehrActivities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static java.lang.String.valueOf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.callmangement.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.models.DashboardUserDetails;
import com.callmangement.EHR.models.DashbordRoot;
import com.callmangement.EHR.models.ModelDistrict;
import com.callmangement.EHR.models.ModelDistrictList;
import com.callmangement.EHR.models.ModelSEUser;
import com.callmangement.EHR.models.ModelSEUserList;
import com.callmangement.EHR.support.Utils;
import com.callmangement.databinding.EhrActivityMainBinding;
import com.callmangement.utils.PrefManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private Activity mActivity;
    private Context mContext;
    private EhrActivityMainBinding binding;
    SwipeRefreshLayout swipeRefreshLayout;
    PrefManager preference;
    private ArrayList<String> permissionsToRequest;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    public final int LAUNCH_MARK_ATTENDANCE_ACTIVITY = 333;
    String USER_TYPE_ID;
    String presentYear, presentMonth;
    String _maxMonth, month_namee;
    int _selectedyear;
    private String Month = "";
    private String Year = "";
    private String dis;
    private Spinner spinnerDistrict;
    private Spinner spinner, se_date_spinner;
    private Spinner spinnerSEUsers;
    private EditText inputStartDate;
    EditText DateSelector;
    private final String startDate = "";
    private final String endDate = "";
    private String districtId = "0";
    private int checkDistrict = 0;
    private String districtNameEng = "";
    private int checkSEUser = 0;
    private String SEUserNameEng = "";
    private String SEUserId = "0";
    private String s1, d1, d2, selecteduserid, USER_Id;
    private List<ModelDistrictList> district_List = new ArrayList<>();
    private List<ModelSEUserList> SEUser_list = new ArrayList<>();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final int checkFilter = 0;
    LinearLayoutCompat layoutDateRange;
    private final String myFormat = "yyyy-MM-dd";

    String curMonth;
    boolean temp;
    ImageView arrowleft, arrowright;


    int date_selected_year = 0;
    int date_selected_month = 0;
    int date_selected_day = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = EhrActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

    }

    private void init() {
        mActivity = this;
        mContext = this;
        preference = new PrefManager(mContext);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        binding.linHavingAttendanceDtl.setVisibility(View.GONE);
        setNormalPicker();
        // filterDialog();
        USER_Id = preference.getUSER_Id();
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.VISIBLE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText("EHR");
        binding.textDestrict.setVisibility(View.GONE);
        try {
            binding.textUsername.setText(preference.getUSER_NAME());
            binding.textEmail.setText(preference.getUSER_EMAIL());
            String USER_TYPE = preference.getUSER_TYPE();
            String USER_TYPE_ID = preference.getUSER_TYPE_ID();
            if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")) {
                binding.textDestrict.setVisibility(View.VISIBLE);
                binding.textDestrict.setText(getResources().getString(R.string.district) + " : " + preference.getUSER_District());
            }
        } catch (Exception e) {
        }

        setClickListener();
    }

    private void setClickListener() {

      /*  binding.leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                Month = String.valueOf(c.get(Calendar.MONTH)+1);

            }
        });

      binding.leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                Month = String.valueOf(c.get(Calendar.MONTH)-1);

            }
        });*/

     /*   binding.actionBar.ivThreeDot.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, binding.actionBar.ivThreeDot);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(getResources().getString(R.string.do_you_want_to_logout_from_this_app))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.logout), (dialog, id) -> {
                            dialog.cancel();
                            logout();
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.alert));
                alert.show();
                return true;
            });
            popupMenu.show();
        });*/

        temp = false; // Initial state of the visibility flag

        binding.buttonComplaintTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.linHavingAttendanceDtl.setVisibility(View.VISIBLE);

                if (!temp) {
                    binding.linHavingAttendanceDtl.setVisibility(View.VISIBLE);
                } else {
                    binding.linHavingAttendanceDtl.setVisibility(View.GONE);
                }
                temp = !temp;


            }
        });
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, com.callmangement.ui.home.MainActivity.class);
                startActivity(i);
            }
        });

        binding.buttonOrganiseCamp.setVisibility(View.GONE);
        binding.buttonManageSurvey.setVisibility(View.GONE);
        binding.buttonManageAttendance.setVisibility(View.GONE);
        binding.linHavingAttendanceAndCampCount.setVisibility(View.GONE);
        String USER_TYPE = preference.getUSER_TYPE();
        USER_TYPE_ID = preference.getUSER_TYPE_ID();
        USER_Id = preference.getUSER_Id();
        Log.d("usertypeid", " " + USER_TYPE_ID);
        if (USER_TYPE_ID.equals("1") && USER_TYPE.equalsIgnoreCase("Admin")) {
            binding.buttonOrganiseCamp.setVisibility(View.VISIBLE);
            binding.buttonManageAttendance.setVisibility(View.VISIBLE);

            binding.textManageCamps.setText("View Camp");
            binding.textManageAttendance.setText("View Attendance");
            binding.buttonMarkAttendance.setVisibility(View.GONE);
            binding.llDisUser.setVisibility(View.VISIBLE);
            binding.buttonFeedbackform.setVisibility(View.GONE);
            binding.buttonFeedbackformlist.setVisibility(View.VISIBLE);

            binding.linHavingAttendanceAndCampCount.setVisibility(View.VISIBLE);
            Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            Month = curMonth;
            Year = year;
            SEUserId = "5";

            filterDialog();
            dashboardApi(USER_Id, Month, Year, SEUserId);

        } else if (USER_TYPE_ID.equals("2") && USER_TYPE.equalsIgnoreCase("Manager") || USER_TYPE_ID.equals("16") && USER_TYPE.equalsIgnoreCase("HRManager")) {
            binding.buttonOrganiseCamp.setVisibility(View.VISIBLE);
            binding.buttonManageAttendance.setVisibility(View.VISIBLE);
            binding.textManageCamps.setText("View Camp");
            binding.textManageAttendance.setText("View Attendance");
            binding.buttonMarkAttendance.setVisibility(View.GONE);
            binding.llDisUser.setVisibility(View.VISIBLE);
            binding.linHavingAttendanceAndCampCount.setVisibility(View.VISIBLE);
            binding.buttonTrainingFilter.setVisibility(View.VISIBLE);
            binding.buttonFeedbackform.setVisibility(View.GONE);
            binding.buttonFeedbackformlist.setVisibility(View.VISIBLE);
            Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            Month = curMonth;
            Year = year;
            SEUserId = "5";
            filterDialog();
            dashboardApi(USER_Id, Month, Year, SEUserId);


        } else if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")) {
            binding.buttonOrganiseCamp.setVisibility(View.VISIBLE);
            binding.buttonManageAttendance.setVisibility(View.VISIBLE);
            binding.textManageCamps.setText("Arrange Camp");
            binding.textManageAttendance.setText("View Attendance");
            binding.buttonManageSurvey.setVisibility(View.VISIBLE);
            binding.textManageSurvey.setText("Manage Survey Form");
            binding.linHavingAttendanceAndCampCount.setVisibility(View.VISIBLE);
            binding.buttonMarkAttendance.setVisibility(View.VISIBLE);
            binding.llDisUser.setVisibility(View.GONE);
            binding.buttonFeedbackform.setVisibility(View.VISIBLE);
            binding.buttonFeedbackformlist.setVisibility(View.VISIBLE);
            filterDialog();
            Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            Month = curMonth;
            Year = year;
            SEUserId = USER_Id;
            dashboardApi(USER_Id, Month, Year, SEUserId);
} else if (USER_TYPE_ID.equals("3") && USER_TYPE.equalsIgnoreCase("Support")) {
            // binding.buttonOrganiseCamp.setVisibility(View.VISIBLE);
            binding.buttonManageAttendance.setVisibility(View.VISIBLE);
            //binding.textManageCamps.setText("Arrange Camp");
            binding.textManageAttendance.setText("View Attendance");
            binding.linHavingAttendanceAndCampCount.setVisibility(View.VISIBLE);
            binding.buttonMarkAttendance.setVisibility(View.GONE);
            binding.llDisUser.setVisibility(View.GONE);
            binding.buttonFeedbackform.setVisibility(View.VISIBLE);
            binding.buttonFeedbackformlist.setVisibility(View.VISIBLE);
            filterDialog();
            Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
            Month = curMonth;
            Year = year;
            SEUserId = USER_Id;
            dashboardApi(USER_Id, Month, Year, SEUserId);
        } else if (USER_TYPE_ID.equals("11") && USER_TYPE.equalsIgnoreCase("SurveyUser")) {
            binding.buttonManageSurvey.setVisibility(View.VISIBLE);
            binding.textManageSurvey.setText("Manage Survey Form");
            binding.buttonMarkAttendance.setVisibility(View.GONE);
            binding.filter.setVisibility(View.GONE);
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        int monthnum = Integer.parseInt(Month) - 1;
        cal.set(Calendar.MONTH, monthnum);
        String month_name = month_date.format(cal.getTime());
    //    Log.e("", month_name);
        String ed_monthYear = "--" + month_name + "  " + Year + "--";
        binding.dateSelt.setText(ed_monthYear);
        binding.buttonOrganiseCamp.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, CampsListingActivity.class);
            startActivity(intent);
        });
        binding.buttonManageSurvey.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, SurveyFormsListingActivity.class);
            startActivity(intent);
        });
        binding.buttonManageAttendance.setOnClickListener(view -> {
          Intent intent = new Intent(mActivity, AttendanceListingActivity.class);
         //   Intent intent = new Intent(mActivity, AttendanceListingActivityTwo.class);
            intent.putExtra("YEAR",date_selected_year);
            intent.putExtra("MONTH",date_selected_month);
            intent.putExtra("DATE",date_selected_day);
            startActivity(intent);
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




        binding.buttonFeedbackform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                    Intent i = new Intent(MainActivity.this, FeedbackFormActivity.class);
                    startActivity(i);


            }
            // Check if the device supports vibration
        });
     binding.buttonFeedbackformlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                    Intent i = new Intent(MainActivity.this, FeedbackFormListActivity.class);
                i.putExtra("key_districtId", districtId);
              i.putExtra("key_fromDate", startDate);
                i.putExtra("key_toDate", endDate);
                 i.putExtra("key_selectedDate", date_selected_day);
                 i.putExtra("key_selectedDis", preference.getUSER_District());
                    startActivity(i);


            }
            // Check if the device supports vibration
        });
     binding.buttonPMReport.setVisibility(View.GONE);

        binding.buttonPMReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                Intent i = new Intent(MainActivity.this, FeedbackReportActivity.class);
                i.putExtra("key_districtId", districtId);
                i.putExtra("key_fromDate", startDate);
                i.putExtra("key_toDate", endDate);
                i.putExtra("key_selectedDate", date_selected_day);
                i.putExtra("key_selectedDis", preference.getUSER_District());
                startActivity(i);


            }
            // Check if the device supports vibration
        });














        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

               /* Calendar c = Calendar.getInstance();
                String year = String.valueOf(c.get(Calendar.YEAR));
                curMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
                Month = curMonth;
                Year = year;
                SEUserId = USER_Id;
                dashboardApi(USER_Id, Month, Year, SEUserId);*/
                filterNow();


            }
        });












    }
    /*private void clearLoginPreferenceData() {
        preference.putString(Preference.USER_LOGIN_STATUS, "false");
        preference.putString(Preference.USER_ID, "0");
        preference.putString(Preference.USER_NAME, "");
        preference.putString(Preference.USER_EMAIL, "");
        preference.putString(Preference.USER_Mobile, "");
        preference.putString(Preference.USER_DISTRICT_ID, "0");
        preference.putString(Preference.USER_DISTRICT, "");
        preference.putString(Preference.USER_TYPE, "");
        preference.putString(Preference.USER_TYPE_ID, "");
    }
    private void logout() {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String USER_Id = preference.getString(Preference.USER_ID);
            String USER_Email = preference.getString(Preference.USER_EMAIL);
            String paramStr = LogoutQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], USER_Id);
            paramData.put(splitArray[1], USER_Email);
            Call<ModelLogout> call = apiInterface.callLogoutApi(Logout(), paramData);
            call.enqueue(new Callback<ModelLogout>() {
                @Override
                public void onResponse(@NonNull Call<ModelLogout> call, @NonNull Response<ModelLogout> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    clearLoginPreferenceData();

                                    Intent intent = new Intent(mActivity, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

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
                public void onFailure(@NonNull Call<ModelLogout> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();

                    makeToast(getResources().getString(R.string.error));

                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }*/


    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }


    private void dashboardApi(String USER_Id, String Month, String Year, String SEUserId) {
//Progress Bar while connection establishes
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            //  Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            binding.progressBar.setVisibility(View.VISIBLE);
            USER_Id = preference.getUSER_Id();
            Log.d("dashboardapi", " " + USER_Id + "   " + Month + "   " + Year + "   " + SEUserId);
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
                                    binding.textcreatedCampCount.setText(CreatedCampCount);
                                    binding.textCampsCount.setText(OrganizedCampCount);
                                    binding.textAttendanceCount.setText(AttendanceCount);
                                    binding.textAttendanceprst.setText(presentCount);
                                    binding.textAbsent.setText(abstCount);
                                    binding.texthalfday.setText(halfdayCount);
                                    binding.textLeave.setText(leaveCount);
                                    binding.textTour.setText(tourCount);


                                    Log.d("ghmn", "asmbnmldjfjas" + CreatedCampCount + "  " + OrganizedCampCount + " " + AttendanceCount);
                                } else {


                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                    dialog.setCancelable(false);
                                    // dialog.setTitle("Dialog on Android");
                                    dialog.setMessage("Month And Year Should Be Less Than Current Month And Year");
                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Action for "Delete".
                                            dialog.dismiss();
                                        }
                                    })
                                    ;

                                    final AlertDialog alert = dialog.create();
                                    alert.show();


                                    //    makeToast(String.valueOf(response.body().getMessage()));
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MainActivity.this, com.callmangement.ui.home.MainActivity.class);
        startActivity(i);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("Exit Application");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();*/

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


    private void filterDialog() {
        try {

            spinnerDistrict = findViewById(R.id.spinnerDistrict);
            spinnerSEUsers = findViewById(R.id.spinnerSEUsers);

            EditText inputTrainingNumber = findViewById(R.id.inputTrainingNumber);
            Button buttonTrainingFilter = findViewById(R.id.buttonTrainingFilter);
          //  Log.d("jgjgk", d1);
           // Log.d("kjkkgk", d2);
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
            //  spinnerSEUsers.setSelection(1);


            spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (++checkDistrict > 1) {

                            String item = adapterView.getItemAtPosition(i).toString();
                          //  if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.district) + "--") ){


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

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                    dialog.setCancelable(false);
                                    // dialog.setTitle("Dialog on Android");
                                    dialog.setMessage("Please Select User. Which User's Count Do you Want?");
                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Action for "Delete".
                                            dialog.dismiss();
                                        }
                                    })
                                    ;

                                    final AlertDialog alert = dialog.create();
                                    alert.show();

                                    binding.textcreatedCampCount.setText("---");
                                    binding.textCampsCount.setText("---");
                                    binding.textAttendanceCount.setText("---");
                                    binding.textAttendanceprst.setText("---");
                                    binding.textAbsent.setText("---");
                                    binding.texthalfday.setText("---");
                                    binding.textLeave.setText("---");
                                    binding.textTour.setText("---");







                                    //  spinnerSEUsers.setSelection(1);

                                 /*   if (USER_TYPE_ID.equals("1") || USER_TYPE_ID.equals("2") || USER_TYPE_ID.equals("16")&&) {
                                        spinnerSEUsers.setSelection(1);

                                    }*/

                                    //   spinnerSEUsers.setSelection(1);

                                  // SEUsersList(districtId);

                                    //Toast.makeText(MainActivity.this, "No possible", Toast.LENGTH_SHORT).show();
                                }
                                SEUsersList(districtId);
                         //   }

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
                            Log.d("ghgfh", "fghgh" + SEUserNameEng + SEUserId);

                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            binding.buttonTrainingFilter.setOnClickListener(view -> {


                filterNow();





            });


            //   dashboardApi(  USER_Id,  Month ,  Year,SEUserId);

            // dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterNow() {


        if (SEUserId.equals("-1"))
        {
            //    Toast.makeText(mActivity, "Please Select User", Toast.LENGTH_SHORT).show();


            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(false);
            // dialog.setTitle("Dialog on Android");
            dialog.setMessage("Please Select User. Which User's Count Do you Want?");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Delete".
                    dialog.dismiss();
                }
            })
            ;

            final AlertDialog alert = dialog.create();
            alert.show();







        }
        else {
            int UserIdd = Integer.parseInt(SEUserId);
            Log.d("userid", "" + UserIdd);

            Log.d("districtList", districtNameEng + "  " + SEUserNameEng);

            Log.d("startDate_endDate", startDate + "  " + endDate);
            Log.d("RESUT--", USER_Id + "  " + Month+" "+Year + "  " + SEUserId);

            dashboardApi(USER_Id, Month, Year, SEUserId);
        }





    }

    private void SEUsersList(String districtId) {
        Log.d("idddddd"," "+ districtId);
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
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
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {

                                    Log.d("dataaaaaaa"," "+ response.body().getSEUsersList().toString());
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

                                        if (USER_TYPE_ID.equals("1") || USER_TYPE_ID.equals("2") || USER_TYPE_ID.equals("16")) {
                                            spinnerSEUsers.setSelection(1);

                                        }
                                        //   spinnerSEUsers.setSelection(1);
                                        Log.d("userid", " "+selecteduserid);
                                        if (selecteduserid != null && !selecteduserid.isEmpty()) {
                                            Log.d("ghjh"," "+ selecteduserid);
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
                                    else {


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
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            Call<ModelDistrict> call = apiInterface.callDistrictListApi(DistictList());
            call.enqueue(new Callback<ModelDistrict>() {
                @Override
                public void onResponse(@NonNull Call<ModelDistrict> call, @NonNull Response<ModelDistrict> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    Log.d("disssssssss", response.body().toString());
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
                                        spinnerDistrict.setSelection(1);
                                        SEUsersList(district_List.get(spinnerDistrict.getSelectedItemPosition()).getDistrictId());
                                        if (USER_TYPE_ID.equals("1") || USER_TYPE_ID.equals("2") || USER_TYPE_ID.equals("16")) {
                                            spinnerDistrict.setSelection(1);

                                        }

                                        //   spinnerDistrict.setSelection(1);

                                        /*if(d2!=null)
                                        {
                                            spinnerDistrict.setSelection(Integer.parseInt(d2));
                                        }*/
                                        if (d2 != null && !d2.isEmpty()) {
                                            Log.d("ghjh", d2);
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
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

    }


    private void setNormalPicker() {

        Calendar calendar = Calendar.getInstance();
        date_selected_year = calendar.get(Calendar.YEAR);
        date_selected_month = calendar.get(Calendar.MONTH);
        date_selected_day = calendar.get(Calendar.DAY_OF_MONTH);

        //  setContentView(R.layout.activity_main);
        final Calendar today = Calendar.getInstance();
        findViewById(R.id.date_selt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

   /*             MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MainActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        Log.d(TAG, "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
                        //   Toast.makeText(AttendanceDashboard.this, "Date set with month" + selectedMonth + " year " + selectedYear, Toast.LENGTH_SHORT).show();
                        Month = String.valueOf(selectedMonth + 1);
                        Log.d("sdsknljf", "gh" + String.valueOf(selectedMonth + 1));
                        Year = String.valueOf(selectedYear);
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                        int monthnum = Integer.parseInt(Month) - 1;
                        cal.set(Calendar.MONTH, monthnum);
                        month_namee = month_date.format(cal.getTime());

                        Log.e(">>>>>", "" + _selectedyear);
                        Log.e(">>>>>", "" + Integer.parseInt(presentYear));
                        Log.e(">>>>>", "" + monthnum);
                        Log.e(">>>>>", "" + Integer.parseInt(presentMonth));
                        String ed_monthYear = "--" + month_namee + "  " + Year + "--";
                        String _maxMonth;
                        if (_selectedyear<=Integer.parseInt(presentYear) && monthnum<=Integer.parseInt(presentMonth)) {
                            binding.dateSelt.setText(ed_monthYear);

                          //  _maxMonth = Year;
                        } else  {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setCancelable(false);
                           // dialog.setTitle("Dialog on Android");
                            dialog.setMessage("Month And Year Should Be Less Than Current Month And Year" );
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Action for "Delete".
                                            dialog.dismiss();
                                        }
                                    })
                                  ;

                            final AlertDialog alert = dialog.create();
                            alert.show();

                        }

                        binding.dateSelt.setText(ed_monthYear);


                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));


                Calendar c = Calendar.getInstance();
                presentYear = String.valueOf(c.get(Calendar.YEAR));
                presentMonth = String.valueOf(c.get(Calendar.MONTH));

                //   builder.setActivatedMonth(Integer.parseInt(presentMonth))
                //.setMinYear(2022)
                //         .setActivatedYear(Integer.parseInt(Year))
                // .setActivatedMonth(Integer.parseInt(month_namee))
                // .setMonthAndYearRange(Calendar.JANUARY,Calendar.MONTH,2015, Integer.parseInt(presentYear))
                // builder .setTitle("Select Month")
                //  .setMonthRange(Calendar.JANUARY, Integer.parseInt(presentMonth))
                //    .setMaxYear(Integer.parseInt(presentYear))
                //    .setMinYear(2022)
                //       .setMaxMonth(Calendar.DECEMBER);


                // .setYearRange(2022, Integer.parseInt(presentYear))


                if (_selectedyear==Integer.parseInt(presentYear))
                {
                    builder .setMonthAndYearRange(Calendar.JANUARY, Integer.parseInt(presentMonth), 2022, Integer.parseInt(presentYear));

                }

                else {
                    builder .setMonthAndYearRange(Calendar.JANUARY, Calendar.DECEMBER, 2022, Integer.parseInt(presentYear));

                }

                        //.showMonthOnly()
                        // .showYearOnly()
                      builder  .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int selectedMonth) {
                                Log.d(TAG, "Selected month : " + selectedMonth);


                                // Toast.makeText(MainActivity.this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {
                                Log.d(TAG, "Selected year : " + selectedYear);


                               _selectedyear = selectedYear;
                                // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build()
                        .show();*/


                long minDate = Long.parseLong("1609439400000");
                Calendar c = Calendar.getInstance();
                long maxDate = c.getTimeInMillis();
                DatePickerDialog(mContext, binding.dateSelt, minDate, maxDate);
                //        showYearMonthPicker();
            }
        });


    }

    Calendar calendar = Calendar.getInstance();

    private void changeMonth(int delta) {
        calendar.add(Calendar.MONTH, delta);
        int year = Integer.parseInt(Year);
        int month = Integer.parseInt(Month);

    }

    private void DatePickerDialog(final Context con, final View v, long mindate, long maxDate) {
        //    DatePickerDialog dpd = new DatePickerDialog(con, new DatePickerDialog.OnDateSetListener() {
        DatePickerDialog dpd = new DatePickerDialog(con,
                android.app.AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date_selected_year = year;
                date_selected_month = monthOfYear;
                date_selected_day = dayOfMonth;
                Log.d(TAG, "selectedMonth : " + monthOfYear + " selectedYear : " + year);
                // TODO Auto-generated method stub
                DecimalFormat formatter = new DecimalFormat("00");
                String date = formatter.format(dayOfMonth);
                String month = formatter.format(monthOfYear + 1);
                Month = month;
                Log.d("sdsknljf", "gh" + (year + 1));
                Year = String.valueOf(year);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                int monthnum = Integer.parseInt(Month) - 1;
                cal.set(Calendar.MONTH, monthnum);
                month_namee = month_date.format(cal.getTime());
                _selectedyear = year;
                Log.e(">>>>>", "" + _selectedyear);
                Log.e(">>>>>", "" + monthnum);
                String ed_monthYear = "--" + month_namee + "  " + Year + "--";
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
                binding.buttonTrainingFilter.performClick();
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
