package com.callmangement.ehr.imagepicker.ui.camera

import com.callmangement.ehr.imagepicker.model.Image


interface OnImageReadyListener {
    fun onImageReady(images: List<Image?>?)
}
