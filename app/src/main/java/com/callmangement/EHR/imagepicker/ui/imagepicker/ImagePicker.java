/*
 * Created by Nguyen Hoang Lam
 * Date: ${DATE}
 */

package com.callmangement.EHR.imagepicker.ui.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.fragment.app.Fragment;

import com.callmangement.R;
import com.callmangement.EHR.imagepicker.model.Config;
import com.callmangement.EHR.imagepicker.model.Image;
import com.callmangement.EHR.imagepicker.model.SavePath;
import com.callmangement.EHR.imagepicker.ui.camera.CameraActivty;

import java.util.ArrayList;

/**
 * Created by hoanglam on 8/4/16.
 */
public class ImagePicker {

    protected Config config;
    public static int RC_PICK_IMAGES_CUSTOM = 1;

    public ImagePicker(Builder builder) {
        config = builder.config;
    }

    public static Builder with(Activity activity) {
        return new ActivityBuilder(activity);
    }

    public static Builder with(Fragment fragment) {
        return new FragmentBuilder(fragment);
    }

    static class ActivityBuilder extends Builder {
        private final Activity activity;

        public ActivityBuilder(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        public void start(int resultCode) {
            RC_PICK_IMAGES_CUSTOM = resultCode;
            Intent intent;
            if (!config.isCameraOnly()) {
                intent = new Intent(activity, ImagePickerActivity.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                activity.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM);
            } else {
                intent = new Intent(activity, CameraActivty.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.overridePendingTransition(0, 0);
                activity.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM);
            }
        }
    }

    static class FragmentBuilder extends Builder {
        private final Fragment fragment;

        public FragmentBuilder(Fragment fragment) {
            super(fragment);
            this.fragment = fragment;
        }

        @Override
        public void start(int resultCode) {
            Intent intent;
            if (!config.isCameraOnly()) {
                intent = new Intent(fragment.getActivity(), ImagePickerActivity.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                fragment.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM);
            } else {
                intent = new Intent(fragment.getActivity(), CameraActivty.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                fragment.getActivity().overridePendingTransition(0, 0);
                fragment.startActivityForResult(intent, RC_PICK_IMAGES_CUSTOM);
            }
        }
    }

    public static abstract class Builder extends BaseBuilder {

        public Builder(Activity activity) {
            super(activity);
        }

        public Builder(Fragment fragment) {
            super(fragment.getContext());
        }

        public Builder setToolbarColor(String toolbarColor) {
            config.setToolbarColor(toolbarColor);
            return this;
        }

        public Builder setStatusBarColor(String statusBarColor) {
            config.setStatusBarColor(statusBarColor);
            return this;
        }

        public Builder setToolbarTextColor(String toolbarTextColor) {
            config.setToolbarTextColor(toolbarTextColor);
            return this;
        }

        public Builder setToolbarIconColor(String toolbarIconColor) {
            config.setToolbarIconColor(toolbarIconColor);
            return this;
        }

        public Builder setProgressBarColor(String progressBarColor) {
            config.setProgressBarColor(progressBarColor);
            return this;
        }

        public Builder setBackgroundColor(String backgroundColor) {
            config.setBackgroundColor(backgroundColor);
            return this;
        }

        public Builder setCameraOnly(boolean isCameraOnly) {
            config.setCameraOnly(isCameraOnly);
            return this;
        }

        public Builder setMultipleMode(boolean isMultipleMode) {
            config.setMultipleMode(isMultipleMode);
            return this;
        }

        public Builder setFolderMode(boolean isFolderMode) {
            config.setFolderMode(isFolderMode);
            return this;
        }

        public Builder setShowCamera(boolean isShowCamera) {
            config.setShowCamera(isShowCamera);
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            config.setMaxSize(maxSize);
            return this;
        }

        public Builder setDoneTitle(String doneTitle) {
            config.setDoneTitle(doneTitle);
            return this;
        }

        public Builder setFolderTitle(String folderTitle) {
            config.setFolderTitle(folderTitle);
            return this;
        }

        public Builder setImageTitle(String imageTitle) {
            config.setImageTitle(imageTitle);
            return this;
        }

        public Builder setSavePath(String path) {
            config.setSavePath(new SavePath(path, false));
            return this;
        }

        public Builder setSelectedImages(ArrayList<Image> selectedImages) {
            config.setSelectedImages(selectedImages);
            return this;
        }

        public abstract void start(int resultCode);

    }

    public static abstract class BaseBuilder {

        protected Config config;

        public BaseBuilder(Context context) {
            this.config = new Config();

            Resources resources = context.getResources();
            config.setCameraOnly(false);
            config.setMultipleMode(true);
            config.setFolderMode(true);
            config.setShowCamera(true);
            config.setMaxSize(Config.MAX_SIZE);
            config.setDoneTitle(resources.getString(R.string.imagepicker_action_done));
            config.setFolderTitle(resources.getString(R.string.imagepicker_title_folder));
            config.setImageTitle(resources.getString(R.string.imagepicker_title_image));
            config.setSavePath(SavePath.DEFAULT);
            config.setSelectedImages(new ArrayList<Image>());
        }
    }

}

