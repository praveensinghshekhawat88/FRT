package com.callmangement.imagepicker.ui.camera;



import com.callmangement.imagepicker.model.Image;
import com.callmangement.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/22/17.
 */

public interface CameraView extends MvpView {

    void finishPickImages(List<Image> images);
}
