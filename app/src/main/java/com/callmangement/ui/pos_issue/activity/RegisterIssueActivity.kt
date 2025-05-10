package com.callmangement.ui.pos_issue.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityRegisterIssueBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.ui.pos_issue.model.ModelIssuesList
import com.callmangement.ui.pos_issue.model.ModelMachineTypeList
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import java.io.File
import java.io.IOException

class RegisterIssueActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityRegisterIssueBinding? = null
    private val modelMachineTypeList: MutableList<ModelMachineTypeList> = ArrayList()
    private val modelIssuesList: MutableList<ModelIssuesList> = ArrayList()
    private val checkMachineType = 0
    private val checkIssues = 0
    val REQUEST_PICK_ISSUE_IMAGES: Int = 1113
    private var issueImagePath = ""
    private var machineType: String? = ""
    private var yourIssue: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterIssueBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.register_issue)
        setUpOnClickListener()
        createMachineTypeList()
        createIssuesList()
    }

    private fun setUpOnClickListener() {
        binding!!.spinnerMachineType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (checkMachineType > 0) {
                        machineType = modelMachineTypeList[i].machineType
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.spinnerYourIssue.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (checkIssues > 0) {
                        yourIssue = modelIssuesList[i].issueName
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.uploadImageLay.setOnClickListener(this)
    }

    private fun createMachineTypeList() {
        val model1 = ModelMachineTypeList()
        model1.id = "1"
        model1.machineType = "Analogics"
        modelMachineTypeList.add(model1)

        val model2 = ModelMachineTypeList()
        model2.id = "2"
        model2.machineType = "Visiontek"
        modelMachineTypeList.add(model2)

        val model3 = ModelMachineTypeList()
        model3.id = "3"
        model3.machineType = "Mobiocean"
        modelMachineTypeList.add(model3)

        val dataAdapter = ArrayAdapter(
            mContext!!, android.R.layout.simple_spinner_item, modelMachineTypeList
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerMachineType.adapter = dataAdapter
    }

    private fun createIssuesList() {
        val model1 = ModelIssuesList()
        model1.id = "1"
        model1.issueName = "RD Issue"
        modelIssuesList.add(model1)

        val model2 = ModelIssuesList()
        model2.id = "2"
        model2.issueName = "Fingerprint not working"
        modelIssuesList.add(model2)

        val model3 = ModelIssuesList()
        model3.id = "3"
        model3.issueName = "Battery Issue"
        modelIssuesList.add(model3)

        val model4 = ModelIssuesList()
        model4.id = "4"
        model4.issueName = "Software Problem"
        modelIssuesList.add(model4)

        val dataAdapter =
            ArrayAdapter(mContext!!, android.R.layout.simple_spinner_item, modelIssuesList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerYourIssue.adapter = dataAdapter
    }

    private fun selectIssueImage() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_str_select_issue_image)
            title.setBackgroundColor(resources.getColor(R.color.colorActionBar))
            title.setPadding(15, 25, 15, 25)
            title.gravity = Gravity.CENTER
            title.setTextColor(Color.WHITE)
            title.textSize = 22f
            val builder = AlertDialog.Builder(
                mContext!!
            )
            builder.setCustomTitle(title)
            builder.setItems(
                items
            ) { dialog: DialogInterface, item: Int ->
                if (items[item] == resources.getString(R.string.imagepicker_str_take_photo)) {
                    ImagePicker.with(mContext)
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
                        .start(REQUEST_PICK_ISSUE_IMAGES)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_choose_from_gallery)) {
                    ImagePicker.with(mContext)
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
                        .start(REQUEST_PICK_ISSUE_IMAGES)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_ISSUE_IMAGES && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                issueImagePath = image.path
                if (issueImagePath.contains("file:/")) {
                    issueImagePath = issueImagePath.replace("file:/", "")
                }
                issueImagePath = compress(
                    issueImagePath,
                    this
                )
                val imgFile = File(issueImagePath)
                binding!!.textImagePath.text = imgFile.name

                try {
                    ensurePortrait(issueImagePath)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.uploadImageLay) {
            selectIssueImage()
        }
    }
}