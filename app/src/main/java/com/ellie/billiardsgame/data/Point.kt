package com.ellie.billiardsgame.data

data class Point(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun update(point: Point) {
        x = point.x
        y = point.y
    }
}