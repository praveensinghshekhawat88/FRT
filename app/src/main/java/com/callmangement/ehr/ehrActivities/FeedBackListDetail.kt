package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.databinding.ActivityFeedbackListdetailedBinding
import com.callmangement.ehr.models.FeedbackbyDCListDatum
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImageInstalledAdapterIris
import com.callmangement.utils.PrefManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class FeedBackListDetail : FragmentActivity(), View.OnClickListener, OnMapReadyCallback {
    private var binding: ActivityFeedbackListdetailedBinding? = null

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
        binding = ActivityFeedbackListdetailedBinding.inflate(
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
            resources.getString(R.string.feedbacklist_detail)

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

        // startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+model.getFilePath()));
        // }
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val location = LatLng(latitude, longitude)
        mMap!!.addMarker(MarkerOptions().position(location).title(model!!.locationAddress))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}

