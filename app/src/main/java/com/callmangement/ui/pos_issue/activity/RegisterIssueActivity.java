package com.callmangement.ui.pos_issue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.callmangement.R;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityRegisterIssueBinding;
import com.callmangement.imagepicker.model.Config;
import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.imagepicker.ImagePicker;
import com.callmangement.support.ImageUtilsForRotate;
import com.callmangement.ui.pos_issue.model.ModelIssuesList;
import com.callmangement.ui.pos_issue.model.ModelMachineTypeList;
import com.callmangement.utils.CompressImage;
import com.callmangement.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterIssueActivity extends CustomActivity implements View.OnClickListener {
    private ActivityRegisterIssueBinding binding;
    private final List<ModelMachineTypeList> modelMachineTypeList = new ArrayList<>();
    private final List<ModelIssuesList> modelIssuesList = new ArrayList<>();
    private final int checkMachineType = 0;
    private final int checkIssues = 0;
    public final int REQUEST_PICK_ISSUE_IMAGES = 1113;
    private String issueImagePath = "";
    private String machineType = "";
    private String yourIssue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterIssueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.register_issue));
        setUpOnClickListener();
        createMachineTypeList();
        createIssuesList();
    }

    private void setUpOnClickListener() {
        binding.spinnerMachineType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkMachineType > 0){
                    machineType = modelMachineTypeList.get(i).getMachineType();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinnerYourIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkIssues > 0){
                    yourIssue = modelIssuesList.get(i).getIssueName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.actionBar.ivBack.setOnClickListener(this);
        binding.uploadImageLay.setOnClickListener(this);

    }

    private void createMachineTypeList(){
        ModelMachineTypeList model1 = new ModelMachineTypeList();
        model1.setId("1");
        model1.setMachineType("Analogics");
        modelMachineTypeList.add(model1);

        ModelMachineTypeList model2 = new ModelMachineTypeList();
        model2.setId("2");
        model2.setMachineType("Visiontek");
        modelMachineTypeList.add(model2);

        ModelMachineTypeList model3 = new ModelMachineTypeList();
        model3.setId("3");
        model3.setMachineType("Mobiocean");
        modelMachineTypeList.add(model3);

        ArrayAdapter<ModelMachineTypeList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, modelMachineTypeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMachineType.setAdapter(dataAdapter);
    }

    private void createIssuesList(){
        ModelIssuesList model1 = new ModelIssuesList();
        model1.setId("1");
        model1.setIssueName("RD Issue");
        modelIssuesList.add(model1);

        ModelIssuesList model2 = new ModelIssuesList();
        model2.setId("2");
        model2.setIssueName("Fingerprint not working");
        modelIssuesList.add(model2);

        ModelIssuesList model3 = new ModelIssuesList();
        model3.setId("3");
        model3.setIssueName("Battery Issue");
        modelIssuesList.add(model3);

        ModelIssuesList model4 = new ModelIssuesList();
        model4.setId("4");
        model4.setIssueName("Software Problem");
        modelIssuesList.add(model4);

        ArrayAdapter<ModelIssuesList> dataAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, modelIssuesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerYourIssue.setAdapter(dataAdapter);
    }

    private void selectIssueImage() {
        try {
            final CharSequence[] items = {getResources().getString(R.string.imagepicker_str_take_photo), getResources().getString(R.string.imagepicker_str_choose_from_gallery), getResources().getString(R.string.imagepicker_str_cancel)};
            TextView title = new TextView(mContext);
            title.setText(getResources().getString(R.string.imagepicker_str_select_issue_image));
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
                            .start(REQUEST_PICK_ISSUE_IMAGES);

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
                            .start(REQUEST_PICK_ISSUE_IMAGES);
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
        if (requestCode == REQUEST_PICK_ISSUE_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            if (images != null && images.size() > 0) {
                Image image = images.get(0);
                issueImagePath = image.getPath();
                if (issueImagePath.contains("file:/")) {
                    issueImagePath = issueImagePath.replace("file:/", "");
                }
                issueImagePath = CompressImage.compress(issueImagePath, this);
                File imgFile = new  File(issueImagePath);
                binding.textImagePath.setText(imgFile.getName());

                try {
                    ImageUtilsForRotate.ensurePortrait(issueImagePath);
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        } else if (id == R.id.uploadImageLay){
            selectIssueImage();
        }

    }

}