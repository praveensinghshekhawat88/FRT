package com.callmangement.EHR.ehrActivities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.callmangement.R;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.api.Constants;
import com.callmangement.EHR.api.MultipartRequester;
import com.callmangement.databinding.ActivityMarkAttendanceBinding;
import com.callmangement.EHR.imagepicker.model.Config;
import com.callmangement.EHR.imagepicker.model.Image;
import com.callmangement.EHR.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.EHR.models.ModelMarkAttendance;
import com.callmangement.EHR.support.CompressImage;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Utils;
import com.callmangement.EHR.tracking_service.GpsUtils;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.support.permissions.PermissionHandler;
import com.callmangement.utils.PrefManager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.callmangement.support.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MarkAttendanceActivity extends BaseActivity {
    Activity mActivity;
    private ActivityMarkAttendanceBinding binding;
    String[] permissions;
    PrefManager preference;
    double latitude = 0.0;
    double longitude = 0.0;
    String completeAddressStr = "";
    String  nearbyAddessStr ="";
    public final int REQUEST_PICK_IMAGE_ONE = 1111;
    private String challanImageStoragePath = "";
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1;
    String yh;
   LocationManager  locationManager;
    String near_address,mapLink;
    private static final int REQUEST_LOCATION = 123; // You can use any integer value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMarkAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void setUpData() {
    }
    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.mark_attendance));
        selectImage(REQUEST_PICK_IMAGE_ONE);

        setUpData();
        setClickListener();
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        binding.buttonMarkAttendance.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                binding.buttonMarkAttendance.setEnabled(false);
                binding.buttonMarkAttendance.setClickable(false);
                binding.buttonMarkAttendance.setAlpha(0.5f);

                if (!challanImageStoragePath.equals("")) {
                    if (latitude != 0.0 && longitude != 0.0) {
                       String USER_Id = preference.getUSER_Id();
                        Calendar currentTime = Calendar.getInstance();
                        int hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY);
                      //  Log.d("sdfkdfd",""+hourOfDay);
                        if (hourOfDay > 20 || hourOfDay < 8) {
                            // Time is greater than or equal to 8 PM (20:00)
                            makeToast("Failed ! Attendance timing is 8 am to 8 Pm ");
                        }
                        else if (USER_Id.isEmpty()) {
                            makeToast(getResources().getString(R.string.UserId_empty));
                        } else if (completeAddressStr.isEmpty()) {
                           // makeToast(getResources().getString(R.string.address_empty));
                          //  markAttendance(USER_Id, "" + latitude, "" + longitude, "" + nearbyAddessStr);
                            getTheUserPermission();
                            if (near_address.isEmpty()){
                                makeToast("Proper network not available! Try after Sometime.");
                            }
                            else {
                                markAttendance(USER_Id, "" + latitude, "" + longitude, near_address);
                            }
                        } else {
                            markAttendance(USER_Id, "" + latitude, "" + longitude, completeAddressStr);

                        }
                      //  Log.d("ALl DetailSub"," "+USER_Id+"  "+latitude+"  "+longitude+"  "+completeAddressStr);
                    } else {
                        makeToast("Location not found.");
                    }
                } else {
                    makeToast("Please capture image.");
                }
            }
        });


        binding.buttonPickImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                selectImage(REQUEST_PICK_IMAGE_ONE);
            }
        });

        binding.imgSelfie.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
            }
        });
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }

    private void selectImage(final Integer requestCode) {
        ImagePicker.with(mActivity)
                .setToolbarColor("#212121")
                .setStatusBarColor("#000000")
                .setToolbarTextColor("#FFFFFF")
                .setToolbarIconColor("#FFFFFF")
                .setProgressBarColor("#4CAF50")
                .setBackgroundColor("#212121")
                .setCameraOnly(true)
                .setMultipleMode(true)
                .setFolderMode(true)
                .setShowCamera(true)
                .setFolderTitle("Albums")
                .setImageTitle("Galleries")
                .setDoneTitle("Done")
                .setMaxSize(1)
                .setSavePath(Constants.saveImagePath)
                .setSelectedImages(new ArrayList<>())
                .start(requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE_ONE && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                challanImageStoragePath = image.getPath();
                if (challanImageStoragePath.contains("file:/")) {
                    challanImageStoragePath = challanImageStoragePath.replace("file:/", "");
                }
                challanImageStoragePath = CompressImage.compress(challanImageStoragePath, this);
                File imgFile = new File(challanImageStoragePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                try {
                    binding.imgSelfie.setImageBitmap(ImageUtilsForRotate.ensurePortrait(challanImageStoragePath));
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
                catch (IOException e) {
                    binding.imgSelfie.setImageBitmap(myBitmap);

                    e.printStackTrace();
                }
             //   Log.d("imagesimages"," "+myBitmap);
                if (myBitmap!=null)
                {
                    binding.buttonPickImage.setVisibility(View.GONE);
                }
                else {
                    //now m
                  //  binding.buttonPickImage.setVisibility(View.VISIBLE);
                    Intent back_intent = new Intent(mActivity, MainActivity.class);
                    startActivity(back_intent);





                }


            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    private void checkPermission() {
      //  Log.d("versionversion"," "+Build.VERSION.SDK_INT);
       /* permissions =  new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.CAMERA};
*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions =  new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.CAMERA};
         //   Log.d("checkggg","b");

        }


/*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions =
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.CAMERA};
            Log.d("checkggg","0");


        }*/
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
        Permissions.check(mActivity,permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!getGpsStatus())
                        turnOnGps();
                    else buildLocation();
                  //  Log.d("check ","fgfg");
                }


             /// this sec else if condition uncomment by me..
         /*  else  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                  if (!getGpsStatus())
                      turnOnGps();
                  else buildLocation();
                  Log.d("check ","gkgh");
              }
*/

             /// this sec else if condition uncomment by me..

                else {
                    if (!getGpsStatus())
                        turnOnGps();
                    else buildLocation();
                  //  Log.d("check ","jgbhk");

                }
            }
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(mActivity, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultCallback<ActivityResult>
                    (){
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){

                    }
                    else

                }



            }
    );

   */


    private void turnOnGps() {
        new GpsUtils(mActivity).turnGPSOn(isGPSEnable -> {
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
                            int locIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(locIndex).getLatitude();
                            longitude = locationResult.getLocations().get(locIndex).getLongitude();
                            Typeface typeface = ResourcesCompat.getFont(mActivity, R.font.roboto_medium);

                             completeAddressStr = getAddressFromLatLong();

                            // nearbyAddessStr = getNearbyAddress();
//                            binding.txtLatitude.setText("Latitude: " + latitude);
//                            binding.txtLongitude.setText("Longitude: " + longitude);
                        }
                    }
                }, Looper.getMainLooper());
    }


    private String getAddressFromLatLong() {
        String localCompleteAddressStr = "";
        try {
          Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        //  Geocoder gcd = new Geocoder(getApplicationContext(), new Locale("hi"));
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

  /*  private String getNearbyAddress() {
        *//*String localCompleteAddressStr = "";
        String city = "";
        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                localCompleteAddressStr = "" + addresses.get(0).getAddressLine(0);
                city = "" + addresses.get(0).getAdminArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
            localCompleteAddressStr = "";
            city = "";
        }
        return city;*//*
        String nearbyAddressLine = "";
        double nearbyLatitude = latitude;
        double nearbyLongitude = longitude;
        // Perform reverse geocoding for nearby location
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> nearbyAddresses = geocoder.getFromLocation(nearbyLatitude, nearbyLongitude, 1);
            if (nearbyAddresses != null && !nearbyAddresses.isEmpty()) {
                // Nearby address found, use it as a fallback
                Address nearbyAddress = nearbyAddresses.get(0);
               nearbyAddressLine = nearbyAddress.getAddressLine(0);
                // Example: Display the nearby address in a TextView
           //     addressTextView.setText("Nearby Address: " + nearbyAddressLine);
            } else {
                // Handle the case where no nearby address was found
                // Example: Display an error message or inform the user
              // addressTextView.setText("No address found nearby.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle geocoding errors here
        }

        return nearbyAddressLine;


    }
*/

    private void markAttendance(String UserID, String Latitude, String Longitude, String Address) {
        mapLink = "http://maps.google.com/maps?q=" + Latitude + "," + Longitude;
        Log.d("UserID----","  "+UserID);
       // Log.d("AddressIs---->","  "+Address);
    // Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapLink));
     // startActivity(intent);
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            RequestBody attachment;
            String fileName = "";
            if (!challanImageStoragePath.equals("")) {
                fileName = new File(challanImageStoragePath).getName();
                attachment = RequestBody.create(MediaType.parse("multipart/form-data"), new File(challanImageStoragePath));
            } else {
                fileName = "";
                attachment = RequestBody.create(MediaType.parse("text/plain"), "");
            }
            Call<ModelMarkAttendance> call = apiInterface.callMarkAttendanceApi(MArkAttendance(),
                    MultipartRequester.fromString(UserID),
                    MultipartRequester.fromString(Latitude),
                    MultipartRequester.fromString(Longitude),
              MultipartRequester.fromString(Address),
                //  MultipartRequester.fromString(encodeToUtf8Hex(Address)),
                    MultipartRequester.fromString(mapLink),
                    MultipartBody.Part.createFormData("image", fileName, attachment));
            call.enqueue(new Callback<ModelMarkAttendance>() {
                @Override
                public void onResponse(@NonNull Call<ModelMarkAttendance> call, @NonNull Response<ModelMarkAttendance> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    if (response.isSuccessful()) {
                        Log.d("response----","  "+response.body().getMessage());
                        Log.d("mapLink----","  "+mapLink);

                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    makeToast(response.body().getMessage());
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();
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
                    binding.buttonMarkAttendance.setEnabled(true);
                    binding.buttonMarkAttendance.setClickable(true);

                }

                @Override
                public void onFailure(@NonNull Call<ModelMarkAttendance> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                    binding.buttonMarkAttendance.setEnabled(true);
                    binding.buttonMarkAttendance.setClickable(true);


                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }

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






/*    private String encodeUTF8(String value) {




            try {
                return new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                return value;
            }





    }*/









  /*  public void openLocationInMap(double latitude, double longitude, String label) {
        String uri = createLocationLink(latitude, longitude, label);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        // Check if there is a map app available to handle this intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Handle the case where no map app is available
            // You can show a message to the user or provide an alternative action.
        }
    }
*/




    private void getTheUserPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationGetter locationGetter = new LocationGetter(MarkAttendanceActivity.this, REQUEST_LOCATION, locationManager);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationGetter.OnGPS();
        } else {
            locationGetter.getLocation();
        }
    }


    public class LocationGetter {
        private final MarkAttendanceActivity mContext;
        private final LocationManager locationManager;
        private Geocoder geocoder;
        private final int REQUEST_LOCATION;
        public LocationGetter(MarkAttendanceActivity mContext, int requestLocation, LocationManager locationManager) {
            this.mContext = mContext;
            this.locationManager = locationManager;
            this.REQUEST_LOCATION = requestLocation;
        }

        public void getLocation() {
            if (ActivityCompat.checkSelfPermission(MarkAttendanceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MarkAttendanceActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (LocationGps != null) {
                    double lat = LocationGps.getLatitude();
                    double longi = LocationGps.getLongitude();
                    getTheAddress(lat, longi);
                } else if (LocationNetwork != null) {
                    double lat = LocationNetwork.getLatitude();
                    double longi = LocationNetwork.getLongitude();
                    getTheAddress(lat, longi);
                } else if (LocationPassive != null) {
                    double lat = LocationPassive.getLatitude();
                    double longi = LocationPassive.getLongitude();
                    getTheAddress(lat, longi);
                } else {
                    Toast.makeText(MarkAttendanceActivity.this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
                }
            }
        }
        private void getTheAddress(double latitude, double longitude) {
            List<Address> addresses;
            geocoder = new Geocoder(MarkAttendanceActivity.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                near_address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
             //   Log.d("neel", near_address);
             //   Log.d("your location","  "+near_address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void OnGPS() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }





}


