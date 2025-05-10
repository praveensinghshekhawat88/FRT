package com.callmangement.ui.inventory

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.adapter.DispatchChallanPartsListDetailsActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDispatchChallanPartsListDetailsBinding
import com.callmangement.model.inventrory.ModelDispatchInvoice
import com.callmangement.model.inventrory.ModelDisputePartsList
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList
import com.callmangement.report_pdf.DispatchChallanPDFActivity
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.utils.Constants
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager

class DispatchChallanPartsListDetailsActivity : CustomActivity(), View.OnClickListener {
    var binding: ActivityDispatchChallanPartsListDetailsBinding? = null
    private var model: ModelPartsDispatchInvoiceList? = null
    private var list: List<ModelPartsDispatchInvoiceList?> = ArrayList()
    private val modelDisputePartsList: List<ModelDisputePartsList> = ArrayList()
    private var inventoryViewModel: InventoryViewModel? = null
    private var prefManager: PrefManager? = null
    private val invoiceId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDispatchChallanPartsListDetailsBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.create_new_challan)
        prefManager = PrefManager(mContext!!)
        inventoryViewModel = ViewModelProviders.of(this).get(
            InventoryViewModel::class.java
        )

        model = intent.getSerializableExtra("param") as ModelPartsDispatchInvoiceList?

        if (model != null) {
            binding!!.textDistrictName.text = model!!.districtNameEng
            binding!!.textUsername.text = model!!.reciverName

            Glide.with(mContext!!)
                .load(Constants.API_BASE_URL + model!!.dispChalImage)
                .placeholder(R.drawable.image_not_fount)
                .into(binding!!.ivChallanImage)

            Glide.with(mContext!!)
                .load(Constants.API_BASE_URL + model!!.partsImage_1)
                .placeholder(R.drawable.image_not_fount)
                .into(binding!!.ivPartsImage1)

            Glide.with(mContext!!)
                .load(Constants.API_BASE_URL + model!!.partsImage_2)
                .placeholder(R.drawable.image_not_fount)
                .into(binding!!.ivPartsImage2)

            binding!!.inputCourierName.setText(model!!.courierName)
            binding!!.inputCourierTrackingNo.setText(model!!.courierTrackingNo)

            //            binding.inputRemark.setText(model.getDispatcherRemarks());

            /*if (model.getIsReceived().equalsIgnoreCase("true")){
                binding.seRemarkLayout.setVisibility(View.VISIBLE);
                binding.inputSERemark.setText(model.getReceiverRemark());
            } else {
                binding.seRemarkLayout.setVisibility(View.GONE);
            }*/
            if (model!!.isReceived.equals("true", ignoreCase = true)) {
                binding!!.sePartsImageLay.visibility = View.VISIBLE
                Glide.with(mContext!!)
                    .load(Constants.API_BASE_URL + model!!.receivedPartsImage)
                    .placeholder(R.drawable.image_not_fount)
                    .into(binding!!.ivSEPartsImage)
            } else {
                binding!!.sePartsImageLay.visibility = View.GONE
            }

            if (model!!.isSubmitted.equals("true", ignoreCase = true)) {
//                binding.buttonDispatch.setVisibility(View.GONE);
                binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
            } else if (model!!.isSubmitted.equals("false", ignoreCase = true)) {
//                binding.buttonDispatch.setVisibility(View.VISIBLE);
                binding!!.actionBar.buttonPDF.visibility = View.GONE
            }
        }

        onClickListener()
        dispatchInvoicePartsList()
        //        getDisputePartsList();
    }

    private fun onClickListener() {
//        binding.buttonDispatch.setOnClickListener(this);
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.actionBar.buttonPDF.setOnClickListener(this)
        binding!!.ivChallanImage.setOnClickListener(this)
        binding!!.ivPartsImage1.setOnClickListener(this)
        binding!!.ivPartsImage2.setOnClickListener(this)
        binding!!.ivSEPartsImage.setOnClickListener(this)
        //        binding.buttonSendAgainToSE.setOnClickListener(this);
//        binding.buttonSendToStock.setOnClickListener(this);
    }

    private fun dispatchInvoicePartsList() {
        if (isNetworkAvailable(mContext!!)) {
            isLoading
            inventoryViewModel!!.dispatchInvoicePartsList(model!!.invoiceId, "0", "0").observe(
                this
            ) { modelDispatchInvoice: ModelDispatchInvoice? ->
                isLoading
                if (modelDispatchInvoice!!.status == "200") {
                    list = modelDispatchInvoice.partsDispatchInvoiceList
                    if (list.size > 0) {
                        binding!!.challanRecycler.visibility = View.VISIBLE
                        binding!!.textNoRecordFound.visibility = View.GONE
                        setDispatchInvoicePartsListAdapter()
                    } else {
                        binding!!.challanRecycler.visibility = View.GONE
                        binding!!.textNoRecordFound.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setDispatchInvoicePartsListAdapter() {
        binding!!.challanRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.challanRecycler.adapter =
            DispatchChallanPartsListDetailsActivityAdapter(mContext!!, list)
    }

    private val isLoading: Unit
        /*private void getDisputePartsList() {
                modelDisputePartsList.clear();
                if (Constants.isNetworkAvailable(mContext)) {
                    isLoading();
                    inventoryViewModel.getDisputePartsList(prefManager.getUseR_Id(), model.getInvoiceId()).observe(this, modelDisputeParts -> {
                        isLoading();
                        if (modelDisputeParts.getStatus().equals("200")) {
                            modelDisputePartsList = modelDisputeParts.getPartsDispueStockDetails();
                            if (modelDisputePartsList.size() > 0) {
                                binding.layoutDisputeStockNoReceive.setVisibility(View.VISIBLE);
                                setDisputeStockNoReceivePartsListAdapter();
                            } else {
                                binding.layoutDisputeStockNoReceive.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    makeToast(getResources().getString(R.string.no_internet_connection));
                }
            }*/
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

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.buttonPDF) {
            Constants.modelPartsDispatchInvoiceList = list
            startActivity(
                Intent(mContext, DispatchChallanPDFActivity::class.java)
                    .putExtra("invoiceId", model!!.invoiceId)
                    .putExtra("dispatchFrom", prefManager!!.useR_NAME)
                    .putExtra("email", prefManager!!.useR_EMAIL)
                    .putExtra("dispatchTo", model!!.districtNameEng)
                    .putExtra("username", model!!.reciverName)
                    .putExtra("datetime", model!!.dispatchDateStr)
                    .putExtra("courierName", model!!.courierName)
                    .putExtra("courierTrackingNo", model!!.courierTrackingNo)
            )
            finish()
        } else if (id == R.id.ivChallanImage) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    Constants.API_BASE_URL + model!!.dispChalImage
                )
            )
        } else if (id == R.id.ivPartsImage1) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    Constants.API_BASE_URL + model!!.partsImage_1
                )
            )
        } else if (id == R.id.ivPartsImage2) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    Constants.API_BASE_URL + model!!.partsImage_2
                )
            )
        } else if (id == R.id.ivSEPartsImage) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    Constants.API_BASE_URL + model!!.receivedPartsImage
                )
            )
        } /* else if (id == R.id.buttonDispatch){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_dispatch_this_invoice))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                        dlg.dismiss();
                        submitDispatchParts();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> {
                        dlg.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        } else if (id == R.id.buttonSendAgainToSE){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.message_send_againg_to_se))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                        dlg.dismiss();
                        createDisputeDispatchJSONArray();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> {
                        dlg.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        } else if (id == R.id.buttonSendToStock){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getResources().getString(R.string.message_send_to_stock))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dlg, ids) -> {
                        dlg.dismiss();
                        updatePartsStock();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dlg, ids) -> {
                        dlg.dismiss();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getResources().getString(R.string.alert));
            alert.show();
        }*/ else if (id == R.id.iv_back) {
            onBackPressed()
        }
    }
}