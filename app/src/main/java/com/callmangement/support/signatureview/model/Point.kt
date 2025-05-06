package com.callmangement.support.signatureview.model

import kotlin.math.pow
import kotlin.math.sqrt

class Point(@JvmField val x: Float, @JvmField val y: Float, @JvmField val time: Long) {
    private fun distanceTo(start: Point): Float {
        return (sqrt((x - start.x).pow(2) + (y - start.y).pow(2))) as Float
    }

    fun velocityFrom(start: Point): Float {
        return distanceTo(start) / (this.time - start.time)
    }
}