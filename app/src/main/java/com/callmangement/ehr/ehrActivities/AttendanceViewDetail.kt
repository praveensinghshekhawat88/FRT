package com.callmangement.ehr.ehrActivities

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.databinding.ActivityViewAttandanceDetailBinding
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.AttendanceDetailsInfo
import com.callmangement.ehr.models.ModelAttendanceListing
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.Objects

class AttendanceViewDetail : BaseActivity() {
    var mActivity: Activity? = null
    var mcontext: Context? = null
    private var binding: ActivityViewAttandanceDetailBinding? = null
    var preference: PrefManager? = null
    var USER_Id: String? = null
    var formattedDate: String? = null
    var districtId: String? = null
    private var attendanceDetailsInfoList: List<AttendanceDetailsInfo> = ArrayList()
    val LAUNCH_MARK_ATTENDANCE_ACTIVITY: Int = 333
    private val permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    var tv_img_one: AppCompatTextView? = null
    var tv_img_two: AppCompatTextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityViewAttandanceDetailBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        val intent = intent
        val bundle = getIntent().extras
        USER_Id = intent.extras!!.getString("YouruserIdId")
        formattedDate = intent.extras!!.getString("formattedDate")
        districtId = intent.extras!!.getString("districtId")
        //   Log.e("useid", USER_Id);
        // Log.e("districtId", districtId);
        mActivity = this
        mcontext = this
        preference = PrefManager(mcontext!!)
        permissions.add(permission.ACCESS_FINE_LOCATION)
        permissions.add(permission.ACCESS_COARSE_LOCATION)
        attendanceDetailsInfoList = ArrayList()
        //USER_Id = preference.getString(Preference.USER_ID);
        getAttendanceList(USER_Id, formattedDate, formattedDate)
        binding!!.btnBack.setOnClickListener {
            onBackPressed()
            /*  Intent i = new Intent(mActivity,AttendanceListingActivity.class);
            startActivity(i);
    */
        }

        tv_img_one = findViewById(R.id.tv_img_one)
        tv_img_two = findViewById(R.id.tv_img_two)
    }


    private fun getAttendanceList(USER_Id: String?, StartDate: String?, EndDate: String?) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )
            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )
            val paramStr = GetAttendanceListQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = USER_Id
            paramData[splitArray[1]] = "0"
            paramData[splitArray[2]] = StartDate
            paramData[splitArray[3]] = EndDate
            val call = apiInterface.callAttendanceListApi(GetAttendanceList(), paramData)
            call!!.enqueue(object : Callback<ModelAttendanceListing?> {
                override fun onResponse(
                    call: Call<ModelAttendanceListing?>,
                    response: Response<ModelAttendanceListing?>
                ) {
                    hideCustomProgressDialogCommonForAll()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    if (response.body()!!.data.size != 0) {
                                        Log.d("alldata", "" + response.body()!!.data)
                                        val username = response.body()!!.data[0].username
                                        binding!!.tvUserName.text = username
                                        val date = response.body()!!.data[0].punchInDate
                                        binding!!.tvPunchInDate.text = date
                                        val day = response.body()!!.data[0].dayName
                                        binding!!.tvDay.text = day
                                        val punchintime = response.body()!!.data[0].punchInTime
                                        binding!!.tvPunchInTime.text = punchintime
                                        val punchouttim = response.body()!!.data[0].punchOutTime
                                        binding!!.tvPunchOutTime.text = punchouttim
                                        val address_in = response.body()!!.data[0].address_In
                                        binding!!.tvAddressIn.text = address_in
                                        val DisName = response.body()!!.data[0].districtName
                                        binding!!.txtDis.text = DisName

                                        /*   try {
                                            String decodedAddressIn = decodeUtf8Hex(address_in);
                                            Log.d("tvAddress"," "+decodedAddressIn);
                                            binding.tvAddressIn.setText(decodedAddressIn);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            binding.tvAddressIn.setText(address_in);
                                        }*/
                                        Log.d("tvAddressIn", " $address_in")


                                        //   Log.d("tvAddressIn", " " + response.body().getList().get(0).getAddress_In());
                                        //   Typeface typeface = ResourcesCompat.getFont(mContext!!, R.font.roboto_medium);
                                        //   binding.tvAddressIn.setTypeface(typeface);
                                        val address_out = response.body()!!.data[0].address_Out
                                        Log.d("tvAddressout", " $address_out")

                                        binding!!.tvAddressOut.text = address_out


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
                                        val inImg = response.body()!!.data[0].inImagePath
                                        binding!!.txtAttendanceStatus.text =
                                            response.body()!!.data[0].attendanceStatus
                                        binding!!.txtRemark.text =
                                            response.body()!!.data[0].remark
                                        if (inImg == null) {
                                            tv_img_one!!.text = "No Attendance"
                                        }
                                        Glide.with(mActivity!!)
                                            .load(BaseUrl() + inImg)
                                            .placeholder(R.drawable.image_not_fount)
                                            .into(binding!!.ImageIn)

                                        binding!!.ImageIn.setOnClickListener {
                                            startActivity(
                                                Intent(
                                                    mActivity,
                                                    ZoomInZoomOutActivity::class.java
                                                ).putExtra(
                                                    "image",
                                                    BaseUrl() + inImg
                                                )
                                            )
                                        }


                                        val outImg = response.body()!!.data[0].outImagePath
                                        if (outImg == null) {
                                            tv_img_two!!.text = "No Attendance"
                                        }

                                        Glide.with(mActivity!!)
                                            .load(BaseUrl() + outImg)
                                            .placeholder(R.drawable.image_not_fount)
                                            .into(binding!!.ImgOut)


                                        binding!!.ImgOut.setOnClickListener {
                                            startActivity(
                                                Intent(
                                                    mActivity,
                                                    ZoomInZoomOutActivity::class.java
                                                ).putExtra(
                                                    "image",
                                                    BaseUrl() + outImg
                                                )
                                            )
                                        }


                                        //   Log.d("jghj", outImg);
                                    } else {
                                        Toast.makeText(
                                            this@AttendanceViewDetail,
                                            "No Data Available",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelAttendanceListing?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()
                    makeToast(resources.getString(R.string.error))
                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val ALL_PERMISSIONS_RESULT = 101

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
        fun decodeUtf8Hex(encoded: String?): String {
            if (encoded == null || encoded.isEmpty()) {
                return ""
            }

            // Remove \x from the string and ensure length is even
            val sanitizedEncoded = encoded.replace("\\x", "").uppercase(Locale.getDefault())

            require(sanitizedEncoded.length % 2 == 0) { "Hex string has an odd length, which is invalid." }

            try {
                // Convert hex string to byte array
                val bytes = hexStringToByteArray(sanitizedEncoded)
                // Convert byte array to UTF-8 string
                return String(bytes, StandardCharsets.UTF_8)
            } catch (e: IllegalArgumentException) {
                System.err.println("Failed to decode hex string: " + e.message)
                throw e // Rethrow to handle elsewhere if needed
            }
        }

        private fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                val high = s[i].digitToIntOrNull(16) ?: -1
                val low = s[i + 1].digitToIntOrNull(16) ?: -1
                require(!(high == -1 || low == -1)) { "Invalid hex character at position $i" }
                data[i / 2] = ((high shl 4) + low).toByte()
                i += 2
            }
            return data
        }
    }
}
