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

    operator fun plus(point: Point) = Point(x + point.x, y + point.y)

    operator fun minus(point: Point) = Point(x - point.x, y - point.y)

    operator fun times(scalar: Float) = Point(x * scalar, y * scalar)

    operator fun times(point: Point) = x * point.x + y * point.y
}