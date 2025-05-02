package com.callmangement.ui.attendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityAttendanceBinding;
import com.callmangement.model.attendance.ModelAttendance;
import com.callmangement.tracking_service.GpsUtils;
import com.callmangement.ui.home.MainActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.utils.PrefManager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.callmangement.support.permissions.PermissionHandler;
import com.callmangement.support.permissions.Permissions;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends CustomActivity {
    private ActivityAttendanceBinding binding;
    private String AddDate = "", AddTime = "";
    private String punchTime = "";
    private SimpleDateFormat completeDateFormat;
    private SimpleDateFormat sdfCompleteDate;
    private SimpleDateFormat sdfCompleteTime;
    private LocationRequest mLocationRequest;
    private PrefManager prefManager;
    double latitude = 0.0;
    double longitude = 0.0;
    String completeAddressStr = "";
    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendance);
        binding.actionBar.ivBack.setVisibility(View.GONE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.attendance));
        prefManager = new PrefManager(this);
        initView();
    }

    @SuppressLint({"Range", "SimpleDateFormat", "SetTextI18n"})
    private void initView() {
        completeDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
        sdfCompleteDate = new SimpleDateFormat("dd-MM-yyyy");
        sdfCompleteTime = new SimpleDateFormat("hh:mm:ss aa");
        AddDate = sdfCompleteDate.format(new Date());
        AddTime = sdfCompleteTime.format(new Date());
        String dayname = getDay();
        binding.txtDate.setText("Date : " + AddDate + ", " + dayname);
        punchTime = AddTime;
        binding.txtDay.setText(AddTime);
        updateTime();
        setUpOnClickListener();
    }

    private void setUpOnClickListener(){
        binding.btnPunch.setOnLongClickListener(v -> {
            checkPermission();
            return false;
        });
        binding.actionBar.ivBack.setOnClickListener(view -> finish());
    }

    private void checkPermission() {
       // String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
          //      , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions =  new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.CAMERA};
          //  Log.d("checkggg","b");

        }
        else {
            permissions =
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.CAMERA};
          //  Log.d("checkggg","0");
        }



        String rationale = "Please provide location permission so that you can ...";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(mContext, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    int backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (backgroundLocationPermissionApproved != 0)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> collectLocationPermissionDataDialog(), 500);
                    else {
                        if (!getGpsStatus())
                            turnOnGps();
                        else buildLocation();
                    }
                }

              else  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    int backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    if (backgroundLocationPermissionApproved != 0)
                        new Handler(Looper.getMainLooper()).postDelayed(() -> collectLocationPermissionDataDialog(), 500);
                    else {
                        if (!getGpsStatus())
                            turnOnGps();
                        else buildLocation();
                    }
                }



                else {
                    if (!getGpsStatus())
                        turnOnGps();
                    else buildLocation();
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void collectLocationPermissionDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.collect_location_permission_alert))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), (dialog, id) -> {
                    checkBackgroundLocationPermission();
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.alert));
        alert.show();
    }

    private void checkBackgroundLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        String rationale = "Please provide Access Background Location";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        Permissions.check(mContext, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (!getGpsStatus())
                    turnOnGps();
                else buildLocation();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void turnOnGps() {
        new GpsUtils(mContext).turnGPSOn(isGPSEnable -> {
            boolean isGPS = isGPSEnable;
        });
    }

    private boolean getGpsStatus() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void buildLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            completeAddressStr = getAddressFromLatLong();
                            markAttendance();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private String getAddressFromLatLong() {
        String localCompleteAddressStr = "";
        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                localCompleteAddressStr = addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            localCompleteAddressStr = "";
        }
        return localCompleteAddressStr;
    }

    private void markAttendance(){
        if (latitude != 0.0 && longitude != 0.0) {
            if (Constants.isNetworkAvailable(mContext)){
                String user_id = prefManager.getUSER_Id();
                String latitudeStr = latitude+"";
                String longitudeStr = longitude+"";
                String address = completeAddressStr;
                String punch_in_date = DateTimeUtils.getCurrentDate();
                String punch_in_time = punchTime;
                String latitude_out = "";
                String longitude_out = "";
                String address_out = "";
                String punch_out_date = "";
                String punch_out_time = "";
              //  insertAttendanceDataInServer(user_id,latitudeStr,longitudeStr,encodeToUtf8Hex(address), punch_in_date,
                      //  punch_in_time, latitude_out,longitude_out,address_out, punch_out_date,punch_out_time);
                insertAttendanceDataInServer(user_id,latitudeStr,longitudeStr,address, punch_in_date,
                        punch_in_time, latitude_out,longitude_out,address_out, punch_out_date,punch_out_time);
            } else {
                makeToast(getResources().getString(R.string.no_internet_connection));
            }

        } else {
            Snackbar snackbar = Snackbar.make(binding.btnPunch, getResources().getString(R.string.location_not_found), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void insertAttendanceDataInServer(String user_id,String latitude, String longitude, String address, String punch_in_date, String punch_in_time,String latitude_out, String longitude_out, String address_out, String punch_out_date, String punch_out_time) {
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
     //   Log.d("address--",""+address);
        Call<ModelAttendance> call = service.addAttendanceRecord(user_id,latitude,longitude,address, punch_in_date,punch_in_time,latitude_out,longitude_out,address_out,punch_out_date, punch_out_time);
        call.enqueue(new Callback<ModelAttendance>() {
            @Override
            public void onResponse(@NonNull Call<ModelAttendance> call, @NonNull Response<ModelAttendance> response) {
                if (response.isSuccessful()){
                    ModelAttendance model = response.body();
                    if (Objects.requireNonNull(model).getStatus().equals("200")){

                        makeToast(getResources().getString(R.string.your_attendance_marked_successfully));

                        startActivity(new Intent(mContext, MainActivity.class));
                        finish();
                    } else {
                        makeToast(getResources().getString(R.string.your_attendance_not_mark));
                    }
                } else {
                    makeToast(getResources().getString(R.string.error));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ModelAttendance> call, @NonNull Throwable t) {
                makeToast(getResources().getString(R.string.error_message));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getDay() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        String dayname = "";
        switch (day) {
            case 1:
                dayname = getResources().getString(R.string.sunday);
                break;
            case 2:
                dayname = getResources().getString(R.string.monday);
                break;
            case 3:
                dayname = getResources().getString(R.string.tuesday);
                break;
            case 4:
                dayname = getResources().getString(R.string.wednesday);
                break;
            case 5:
                dayname = getResources().getString(R.string.thursday);
                break;
            case 6:
                dayname = getResources().getString(R.string.friday);
                break;
            case 7:
                dayname = getResources().getString(R.string.saturday);
        }
        return dayname;
    }

    public void updateTime() {
        try {
            @SuppressLint("SetTextI18n") Runnable runnable = () -> {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCompleteDate = new SimpleDateFormat("dd-MM-yyyy");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCompleteTime = new SimpleDateFormat("hh:mm:ss aa");
                AddDate = sdfCompleteDate.format(new Date());
                AddTime = sdfCompleteTime.format(new Date());
                String dayname = getDay();
                binding.txtDate.setText(getResources().getString(R.string.date_str)+" : " + AddDate + ", " + dayname);
                punchTime = AddTime;
                binding.txtDay.setText(AddTime);
                updateTime();
            };

            Handler handler = new Handler();
            handler.postDelayed(runnable, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            boolean isGPS = getGpsStatus();
            if (isGPS)
                buildLocation();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }





    public static String encodeToUtf8Hex(String input) {
        StringBuilder utf8Hex = new StringBuilder();
        try {
            byte[] bytes = input.getBytes("UTF-8");
            for (byte b : bytes) {
                utf8Hex.append(String.format("\\x%02x", b));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return utf8Hex.toString();
    }

}