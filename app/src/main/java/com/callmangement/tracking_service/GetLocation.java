package com.callmangement.tracking_service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;

import com.callmangement.database.DbController;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.utils.MyDialog;
import com.callmangement.utils.PrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;
import static com.callmangement.utils.Constants.currentLat;
import static com.callmangement.utils.Constants.currentLong;
import static com.callmangement.utils.Constants.currentTime;
import static com.callmangement.utils.MyDialog.alertD;

public class GetLocation {
    private LocationRequest mLocationRequest;
    private final Context mContext;
    private final PrefManager prefManager;

    @SuppressLint("MissingPermission")
    public GetLocation(Context context) {
        mContext = context;
        prefManager = new PrefManager(context);
    }

    public void build() {
        createLocationRequest();
        checkLocationSettings();
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).setAlwaysShow(false);
        initializeTimerTask(builder);
    }

    public void initializeTimerTask(final LocationSettingsRequest.Builder builder) {
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(Objects.requireNonNull(mContext)).checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation();
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    getLocation();
                } else {
                    Runnable runnable = () -> {
                        if (alertD == null || !alertD.isShowing()) {
                            Intent intent = new Intent(mContext, MyDialog.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    };
                    Handler handler = new Handler();
                    handler.postDelayed(runnable, 1500);
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(mContext));
        Task<Location> location = mFusedLocationClient.getLastLocation();
        location.addOnSuccessListener(location1 -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
            double lat1, lon1, lat2, lon2;
            Date date1;
            Date date2;
            long diffSeconds = 0;
            double speed = 0;
            if (location1 != null) {
                currentLat = prefManager.getUserCurrentlat();
                currentLong = prefManager.getUserCurrentlong();
                currentTime = prefManager.getUserCurrenttime();
                if ((currentLat == null || currentLat.equalsIgnoreCase("")) && ((currentLong == null || currentLong.equalsIgnoreCase("")))) {
                    currentLat = String.valueOf(location1.getLatitude());
                    currentLong = String.valueOf(location1.getLongitude());
                    Date c = Calendar.getInstance().getTime();
                    currentTime = simpleDateFormat.format(c);

                }
                else {
                    lat1 = Double.parseDouble(Objects.requireNonNull(currentLat));
                    lon1 = Double.parseDouble(currentLong);
                    lat2 = location1.getLatitude();
                    lon2 = location1.getLongitude();
                    //current time
                    Date c = Calendar.getInstance().getTime();
                    String currentTimeNew = simpleDateFormat.format(c);
                    try {
                        date1 = simpleDateFormat.parse(currentTime);
                        date2 = simpleDateFormat.parse(currentTimeNew);
                        //find difference
                        long diff = Objects.requireNonNull(date2).getTime() - Objects.requireNonNull(date1).getTime();
                        diffSeconds = diff / 1000;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000) % 24;
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        long difonlysec = diff / 1000;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Calculate distance
                    double earthRadius = 6371000; //meters
                    double dLat = Math.toRadians(lat2 - lat1);
                    double dLng = Math.toRadians(lon2 - lon1);
                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                                    Math.sin(dLng / 2) * Math.sin(dLng / 2);
                    double cc = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    float dist = (float) (earthRadius * cc);
                    float distMeter = dist * 1000;
                    //calculate speed
                    speed = distMeter / diffSeconds;
                    currentLat = String.valueOf(lat2);
                    currentLong = String.valueOf(lon2);
                    currentTime = currentTimeNew;
                }
                prefManager.setUserCurrentlat(currentLat);
                prefManager.setUserCurrentlong(currentLong);
                prefManager.setUserCurrenttime(currentTime);
                //calculate speed
//                    if (speed < 150000) {
//                        updateCityAndPincode(location.getLatitude(), location.getLongitude());
//                    }
                updateCityAndPincode(location1.getLatitude(), location1.getLongitude());
            }
        });
    }
    private void updateCityAndPincode(double latitude, double longitude) {
        try {
            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                String add1 = "" + addresses.get(0).getAddressLine(0);
                if (add1.length() > 0) {
                    saveLocation(add1, latitude, longitude);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveLocation(String address, double latitude, double longitude) {
        //Log.e("location","latitude - "+latitude +", longitude - "+longitude+", address - "+ address);
        new DbController(mContext).insertLocation(prefManager.getUseR_Id(),prefManager.getUseR_DistrictId(),String.valueOf(latitude), String.valueOf(longitude), address, DateTimeUtils.getCurrentDataTime());
    }

}
