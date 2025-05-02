package com.callmangement.ui.training_schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.callmangement.R;
import com.callmangement.adapter.TrainingScheduleFormActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityTrainingScheduleFormBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.model.training_schedule.ModelTrainingScheduleFormAddItem;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;
import com.callmangement.utils.EqualSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrainingScheduleFormActivity extends CustomActivity implements View.OnClickListener {
    private ActivityTrainingScheduleFormBinding binding;
    private TrainingScheduleFormActivityAdapter adapter;
    private final List<ModelTrainingScheduleFormAddItem> list = new ArrayList<>();
    private JSONArray jsonArray = new JSONArray();
    private String imageStoragePath1 = "";
    private String imageStoragePath2 = "";
    private String imageStoragePath3 = "";
    private String imageStoragePath4 = "";
    private String imageStoragePath5 = "";
    private String imageStoragePathPhysicalForm = "";
    public final int REQUEST_PICK_IMAGES_PHOTO1 = 1113;
    public final int REQUEST_PICK_IMAGES_PHOTO2 = 1114;
    public final int REQUEST_PICK_IMAGES_PHOTO3 = 1115;
    public final int REQUEST_PICK_IMAGES_PHOTO4 = 1116;
    public final int REQUEST_PICK_IMAGES_PHOTO5 = 1117;
    public final int REQUEST_PICK_IMAGES_PHYSICAL_FORM = 1118;
    Boolean isImageSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_schedule_form);
        initView();
    }

    private void initView() {
        onClickListener();
        setUpData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpData() {
        list.clear();
        list.add(new ModelTrainingScheduleFormAddItem("", "",""));

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.training_schedule));

        adapter = new TrainingScheduleFormActivityAdapter(mContext, list);
        adapter.notifyDataSetChanged();
        binding.rvLayoutPhysicalForm.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvLayoutPhysicalForm.addItemDecoration(new EqualSpacingItemDecoration(30, EqualSpacingItemDecoration.VERTICAL));
        binding.rvLayoutPhysicalForm.setAdapter(adapter);

    }

    private void onClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonAddLayout.setOnClickListener(this);
        binding.buttonSubmit.setOnClickListener(this);
        binding.photo1.setOnClickListener(this);
        binding.photo2.setOnClickListener(this);
        binding.photo3.setOnClickListener(this);
        binding.photo4.setOnClickListener(this);
        binding.photo5.setOnClickListener(this);
        binding.inputImagePhysicalForm.setOnClickListener(this);
    }

    private void submitForm(){
        jsonArray = new JSONArray();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", list.get(i).name);
                    jsonObject.put("fps_code", list.get(i).fpsCode);
                    jsonObject.put("phone", list.get(i).phone);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (jsonArray.length() > 0) {
             //   Log.e("array", String.valueOf(jsonArray));
                saveData(jsonArray);
            }
        }
    }

    private void saveData(JSONArray jsonArray){

    }

    private void addItem(){
        int lastIndex = list.size() - 1;
        if (!list.get(lastIndex).name.equals("") && !list.get(lastIndex).fpsCode.equals("") && !list.get(lastIndex).phone.equals("")) {
            list.add(new ModelTrainingScheduleFormAddItem("", "",""));
            if (adapter != null)
                adapter.notifyItemInserted(list.size() - 1);
        }else Toast.makeText(mContext, "Please fill item first data.", Toast.LENGTH_SHORT).show();
    }

    private void selectImage1() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_photo1));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO1);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO1);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage2() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_photo2));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO2);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO2);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage3() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_photo3));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO3);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO3);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage4() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_photo4));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO4);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO4);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage5() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_photo5));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO5);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHOTO5);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImagePhysicalForm() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_physical_form));
            title.setBackgroundColor(getResources().getColor(R.color.colorActionBar));
            title.setPadding(15, 25, 15, 25);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(22);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCustomTitle(title);
            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getResources().getString(R.string.imagepicker_str_take_photo))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHYSICAL_FORM);

                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_choose_from_gallery))) {
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
                            .setSelectedImages(new ArrayList<>())
                            .start(REQUEST_PICK_IMAGES_PHYSICAL_FORM);
                } else if (items[item].equals(getResources().getString(R.string.imagepicker_str_cancel))) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGES_PHOTO1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                imageStoragePath1 = image.getPath();
                if (imageStoragePath1.contains("file:/")) {
                    imageStoragePath1 = imageStoragePath1.replace("file:/", "");
                }
                imageStoragePath1 = CompressImage.compress(imageStoragePath1, this);
                Bitmap bitmap = BitmapFactory.decodeFile(imageStoragePath1);

                try {
                    binding.photo1.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath1));
                } catch (IOException e) {
                    binding.photo1.setImageBitmap(bitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }else if (requestCode == REQUEST_PICK_IMAGES_PHOTO2 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                imageStoragePath2 = image.getPath();
                if (imageStoragePath2.contains("file:/")) {
                    imageStoragePath2 = imageStoragePath2.replace("file:/", "");
                }
                imageStoragePath2 = CompressImage.compress(imageStoragePath2, this);
                Bitmap bitmap = BitmapFactory.decodeFile(imageStoragePath2);
                try {
                    binding.photo2.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath2));
                } catch (IOException e) {
                    binding.photo2.setImageBitmap(bitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }else if (requestCode == REQUEST_PICK_IMAGES_PHOTO3 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                imageStoragePath3 = image.getPath();
                if (imageStoragePath3.contains("file:/")) {
                    imageStoragePath3 = imageStoragePath3.replace("file:/", "");
                }
                imageStoragePath3 = CompressImage.compress(imageStoragePath3, this);
                Bitmap bitmap = BitmapFactory.decodeFile(imageStoragePath3);

                try {
                    binding.photo3.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath3));
                } catch (IOException e) {
                    binding.photo3.setImageBitmap(bitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }else if (requestCode == REQUEST_PICK_IMAGES_PHOTO4 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                imageStoragePath4 = image.getPath();
                if (imageStoragePath4.contains("file:/")) {
                    imageStoragePath4 = imageStoragePath4.replace("file:/", "");
                }
                imageStoragePath4 = CompressImage.compress(imageStoragePath4, this);
                Bitmap bitmap = BitmapFactory.decodeFile(imageStoragePath4);

                try {
                    binding.photo4.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath4));
                } catch (IOException e) {
                    binding.photo4.setImageBitmap(bitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }else if (requestCode == REQUEST_PICK_IMAGES_PHOTO5 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                imageStoragePath5 = image.getPath();
                if (imageStoragePath5.contains("file:/")) {
                    imageStoragePath5 = imageStoragePath5.replace("file:/", "");
                }
                imageStoragePath5 = CompressImage.compress(imageStoragePath5, this);
                Bitmap bitmap = BitmapFactory.decodeFile(imageStoragePath5);

                try {
                    binding.photo5.setImageBitmap(ImageUtilsForRotate.ensurePortrait(imageStoragePath5));
                } catch (IOException e) {
                    binding.photo5.setImageBitmap(bitmap);
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }else if (requestCode == REQUEST_PICK_IMAGES_PHYSICAL_FORM && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                imageStoragePathPhysicalForm = image.getPath();
                if (imageStoragePathPhysicalForm.contains("file:/")) {
                    imageStoragePathPhysicalForm = imageStoragePathPhysicalForm.replace("file:/", "");
                }
                imageStoragePathPhysicalForm = CompressImage.compress(imageStoragePathPhysicalForm, this);
                binding.inputImagePhysicalForm.setText(image.getPath());
                try {
                    ImageUtilsForRotate.ensurePortrait(imageStoragePathPhysicalForm);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                //isImageSelected = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.buttonAddLayout:
                addItem();
                break;
            case R.id.buttonSubmit:
                int lastIndex = list.size() - 1;
                if (!list.get(lastIndex).name.equals("") && !list.get(lastIndex).fpsCode.equals("") && !list.get(lastIndex).phone.equals("")) {
                    submitForm();
                }else Toast.makeText(mContext, "Please fill item first data.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.photo1:
                selectImage1();
                break;
            case R.id.photo2:
                selectImage2();
                break;
            case R.id.photo3:
                selectImage3();
                break;
            case R.id.photo4:
                selectImage4();
                break;
            case R.id.photo5:
                selectImage5();
                break;
            case R.id.inputImagePhysicalForm:
                selectImagePhysicalForm();
                break;
            default:
                break;
        }
    }
}