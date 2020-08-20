package com.ellie.billiardsgame.model

/**
 * x, y 좌표를 가지는 데이터 클래스.
 */
data class Point(
    var x: Float = 0f,
    var y: Float = 0f
) {

    //----------------------------------------------------------
    // Public interface.
    //

    /**
     * Point의 x, y값 업데이트.
     */
    fun update(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    /**
     * Point의 x, y값 업데이트.
     */
    fun update(point: Point) {
        x = point.x
        y = point.y
    }

    /**
     * Point와 Point의 "+" 연산.
     */
    operator fun plus(point: Point) = Point(point.x + x, point.y + y)

    /**
     * Point와 scalar의 "*" 연산.
     */
    operator fun times(scalar: Float) = Point(scalar * x, scalar * y)

    /**
     * Point와 Point의 "*" 연산.
     */
    operator fun times(point: Point) = point.x * x + point.y * y
}