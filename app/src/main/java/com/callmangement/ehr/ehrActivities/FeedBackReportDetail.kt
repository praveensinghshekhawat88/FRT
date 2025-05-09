package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.databinding.ActivityFeedbackReportdetailedBinding
import com.callmangement.ehr.models.FeedbackbyDCListDatum
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImageInstalledAdapterIris
import com.callmangement.utils.PrefManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class FeedBackReportDetail : FragmentActivity(), View.OnClickListener,
    OnMapReadyCallback {

    private var binding: ActivityFeedbackReportdetailedBinding? = null
    //private ModelExpensesList model;
    private var model: FeedbackbyDCListDatum? = null
    private var preference: PrefManager? = null
    private val playerNames = ArrayList<String>()
    private var selectedValue: String? = null
    private var FPS_CODE: String? = null
    private var Input_Device_Code: String? = null
    private var Input_Remark: String? = null
    private var stringArrayListHavingAllFilePath: ArrayList<String> = ArrayList()
    private val REQUEST_PICK_IMAGE_ONE: Int = 1111
    private val REQUEST_PICK_IMAGE_TWO: Int = 1112
    private val REQUEST_PICK_IMAGE_THREE: Int = 1113
    private val adapter: ViewImageInstalledAdapterIris? = null
    private val adapterSec: ViewImageInstalledAdapterIris? = null
    private var mActivity: Activity? = null
    private var mContext: Context? = null
    private var mMap: GoogleMap? = null
    private var latitude = 0.0 // Replace with your latitude
    private var longitude = 0.0 // Replace with your longitude

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        //    getSupportActionBar().hide(); // hide the title bar
        binding = ActivityFeedbackReportdetailedBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        initView()
    }

    private fun initView() {
        mActivity = this
        mContext = this
        preference = PrefManager(mContext!!)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.feedbackreport_detail)
        intentData
        setUpOnClickListener()
        setUpData()
        //  GetErrorImages();
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { finish() }
        //  binding.ivChallanImage.setOnClickListener(this);
    }


    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("paramm") as FeedbackbyDCListDatum?
        }

    private fun setUpData() {
        binding!!.inputFpsCode.text = model!!.fpscode
        binding!!.inputdis.text = model!!.districtName
        binding!!.inputDealerName.text = model!!.feedBackBy
        binding!!.inputday.text = model!!.updatedOn.dayOfWeek

        val remark = model!!.remarks.toString()
        if (remark == null || remark.isEmpty()) {
            binding!!.inputRemark.text = " "
        } else {
            binding!!.inputRemark.text = remark
        }

        binding!!.inputfeedbackid.text = model!!.feedBackId.toString()
        val date =
            model!!.updatedOn.dayOfMonth.toString() + "-" + model!!.updatedOn.monthValue + "-" + model!!.updatedOn.year
        binding!!.inputdelivereddate.text = date
        //  String time = model.getUpdatedOn().getHour()+":"+model.getUpdatedOn().getMinute()+":"+model.getUpdatedOn().getSecond();
        binding!!.inputtime.text = model!!.logInTime
        binding!!.inputouttime.text = model!!.logOutTime
        binding!!.inputaddress.text = model!!.locationAddress

        //   ArrayList<String> myvalue = new ArrayList<String>();
        //   myvalue.add(model.getErrorType().toString());
        /* ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ErrorposDetail.this, simple_spinner_item, myvalue);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        binding.spinner.setAdapter(spinnerArrayAdapter);*/
        //   binding.spinner.setText(model.getErrorType());
        //  Log.d("getDealerMobileNo", "jfdl" + model.getDealerMobileNo());
        //    binding.inputRemark.setText(model.getRemark());
        //  binding.inputRemark.setText(model.getRemark());
        //    binding.inputDeviceCode.setText(String.valueOf(model.getDeviceCode()));
        //  binding.inputRemark.setText(model.getRemark());

        /*  if (model.getCompletedOnStr().isEmpty()){
            binding.expenseCompletedDateLay.setVisibility(View.GONE);
        } else {
            binding.expenseCompletedDateLay.setVisibility(View.VISIBLE);
            binding.inputExpenseCompletedDate.setText(model.getCompletedOnStr());
        }

        if (model.getExpenseStatusID() == 2) {
            binding.buttonComplete.setVisibility(View.GONE);
        }*/
        val shopimg = model!!.selfyWithFPSShopImage


        if (shopimg == null) {
            binding!!.textNoDataFound.text = "No Image"
        }
        Glide.with(mActivity!!)
            .load(shopimg)
            .placeholder(R.drawable.image_not_fount)
            .into(binding!!.ivPartsImage1)
        binding!!.textNoDataFound.text = "Dealer's Selfi"

        binding!!.ivPartsImage1.setOnClickListener {
            startActivity(
                Intent(
                    mActivity,
                    ZoomInZoomOutActivity::class.java
                ).putExtra("image", shopimg)
            )
        }


        val sigimg = model!!.dealerSignatureImage
        latitude = model!!.latitude.toDouble()
        longitude = model!!.longitude.toDouble()




        if (sigimg == null) {
            binding!!.textNoDataFoundSec.text = "No Image"
        }
        Glide.with(mActivity!!)
            .load(sigimg)
            .placeholder(R.drawable.image_not_fount)
            .into(binding!!.ivPartsImage2)
        binding!!.textNoDataFoundSec.text = "Dealer Signature"


        binding!!.ivPartsImage2.setOnClickListener {
            startActivity(
                Intent(
                    mActivity,
                    ZoomInZoomOutActivity::class.java
                ).putExtra("image", sigimg)
            )
        }


        val dcvstimg = model!!.dcVisitingChallan


        if (dcvstimg == null) {
            binding!!.textNoDataFound3.text = "No Image"
        }
        Glide.with(mActivity!!)
            .load(dcvstimg)
            .placeholder(R.drawable.image_not_fount)
            .into(binding!!.ivPartsImage3)
        binding!!.textNoDataFound3.text = "Challan Image"

        binding!!.ivPartsImage3.setOnClickListener {
            startActivity(
                Intent(
                    mActivity,
                    ZoomInZoomOutActivity::class.java
                ).putExtra("image", dcvstimg)
            )
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } //else if (id == R.id.your_button){

        // startActivity(new Intent(mContext!!, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        // }
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        /*  mMap = googleMap;
               LatLng location = new LatLng(latitude, longitude);
               mMap.addMarker(new MarkerOptions().position(location).title(model.getLocationAddress()));
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));*/

        mMap = googleMap
        // Add three points (latitude and longitude)
        val point1 = LatLng(26.9124, 75.7873) // Example: Jaipur
        val point2 = LatLng(26.9248, 75.8243) // Example: Delhi
        val point3 = LatLng(26.9361, 75.8108) // Example: Agra

        // Mark these points on the map
        mMap!!.addMarker(MarkerOptions().position(point1).title("Point 1"))
        mMap!!.addMarker(MarkerOptions().position(point2).title("Point 2"))
        mMap!!.addMarker(MarkerOptions().position(point3).title("Point 3"))

        // Move the camera to one of the points
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(point1, 15f))

        // Draw a line between the points
        val polyline = mMap!!.addPolyline(
            PolylineOptions()
                .add(point1, point2, point3) // LatLng points
                .width(5f) // Line width
                .color(Color.RED)
        ) // Line color
    }
}

