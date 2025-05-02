package com.callmangement.EHR.imagepicker.ui.camera;



import com.callmangement.EHR.imagepicker.model.Image;

import java.util.List;

public interface OnImageReadyListener {
    void onImageReady(List<Image> images);
}
