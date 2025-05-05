package com.callmangement.ehr.imagepicker.listener

import com.callmangement.ehr.imagepicker.model.Image


/**
 * Created by hoanglam on 8/18/17.
 */
interface OnImageSelectionListener {
    fun onSelectionUpdate(images: List<Image?>?)
}
