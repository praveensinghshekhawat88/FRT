package com.callmangement.imagepicker.ui.imagepicker

import com.callmangement.imagepicker.model.Folder
import com.callmangement.imagepicker.model.Image
import com.callmangement.imagepicker.ui.common.MvpView


/**
 * Created by hoanglam on 8/17/17.
 */
interface ImagePickerView : MvpView {
    fun showLoading(isLoading: Boolean)

    fun showFetchCompleted(images: List<Image>?, folders: List<Folder>?)

    fun showError(throwable: Throwable?)

    fun showEmpty()

    fun showCapturedImage()

    fun finishPickImages(images: List<Image>?)
}