package com.callmangement.ehr.imagepicker.listener

import com.callmangement.ehr.imagepicker.model.Folder
import com.callmangement.ehr.imagepicker.model.Image


/**
 * Created by hoanglam on 8/17/17.
 */
interface OnImageLoaderListener {
    fun onImageLoaded(images: List<Image>?, folders: List<Folder>?)

    fun onFailed(throwable: Throwable?)
}
