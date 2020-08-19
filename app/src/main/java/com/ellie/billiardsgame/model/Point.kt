package com.ellie.billiardsgame.model

data class Point(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun update(point: Point) {
        x = point.x
        y = point.y
    }

    operator fun plus(point: Point) = Point(point.x + x, point.y + y)

    operator fun times(scalar: Float) = Point(scalar * x, scalar * y)

    operator fun times(point: Point) = point.x * x + point.y * y
}