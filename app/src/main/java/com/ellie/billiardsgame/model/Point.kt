package com.ellie.billiardsgame.model

data class Point(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun update(point: Point) {
        x = point.x
        y = point.y
    }
}