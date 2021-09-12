package com.ellie.billiardsgame.model

import kotlin.math.hypot

data class Point(
    var x: Float = 0f,
    var y: Float = 0f
) {

    fun update(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun update(point: Point) {
        x = point.x
        y = point.y
    }

    fun size() = hypot(x, y)

    operator fun plus(point: Point) = Point(point.x + x, point.y + y)

    operator fun minus(point: Point) = Point(point.x - x, point.y - y)

    operator fun times(scalar: Float) = Point(scalar * x, scalar * y)

    operator fun times(point: Point) = point.x * x + point.y * y
}