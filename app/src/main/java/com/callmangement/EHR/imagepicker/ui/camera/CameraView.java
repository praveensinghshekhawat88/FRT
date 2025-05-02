package com.callmangement.EHR.imagepicker.ui.camera;



import com.callmangement.EHR.imagepicker.model.Image;
import com.callmangement.EHR.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/22/17.
 */

public interface CameraView extends MvpView {

    void finishPickImages(List<Image> images);
}
