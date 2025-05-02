package com.callmangement.firebase;

import static com.callmangement.utils.MyDialog.alertD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.callmangement.Network.APIService;
import com.callmangement.Network.RetrofitInstance;
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
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    private LocationRequest mLocationRequest;
    private PrefManager prefManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      //  Log.e(TAG, "onMessageReceived: " + remoteMessage.toString());
       // Log.e(TAG, "onMessageReceived: " + remoteMessage.getData());
        prefManager = new PrefManager(getApplicationContext());
        buildLocation();
        /*if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();
            if (FirebaseMessageController.parseParam(params));
        }*/
    }

    public void buildLocation() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).setAlwaysShow(false);
        initialize(builder);
    }

    public void initialize(final LocationSettingsRequest.Builder builder) {
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(Objects.requireNonNull(getApplicationContext())).checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation();
                }
//                else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//                    }
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                            1000,
//                            10, (LocationListener) this);
//                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    if (location != null) {
//                        double latitude = location.getLatitude();
//                        double longitude = location.getLongitude();
//                        getAddressFromLatLong(latitude, longitude);
//                    }
//                }
                else {
                    String address = "GPS not enabled.";
                    double latitude = 0.0;
                    double longitude = 0.0;
                    saveLocation(address, latitude, longitude);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void getLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getApplicationContext()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> location = mFusedLocationClient.getLastLocation();
        location.addOnSuccessListener(location1 -> {
            if (location1 != null) {
                getAddressFromLatLong(location1.getLatitude(), location1.getLongitude());
            } else {
                String address = "GPS not enabled.";
                double latitude = 0.0;
                double longitude = 0.0;
                saveLocation(address, latitude, longitude);
            }
        });
    }

    private void getAddressFromLatLong(double latitude, double longitude) {
        String address = "";
        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveLocation(address, latitude, longitude);
//        createLocationJsonArray(address, latitude, longitude);
    }

    private void saveLocation(String address, double latitude, double longitude) {
      //  Log.e("location", "latitude - " + latitude + ", longitude - " + longitude + ", address - " + address);
        new DbController(getApplicationContext()).insertLocation(prefManager.getUSER_Id(), prefManager.getUSER_DistrictId(), String.valueOf(latitude), String.valueOf(longitude), address, DateTimeUtils.getCurrentDataTime());
        try {
            getLocationDataFromLocalDB();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLocationDataFromLocalDB() throws JSONException {
        JSONArray locationJsonArray = new JSONArray();
        Cursor cursor = new DbController(getApplicationContext()).getLocationData();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String latitude = cursor.getString(2);
                    String longitude = cursor.getString(3);
                    String address = cursor.getString(4);
                    String location_date_time = cursor.getString(5);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("latitude", latitude);
                    jsonObject.put("longitude", longitude);
                    jsonObject.put("address", address);
                    jsonObject.put("location_Date_Time", location_date_time);
                    locationJsonArray.put(jsonObject);
                } while (cursor.moveToNext());
            }
            if (locationJsonArray.length() > 0) {
                insertLocationDataInServer(String.valueOf(locationJsonArray));
            }
        }
    }

    /*private void createLocationJsonArray(String address, double latitude, double longitude){
        JSONArray locationJsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("address", address);
            jsonObject.put("location_Date_Time", DateTimeUtils.getCurrentDataTime());
            locationJsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (locationJsonArray.length() > 0) {
            insertLocationDataInServer(String.valueOf(locationJsonArray));
        }
    }*/

    private void insertLocationDataInServer(String locationJsonArray) {
        RequestBody jsonArrBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(locationJsonArray));
        APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
        Call<ResponseBody> call = service.saveSELocations(prefManager.getUSER_Id(), prefManager.getUSER_DistrictId(), prefManager.getDEVICE_ID(), jsonArrBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                String responseStr = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseStr);
                                String status = jsonObject.optString("status");
                                String message = jsonObject.optString("message");
                            //    Log.e("location", message);
//                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                if (status.equals("200")) {
                                    new DbController(getApplicationContext()).deleteLocationData();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }
}