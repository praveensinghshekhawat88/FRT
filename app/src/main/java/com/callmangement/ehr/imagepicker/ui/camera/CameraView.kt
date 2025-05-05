package com.callmangement.ehr.imagepicker.ui.camera

import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.ui.common.MvpView


/**
 * Created by hoanglam on 8/22/17.
 */
interface CameraView : MvpView {
    fun finishPickImages(images: List<Image?>?)
}
