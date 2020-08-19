package com.ellie.billiardsgame.data

import com.ellie.billiardsgame.GlobalApplication

class Boundary(
    private val leftTopPoint: Point = Point(0f, 0f),
    private val rightBottomPoint: Point = Point(0f, 0f)
) {
    private val ballDiameter = GlobalApplication.ballDiameter

    fun adjustX(newX: Float, outside: () -> Unit = {}): Float {
        return when {
            (newX < leftTopPoint.x) -> {
                outside()
                leftTopPoint.x
            }
            (newX > rightBottomPoint.x - ballDiameter) -> {
                outside()
                rightBottomPoint.x - ballDiameter
            }
            else -> newX
        }
    }

    fun adjustY(newY: Float, outside: () -> Unit = {}): Float {
        return when {
            (newY < leftTopPoint.y) -> {
                outside()
                leftTopPoint.y
            }
            (newY > rightBottomPoint.y - ballDiameter) -> {
                outside()
                rightBottomPoint.y - ballDiameter
            }
            else -> newY
        }
    }
}