package com.callmangement.EHR.ehrActivities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.models.AttendanceDetailsInfo;
import com.callmangement.EHR.models.ModelAttendanceListing;
import com.callmangement.EHR.support.Utils;
import com.callmangement.R;
import com.callmangement.databinding.ActivityViewAttandanceDetailBinding;
import com.callmangement.utils.PrefManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceViewDetail extends BaseActivity {
    Activity mActivity;
    Context mcontext;
    private ActivityViewAttandanceDetailBinding binding;
    PrefManager preference;
    String USER_Id, formattedDate, districtId;
    private List<AttendanceDetailsInfo> attendanceDetailsInfoList = new ArrayList<>();
    public final int LAUNCH_MARK_ATTENDANCE_ACTIVITY = 333;
    private ArrayList<String> permissionsToRequest;
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    private final ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    AppCompatTextView tv_img_one, tv_img_two;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityViewAttandanceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        USER_Id = intent.getExtras().getString("YouruserIdId");
        formattedDate = intent.getExtras().getString("formattedDate");
        districtId = intent.getExtras().getString("districtId");
        //   Log.e("useid", USER_Id);
        // Log.e("districtId", districtId);
        mActivity = this;
        mcontext = this;
        preference = new PrefManager(mcontext);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        attendanceDetailsInfoList = new ArrayList<>();
        //USER_Id = preference.getString(Preference.USER_ID);
        getAttendanceList(USER_Id, formattedDate, formattedDate);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
      /*  Intent i = new Intent(mActivity,AttendanceListingActivity.class);
        startActivity(i);
*/
            }
        });

        tv_img_one = findViewById(R.id.tv_img_one);
        tv_img_two = findViewById(R.id.tv_img_two);

    }


    private void getAttendanceList(final String USER_Id, final String StartDate, final String EndDate) {
        if (Utils.isNetworkAvailable(mActivity)) {
            Utils.hideKeyboard(mActivity);
            Utils.showCustomProgressDialogCommonForAll(mActivity, getResources().getString(R.string.please_wait));
            APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mActivity, BaseUrl()).create(APIInterface.class);
            String paramStr = GetAttendanceListQueryParams();
            String[] splitArray = paramStr.split(",");
            Map<String, String> paramData = new HashMap<>();
            paramData.put(splitArray[0], USER_Id);
            paramData.put(splitArray[1], "0");
            paramData.put(splitArray[2], StartDate);
            paramData.put(splitArray[3], EndDate);
            Call<ModelAttendanceListing> call = apiInterface.callAttendanceListApi(GetAttendanceList(), paramData);
            call.enqueue(new Callback<ModelAttendanceListing>() {
                @Override
                public void onResponse(@NonNull Call<ModelAttendanceListing> call, @NonNull Response<ModelAttendanceListing> response) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                    if (response.body().getData().size() != 0) {
                                        Log.d("alldata", "" + response.body().getData());
                                        String username = response.body().getData().get(0).getUsername();
                                        binding.tvUserName.setText(username);
                                        String date = response.body().getData().get(0).getPunchInDate();
                                        binding.tvPunchInDate.setText(date);
                                        String day = response.body().getData().get(0).getDayName();
                                        binding.tvDay.setText(day);
                                        String punchintime = response.body().getData().get(0).getPunchInTime();
                                        binding.tvPunchInTime.setText(punchintime);
                                        String punchouttim = response.body().getData().get(0).getPunchOutTime();
                                        binding.tvPunchOutTime.setText(punchouttim);
                                        String address_in = response.body().getData().get(0).getAddress_In();
                                        binding.tvAddressIn.setText(address_in);
                                        String DisName = response.body().getData().get(0).getDistrictName();
                                        binding.txtDis.setText(DisName);

  /*   try {
                                            String decodedAddressIn = decodeUtf8Hex(address_in);
                                            Log.d("tvAddress"," "+decodedAddressIn);
                                            binding.tvAddressIn.setText(decodedAddressIn);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            binding.tvAddressIn.setText(address_in);
                                        }*/

                                        Log.d("tvAddressIn", " " + address_in);
                                        //   Log.d("tvAddressIn", " " + response.body().getList().get(0).getAddress_In());
                                        //   Typeface typeface = ResourcesCompat.getFont(mcontext, R.font.roboto_medium);
                                        //   binding.tvAddressIn.setTypeface(typeface);


                                        String address_out = response.body().getData().get(0).getAddress_Out();
                                        Log.d("tvAddressout", " " + address_out);

                                        binding.tvAddressOut.setText(address_out);






                                    /*    try {
                                            String decodedAddressout = decodeUtf8Hex(address_out);
                                            Log.d("tvAddress", " " + decodedAddressout);

                                            //  decodeFromUTF8(address_out);
                                            binding.tvAddressOut.setText(decodedAddressout);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            binding.tvAddressOut.setText(address_out);
                                        }*/

                                        //   binding.tvAddressOut.setTypeface(typeface);


                                        String inImg = response.body().getData().get(0).getInImagePath();
                                        binding.txtAttendanceStatus.setText(response.body().getData().get(0).getAttendanceStatus());
                                        binding.txtRemark.setText(response.body().getData().get(0).getRemark());
                                        if (inImg == null) {
                                            tv_img_one.setText("No Attendance");
                                        }
                                        Glide.with(mActivity)
                                                .load(BaseUrl() + inImg)
                                                .placeholder(R.drawable.image_not_fount)
                                                .into(binding.ImageIn);

                                        binding.ImageIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", BaseUrl() + inImg));

                                            }
                                        });


                                        String outImg = response.body().getData().get(0).getOutImagePath();
                                        if (outImg == null) {
                                            tv_img_two.setText("No Attendance");
                                        }

                                        Glide.with(mActivity)
                                                .load(BaseUrl() + outImg)
                                                .placeholder(R.drawable.image_not_fount)
                                                .into(binding.ImgOut);


                                        binding.ImgOut.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                startActivity(new Intent(mActivity, ZoomInZoomOutActivity.class).putExtra("image", BaseUrl() + outImg));

                                            }
                                        });


                                        //   Log.d("jghj", outImg);


                                    } else {
                                        Toast.makeText(AttendanceViewDetail.this, "No Data Available", Toast.LENGTH_SHORT).show();
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
                public void onFailure(@NonNull Call<ModelAttendanceListing> call, @NonNull Throwable error) {
                    Utils.hideCustomProgressDialogCommonForAll();
                    makeToast(getResources().getString(R.string.error));
                    call.cancel();
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show();
    }




  /*  public static String decodeUtf8Hex(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }

        byte[] bytes = hexStringToByteArray(encoded.replace("\\x", ""));

        // Convert the byte array to a UTF-8 encoded string
        return new String(bytes, StandardCharsets.UTF_8);





    }

    private static byte[] hexStringToByteArray(String s) {
     int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;


    }*/

    public static String decodeUtf8Hex(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return "";
        }

        // Remove \x from the string and ensure length is even
        String sanitizedEncoded = encoded.replace("\\x", "").toUpperCase();

        if (sanitizedEncoded.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string has an odd length, which is invalid.");
        }

        try {
            // Convert hex string to byte array
            byte[] bytes = hexStringToByteArray(sanitizedEncoded);
            // Convert byte array to UTF-8 string
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to decode hex string: " + e.getMessage());
            throw e; // Rethrow to handle elsewhere if needed
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(s.charAt(i), 16);
            int low = Character.digit(s.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid hex character at position " + i);
            }
            data[i / 2] = (byte) ((high << 4) + low);
        }
        return data;
    }


}
