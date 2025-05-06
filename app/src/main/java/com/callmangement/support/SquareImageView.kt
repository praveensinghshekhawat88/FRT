package com.callmangement.support

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * Created by Theophrast
 */
class SquareImageView : AppCompatImageView {
    var hwRatio: Float = 1f
        private set

    constructor(context: Context) : super(context) {
        setAttributes(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        setAttributes(context, attrs)
    }

    private fun setAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return
        val packageName = "http://schemas.android.com/apk/res-auto"
        hwRatio = attrs.getAttributeFloatValue(packageName, key_hwRatio, 1f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = measuredHeight

        val calculatedHeight = calculateHeightByRatio(width)

        if (calculatedHeight != height) {
            setMeasuredDimension(width, calculatedHeight)
        }
    }

    private fun calculateHeightByRatio(side: Int): Int {
        return (hwRatio * side.toFloat()).toInt()
    }

    fun setXyRatio(xyRatio: Float) {
        this.hwRatio = xyRatio
        this.invalidate()
    }

    companion object {
        private const val key_hwRatio = "hwRatio"
    }
}