package com.ellie.billiardsgame.model

import com.ellie.billiardsgame.GlobalApplication

class Boundary(
    private val leftTopPoint: Point = Point(),
    private val rightBottomPoint: Point = Point()
) {
    private val ballDiameter = GlobalApplication.ballDiameter

    fun getAdjustedX(newX: Float, collision: () -> Unit): Float {
        return when {
            (newX < leftTopPoint.x) -> {
                collision()
                leftTopPoint.x
            }
            (newX > rightBottomPoint.x - ballDiameter) -> {
                collision()
                rightBottomPoint.x - ballDiameter
            }
            else -> newX
        }
    }

    fun getAdjustedY(newY: Float, collision: () -> Unit): Float {
        return when {
            (newY < leftTopPoint.y) -> {
                collision()
                leftTopPoint.y
            }
            (newY > rightBottomPoint.y - ballDiameter) -> {
                collision()
                rightBottomPoint.y - ballDiameter
            }
            else -> newY
        }
    }
}