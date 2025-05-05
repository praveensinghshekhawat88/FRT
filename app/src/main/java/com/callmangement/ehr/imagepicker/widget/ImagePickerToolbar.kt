package com.callmangement.ehr.imagepicker.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.R

/**
 * Created by hoanglam on 8/11/17.
 */
class ImagePickerToolbar : RelativeLayout {
    private var titleText: TextView? = null
    private var doneText: TextView? = null
    private var backImage: AppCompatImageView? = null
    private var cameraImage: AppCompatImageView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.imagepicker_toolbar, this)
        if (isInEditMode) {
            return
        }

        titleText = findViewById(R.id.text_toolbar_title)
        doneText = findViewById(R.id.text_toolbar_done)
        backImage = findViewById(R.id.image_toolbar_back)
        cameraImage = findViewById(R.id.image_toolbar_camera)
    }

    fun config(config: Config) {
        setBackgroundColor(config.getToolbarColor())

        titleText!!.text = if (config.isFolderMode) config.folderTitle else config.imageTitle
        titleText!!.setTextColor(config.getToolbarTextColor())

        doneText!!.text = config.doneTitle
        doneText!!.setTextColor(config.getToolbarTextColor())

        backImage!!.setColorFilter(config.getToolbarIconColor())

        cameraImage!!.setColorFilter(config.getToolbarIconColor())
        cameraImage!!.visibility =
            if (config.isShowCamera) VISIBLE else GONE

        doneText!!.visibility = GONE
    }

    fun setTitle(title: String?) {
        titleText!!.text = title
    }

    fun showDoneButton(isShow: Boolean) {
        doneText!!.visibility = if (isShow) VISIBLE else GONE
    }

    fun setOnBackClickListener(clickListener: OnClickListener?) {
        backImage!!.setOnClickListener(clickListener)
    }

    fun setOnCameraClickListener(clickListener: OnClickListener?) {
        cameraImage!!.setOnClickListener(clickListener)
    }

    fun setOnDoneClickListener(clickListener: OnClickListener?) {
        doneText!!.setOnClickListener(clickListener)
    }
}
