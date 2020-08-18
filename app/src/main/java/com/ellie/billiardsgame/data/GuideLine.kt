package com.ellie.billiardsgame.data

import com.ellie.billiardsgame.MAX_LINE_LENGTH
import com.ellie.billiardsgame.MAX_POWER
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

class GuideLine {
    var start = Point(0f, 0f)
    var end = Point(0f, 0f)

    val points: FloatArray
        get() = floatArrayOf(start.x, start.y, end.x, end.y)

    val length
        get() = hypot(dx, dy)

    val dx
        get() = end.x - start.x

    val dy
        get() = end.y - start.y

    fun setPoints(startX: Float, startY: Float, endX: Float, endY: Float) {
        start.x = startX
        start.y = startY
        end.x = endX
        end.y = endY
    }

    fun getVelocity(): Point {
        val ratio = length / MAX_LINE_LENGTH
        val slope = if (dx == 0f) 0f else dy / dx

        val velocityX = getSign(dx) * sqrt((MAX_POWER * ratio) / (1 + slope.pow(2)))
        val velocityY = slope * velocityX

        return Point(velocityX, velocityY)
    }

    private fun getSign(dx: Float) = if (dx < 0) (-1) else 1
}