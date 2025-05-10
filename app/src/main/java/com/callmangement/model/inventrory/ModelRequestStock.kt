package com.callmangement.model.inventrory

import android.widget.RelativeLayout

class ModelRequestStock(
    @JvmField var id: String,
    @JvmField var itemName: String,
    @JvmField var qty: String,
    var spinnerItemList: List<ModelPartsList>
) {
    var isItemSelectFlag: Boolean = false
    @JvmField
    var spinnerSelectedIndex: Int = 0
    @JvmField
    var relativeLayout: RelativeLayout? = null
    var isFlag: Boolean = false
}
