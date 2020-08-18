package com.ellie.billiardsgame.data

import kotlin.math.hypot

class Line {
    var start = Point(0f, 0f)
    var end = Point(0f, 0f)

    val points: FloatArray
        get() = floatArrayOf(start.x, start.y, end.x, end.y)

    val dx = end.x - start.x

    val dy = end.y - start.y

    val length
        get() = hypot(dx, dy)

    fun setPoints(startX: Float, startY: Float, endX: Float, endY: Float) {
        start.x = startX
        start.y = startY
        end.x = endX
        end.y = endY
    }
}