package com.callmangement.imagepicker.ui.camera;



import com.callmangement.imagepicker.model.Image;

import java.util.List;

public interface OnImageReadyListener {
    void onImageReady(List<Image> images);
}
