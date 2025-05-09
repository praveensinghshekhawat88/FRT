package com.callmangement.ui.complaint

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.custom_spinner_with_checkbox.MutliSelectDistributionPartsAdapter
import com.callmangement.databinding.ActivityChallanUploadBinding
import com.callmangement.databinding.CustomMultiSelectBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.model.ModelResponse
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.complaints.ModelResolveComplaint
import com.callmangement.model.inventrory.ModelParts
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.support.ImageUtilsForRotate
import com.callmangement.support.dexter.Dexter
import com.callmangement.support.dexter.MultiplePermissionsReport
import com.callmangement.support.dexter.PermissionToken
import com.callmangement.support.dexter.listener.PermissionRequest
import com.callmangement.support.dexter.listener.multi.MultiplePermissionsListener
import com.callmangement.support.slidetoact.SlideToActView
import com.callmangement.support.slidetoact.SlideToActView.OnSlideCompleteListener
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.ui.inventory.InventoryViewModel
import com.callmangement.utils.CompressImage
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Objects

class ChallanUploadActivity : CustomActivity(), View.OnClickListener {

    
    private lateinit var mActivity: Activity
    val REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT: Int = 1113
    val REQUEST_PICK_IMAGES_DSO_LETTER: Int = 1114
    private val myCalendarResolvedDate: Calendar = Calendar.getInstance()
    var TAG: String = "Cancel"
    var alreadySelectedParts: List<String> = ArrayList()
    lateinit var permissions: Array<String>
    private var binding: ActivityChallanUploadBinding? = null
    private var model: ModelComplaintList? = null
    private var prefManager: PrefManager? = null
    private var viewModel: ComplaintViewModel? = null
    private var inventoryViewModel: InventoryViewModel? = null
    private var remark = ""
    private var replacePart = ""
    private var courierDetail = ""
    private var challanNumber = ""
    private var resolvedDate = ""
    private var param: String? = null
    private var imageStoragePath = ""
    private var dsoletterStoragePath = ""

    //    private String dsoletterImageStoragePath = "";
    private var DSO_LETTER_TYPE = ""
    private var permissionGranted = false
    private var listParts: List<ModelPartsList> = ArrayList()
    private var replacePartsIds = ""
    private var dateResolved: OnDateSetListener? = null
    private var dialogReplaceParts: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_challan_upload)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.challan_upload)

        mContext = this
        mActivity = this
        prefManager = PrefManager(mContext!!)
        model = intent.getSerializableExtra("param") as ModelComplaintList?
        param = intent.getStringExtra("param2")
        binding!!.setData(model)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        inventoryViewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )
        initView()
    }

    private fun initView() {
        checkPermission()
        setUpOnClickListener()
        setUpData()
        sE_AvlStockPartsList

        binding!!.inputChallanNumber.requestFocus()

        dialogReplaceParts = Dialog(this)
    }

    private fun setUpOnClickListener() {
        binding!!.seImage.setOnClickListener(this)
        binding!!.inputResolvedDate.setOnClickListener(this)
        binding!!.buttonResolved.setOnClickListener(this)
        binding!!.btnChallanUpload.setOnClickListener(this)
        binding!!.inputImage.setOnClickListener(this)
        binding!!.inputDSOLetter.setOnClickListener(this)
        binding!!.inputReplacePart.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonSliderSendToSE.onSlideCompleteListener =
            OnSlideCompleteListener { slideToActView: SlideToActView? -> onClickSendToSEComplaint() }
        binding!!.buttonAcceptComplaint.setOnClickListener(this)


        //        binding.chkBoxIsPhysicalDamage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (binding.chkBoxIsPhysicalDamage.isChecked()) {
//                    binding.inputDSOLetterHint.setVisibility(View.VISIBLE);
//                    binding.inputDSOLetter.setVisibility(View.VISIBLE);
//                } else {
//                    binding.inputDSOLetterHint.setVisibility(View.GONE);
//                    binding.inputDSOLetter.setVisibility(View.GONE);
//                }
//            }
//        });
    }

    private fun setUpData() {
        dateResolved =
            OnDateSetListener { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                myCalendarResolvedDate[Calendar.YEAR] =
                    year
                myCalendarResolvedDate[Calendar.MONTH] = monthOfYear
                myCalendarResolvedDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                timePicker()
            }


        //        if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) {
//            if (param.equalsIgnoreCase("pending")) {
//                binding.chkBoxIsPhysicalDamage.setVisibility(View.VISIBLE);
//                binding.inputRemark.setClickable(true);
//                binding.inputReplacePart.setEnabled(true);
//                binding.inputCourierDetail.setEnabled(true);
//                binding.inputChallanNumber.setEnabled(true);
//                binding.inputResolvedDate.setEnabled(true);
//                binding.inputImageHint.setVisibility(View.VISIBLE);
//                binding.inputImage.setVisibility(View.VISIBLE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//                binding.chkBoxIsPhysicalDamage.setChecked(true);
//
//                if (binding.chkBoxIsPhysicalDamage.isChecked()) {
//                    binding.inputDSOLetterHint.setVisibility(View.VISIBLE);
//                    binding.inputDSOLetter.setVisibility(View.VISIBLE);
//                } else {
//                    binding.inputDSOLetterHint.setVisibility(View.GONE);
//                    binding.inputDSOLetter.setVisibility(View.GONE);
//                }
//
//
//                if (model.getComplainStatusId() != null) {
//                    if (model.getComplainStatusId().equals("5")) {
//                        binding.chkBoxIsPhysicalDamage.setClickable(false);
//                        binding.inputRemark.setEnabled(false);
//                        binding.inputReplacePart.setEnabled(false);
//                        binding.inputCourierDetail.setEnabled(false);
//                        binding.inputChallanNumber.setEnabled(false);
//                        binding.inputResolvedDate.setEnabled(false);
//                        binding.inputImageHint.setVisibility(View.GONE);
//                        binding.inputImage.setVisibility(View.GONE);
//                        binding.inputDSOLetterHint.setVisibility(View.GONE);
//                        binding.inputDSOLetter.setVisibility(View.GONE);
//                        binding.seImage.setVisibility(View.VISIBLE);
//                        binding.buttonAcceptComplaint.setVisibility(View.VISIBLE);
//                        binding.buttonResolved.setVisibility(View.GONE);
//                        binding.buttonSliderSendToSE.setVisibility(View.GONE);
//
//                        if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                            binding.seImage.setVisibility(View.VISIBLE);
//                            Glide.with(mContext!!)
//                                    .load(model.getImagePath())
//                                    .placeholder(R.drawable.image_not_fount)
//                                    .into(binding.seImage);
//                        } else {
//                            binding.seImage.setVisibility(View.GONE);
//                        }
//
//                    } else {
//                        binding.buttonAcceptComplaint.setVisibility(View.GONE);
//                        binding.buttonResolved.setVisibility(View.VISIBLE);
//                        binding.buttonSliderSendToSE.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    binding.buttonAcceptComplaint.setVisibility(View.GONE);
//                    binding.buttonResolved.setVisibility(View.VISIBLE);
//                    binding.buttonSliderSendToSE.setVisibility(View.VISIBLE);
//                }
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getIsSentToServiceCentre().equals("true")) {
//                    if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                        binding.layoutReplacePartsByServiceCenter.setVisibility(View.VISIBLE);
//                        binding.inputReplacePartByServiceCenter.setText(model.getReplacedPartsDetail());
//                    } else {
//                        binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    }
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//            } else if (param.equalsIgnoreCase("resolved")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("service_center")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")) {
//            if (param.equalsIgnoreCase("pending")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.GONE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("resolved")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("service_center")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) {
//            if (param.equalsIgnoreCase("pending")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//
//
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.GONE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("resolved")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("service_center")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("5") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceCentre")) {
//            if (param.equalsIgnoreCase("pending")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.seImage.setVisibility(View.GONE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("resolved")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("service_center")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) {
//            if (param.equalsIgnoreCase("pending")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.GONE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("resolved")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//
//            } else if (param.equalsIgnoreCase("service_center")) {
//                binding.chkBoxIsPhysicalDamage.setClickable(false);
//                binding.inputRemark.setEnabled(false);
//                binding.inputReplacePart.setEnabled(false);
//                binding.inputCourierDetail.setEnabled(false);
//                binding.inputChallanNumber.setEnabled(false);
//                binding.inputResolvedDate.setEnabled(false);
//                binding.inputImageHint.setVisibility(View.GONE);
//                binding.inputImage.setVisibility(View.GONE);
//                binding.inputDSOLetterHint.setVisibility(View.GONE);
//                binding.inputDSOLetter.setVisibility(View.GONE);
//                binding.seImage.setVisibility(View.VISIBLE);
//                binding.buttonResolved.setVisibility(View.GONE);
//                binding.buttonSliderSendToSE.setVisibility(View.GONE);
//                binding.buttonAcceptComplaint.setVisibility(View.GONE);
//
//                binding.chkBoxIsPhysicalDamage.setChecked(model.getPhysicalDamage() != null && model.getPhysicalDamage());
//
//                if (model.getSeremark() != null && !model.getSeremark().equalsIgnoreCase("null")) {
//                    binding.inputRemark.setText(model.getSeremark());
//                } else {
//                    binding.inputRemark.setText("");
//                }
//
//                if (model.getReplacedPartsDetail() != null && !model.getReplacedPartsDetail().equalsIgnoreCase("null")) {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText(model.getReplacedPartsDetail());
//                } else {
//                    binding.layoutReplacePartsByServiceCenter.setVisibility(View.GONE);
//                    binding.inputReplacePart.setText("");
//                }
//
//                if (model.getCourierServicesDetail() != null && !model.getCourierServicesDetail().equalsIgnoreCase("null")) {
//                    binding.inputCourierDetail.setText(model.getCourierServicesDetail());
//                } else {
//                    binding.inputCourierDetail.setText("");
//                }
//
//                if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
//                    binding.inputResolvedDate.setText(model.getSermarkDateStr());
//                } else {
//                    binding.inputResolvedDate.setText("");
//                }
//
//                if (model.getChallanNo() != null && !model.getChallanNo().equalsIgnoreCase("null")) {
//                    binding.inputChallanNumber.setText(model.getChallanNo());
//                } else {
//                    binding.inputChallanNumber.setText("");
//                }
//
//                if (model.getImagePath() != null && !model.getImagePath().isEmpty() && !model.getImagePath().equalsIgnoreCase("null")) {
//                    binding.seImage.setVisibility(View.VISIBLE);
//                    Glide.with(mContext!!)
//                            .load(model.getImagePath())
//                            .placeholder(R.drawable.image_not_fount)
//                            .into(binding.seImage);
//                } else {
//                    binding.seImage.setVisibility(View.GONE);
//                }
//            }
//        }
        binding!!.btnChallanUpload.visibility = View.GONE

        if (prefManager!!.useR_TYPE_ID.equals(
                "4",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("ServiceEngineer", ignoreCase = true)
        ) {
            binding!!.btnChallanUpload.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) {
            binding!!.btnChallanUpload.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.btnChallanUpload.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID == "5" && prefManager!!.useR_TYPE.equals(
                "ServiceCentre",
                ignoreCase = true
            )
        ) {
            binding!!.btnChallanUpload.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.btnChallanUpload.visibility = View.VISIBLE
        }
    }

    private fun timePicker() {
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mCurrentTime[Calendar.MINUTE]
        val second = mCurrentTime[Calendar.SECOND]
        val mTimePicker = TimePickerDialog(
            this,
            { timePicker: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                @SuppressLint("DefaultLocale") val timeStr =
                    String.format("%02d:%02d:%02d", selectedHour, selectedMinute, second)
                updateLabelResolvedDateTime(timeStr)
            },
            hour,
            minute,
            false
        )
        mTimePicker.setTitle(resources.getString(R.string.select_time))
        mTimePicker.show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateLabelResolvedDateTime(timeStr: String) {
        binding!!.inputResolvedDate.text!!.clear()
        val myFormat = "yyyy-MM-dd"
        val myFormatSelectedDate = "dd-MM-yyyy"

        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val sdfSelectedDate = SimpleDateFormat(myFormatSelectedDate, Locale.US)

        val separator =
            model!!.complainRegDateStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val regDateSep = separator[0]
        val regTimeSep = separator[1]
        val selectedDate = sdfSelectedDate.format(myCalendarResolvedDate.time)

        try {
            @SuppressLint("SimpleDateFormat") val sdfTime = SimpleDateFormat("HH:mm:ss")
            val regTime = sdfTime.parse(regTimeSep)
            val selectedTime = sdfTime.parse(timeStr)
            if (regDateSep == selectedDate) {
                if (Objects.requireNonNull(selectedTime).before(regTime)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(resources.getString(R.string.please_select_time_after_registered_complaint_time))
                        .setCancelable(false)
                        .setPositiveButton(
                            resources.getString(R.string.ok)
                        ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                    val alert = builder.create()
                    alert.setTitle(resources.getString(R.string.alert))
                    alert.show()
                } else {
                    binding!!.inputResolvedDate.setText(sdf.format(myCalendarResolvedDate.time) + " " + timeStr)
                }
            } else {
                binding!!.inputResolvedDate.setText(sdf.format(myCalendarResolvedDate.time) + " " + timeStr)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onClickSendToSEComplaint() {
        remark = Objects.requireNonNull(binding!!.inputRemark.text).toString().trim { it <= ' ' }
        replacePart =
            Objects.requireNonNull(binding!!.inputReplacePart.text).toString().trim { it <= ' ' }
        courierDetail =
            Objects.requireNonNull(binding!!.inputCourierDetail.text).toString().trim { it <= ' ' }
        challanNumber =
            Objects.requireNonNull(binding!!.inputChallanNumber.text).toString().trim { it <= ' ' }
        resolvedDate =
            Objects.requireNonNull(binding!!.inputResolvedDate.text).toString().trim { it <= ' ' }

        /*replacePartsIds = "";
        replacePart = "";*/
        if (challanNumber.isEmpty()) {
            binding!!.buttonSliderSendToSE.outerColor = Color.RED
            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
            Handler(Looper.myLooper()!!).postDelayed({
                binding!!.buttonSliderSendToSE.outerColor =
                    resources.getColor(R.color.holo_blue_dark)
                binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                binding!!.buttonSliderSendToSE.resetSlider()
            }, 2000)
            makeToast(resources.getString(R.string.please_input_challan_number))
        } else if (resolvedDate.isEmpty()) {
            binding!!.buttonSliderSendToSE.outerColor = Color.RED
            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
            Handler(Looper.myLooper()!!).postDelayed({
                binding!!.buttonSliderSendToSE.outerColor =
                    resources.getColor(R.color.holo_blue_dark)
                binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                binding!!.buttonSliderSendToSE.resetSlider()
            }, 2000)
            makeToast(resources.getString(R.string.please_select_resolved_date))
        } else if (remark.isEmpty()) {
            binding!!.buttonSliderSendToSE.outerColor = Color.RED
            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
            Handler(Looper.myLooper()!!).postDelayed({
                binding!!.buttonSliderSendToSE.outerColor =
                    resources.getColor(R.color.holo_blue_dark)
                binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                binding!!.buttonSliderSendToSE.resetSlider()
            }, 2000)
            makeToast(resources.getString(R.string.please_input_remark))
        } /*else if (replacePart.isEmpty()){
                binding.buttonSliderSendToSE.setOuterColor(Color.RED);
                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_clear);
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
                    binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
                    binding.buttonSliderSendToSE.resetSlider();
                },2000);
                makeToast(getResources().getString(R.string.please_input_replace_part));

            }*/ else if (courierDetail.isEmpty()) {
            binding!!.buttonSliderSendToSE.outerColor = Color.RED
            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
            Handler(Looper.myLooper()!!).postDelayed({
                binding!!.buttonSliderSendToSE.outerColor =
                    resources.getColor(R.color.holo_blue_dark)
                binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                binding!!.buttonSliderSendToSE.resetSlider()
            }, 2000)
            makeToast(resources.getString(R.string.please_input_courier_detail))
        } else if (imageStoragePath.isEmpty()) {
            binding!!.buttonSliderSendToSE.outerColor = Color.RED
            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
            Handler(Looper.myLooper()!!).postDelayed({
                binding!!.buttonSliderSendToSE.outerColor =
                    resources.getColor(R.color.holo_blue_dark)
                binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                binding!!.buttonSliderSendToSE.resetSlider()
            }, 2000)
            makeToast(resources.getString(R.string.please_select_image))
        } else if (binding!!.inputDSOLetter.visibility == View.VISIBLE && dsoletterStoragePath.isEmpty()) {
            binding!!.buttonSliderSendToSE.outerColor = Color.RED
            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
            Handler(Looper.myLooper()!!).postDelayed({
                binding!!.buttonSliderSendToSE.outerColor =
                    resources.getColor(R.color.holo_blue_dark)
                binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                binding!!.buttonSliderSendToSE.resetSlider()
            }, 2000)
            makeToast(resources.getString(R.string.please_upload_dso_letter))
        } else {
            val builder = AlertDialog.Builder(mContext!!)
            builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_send_to_service_center_this_complaint))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    var dsoletterStoragePathStr = ""
                    if (binding!!.inputDSOLetter.visibility == View.VISIBLE && !dsoletterStoragePath.isEmpty()) {
                        dsoletterStoragePathStr = dsoletterStoragePath
                    }
                    resolveComplaint(
                        convertStringToUTF8(remark),
                        if (binding!!.chkBoxIsPhysicalDamage.isChecked) "1" else "0",
                        "SendToSECenter",
                        convertStringToUTF8(replacePart),
                        convertStringToUTF8(courierDetail),
                        imageStoragePath,
                        convertStringToUTF8(challanNumber),
                        resolvedDate,
                        replacePartsIds,
                        dsoletterStoragePathStr,
                        DSO_LETTER_TYPE
                    )
                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    binding!!.buttonSliderSendToSE.outerColor = Color.RED
                    binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
                    Handler(Looper.myLooper()!!).postDelayed({
                        binding!!.buttonSliderSendToSE.outerColor =
                            resources.getColor(R.color.holo_blue_dark)
                        binding!!.buttonSliderSendToSE.resetSlider()
                    }, 2000)
                }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }

    fun onClickResolveComplaint() {
        remark = Objects.requireNonNull(binding!!.inputRemark.text).toString().trim { it <= ' ' }
        replacePart =
            Objects.requireNonNull(binding!!.inputReplacePart.text).toString().trim { it <= ' ' }
        courierDetail =
            Objects.requireNonNull(binding!!.inputCourierDetail.text).toString().trim { it <= ' ' }
        challanNumber =
            Objects.requireNonNull(binding!!.inputChallanNumber.text).toString().trim { it <= ' ' }
        resolvedDate =
            Objects.requireNonNull(binding!!.inputResolvedDate.text).toString().trim { it <= ' ' }

        if (challanNumber.isEmpty()) {
            makeToast(resources.getString(R.string.please_input_challan_number))
        } else if (resolvedDate.isEmpty()) {
            makeToast(resources.getString(R.string.please_select_resolved_date))
        } else if (remark.isEmpty()) {
            makeToast(resources.getString(R.string.please_input_remark))
        } /*else if (replacePart.isEmpty()){
            makeToast(getResources().getString(R.string.please_input_replace_part));
        }*/ else if (courierDetail.isEmpty()) {
            makeToast(resources.getString(R.string.please_input_courier_detail))
        } else if (imageStoragePath.isEmpty()) {
            makeToast(resources.getString(R.string.please_select_image))
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_resolve_this_complaint))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    var dsoletterStoragePathStr = ""
                    if (binding!!.inputDSOLetter.visibility == View.VISIBLE && !dsoletterStoragePath.isEmpty()) {
                        dsoletterStoragePathStr = dsoletterStoragePath
                    }
                    resolveComplaint(
                        convertStringToUTF8(remark),
                        if (binding!!.chkBoxIsPhysicalDamage.isChecked) "1" else "0",
                        "Resolved",
                        convertStringToUTF8(replacePart),
                        convertStringToUTF8(courierDetail),
                        imageStoragePath,
                        convertStringToUTF8(challanNumber),
                        resolvedDate,
                        replacePartsIds,
                        dsoletterStoragePathStr,
                        DSO_LETTER_TYPE
                    )
                }
                .setNegativeButton(
                    resources.getString(R.string.no)
                ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }

    private fun resolveComplaint(
        remark: String,
        IsPhysicalDamage: String,
        getStatus: String,
        replacePart: String,
        courierDetail: String,
        se_image: String,
        challanNo: String,
        seRemarkDate: String,
        replacePartsIds: String,
        damageApprovalLetter: String,
        DSO_LETTER_TYPE: String
    ) {
        isLoading
        viewModel!!.resolveComplaint(
            prefManager!!.useR_Id,
            IsPhysicalDamage,
            remark,
            model!!.complainRegNo,
            getStatus,
            replacePart,
            courierDetail,
            se_image,
            challanNo,
            seRemarkDate,
            replacePartsIds,
            damageApprovalLetter,
            DSO_LETTER_TYPE
        ).observe(
            this
        ) { modelResolveComplaint: ModelResolveComplaint? ->
            isLoading
            Log.d("replacepartsid", "  $replacePartsIds")
            if (modelResolveComplaint!!.status == "200") {
                val msg = modelResolveComplaint.message

                //                Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
//                binding.buttonSliderSendToSE.setOuterColor(getResources().getColor(R.color.holo_blue_dark));
//                binding.buttonSliderSendToSE.setCompleteIcon(R.drawable.ic_check);
//                binding.buttonSliderSendToSE.resetSlider();
//                Intent returnIntent = new Intent();
//                setResult(Activity.RESULT_OK, returnIntent);
//                finish();
                val builder = AlertDialog.Builder(this)
                builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                        binding!!.buttonSliderSendToSE.outerColor =
                            resources.getColor(R.color.holo_blue_dark)
                        binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                        binding!!.buttonSliderSendToSE.resetSlider()
                        val returnIntent = Intent()
                        setResult(RESULT_OK, returnIntent)
                        finish()
                    }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            } else {
                val msg = modelResolveComplaint.message
                val builder = AlertDialog.Builder(this)
                builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                        binding!!.buttonSliderSendToSE.outerColor = Color.RED
                        binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
                        Handler(Looper.myLooper()!!).postDelayed({
                            binding!!.buttonSliderSendToSE.outerColor =
                                resources.getColor(R.color.holo_blue_dark)
                            binding!!.buttonSliderSendToSE.resetSlider()
                        }, 2000)
                    }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            }
        }
    }

    fun onClickChallanUpload() {
        remark = Objects.requireNonNull(binding!!.inputRemark.text).toString().trim { it <= ' ' }
        replacePart =
            Objects.requireNonNull(binding!!.inputReplacePart.text).toString().trim { it <= ' ' }
        courierDetail =
            Objects.requireNonNull(binding!!.inputCourierDetail.text).toString().trim { it <= ' ' }
        challanNumber =
            Objects.requireNonNull(binding!!.inputChallanNumber.text).toString().trim { it <= ' ' }
        resolvedDate =
            Objects.requireNonNull(binding!!.inputResolvedDate.text).toString().trim { it <= ' ' }

        if (challanNumber.isEmpty()) {
            makeToast(resources.getString(R.string.please_input_challan_number))
            //        } else if (resolvedDate.isEmpty()) {
//            makeToast(getResources().getString(R.string.please_select_resolved_date));
//        } else if (remark.isEmpty()) {
//            makeToast(getResources().getString(R.string.please_input_remark));
//        }  else if (courierDetail.isEmpty()) {
//            makeToast(getResources().getString(R.string.please_input_courier_detail));
        } else if (imageStoragePath.isEmpty()) {
            makeToast(resources.getString(R.string.please_select_image))
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_upload_challan))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    //    resolveComplaint(convertStringToUTF8(remark), binding.chkBoxIsPhysicalDamage.isChecked() ? "1" : "0", "Resolved", convertStringToUTF8(replacePart), convertStringToUTF8(courierDetail), imageStoragePath, convertStringToUTF8(challanNumber), resolvedDate, replacePartsIds, dsoletterStoragePathStr, DSO_LETTER_TYPE);
                    challanUpload(convertStringToUTF8(challanNumber), imageStoragePath)
                }
                .setNegativeButton(
                    resources.getString(R.string.no)
                ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }

    private fun challanUpload(challanNo: String, challanImage: String) {
        isLoading
        viewModel!!.challanUpload(
            prefManager!!.useR_Id,
            model!!.complainId,
            model!!.complainRegNo,
            challanNo,
            challanImage
        )
            .observe(this) { modelResolveComplaint: ModelResolveComplaint? ->
                isLoading
                Log.d("replacepartsid", "  $replacePartsIds")
                if (modelResolveComplaint!!.status == "200") {
                    val msg = modelResolveComplaint.message

                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                            dialog.cancel()
                            binding!!.buttonSliderSendToSE.outerColor =
                                resources.getColor(R.color.holo_blue_dark)
                            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_check
                            binding!!.buttonSliderSendToSE.resetSlider()
                            val returnIntent = Intent()
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        }
                    val alert = builder.create()
                    alert.setTitle(resources.getString(R.string.alert))
                    alert.show()
                } else {
                    val msg = modelResolveComplaint.message
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, id: Int ->
                            dialog.cancel()
                            binding!!.buttonSliderSendToSE.outerColor = Color.RED
                            binding!!.buttonSliderSendToSE.completeIcon = R.drawable.ic_clear
                            Handler(Looper.myLooper()!!).postDelayed({
                                binding!!.buttonSliderSendToSE.outerColor =
                                    resources.getColor(R.color.holo_blue_dark)
                                binding!!.buttonSliderSendToSE.resetSlider()
                            }, 2000)
                        }
                    val alert = builder.create()
                    alert.setTitle(resources.getString(R.string.alert))
                    alert.show()
                }
            }
    }

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading.observe(
                this
            ) { aBoolean: Boolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    private val isInventoryLoading: Unit
        get() {
            inventoryViewModel!!.isLoading.observe(
                this
            ) { aBoolean: Boolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    private fun convertStringToUTF8(s: String): String {
        val out =
            String(s.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
        return out
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            permissionGranted = true
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            permissionGranted = true
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
    }


    private fun selectImage() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext!!)
            title.text = resources.getString(R.string.imagepicker_str_select_challan_image)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f
            val builder = AlertDialog.Builder(mContext!!)
            builder.setCustomTitle(title)
            // builder.setTitle("Add Photo!");
            builder.setItems(
                items
            ) { dialog: DialogInterface, item: Int ->
                if (items[item] == resources.getString(R.string.imagepicker_str_take_photo)) {
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
                        .setSelectedImages(ArrayList())
                        .start(REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_choose_from_gallery)) {
                    ImagePicker.with(mActivity)
                        .setToolbarColor("#212121")
                        .setStatusBarColor("#000000")
                        .setToolbarTextColor("#FFFFFF")
                        .setToolbarIconColor("#FFFFFF")
                        .setProgressBarColor("#4CAF50")
                        .setBackgroundColor("#212121")
                        .setCameraOnly(false)
                        .setMultipleMode(true)
                        .setFolderMode(true)
                        .setShowCamera(false)
                        .setFolderTitle("Albums")
                        .setImageTitle("Galleries")
                        .setDoneTitle("Done")
                        .setMaxSize(1)
                        .setSavePath(Constants.saveImagePath)
                        .setSelectedImages(ArrayList())
                        .start(REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("kdcfjcl", "" + e)
        }
    }

    private fun selectMultiCheckboxForReplacePart() {
        /*if (!model.getComplPartsIds().isEmpty()) {
                   String[] data = model.getComplPartsIds().split(",");
                   for (int i =0; i < data.length; i++){
                       for (int j =0; j < listParts.size(); j++){
                           if (listParts.get(j).getItemId().equalsIgnoreCase(data[i])){
                               listParts.remove(j);
                           }
                       }
                   }
               }*/

        if (!dialogReplaceParts!!.isShowing) {
            replacePartsIds = ""
            //    final Dialog dialog = new Dialog(this);
            val dialogBinding =
                CustomMultiSelectBinding.inflate(
                    layoutInflater
                )
            dialogReplaceParts!!.setContentView(dialogBinding.root)
            dialogReplaceParts!!.setTitle(resources.getString(R.string.select_replace_parts))
            dialogReplaceParts!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogReplaceParts!!.setCanceledOnTouchOutside(false)

            dialogBinding.recyclerViewParts.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            dialogBinding.recyclerViewParts.adapter =
                MutliSelectDistributionPartsAdapter(this, listParts)

            dialogBinding.done.setOnClickListener { view: View? ->
                replacePartsIds = getCommaSeparatedIds(listParts)
                Log.d("replacePartsIds", replacePartsIds)
                val partName = getCommaSeparatedName(listParts)
                binding!!.inputReplacePart.setText(partName)
                dialogReplaceParts!!.dismiss()
            }

            dialogBinding.cancel.setOnClickListener { view: View? ->
                for (i in listParts.indices) {
                    listParts[i].isSelectFlag = false
                }
                replacePartsIds = getCommaSeparatedIds(listParts)
                Log.d("replacePartsIds", replacePartsIds)
                binding!!.inputReplacePart.setText("")
                dialogReplaceParts!!.dismiss()
            }

            dialogReplaceParts!!.show()
        }
    }

    private fun getCommaSeparatedIds(selectedIds: List<ModelPartsList>): String {
        val partsIds = StringBuilder()
        for (i in selectedIds.indices) {
            if (selectedIds[i].isSelectFlag) partsIds.append(", ").append(selectedIds[i].itemId)
        }
        return if (partsIds.length > 0) {
            partsIds.substring(1)
        } else {
            ""
        }
    }

    private fun getCommaSeparatedName(selectedIds: List<ModelPartsList>): String {
        val partName = StringBuilder()
        for (i in selectedIds.indices) {
            if (selectedIds[i].isSelectFlag) partName.append(", ").append(selectedIds[i].itemName)
        }
        return if (partName.length > 0) {
            partName.substring(1)
        } else {
            ""
        }
    }

    private val sE_AvlStockPartsList: Unit
        get() {
            isInventoryLoading
            inventoryViewModel!!.getAvailableStockListForSE(prefManager!!.useR_Id, "0").observe(
                this
            ) { modelParts: ModelParts ->
                isInventoryLoading
                if (modelParts.status == "200") {
                    listParts = modelParts.parts
                    Log.d("fdnf", "  $listParts")
                }
            }
        }

    private fun acceptComplaint() {
        if (Constants.isNetworkAvailable(mContext!!)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.AcceptBySE(prefManager!!.useR_Id, model!!.complainRegNo)
            call.enqueue(object : Callback<ModelResponse?> {
                override fun onResponse(
                    call: Call<ModelResponse?>,
                    response: Response<ModelResponse?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            val modelResponse = response.body()
                            if (modelResponse!!.status == "200") {
                                val returnIntent = Intent()
                                setResult(RESULT_OK, returnIntent)
                                finish()
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

                override fun onFailure(call: Call<ModelResponse?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGES_CUSTOM_AAD_COMPLAINT && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                imageStoragePath = image.path
                if (imageStoragePath.contains("file:/")) {
                    imageStoragePath = imageStoragePath.replace("file:/", "")
                }
                imageStoragePath = CompressImage.compress(
                    imageStoragePath,
                    this
                )
                binding!!.inputImage.setText(image.path)

                try {
                    ImageUtilsForRotate.ensurePortrait(imageStoragePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }

        if (requestCode == REQUEST_PICK_IMAGES_DSO_LETTER && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                DSO_LETTER_TYPE = "IMAGE"
                dsoletterStoragePath = image.path
                if (dsoletterStoragePath.contains("file:/")) {
                    dsoletterStoragePath = dsoletterStoragePath.replace("file:/", "")
                }
                dsoletterStoragePath = CompressImage.compress(
                    dsoletterStoragePath,
                    this
                )
                binding!!.inputDSOLetter.setText(image.path)
                try {
                    ImageUtilsForRotate.ensurePortrait(dsoletterStoragePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }

        if (requestCode == REQUEST_CODE_PICK_PDF && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                //     getFileFromUri(getApplicationContext(),uri);
                //    openPdf(uri);
                DSO_LETTER_TYPE = "PDF"
                dsoletterStoragePath = uri.toString()
                //                dsoletterStoragePath = data.getData().getPath().toString();
//                if (dsoletterStoragePath.contains("file:/")) {
//                    dsoletterStoragePath = dsoletterStoragePath.replace("file:/", "");
//                }
                binding!!.inputDSOLetter.setText(uri!!.path)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.inputResolvedDate) {
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateResolved,
                myCalendarResolvedDate[Calendar.YEAR],
                myCalendarResolvedDate[Calendar.MONTH],
                myCalendarResolvedDate[Calendar.DAY_OF_MONTH]
            )
            @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd-MM-yyyy")
            try {
                val date = format.parse(model!!.complainRegDateStr)
                datePickerDialog.datePicker.minDate = Objects.requireNonNull(date).time
            } catch (e: Exception) {
                e.printStackTrace()
            }
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        } else if (id == R.id.inputReplacePart) {
            if (Constants.isNetworkAvailable(mContext!!)) {
                selectMultiCheckboxForReplacePart()
            } else {
                makeToast(resources.getString(R.string.no_internet_connection))
            }
        } else if (id == R.id.inputImage) {
            if (permissionGranted) {
                selectImage()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.inputDSOLetter) {
            //   chooseFile();
            //    checkStoragePermission();
            if (permissionGranted) {
                selectDSOLetter()
            } else {
                makeToast(resources.getString(R.string.please_allow_all_permission))
            }
        } else if (id == R.id.buttonResolved) {
            onClickResolveComplaint()
        } else if (id == R.id.btnChallanUpload) {
            onClickChallanUpload()
        } else if (id == R.id.se_image) {
            startActivity(
                Intent(mContext!!, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    model!!.imagePath
                )
            )
        } else if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.buttonAcceptComplaint) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_accept_this_complaint))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, ids: Int ->
                    dialog.cancel()
                    acceptComplaint()
                }
                .setNegativeButton(
                    resources.getString(R.string.no)
                ) { dialog: DialogInterface, ids: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }

    private fun chooseFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_STORAGE
            )
        } else {
            openFilePicker()
        }
    }


    //    private void openFilePicker() {
    //        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    //        intent.setType("*/*");
    //        String[] mimeTypes = {"image/*", "application/pdf"};
    //        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
    //        resultLauncher.launch(intent);
    //    }
    private fun selectDSOLetter() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.choose_pdf),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext!!)
            title.text = resources.getString(R.string.imagepicker_str_select_challan_image)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f
            val builder = AlertDialog.Builder(mContext!!)
            builder.setCustomTitle(title)
            // builder.setTitle("Add Photo!");
            builder.setItems(
                items
            ) { dialog: DialogInterface, item: Int ->
                if (items[item] == resources.getString(R.string.imagepicker_str_take_photo)) {
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
                        .setSelectedImages(ArrayList())
                        .start(REQUEST_PICK_IMAGES_DSO_LETTER)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_choose_from_gallery)) {
                    ImagePicker.with(mActivity)
                        .setToolbarColor("#212121")
                        .setStatusBarColor("#000000")
                        .setToolbarTextColor("#FFFFFF")
                        .setToolbarIconColor("#FFFFFF")
                        .setProgressBarColor("#4CAF50")
                        .setBackgroundColor("#212121")
                        .setCameraOnly(false)
                        .setMultipleMode(true)
                        .setFolderMode(true)
                        .setShowCamera(false)
                        .setFolderTitle("Albums")
                        .setImageTitle("Galleries")
                        .setDoneTitle("Done")
                        .setMaxSize(1)
                        .setSavePath(Constants.saveImagePath)
                        .setSelectedImages(ArrayList())
                        .start(REQUEST_PICK_IMAGES_DSO_LETTER)
                } else if (items[item] == resources.getString(R.string.choose_pdf)) {
                    checkStoragePermission()
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("kdcfjcl", "" + e)
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_STORAGE
                )
            } else {
                openFilePicker()
            }
        } else {
            openFilePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_STORAGE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFilePicker()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("application/pdf")
        //        intent.setType("*/*");
//        String[] mimeTypes = {"image/*", "application/pdf"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_CODE_PICK_PDF)
    }


    private fun openPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application found to open PDF", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_READ_STORAGE = 100
        private const val REQUEST_CODE_PICK_PDF = 101
    }
}
