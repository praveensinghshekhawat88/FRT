package com.callmangement.ehr.imagepicker.model

/**
 * Created by boss1088 on 8/22/16.
 */
class Folder(@JvmField var folderName: String) {
    @JvmField
    var images: ArrayList<Image> = ArrayList()
}
