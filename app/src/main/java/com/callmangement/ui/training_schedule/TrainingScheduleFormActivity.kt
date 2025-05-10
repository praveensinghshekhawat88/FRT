package com.callmangement.ui.training_schedule

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.TrainingScheduleFormActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityTrainingScheduleFormBinding
import com.callmangement.imagepicker.model.Config
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker
import com.callmangement.model.training_schedule.ModelTrainingScheduleFormAddItem
import com.callmangement.support.ImageUtilsForRotate.ensurePortrait
import com.callmangement.utils.CompressImage.Companion.compress
import com.callmangement.utils.Constants
import com.callmangement.utils.EqualSpacingItemDecoration
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class TrainingScheduleFormActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityTrainingScheduleFormBinding? = null
    private var adapter: TrainingScheduleFormActivityAdapter? = null
    private val list: MutableList<ModelTrainingScheduleFormAddItem> = ArrayList()
    private var jsonArray = JSONArray()
    private var imageStoragePath1 = ""
    private var imageStoragePath2 = ""
    private var imageStoragePath3 = ""
    private var imageStoragePath4 = ""
    private var imageStoragePath5 = ""
    private var imageStoragePathPhysicalForm = ""
    val REQUEST_PICK_IMAGES_PHOTO1: Int = 1113
    val REQUEST_PICK_IMAGES_PHOTO2: Int = 1114
    val REQUEST_PICK_IMAGES_PHOTO3: Int = 1115
    val REQUEST_PICK_IMAGES_PHOTO4: Int = 1116
    val REQUEST_PICK_IMAGES_PHOTO5: Int = 1117
    val REQUEST_PICK_IMAGES_PHYSICAL_FORM: Int = 1118
    var isImageSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_schedule_form)
        initView()
    }

    private fun initView() {
        onClickListener()
        setUpData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpData() {
        list.clear()
        list.add(ModelTrainingScheduleFormAddItem("", "", ""))

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.training_schedule)

        adapter = TrainingScheduleFormActivityAdapter(mContext!!, list)
        adapter!!.notifyDataSetChanged()
        binding!!.rvLayoutPhysicalForm.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.rvLayoutPhysicalForm.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvLayoutPhysicalForm.adapter = adapter
    }

    private fun onClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.buttonAddLayout.setOnClickListener(this)
        binding!!.buttonSubmit.setOnClickListener(this)
        binding!!.photo1.setOnClickListener(this)
        binding!!.photo2.setOnClickListener(this)
        binding!!.photo3.setOnClickListener(this)
        binding!!.photo4.setOnClickListener(this)
        binding!!.photo5.setOnClickListener(this)
        binding!!.inputImagePhysicalForm.setOnClickListener(this)
    }

    private fun submitForm() {
        jsonArray = JSONArray()
        if (list.size > 0) {
            for (i in list.indices) {
                try {
                    val jsonObject = JSONObject()
                    jsonObject.put("name", list[i].name)
                    jsonObject.put("fps_code", list[i].fpsCode)
                    jsonObject.put("phone", list[i].phone)
                    jsonArray.put(jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            if (jsonArray.length() > 0) {
                //   Log.e("array", String.valueOf(jsonArray));
                saveData(jsonArray)
            }
        }
    }

    private fun saveData(jsonArray: JSONArray) {
    }

    private fun addItem() {
        val lastIndex = list.size - 1
        if (list[lastIndex].name != "" && list[lastIndex].fpsCode != "" && list[lastIndex].phone != "") {
            list.add(ModelTrainingScheduleFormAddItem("", "", ""))
            if (adapter != null) adapter!!.notifyItemInserted(list.size - 1)
        } else Toast.makeText(mContext, "Please fill item first data.", Toast.LENGTH_SHORT).show()
    }

    private fun selectImage1() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_photo1)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO1)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO1)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectImage2() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_photo2)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO2)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO2)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectImage3() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_photo3)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO3)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO3)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectImage4() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_photo4)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO4)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO4)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectImage5() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_photo5)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO5)
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
                        .start(REQUEST_PICK_IMAGES_PHOTO5)
                } else if (items[item] == resources.getString(R.string.imagepicker_str_cancel)) {
                    dialog.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectImagePhysicalForm() {
        try {
            val items = arrayOf<CharSequence>(
                resources.getString(R.string.imagepicker_str_take_photo),
                resources.getString(R.string.imagepicker_str_choose_from_gallery),
                resources.getString(R.string.imagepicker_str_cancel)
            )
            val title = TextView(mContext)
            title.text = resources.getString(R.string.imagepicker_physical_form)
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
                        .start(REQUEST_PICK_IMAGES_PHYSICAL_FORM)
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
                        .start(REQUEST_PICK_IMAGES_PHYSICAL_FORM)
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
        if (requestCode == REQUEST_PICK_IMAGES_PHOTO1 && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                imageStoragePath1 = image.path
                if (imageStoragePath1.contains("file:/")) {
                    imageStoragePath1 = imageStoragePath1.replace("file:/", "")
                }
                imageStoragePath1 = compress(
                    imageStoragePath1,
                    this
                )
                val bitmap = BitmapFactory.decodeFile(imageStoragePath1)

                try {
                    binding!!.photo1.setImageBitmap(ensurePortrait(imageStoragePath1))
                } catch (e: IOException) {
                    binding!!.photo1.setImageBitmap(bitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGES_PHOTO2 && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                imageStoragePath2 = image.path
                if (imageStoragePath2.contains("file:/")) {
                    imageStoragePath2 = imageStoragePath2.replace("file:/", "")
                }
                imageStoragePath2 = compress(
                    imageStoragePath2,
                    this
                )
                val bitmap = BitmapFactory.decodeFile(imageStoragePath2)
                try {
                    binding!!.photo2.setImageBitmap(ensurePortrait(imageStoragePath2))
                } catch (e: IOException) {
                    binding!!.photo2.setImageBitmap(bitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGES_PHOTO3 && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                imageStoragePath3 = image.path
                if (imageStoragePath3.contains("file:/")) {
                    imageStoragePath3 = imageStoragePath3.replace("file:/", "")
                }
                imageStoragePath3 = compress(
                    imageStoragePath3,
                    this
                )
                val bitmap = BitmapFactory.decodeFile(imageStoragePath3)

                try {
                    binding!!.photo3.setImageBitmap(ensurePortrait(imageStoragePath3))
                } catch (e: IOException) {
                    binding!!.photo3.setImageBitmap(bitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGES_PHOTO4 && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                imageStoragePath4 = image.path
                if (imageStoragePath4.contains("file:/")) {
                    imageStoragePath4 = imageStoragePath4.replace("file:/", "")
                }
                imageStoragePath4 = compress(
                    imageStoragePath4,
                    this
                )
                val bitmap = BitmapFactory.decodeFile(imageStoragePath4)

                try {
                    binding!!.photo4.setImageBitmap(ensurePortrait(imageStoragePath4))
                } catch (e: IOException) {
                    binding!!.photo4.setImageBitmap(bitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGES_PHOTO5 && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                imageStoragePath5 = image.path
                if (imageStoragePath5.contains("file:/")) {
                    imageStoragePath5 = imageStoragePath5.replace("file:/", "")
                }
                imageStoragePath5 = compress(
                    imageStoragePath5,
                    this
                )
                val bitmap = BitmapFactory.decodeFile(imageStoragePath5)

                try {
                    binding!!.photo5.setImageBitmap(ensurePortrait(imageStoragePath5))
                } catch (e: IOException) {
                    binding!!.photo5.setImageBitmap(bitmap)
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == REQUEST_PICK_IMAGES_PHYSICAL_FORM && resultCode == RESULT_OK && data != null) {
            val images = data.getParcelableArrayListExtra<Image>(Config.EXTRA_IMAGES)
            if (images != null && images.size > 0) {
                val image = images[0]
                imageStoragePathPhysicalForm = image.path
                if (imageStoragePathPhysicalForm.contains("file:/")) {
                    imageStoragePathPhysicalForm =
                        imageStoragePathPhysicalForm.replace("file:/", "")
                }
                imageStoragePathPhysicalForm = compress(
                    imageStoragePathPhysicalForm,
                    this
                )
                binding!!.inputImagePhysicalForm.setText(image.path)
                try {
                    ensurePortrait(imageStoragePathPhysicalForm)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                //isImageSelected = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> onBackPressed()
            R.id.buttonAddLayout -> addItem()
            R.id.buttonSubmit -> {
                val lastIndex = list.size - 1
                if (list[lastIndex].name != "" && list[lastIndex].fpsCode != "" && list[lastIndex].phone != "") {
                    submitForm()
                } else Toast.makeText(mContext, "Please fill item first data.", Toast.LENGTH_SHORT)
                    .show()
            }

            R.id.photo1 -> selectImage1()
            R.id.photo2 -> selectImage2()
            R.id.photo3 -> selectImage3()
            R.id.photo4 -> selectImage4()
            R.id.photo5 -> selectImage5()
            R.id.inputImagePhysicalForm -> selectImagePhysicalForm()
            else -> {}
        }
    }
}